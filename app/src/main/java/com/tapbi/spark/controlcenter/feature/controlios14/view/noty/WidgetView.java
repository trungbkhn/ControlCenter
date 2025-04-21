package com.tapbi.spark.controlcenter.feature.controlios14.view.noty;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.adapter.SearchAppAdapter;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.common.models.MessageEvent;
import com.tapbi.spark.controlcenter.feature.controlios14.helper.BlurBackground;
import com.tapbi.spark.controlcenter.feature.controlios14.manager.PermissionManager;
import com.tapbi.spark.controlcenter.feature.controlios14.manager.SuggestAppManager;
import com.tapbi.spark.controlcenter.feature.controlios14.model.AppInstallModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.noty.widget.EventCalendarView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.noty.widget.SearchView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.noty.widget.SuggestView;
import com.tapbi.spark.controlcenter.utils.DensityUtils;
import com.tapbi.spark.controlcenter.utils.helper.rcvhepler.NpaGridLayoutManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import timber.log.Timber;

public class WidgetView extends ConstraintLayout {

    private Context context;

    private EventCalendarView eventCalendarView;

    private SearchView searchView;
    private ImageView background;
    private RecyclerView listAppSearch;
    private SearchAppAdapter searchAppAdapter;
    private ArrayList<AppInstallModel> allApp, appSearch;
    private TextView actionCancelSearch;
    private SuggestView suggestView;
    private SuggestView suggestViewSearch;

    private OnWidgetListener onWidgetListener;

    public WidgetView(Context context) {
        super(context);
        init(context);
    }

    public WidgetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WidgetView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setOnWidgetListener(OnWidgetListener onWidgetListener) {
        this.onWidgetListener = onWidgetListener;
    }

    public void setViewGroup(ViewGroup container) {
        eventCalendarView.setViewGroup((NotyCenterViewOS) container);
        searchView.setViewGroup((NotyCenterViewOS) container);
        suggestView.setViewGroup((NotyCenterViewOS) container);
        suggestViewSearch.setViewGroup((NotyCenterViewOS) container);
        background.setImageBitmap(BlurBackground.getInstance().getBitmapBgBlur());
    }

    public void updateBitmapBlur() {
        searchView.getBitmapBlur();
        suggestView.getBitmapBlur();
        eventCalendarView.getBitmapBlur();
        background.setImageBitmap(BlurBackground.getInstance().getBitmapBgBlur());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(Context ctx) {
        this.context = ctx;
        allApp = new ArrayList<>();
        appSearch = new ArrayList<>();
        if ( DensityUtils.getOrientationWindowManager(getContext()) == Configuration.ORIENTATION_PORTRAIT) {
            LayoutInflater.from(context).inflate(R.layout.view_widget_viewpager, this, true);
        } else {
            LayoutInflater.from(context).inflate(R.layout.view_widget_viewpager_land, this, true);
        }
        eventCalendarView = findViewById(R.id.eventCalendarView);
        actionCancelSearch = findViewById(R.id.actionCancelSearch);
        setSuggestView();
        setSearchView();
        background = findViewById(R.id.imgBackground);
        listAppSearch = findViewById(R.id.listAppSearch);
        listAppSearch.setLayoutManager(new NpaGridLayoutManager(context, 4));
        searchAppAdapter = new SearchAppAdapter(context, appSearch, appInstallModel -> {
            Intent intent = new Intent(Constant.ACTION_OPEN_APP);
            intent.putExtra(Constant.PACKAGE_NAME_APP_OPEN, appInstallModel.getPackageName());
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            searchView.disableSearch();
        });
        listAppSearch.setAdapter(searchAppAdapter);
        updateUI();
        actionCancelSearch.setOnClickListener(v -> searchView.disableSearch());
        listAppSearch.setOnTouchListener((v, event) -> {
            searchView.closeKeyBoard();
            return false;
        });
    }

    private void setSuggestView() {
        suggestView = findViewById(R.id.suggestView);
        suggestView.setOnSuggestListener(() -> searchView.setEnable(true));
        suggestViewSearch = findViewById(R.id.suggestViewSearch);
        suggestViewSearch.setupWhenSearch(searchView);
    }

    private void setSearchView() {
        searchView = findViewById(R.id.searchView);
        LayoutParams paramsSearch = (LayoutParams) searchView.getLayoutParams();
        paramsSearch.topMargin = App.statusBarHeight + 10;
        searchView.setOnSearchViewListener(new SearchView.OnSearchViewListener() {
            @Override
            public void onOpenSearch() {

                if (onWidgetListener != null) {
                    onWidgetListener.onOpenSearch();
                }

                searchView.setEnable(false);
                background.setVisibility(VISIBLE);
                background.setAlpha(0f);
                background.clearAnimation();
                background.animate().alpha(1f).setDuration(300).withEndAction(() -> {
                    suggestViewSearch.setVisibility(VISIBLE);
                    suggestView.setVisibility(GONE);
                    eventCalendarView.setVisibility(GONE);
                    suggestViewSearch.updateClipPath();
                }).start();
            }

            @Override
            public void onCloseSearch() {
                suggestViewSearch.setVisibility(GONE);
                background.clearAnimation();
                background.animate().alpha(0f).setDuration(300).withEndAction(() -> {
                    background.setVisibility(INVISIBLE);
                    actionCancelSearch.setVisibility(GONE);
                    suggestView.setVisibility(VISIBLE);
                    eventCalendarView.setVisibility(VISIBLE);
                    searchView.setEnable(true);

                    if (onWidgetListener != null) {
                        onWidgetListener.onCloseSearch();
                    }
                }).start();
            }

            @Override
            public void onTextChange(String s) {
                if (allApp.isEmpty()) {
                    allApp = SuggestAppManager.getInstance().getAllApps();
                }
                appSearch.clear();
                if (s.isEmpty()) {
                    suggestViewSearch.setVisibility(VISIBLE);
                    listAppSearch.setVisibility(GONE);
                } else {
                    suggestViewSearch.setVisibility(GONE);
                    listAppSearch.setVisibility(VISIBLE);
                    for (AppInstallModel app : allApp) {
                        if (app.getName().toLowerCase().contains(s.toLowerCase())) {
                            appSearch.add(app);
                        }
                    }
                    if (appSearch.size() == 0) {
                        suggestViewSearch.setVisibility(VISIBLE);
                        listAppSearch.setVisibility(GONE);
                    }
                }

                searchAppAdapter.notifyDataSetChanged();
            }

            @Override
            public void onProgressAnimaion(float f) {
                actionCancelSearch.setVisibility(VISIBLE);
                actionCancelSearch.setTranslationX(f);
            }
        });
    }

    public void translation(int positionOffsetPixels) {
        suggestView.setTranslationX(positionOffsetPixels);
        searchView.setTranslationX(positionOffsetPixels);
        eventCalendarView.setTranslationX(positionOffsetPixels);
    }

    public void updateAppRecent() {
        loadAppRecent();
    }

    public void updateEvent() {
        eventCalendarView.updateEvent();
    }

    private void updateUI() {
        background.setVisibility(INVISIBLE);
        suggestViewSearch.setVisibility(GONE);
        listAppSearch.setVisibility(GONE);
        searchView.screenOff();
    }

    private void loadAppRecent() {
        if (SuggestAppManager.getInstance().isRefresh()) {
            suggestView.updateAppSuggest();
            suggestViewSearch.updateAppSuggest();
            SuggestAppManager.getInstance().setRefresh(false);
        } else {
            suggestView.setTextPermissionAppRecent(PermissionManager.getInstance().checkPermissionAppRecent(context));
            SuggestAppManager.getInstance().loadSuggestApp(context, () -> {
                Timber.e(".");
                suggestView.post(() -> suggestView.updateAppSuggest());
                suggestViewSearch.post(new Runnable() {
                    @Override
                    public void run() {
                        suggestViewSearch.updateAppSuggest();
                    }
                });
                SuggestAppManager.getInstance().setRefresh(false);
            });
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent messageEvent) {
        if (messageEvent.getTypeEvent() == Constant.PACKAGE_APP_REMOVE) {
            suggestViewSearch.updateAppSuggest();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    public interface OnWidgetListener {
        void onOpenSearch();

        void onCloseSearch();
    }
}


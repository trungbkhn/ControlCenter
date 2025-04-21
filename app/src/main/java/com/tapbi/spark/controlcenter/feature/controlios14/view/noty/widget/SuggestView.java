package com.tapbi.spark.controlcenter.feature.controlios14.view.noty.widget;

import static com.tapbi.spark.controlcenter.common.Constant.LIGHT;
import static com.tapbi.spark.controlcenter.common.Constant.STYLE_SELECTED;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.adapter.RecentAppAdapter;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.common.models.MessageEvent;
import com.tapbi.spark.controlcenter.eventbus.EventAppRecent;
import com.tapbi.spark.controlcenter.feature.controlios14.manager.PermissionManager;
import com.tapbi.spark.controlcenter.feature.controlios14.manager.SuggestAppManager;
import com.tapbi.spark.controlcenter.feature.controlios14.model.AppInstallModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.noty.ImageBackgroundView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.noty.MaskView;
import com.tapbi.spark.controlcenter.utils.AppUtils;
import com.tapbi.spark.controlcenter.utils.MethodUtils;
import com.tapbi.spark.controlcenter.utils.helper.rcvhepler.NpaGridLayoutManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import timber.log.Timber;

public class SuggestView extends MaskView {

    private Context context;

    private TextView actionShowMore;
    private ImageView icon;
    private TextView tvPermissionAppRecent;
    private TextView tvAppSuggest;
    private View rltContent;
    private RecyclerView rcvSuggest1, rcvSuggest2;
    private ArrayList<AppInstallModel> appRecents;
    private ArrayList<AppInstallModel> recent1, recent2;
    private RecentAppAdapter recentAppAdapter1, recentAppAdapter2;
    private boolean showmore = true;
    private int heightMore = 0;
    private int heightLess = 0;

    private View backgroundTop;
    private LinearLayout containerSuggest;

    private SearchView searchView;

    private ImageBackgroundView background;
    private boolean isSetbackground;

    private OnSuggestListener onSuggestListener;
    private RecentAppAdapter.OnRecentAppListener onRecentAppListener = new RecentAppAdapter.OnRecentAppListener() {
        @Override
        public void onClick(String pka) {
            Intent intent = new Intent(Constant.ACTION_OPEN_APP);
            intent.putExtra(Constant.PACKAGE_NAME_APP_OPEN, pka);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            if (searchView != null) {
                searchView.disableSearch();
            }
        }
    };

    public SuggestView(Context context) {
        super(context);
        init(context);
    }

    public SuggestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SuggestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setOnSuggestListener(OnSuggestListener onSuggestListener) {
        this.onSuggestListener = onSuggestListener;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
    }

    public void setSetColorBlur() {
        int colorTitle;
        int colorContent;
        if (App.tinyDB.getInt(STYLE_SELECTED, LIGHT) == 0) {
            colorContent = R.color.colorBackgroundContentWidget;
            colorTitle = R.color.colorBackgroundTopWidget;
        } else {
            colorContent = R.color.color_background_item_dark;
            colorTitle = R.color.color_background_item;
        }

        backgroundTop.setBackgroundColor(colorTitle);
        rltContent.setBackgroundColor(colorContent);
    }

    public void updateBg() {
        setViewBackground(background);

    }

    private void init(Context ctx) {
        this.context = ctx;

        LayoutInflater.from(context).inflate(R.layout.view_app_suggest, this, true);

        background = findViewById(R.id.background);
//        updateBg();

        icon = findViewById(R.id.icon);
        backgroundTop = findViewById(R.id.vHeader);
        tvPermissionAppRecent = findViewById(R.id.tvPermissionAppRecent);
        tvAppSuggest = findViewById(R.id.tvAppSuggest);
        actionShowMore = findViewById(R.id.actionShowMore);
        containerSuggest = findViewById(R.id.containerSuggest);
        rltContent = findViewById(R.id.rltContent);
        rcvSuggest1 = findViewById(R.id.rcvSuggest1);
        rcvSuggest2 = findViewById(R.id.rcvSuggest2);
        appRecents = new ArrayList<>();
        recent1 = new ArrayList<>();
        recent2 = new ArrayList<>();
        rcvSuggest1.setLayoutManager(new NpaGridLayoutManager(context, 4));
        rcvSuggest2.setLayoutManager(new NpaGridLayoutManager(context, 4));
        recentAppAdapter1 = new RecentAppAdapter(context, recent1, onRecentAppListener);
        recentAppAdapter2 = new RecentAppAdapter(context, recent2, onRecentAppListener);
        rcvSuggest1.setAdapter(recentAppAdapter1);
        rcvSuggest2.setAdapter(recentAppAdapter2);

        actionShowMore.setOnClickListener(v -> showMoreLess());

        tvPermissionAppRecent.setOnClickListener(v -> {

            if (!PermissionManager.getInstance().checkPermissionAppRecent(context)) {
                if (searchView != null) {
                    searchView.closeKeyBoard();
                    searchView.disableSearch();
                }

                Intent intent = new Intent(Constant.ACTION_OPEN_APP);
                intent.putExtra(Constant.PACKAGE_NAME_APP_OPEN, Constant.REQUEST_PERMISSION_APP_RECENT);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
//                    tvPermissionAppRecent.setText("");
            } else {
                updateAppSuggest();
            }
        });
    }

    public void setAppRecents(ArrayList<AppInstallModel> appRecents) {
        recent1.clear();
        recent2.clear();

        checkPermissionShowUI();
        if (appRecents.size() > 4) {
            for (int i = 0; i < 4; i++) {
                recent1.add(appRecents.get(i));
            }

            for (int i = 4; i < appRecents.size(); i++) {
                recent2.add(appRecents.get(i));
            }
        } else {
            recent1.addAll(appRecents);
        }
        recentAppAdapter1.notifyDataSetChanged();
        recentAppAdapter2.notifyDataSetChanged();
    }

    public void updateAppSuggest() {
        appRecents = SuggestAppManager.getInstance().getSuggestApps();
        recent1.clear();
        recent2.clear();
        checkPermissionShowUI();
        for (int i = 0; i < appRecents.size(); i++) {
            if (i >= 4) {
                break;
            }
            recent1.add(appRecents.get(i));
        }
        if (appRecents.size() > 4) {
            for (int i = 4; i < appRecents.size(); i++) {
                recent2.add(appRecents.get(i));
            }
        }
        recentAppAdapter1.notifyDataSetChanged();
        recentAppAdapter2.notifyDataSetChanged();
    }

    public boolean isSetbackground() {
        return isSetbackground;
    }

    public void setupWhenSearch(SearchView searchView) {
        this.searchView = searchView;
        background.setVisibility(GONE);
        containerSuggest.setBackgroundResource(R.drawable.background_suggest_search);
        tvPermissionAppRecent.setBackgroundResource(R.drawable.background_suggest_search);
        backgroundTop.setBackgroundColor(Color.TRANSPARENT);
        rltContent.setBackgroundColor(Color.TRANSPARENT);
        icon.setVisibility(GONE);

        recentAppAdapter1.setColor(Color.WHITE);
        recentAppAdapter2.setColor(Color.WHITE);

        actionShowMore.setTextColor(ContextCompat.getColor(context, R.color.colorTextSearchHint));
        tvAppSuggest.setTextColor(ContextCompat.getColor(context, R.color.colorTextSearchHint));
        tvPermissionAppRecent.setTextColor(Color.WHITE);
    }

    private void showMoreLess() {
        if (heightMore == 0) {
            heightMore = containerSuggest.getHeight() + backgroundTop.getHeight();
            heightLess = containerSuggest.getHeight() / 2 + backgroundTop.getHeight();
        }

        int from = containerSuggest.getHeight() + backgroundTop.getHeight();
        int to;
        showmore = !showmore;
        if (showmore) {
            to = heightMore;
            actionShowMore.setText(context.getString(R.string.show_less));
        } else {
            to = heightLess;
            actionShowMore.setText(context.getString(R.string.show_more));
        }
        animationMoreLess(this, from, to);
    }

    private void checkPermissionShowUI() {
        if (PermissionManager.getInstance().checkPermissionAppRecent(context)) {
            containerSuggest.setVisibility(VISIBLE);
            tvPermissionAppRecent.setVisibility(GONE);
            actionShowMore.setVisibility(VISIBLE);
        } else {
            containerSuggest.setVisibility(GONE);
            tvPermissionAppRecent.setVisibility(VISIBLE);
            actionShowMore.setVisibility(GONE);
            setTextPermissionAppRecent(false);
        }
    }

    public void setTextPermissionAppRecent(boolean allowPermission) {
        tvPermissionAppRecent.setText(allowPermission ? R.string.processing : R.string.access_permission_app_recent);
    }

    @Subscribe
    public void onEventAppRecent(EventAppRecent eventAppRecent) {
        if (PermissionManager.getInstance().checkPermissionAppRecent(context)) {
            new Handler(Looper.getMainLooper()).post(this::updateAppSuggest);

        }
    }



    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    public interface OnSuggestListener {
        void onBackgroundSet();
    }


}

package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group3;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.adapter.ScreenTimeoutAdapter;
import com.tapbi.spark.controlcenter.feature.controlios14.model.TimeoutScreen;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlSettingIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ConstraintLayoutBase;
import com.tapbi.spark.controlcenter.ui.transparent.TransparentActivity;
import com.tapbi.spark.controlcenter.utils.helper.rcvhepler.NpaLinearLayoutManager;

import java.util.ArrayList;

public class ScreenTimeoutSettingView extends ConstraintLayoutBase implements ScreenTimeoutAdapter.IClickScreenTimeout {
    private final int TIMEOUT_15S = 15000;
    private final int TIMEOUT_30S = 30000;
    private final int TIMEOUT_1M = 60000;
    private final int TIMEOUT_2M = 120000;
    private final int TIMEOUT_5M = 300000;
    private final int TIMEOUT_10M = 600000;
    private final int TIMEOUT_30M = 1800000;
    private final ArrayList<TimeoutScreen> listTime = new ArrayList<>();
    private Context context;
    private ScreenTimeoutAdapter timeoutAdapter;
    private RecyclerView rcvTime;
    private TextView tvTitle;
    private int time = 0;
    private int posSelected = 0;

    private final ContentObserver contentObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            try {
                time = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
                getPosSelected();
                updateTimeout(time);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
        }
    };
    private boolean firstLoad = true;

    public ScreenTimeoutSettingView(Context context) {
        super(context);
        init(context);
    }


    public ScreenTimeoutSettingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScreenTimeoutSettingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        context.getContentResolver().registerContentObserver(Settings.System.getUriFor(Settings.System.SCREEN_OFF_TIMEOUT), false, contentObserver);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        context.getContentResolver().unregisterContentObserver(contentObserver);
    }

    private void init(Context context) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.layout_screen_timeout_open, this, true);


        NpaLinearLayoutManager linearLayoutManager = new NpaLinearLayoutManager(context, RecyclerView.VERTICAL, false);
        rcvTime = findViewById(R.id.rcvTimeout);
        tvTitle = findViewById(R.id.tvTitleViewTimeout);
        try {
            time = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }


        listTime.add(new TimeoutScreen(TIMEOUT_15S, context.getString(R.string.timeout_15s)));
        listTime.add(new TimeoutScreen(TIMEOUT_30S, context.getString(R.string.timeout_30s)));
        listTime.add(new TimeoutScreen(TIMEOUT_1M, context.getString(R.string.timeout_1m)));
        listTime.add(new TimeoutScreen(TIMEOUT_2M, context.getString(R.string.timeout_2m)));
        listTime.add(new TimeoutScreen(TIMEOUT_5M, context.getString(R.string.timeout_5m)));
        listTime.add(new TimeoutScreen(TIMEOUT_10M, context.getString(R.string.timeout_10m)));
//        listTime.add(new TimeoutScreen(TIMEOUT_30M, context.getString(R.string.timeout_30m)));
        getPosSelected();
        timeoutAdapter = new ScreenTimeoutAdapter(context, this);
        updateTimeout(time);
        rcvTime.setLayoutManager(linearLayoutManager);
        rcvTime.setAdapter(timeoutAdapter);
        invalidate();
    }

    private void getPosSelected() {
        for (int i = 0; i < listTime.size(); i++) {
            if (listTime.get(i).getTime() == time) {
                posSelected = i;
                break;
            }
        }
    }

    public void updateTimeout(int timeSelected) {
        if (timeoutAdapter != null) {
            timeoutAdapter.setData(listTime, timeSelected);
        }
    }


    @Override
    public void timeOut(int pos, TimeoutScreen itemTime) {
        this.time = itemTime.getTime();
        this.posSelected = pos;
        Intent intent = new Intent(getContext(), TransparentActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setAction(TransparentActivity.ACTION_CHANGE_TIME_OUT_SCREEN);
        intent.putExtra(TransparentActivity.KEY_VALUE_TIME_OUT_SCREEN, time);
        getContext().startActivity(intent);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (getVisibility() == View.VISIBLE && rcvTime != null) {
            if (firstLoad) {
                rcvTime.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        new Handler().postDelayed(() -> scrollRcvTime(), 100);
                        rcvTime.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
                firstLoad = false;
            } else {
                scrollRcvTime();
            }
        }
    }

    private void scrollRcvTime() {
        if (rcvTime != null) {
            rcvTime.smoothScrollToPosition(posSelected);
        }
    }

    public void changeFont(Typeface typeface){
        tvTitle.setTypeface(typeface);
        if (timeoutAdapter != null) {
            timeoutAdapter.changeFont(typeface);
        }
    }

}



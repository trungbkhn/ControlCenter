package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.eventbus.EventOpen;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlSettingIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ImageBase;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.callback.OnClickSettingListener;
import com.tapbi.spark.controlcenter.ui.splash.SplashActivity;

import org.greenrobot.eventbus.EventBus;

public class EdgeActionView extends ImageBase {

    private Context context;
    private Handler handler;

    private OnClickSettingListener onClickSettingListener;

    public void setOnClickSettingListener(OnClickSettingListener onClickSettingListener) {
        this.onClickSettingListener = onClickSettingListener;
    }

    public EdgeActionView(Context context) {
        super(context);
        init(context);
    }

    public EdgeActionView(Context context, ControlSettingIosModel controlSettingIosModel) {
        super(context);
        this.controlSettingIosModel = controlSettingIosModel;
        init(context);
    }

    public EdgeActionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EdgeActionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        handler = new Handler();
        setImageResource(R.drawable.ic_action_edge_triggers);
        setBackgroundImage();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int paddingIcon = (int) (w * 0.25f);
        setPadding(paddingIcon, paddingIcon, paddingIcon, paddingIcon);
    }

    private void openEdgeSettings() {
        if (App.isStartActivity) {
            EventBus.getDefault().post(new EventOpen(Constant.ACTION_EDGE_TRIGGERS));
        } else {
            Intent intent = new Intent(getContext(), SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Constant.ACTION_EDGE_TRIGGERS);
            getContext().startActivity(intent);
        }
    }

    @Override
    protected void click() {
        openEdgeSettings();
        handler.postDelayed(() -> {
            if (onClickSettingListener != null) {
                onClickSettingListener.onClick();
            }
        }, 300);
    }

    @Override
    protected void longClick() {

    }

    @Override
    protected void onDown() {
        animationDown();
    }

    @Override
    protected void onUp() {
        animationUp();
    }
}
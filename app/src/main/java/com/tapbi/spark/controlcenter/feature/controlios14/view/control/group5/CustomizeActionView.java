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
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ImageBase;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.callback.OnClickSettingListener;
import com.tapbi.spark.controlcenter.ui.splash.SplashActivity;

import org.greenrobot.eventbus.EventBus;

import timber.log.Timber;

public class CustomizeActionView extends ImageBase {

    private Context context;
    private Handler handler;

    private OnClickSettingListener onClickSettingListener;

    public void setOnClickSettingListener(OnClickSettingListener onClickSettingListener) {
        this.onClickSettingListener = onClickSettingListener;
    }

    public CustomizeActionView(Context context) {
        super(context);
        init(context);
    }

    public CustomizeActionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomizeActionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        handler = new Handler();
        setImageResource(R.drawable.ic_action_custom_control);
        setBackgroundImage();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int paddingIcon = (int) (w * 0.25f);
        setPadding(paddingIcon, paddingIcon, paddingIcon, paddingIcon);
    }

    private void openCustomizeSettings() {
        Timber.e("NVQ openCustomizeSettings");
        if (App.isStartActivity) {
            EventBus.getDefault().post(new EventOpen(Constant.ACTION_CUSTOMIZE_CONTROL));
        } else {
            Timber.e("NVQ openCustomizeSettings go SplashActivity");

            Intent intent = new Intent(getContext() != null ? getContext()  : App.mContext, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Constant.ACTION_CUSTOMIZE_CONTROL);
            (getContext()  != null ? getContext()  : App.mContext).startActivity(intent);
        }
    }

    @Override
    protected void click() {
        openCustomizeSettings();
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
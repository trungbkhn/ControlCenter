package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ImageBase;
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614;
import com.tapbi.spark.controlcenter.utils.SettingUtils;

import timber.log.Timber;


@SuppressLint("AppCompatCustomView")
public class WifiSettingView extends ImageBase {
    private Context context;
    private OnAnimationListener onAnimationListener;
    private boolean enableWifi=true;

    public WifiSettingView(Context context) {
        super(context);
        init(context);
    }

    public WifiSettingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WifiSettingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        changeIsSelect(enableWifi);
    }

    public void setViewTouching(boolean touching) {
        anotherViewTouching = touching;
    }


    @Override
    protected void click() {
        animationClick();
        Timber.e("NVQ STRING_ACTION_WIFI3");
        NotyControlCenterServicev614.getInstance().setWifiNoty();
    }

    @Override
    protected void longClick() {
        Timber.e("NVQ onHideControl2");
        SettingUtils.intentChangeWifi(getContext());
        if (onAnimationListener != null) {
            onAnimationListener.onLongClick();
        }
    }

    @Override
    protected void onDown() {
        if (onAnimationListener != null) {
            onAnimationListener.onDown();
        }
        animationDown();
    }

    @Override
    protected void onUp() {
        if (onAnimationListener != null) {
            onAnimationListener.onUp();
        }
        animationUp();
    }

    public void setOnAnimationListener(OnAnimationListener onAnimationListener) {
        this.onAnimationListener = onAnimationListener;
    }


    public void updateState(boolean b) {
        this.enableWifi = b;
        cancelAni();
//        setImageUpdate(b);
    }

    private void setImageUpdate(boolean b) {
//        if (b) {
//            setImageResource(R.drawable.wifi_on);
//        } else {
//            setImageResource(R.drawable.wifi_off);
//        }
        changeIsSelect(b);
    }

    private final Handler handlerAnimation = new Handler(Looper.getMainLooper());
    private int statusHandlerWifi = 3;
    private final Runnable runnableAniWifi = new Runnable() {
        @Override
        public void run() {
            if (statusHandlerWifi == 3) {
                statusHandlerWifi = 1;
                setImageResource(R.drawable.ic_wifi_on_ios_1);
            } else if (statusHandlerWifi == 1) {
                statusHandlerWifi = 2;
                setImageResource(R.drawable.ic_wifi_on_ios_2);
            } else {
                statusHandlerWifi = 3;
                setImageResource(R.drawable.ic_wifi_ios);
            }
            handlerAnimation.postDelayed(this, 400);
        }
    };

    private void animationClick() {
        handlerAnimation.removeCallbacks(runnableAniWifi);
        handlerAnimation.postDelayed(runnableAniWifi, 300);
    }

    private void cancelAni() {
        handlerAnimation.removeCallbacks(runnableAniWifi);
        setImageUpdate(enableWifi);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility != View.VISIBLE) {
            cancelAni();
        }
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int paddingIcon = (int) (w * 0.267);
        setPadding(paddingIcon, paddingIcon, paddingIcon, paddingIcon);
    }
}

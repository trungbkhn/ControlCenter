package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ImageBase;
import com.tapbi.spark.controlcenter.feature.controlcenter.utils.DataMobileUtils;
import com.tapbi.spark.controlcenter.interfaces.IListenActionClick;
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614;
import com.tapbi.spark.controlcenter.utils.SettingUtils;
import com.tapbi.spark.controlcenter.utils.VibratorUtils;

@SuppressLint("AppCompatCustomView")
public class DataSettingView extends ImageBase {

    private Context context;
    private OnAnimationListener onAnimationListener;
    private DataMobileUtils dataMobileUtils;

    public DataSettingView(Context context) {
        super(context);
        init(context);
    }

    public DataSettingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DataSettingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        dataMobileUtils = new DataMobileUtils(context);
        updateState(dataMobileUtils.isDataEnable());
    }

    public void setViewTouching(boolean touching) {
        anotherViewTouching = touching;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //Timber.e(".");
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (getVisibility() == View.VISIBLE && dataMobileUtils != null) {
            updateState(!SettingUtils.isAirplaneModeOn(getContext()) && dataMobileUtils.isDataEnable());
        } else if (visibility != VISIBLE) {
            stopAniZoom();;
        }
    }

    public void updateState(boolean enabled) {
        stopAniZoom();
        setViewState(enabled);
    }

    private void setViewState(boolean enabled) {
//        if (enabled) {
//            setImageResource(R.drawable.anten_on);
//        } else {
//            setImageResource(R.drawable.anten_off);
//        }
        changeIsSelect(enabled);
    }

    @Override
    protected void click() {
//        if (!NotyControlCenterServicev614.getInstance().isAirPlaneModeEnabled()) {
//            animationClick();
//        }
//        NotyControlCenterServicev614.getInstance().setHandingAction(this::stopAniZoom, Constant.STRING_ACTION_DATA_MOBILE);
        if (NotyControlCenterServicev614.getInstance() != null) {
            if (NotyControlCenterServicev614.getInstance().allowClickAction()) {
                if (!NotyControlCenterServicev614.getInstance().isAirPlaneModeEnabled()
                        && SettingUtils.hasSimCard(getContext())) {
//                animationClick();
                    statAniZoom();
                }
                NotyControlCenterServicev614.getInstance().setHandingAction(new IListenActionClick() {
                    @Override
                    public void noFindAction() {
                        stopAniZoom();
                    }

                    @Override
                    public void actionClicked() {
                        stopAniZoom();
                    }
                }, Constant.STRING_ACTION_DATA_MOBILE);
            } else {
                NotyControlCenterServicev614.getInstance().showToast(context.getString(R.string.wait_until_job_done));
            }
        }


    }

    @Override
    protected void longClick() {
        if (App.tinyDB.getBoolean(Constant.VIBRATOR_CONTROL_LONG_CLICK, Constant.VALUE_DEFAULT_VIBRATOR)) {
            VibratorUtils.getInstance(context).vibrator(VibratorUtils.TIME_DEFAULT);
        }
        if (!SettingUtils.hasSimCard(getContext())) {
            NotyControlCenterServicev614.getInstance().showDialogContent(() -> {
                if (onAnimationListener != null) {
                    onAnimationListener.onLongClick();
                }
            });
            return;
        }
        SettingUtils.intentChangeDataMobile(getContext());
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

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int paddingIcon = (int) (w * 0.267);
        setPadding(paddingIcon, paddingIcon, paddingIcon, paddingIcon);
    }


}

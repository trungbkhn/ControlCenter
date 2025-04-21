package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ImageBase;
import com.tapbi.spark.controlcenter.interfaces.IListenActionClick;
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614;
import com.tapbi.spark.controlcenter.utils.SettingUtils;
import com.tapbi.spark.controlcenter.utils.VibratorUtils;

@SuppressLint("AppCompatCustomView")
public class AirPlaneSettingView extends ImageBase {

    private Context context;
    private OnAnimationListener onAnimationListener;

    private boolean isSetRatioIcon = true;
    //private AirPlaneReceiver airPlaneReceiver;
//    private IntentFilter intentFilter;

    public AirPlaneSettingView(Context context) {
        super(context);
        init(context);
    }

    public AirPlaneSettingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AirPlaneSettingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
    }

    public void setViewTouching(boolean touching) {
        anotherViewTouching = touching;
    }

    public void updateAirPlaneState(boolean b) {
        stopAniZoom();
//        if (b) {
//            setImageResource(R.drawable.airplane_on);
//        } else {
//            setImageResource(R.drawable.airplane_off);
//        }
        changeIsSelect(b);
    }

    @Override
    protected void click() {
        if (NotyControlCenterServicev614.getInstance().allowClickAction()) {
            statAniZoom();
            if (NotyControlCenterServicev614.getInstance() != null) {
                NotyControlCenterServicev614.getInstance().setHandingAction(new IListenActionClick() {
                    @Override
                    public void noFindAction() {
                        stopAniZoom();

                    }

                    @Override
                    public void actionClicked() {
                        stopAniZoom();
                    }
                }, Constant.STRING_ACTION_AIRPLANE_MODE);
            }
        } else {
            if (NotyControlCenterServicev614.getInstance() != null) {
                NotyControlCenterServicev614.getInstance().showToast(context.getString(R.string.wait_until_job_done));
            }
        }

    }

    @Override
    protected void longClick() {
        if (App.tinyDB.getBoolean(Constant.VIBRATOR_CONTROL_LONG_CLICK, Constant.VALUE_DEFAULT_VIBRATOR)) {
            VibratorUtils.getInstance(context).vibrator(VibratorUtils.TIME_DEFAULT);
        }
        SettingUtils.intentChangeAirPlane(getContext());
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

    public void changeSetRatioIcon(boolean isSetRatioIcon) {
        this.isSetRatioIcon = isSetRatioIcon;
        requestLayout();
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (isSetRatioIcon) {
            int paddingIcon = (int) (w * 0.267);
            setPadding(paddingIcon, paddingIcon, paddingIcon, paddingIcon);
        } else {
            setPadding(0, 0, 0, 0);
        }
    }

}

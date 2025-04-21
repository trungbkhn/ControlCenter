package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.common.models.MessageEvent;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlSettingIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ImageBase;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.callback.OnClickSettingListener;
import com.tapbi.spark.controlcenter.interfaces.IListenActionClick;
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614;
import com.tapbi.spark.controlcenter.utils.SettingUtils;

import org.greenrobot.eventbus.EventBus;

public class LowPowerActionView extends ImageBase {

    private final Handler handlerBattery = new Handler();
    private Context context;
    private OnClickSettingListener onClickSettingListener;
    private int countRunnableBattery = 0;
    private boolean modeOn;

    public LowPowerActionView(Context context) {
        super(context);
        init(context);
    }

    public LowPowerActionView(Context context, ControlSettingIosModel controlSettingIosModel, DataSetupViewControlModel dataSetupViewControlModel) {
        super(context);
        this.controlSettingIosModel = controlSettingIosModel;
        this.dataSetupViewControlModel = dataSetupViewControlModel;
        init(context);
    }

    public LowPowerActionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private final Runnable runnableBattery = new Runnable() {
        @Override
        public void run() {
            countRunnableBattery++;
            boolean modeOnCurrent = SettingUtils.isPowerSaveMode(context);
            if (modeOn != modeOnCurrent) {
                setStates(SettingUtils.isPowerSaveMode(context));
            }
            handlerBattery.postDelayed(runnableBattery, 1000);
            if (countRunnableBattery > 5) {
                handlerBattery.removeCallbacks(this);
            }
        }
    };

    public LowPowerActionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setOnClickSettingListener(OnClickSettingListener onClickSettingListener) {
        this.onClickSettingListener = onClickSettingListener;
    }

    private void init(Context context) {
        this.context = context;
        setStates(SettingUtils.isPowerSaveMode(context));
        initColorIcon();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        int paddingIcon = (int) (w * 0.25f);
//        setPadding(paddingIcon, paddingIcon, paddingIcon, paddingIcon);
        int paddingIcon = (int) (w * 0.267);
        setPadding(paddingIcon, paddingIcon, paddingIcon, paddingIcon);
    }

    public void setStates(boolean modeOn) {
        this.modeOn = modeOn;
        stopAniZoom();
        if (modeOn) {
//            setBackgroundResource(R.drawable.background_boder_radius_white);
            setImageResource(R.drawable.ic_low_power_mode_on);
        } else {
//            setBackgroundResource(R.drawable.background_boder_radius_gray);
            setImageResource(R.drawable.ic_low_power_mode);
        }
        changeIsSelect(modeOn);
        EventBus.getDefault().post(new MessageEvent(Constant.CHANGE_LOW_POWER));
    }

    private void openNotyFindLowPowerMode() {
        NotyControlCenterServicev614.getInstance().setHandingAction(new IListenActionClick() {
            @Override
            public void noFindAction() {
                stopAniZoom();
            }

            @Override
            public void actionClicked() {
                stopAniZoom();
            }
        }, Constant.STRING_ACTION_BATTERY);

        countRunnableBattery = 0;
        handlerBattery.removeCallbacks(runnableBattery);
        handlerBattery.postDelayed(runnableBattery, 1000);

    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (isShown()) {
            boolean modeOnCurrent = SettingUtils.isPowerSaveMode(context);
            if (modeOn != modeOnCurrent) {
                setStates(SettingUtils.isPowerSaveMode(context));
            }
        }
    }

    @Override
    protected void click() {
        if (NotyControlCenterServicev614.getInstance().allowClickAction()) {
            statAniZoom();
            openNotyFindLowPowerMode();
        } else {
            if (NotyControlCenterServicev614.getInstance() != null) {
                NotyControlCenterServicev614.getInstance().showToast(context.getString(R.string.wait_until_job_done));
            }
        }

    }

    @Override
    protected void longClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            SettingUtils.intentChangeBatterySaver(getContext());
            if (onClickSettingListener != null) {
                onClickSettingListener.onClick();
            }
        }
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
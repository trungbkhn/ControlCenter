package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.common.models.MessageEvent;
import com.tapbi.spark.controlcenter.databinding.BaseLayoutControlSingleFunctionTextViewBinding;
import com.tapbi.spark.controlcenter.databinding.LayoutControlLowBatteryTextViewBinding;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlSettingIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ConstraintLayoutBase;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.callback.OnClickSettingListener;
import com.tapbi.spark.controlcenter.interfaces.IListenActionClick;
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614;
import com.tapbi.spark.controlcenter.utils.SettingUtils;

import org.greenrobot.eventbus.EventBus;

public class LowPowerTextView extends ConstraintLayoutBase {

    private Context context;
    private ControlSettingIosModel controlSettingIOS;
    private BaseLayoutControlSingleFunctionTextViewBinding binding;
    private boolean isSelect = false;
    private final Handler handlerBattery = new Handler();
    private OnClickSettingListener onClickSettingListener;
    private int countRunnableBattery = 0;

    public LowPowerTextView(Context context) {
        super(context);
        init(context);
    }

    public LowPowerTextView(Context context, ControlSettingIosModel controlSettingIOS, DataSetupViewControlModel dataSetupViewControlModel) {
        super(context);
        this.dataSetupViewControlModel = dataSetupViewControlModel;
        this.controlSettingIOS = controlSettingIOS;
        init(context);
    }

    public LowPowerTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LowPowerTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private final Runnable runnableBattery = new Runnable() {
        @Override
        public void run() {
            countRunnableBattery++;
            boolean modeOnCurrent = SettingUtils.isPowerSaveMode(context);
            if (isSelect != modeOnCurrent) {
                setStates(SettingUtils.isPowerSaveMode(context));
            }
            handlerBattery.postDelayed(runnableBattery, 1000);
            if (countRunnableBattery > 5) {
                handlerBattery.removeCallbacks(this);
            }
        }
    };

    private void init(Context context){
        this.context = context;
        binding = BaseLayoutControlSingleFunctionTextViewBinding.inflate(LayoutInflater.from(context), this, true);
        binding.tvFunction.setSelected(true);
        setStates(SettingUtils.isPowerSaveMode(context));
        initView();
    }

    public void initView(){
        if (controlSettingIOS != null){
            changeColorBackground(controlSettingIOS.getBackgroundDefaultColorViewParent(), controlSettingIOS.getBackgroundSelectColorViewParent(), controlSettingIOS.getCornerBackgroundViewParent());
        }

        binding.tvFunction.setTypeface(dataSetupViewControlModel.getTypefaceText());
        binding.tvFunction.setText(context.getString(R.string.low_power_mode));
        updateUI();
    }
    public void setOnClickSettingListener(OnClickSettingListener onClickSettingListener) {
        this.onClickSettingListener = onClickSettingListener;
    }



    public void setStates(boolean modeOn) {
        this.isSelect = modeOn;
        stopAniZoom();
        if (modeOn) {
//            setBackgroundResource(R.drawable.background_boder_radius_white);
            binding.imgIcon.setImageResource(R.drawable.ic_low_power_mode_on);
        } else {
//            setBackgroundResource(R.drawable.background_boder_radius_gray);
            binding.imgIcon.setImageResource(R.drawable.ic_low_power_mode);
        }
        updateUI();
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
            if (isSelect != modeOnCurrent) {
                setStates(SettingUtils.isPowerSaveMode(context));
            }
        }
    }


    @Override
    protected void onTouchDown() {
        super.onTouchDown();
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
    public void setOnLongClickListener(@Nullable OnLongClickListener l) {
        super.setOnLongClickListener(l);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            SettingUtils.intentChangeBatterySaver(getContext());
            if (onClickSettingListener != null) {
                onClickSettingListener.onClick();
            }
        }
    }


    private void updateUI(){
        if (controlSettingIOS != null) {
            if (isSelect) {
                binding.tvFunction.setTextColor(Color.parseColor(controlSettingIOS.getColorTextTitleSelect()));
                binding.imgIcon.setColorFilter(Color.parseColor(controlSettingIOS.getColorSelectIcon()));
            } else {
                binding.tvFunction.setTextColor(Color.parseColor(controlSettingIOS.getColorTextTitle()));
                binding.imgIcon.setColorFilter(Color.parseColor(controlSettingIOS.getColorDefaultIcon()));

            }
        }
        changeIsSelect(isSelect);
    }
}

package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group1;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.annotation.Nullable;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.databinding.LayoutControlAirplaneBinding;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlSettingIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ConstraintLayoutBase;
import com.tapbi.spark.controlcenter.interfaces.IListenActionClick;
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614;
import com.tapbi.spark.controlcenter.utils.SettingUtils;
import com.tapbi.spark.controlcenter.utils.VibratorUtils;

public class ControlAirplaneView extends ConstraintLayoutBase {


    public SettingView.OnSettingListener onSettingListener;
    private LayoutControlAirplaneBinding binding;
    private ControlSettingIosModel controlSettingIOS;
    private boolean isLongClick;
    private Handler handler;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            isLongClick = true;
            longClick();
        }
    };

    public ControlAirplaneView(Context context) {
        super(context);
        init(context);
    }

    public ControlAirplaneView(Context context, ControlSettingIosModel controlSettingIOS, DataSetupViewControlModel dataSetupViewControlModel) {
        super(context);
        this.dataSetupViewControlModel = dataSetupViewControlModel;
        this.controlSettingIOS = controlSettingIOS;
        init(context);
    }

    public ControlAirplaneView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ControlAirplaneView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        handler = new Handler(Looper.getMainLooper());
        binding = LayoutControlAirplaneBinding.inflate(LayoutInflater.from(context), this, true);
        binding.tvAirplane.setSelected(true);
        initView();
        binding.airplaneAction.changeSetRatioIcon(false);
//        binding.airplaneAction.setOnAnimationListener(new AirPlaneSettingView.OnAnimationListener() {
//            @Override
//            public void onDown() {
//                if (onSettingListener != null) {
//                    onSettingListener.onDown();
//                }
//            }
//
//            @Override
//            public void onUp() {
//                if (onSettingListener != null) {
//                    onSettingListener.onUp();
//                }
//            }
//
//            @Override
//            public void onClick() {
//                if (onSettingListener != null) {
//                    onSettingListener.onHide();
//                }
//            }
//
//            @Override
//            public void onLongClick() {
//                if (onSettingListener != null) {
//                    onSettingListener.onHide();
//                }
//            }
//
//            @Override
//            public void onClose() {
//
//            }
//        });
    }

    public void initView() {
        if (controlSettingIOS != null) {
            binding.airplaneAction.changeData(controlSettingIOS);
            changeColorBackground(controlSettingIOS.getBackgroundDefaultColorViewParent(), controlSettingIOS.getBackgroundSelectColorViewParent(), controlSettingIOS.getCornerBackgroundViewParent());
            binding.tvAirplane.setTextColor(Color.parseColor(controlSettingIOS.getColorTextTitle()));
            binding.tvAirplane.setTypeface(dataSetupViewControlModel.getTypefaceText());
        }
    }

    public void updateBgAirplane(boolean b) {
        binding.airplaneAction.updateAirPlaneState(b);
        if (b) {
            binding.tvAirplane.setTextColor(Color.parseColor(controlSettingIOS.getColorTextTitleSelect()));
        } else {
            binding.tvAirplane.setTextColor(Color.parseColor(controlSettingIOS.getColorTextTitle()));
        }
        changeIsSelect(b);
    }

    public void setViewTouching(boolean touching) {
        binding.airplaneAction.setViewTouching(touching);
    }

    public void changeControlSettingIos(ControlSettingIosModel controlSettingIosModel) {
        this.controlSettingIOS = controlSettingIosModel;
        initView();
    }

    public void setOnSettingListener(SettingView.OnSettingListener onSettingListener) {
        this.onSettingListener = onSettingListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                animationDown();
                handler.postDelayed(runnable, ViewConfiguration.getLongPressTimeout());
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                handler.removeCallbacks(runnable);
                animationUp();

                if (checkClick(event.getX(), event.getY()) && !isLongClick) {
                    click();
                }
                isLongClick = false;
                break;
        }
        return true;
    }

    private void longClick() {
        if (App.tinyDB.getBoolean(Constant.VIBRATOR_CONTROL_LONG_CLICK, Constant.VALUE_DEFAULT_VIBRATOR)) {
            VibratorUtils.getInstance(getContext()).vibrator(VibratorUtils.TIME_DEFAULT);
        }
        SettingUtils.intentChangeAirPlane(getContext());
        if (onSettingListener != null) {
            onSettingListener.onHide();
        }
    }

    private void click() {
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
                NotyControlCenterServicev614.getInstance().showToast(getContext().getString(R.string.wait_until_job_done));
            }
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        binding.tvAirplane.setTextSize(TypedValue.COMPLEX_UNIT_PX, 0.13f * h);
    }
}

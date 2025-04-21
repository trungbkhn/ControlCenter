package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group1;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.animation.AlphaAnimation;

import androidx.annotation.Nullable;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.databinding.LayoutControlAirplaneRecordSyndataBinding;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlSettingIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ConstraintLayoutBase;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.callback.OnClickSettingListener;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5.ScreenRecordActionView;
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614;
import com.tapbi.spark.controlcenter.utils.SettingUtils;

public class ControlAirplaneRecordSynDataView extends ConstraintLayoutBase {

    private final Handler handler = new Handler();
    public SettingView.OnSettingListener onSettingListener;
    private final Runnable runnable = this::onLongClick;
    private LayoutControlAirplaneRecordSyndataBinding binding;

    private ControlSettingIosModel controlSettingIOS;

    public ControlAirplaneRecordSynDataView(Context context) {
        super(context);
        init(context);
    }

    public ControlAirplaneRecordSynDataView(Context context, ControlSettingIosModel controlSettingIosModel, DataSetupViewControlModel dataSetupViewControlModel) {
        super(context);
        this.dataSetupViewControlModel = dataSetupViewControlModel;
        this.controlSettingIOS = controlSettingIosModel;
        init(context);
    }

    public ControlAirplaneRecordSynDataView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ControlAirplaneRecordSynDataView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void onLongClick() {
        if (onSettingListener != null) {
            onSettingListener.onLongClick(this);
        }
    }

    public void updateBgAirplane(boolean b) {
        binding.airplaneAction.updateAirPlaneState(b);
        binding.tvAirplaneConnection.setText(b ? getContext().getText(R.string.text_on) : getContext().getText(R.string.text_off));
    }

    public void updateStateDataSyn(){
        boolean isEnable = SettingUtils.isSyncAutomaticallyEnable();
        binding.tvSynDataConnection.setText(isEnable ? getContext().getText(R.string.text_on) : getContext().getText(R.string.text_off));
        binding.syndataAction.changeIsSelect(isEnable);
    }



    public void setViewTouching(boolean touching) {
        binding.airplaneAction.setViewTouching(touching);
    }

    private void init(Context context) {
        binding = LayoutControlAirplaneRecordSyndataBinding.inflate(LayoutInflater.from(context), this, true);

        initView();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (onSettingListener != null) {
                    onSettingListener.onDown();
                }
                handler.postDelayed(runnable, ViewConfiguration.getLongPressTimeout());
                animationDown();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (onSettingListener != null) {
                    onSettingListener.onUp();
                }
                handler.removeCallbacks(runnable);
                animationUp();
                break;
        }
        return true;
    }

    public void setOnSettingListener(SettingView.OnSettingListener onSettingListener) {
        this.onSettingListener = onSettingListener;
        binding.syndataAction.setOnSettingListener(onSettingListener);
    }

    public void destroy() {
        //wifiAction.destroy();
        //bluetoothAction.destroy();
        //dataAction.destroy();
        //airPlaneAction.destroy();
    }

    public void initView() {
        if (controlSettingIOS != null) {
            changeColorBackground(controlSettingIOS.getBackgroundDefaultColorViewParent(), controlSettingIOS.getBackgroundSelectColorViewParent(), controlSettingIOS.getCornerBackgroundViewParent());
            initAirplanes();
            initViewRecord();
            initSynData();

        }
    }

    private void initViewRecord() {
        if (controlSettingIOS != null) {
            binding.recordAction.changeData(controlSettingIOS);
            binding.recordAction.setColorTextCount(Color.parseColor(controlSettingIOS.getColorTextTitle()));
            binding.tvRecord.setTextColor(Color.parseColor(controlSettingIOS.getColorTextTitle()));
            binding.tvRecord.setTypeface(dataSetupViewControlModel.getTypefaceText());
            binding.tvRecordConnection.setTextColor(Color.parseColor(controlSettingIOS.getColorTextDescription()));
            binding.tvRecordConnection.setTypeface(dataSetupViewControlModel.getTypefaceText());
            binding.recordAction.setOnClickSettingListener(() -> {
                if (NotyControlCenterServicev614.getInstance() != null) {
                    NotyControlCenterServicev614.getInstance().closeNotyCenter();
                }
            });
            binding.recordAction.setListenerUpdateViewRecord(b -> binding.tvRecordConnection.setText(b ? getContext().getText(R.string.text_on) : getContext().getText(R.string.text_off)));
        }
    }

    private void initAirplanes() {
        if (controlSettingIOS != null) {
            binding.airplaneAction.changeData(controlSettingIOS);
            binding.tvAirplane.setTextColor(Color.parseColor(controlSettingIOS.getColorTextTitle()));
            binding.tvAirplane.setTypeface(dataSetupViewControlModel.getTypefaceText());
            binding.tvAirplaneConnection.setTypeface(dataSetupViewControlModel.getTypefaceText());
            binding.tvAirplaneConnection.setTextColor(Color.parseColor(controlSettingIOS.getColorTextDescription()));
        }
    }

    private void initSynData() {
        if (controlSettingIOS != null) {
            binding.syndataAction.changeData(controlSettingIOS);
            binding.syndataAction.setOnSettingListener(onSettingListener);
            binding.syndataAction.setIListenerUpdate(b -> binding.tvSynDataConnection.setText(b ? getContext().getText(R.string.text_on) : getContext().getText(R.string.text_off)));
            binding.tvSynData.setTextColor(Color.parseColor(controlSettingIOS.getColorTextTitle()));
            binding.tvSynDataConnection.setTextColor(Color.parseColor(controlSettingIOS.getColorTextDescription()));
            binding.tvSynData.setTypeface(dataSetupViewControlModel.getTypefaceText());
            binding.tvSynDataConnection.setTypeface(dataSetupViewControlModel.getTypefaceText());
        }
    }


    public interface OnSettingListener {
        void onDown();

        void onUp();

        void onHide();

        void onWifiChange();

        void onBluetoothChange(boolean change);

        void onLongClick();

        void onClose();
    }



}

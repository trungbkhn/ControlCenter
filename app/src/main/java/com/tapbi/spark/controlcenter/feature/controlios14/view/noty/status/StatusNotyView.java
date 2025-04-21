package com.tapbi.spark.controlcenter.feature.controlios14.view.noty.status;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.feature.controlios14.view.status.BatteryView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.status.WaveViewNotyIOS;
import com.tapbi.spark.controlcenter.feature.controlios14.view.status.WifiView;

public class StatusNotyView extends RelativeLayout {

    private WaveViewNotyIOS waveView;
    private WifiView wifiView;
    private TextView tvTime;
    private ImageView imgPlane;

    private  BatteryView batteryView;


    public StatusNotyView(Context context) {
        super(context);
        init(context);
    }

    public StatusNotyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StatusNotyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_status_bar, this, true);
        waveView = findViewById(R.id.waveView);
        wifiView = findViewById(R.id.wifiView);
         batteryView = findViewById(R.id.batteryView);
        imgPlane = findViewById(R.id.imgPlane);
        tvTime = findViewById(R.id.tvTime);
        batteryView.setTextShowHide(View.GONE);
    }

    public void updatePlaneMode(boolean b) {
        waveView.setVisibility(b ? GONE : VISIBLE);
        imgPlane.setVisibility(b ? VISIBLE : GONE);
        wifiView.updateTvDataMobile();
    }

    public void updateWifi(boolean b) {
        wifiView.updateWifiMode(b);
    }

    public void updateDataMobile(boolean b) {
        wifiView.updateTvDataMobile();
    }

    public void updateStateSim() {
        waveView.updateStateSim();
    }

    public void setTime(String time) {
        if (tvTime != null) {
            tvTime.setText(time);
        }
    }

    public void setonSignalsChange(int lever){
        if (waveView!=null){
            waveView.onSignalsChange(lever);
        }
    }

    public void changeBattery(boolean isChange, int level) {
        if (batteryView != null) {
            batteryView.setLevelBattery(level);
            batteryView.changeImageBattery(isChange);
        }
    }

    }

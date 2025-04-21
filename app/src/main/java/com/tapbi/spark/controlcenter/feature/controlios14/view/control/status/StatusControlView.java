package com.tapbi.spark.controlcenter.feature.controlios14.view.control.status;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.feature.controlios14.view.status.BatteryView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.status.WaveView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.status.WifiView;


public class StatusControlView extends ConstraintLayout {

    private Context context;
    private WaveView waveView;
    private ImageView imgLockRotate;
    private ImageView imgBluetooth;
    private ImageView imgLocation;
    private WifiView wifiView;
    private ImageView imgPlane;
    private BatteryView batteryView;
    private int colorStatus = Color.WHITE;

    private ViewPropertyAnimator scaleY = null;

    public StatusControlView(Context context) {
        super(context);
        init(context);
    }

    public StatusControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StatusControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context ctx) {
        this.context = ctx;
        LayoutInflater.from(context).inflate(R.layout.layout_status_control, this, true);
        wifiView = findViewById(R.id.wifiView);
        waveView = findViewById(R.id.waveView);
        imgLockRotate = findViewById(R.id.imgLockRotate);
        imgBluetooth = findViewById(R.id.imgBluetooth);
        imgLocation = findViewById(R.id.imgLocation);
        batteryView = findViewById(R.id.batteryView);
        imgPlane = findViewById(R.id.imgPlane);
        updateRotate();
        setColorStatus();
    }

    public void changeColorStatus(int colorStatus){
        this.colorStatus = colorStatus;
        setColorStatus();
    }

    private void setColorStatus(){
        imgBluetooth.setColorFilter(colorStatus);
        imgLocation.setColorFilter(colorStatus);
        imgPlane.setColorFilter(colorStatus);
        imgLockRotate.setColorFilter(colorStatus);
        batteryView.changeColor(colorStatus);
        wifiView.setColor(colorStatus);
        waveView.setColor(colorStatus);
    }

    public void setFont(Typeface typeface){
        waveView.setFont(typeface);
    }


    public void updateBgLocation(boolean b) {
        if (b) {
            imgLocation.setVisibility(VISIBLE);
        } else {
            imgLocation.setVisibility(GONE);
        }
    }

    public void updateBgBluetooth(boolean b) {
        if (b) {
            imgBluetooth.setVisibility(VISIBLE);
        } else {
            imgBluetooth.setVisibility(GONE);
        }
    }

    public void update() {
        updateRotate();
    }

    public void updatePlaneMode(boolean b) {
        waveView.setVisibility(b ? GONE : VISIBLE);
        imgPlane.setVisibility(b ? VISIBLE : GONE);
        wifiView.updateTvDataMobile();
    }

    public void updateDataMobile() {
        wifiView.updateTvDataMobile();
    }

    public void updateWifi(boolean b) {
        wifiView.updateWifiMode(b);
    }

    public void updateStateSim() {
        waveView.updateStateSim();
    }

    public void updateRotate() {
        int autoRotate = -1;
        try {
            autoRotate = Settings.System.getInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        imgLockRotate.setVisibility(autoRotate == 0 ? VISIBLE : GONE);

    }

    public void animationShow() {
        if (scaleY != null) {
            scaleY.cancel();
        }
        setScaleX(0.8f);
        setScaleY(0.8f);
        setAlpha(0f);
        scaleY = animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(300).withEndAction(() -> {
            if (getAlpha() != 1f) {
                setAlpha(1f);
            }
        }).setInterpolator(new DecelerateInterpolator());
        scaleY.start();
    }

    public void animationHide() {
        if (scaleY != null) {
            scaleY.cancel();
        }
        setScaleX(1f);
        setScaleY(1f);
        setAlpha(1f);
        scaleY = animate().scaleX(0.8f).scaleY(0.8f).alpha(0f).setDuration(300).setInterpolator(new AccelerateInterpolator());
        scaleY.start();
    }

    public void setonSignalsChange(int lever) {
        if (waveView != null) {
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

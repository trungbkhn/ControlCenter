package com.tapbi.spark.controlcenter.feature.controlios14.view.status;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.common.models.Battery;
import com.tapbi.spark.controlcenter.common.models.MessageEvent;
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614;
import com.tapbi.spark.controlcenter.utils.DensityUtils;
import com.tapbi.spark.controlcenter.utils.SettingUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


public class BatteryView extends LinearLayout {
    boolean isCharging = false;
    private Context context;
    private TextView tvBattery;
    private ImageView ivBattery;


    public BatteryView(Context context) {
        super(context);
        init(context);
    }

    public BatteryView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BatteryView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context ct) {
        this.context = ct;
        if ( DensityUtils.getOrientationWindowManager(getContext()) == Configuration.ORIENTATION_PORTRAIT) {
            LayoutInflater.from(context).inflate(R.layout.layout_battery, this, true);
        } else {
            LayoutInflater.from(context).inflate(R.layout.layout_battery_land, this, true);
        }
        tvBattery = findViewById(R.id.tvBattery);
        ivBattery = findViewById(R.id.ivBattery);

        if (NotyControlCenterServicev614.getInstance() != null) {
            Battery battery = NotyControlCenterServicev614.getInstance().battery;
            if (battery != null) {
                setLevelBattery(battery.getLevel());
                changeImageBattery(battery.isChange());
            }

        }
    }

    public void changeColor(int color){
        tvBattery.setTextColor(color);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);


    }

    @Subscribe
    public void onEvent(MessageEvent event) {
        if (isAttachedToWindow()) {
            if (event.getTypeEvent() == Constant.CHANGE_LOW_POWER) {
                changeImageBattery(isCharging);
            }
        }

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }


    public void setTextShowHide(int i) {
        if (tvBattery != null) {
            tvBattery.setVisibility(i);
        }
    }

    public void changeImageBattery(boolean isCharging) {
        this.isCharging=isCharging;
        if (isCharging) {
            ivBattery.setImageResource(R.drawable.ic_battery_green);
        } else if (SettingUtils.isPowerSaveMode(context)) {
            ivBattery.setImageResource(R.drawable.ic_battery_yellow);
        } else {
            ivBattery.setImageResource(R.drawable.ic_battery);
        }

    }

    public void setLevelBattery(int level) {
        ivBattery.setImageLevel(level);
    }


}

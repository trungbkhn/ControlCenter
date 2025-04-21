package com.tapbi.spark.controlcenter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

public class BatteryBroadCast extends BroadcastReceiver {


    private IChangeBattery iChangeBattery;

    public BatteryBroadCast(IChangeBattery iChangeBattery) {
        this.iChangeBattery = iChangeBattery;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int lv = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
        iChangeBattery.changeBattery(isCharging,lv/10,lv * 100 / (float)scale);


    }

    public interface IChangeBattery{
        void changeBattery(boolean isCharging, int lv,float pct);
    }
}

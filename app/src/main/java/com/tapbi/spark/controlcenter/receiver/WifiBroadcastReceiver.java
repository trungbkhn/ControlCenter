package com.tapbi.spark.controlcenter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CallBackUpdateUi;

public class WifiBroadcastReceiver extends BroadcastReceiver {
    private CallBackUpdateUi callBackStageFlash;
    private String valueRegister;

    public WifiBroadcastReceiver(CallBackUpdateUi callBackStageFlash, String valueRegister) {
        this.valueRegister = valueRegister;
        this.callBackStageFlash = callBackStageFlash;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
            WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            callBackStageFlash.stage(valueRegister, wifi.isWifiEnabled(),0);
        }
    }
}

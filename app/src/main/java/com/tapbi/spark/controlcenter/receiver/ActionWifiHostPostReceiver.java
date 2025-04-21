package com.tapbi.spark.controlcenter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CallBackUpdateUi;

public class ActionWifiHostPostReceiver extends BroadcastReceiver {
  private CallBackUpdateUi callBackUpdateUi;
  private String valueRegister;


  public ActionWifiHostPostReceiver(CallBackUpdateUi callBackUpdateUi, String valueRegister) {
    this.callBackUpdateUi = callBackUpdateUi;
    this.valueRegister = valueRegister;
  }



  @Override
  public void onReceive(Context context, Intent intent) {
    if (intent.getAction().equals("android.net.wifi.WIFI_AP_STATE_CHANGED")) {
      int apState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
      if (apState == 13) {
        callBackUpdateUi.stage(valueRegister, true,0);
      } else {
        callBackUpdateUi.stage(valueRegister, false,0);
      }
    }
  }
}

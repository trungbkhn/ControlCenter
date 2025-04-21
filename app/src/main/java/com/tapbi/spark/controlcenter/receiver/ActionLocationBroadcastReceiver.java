package com.tapbi.spark.controlcenter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CallBackUpdateUi;

public class ActionLocationBroadcastReceiver extends BroadcastReceiver {
  private CallBackUpdateUi callBackUpdateUi;
  private String valueRegister;
  public LocationManager lm;

  public ActionLocationBroadcastReceiver(CallBackUpdateUi callBackUpdateUi, String valueRegister, Context context) {
    this.callBackUpdateUi = callBackUpdateUi;
    this.valueRegister = valueRegister;
    lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

  }

  @Override
  public void onReceive(Context context, Intent intent) {
    if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
      callBackUpdateUi.stage(valueRegister, (lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)),0);
    }

  }
}

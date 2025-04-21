package com.tapbi.spark.controlcenter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CallBackUpdateUi;

public class ActionAirplaneModeChange extends BroadcastReceiver {

  private CallBackUpdateUi closeNotyControl;
  private String valueRegister;

  public ActionAirplaneModeChange(CallBackUpdateUi closeNotyControl, String valueRegister) {
    this.valueRegister = valueRegister;
    this.closeNotyControl = closeNotyControl;
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    if (intent.getAction().equals(Intent.ACTION_AIRPLANE_MODE_CHANGED)) {
      closeNotyControl.stage(valueRegister, Settings.System.getInt(
          context.getContentResolver(),
          Settings.Global.AIRPLANE_MODE_ON, 0) == 1,0);
    }
  }

}

package com.tapbi.spark.controlcenter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CloseNotyControl;

public class ActionScreenOffReceiver extends BroadcastReceiver {

  private CloseNotyControl closeNotyControl;

  public ActionScreenOffReceiver(CloseNotyControl closeNotyControl) {
    this.closeNotyControl = closeNotyControl;
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
      closeNotyControl.screenOff();
    }
  }
}

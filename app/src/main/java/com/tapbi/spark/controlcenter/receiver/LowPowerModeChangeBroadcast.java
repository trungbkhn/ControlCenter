package com.tapbi.spark.controlcenter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tapbi.spark.controlcenter.utils.SettingUtils;

public class LowPowerModeChangeBroadcast extends BroadcastReceiver {
  private final IListenerLowPowerModeChange iListenerLowPowerModeChange;

  public LowPowerModeChangeBroadcast(IListenerLowPowerModeChange iListenerLowPowerModeChange) {
   this.iListenerLowPowerModeChange = iListenerLowPowerModeChange;
  }

  @Override
  public void onReceive(Context context, Intent intent) {
   iListenerLowPowerModeChange.lowPowerMode(SettingUtils.isPowerSaveMode(context));
  }

  public interface IListenerLowPowerModeChange {
    void lowPowerMode(boolean turnOn);
  }
}

package com.tapbi.spark.controlcenter.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CallBackUpdateUi;

public class BluetoothReceiver extends BroadcastReceiver {

  private CallBackUpdateUi callBackStageFlash;
  private String valueRegister;

  public BluetoothReceiver(CallBackUpdateUi callBackStageFlash, String valueRegister) {
    this.valueRegister = valueRegister;
    this.callBackStageFlash = callBackStageFlash;
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    String action = intent.getAction();
    if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
      callBackStageFlash.stage(valueRegister, intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_ON,0);
    }
  }

}

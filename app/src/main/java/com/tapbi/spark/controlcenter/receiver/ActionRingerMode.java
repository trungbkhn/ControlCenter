package com.tapbi.spark.controlcenter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CallBackUpdateSound;

public class ActionRingerMode  extends BroadcastReceiver {
  private CallBackUpdateSound callBackUpdateUi;
  private String valueRegister;
  public AudioManager audioManager;

  public ActionRingerMode(CallBackUpdateSound callBackUpdateUi, String valueRegister, Context context) {
    this.valueRegister = valueRegister;
    this.callBackUpdateUi = callBackUpdateUi;
    audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    callBackUpdateUi.updateSound(valueRegister, audioManager.getRingerMode());
  }
}

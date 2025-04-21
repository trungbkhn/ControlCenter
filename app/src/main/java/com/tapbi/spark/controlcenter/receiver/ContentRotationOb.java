package com.tapbi.spark.controlcenter.receiver;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;

import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CallBackUpdateUi;

public class ContentRotationOb extends ContentObserver {
  private CallBackUpdateUi callBackUpdateUi;
  private String valueRegister;
  private Context context;

  public ContentRotationOb(Handler handler, CallBackUpdateUi callBackUpdateUi, String valueRegister,  Context context) {
    super(handler);
    this.callBackUpdateUi = callBackUpdateUi;
    this.valueRegister = valueRegister;
    this.context = context;
  }

  @Override
  public void onChange(boolean selfChange) {
    super.onChange(selfChange);
    callBackUpdateUi.stage(valueRegister, android.provider.Settings.System.getInt(context.getContentResolver(),
        Settings.System.ACCELEROMETER_ROTATION, 0) == 1,0);
  }
}

package com.tapbi.spark.controlcenter.receiver;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;

import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CallBackUpdateUi;
import com.tapbi.spark.controlcenter.feature.controlcenter.utils.DataMobileUtils;

import timber.log.Timber;

public class ContentObDataMobile extends ContentObserver {
  private CallBackUpdateUi callBackUpdateUi;
  private String valueRegister;
  public DataMobileUtils dataMobileUtils;

  public ContentObDataMobile(Handler handler, CallBackUpdateUi callBackUpdateUi, String valueRegister, Context context) {
    super(handler);
    this.valueRegister = valueRegister;
    this.callBackUpdateUi = callBackUpdateUi;
    dataMobileUtils = new DataMobileUtils(context);
  }

  @Override
  public void onChange(boolean selfChange) {
    super.onChange(selfChange);
    Timber.e("data mobile: " + selfChange+" dataMobileUtils.isDataEnable(): "+dataMobileUtils.isDataEnable());
    callBackUpdateUi.stage(valueRegister, dataMobileUtils.isDataEnable(),0);
  }
}

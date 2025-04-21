package com.tapbi.spark.controlcenter.feature.controlcenter.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;

public class DataMobileUtils {
  private ConnectivityManager cm;
  private Class cmClass;
  private Method method;
  private TelephonyManager tm;

  public DataMobileUtils(Context context) {
    boolean mobileDataEnabled = false;
    cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    try {
      cmClass = Class.forName(cm.getClass().getName());
      method = cmClass.getDeclaredMethod("getMobileDataEnabled");
      method.setAccessible(true);
      tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public boolean isDataEnable() {
    try {
//      Timber.e("kq: " + tm.getSimState());
      if (tm.getSimState() == TelephonyManager.SIM_STATE_READY) {
        return (boolean) method.invoke(cm);
      }else {
        return false;
      }

    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
}

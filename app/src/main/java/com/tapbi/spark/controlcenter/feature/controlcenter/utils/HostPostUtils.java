package com.tapbi.spark.controlcenter.feature.controlcenter.utils;

import android.content.Context;
import android.net.wifi.WifiManager;

import java.lang.reflect.InvocationTargetException;

public class HostPostUtils {



    private WifiManager wifiManager;

    public HostPostUtils(Context context) {
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }


    public boolean getStateWifi() {
        int value;
        boolean isEnable = false;
        try {
            value = (Integer) wifiManager.getClass().getMethod("getWifiApState").invoke(wifiManager);
            if (value == 13) {
                isEnable = true;
            } else {
                isEnable = false;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return isEnable;
    }

}

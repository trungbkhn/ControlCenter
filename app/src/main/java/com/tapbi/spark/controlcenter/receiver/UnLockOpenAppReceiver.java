package com.tapbi.spark.controlcenter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tapbi.spark.controlcenter.common.Constant;

public class UnLockOpenAppReceiver extends BroadcastReceiver {
    public IClickOpenApp iClickOpenApp;

    public UnLockOpenAppReceiver(IClickOpenApp iClickOpenApp) {
        this.iClickOpenApp = iClickOpenApp;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        if (action.equals(Constant.ACTION_OPEN_APP)) {
            String pka = intent.getStringExtra(Constant.PACKAGE_NAME_APP_OPEN);
            String idEvent = "";
            if (pka != null) {
                if (pka.equals(Constant.OPEN_EVENT_NEXT_UP)) {
                    idEvent = intent.getStringExtra(Constant.ID_EVENT_NEXT_UP);
                }
                iClickOpenApp.openAppWhenUnlock(pka, idEvent);
            }


        }
    }

    public interface IClickOpenApp {
        void openAppWhenUnlock(String pka, String idEvent);
    }
}
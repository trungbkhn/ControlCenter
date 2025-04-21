package com.tapbi.spark.controlcenter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.service.NotificationListener;

public class CallReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(intent.getAction())
                && intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)
        ) {
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Intent intentAutoService = new Intent(context, NotificationListener.class);
            intentAutoService.putExtra(Constant.INCOMING_CALL, incomingNumber);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                context.startForegroundService(intentAutoService);
//            } else {
//                context.startService(intentAutoService);
//            }
            context.startService(intentAutoService);
        }
    }
}

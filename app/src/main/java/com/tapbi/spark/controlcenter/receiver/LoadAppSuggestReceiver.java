package com.tapbi.spark.controlcenter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tapbi.spark.controlcenter.feature.controlios14.manager.SuggestAppManager;

public class LoadAppSuggestReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SuggestAppManager.getInstance().loadSuggestAppAndAlarm(context);
    }
}

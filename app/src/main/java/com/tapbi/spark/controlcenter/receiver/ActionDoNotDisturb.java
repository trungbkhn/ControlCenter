package com.tapbi.spark.controlcenter.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CallBackUpdateUi;
import com.tapbi.spark.controlcenter.feature.controlios14.manager.AudioManagerUtils;

import timber.log.Timber;

public class ActionDoNotDisturb extends BroadcastReceiver {
    private CallBackUpdateUi callBackUpdateUi;
    private String valueRegister;
    private Context context;


    public ActionDoNotDisturb(CallBackUpdateUi callBackUpdateUi, String valueRegister, Context context) {
        this.callBackUpdateUi = callBackUpdateUi;
        this.context = context;
        this.valueRegister = valueRegister;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null) {
            if (intent.getAction().equals(NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED) && !AudioManagerUtils.isChangingRingerMode) {
                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int currentFilter = mNotificationManager.getCurrentInterruptionFilter();
                    if (currentFilter == NotificationManager.INTERRUPTION_FILTER_ALL) {
                        callBackUpdateUi.stage(valueRegister, false, 0); // DND tắt
                    } else if (currentFilter == NotificationManager.INTERRUPTION_FILTER_PRIORITY) {
                        callBackUpdateUi.stage(valueRegister, true, 0); // DND bật
                    }
                }
            }
        }

    }


}

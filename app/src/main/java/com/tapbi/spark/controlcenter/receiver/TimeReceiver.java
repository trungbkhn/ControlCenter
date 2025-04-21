package com.tapbi.spark.controlcenter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614;

public class TimeReceiver extends BroadcastReceiver {


    private ITimeChange iTimeChange;


    public TimeReceiver(ITimeChange iTimeChange) {
        this.iTimeChange = iTimeChange;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            if (action.equals(Intent.ACTION_TIME_CHANGED)) {
                if (NotyControlCenterServicev614.getInstance() != null) {
                    NotyControlCenterServicev614.getInstance().setLastClick(0);
                }
            }

            if (action.equals(Intent.ACTION_TIME_CHANGED) || action.equals(Intent.ACTION_TIME_TICK)) {
                if (App.ins.focusUtils != null) {
                    App.ins.focusUtils.sendActionFocus(Constant.TIME_CHANGE, "");

                }
            }
            iTimeChange.timeChange();

        }

    }


    public interface ITimeChange {
        void timeChange();
    }

}

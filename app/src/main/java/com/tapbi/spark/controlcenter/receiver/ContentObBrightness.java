package com.tapbi.spark.controlcenter.receiver;

import android.database.ContentObserver;
import android.os.Handler;

public class ContentObBrightness extends ContentObserver {
    private CallBackUpdateUiBrightness callBackUpdateUiBrightness;

    public ContentObBrightness(Handler handler, CallBackUpdateUiBrightness callBackUpdateUiBrightness) {
        super(handler);
        this.callBackUpdateUiBrightness = callBackUpdateUiBrightness;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        callBackUpdateUiBrightness.updateUiBrightness();
    }

    public interface CallBackUpdateUiBrightness {
        void updateUiBrightness();
    }
}

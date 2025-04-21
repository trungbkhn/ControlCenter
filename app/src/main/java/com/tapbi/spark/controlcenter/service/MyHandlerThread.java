package com.tapbi.spark.controlcenter.service;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

class MyHandlerThread extends HandlerThread {
   private Handler handler;

    public MyHandlerThread(String name) {
        super(name);
    }

    @Override
    protected void onLooperPrepared() {
        // Only instantiates the Handler when looper is prepared
        // So, Handler can be associated with that Looper
        handler = new Handler(getLooper()) {
            @Override
            public void handleMessage(Message msg) {

                // process incoming messages here
                // this will run in non-ui/background thread
            }
        };
    }
}

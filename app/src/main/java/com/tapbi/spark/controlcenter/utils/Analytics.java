package com.tapbi.spark.controlcenter.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class Analytics {
    private static Analytics instance;
    private static FirebaseAnalytics firebaseAnalytics;

    public static void init(Context context) {
        if (instance == null) {
            instance = new Analytics();
            firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        }
    }

    public static Analytics getInstance() {
        return instance;
    }

    public void setUserProperty(String key, String value) {
        firebaseAnalytics.setUserProperty(key, value);
    }

    public void logEvent(String key, Bundle bundle) {
        firebaseAnalytics.logEvent(key, bundle);
    }

    public void setCurrentScreen(Activity context, String nameScreen) {
        if (firebaseAnalytics != null){
            firebaseAnalytics.setCurrentScreen(context, nameScreen, null);
        }
    }
}

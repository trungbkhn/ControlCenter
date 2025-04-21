package com.tapbi.spark.controlcenter.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;

import java.util.HashMap;

public class ActivityCallBack implements Application.ActivityLifecycleCallbacks {
    public HashMap<String, Activity> mActivities = new HashMap<>();
    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
//        Timber.e("onActivityCreated" + activity.getLocalClassName());
        if (!mActivities.containsKey(activity.getLocalClassName())) {
            mActivities.put(activity.getLocalClassName(), activity);
        }
    }
    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        if (!mActivities.containsKey(activity.getLocalClassName())) {
            mActivities.put(activity.getLocalClassName(), activity);
        }
//        Timber.e("onActivityStarted" + activity.getLocalClassName());
    }
    @Override
    public void onActivityResumed(@NonNull Activity activity) {
//        Timber.e("onActivityResumed" + activity.getLocalClassName());
        processHideSoftInputOnActivityDestroy(activity, false);
    }
    @Override
    public void onActivityPaused(@NonNull Activity activity) {
//        Timber.e("onActivityPaused" + activity.getLocalClassName());
    }
    @Override
    public void onActivityStopped(@NonNull Activity activity) {
//        Timber.e("onActivityStopped "+activity.getLocalClassName());
        processHideSoftInputOnActivityDestroy(activity, true);
        mActivities.remove(activity.getLocalClassName());
    }
    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {
    }
    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
//        Timber.e("onActivityDestroyed "+activity.getLocalClassName());
    }
    public Activity getCurrentActivityVisible () {
        if (!mActivities.isEmpty()) {
            for (String key : mActivities.keySet()) {
                if (mActivities.get(key) != null && mActivities.get(key) instanceof AppCompatActivity) {
                    if (((AppCompatActivity) mActivities.get(key)).getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
                        return mActivities.get(key);
                    }
                }
            }
        }
        return null;
    }
    /**
     * To solve close keyboard when activity onDestroy.
     * The preActivity set windowSoftInputMode will prevent
     * the keyboard from closing when curActivity onDestroy.
     */
    private void processHideSoftInputOnActivityDestroy(final Activity activity, boolean isSave) {
        try {
            if (isSave) {
                Window window = activity.getWindow();
                final WindowManager.LayoutParams attrs = window.getAttributes();
                final int softInputMode = attrs.softInputMode;
                window.getDecorView().setTag(-123, softInputMode);
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            } else {
                final Object tag = activity.getWindow().getDecorView().getTag(-123);
                if (!(tag instanceof Integer)) return;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Window window = activity.getWindow();
                            if (window != null) {
                                window.setSoftInputMode(((Integer) tag));
                            }
                        } catch (Exception ignore) {
                        }
                    }
                },100);
            }
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }
}

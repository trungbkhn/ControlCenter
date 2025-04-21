package com.tapbi.spark.controlcenter.utils;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.tapbi.spark.controlcenter.App;


public class VibratorUtils {
    private static VibratorUtils instance;
    private static Vibrator vibrator;
    private static Context context;
    private static TinyDB tinyDB;

    public static long TIME_DEFAULT = 80;

    public static VibratorUtils getInstance(Context ctx) {
        if (instance == null) {
            context = ctx;
            instance = new VibratorUtils();
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            tinyDB = App.tinyDB;
        }
        return instance;
    }

    public void vibrator(long t) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(t, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void vibrator() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(TIME_DEFAULT, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(TIME_DEFAULT);
        }
    }
}

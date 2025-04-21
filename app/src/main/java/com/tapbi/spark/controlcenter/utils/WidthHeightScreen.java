package com.tapbi.spark.controlcenter.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.NonNull;

public class WidthHeightScreen {
    public int w, h;

    public WidthHeightScreen(Context context) {
        WindowManager WindowManager =  (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = WindowManager.getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);

        int orientation =  DensityUtils.getOrientationWindowManager(context);
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (size.y > size.x) {
                setWidthHeight(size.x, size.y);
            } else {
                setWidthHeight(size.y, size.x);
            }
        } else {
            if (size.y > size.x) {
                setWidthHeight(size.y, size.x);
            } else {
                setWidthHeight(size.x, size.y);
            }
        }
    }

    private void setWidthHeight(int width, int height){
        w = width;
        h = height;
    }

    @NonNull
    @Override
    public String toString() {
        return "w " + w + ", h " + h;
    }

    public int getSize(){
        return w * h;
    }
}

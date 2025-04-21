package com.tapbi.spark.controlcenter.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;

public class DrawableUtils {

    public static Drawable getIconDefaultApp(String name, Context context) {
        if (name == null) {
            return null;
        }
        switch (name) {
            case Constant.CUSTOM:
                return ContextCompat.getDrawable(context, R.drawable.ic_custom_focus);
            case Constant.SLEEP:
                return ContextCompat.getDrawable(context, R.drawable.ic_sleep);
            case Constant.DO_NOT_DISTURB:
                return ContextCompat.getDrawable(context, R.drawable.ic_silent_ios);
            case Constant.READING:
                return ContextCompat.getDrawable(context, R.drawable.ic_reading);
            case Constant.MINDFULNESS:
                return ContextCompat.getDrawable(context, R.drawable.ic_mindfulness);
            case Constant.WORK:
                return ContextCompat.getDrawable(context, R.drawable.ic_work);
            case Constant.GAMING:
                return ContextCompat.getDrawable(context, R.drawable.ic_gaming);
            case Constant.DRIVING:
                return ContextCompat.getDrawable(context, R.drawable.ic_driving);
            case Constant.PERSONAL:
                return ContextCompat.getDrawable(context, R.drawable.ic_personal);
        }
        return null;
    }


}

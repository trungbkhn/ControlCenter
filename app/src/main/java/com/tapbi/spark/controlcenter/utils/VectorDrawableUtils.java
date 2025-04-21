package com.tapbi.spark.controlcenter.utils;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

public class VectorDrawableUtils {

  Drawable getDrawable(Context context, int drawableResId) {
    return VectorDrawableCompat.create(context.getResources(), drawableResId, context.getTheme());
  }

  public Drawable getDrawable(Context context, int drawableResId, int colorFilter) {
    Drawable drawable = getDrawable(context, drawableResId);
    drawable.setColorFilter(ContextCompat.getColor(context, colorFilter), PorterDuff.Mode.SRC_IN);
    return drawable;
  }

}

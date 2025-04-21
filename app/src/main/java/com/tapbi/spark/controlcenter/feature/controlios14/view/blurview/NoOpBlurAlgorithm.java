package com.tapbi.spark.controlcenter.feature.controlios14.view.blurview;

import android.graphics.Bitmap;
import androidx.annotation.NonNull;

/**
 * Created by DoThanhTrang on 1/23/2019.
 */

public class NoOpBlurAlgorithm implements BlurAlgorithm {
    @Override
    public Bitmap blur(Bitmap bitmap, float blurRadius) {
        return bitmap;
    }

    @Override
    public void destroy() {
    }

    @Override
    public boolean canModifyBitmap() {
        return true;
    }

    @NonNull
    @Override
    public Bitmap.Config getSupportedBitmapConfig() {
        return Bitmap.Config.ARGB_8888;
    }
}

package com.tapbi.spark.controlcenter.feature.controlios14.view.blurview;

import android.graphics.Bitmap;
import androidx.annotation.NonNull;

/**
 * Created by DoThanhTrang on 1/23/2019.
 */

public interface BlurAlgorithm {
    Bitmap blur(Bitmap bitmap, float blurRadius);
    void destroy();
    boolean canModifyBitmap();
    @NonNull
    Bitmap.Config getSupportedBitmapConfig();
}

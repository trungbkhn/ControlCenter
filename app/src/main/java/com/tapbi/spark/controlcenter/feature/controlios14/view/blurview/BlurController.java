package com.tapbi.spark.controlcenter.feature.controlios14.view.blurview;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

/**
 * Created by DoThanhTrang on 1/23/2019.
 */

public interface BlurController {
    float DEFAULT_SCALE_FACTOR = 16f;
    float DEFAULT_BLUR_RADIUS = 16f;

    void drawBlurredContent(Canvas canvas);

    void updateBlurViewSize();

    void onDrawEnd(Canvas canvas);

    void setBlurRadius(float radius);

    void setBlurAlgorithm(BlurAlgorithm algorithm);

    void setFrameClearDrawable(Drawable frameClearDrawable);

    void destroy();

    void setBlurEnabled(boolean enabled);

    void setBlurAutoUpdate(boolean enabled);

    void setHasFixedTransformationMatrix(boolean hasFixedTransformationMatrix);

    void drawNotUpdate(Canvas canvas);
}

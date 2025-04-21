package com.tapbi.spark.controlcenter.feature.controlios14.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by DoThanhTrang on 12/28/2018.
 */

@SuppressLint("AppCompatCustomView")
public class ImageCustom extends ImageView {

    private float radius = 24.0f;

    public ImageCustom(Context context) {
        super(context);
    }

    public ImageCustom(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageCustom(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Path clipPath = new Path();
        RectF rectF = new RectF(0, 0, this.getWidth(), this.getHeight());
        clipPath.addRoundRect(rectF, radius, radius, Path.Direction.CW);
        canvas.clipPath(clipPath);
        super.onDraw(canvas);

    }
}

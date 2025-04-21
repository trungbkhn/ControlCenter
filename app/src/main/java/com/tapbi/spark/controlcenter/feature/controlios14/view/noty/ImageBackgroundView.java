package com.tapbi.spark.controlcenter.feature.controlios14.view.noty;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import androidx.annotation.Nullable;

import android.util.AttributeSet;
import android.view.View;

public class ImageBackgroundView extends View {

    private Bitmap bitmap;

    public ImageBackgroundView(Context context) {
        super(context);
    }

    public ImageBackgroundView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageBackgroundView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setBitmap(Bitmap bitmap) {
        if (bitmap != null) {
//            recyclerBitmap();
            this.bitmap = bitmap;
//            this.bitmap = Bitmap.createScaledBitmap(bitmap, getWidth(), getWidth() * bitmap.getHeight() / bitmap.getWidth(), true);
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0, 0, null);
        }
        super.onDraw(canvas);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        recyclerBitmap();
    }

    private void recyclerBitmap() {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }
}

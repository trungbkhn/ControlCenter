package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ImageBase;

import timber.log.Timber;


public class MusicControlView extends ImageBase {

    private OnAnimationListener onAnimationListener;
    private Paint paint;
    private Path path;
    private Float cornerBackground = 0f;

    private Bitmap bitmapSrc;

    public MusicControlView(Context context) {
        super(context);
        init();
    }


    public MusicControlView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MusicControlView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setOnAnimationListener(OnAnimationListener onAnimationListener) {
        this.onAnimationListener = onAnimationListener;
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        path = new Path();
    }

    public void setCornerBackground(Float cornerBackground) {
        this.cornerBackground = cornerBackground;
        updatePath(); // Update path when corner background changes
        invalidate();
    }

    public void setSrc(Bitmap bitmap) {
        this.bitmapSrc = bitmap;
        invalidate();  // Redraw the view when bitmap changes
    }

    private void updatePath() {
        if (cornerBackground > 0) {
            path.reset();
            float corner = cornerBackground * getHeight();
            path.addRoundRect(0, 0, getWidth(), getHeight(), corner, corner, Path.Direction.CW);
            path.close();
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updatePath();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (bitmapSrc != null) {
            canvas.save();
            canvas.clipPath(path);

            canvas.drawBitmap(bitmapSrc, null, new Rect(0, 0, getWidth(), getHeight()), paint);

            canvas.restore();
        }
    }

    @Override
    protected void click() {
        if (onAnimationListener != null) {
            onAnimationListener.onClick();
        }
    }

    @Override
    protected void longClick() {
        if (onAnimationListener != null) {
            onAnimationListener.onLongClick();
        }
    }

    @Override
    protected void onDown() {
        if (onAnimationListener != null) {
            onAnimationListener.onDown();
        }
    }

    @Override
    protected void onUp() {
        if (onAnimationListener != null) {
            onAnimationListener.onUp();
        }
    }
}

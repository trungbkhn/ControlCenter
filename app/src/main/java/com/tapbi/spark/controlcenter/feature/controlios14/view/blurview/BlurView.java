package com.tapbi.spark.controlcenter.feature.controlios14.view.blurview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.tapbi.spark.controlcenter.R;

/**
 * Created by DoThanhTrang on 1/23/2019.
 */

public class BlurView extends FrameLayout {

    private static final String TAG = BlurView.class.getSimpleName();
    @ColorInt
    private static final int TRANSPARENT = 0x00000000;

    BlurController blurController = createStubController();

    private boolean isUpdate;

    public void setUpdate(boolean update) {
        if (blockingBlurController != null) {
            isUpdate = update;
            blockingBlurController.saveBitmap();
            invalidate();
        }
    }

    @ColorInt
    private int overlayColor;

    public BlurView(Context context) {
        super(context);
        init(null, 0);
    }

    public BlurView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public BlurView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        isUpdate = true;
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BlurView, defStyleAttr, 0);
        overlayColor = a.getColor(R.styleable.BlurView_blurOverlayColor, Color.BLACK);
        a.recycle();

        setWillNotDraw(false);
    }

    @Override
    public void draw(Canvas canvas) {
        if (canvas.isHardwareAccelerated()) {
            if (isUpdate) {
                blurController.drawBlurredContent(canvas);
                if (overlayColor != TRANSPARENT) {
                    canvas.drawColor(overlayColor);
                }
            } else {
                blurController.drawNotUpdate(canvas);
                if (overlayColor != TRANSPARENT) {
                    canvas.drawColor(overlayColor);
                }
            }
            super.draw(canvas);
        } else if (!isHardwareAccelerated()) {
            super.draw(canvas);
        }
    }

    public BlurView setBlurAutoUpdate(final boolean enabled) {
        post(new Runnable() {
            @Override
            public void run() {
                blurController.setBlurAutoUpdate(enabled);
            }
        });
        return this;
    }

    public BlurView setBlurEnabled(final boolean enabled) {
        post(new Runnable() {
            @Override
            public void run() {
                blurController.setBlurEnabled(enabled);
            }
        });
        return this;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        blurController.updateBlurViewSize();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        blurController.onDrawEnd(canvas);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        blurController.setBlurAutoUpdate(false);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isHardwareAccelerated()) {
            Log.e(TAG, "BlurView can't be used in not hardware-accelerated window!");
        } else {
            blurController.setBlurAutoUpdate(true);
        }
    }

    private void setBlurController(@NonNull BlurController blurController) {
        this.blurController.destroy();
        this.blurController = blurController;
    }


    public BlurView setOverlayColor(@ColorInt int overlayColor) {
        if (overlayColor != this.overlayColor) {
            this.overlayColor = overlayColor;
            invalidate();
        }
        return this;
    }


    public BlurView setHasFixedTransformationMatrix(boolean hasFixedTransformationMatrix) {
        blurController.setHasFixedTransformationMatrix(hasFixedTransformationMatrix);
        return this;
    }

    public BlockingBlurController blockingBlurController;

    public BlurView setupWith(@NonNull ViewGroup rootView) {
        BlurController blurController = new BlockingBlurController(this, rootView);
        blockingBlurController = (BlockingBlurController) blurController;
        setBlurController(blurController);

        if (!isHardwareAccelerated()) {
            blurController.setBlurAutoUpdate(false);
        }

        return this;
    }

    public BlurView setBlurRadius(float radius) {
        blurController.setBlurRadius(radius);
        return this;
    }

    public BlurView setBlurAlgorithm(BlurAlgorithm algorithm) {
        blurController.setBlurAlgorithm(algorithm);
        return this;
    }

    public BlurView setFrameClearDrawable(@Nullable Drawable frameClearDrawable) {
        blurController.setFrameClearDrawable(frameClearDrawable);
        return this;
    }

    private BlurController createStubController() {
        return new BlurController() {
            @Override
            public void drawBlurredContent(Canvas canvas) {
            }

            @Override
            public void updateBlurViewSize() {
            }

            @Override
            public void onDrawEnd(Canvas canvas) {
            }

            @Override
            public void setBlurRadius(float radius) {
            }

            @Override
            public void setBlurAlgorithm(BlurAlgorithm algorithm) {
            }

            @Override
            public void setFrameClearDrawable(@Nullable Drawable windowBackground) {
            }

            @Override
            public void destroy() {
            }

            @Override
            public void setBlurEnabled(boolean enabled) {
            }

            @Override
            public void setBlurAutoUpdate(boolean enabled) {
            }

            @Override
            public void setHasFixedTransformationMatrix(boolean hasFixedTransformationMatrix) {
            }

            @Override
            public void drawNotUpdate(Canvas canvas) {

            }

        };
    }
}

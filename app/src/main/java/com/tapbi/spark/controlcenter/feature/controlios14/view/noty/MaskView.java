package com.tapbi.spark.controlcenter.feature.controlios14.view.noty;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.tapbi.spark.controlcenter.feature.controlios14.helper.BlurBackground;
import com.tapbi.spark.controlcenter.utils.MethodUtils;


public class MaskView extends ConstraintLayout {

    private Context context;

    private ImageBackgroundView viewBackground;
    private Path path;
    private NotyCenterViewOS viewGroup;

    private int height = 0;
    private int heightGetBitmap = 0;
    private int margin = 0;
    private int radius = 0;
    private ValueAnimator valueAnimator;
//    private Bitmap bitmapBlur;

    public MaskView(Context context) {
        super(context);
        init(context);
    }

    public MaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context ctx) {
        this.context = ctx;
        path = new Path();
        margin = MethodUtils.dp2px(context, 10);
        radius = MethodUtils.dp2px(context, 10);
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        canvas.clipPath(path);
        super.dispatchDraw(canvas);
    }

    public void setViewGroup(NotyCenterViewOS viewGroup) {
        this.viewGroup = viewGroup;
    }

    public void setTranslationX(int x) {
        if (viewBackground != null) {
            viewBackground.setTranslationX(x);
        }

        if (height == 0) {
            getBitmapBlur();
            updateClipPath();
        }
    }

    public void updateClipPath() {
        height = getHeight();
        path = new Path();
        path.addRoundRect(new RectF(
                        margin,
                        0,
                        getWidth() - margin,
                        height),
                radius, radius,
                Path.Direction.CW);
        invalidate();
    }

    public void updateCLipPath(int right) {
        path = new Path();
        path.addRoundRect(new RectF(
                        margin,
                        0,
                        right,
                        height),
                radius, radius,
                Path.Direction.CW);
        invalidate();
    }

    public void setRadius(int dp) {
        this.radius = MethodUtils.dp2px(context, dp);
        invalidate();
    }

    protected void animationMoreLess(final View view, float from, float to) {
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        valueAnimator = ValueAnimator.ofFloat(from, to);
        valueAnimator.setDuration(300);
        valueAnimator.setInterpolator(new DecelerateInterpolator());

        final int width = getWidth();

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (float) animation.getAnimatedValue();
                path = new Path();
                path.addRoundRect(new RectF(
                                margin,
                                0,
                                width - margin,
                                f),
                        margin, margin,
                        Path.Direction.CW);
                invalidate();
                view.getLayoutParams().height = (int) f;
                view.requestLayout();
            }
        });
        valueAnimator.start();
    }

    protected void setViewBackground(ImageBackgroundView viewBackground) {
        this.viewBackground = viewBackground;
    }

    protected void getBitmapBlur() {
        if (viewGroup != null && getVisibility() == VISIBLE) {
            if (heightGetBitmap == 0) {
                heightGetBitmap = getHeight();
            }

            if (getWidth() <= 0) return;
            try {
//                resetBitmapBlur();
//                bitmapBlur = DensityUtils.scaleBitmap(BlurBackground.getInstance().createBitmapBlur(0, getTop(), getWidth(), getHeight()));
//                viewBackground.setBitmap(bitmapBlur);
                //viewBackground.setBitmap(BlurBackground.getInstance().createBitmapBlur(0, getTop(), getWidth(), getHeight()));

                Bitmap originalBitmap = BlurBackground.getInstance().createBitmapBlur(0, getTop(), getWidth(), getHeight());
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, getWidth() / 2, heightGetBitmap / 2, true);
                viewBackground.setBitmap(scaledBitmap);
            } catch (Exception e) {
                //Bitmap.createScaledBitmap(BlurBackground.getInstance().getBitmapTransparent(), BlurBackground.getInstance().getBitmapTransparent().getWidth(), getWidth() * BlurBackground.getInstance().getBitmapTransparent().getHeight() / BlurBackground.getInstance().getBitmapTransparent().getWidth(), false);
//                viewBackground.setBitmap(BlurBackground.getInstance().getBitmapTransparent());
            }
        }
    }

    private void resetBitmapBlur() {
//        if (bitmapBlur != null && !bitmapBlur.isRecycled()) {
//            bitmapBlur.recycle();
//            bitmapBlur = null;
//        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
//        resetBitmapBlur();
    }
}

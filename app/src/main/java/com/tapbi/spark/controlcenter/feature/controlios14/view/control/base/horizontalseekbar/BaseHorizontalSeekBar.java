package com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.horizontalseekbar;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewPropertyAnimator;

import android.view.GestureDetector.SimpleOnGestureListener;

import androidx.annotation.NonNull;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.BaseView;
import com.tapbi.spark.controlcenter.utils.DensityUtils;
import com.tapbi.spark.controlcenter.utils.Utils;

import timber.log.Timber;

public class BaseHorizontalSeekBar extends BaseView {
    private static final int MAX_CLICK_DISTANCE = 15;
    float progress;
    boolean isEnabled = true;
    private Drawable thumbDrawable;
    private String colorThumb = null;
    private int thumbOffset;
    private GestureDetector gestureDetector;
    private int keyProgressIncrement = 1;
    private float clickAnimationValue;
    private long pressStartTime;
    private float pressedX;
    private float pressedY;
    private boolean longPress = false;
    private ViewPropertyAnimator scaleAnimator = null;
    private boolean down;
    private float positionX = 0;

    public BaseHorizontalSeekBar(Context context) {
        super(context);
    }

    public BaseHorizontalSeekBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public BaseHorizontalSeekBar(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        this.gestureDetector = new GestureDetector(context, new GestureListener(this));

        int[] seekBarAttrs = new int[]{16843074, 16843075, R.attr.ClickAnimationValue};
        TypedArray a = context.obtainStyledAttributes(attributeSet, seekBarAttrs, defStyle, 0);
        setThumbOffset(a.getDimensionPixelOffset(1, getThumbOffset()));
        a.recycle();


        int[] themeAttrs = new int[]{16842803};
        TypedArray b = context.obtainStyledAttributes(attributeSet, themeAttrs, 0, 0);
        this.clickAnimationValue = b.getFloat(0, 0.5f);
        b.recycle();
    }

    private static float distance(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        float distanceInPx = (float) Math.sqrt(dx * dx + dy * dy);
        return pxToDp(distanceInPx);
    }

    private static float pxToDp(float px) {
        return px / Resources.getSystem().getDisplayMetrics().density;
    }

    private void updateThumb(Drawable drawable) {
        int thumbWidth = (int) (getHeight() + DensityUtils.pxFromDp(getContext(), 1));
        int thumbHeight = getHeight();
        int topBound = (this.getHeight() - this.getPaddingTop() - this.getPaddingBottom() - thumbHeight) / 2;
        int bottomBound = (int) (topBound + thumbHeight );
        Timber.e("Duongcv " + thumbWidth +":"+bottomBound);
        drawable.setBounds(0, 0, thumbWidth, bottomBound);
    }

    private void updateProgress(MotionEvent event) {

        int availableWidth = (this.getWidth() - this.getPaddingLeft()) - this.getPaddingRight();
        int x = (int) event.getX();
        float scale = 0.0f;

        if (x < this.getPaddingLeft()) {
            scale = 0.0f;
        } else if (x > this.getWidth() - this.getPaddingRight()) {
            scale = 1.0f;
        } else {
            scale = (float) (x - this.getPaddingLeft()) / (float) availableWidth;
        }

        float max = getMax();
        m4865a((int) (scale * max + 0.5f), true);
//        updateThumbPosition(getWidth(), thumbDrawable, getProgress()/Float.valueOf(getMax()), thumbOffset);
    }

    protected void onProgressChanged(float progress, boolean fromUser) {
        Drawable drawable = this.thumbDrawable;
        if (drawable != null) {
            updateThumb( drawable);
            invalidate();
        }
    }

    protected void onStartTrackingTouch() {

    }

    protected void onStopTrackingTouch() {

    }

    protected void onKeyChange() {
    }

    protected void onKeyRelease() {
    }

    @Override
    public void drawableStateChanged() {
        super.drawableStateChanged();
        Drawable progressDrawable = getProgressDrawable();
        if (progressDrawable != null) {
            progressDrawable.setAlpha(isEnabled() ? 255 : (int) (this.clickAnimationValue * 255.0f));
        }
        if (this.thumbDrawable != null && this.thumbDrawable.isStateful()) {
            this.thumbDrawable.setState(getDrawableState());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Implement key handling if necessary
        return super.onKeyDown(keyCode, event);
    }

    public int getKeyProgressIncrement() {
        return this.keyProgressIncrement;
    }

    public void setKeyProgressIncrement(int increment) {
        if (increment < 0) {
            increment = -increment;
        }
        this.keyProgressIncrement = increment;
    }

    public int getThumbOffset() {
        return this.thumbOffset;
    }

    public void setThumbOffset(int offset) {
        this.thumbOffset = offset;
        invalidate();
    }



    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    @Override
    protected void drawThumb(Canvas canvas) {
        super.drawThumb(canvas);
        if (this.thumbDrawable != null) {
            canvas.save();
            float position = (ratioProgress * (getWidth())) - getHeight()/2f;
            position = Math.max(position, 0);
            position = Math.min(position, getWidth() - getHeight() - DensityUtils.pxFromDp(getContext(), 1));
            canvas.translate(position , this.getPaddingTop());
            this.thumbDrawable.draw(canvas);
            canvas.restore();
        }
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int thumbWidth = this.thumbDrawable == null ? 0 : this.thumbDrawable.getIntrinsicWidth();
        Drawable currentDrawable = getCurrentDrawable();
        int width = 0;
        if (currentDrawable != null) {
            width = Math.max(thumbWidth, currentDrawable.getIntrinsicWidth());
        }
        int height = 0;
        if (currentDrawable != null) {
            height = currentDrawable.getIntrinsicHeight();
        }
        setMeasuredDimension(resolveSize(width + getPaddingLeft() + getPaddingRight(), widthMeasureSpec),
                resolveSize(height + getPaddingTop() + getPaddingBottom(), heightMeasureSpec));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Drawable currentDrawable = getCurrentDrawable();
        Drawable drawable = this.thumbDrawable;
        int thumbWidth = drawable == null ? 0 : drawable.getIntrinsicWidth();
        int maxAvailable = getMax();

        float scale = maxAvailable > 0 ? ((float) getProgress()) / (float) maxAvailable : 0;
        if (thumbWidth > w) {
            int offset = (thumbWidth - w) / 2;
            if (drawable != null) {
                updateThumb( drawable);
            }
            if (currentDrawable != null) {
                currentDrawable.setBounds(offset, 0, w - offset, h);
            }
        } else {
            if (currentDrawable != null) {
                currentDrawable.setBounds(0, 0, w, h);
                Timber.e("Duongcv " + w+":"+h);
            }
            if (drawable != null) {
                updateThumb(drawable);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.isEnabled) {
            if (isEnabled()) {
                if (this.gestureDetector != null) {
                    this.gestureDetector.onTouchEvent(event);
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        setPressed(true);
                        onStartTrackingTouch();

                        pressStartTime = System.currentTimeMillis();
                        pressedX = event.getX();
                        pressedY = event.getY();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        longPress = false;
                        onStopTrackingTouch();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!longPress && distance(pressedX, pressedY, event.getX(), event.getY()) > MAX_CLICK_DISTANCE) {
                            updateProgress(event);
                            if (this.getParent() != null) {
                                this.getParent().requestDisallowInterceptTouchEvent(true);
                            }
                        }
                        break;
                    default:
                        break;
                }
                onKeyChange();
                setPressed(false);
                invalidate();
                return true;
            }
        }
        return false;
    }

    public synchronized void setMax(int max) {
        super.setMax(max);
        if (this.keyProgressIncrement == 0 || getMax() / this.keyProgressIncrement > 20) {
            setKeyProgressIncrement(Math.max(1, Math.round((float) getMax() / 20.0f)));
        }
    }



    public void setThumb(Drawable drawable, String colorThumb) {
        this.colorThumb = colorThumb;
        if (drawable != null) {
            drawable.setCallback(this);
            this.thumbOffset = drawable.getIntrinsicWidth() / 2;
        }
        if (colorThumb != null){
            drawable.setTint(Color.parseColor(colorThumb));
        }
        this.thumbDrawable = drawable;
        updateThumb(this.thumbDrawable);
        invalidate();
    }

    protected void mo2403c() {
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who == this.thumbDrawable || super.verifyDrawable(who);
    }

    final class GestureListener extends SimpleOnGestureListener {
        final BaseHorizontalSeekBar seekBar;

        private GestureListener(BaseHorizontalSeekBar seekBar) {
            this.seekBar = seekBar;
        }


        @Override
        public void onLongPress(@NonNull MotionEvent e) {
            super.onLongPress(e);
            longPress=true;
            seekBar.mo2403c();
        }
    }
}

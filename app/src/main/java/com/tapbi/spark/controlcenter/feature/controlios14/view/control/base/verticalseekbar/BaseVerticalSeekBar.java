package com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.verticalseekbar;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ViewPropertyAnimator;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.BaseView;

import timber.log.Timber;

public class BaseVerticalSeekBar extends BaseView {
    private static final int MAX_CLICK_DISTANCE = 15;
    float f5959a;
    boolean f5960b = true;
    private Drawable f5962n;
    private int f5963o;
    private GestureDetector f5964p;
    private int f5965q = 1;
    private float f5966r;
    private long pressStartTime;
    private float pressedX;
    private float pressedY;


    private boolean longPress = false;
    private ViewPropertyAnimator scale = null;
    private boolean down;

    public BaseVerticalSeekBar(Context context) {
        super(context);
    }

    public BaseVerticalSeekBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public BaseVerticalSeekBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.f5964p = new GestureDetector(context, new C0027a(this));

        int[] SeekBar = new int[]{16843074, 16843075, R.attr.ClickAnimationValue};
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, SeekBar, i, 0);
        setThumbOffset(obtainStyledAttributes.getDimensionPixelOffset((int) 1, getThumbOffset()));
        obtainStyledAttributes.recycle();
        int[] theme = new int[]{16842803};
        TypedArray obtainStyledAttributes2 = context.obtainStyledAttributes(attributeSet, theme, 0, 0);
        this.f5966r = obtainStyledAttributes2.getFloat(0, 0.5f);
        obtainStyledAttributes2.recycle();
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

    private void m5676a(int i, Drawable drawable, float f, int i2) {
        int i3;
        i = (i - this.e) - this.f;
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        i = (int) ((1.0f - f) * ((float) ((i - intrinsicHeight) + (this.f5963o * 2))));
        if (i2 == Integer.MIN_VALUE) {
            Rect bounds = drawable.getBounds();
            i2 = bounds.left;
            i3 = bounds.right;
        } else {
            i3 = i2 + intrinsicWidth;
        }
        drawable.setBounds(i2, i, i3, intrinsicHeight + i);
    }

    private void m5677a(MotionEvent motionEvent) {
        float f;
        int height = getHeight();
        int i = (height - this.e) - this.f;
        int y = height - ((int) motionEvent.getY());
        float f2 = 0.0f;
        if (y < this.f) {
            f = 0.0f;
        } else if (y > height - this.e) {
            f = 1.0f;
        } else {
            float f3 = ((float) (y - this.f)) / ((float) i);
            f2 = this.f5959a;
            f = f3;
        }
        m4865a((int) (f2 + (f * ((float) getMax()))), true);
    }

    protected void mo2401a() {
    }

    protected void onStopTrackingTouch() {
    }

    protected void mo2184a(float f, boolean z) {
        Drawable drawable = this.f5962n;
        if (drawable != null) {
            m5676a(getHeight(), drawable, f, Integer.MIN_VALUE);
            invalidate();
        }
    }

    protected void mo2402b() {
    }

    protected void mo2403c() {
    }

    public void drawableStateChanged() {
        super.drawableStateChanged();
        Drawable progressDrawable = getProgressDrawable();
        if (progressDrawable != null) {
            progressDrawable.setAlpha(isEnabled() ? 255 : (int) (this.f5966r * 255.0f));
        }
        if (this.f5962n != null && this.f5962n.isStateful()) {
            this.f5962n.setState(getDrawableState());
        }
    }

//    public boolean onKeyDown(int r4, android.view.KeyEvent r5) {
//        throw new UnsupportedOperationException("Method not decompiled: ahe.onKeyDown(int, android.view.KeyEvent):boolean");
//    }

    public int getKeyProgressIncrement() {
        return this.f5965q;
    }

    public void setKeyProgressIncrement(int i) {
        if (i < 0) {
            i = -i;
        }
        this.f5965q = i;
    }

    public int getThumbOffset() {
        return this.f5963o;
    }

    public void setThumbOffset(int i) {
        this.f5963o = i;
        invalidate();
    }

    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.f5962n != null) {
            canvas.save();
            canvas.translate((float) this.c, (float) (this.e - this.f5963o));
            this.f5962n.draw(canvas);
            canvas.restore();
        }
    }

    protected synchronized void onMeasure(int i, int i2) {
        int max;
        Drawable currentDrawable = getCurrentDrawable();
        int i3 = 0;
        int intrinsicWidth = this.f5962n == null ? 0 : this.f5962n.getIntrinsicWidth();
        if (currentDrawable != null) {
            Math.max(this.h, Math.min(this.i, currentDrawable.getIntrinsicWidth()));
            i3 = Math.max(intrinsicWidth, 0);
            max = Math.max(this.j, Math.min(this.k, currentDrawable.getIntrinsicHeight()));
        } else {
            max = 0;
        }
        setMeasuredDimension(BaseVerticalSeekBar.resolveSize(i3 + (this.c + this.d), i), BaseVerticalSeekBar.resolveSize(max + (this.e + this.f), i2));
    }

    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        Drawable currentDrawable = getCurrentDrawable();
        Drawable drawable = this.f5962n;
        int intrinsicWidth = drawable == null ? 0 : drawable.getIntrinsicWidth();
        int min = Math.min(this.i, (i - this.d) - this.c);
        int max = getMax();
        float progress = max > 0 ? ((float) getProgress()) / ((float) max) : 0.0f;
        if (intrinsicWidth > min) {
            intrinsicWidth = (intrinsicWidth - min) / 2;
            if (drawable != null) {
                m5676a(i2, drawable, progress, intrinsicWidth * -1);
            }
            if (currentDrawable != null) {
                currentDrawable.setBounds(intrinsicWidth, 0, ((i - this.d) - this.c) - intrinsicWidth, (i2 - this.f) - this.e);
            }
            return;
        }
        if (currentDrawable != null) {
            currentDrawable.setBounds(0, 0, (i - this.d) - this.c, (i2 - this.f) - this.e);
        }
        min = (min - intrinsicWidth) / 2;
        if (drawable != null) {
            m5676a(i2, drawable, progress, min);
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.f5960b) {
            if (isEnabled()) {
                if (this.f5964p != null) {
                    this.f5964p.onTouchEvent(motionEvent);
                }
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //Timber.e("hachung :" + "ACTION_DOWN");
                        setPressed(true);
                        mo2401a();

                        pressStartTime = System.currentTimeMillis();
                        pressedX = motionEvent.getX();
                        pressedY = motionEvent.getY();

//                        m5677a(motionEvent);

//                        if (scale != null) {
//                            scale.cancel();
//                        }
//                        down = true;
//                        scale = animate().scaleX(1.15f).scaleY(1.15f);
//                        scale.setDuration(200).setInterpolator(new DecelerateInterpolator());
//                        scale.setListener(new Animator.AnimatorListener() {
//                            @Override
//                            public void onAnimationStart(Animator animation) {
//
//                            }
//
//                            @Override
//                            public void onAnimationEnd(Animator animation) {
//                                if (down) {
//                                    ViewPropertyAnimator scaleY = animate().scaleX(1.1f).scaleY(1.1f);
//                                    scaleY.start();
//                                }
//                            }
//
//                            @Override
//                            public void onAnimationCancel(Animator animation) {
//                                down = false;
//                            }
//
//                            @Override
//                            public void onAnimationRepeat(Animator animation) {
//
//                            }
//                        });
//                        scale.start();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        //Timber.e("hachung :" + "ACTION_UP");
                        longPress = false;
                        onStopTrackingTouch();
//                        m5677a(motionEvent);

//                        down = false;
//                        scale = animate().scaleX(1.0f).scaleY(1.0f);
//                        scale.setDuration(200).setInterpolator(new AccelerateInterpolator());
//                        scale.start();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //Timber.e("hachung :" + "ACTION_MOVE");
                        if (!longPress && distance(pressedX, pressedY, motionEvent.getX(), motionEvent.getY()) > MAX_CLICK_DISTANCE) {
                            m5677a(motionEvent);
                            if (this.g != null) {
                                this.g.requestDisallowInterceptTouchEvent(true);
                                break;
                            }

                        }
                        break;
                    default:
                        break;
                }
                mo2402b();
                setPressed(false);
                invalidate();
                return true;
            }
        }
        return false;
    }

    public synchronized void setMax(int i) {
        super.setMax(i);
        if (this.f5965q == 0 || getMax() / this.f5965q > 20) {
            setKeyProgressIncrement(Math.max(1, Math.round(((float) getMax()) / 20.0f)));
        }
    }

    public void setThumb(Drawable drawable) {
        if (drawable != null) {
            drawable.setCallback(this);
            this.f5963o = drawable.getIntrinsicHeight() / 2;
        }
        this.f5962n = drawable;
        invalidate();
    }

    protected boolean verifyDrawable(Drawable drawable) {
        if (drawable != this.f5962n) {
            if (!super.verifyDrawable(drawable)) {
                return false;
            }
        }
        return true;
    }

    final class C0027a extends SimpleOnGestureListener {
        final BaseVerticalSeekBar f350a;

        private C0027a(BaseVerticalSeekBar BaseVerticalSeekBar) {
            this.f350a = BaseVerticalSeekBar;
        }

        public  void onLongPress(MotionEvent motionEvent) {
            Timber.e("hachung onLongPress:");
            this.f350a.mo2403c();
            longPress = true;
            super.onLongPress(motionEvent);
        }
    }
}

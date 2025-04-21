package com.tapbi.spark.controlcenter.feature.controlios14.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.interfaces.OnSwipeListener;
import com.tapbi.spark.controlcenter.utils.MethodUtils;

public class SwipeLayoutNoty extends ConstraintLayout {
    private boolean isSwipeEnable = true;
    private PointF mLastP = new PointF();
    private PointF mFirstP = new PointF();
    private int xDown, yDown;
    private ValueAnimator mExpandAnim, mCloseAnim, animExpandToDelete;
    private int mMaxVelocity;
    private int mPointerId;
    private VelocityTracker mVelocityTracker;
    private boolean isRightSwipe = true;
    private boolean isTranslate = false;
    private int widthItem = 0;
    private MenuRightLayout menuRightLayout;
    private boolean isNotyChild = true;
    private OnSwipeListener onSwipeListener;
    /////////
    private Path path;
    private int radius;
    private int margin;
    private String content = "";

    public SwipeLayoutNoty(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SwipeLayoutNoty(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mMaxVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isSwipeEnable && onSwipeListener != null) {
            acquireVelocityTracker(ev);
            final VelocityTracker verTracker = mVelocityTracker;
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isTranslate = false;
                    onSwipeListener.onStartSwipe();
                    onSwipeListener.onScrolling(false);
                    mLastP.set(ev.getRawX(), ev.getRawY());
                    mFirstP.set(ev.getRawX(), ev.getRawY());
                    xDown = (int) getX();
                    yDown = (int) getY();
                    mPointerId = ev.getPointerId(0);
                    break;
                case MotionEvent.ACTION_MOVE:
                    onSwipeListener.onScrolling(true);
                    float spaceCurrent = ev.getRawX() - mFirstP.x + xDown;
//                    Timber.e("spaceCurrent: " + spaceCurrent + " /xDown: " + xDown);
                    float spaceCurrenty = ev.getRawY() - mFirstP.y + yDown;
//                    if (Math.abs(spaceCurrent) < 10) {
                    if (spaceCurrent > 0 && xDown >= 0 || (Math.abs(spaceCurrent) - Math.abs(spaceCurrenty) * 1.5F < 0 && !isTranslate)) {
                        onSwipeListener.onScrolling(false);
                        getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                    } else {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }


                    if (Math.abs(spaceCurrent) > 10) {
                        isTranslate = true;
                    }

                    if (!isTranslate) {
                        break;
                    }

                    //Timber.e("spaceCurrent: " + spaceCurrent + " / spaceCurrenty: " + spaceCurrenty);
                    if (!menuRightLayout.isTypeCanDelete() && spaceCurrent < 0 && Math.abs(spaceCurrent) > menuRightLayout.getWidthShow()) {
                        //Timber.e("---------------------");
                        setX(-menuRightLayout.getWidthShow());
                        onSwipeListener.onSwipe(-menuRightLayout.getWidthShow());
                        break;
                    }

                    setX(spaceCurrent);
                    if (getX() > 0) {
                        setX(0);
                    }

                    onSwipeListener.onSwipe((int) getX());

                    isRightSwipe = mLastP.x < ev.getRawX();
                    mLastP.set(ev.getRawX(), ev.getRawY());

                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    verTracker.computeCurrentVelocity(1000, mMaxVelocity);
                    final float velocityX = verTracker.getXVelocity(mPointerId);

                    //Timber.e("ACTION_CANCEL: " + getX() + " /gap: "+gap + " /velocityX: "+velocityX);
                    onSwipeListener.onScrolling(false);
                    float space = getX();
                    if (space < 0) {
                        if (menuRightLayout.isSwipeToDelete()) {
                            smoothExpandToDelete();
                        } else {
                            if (Math.abs(velocityX) > 300) {
                                //Timber.e("mLastP.x: " + mLastP.x + " / : " + ev.getRawX() + " / x: " + ev.getX());
                                if (isRightSwipe) {
                                    //Timber.e("...");
                                    smoothClose();
                                } else {
                                    smoothExpand();
                                }
                            } else {
                                if (Math.abs(space) > (float) menuRightLayout.getWidthShow() / 3) {
                                    smoothExpand();
                                } else {
                                    //Timber.e("...");
                                    smoothClose();
                                }
                            }
                        }
                    }
                    releaseVelocityTracker();
                    break;
                default:
                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public boolean isTranslate() {
        return isTranslate;
    }

    public void setOnSwipeListener(OnSwipeListener onSwipeListener) {
        this.onSwipeListener = onSwipeListener;
    }

    public void smoothExpand() {
        //Timber.e("...");
        cancelAnim();
        mExpandAnim = ValueAnimator.ofInt((int) getX(), -menuRightLayout.getWidthShow());
        mExpandAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int translation = (Integer) animation.getAnimatedValue();
                setX(translation);

                if (onSwipeListener != null) {
                    onSwipeListener.onSwipe((int) getX());
                }
            }
        });

        mExpandAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                onSwipeListener.onEndSwipe();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        mExpandAnim.setDuration(200).start();
    }

    private void cancelAnim() {
        if (mCloseAnim != null && mCloseAnim.isRunning()) {
            mCloseAnim.cancel();
        }
        if (mExpandAnim != null && mExpandAnim.isRunning()) {
            mExpandAnim.cancel();
        }
        if (animExpandToDelete != null && animExpandToDelete.isRunning()) {
            animExpandToDelete.cancel();
        }
    }

    public void smoothClose() {
        //Timber.e("...getX(): " + getX());
        cancelAnim();
        if (getX() == 0) {
            return;
        }
        mCloseAnim = ValueAnimator.ofInt((int) getX(), 0);
        mCloseAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int translation = (Integer) animation.getAnimatedValue();
                setX(translation);
                if (onSwipeListener != null) {
                    onSwipeListener.onSwipe((int) getX());
                }
            }
        });
        mCloseAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isTranslate = false;
            }
        });
        mCloseAnim.setDuration(200).start();
    }

    public void setDefaultClose() {
        //Timber.e("hoangld: ");
        cancelAnim();
        setX(0);
        isTranslate = false;
        if (onSwipeListener != null) {
            onSwipeListener.onSwipe((int) getX());
        }
    }

    public void smoothExpandToDelete() {
        //Timber.e("...");
        cancelAnim();
        animExpandToDelete = ValueAnimator.ofInt((int) getX(), -getWidth());
        animExpandToDelete.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int translation = (Integer) animation.getAnimatedValue();
                if (onSwipeListener != null) {
                    onSwipeListener.onSwipe((int) getX());
                }
                setX(translation);
            }
        });

        animExpandToDelete.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setX(0);
                if (onSwipeListener != null) {
                    onSwipeListener.onSwipeToDelete();
                }
            }
        });

        animExpandToDelete.setDuration(200).start();
    }

    private void acquireVelocityTracker(final MotionEvent event) {
        if (null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private void releaseVelocityTracker() {
        if (null != mVelocityTracker) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    public void setSwipeEnable(boolean isSwipeEnable) {
        this.isSwipeEnable = isSwipeEnable;
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SwipeLayoutNoty);
        isNotyChild = typedArray.getBoolean(R.styleable.SwipeLayoutNoty_isNotyChild, false);
        typedArray.recycle();

        if (isNotyChild) {
            margin = (int) MethodUtils.dpToPx(10);
            radius = (int) MethodUtils.dpToPx(12);
            path = new Path();
            widthItem = App.widthHeightScreenCurrent.w;
        }

    }


    //
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (isNotyChild) {
            canvas.clipPath(path);
        }
        super.dispatchDraw(canvas);
    }

    public void update() {
        if (isNotyChild) {
            setTranslationX();
            post(new Runnable() {
                @Override
                public void run() {
                    setTranslationX();
                }
            });
        }

    }

    public void setMenuRight(MenuRightLayout menuRightLayout) {
        setMenuRight(menuRightLayout, "");
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setMenuRight(MenuRightLayout menuRightLayout, String content) {
        this.content = content;
        this.menuRightLayout = menuRightLayout;
        update();
    }

    public void setTranslationX() {
        path.reset();
        path.addRoundRect(new RectF(margin, 0, widthItem - margin, getHeight()), radius, radius, Path.Direction.CW);
        invalidate();
    }

}

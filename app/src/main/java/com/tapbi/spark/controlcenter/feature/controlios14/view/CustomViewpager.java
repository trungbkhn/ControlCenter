package com.tapbi.spark.controlcenter.feature.controlios14.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

import timber.log.Timber;


public class CustomViewpager extends ViewPager {
    private boolean canScroll = true;
    private boolean onMeasured = false;

    public CustomViewpager(Context context) {
        super(context);
    }

    public CustomViewpager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getAdapter() == null || getAdapter().getCount() == 0) {
            setMeasuredDimension(0, 0);
            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        onMeasured = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            return onMeasured && this.canScroll && super.onTouchEvent(event);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        try {
            return onMeasured && this.canScroll && super.onInterceptTouchEvent(event);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }
}

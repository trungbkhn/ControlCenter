package com.tapbi.spark.controlcenter.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

public class NoSwipeViewPager extends ViewPager {

    private boolean swipeEnabled;

    public NoSwipeViewPager(Context context) {
        super(context);
        this.swipeEnabled = false;
    }

    public NoSwipeViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.swipeEnabled = false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return swipeEnabled && super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return swipeEnabled && super.onTouchEvent(event);
    }

    public void setSwipeEnabled(boolean enabled) {
        this.swipeEnabled = enabled;
    }
}


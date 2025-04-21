package com.tapbi.spark.controlcenter.interfaces;

import android.view.MotionEvent;

public interface OnTouchViewListener {
    void onDown(boolean isTouchNoty, int typeEdge, MotionEvent event);
    void onMove(boolean isTouchNoty, int typeEdge, MotionEvent event);
    void onUp(boolean isTouchNoty, int typeEdge, MotionEvent event);
}

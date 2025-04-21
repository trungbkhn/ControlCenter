package com.tapbi.spark.controlcenter.interfaces;

public interface OnSwipeListener {
    void onSwipe(int x);
    void onScrolling(boolean isScrolling);
    void onStartSwipe();
    void onEndSwipe();
    void onSwipeToDelete();
}

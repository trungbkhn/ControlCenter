package com.tapbi.spark.controlcenter.eventbus;

public class EventShowHideRootView {
    private boolean isShow;

    public EventShowHideRootView(boolean isShow) {
        this.isShow = isShow;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }
}

package com.tapbi.spark.controlcenter.eventbus;

public class EventSaveControl {
    private String action;

    public EventSaveControl(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }
}

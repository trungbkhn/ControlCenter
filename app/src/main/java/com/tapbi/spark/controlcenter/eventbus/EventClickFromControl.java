package com.tapbi.spark.controlcenter.eventbus;

import com.tapbi.spark.controlcenter.data.model.FocusIOS;

public class EventClickFromControl {

    private String type;
    private FocusIOS focusIOS;

    public EventClickFromControl(String type, FocusIOS focusIOS) {
        this.type = type;
        this.focusIOS = focusIOS;
    }
}

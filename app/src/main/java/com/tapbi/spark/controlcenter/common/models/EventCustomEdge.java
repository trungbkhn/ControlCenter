package com.tapbi.spark.controlcenter.common.models;

import com.tapbi.spark.controlcenter.ui.main.edgetriggers.SettingTouchFragment;

public class EventCustomEdge {
    private String action;
    private SettingTouchFragment.TabEdge tabEdge;
    private int valueInt;
    private boolean valueBoolean;

    public EventCustomEdge(String action, SettingTouchFragment.TabEdge tabEdge, int valueInt) {
        this.action = action;
        this.tabEdge = tabEdge;
        this.valueInt = valueInt;
    }

    public EventCustomEdge(String action, SettingTouchFragment.TabEdge tabEdge, boolean valueBoolean) {
        this.action = action;
        this.tabEdge = tabEdge;
        this.valueBoolean = valueBoolean;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public SettingTouchFragment.TabEdge getTabEdge() {
        return tabEdge;
    }

    public void setTabEdge(SettingTouchFragment.TabEdge tabEdge) {
        this.tabEdge = tabEdge;
    }

    public int getValueInt() {
        return valueInt;
    }

    public void setValueInt(int valueInt) {
        this.valueInt = valueInt;
    }

    public boolean isValueBoolean() {
        return valueBoolean;
    }

    public void setValueBoolean(boolean valueBoolean) {
        this.valueBoolean = valueBoolean;
    }
}

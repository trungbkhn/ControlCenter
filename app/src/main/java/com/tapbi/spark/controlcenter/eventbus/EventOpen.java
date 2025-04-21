package com.tapbi.spark.controlcenter.eventbus;

public class EventOpen {

    private String action = "";
    private int idFocus = -1;

    public EventOpen(String action, int idFocus) {
        this.action = action;
        this.idFocus = idFocus;
    }

    public EventOpen(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getIdFocus() {
        return idFocus;
    }

    public void setIdFocus(int idFocus) {
        this.idFocus = idFocus;
    }
}

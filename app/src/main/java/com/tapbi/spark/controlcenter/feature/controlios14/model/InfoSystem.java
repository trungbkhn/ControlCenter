package com.tapbi.spark.controlcenter.feature.controlios14.model;

public class InfoSystem {
    private String name;
    private String action;
    private String uri;
    private int icon;

    public InfoSystem() {
    }

    public InfoSystem(String name, String action, String uri, int icon) {
        this.name = name;
        this.action = action;
        this.uri = uri;
        this.icon = icon;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

}

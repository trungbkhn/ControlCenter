package com.tapbi.spark.controlcenter.feature.controlios14.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class NotyGroup {
    @Expose
    private String packageName;
    @Expose
    private String groupKey;
    @Expose
    private ArrayList<NotyModel> notyModels;
    @Expose
    private STATE state;

    private String appName;

    public enum STATE {EXPAND, EXPANDED, COLLAPSE, NONE, SNOOZED, EXPANDSNOOZED}

    public NotyGroup() {
    }

    public NotyGroup(String packageName, String groupKey, String appName, ArrayList<NotyModel> notyModels) {
        this.packageName = packageName;
        this.groupKey = groupKey;
        this.notyModels = notyModels;
        this.appName = appName;
        this.state = STATE.NONE;
    }




    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    public ArrayList<NotyModel> getNotyModels() {
        return notyModels;
    }

    public void setNotyModels(ArrayList<NotyModel> notyModels) {
        this.notyModels = notyModels;
    }

    public STATE getState() {
        return state;
    }

    public void setState(STATE state) {
        if (state!= null){
            this.state = state;
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(packageName);

        for (int i = 0; i < notyModels.size(); i++) {
            s.append("\t").append(notyModels.get(i).toString());
        }

        return s.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NotyGroup)) return false;
        NotyGroup that = (NotyGroup) o;
        return notyModels.toString().equals(that.toString());
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}

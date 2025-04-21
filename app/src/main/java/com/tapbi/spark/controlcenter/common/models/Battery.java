package com.tapbi.spark.controlcenter.common.models;

import androidx.annotation.NonNull;

public class Battery {
    private int level;
    private boolean isChange;

    private float pct;

    public Battery(int level, boolean isChange, float pct) {
        this.level = level;
        this.pct = pct;
        this.isChange = isChange;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isChange() {
        return isChange;
    }

    public void setChange(boolean change) {
        isChange = change;
    }

    public float getPct() {
        return pct;
    }

    public void setPct(float pct) {
        this.pct = pct;
    }

    @NonNull
    @Override
    public String toString() {
        return "Battery: " + level + "/" + pct + "%" + "/" + isChange;
    }
}

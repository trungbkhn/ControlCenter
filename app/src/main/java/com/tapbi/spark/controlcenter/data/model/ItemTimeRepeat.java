package com.tapbi.spark.controlcenter.data.model;

public class ItemTimeRepeat {
    private String time;
    private String color;
    private boolean isSelect;

    public ItemTimeRepeat(String time, String color, boolean isSelect) {
        this.time = time;
        this.color = color;
        this.isSelect = isSelect;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}

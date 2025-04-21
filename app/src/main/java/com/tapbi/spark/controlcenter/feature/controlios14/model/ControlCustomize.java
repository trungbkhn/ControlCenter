package com.tapbi.spark.controlcenter.feature.controlios14.model;

import android.graphics.drawable.Drawable;

import java.util.Objects;

public class ControlCustomize {
    private int isDefault;
    private String name;
    private Drawable icon;
    private String packageName;

    public ControlCustomize(int isDefault, String name, Drawable icon, String packageName) {
        this.isDefault = isDefault;
        this.name = name;
        this.icon = icon;
        this.packageName = packageName;
    }

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ControlCustomize)) return false;
        ControlCustomize that = (ControlCustomize) o;
        return isDefault == that.isDefault &&
                Objects.equals(name, that.name) &&
                Objects.equals(packageName, that.packageName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isDefault, name, packageName);
    }


    @Override
    public String toString() {
        return "ControlCustomize{" +
                "isDefault=" + isDefault +
                ", name='" + name + '\'' +
                ", icon=" + icon +
                ", packageName='" + packageName + '\'' +
                '}';
    }
}

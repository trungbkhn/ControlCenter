package com.tapbi.spark.controlcenter.feature.controlios14.model;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

public class AppInstallModel {
    private String name;
    private String packageName;
    private Drawable drawable;

    public AppInstallModel() {
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    @NonNull
    @Override
    public String toString() {
        return "AppInstallModel{" +
                "name='" + name + '\'' +
                ", packageName='" + packageName + '\'' +
                '}';
    }
}

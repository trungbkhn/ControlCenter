package com.tapbi.spark.controlcenter.data.model;

import android.graphics.drawable.Drawable;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


@Entity
public class ItemApp {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo
    private String name;
    @ColumnInfo
    private String packageName;
    @ColumnInfo
    private String nameFocus;
    @ColumnInfo
    private boolean isStart;
    @Ignore
    private Drawable iconApp;

    public ItemApp(String name, String packageName, String nameFocus, boolean isStart) {
        this.name = name;
        this.packageName = packageName;
        this.nameFocus = nameFocus;
        this.isStart = isStart;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getNameFocus() {
        return nameFocus;
    }

    public void setNameFocus(String nameFocus) {
        this.nameFocus = nameFocus;
    }

    public boolean isStart() {
        return isStart;
    }

    public void setStart(boolean start) {
        isStart = start;
    }

    public Drawable getIconApp() {
        return iconApp;
    }

    public void setIconApp(Drawable iconApp) {
        this.iconApp = iconApp;
    }
}

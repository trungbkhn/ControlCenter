package com.tapbi.spark.controlcenter.common.models;

import com.google.gson.annotations.SerializedName;

public class AdsItem {
    public AdsItem(String id, String icon, String title, String description, String cover) {
        this.id = id;
        this.icon = icon;
        this.title = title;
        this.description = description;
        this.cover = cover;
    }

    @SerializedName("id")
    private String id = "";

    @SerializedName("icon")
    private String icon = "";

    @SerializedName("star")
    private float star = 4.5f;

    @SerializedName("title")
    private String title = "";

    @SerializedName("description")
    private String description = "";

    @SerializedName("cover")
    private String cover = "";

    @SerializedName("action")
    private int action = 0;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public float getStar() {
        return star;
    }

    public void setStar(float star) {
        this.star = star;
    }
}
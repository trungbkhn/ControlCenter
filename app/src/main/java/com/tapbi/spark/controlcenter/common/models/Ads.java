package com.tapbi.spark.controlcenter.common.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Ads {
    @SerializedName("ratio")
    private int ratio = 0;

    @SerializedName("ads_rollback")
    private ArrayList<AdsItem> adsList = new ArrayList<>();

    public int getRatio() {
        return ratio;
    }

    public void setRatio(int ratio) {
        this.ratio = ratio;
    }

    public ArrayList<AdsItem> getAdsList() {
        return adsList;
    }

    public void setAdsList(ArrayList<AdsItem> adsList) {
        this.adsList = adsList;
    }


}
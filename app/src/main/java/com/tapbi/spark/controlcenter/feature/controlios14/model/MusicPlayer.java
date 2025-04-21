package com.tapbi.spark.controlcenter.feature.controlios14.model;



public class MusicPlayer {
    private String packageName;
    private String receiverName;

    public MusicPlayer(String packageName, String receiverName) {
        this.packageName = packageName;
        this.receiverName = receiverName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }
}

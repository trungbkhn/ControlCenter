package com.tapbi.spark.controlcenter.common.models;

public class ItemRemovedNoty {
    private int posNotyGroup = -1;
    private String groupKey = "";
    private int posNotyModel = -1;
    private String keyNoty = "-1";
    private boolean removeGroups = false;
    private boolean isNotyNow = false;


    public ItemRemovedNoty() {

    }

    public ItemRemovedNoty(int posNotyGroup, int posNotyModel , boolean removeGroups) {
        this.posNotyGroup = posNotyGroup;
        this.posNotyModel = posNotyModel;
        this.removeGroups =  removeGroups;
    }

    public int getPosNotyGroup() {
        return posNotyGroup;
    }

    public void setPosNotyGroup(int posNotyGroup) {
        this.posNotyGroup = posNotyGroup;
    }

    public int getPosNotyModel() {
        return posNotyModel;
    }

    public void setPosNotyModel(int posNotyModel) {
        this.posNotyModel = posNotyModel;
    }

    public boolean isRemoveGroups() {
        return removeGroups;
    }

    public void setRemoveGroups(boolean removeGroups) {
        this.removeGroups = removeGroups;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    public String getKeyNoty() {
        return keyNoty;
    }

    public void setKeyNoty(String keyNoty) {
        this.keyNoty = keyNoty;
    }

    public boolean isNotyNow() {
        return isNotyNow;
    }

    public void setNotyNow(boolean notyNow) {
        isNotyNow = notyNow;
    }
}

package com.tapbi.spark.controlcenter.common.models;

public class ItemAddedNoty {
    private int posGroupAdd = -1;
    private int posGroupRemove = -1;
    private int posGroupNotify = -1;
    private int posChildAdd = -1;
    private int posChildRemove = -1;
    private boolean isNewGroupListNow = false;
    private String packageName = "";
    private String keyNoty = "";

    public ItemAddedNoty() {
    }

    public ItemAddedNoty(int posGroupAdd, int posGroupRemove, int posChildAdd, int posChildRemove) {
        this.posGroupAdd = posGroupAdd;
        this.posGroupRemove = posGroupRemove;
        this.posChildAdd = posChildAdd;
        this.posChildRemove = posChildRemove;
    }

    public int getPosGroupAdd() {
        return posGroupAdd;
    }

    public void setPosGroupAdd(int posGroupAdd) {
        this.posGroupAdd = posGroupAdd;
    }

    public int getPosGroupRemove() {
        return posGroupRemove;
    }

    public void setPosGroupRemove(int posGroupRemove) {
        this.posGroupRemove = posGroupRemove;
    }

    public int getPosChildAdd() {
        return posChildAdd;
    }

    public void setPosChildAdd(int posChildAdd) {
        this.posChildAdd = posChildAdd;
    }

    public int getPosChildRemove() {
        return posChildRemove;
    }

    public void setPosChildRemove(int posChildRemove) {
        this.posChildRemove = posChildRemove;
    }

    public int getPosGroupNotify() {
        return posGroupNotify;
    }

    public void setPosGroupNotify(int posGroupNotify) {
        this.posGroupNotify = posGroupNotify;
    }

    public boolean isNewGroupListNow() {
        return isNewGroupListNow;
    }

    public void setNewGroupListNow(boolean newGroupListNow) {
        isNewGroupListNow = newGroupListNow;
    }

    public String getPackageName() {
        if (packageName == null) {
            return "";
        }
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getKeyNoty() {
        return keyNoty;
    }

    public void setKeyNoty(String keyNoty) {
        this.keyNoty = keyNoty;
    }

    @Override
    public String toString() {
        return "ItemAddedNoty{" +
                "posGroupAdd=" + posGroupAdd +
                ", posGroupRemove=" + posGroupRemove +
                ", posGroupNotify=" + posGroupNotify +
                ", posChildAdd=" + posChildAdd +
                ", posChildRemove=" + posChildRemove +
                ", isNewGroupListNow=" + isNewGroupListNow +
                ", packageName='" + packageName + '\'' +
                '}';
    }
}

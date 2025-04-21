package com.tapbi.spark.controlcenter.feature.controlios14.model;

public class PosNotyModel {
  private NotyModel notyModel;
  private int posGroup;
  private int posCount;
  private boolean isShow = false;

  public int getPosCount() {
    return posCount;
  }

  public void setPosCount(int posCount) {
    this.posCount = posCount;
  }

  public PosNotyModel(NotyModel notyModel, int posGroup, int posCount, boolean isShow) {
    this.notyModel = notyModel;
    this.posGroup = posGroup;
    this.posCount = posCount;
    this.isShow = isShow;
  }

  public boolean isShow() {
    return isShow;
  }

  public void setShow(boolean show) {
    isShow = show;
  }

  public PosNotyModel() {
  }


  public NotyModel getNotyModel() {
    return notyModel;
  }

  public void setNotyModel(NotyModel notyModel) {
    this.notyModel = notyModel;
  }

  public int getPosGroup() {
    return posGroup;
  }

  public void setPosGroup(int posGroup) {
    this.posGroup = posGroup;
  }
}

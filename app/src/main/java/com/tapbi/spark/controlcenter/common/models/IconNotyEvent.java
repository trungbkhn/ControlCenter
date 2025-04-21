package com.tapbi.spark.controlcenter.common.models;

public class IconNotyEvent {
  private boolean isChange;

  public IconNotyEvent(boolean isChange) {
    this.isChange = isChange;
  }

  public boolean isChange() {
    return isChange;
  }

  public void setChange(boolean change) {
    isChange = change;
  }
}

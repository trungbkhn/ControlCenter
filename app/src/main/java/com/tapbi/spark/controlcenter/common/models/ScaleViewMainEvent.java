package com.tapbi.spark.controlcenter.common.models;

public class ScaleViewMainEvent {
  private boolean isZoomIn;
  private boolean isZoomOut;
  private float scale;


  public ScaleViewMainEvent(boolean isZoomIn, boolean isZoomOut, float scale) {
    this.isZoomIn = isZoomIn;
    this.isZoomOut = isZoomOut;
    this.scale = scale;
  }

  public boolean isZoomIn() {
    return isZoomIn;
  }

  public void setZoomIn(boolean zoomIn) {
    isZoomIn = zoomIn;
  }

  public boolean isZoomOut() {
    return isZoomOut;
  }

  public void setZoomOut(boolean zoomOut) {
    isZoomOut = zoomOut;
  }

  public float getScale() {
    return scale;
  }

  public void setScale(float scale) {
    this.scale = scale;
  }
}

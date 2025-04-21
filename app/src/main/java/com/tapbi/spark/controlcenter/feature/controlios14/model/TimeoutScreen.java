package com.tapbi.spark.controlcenter.feature.controlios14.model;

public class TimeoutScreen {
  private int time;
  private String title;

  public TimeoutScreen(){

  }

  public TimeoutScreen(int time, String title) {
    this.time = time;
    this.title = title;
  }

  public int getTime() {
    return time;
  }

  public void setTime(int time) {
    this.time = time;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
}

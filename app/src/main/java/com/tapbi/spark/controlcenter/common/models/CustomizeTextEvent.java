package com.tapbi.spark.controlcenter.common.models;

public class CustomizeTextEvent {
  private String text;

  public CustomizeTextEvent(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }
}

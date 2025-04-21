package com.tapbi.spark.controlcenter.feature.controlcenter.model;

public class InfoIcon {
  String name;
  int Icon;
  String nameRes;
  boolean isSelect = false;

  public InfoIcon(String name, int icon, String nameRes) {
    this.name = name;
    Icon = icon;
    this.nameRes = nameRes;
  }

  public String getNameRes() {
    return nameRes;
  }

  public void setNameRes(String nameRes) {
    this.nameRes = nameRes;
  }

  public InfoIcon(String name, int icon, boolean isSelect) {
    this.name = name;
    Icon = icon;
    this.isSelect = isSelect;
  }

  public boolean isSelect() {
    return isSelect;
  }

  public void setSelect(boolean select) {
    isSelect = select;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getIcon() {
    return Icon;
  }

  public void setIcon(int icon) {
    Icon = icon;
  }
}

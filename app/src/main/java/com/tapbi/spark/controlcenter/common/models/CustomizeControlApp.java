package com.tapbi.spark.controlcenter.common.models;

import com.tapbi.spark.controlcenter.feature.controlios14.model.ControlCustomize;

import java.util.ArrayList;

public class CustomizeControlApp {
  private ArrayList<ControlCustomize> listCustomizeCurrentApp;
  private ArrayList<ControlCustomize> listExceptCurrentApp;

  public CustomizeControlApp(ArrayList<ControlCustomize> listCustomizeCurrentApp, ArrayList<ControlCustomize> listExceptCurrentApp) {
    this.listCustomizeCurrentApp = listCustomizeCurrentApp;
    this.listExceptCurrentApp = listExceptCurrentApp;
  }

  public ArrayList<ControlCustomize> getListCustomizeCurrentApp() {
    return listCustomizeCurrentApp;
  }

  public void setListCustomizeCurrentApp(ArrayList<ControlCustomize> listCustomizeCurrentApp) {
    this.listCustomizeCurrentApp = listCustomizeCurrentApp;
  }

  public ArrayList<ControlCustomize> getListExceptCurrentApp() {
    return listExceptCurrentApp;
  }

  public void setListExceptCurrentApp(ArrayList<ControlCustomize> listExceptCurrentApp) {
    this.listExceptCurrentApp = listExceptCurrentApp;
  }

}

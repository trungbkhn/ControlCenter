package com.tapbi.spark.controlcenter.feature.controlcenter.cb;

import com.tapbi.spark.controlcenter.feature.controlios14.model.InfoSystem;

public interface ClickAddOrRemoveAction {
  void clickAdd(InfoSystem infoSystem, int pos);
  void clickRemove(InfoSystem infoSystem, int pos);
}

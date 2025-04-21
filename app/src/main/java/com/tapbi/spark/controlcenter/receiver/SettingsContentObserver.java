package com.tapbi.spark.controlcenter.receiver;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;

import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group4.SettingVolumeView;

public class SettingsContentObserver extends ContentObserver {
  private SettingVolumeView.updateUI updateUI;

  public SettingsContentObserver(Context context, Handler handler, SettingVolumeView.updateUI updateUI) {
    super(handler);
    this.updateUI = updateUI;
  }

  @Override
  public boolean deliverSelfNotifications() {
    return false;
  }

  @Override
  public void onChange(boolean selfChange) {
    updateUI.update();
  }
}

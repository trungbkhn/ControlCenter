package com.tapbi.spark.controlcenter.utils.helper;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.tapbi.spark.controlcenter.R;

public class ViewHelper {
  public static void preventTwoClick(final View view){
    view.setEnabled(false);
    view.postDelayed(new Runnable() {
      public void run() {
        view.setEnabled(true);
      }
    }, 500);
  }
  public static void preventTwoClick(final View view,long time){
    view.setEnabled(false);
    view.postDelayed(new Runnable() {
      public void run() {
        view.setEnabled(true);
      }
    }, time);
  }

  public static void setUpWrapHeight(BottomSheetDialog bottomSheetDialog) {
    BottomSheetBehavior behavior = bottomSheetDialog.getBehavior();
    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    behavior.setSkipCollapsed(true);
  }

  private static int getWindowHeight(Context context) {
    // Calculate window height for fullscreen use
    DisplayMetrics displayMetrics = new DisplayMetrics();
    ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    return displayMetrics.heightPixels;
  }

}
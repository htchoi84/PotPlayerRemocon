package com.htchoi.potplayerremote.utils;

import android.app.Activity;
import android.widget.Toast;

public abstract class AbBackPressCloseHandler {

  private long backKeyPressedTime = 0;
  private Toast toast;
  protected Activity activity;

  public AbBackPressCloseHandler(Activity activity) {
    this.activity = activity;
  }

  public void onBackPressed() {
    if (System.currentTimeMillis() > backKeyPressedTime + getDelayTime()) {
      backKeyPressedTime = System.currentTimeMillis();
      showGuide();
      return;
    }
    if (System.currentTimeMillis() <= backKeyPressedTime + getDelayTime()) {
      activity.finish();
      toast.cancel();
    }
  }

  protected abstract int getDelayTime();
  protected abstract String getToastMessage();

  private void showGuide() {
    toast = Toast.makeText(activity, getToastMessage(), Toast.LENGTH_SHORT);
    toast.show();
  }
}

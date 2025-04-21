package com.tapbi.spark.controlcenter.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class HHeadsetReceiver: BroadcastReceiver() {

  var callback: HeadsetReceiverCallback? = null

  private object HOLDER {
    val INSTANCE = HHeadsetReceiver()
  }

  companion object {
    @JvmStatic
    val instance: HHeadsetReceiver by lazy { HOLDER.INSTANCE }
  }

  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action == Intent.ACTION_HEADSET_PLUG) {
      if(intent.getIntExtra("state", -1) == 0) {
        callback?.onHeadsetDisconnected()
      } else {
        callback?.onHeadsetConnected()
      }
    }
  }

  fun register(context: Context) {
    val receiverFilter = IntentFilter(Intent.ACTION_HEADSET_PLUG)
    try {
      context.registerReceiver(this, receiverFilter)
    } catch (e: Exception) {
      e.printStackTrace();
    }
  }

  fun unregister(context: Context) {
    try {
      context.unregisterReceiver(this)
    }catch (e: Exception) {
      e.printStackTrace();
    }

    callback = null
  }

  interface HeadsetReceiverCallback {
    fun onHeadsetConnected()
    fun onHeadsetDisconnected()
  }
}
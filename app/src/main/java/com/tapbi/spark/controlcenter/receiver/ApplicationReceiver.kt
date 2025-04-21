package com.tapbi.spark.controlcenter.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.common.models.MessageEvent
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

class ApplicationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_PACKAGE_REMOVED) {
            Timber.e("hachung ACTION_PACKAGE_REMOVED: ")
            intent.data?.encodedSchemeSpecificPart?.let {
                Timber.e("hachung it: $it")
                App.ins.focusUtils?.sendActionFocus(
                    Constant.PACKAGE_REMOVE,
                    it
                )
            }
        }
        if (intent.action == Intent.ACTION_PACKAGE_ADDED) {
            EventBus.getDefault().post(intent.data?.encodedSchemeSpecificPart?.let {
                MessageEvent(Constant.PACKAGE_APP_ADD, it)
            })
        }
    }

}
package com.tapbi.spark.controlcenter.utils

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.Activity
import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.accessibility.AccessibilityManager
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614
import timber.log.Timber

object AccessUtils {
    fun isAccessibilityServiceEnabled(context: Context, service: Class<out AccessibilityService> = NotyControlCenterServicev614::class.java): Boolean {
        if (!isAccessibilityServiceEnabledInSetting(context,service)) {
            return false
        }
        if (!isServiceRunning(context,service)) {
            return false
        }
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        for (enabledService in enabledServices) {
            val enabledServiceInfo: ServiceInfo = enabledService.resolveInfo.serviceInfo
            if (enabledServiceInfo.packageName == context.packageName && enabledServiceInfo.name == service.name) {
                return true
            }
        }
        return false
    }

    private fun isAccessibilityServiceEnabledInSetting(context: Context, service: Class<out AccessibilityService>): Boolean {
        val enabledServices = Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
        val colonSplitter = TextUtils.SimpleStringSplitter(':')
        try{
            colonSplitter.setString(enabledServices)
        } catch (_:Exception){return false}
        while (colonSplitter.hasNext()) {
            val componentName = ComponentName.unflattenFromString(colonSplitter.next())
            if (componentName != null && componentName.packageName == context.packageName && componentName.className == service.name) {
                return true
            }
        }
        return false
    }

    private fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningServices = manager.getRunningServices(Integer.MAX_VALUE) ?: return false

        for (serviceInfo in runningServices) {
            if (serviceInfo.service.className == serviceClass.name) {
                return true
            }
        }
        return false
    }
    fun requestAss(activity: Activity){
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        val bundle = Bundle()
        val showArgs = activity.packageName + "/" + NotyControlCenterServicev614::class.java.name
        bundle.putString(":settings:fragment_args_key", showArgs)
        intent.putExtra(":settings:fragment_args_key", showArgs)
        intent.putExtra(":settings:show_fragment_args", bundle)
        try {
            activity.startActivity(intent)
        } catch (e: Exception) {
            Timber.e( "Error starting activity: ${e.message}")
        }
    }

}
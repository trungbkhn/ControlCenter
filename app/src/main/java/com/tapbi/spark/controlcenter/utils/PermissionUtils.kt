package com.tapbi.spark.controlcenter.utils

import android.Manifest
import android.app.Activity
import android.app.AppOpsManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.interfaces.IListenerBackPressed
import com.tapbi.spark.controlcenter.service.NotificationListener
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614
import com.tapbi.spark.controlcenter.ui.dialog.DialogRequestPermissionWriteSetting
import com.tapbi.spark.controlcenter.utils.AccessUtils.isAccessibilityServiceEnabled
import timber.log.Timber
import java.security.AccessController.getContext


object PermissionUtils {

    private var dialogPermissionAlertDialog: DialogRequestPermissionWriteSetting? = null
    fun checkPermissionReadContact(): Boolean {
        return ContextCompat.checkSelfPermission(
            App.mContext,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun checkPermissionService(): Boolean {
       return SettingUtils.checkPermissionNotificationListener(App.ins) && SettingUtils.checkPermissionOverlay(
            App.ins
        ) && isAccessibilityServiceEnabled(App.ins) && checkPermissionReadPhoneState(
            App.ins
        )
    }
    fun checkAllPermissionWithOutReadPhoneState() : Boolean{
        return SettingUtils.checkPermissionNotificationListener(App.ins) && SettingUtils.checkPermissionOverlay(
            App.ins
        ) && isAccessibilityServiceEnabled(App.ins)
    }

    fun checkPermissionWriteExternalStorage(): Boolean {
        return ContextCompat.checkSelfPermission(
            App.mContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }


//    fun checkPermissionCallListener(context: Context): Boolean {
//        val requiredPermissions: MutableList<String> = ArrayList()
//        requiredPermissions.add(Manifest.permission.CALL_PHONE)
//        requiredPermissions.add(Manifest.permission.READ_PHONE_STATE)
//        requiredPermissions.add(Manifest.permission.READ_CALL_LOG)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            requiredPermissions.add(Manifest.permission.ANSWER_PHONE_CALLS)
//        }
//        val missingPermissions: MutableList<String> = java.util.ArrayList()
//        for (permission in requiredPermissions) {
//            if (ContextCompat.checkSelfPermission(context, permission)
//                != PackageManager.PERMISSION_GRANTED
//            ) {
//                missingPermissions.add(permission)
//            }
//        }
//        return missingPermissions.isEmpty()
//    }

//    fun checkPermissionPhone(context: Context): Boolean {
//        if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.M)) {
//            return true
//        }
//        val requiredPermissions: MutableList<String> = java.util.ArrayList()
//        requiredPermissions.add(Manifest.permission.CALL_PHONE)
//        requiredPermissions.add(Manifest.permission.READ_PHONE_STATE)
//        requiredPermissions.add(Manifest.permission.READ_CALL_LOG)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            requiredPermissions.add(Manifest.permission.ANSWER_PHONE_CALLS)
//        }
//
//        var checkPermission = true
//
//        for (permission in requiredPermissions) {
//            if (ContextCompat.checkSelfPermission(context, permission)
//                != PackageManager.PERMISSION_GRANTED
//            ) {
//                checkPermission = false
//            }
//        }
//        return checkPermission
//    }

    fun checkPermissionReadPhoneState(context: Context): Boolean {
        return (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) || ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED

    }

    fun isAccessGranted(context: Context): Boolean {
        return try {
            val packageManager = context.packageManager
            val applicationInfo: ApplicationInfo =
                packageManager.getApplicationInfo(context.packageName, 0)
            val appOpsManager: AppOpsManager =
                context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode: Int = appOpsManager.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                applicationInfo.uid, applicationInfo.packageName
            )
            mode == AppOpsManager.MODE_ALLOWED
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private const val REQUEST_CODE_PERMISSIONS = 100


    /**
     * Check if multiple permissions are granted, if not request them.
     *
     * @param activity calling activity which needs permissions.
     * @param permissions one or more permissions, such as [android.Manifest.permission.CAMERA].
     * @return true if all permissions are granted, false if at least one is not granted yet.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    fun checkAndRequestPermissions(activity: Activity, vararg permissions: String): Boolean {
        val permissionsList: MutableList<String> = ArrayList()
        for (permission in permissions) {
            val permissionState: Int = activity.checkSelfPermission(permission)
            if (permissionState == PackageManager.PERMISSION_DENIED) {
                permissionsList.add(permission)
            }
        }
        Timber.e("hachung permissionsList: $permissionsList")
        if (permissionsList.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                activity,
                permissionsList.toTypedArray(),
                REQUEST_CODE_PERMISSIONS
            )
            return false
        }
        return true
    }


    /**
     * Handle the result of permission request, should be called from the calling [Activity]'s
     * [ActivityCompat.OnRequestPermissionsResultCallback.onRequestPermissionsResult]
     *
     * @param activity calling activity which needs permissions.
     * @param requestCode code used for requesting permission.
     * @param permissions permissions which were requested.
     * @param grantResults results of request.
     * @param callBack Callback interface to receive the result of permission request.
     */
    fun onRequestPermissionsResult(
        activity: Activity,
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray,
        callBack: PermissionsCallBack?
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS && grantResults.isNotEmpty()) {
            val permissionsList: MutableList<String?> = ArrayList()
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    permissionsList.add(permissions[i])
                }
            }
            if (permissionsList.isEmpty() && callBack != null) {
                callBack.permissionsGranted()
            } else {
                var showRationale = false
                for (permission in permissionsList) {
                    if (shouldShowRequestPermissionRationale(
                            activity,
                            permission!!
                        )
                    ) {
                        showRationale = true
                        break
                    }
                }
                if (showRationale) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (getContext() != null) {
                            if (dialogPermissionAlertDialog == null) {
                                //NVQ dialog
                                dialogPermissionAlertDialog = MethodUtils.showDialogPermission(
                                    activity,
                                    true,"",
                                    false,
                                    null
                                )
                            }
                            if (dialogPermissionAlertDialog?.dialog?.isShowing != true) {
                                dialogPermissionAlertDialog?.showDialog(activity.fragmentManager as androidx.fragment.app.FragmentManager,
                                    Constant.DIALOG_REQUEST_PERMISSION_WRITE_SETTING)
                            }
                        } else {
                            callBack?.permissionsDenied()
                        }
                    } else {
                        callBack?.permissionsDenied()
                    }
                }
            }
        }
    }

    /**
     * Show alert if any permission is denied and ask again for it.
     *
     * @param context
     * @param okListener
     * @param cancelListener
     */


    interface PermissionsCallBack {
        fun permissionsGranted()
        fun permissionsDenied()
    }


    fun requestPermissionOverlay(context: Context?) {
        if (context != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(context)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + context.packageName)
                )
                intent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK
                        or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        or Intent.FLAG_ACTIVITY_NO_HISTORY
                        or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                        or Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
                try {
                    context.startActivity(intent)
                } catch (e: java.lang.Exception) {
                    Timber.e(e)
                }
            }
        }
    }
    fun requestPermissionNotifyListener(context: Context?) {
        if (context == null)
            return
        val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
        intent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK
                or Intent.FLAG_ACTIVITY_SINGLE_TOP
                or Intent.FLAG_ACTIVITY_CLEAR_TOP
                or Intent.FLAG_ACTIVITY_NO_HISTORY
                or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                or Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
        val bundle = Bundle()
        val str: String = context.packageName + "/" + NotificationListener::class.java.name
        bundle.putString(":settings:fragment_args_key", str)
        intent.putExtra(":settings:fragment_args_key", str)
        intent.putExtra(":settings:show_fragment_args", bundle)

//        Thread {
//            while (true) {
//                if (checkPermissionNotificationListener(context)) {
//                    startPermissionFragment(context)
//                    break
//                }
//            }
//        }.start()

        try {
            context.startActivity(intent)
        } catch (e: java.lang.Exception) {
            Timber.e(e)
        }
    }
    fun openSettingsAccessibility(context: Context?) {
        if (context == null)
            return
        try {
            val intent = Intent("android.settings.ACCESSIBILITY_SETTINGS")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val bundle = Bundle()
            val flattenToString = ComponentName(
                context.packageName,
                NotyControlCenterServicev614::class.java.name
            ).flattenToString()
            bundle.putString(":settings:fragment_args_key", flattenToString)
            intent.putExtra(":settings:fragment_args_key", flattenToString)
            intent.putExtra(":settings:show_fragment_args", bundle)
            context.startActivity(intent)
        } catch (e: java.lang.Exception) {
            try {
                val intent2 = Intent("android.settings.ACCESSIBILITY_SETTINGS")
                intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent2)
                e.printStackTrace()
            } catch (unused: java.lang.Exception) {
                Timber.e(unused)
            }
        }
    }
    fun showDialogGotoSetting(
        context: Activity,
        content: String,
        iListenerBackPressed: IListenerBackPressed?,
    ): DialogRequestPermissionWriteSetting {

        val dialogGoToSetting = DialogRequestPermissionWriteSetting()

        dialogGoToSetting.setText(
            content.ifEmpty { context.getString(R.string.You_need_to_enable_permissions_to_use_this_feature) },
                context.getString(R.string.go_to_setting),
                ""
            )
        dialogGoToSetting.isCancelable = false

        dialogGoToSetting.setDialogListener(object : DialogRequestPermissionWriteSetting.ClickListener {
            override fun onClickOke() {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)

                dialogGoToSetting.dismissAllowingStateLoss()
            }

            override fun onClickCancel() {
                dialogGoToSetting.dismissAllowingStateLoss()
            }

            override fun onBackPress() {
                iListenerBackPressed?.onBackPressed()
            }
        })

        return dialogGoToSetting
    }

}
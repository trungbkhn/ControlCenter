package com.tapbi.spark.controlcenter.utils;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.text.TextUtils;


import com.tapbi.spark.controlcenter.App;

import java.lang.reflect.Method;
import java.util.List;

// MIUI. Redefining Android.
// (not in the very best way I'd say)
public class XiaomiUtilities {

    // custom permissions
    public static final int OP_ACCESS_XIAOMI_ACCOUNT = 10015;
    public static final int OP_AUTO_START = 10008;
    public static final int OP_BACKGROUND_START_ACTIVITY = 10021;
    public static final int OP_BLUETOOTH_CHANGE = 10002;
    public static final int OP_BOOT_COMPLETED = 10007;
    public static final int OP_DATA_CONNECT_CHANGE = 10003;
    public static final int OP_DELETE_CALL_LOG = 10013;
    public static final int OP_DELETE_CONTACTS = 10012;
    public static final int OP_DELETE_MMS = 10011;
    public static final int OP_DELETE_SMS = 10010;
    public static final int OP_EXACT_ALARM = 10014;
    public static final int OP_GET_INSTALLED_APPS = 10022;
    public static final int OP_GET_TASKS = 10019;
    public static final int OP_INSTALL_SHORTCUT = 10017;
    public static final int OP_NFC = 10016;
    public static final int OP_NFC_CHANGE = 10009;
    public static final int OP_READ_MMS = 10005;
    public static final int OP_READ_NOTIFICATION_SMS = 10018;
    public static final int OP_SEND_MMS = 10004;
    public static final int OP_SERVICE_FOREGROUND = 10023;
    public static final int OP_SHOW_WHEN_LOCKED = 10020;
    public static final int OP_WIFI_CHANGE = 10001;
    public static final int OP_WRITE_MMS = 10006;

    public static boolean isMIUI() {
        return !TextUtils.isEmpty(DensityUtils.getSystemProperty("ro.miui.ui.version.name"));
    }

    @SuppressWarnings("JavaReflectionMemberAccess")
    @TargetApi(19)
    public static boolean isCustomPermissionGranted(int permission) {
        try {
            AppOpsManager mgr = (AppOpsManager) App.mContext.getSystemService(Context.APP_OPS_SERVICE);
            Method m = AppOpsManager.class.getMethod("checkOpNoThrow", int.class, int.class, String.class);
            int result = (int) m.invoke(mgr, permission, android.os.Process.myUid(), App.mContext.getPackageName());
            return result == AppOpsManager.MODE_ALLOWED;
        } catch (Exception x) {
            //Timber.e(x);
        }
        return true;
    }

    public static int getMIUIMajorVersion() {
        String prop = DensityUtils.getSystemProperty("ro.miui.ui.version.name");
        if (prop != null) {
            try {
                return Integer.parseInt(prop.replace("V", ""));
            } catch (NumberFormatException ignore) {
            }
        }
        return -1;
    }

    public static Intent getPermissionManagerIntent() {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.putExtra("extra_package_uid", android.os.Process.myUid());
        intent.putExtra("extra_pkgname", App.mContext.getPackageName());
        return intent;
    }


    public static void goToXiaomiPermissions(Context context) {
//        val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
//        intent.setClassName(
//                "com.miui.securitycenter",
//                "com.miui.permcenter.permissions.PermissionsEditorActivity"
//        )
//        intent.putExtra("extra_pkgname", context.packageName)
        context.startActivity(getPermissionManagerIntent());
    }

    public static boolean needAutoStartup(Context context) {
        try {
            Intent intent = new Intent();
            String manufacturer = Build.MANUFACTURER;

            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName(
                        "com.miui.securitycenter",
                        "com.miui.permcenter.autostart.AutoStartManagementActivity"
                ));
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName(
                        "com.coloros.safecenter",
                        "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                ));
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName(
                        "com.vivo.permissionmanager",
                        "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                ));
            } else if ("Letv".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName(
                        "com.letv.android.letvsafe",
                        "com.letv.android.letvsafe.AutobootManageActivity"
                ));
            } else if ("Honor".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName(
                        "com.huawei.systemmanager",
                        "com.huawei.systemmanager.optimize.process.ProtectActivity"
                ));
            }
            List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(
                    intent,
                    PackageManager.MATCH_DEFAULT_ONLY
            );
            if (list.size() > 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void goToXiaomiPermissionsAutoStart(Context context) {
        Intent intent = new Intent();
        String manufacturer = Build.MANUFACTURER;
        if ("xiaomi".equalsIgnoreCase(manufacturer)) {
            intent.setComponent(new ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
            ));
        } else if ("oppo".equalsIgnoreCase(manufacturer)) {
            intent.setComponent(new ComponentName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.permission.startup.StartupAppListActivity"
            ));
        } else if ("vivo".equalsIgnoreCase(manufacturer)) {
            intent.setComponent(new ComponentName(
                    "com.vivo.permissionmanager",
                    "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
            ));
        } else if ("Letv".equalsIgnoreCase(manufacturer)) {
            intent.setComponent(new ComponentName(
                    "com.letv.android.letvsafe",
                    "com.letv.android.letvsafe.AutobootManageActivity"
            ));
        } else if ("Honor".equalsIgnoreCase(manufacturer)) {
            intent.setComponent(new ComponentName(
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.optimize.process.ProtectActivity"
            ));
        }
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
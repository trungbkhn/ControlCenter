package com.tapbi.spark.controlcenter.feature.controlios14.manager;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.lang.reflect.Method;

public class PermissionManager {

    private static PermissionManager instance;

    public static PermissionManager getInstance() {
        if (instance == null) {
            instance = new PermissionManager();
        }
        return instance;
    }

    public boolean checkPermission(Context context, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    public void requestPermission(Activity activity, String[] permission, int requestCode) {
        ActivityCompat.requestPermissions(activity, permission, requestCode);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean checkPermissionAppRecent(Context context) {
        boolean granted;
        AppOpsManager appOps = (AppOpsManager) context
                .getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), context.getPackageName());

        if (mode == AppOpsManager.MODE_DEFAULT) {
            granted = (context.checkCallingOrSelfPermission("android.permission.PACKAGE_USAGE_STATS") == PackageManager.PERMISSION_GRANTED);
        } else {
            granted = (mode == AppOpsManager.MODE_ALLOWED);
        }

        return granted;
    }

    public static boolean isFloatWindowOptionAllowed(Context context) {
        AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        Class localClass = manager.getClass();
        Class[] arrayOfClass = new Class[3];
        arrayOfClass[0] = Integer.TYPE;
        arrayOfClass[1] = Integer.TYPE;
        arrayOfClass[2] = String.class;
        try {
            Method method = localClass.getMethod("checkOp", arrayOfClass);
            if (method == null) {
                return false;
            }
            Object[] arrayOfObjects = new Object[3];
            arrayOfObjects[0] = Integer.valueOf(24);
            arrayOfObjects[1] = Integer.valueOf(Binder.getCallingUid());
            arrayOfObjects[2] = context.getPackageName();
            int m = ((Integer) method.invoke((Object) manager, arrayOfObjects)).intValue();
            return m == AppOpsManager.MODE_ALLOWED;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean canDrawOverlaysUsingReflection(Context context) {
        try {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            Class clazz = AppOpsManager.class;
            Method dispatchMethod = clazz.getMethod("checkOp", new Class[]{int.class, int.class, String.class});
            //AppOpsManager.OP_SYSTEM_ALERT_WINDOW = 24
            int mode = (Integer) dispatchMethod.invoke(manager,
                    new Object[]{24, Binder.getCallingUid(), context.getApplicationContext().getPackageName()});

            return AppOpsManager.MODE_ALLOWED == mode;

        } catch (Exception e) {
            return false;
        }
    }


//    public static boolean isMIUI() {
//        try {
//            Class<?> c = Class.forName("android.os.SystemProperties");
//            Method get = c.getMethod("get", String.class);
//            String miui = (String) get.invoke(c, "ro.miui.ui.version.name");
//            if (miui != null && (miui.contains("11") || miui.contains("12") || miui.contains("13"))) {
//                return true;
//            }
//        } catch (Exception e) {
//            Timber.e(e);
//        }
//        return false;
//    }

//    public static void onDisplayPopupPermission(Context context) {
//        if (!isMIUI()) {
//            return;
//        }
//        try {
//            // MIUI 8
//            Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
//            localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
//            localIntent.putExtra("extra_pkgname", context.getPackageName());
//            context.startActivity(localIntent);
//            return;
//        } catch (Exception ignore) {
//        }
//        try {
//            // MIUI 5/6/7
//            Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
//            localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
//            localIntent.putExtra("extra_pkgname", context.getPackageName());
//            context.startActivity(localIntent);
//            return;
//        } catch (Exception ignore) {
//        }
//        // Otherwise jump to application details
//        try {
//            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
//            intent.setData(uri);
//            context.startActivity(intent);
//        } catch (Exception e) {
//            Timber.e(e);
//        }
//
//    }

}

package com.tapbi.spark.controlcenter.utils;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.provider.Telephony;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.data.model.ItemApp;
import com.tapbi.spark.controlcenter.data.model.ItemTurnOn;
import com.tapbi.spark.controlcenter.feature.controlios14.model.AppInstallModel;
import com.tapbi.spark.controlcenter.feature.controlios14.model.ControlCustomize;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;
import timber.log.Timber;

public class AppUtils {
    public static List<ItemApp> updatePackageRemoveAllowedApp(String packageName, List<ItemApp> list) {
        List<ItemApp> listAllowed = new ArrayList<>(list);
        for (ItemApp item : list) {
            if (packageName.equals(item.getPackageName())) {
                listAllowed.remove(item);
                break;
            }
        }
        return listAllowed;
    }

    public static ArrayList<ControlCustomize> updatePackageRemoveControlCustomize(String packageName, List<ControlCustomize> list) {
        ArrayList<ControlCustomize> listAllowed = new ArrayList<>(list);
        for (ControlCustomize item : list) {
            if (packageName.equals(item.getPackageName())) {
                listAllowed.remove(item);
                break;
            }
        }
        return listAllowed;
    }

    public static ArrayList<AppInstallModel> updatePackageRemoveApp(String packageName, List<AppInstallModel> list) {
        ArrayList<AppInstallModel> listAllowed = new ArrayList<>(list);
        for (AppInstallModel item : list) {
            if (packageName.equals(item.getPackageName())) {
                listAllowed.remove(item);
                break;
            }
        }
        return listAllowed;
    }

    public static Single<Bitmap> getBitmapControl(Context context, String path, boolean isAssets) {
        return Single.fromCallable(() -> {
            Bitmap bitmap = null;
            if (isAssets) {
                try (InputStream is = context.getAssets().open(path.replace("file:///android_asset/", ""))) {
                    bitmap = BitmapFactory.decodeStream(is);
                } catch (IOException e) {
                    Timber.e(e, "Error loading image from assets");
                }
            } else {
                File imgFile = new File(path);
                if (imgFile.exists()) {
                    bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                }
            }
            return bitmap;
        });
    }


    public static List<ItemTurnOn> updatePackageRemoveAllowedAppAuto(String packageName, List<ItemTurnOn> list) {
        List<ItemTurnOn> listTurnOn = new ArrayList<>(list);
        for (ItemTurnOn item : list) {
            if (packageName.equals(item.getPackageName())) {
                listTurnOn.remove(item);
                break;
            }
        }
        return listTurnOn;
    }

    public static String getNameAppFromPackage(String packageName) {
        PackageManager pm = App.mContext.getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            ai = null;
        }
        if (ai != null) {
            return (String) pm.getApplicationLabel(ai);
        } else {
            return "(unknown)";
        }
    }

    public static String getForegroundPackage(UsageStatsManager usageStatsManager) {
        String packageName = null;
        final long INTERVAL = 1000 * 60;
        final long end = System.currentTimeMillis();
        final long begin = end - INTERVAL;
        final UsageEvents usageEvents = usageStatsManager.queryEvents(begin, end);
        while (usageEvents.hasNextEvent()) {
            UsageEvents.Event event = new UsageEvents.Event();
            usageEvents.getNextEvent(event);
            switch (event.getEventType()) {
                case UsageEvents.Event.MOVE_TO_FOREGROUND:
                    packageName = event.getPackageName();
                    break;
                case UsageEvents.Event.MOVE_TO_BACKGROUND:
                    if (event.getPackageName().equals(packageName)) {
                        packageName = null;
                    }
            }
        }
        return packageName;
    }


    public static String getDefaultSmsAppPackageName(Context context) {
        try {
            return Telephony.Sms.getDefaultSmsPackage(context);
        } catch (final Throwable e) {
            Timber.e("hachung Throwable:"+e);
        }
        final Intent intent = new Intent(Intent.ACTION_VIEW)
                .addCategory(Intent.CATEGORY_DEFAULT).setType("vnd.android-dir/mms-sms");
        final List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(intent, 0);
        if (!resolveInfoList.isEmpty())
            return resolveInfoList.get(0).activityInfo.packageName;
        return "";
    }

    public static boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    public static void safeDelay(long delayMillis, final Runnable action) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    action.run();
                } catch (Exception ignored) {}
            }
        }, delayMillis);
    }

    public static void safeDelay(Runnable action) {
        safeDelay(0, action);
    }

}

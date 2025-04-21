package com.tapbi.spark.controlcenter.utils;

import android.Manifest;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.graphics.PixelFormat;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.AlarmClock;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.common.models.EventCustomEdge;
import com.tapbi.spark.controlcenter.feature.controlios14.manager.AudioManagerUtils;
import com.tapbi.spark.controlcenter.service.NotificationListener;
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614;
import com.tapbi.spark.controlcenter.ui.RequestPermissionActivity;
import com.tapbi.spark.controlcenter.ui.choosemusic.ChooseMusicPlayerActivity;
import com.tapbi.spark.controlcenter.ui.main.edgetriggers.SettingTouchFragment;
import com.tapbi.spark.controlcenter.ui.splash.SplashActivity;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


public class SettingUtils {

    // AIR PLANE SETTING
    public static boolean isAirplaneModeOn(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }

    public static void intentChangeAirPlane(Context context) {
        try {
            Intent i = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
            i.putExtra(":android:show_fragment", "com.android.settings.AirplaneModeSettings");
            i.putExtra(":android:no_headers", true);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void intentSetting(Context context) {
        try {
            Intent i = new Intent(Settings.ACTION_SETTINGS);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void intentChangeDataMobile(Context context) {
        try {
            Intent i = new Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void intentChangeWifi(Context context) {
        try {
//            Intent i = new Intent(Settings.ACTION_WIFI_SETTINGS);
            Intent i = new Intent(Settings.Panel.ACTION_WIFI);
//            Intent i = new Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void intentChangeBlueTooth(Context context) {
        try {
            Intent i = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void intentChangeSync(Context context) {
        try {
            Intent i = new Intent(Settings.ACTION_SYNC_SETTINGS);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void intentChangeLocation(Context context) {
        try {
            Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void intentChangeDisplay(Context context) {
        try {
            Intent i = new Intent(Settings.ACTION_DISPLAY_SETTINGS);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void intentChangeBatterySaver(Context context) {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                Intent i = new Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void intentChangeHostPost(Context context) {
        try {
            final Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            final ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.TetherSettings");
            intent.setComponent(cn);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Boolean isPowerSaveMode(Context context) {
        if (Build.MANUFACTURER.equalsIgnoreCase("Huawei")) {
            return isPowerSaveModeHuawei(context);
        } else if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
            return isPowerSaveModeXiaomi(context);
        } else {
            return isPowerSaveModeAndroid(context);
        }
    }

    private static Boolean isPowerSaveModeAndroid(Context context) {
        boolean isPowerSaveMode = false;
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (pm != null) isPowerSaveMode = pm.isPowerSaveMode();
        return isPowerSaveMode;
    }

    private static Boolean isPowerSaveModeHuawei(Context context) {
        try {
            int value = android.provider.Settings.System.getInt(context.getContentResolver(), "SmartModeStatus");
            return (value == 4);
        } catch (Settings.SettingNotFoundException e) {
            // Setting not found?  Return standard android mechanism and hope for the best...
            return isPowerSaveModeAndroid(context);
        }
    }

    private static Boolean isPowerSaveModeXiaomi(Context context) {
        try {
            int value = android.provider.Settings.System.getInt(context.getContentResolver(), "POWER_SAVE_MODE_OPEN");
            return (value == 1);
        } catch (Settings.SettingNotFoundException e) {
            // Setting not found?  Return standard android mechanism and hope for the best...
            return isPowerSaveModeAndroid(context);
        }
    }


    public static String getGSM(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null && tm.getSimOperatorName() != null && !tm.getSimOperatorName().equals("")) {
            return tm.getSimOperatorName();
        }
        return context.getString(R.string.no_sim);
    }

    public static boolean hasSimCard(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm != null && tm.getSimOperatorName() != null && !tm.getSimOperatorName().equals("");
    }

    public static List<String> getNetworkOperator(final Context context) {
        // Get System TELEPHONY service reference
        List<String> carrierNames = new ArrayList<>();
        try {
            final String permission = Manifest.permission.READ_PHONE_STATE;
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) && (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED)) {
                final List<SubscriptionInfo> subscriptionInfos;
                subscriptionInfos = SubscriptionManager.from(context).getActiveSubscriptionInfoList();
                if (subscriptionInfos != null) {
                    for (int i = 0; i < subscriptionInfos.size(); i++) {
                        carrierNames.add(subscriptionInfos.get(i).getDisplayName().toString());
                    }
                }
            } else {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                // Get carrier name (Network Operator Name)
                carrierNames.add(telephonyManager.getNetworkOperatorName());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return carrierNames;
    }

    // DATA SETTING
    public static void intentSettingData(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
            intent.setClassName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static boolean isDataEnable(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null) {
            if (!wifiManager.isWifiEnabled() && networkInfo.isAvailable()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    // WIFI SETTING
    public static void settingWifi(Context context) {
        try {
            WifiManager wifiManager;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                wifiManager = context.getSystemService(WifiManager.class);
            } else {
                wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            }
            wifiManager.setWifiEnabled(!wifiManager.isWifiEnabled());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getNetworkType(Context context, TelephonyManager manager) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            int networkType = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                networkType = manager.getDataNetworkType();
            } else {
                networkType = manager.getNetworkType();
            }
//        Timber.e("networkType: " + networkType);
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return "2G";
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return "3G";
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return "LTE";
                case 20:
                    //TelephonyManager.NETWORK_TYPE_LTE_CA
                    return "5G";
                default:
                    return "";
            }
        }
        return "";
    }

    public static boolean isEnableWifi(Context context) {
//        Timber.e("hachung : isEnableWifi");
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    // BLUETOOTH SETTING
    public static void setOnOffBluetooth(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            SettingUtils.intentActivityRequestPermission(context, new String[]{Manifest.permission.BLUETOOTH_CONNECT});
            NotyControlCenterServicev614.getInstance().closeNotyCenter();
            return;
        }
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                changeBluetooth(context, mBluetoothAdapter);
            } else {
                try {
                    if (!mBluetoothAdapter.isEnabled()) {
                        mBluetoothAdapter.enable();
                    } else {
                        mBluetoothAdapter.disable();
                    }
                } catch (Exception e) {
                    changeBluetooth(context, mBluetoothAdapter);
                    NotyControlCenterServicev614.getInstance().closeNotyCenter();
                }

            }
        }
    }

    @SuppressLint("MissingPermission")
    private static void changeBluetooth(Context context, BluetoothAdapter mBluetoothAdapter) {
        if (!mBluetoothAdapter.isEnabled()) {
            try {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                Intent intent = new Intent("android.bluetooth.adapter.action.REQUEST_DISABLE");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void setOnBluetoothApi33(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            SettingUtils.intentActivityRequestPermission(context, new String[]{Manifest.permission.BLUETOOTH_CONNECT});
            NotyControlCenterServicev614.getInstance().closeNotyCenter();
            return;
        }

        try {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setOffBluetoothApi33(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            SettingUtils.intentActivityRequestPermission(context, new String[]{Manifest.permission.BLUETOOTH_CONNECT});
            NotyControlCenterServicev614.getInstance().closeNotyCenter();
            return;
        }
        try {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Intent intent = new Intent("android.bluetooth.adapter.action.REQUEST_DISABLE");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean isEnableBluetooth(Context context) {
        PackageManager pm = context.getPackageManager();
        boolean hasBluetooth = pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (hasBluetooth && mBluetoothAdapter != null) {
            return mBluetoothAdapter.isEnabled();
        }
        return false;
    }

    // ROTATE SETTING
    public static void settingRotate(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.System.canWrite(context)) {
                    return;
                }
            }
            int result = Settings.System.getInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);
            if (result == 0) {
                Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 1);
            } else if (result == 1) {
                Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isRotate(Context context) {
        try {
            int result = Settings.System.getInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);
            if (result == 0) {
                return true;
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }

    // SILIENT SETTING
    public static void settingSilient(Context context) {
        AudioManagerUtils.getInstance(context).settingSilient();
    }

    public static int getRingerMode(Context context) {
        return AudioManagerUtils.getInstance(context).getRingerMode();
    }

    // DO NOT DISTURB
    public static void settingDoNotDisturb(Context context) {
        try {
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                if (!mNotificationManager.isNotificationPolicyAccessGranted()) {

                    Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else {
                    int type = mNotificationManager.getCurrentInterruptionFilter();
                    if (type == NotificationManager.INTERRUPTION_FILTER_ALL) {
                        mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALARMS);
                    } else {
                        mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //  OPEN CAMERA
    public static void openCamera(Context context) {
        try {
            Intent intent = new Intent("android.media.action.STILL_IMAGE_CAMERA");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // OPEN CLOCK
    public static void openClock(Context context) {
        try {
            Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //location
    public static boolean isLocationTurnOn(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled;

        try {
            gps_enabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            return gps_enabled;
        } catch (Exception ex) {
            return false;
        }
    }

    public static void intentSettingLocation(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            context.startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //intent
    public static void intentPermissionWriteSetting(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            try {
                context.startActivity(intent);
            } catch (Exception e) {
                Timber.d(e);
            }

        }
    }

    public static void intentActivityRequestPermission(Context context, String[] permission) {
        try {
            Intent intent = new Intent(context, RequestPermissionActivity.class);
            intent.putExtra(RequestPermissionActivity.PERMISSION, permission);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Timber.d(e);
        }
    }

    public static void intentActivityRequestPermissionRealTimeBackGround(Context context, String[] permission) {
        try {
            Intent intent = new Intent(context, RequestPermissionActivity.class);
            intent.putExtra(RequestPermissionActivity.TYPE_REALTIME_BG, true);
            intent.putExtra(RequestPermissionActivity.PERMISSION, permission);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Timber.d(e);
        }
    }

    public static void intentOtherApp(Context context, String pka) {
        try {
            PackageManager pm = context.getPackageManager();
            Intent launchIntent = pm.getLaunchIntentForPackage(pka);
            if (launchIntent != null) {
                launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(launchIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Sync
    public static void setSyncAutomatically() {
        try {
            boolean b = ContentResolver.getMasterSyncAutomatically();
            ContentResolver.setMasterSyncAutomatically(!b);
        } catch (Exception e) {
            Timber.e("hachung ew"+e);
        }
    }

    public static boolean isSyncAutomaticallyEnable() {
        try {
            return ContentResolver.getMasterSyncAutomatically();
//            return isDataSaverEnabled(App.ins);
        } catch (Exception e) {
            Timber.e(e);
            return false;
        }
    }
    public static boolean checkIfLocationOpened() {
        final LocationManager manager = (LocationManager) App.ins.getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) || manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            return true;
        }
        return false;
    }

    public static boolean setDataSaver(Context context){
        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            intent = new Intent(Settings.ACTION_DATA_USAGE_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
                return true;
            }
        }
       EventBus.getDefault().post(new EventCustomEdge(Constant.EVENT_DATA_SAVER_NOT_SUPPORT, SettingTouchFragment.TabEdge.LEFT, 0));
        return false;
    }




//    public static boolean checkPermissionNotificationListener(Context context) {
//        ContentResolver contentResolver = context.getContentResolver();
//        String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
//        String packageName = context.getPackageName();
//        if (enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName)) {
//            return false;
//        } else {
//            return true;
//        }
//    }

    @SuppressLint("WrongConstant")
    public static boolean checkPermissionNotificationListener(Context context) {
        try {
            ComponentName componentName = new ComponentName(context.getPackageName(), NotificationListener.class.getName());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                if (context.getSystemService("notification") != null) {
                    return ((NotificationManager) context.getSystemService("notification")).isNotificationListenerAccessGranted(componentName);
                }
            }
            String string = Settings.Secure.getString(context.getContentResolver(), "enabled_notification_listeners");
            return string != null && string.contains(componentName.flattenToString());
        } catch (Exception e) {
            return false;
        }
    }


//    public static boolean checkPermissionNotificationListener(Context context) {
//        ContentResolver contentResolver = context.getContentResolver();
//        String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
//        String packageName = context.getPackageName();
//        if (enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName)) {
//            return false;
//        } else {
//            return true;
//        }
//    }

    //    public static boolean checkPermissionOverlay(Context context) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            return Settings.canDrawOverlays(context);
//        }
//        return true;
//    }
    public static boolean checkPermissionOverlay(Context context) {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
            else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                return Settings.canDrawOverlays(context);
            } else {
                if (Settings.canDrawOverlays(context)) return true;
                try {
                    WindowManager mgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                    if (mgr == null) return false; //getSystemService might return null
                    View viewToAdd = new View(context);
                    WindowManager.LayoutParams params = new WindowManager.LayoutParams(0, 0, android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
                    viewToAdd.setLayoutParams(params);
                    ExtensionsKt.addLayout(mgr, viewToAdd, params);
                    ExtensionsKt.removeLayout(mgr, viewToAdd);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isAccessibilitySettingsOn(Context mContext) {
        AccessibilityManager am = (AccessibilityManager) mContext.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);
        for (AccessibilityServiceInfo enabledService : enabledServices) {
            if (enabledService == null) {
                continue;
            }
            ServiceInfo enabledServiceInfo = enabledService.getResolveInfo().serviceInfo;
            if (enabledServiceInfo.packageName.equals(mContext.getPackageName()) && enabledServiceInfo.name.equals(NotyControlCenterServicev614.class.getName()))
                return !NotyControlCenterServicev614.isErrorService;
        }
        return false;
    }

    public static void intentToPermissionActivity(Context context) {
        Toast.makeText(context, context.getText(R.string.text_detail_when_permission_denied), Toast.LENGTH_SHORT).show();
        if (NotyControlCenterServicev614.getInstance() != null) {
            NotyControlCenterServicev614.getInstance().closeNotyCenter();
        }
        Intent intent = new Intent(context, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    public static void intentToChooseMusicActivity(Context context) {
        try {
            Intent intent = new Intent(context, ChooseMusicPlayerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Timber.d(e);
        }
    }

    public static void expandNotificationsPanel(Context context) {
        try {
            @SuppressLint("WrongConstant") Object sbservice = context.getSystemService("statusbar");
            Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
            Method showsb;
            showsb = statusbarManager.getMethod("expandSettingsPanel");
            showsb.invoke(sbservice);
        } catch (Exception exception) {
            Timber.e(exception);
        }
    }

    public static void collapseNotificationsPanel(Context context) {
        try {
            @SuppressLint("WrongConstant") Object sbservice = context.getSystemService("statusbar");
            Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
            Method showsb;
            showsb = statusbarManager.getMethod("collapsePanels");
            showsb.invoke(sbservice);
        } catch (Exception exception) {
            //Timber.e(exception);
        }
    }

    public static int getMaxBrightness(Context context) {
        int defaultValue = 255;
        if (Build.MODEL.equals("RMX3690")) { // fix max tren realme C30s
            return Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 255);
        } else {
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (powerManager != null) {
                Field[] fields = powerManager.getClass().getDeclaredFields();
                for (Field field : fields) {
                    if (field.getName().equals("BRIGHTNESS_ON")) {
                        field.setAccessible(true);
                        try {
                            return (int) field.get(powerManager);
                        } catch (IllegalAccessException e) {
                            return defaultValue;
                        }
                    }
                }
            }
            return defaultValue;
        }
    }



    public static void setValueBrightness(Context context, int valueBrightness) {
        try {
            Timber.e("hachung valueBrightness:"+valueBrightness);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.System.canWrite(context)) {
                    changeBrightness(context, valueBrightness);
                }
            } else {
                changeBrightness(context, valueBrightness);
            }

        } catch (Exception e) {
            Timber.e(e);
        }

    }

    private static void changeBrightness(Context context, int valueBrightness) {
        setModeBrightness(context, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, valueBrightness);
    }

    public static void setModeBrightness(Context context, int valueBrightness) {
        try {
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, valueBrightness);
        } catch (Exception ignored) {
        }
    }

    public static int getModeBrightness(Context context) throws Settings.SettingNotFoundException {
        try {
            return Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
        } catch (Exception e) {
            Timber.e("hachung e: " + e);
            return Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;
        }
    }

    public static int getValueBrightness(Context context) {
        try {
            return Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void intentPermissionNotificationListener(Context context) {
        Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        String str = context.getPackageName() + "/" + NotificationListener.class.getName();
        bundle.putString(":settings:fragment_args_key", str);
        intent.putExtra(":settings:fragment_args_key", str);
        intent.putExtra(":settings:show_fragment_args", bundle);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Timber.e("NVQ " + e);
            e.printStackTrace();
        }
    }

    public static boolean isDataSaverEnabled(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            int status = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                status = connectivityManager.getRestrictBackgroundStatus();
            }

            return status == ConnectivityManager.RESTRICT_BACKGROUND_STATUS_ENABLED;

        }
        return false;
    }
}

package com.tapbi.spark.controlcenter.utils;

import android.content.res.Resources;
import android.os.Build;
import android.provider.Settings;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.feature.controlios14.model.InfoSystem;

import java.util.ArrayList;
import java.util.List;

public class StringAction {
    public List<InfoSystem> addString(Resources res) {
        List<InfoSystem> infoSystems = new ArrayList<>();
        infoSystems.add(new InfoSystem(Constant.STRING_ACTION_DATA_MOBILE, "", Settings.ACTION_DATA_ROAMING_SETTINGS, R.drawable.ic_mi_mobile_data));
        infoSystems.add(new InfoSystem(Constant.STRING_ACTION_WIFI, "", Settings.ACTION_WIFI_SETTINGS, R.drawable.ic_mi_wifi));
        infoSystems.add(new InfoSystem(Constant.STRING_ACTION_BLUETOOTH, "", Settings.ACTION_BLUETOOTH_SETTINGS, R.drawable.ic_mi_bluetooth));
        infoSystems.add(new InfoSystem(Constant.STRING_ACTION_FLASH_LIGHT, "", "", R.drawable.ic_mi_flash));

        infoSystems.add(new InfoSystem(Constant.STRING_ACTION_SOUND, "", Settings.ACTION_SOUND_SETTINGS, R.drawable.ic_mi_sounds));
        infoSystems.add(new InfoSystem(Constant.STRING_ACTION_AIRPLANE_MODE, "", Settings.ACTION_AIRPLANE_MODE_SETTINGS, R.drawable.ic_mi_airplane));
        infoSystems.add(new InfoSystem(Constant.STRING_ACTION_DO_NOT_DISTURB, "", Settings.ACTION_SOUND_SETTINGS, R.drawable.ic_mi_do_no_disturb));
        infoSystems.add(new InfoSystem(Constant.STRING_ACTION_LOCATION, "", Settings.ACTION_LOCATION_SOURCE_SETTINGS, R.drawable.ic_mi_location));
        infoSystems.add(new InfoSystem(Constant.STRING_ACTION_AUTO_ROTATE, "", Settings.ACTION_DISPLAY_SETTINGS, R.drawable.ic_mi_rotate_mishade));

        //infoSystems.add(new InfoSystem(Constant.STRING_ACTION_NIGHT_LIGHT, "", "", R.drawable.ic_mi_night_light));
        infoSystems.add(new InfoSystem(Constant.DARK_MODE, "", Settings.ACTION_DISPLAY_SETTINGS, R.drawable.ic_mi_dark_theme));

        infoSystems.add(new InfoSystem(Constant.STRING_ACTION_HOST_POST, "", "", R.drawable.ic_mi_host_post));
        infoSystems.add(new InfoSystem(Constant.STRING_ACTION_SCREEN_CAST, "", Settings.ACTION_CAST_SETTINGS, R.drawable.ic_mi_screen));
        infoSystems.add(new InfoSystem(Constant.STRING_ACTION_OPEN_SYSTEM, "", Settings.ACTION_SETTINGS, R.drawable.ic_mi_open_system));

//    infoSystems.add(new InfoSystem(res.getString(R.string.screen_shot),"Screen Shot", "", "android.settings.CAST_SETTINGS", R.drawable.ic_mi_screen_short));
//    infoSystems.add(new InfoSystem(res.getString(R.string.screen_recoding),"Screen Recoding", "", "android.settings.CAST_SETTINGS", R.drawable.ic_mi_recoding));

        infoSystems.add(new InfoSystem(Constant.STRING_ACTION_SYNC, "", Settings.ACTION_SYNC_SETTINGS, R.drawable.ic_mi_sync));

//    infoSystems.add(new InfoSystem(res.getString(R.string.screen_time),"Screen time", "", "android.provider.Settings.ACTION_DATE_SETTINGS", R.drawable.ic_mi_screnn_time));
//    infoSystems.add(new InfoSystem(res.getString(R.string.screen_lock),"Screen lock", "", "android.provider.Settings.ACTION_DATE_SETTINGS", R.drawable.ic_mi_screen_lock));
        infoSystems.add(new InfoSystem(Constant.STRING_ACTION_BATTERY, "", (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) ? Settings.ACTION_BATTERY_SAVER_SETTINGS : "", R.drawable.ic_mi_battery));
        infoSystems.add(new InfoSystem(Constant.STRING_ACTION_CLOCK, "", "", R.drawable.ic_mi_clock));
        infoSystems.add(new InfoSystem(Constant.STRING_ACTION_CAMERA, "", "", R.drawable.ic_mi_camera));
        infoSystems.add(new InfoSystem(Constant.STRING_ACTION_KEYBOARD_PICKER, "", "", R.drawable.ic_mi_keyboard));

//    infoSystems.add(new InfoSystem(res.getString(R.string.dark_mode),"Dark mode", "", "android.provider.Settings.ACTION_DATE_SETTINGS", R.drawable.ic_mi_dark_mode));
//    infoSystems.add(new InfoSystem(res.getString(R.string.nearby_share),"Nearby Share", "", "android.provider.Settings.ACTION_DATE_SETTINGS", R.drawable.ic_mi_nearby_share));
//    infoSystems.add(new InfoSystem(res.getString(R.string.call_text_on_other_device),"Call & Text On Other Device", "", "android.provider.Settings.ACTION_DATE_SETTINGS", R.drawable.ic_mi_call));

//    infoSystems.add(new InfoSystem(res.getString(R.string.night_mode),"Night Mode", "", "com.android.settings.Settings$DataUsageSummaryActivity", R.drawable.ic_mi_night_mode));
//    infoSystems.add(new InfoSystem(res.getString(R.string.screen_shot),"Screen Short", "", "android.settings.CAST_SETTINGS", R.drawable.ic_mi_screen_short));
//    infoSystems.add(new InfoSystem(res.getString(R.string.time_screen),"Time screen", "", "android.provider.Settings.ACTION_DATE_SETTINGS", R.drawable.ic_mi_time_screen));
//    infoSystems.add(new InfoSystem(res.getString(R.string.focus_mode),"Focus mode", "", "android.provider.Settings.ACTION_DATE_SETTINGS", R.drawable.ic_mi_focus_mode));
//    infoSystems.add(new InfoSystem(res.getString(R.string.kids_home),"Kids Home", "", "android.provider.Settings.ACTION_DATE_SETTINGS", R.drawable.ic_mi_kids_home));
//    infoSystems.add(new InfoSystem(res.getString(R.string.power_mode),"Power Mode", "", "android.provider.Settings.ACTION_DATE_SETTINGS", R.drawable.ic_mi_power_mode));
//    infoSystems.add(new InfoSystem(res.getString(R.string.smart_view),"Smart View", "", "android.provider.Settings.ACTION_DATE_SETTINGS", R.drawable.ic_mi_smart_view));
//    infoSystems.add(new InfoSystem(res.getString(R.string.secure_folder),"Secure Folder", "", "android.provider.Settings.ACTION_DATE_SETTINGS", R.drawable.ic_mi_secure_folder));
//    infoSystems.add(new InfoSystem(res.getString(R.string.auto_wallpaper),"Auto Wallpaper", "", "android.provider.Settings.ACTION_DATE_SETTINGS", R.drawable.ic_auto_wallpaper));
//    infoSystems.add(new InfoSystem(res.getString(R.string.night_mode),"Night Mode", "", "android.provider.Settings.ACTION_DATE_SETTINGS", R.drawable.ic_mi_night_mode));
        return infoSystems;
    }


    public int getIconAction(String action) {
        switch (action) {
            case Constant.STRING_ACTION_DATA_MOBILE:
                return R.drawable.ic_mi_mobile_data;
            case Constant.STRING_ACTION_WIFI:
                return R.drawable.ic_mi_wifi;
            case Constant.STRING_ACTION_BLUETOOTH:
                return R.drawable.ic_mi_bluetooth;

            case Constant.STRING_ACTION_FLASH_LIGHT:
                return R.drawable.ic_mi_flash;

            case Constant.STRING_ACTION_SOUND:
                return R.drawable.ic_mi_sounds;

            case Constant.STRING_ACTION_AIRPLANE_MODE:
                return R.drawable.ic_mi_airplane;

            case Constant.STRING_ACTION_DO_NOT_DISTURB:
                return R.drawable.ic_mi_do_no_disturb;

            case Constant.STRING_ACTION_LOCATION:
                return R.drawable.ic_mi_location;

            case Constant.STRING_ACTION_AUTO_ROTATE:
                return R.drawable.ic_mi_rotate;

            case Constant.STRING_ACTION_HOST_POST:
                return R.drawable.ic_mi_host_post;

            case Constant.STRING_ACTION_SCREEN_CAST:
                return R.drawable.ic_mi_screen;

            case Constant.STRING_ACTION_OPEN_SYSTEM:
                return R.drawable.ic_mi_open_system;

            case Constant.STRING_ACTION_SYNC:
                return R.drawable.ic_mi_sync;

            case Constant.STRING_ACTION_CLOCK:
                return R.drawable.ic_mi_clock;

            case Constant.STRING_ACTION_CAMERA:
                return R.drawable.ic_mi_camera;

            case Constant.STRING_ACTION_KEYBOARD_PICKER:
                return R.drawable.ic_mi_keyboard;

            case Constant.DARK_MODE:
                return R.drawable.ic_dark_mode;

            case Constant.STRING_ACTION_BATTERY:

            default:
                return R.drawable.ic_mi_battery;
        }
    }


}

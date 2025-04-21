package com.tapbi.spark.controlcenter.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.data.repository.ThemeHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ControlCustomizeManager {

    private static final String keyAppNote1 = "note";
    private static final String keyAppNote2 = "keep";
    private static final String keyAppVoiceNoteSamsung = "voicenote";
    //    public static String[] nameDefault = {Constand.FLASH_LIGHT, Constand.CLOCK, Constand.CALCULATOR, Constand.CAMERA, Constand.RECORD};
//    public static int[] iconDefault = {R.drawable.flashlight_off, R.drawable.clock, R.drawable.calculator, R.drawable.camera, R.drawable.record_circle_small};
    public static String[] nameDefault = {Constant.STRING_ACTION_FLASH_LIGHT, Constant.CLOCK, Constant.CALCULATOR, Constant.CAMERA, Constant.RECORD, Constant.DARK_MODE, Constant.STRING_ACTION_BATTERY, Constant.KEY_CONTROL_ROTATE, Constant.KEY_CONTROL_SCREEN_TIME_OUT};
    public static Drawable[] iconDefault = {getIcon(R.drawable.flashlight_off), getIcon(R.drawable.clock), getIcon(R.drawable.calculator), getIcon(R.drawable.camera), getIcon(R.drawable.record_icon), getIcon(R.drawable.ic_dark_mode), getIcon(R.drawable.ic_low_power_mode), getIcon(R.drawable.ic_rotate_ios), getIcon(R.drawable.ic_screen_time_out)};
    public static String[] customControl;
    public static String packageAppNote = "";
    private static ControlCustomizeManager instance;
    private static TinyDB tinyDB;

    public static ControlCustomizeManager getInstance(Context context) {
        if (instance == null) {
            instance = new ControlCustomizeManager();
            tinyDB = App.tinyDB;

            //check device have app note
            boolean haveAppNote = getActionNote(context);
            if (haveAppNote) {
                nameDefault = new String[]{Constant.STRING_ACTION_FLASH_LIGHT, Constant.CLOCK, Constant.CALCULATOR, Constant.CAMERA, Constant.RECORD, Constant.DARK_MODE, Constant.STRING_ACTION_BATTERY, Constant.NOTE, Constant.KEY_CONTROL_ROTATE, Constant.KEY_CONTROL_SCREEN_TIME_OUT};
                iconDefault = new Drawable[]{getIcon(R.drawable.flashlight_off), getIcon(R.drawable.clock), getIcon(R.drawable.calculator), getIcon(R.drawable.camera), getIcon(R.drawable.record_icon), getIcon(R.drawable.ic_dark_mode), getIcon(R.drawable.ic_low_power_mode), getIcon(R.drawable.ic_note), getIcon(R.drawable.ic_rotate_ios), getIcon(R.drawable.ic_screen_time_out)};
            }
        }
        return instance;
    }

    public static Drawable getIcon(int id) {
        return ContextCompat.getDrawable(App.mContext, id);
    }

    public static boolean getActionNote(Context context) {
        packageAppNote = "";
        boolean haveAppNote = false;
        PackageManager manager = context.getPackageManager();
        List<String> listAppNote = new ArrayList<>();

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> apps = manager.queryIntentActivities(intent, PackageManager.GET_META_DATA);
        for (ResolveInfo app : apps) {
            String packageName = app.activityInfo.packageName;
            if (isValidAppNotePackage(packageName, manager)) {
                haveAppNote = true;
                packageAppNote = packageName;
                listAppNote.add(packageName);
            }
        }

        for (String appNote : listAppNote) {
            if (appNote.contains(keyAppNote1)) {
                packageAppNote = appNote;
                break;
            }
        }

        return haveAppNote;
    }

    private static boolean isValidAppNotePackage(String packageName, PackageManager manager) {
        return (packageName != null && (packageName.contains(keyAppNote1) || packageName.contains(keyAppNote2))) && !packageName.contains(keyAppVoiceNoteSamsung) && manager.getLaunchIntentForPackage(packageName) != null;
    }

    public String[] getListControlsSave() {
        if (customControl == null) {
            String s = tinyDB.getString(Constant.CONTROL_CUSTOMIZE_SAVE);
            if (s.isEmpty()) {
                StringBuilder nameApp = new StringBuilder();
                for (int i = 0; i < nameDefault.length; i++) {
                    if (i == 0) {
                        nameApp = new StringBuilder(nameDefault[i]);
                    } else {
                        nameApp.append(",").append(nameDefault[i]);
                    }
                }
                tinyDB.putString(Constant.CONTROL_CUSTOMIZE_SAVE, nameApp.toString());
                customControl = nameApp.toString().split(",");
                //Timber.e("hoangld nameApp: " + nameApp);
            } else {
                customControl = s.split(",");
                //Timber.e("hoangld s: " + s);
            }
        }
        customControl = checkAppInstall();
        return customControl;
    }

    private String[] checkAppInstall() {
        PackageManager packageManager = App.ins.getPackageManager();
        List<String> list = new ArrayList<>(Arrays.asList(customControl));
        for (String name : customControl) {
            switch (name) {
                case Constant.STRING_ACTION_FLASH_LIGHT:
                case Constant.CLOCK:
                case Constant.CALCULATOR:
                case Constant.CAMERA:
                case Constant.RECORD:
                case Constant.DARK_MODE:
                case Constant.STRING_ACTION_BATTERY:
                case Constant.NOTE:
                    break;
                default:
                    if (!name.isEmpty()) {
                        boolean isInstall = applicationIsInstall(packageManager, name);
                        if (!isInstall) {
                            list.remove(name);
                        }
                    }
                    break;
            }
        }
        return list.toArray(new String[0]);
    }

    public void saveCustomControl(String[] s) {
        customControl = s;
        StringBuilder sss = new StringBuilder();
        for (String value : customControl) {
            sss.append(value).append(",");
        }
        tinyDB.putString(Constant.CONTROL_CUSTOMIZE_SAVE, sss.toString());
    }

    public void replaceCustomControl(List<String> s, String appName) {
        StringBuilder nameApp = new StringBuilder();
        boolean isFirst = false;
        for (String value : s) {
            if (!value.equals(appName)) {
                if (!isFirst) {
                    isFirst = true;
                    nameApp = new StringBuilder(value);
                } else {
                    nameApp.append(",").append(value);
                }

            }
        }
        customControl = nameApp.toString().split(",");
        tinyDB.putString(Constant.CONTROL_CUSTOMIZE_SAVE, nameApp.toString());
    }

    public String getNameActionToShow(Resources res, String nameDefault) {
        switch (nameDefault) {
            case Constant.KEY_CONTROL_FLASH:
                return res.getString(R.string.flash_light);
            case Constant.KEY_CONTROL_ALARM:
                return res.getString(R.string.clock);
            case Constant.KEY_CONTROL_CALCULATOR:
                return res.getString(R.string.calculator);
            case Constant.KEY_CONTROL_CAMERA:
                return res.getString(R.string.camera);
            case Constant.KEY_CONTROL_RECORD:
                return res.getString(R.string.screen_recoding);
            case Constant.KEY_CONTROL_DARKMODE:
                return res.getString(R.string.dark_mode);
            case Constant.KEY_CONTROL_PIN:
                return res.getString(R.string.low_power_mode);
            case Constant.KEY_CONTROL_NOTE:
                return res.getString(R.string.notes);
            case Constant.KEY_CONTROL_ROTATE:
                return res.getString(R.string.lock_rotation);
            case Constant.KEY_CONTROL_SCREEN_TIME_OUT:
                return res.getString(R.string.time_screen);
            case Constant.KEY_CONTROL_SILENT:
                return res.getString(R.string.do_nit_disturb);
            default:
                return nameDefault;
        }
    }

    public ArrayList<String>  getFullActionToShow() {
        ArrayList<String> controlNotAdd = new ArrayList<>();
        controlNotAdd.add(Constant.KEY_CONTROL_FLASH);
        controlNotAdd.add(Constant.KEY_CONTROL_ALARM);
        controlNotAdd.add(Constant.KEY_CONTROL_CALCULATOR);
        controlNotAdd.add(Constant.KEY_CONTROL_CAMERA);
        controlNotAdd.add(Constant.KEY_CONTROL_RECORD);
        controlNotAdd.add(Constant.KEY_CONTROL_DARKMODE);
        controlNotAdd.add(Constant.KEY_CONTROL_PIN);
        controlNotAdd.add(Constant.KEY_CONTROL_NOTE);
        controlNotAdd.add(Constant.KEY_CONTROL_ROTATE);
        controlNotAdd.add(Constant.KEY_CONTROL_SCREEN_TIME_OUT);
        controlNotAdd.add(Constant.KEY_CONTROL_SILENT);
        return controlNotAdd;
    }

    public boolean applicationIsInstall(PackageManager manager, String packageName) {
        try {
            manager.getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

}

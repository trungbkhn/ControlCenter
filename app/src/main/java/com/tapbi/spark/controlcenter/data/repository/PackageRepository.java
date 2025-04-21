package com.tapbi.spark.controlcenter.data.repository;

import static kotlinx.coroutines.flow.FlowKt.subscribeOn;

import android.annotation.SuppressLint;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.common.models.CustomizeControlApp;
import com.tapbi.spark.controlcenter.feature.controlios14.model.AppInstallModel;
import com.tapbi.spark.controlcenter.feature.controlios14.model.ControlCustomize;
import com.tapbi.spark.controlcenter.feature.controlios14.model.MusicPlayer;
import com.tapbi.spark.controlcenter.utils.ControlCustomizeManager;
import com.tapbi.spark.controlcenter.utils.MethodUtils;
import com.tapbi.spark.controlcenter.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

public class PackageRepository {
    private Comparator<UsageStats> usageStatsComparator = new Comparator<UsageStats>() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public int compare(UsageStats o1, UsageStats o2) {
            if (o1.getLastTimeUsed() > o1.getLastTimeUsed()) {
                return 1;
            } else if (o1.getLastTimeUsed() < o2.getLastTimeUsed()) {
                return -1;
            } else if (o1.getLastTimeUsed() == o2.getLastTimeUsed()) {
                return 0;
            }
            return 0;
        }
    };

    @Inject
    public PackageRepository() {

    }


    public Single<CustomizeControlApp> getListAppCustomizeRx(Context context, String[] customControlCurrent) {
        return Single.fromCallable(() -> getListAppCustomize(context, customControlCurrent)).subscribeOn(Schedulers.io());
    }
    public Single<CustomizeControlApp> getListAppCustomizeEditRx(Context context, String[] customControlCurrent) {
        return Single.fromCallable(() -> getListAppCustomizeEdit(context, customControlCurrent)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<ArrayList<MusicPlayer>> getListAppMusicPlayerRx(Context context) {
        return Single.fromCallable(() -> getListAppMusicPlayer(context)).subscribeOn(Schedulers.io());
    }

    public Single<ArrayList<AppInstallModel>> getListAppRecentRx(Context context, ArrayList<AppInstallModel> allApp) {
        return Single.fromCallable(() -> getListAppRecent(context, allApp)).subscribeOn(Schedulers.io());
    }

    public Single<ArrayList<AppInstallModel>> getListAllAppInstallRx(Context context) {
        return Single.fromCallable(() -> getListAppInstallModels(context)).subscribeOn(Schedulers.io());
    }


    private CustomizeControlApp getListAppCustomize(Context context, String[] customControlCurrent) {
        ArrayList<ControlCustomize> listCustomizeCurrentApp = new ArrayList<>();
        ArrayList<ControlCustomize> listExceptCurrentApp = new ArrayList<>();

        PackageManager manager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> applicationInfos = manager.queryIntentActivities(intent, PackageManager.GET_META_DATA);

        HashSet<String> customControlCurrentSet = new HashSet<>(Arrays.asList(customControlCurrent));


        ControlCustomizeManager customizeManager = ControlCustomizeManager.getInstance(context);
        Resources res = context.getResources();

        for (String controlName : customControlCurrent) {
            Timber.e("NVQ : getListAppCustomize" + controlName);
            String nameToShow = customizeManager.getNameActionToShow(res, controlName);
            ControlCustomize controlCustomize = new ControlCustomize(1, nameToShow, Utils.getIconShow(controlName,false), controlName);
            listCustomizeCurrentApp.add(controlCustomize);
        }

        for (int i = 0; i < applicationInfos.size(); i++) {
            ResolveInfo appInfo = applicationInfos.get(i);
            String packageName = appInfo.activityInfo.packageName;
            if (i > 0 && applicationInfos.get(i - 1).activityInfo.packageName.equals(packageName)) {
                continue;
            }
            if (manager.getLaunchIntentForPackage(packageName) != null && !customControlCurrentSet.contains(packageName)) {
                String appName = manager.getApplicationLabel(appInfo.activityInfo.applicationInfo).toString();
                ControlCustomize controlCustomize = new ControlCustomize(0, appName, MethodUtils.getIconFromPackageName(context, packageName), packageName);
                listExceptCurrentApp.add(controlCustomize);
            }
        }

        return new CustomizeControlApp(listCustomizeCurrentApp, listExceptCurrentApp);
    }
    private CustomizeControlApp getListAppCustomizeEdit(Context context, String[] customControlCurrent) {
        ArrayList<ControlCustomize> listCustomizeCurrentApp = new ArrayList<>();
        ArrayList<ControlCustomize> listExceptCurrentApp = new ArrayList<>();

        PackageManager manager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> applicationInfos = manager.queryIntentActivities(intent, PackageManager.GET_META_DATA);

        HashSet<String> customControlCurrentSet = new HashSet<>(Arrays.asList(customControlCurrent));


        ControlCustomizeManager customizeManager = ControlCustomizeManager.getInstance(context);
        Resources res = context.getResources();

        ArrayList<String> fullList = customizeManager.getFullActionToShow();
        List<String> currentList = Arrays.asList(customControlCurrent);
        ArrayList<String> result = new ArrayList<>(fullList);
        result.removeAll(currentList);
        String[] listAdd = result.toArray(new String[0]);

        for (String controlName : listAdd) {
            String nameToShow = customizeManager.getNameActionToShow(res, controlName);
            ControlCustomize controlCustomize = new ControlCustomize(1, nameToShow, Utils.getIconShow(controlName, true), controlName);
            listCustomizeCurrentApp.add(controlCustomize);
        }

        for (int i = 0; i < applicationInfos.size(); i++) {
            ResolveInfo appInfo = applicationInfos.get(i);
            String packageName = appInfo.activityInfo.packageName;
            if (i > 0 && applicationInfos.get(i - 1).activityInfo.packageName.equals(packageName)) {
                continue;
            }
            if (manager.getLaunchIntentForPackage(packageName) != null && !customControlCurrentSet.contains(packageName)) {
                String appName = manager.getApplicationLabel(appInfo.activityInfo.applicationInfo).toString();
                ControlCustomize controlCustomize = new ControlCustomize(0, appName, MethodUtils.getIconFromPackageName(context, packageName), packageName);
                listExceptCurrentApp.add(controlCustomize);
            }
        }

        return new CustomizeControlApp(listCustomizeCurrentApp, listExceptCurrentApp);
    }


    private String getKeyName(String key) {
        switch (key) {
            case Constant.STRING_ACTION_FLASH_LIGHT:
                return Constant.KEY_CONTROL_FLASH;

            case Constant.CLOCK:
                return Constant.KEY_CONTROL_ALARM;

            case Constant.CAMERA:
                return Constant.KEY_CONTROL_CAMERA;

            case Constant.RECORD:
                return Constant.KEY_CONTROL_RECORD;

            case Constant.DARK_MODE:
                return Constant.KEY_CONTROL_DARKMODE;

            case Constant.STRING_ACTION_BATTERY:
                return Constant.KEY_CONTROL_PIN;

            default:
                return key;
        }
    }


    private ControlCustomize isSave(ArrayList<ControlCustomize> listExceptCurrentApp, String packageName) {
        for (ControlCustomize control : listExceptCurrentApp) {

            if (control.getPackageName().equals(packageName)) {
                return control;
            }
        }
        return null;
    }


    private ArrayList<MusicPlayer> getListAppMusicPlayer(Context context) {
        ArrayList<MusicPlayer> musicPlayers = new ArrayList<>();
        Set<String> existingPackages = new HashSet<>();

        // Query broadcast receivers with the MEDIA_BUTTON intent
        Intent mainIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        List<ResolveInfo> pkgAppsList = context.getPackageManager().queryBroadcastReceivers(
                mainIntent,
                PackageManager.GET_RESOLVED_FILTER
        );

        // Add activity and service info to the musicPlayers list
        for (ResolveInfo info : pkgAppsList) {
            String packageName = null;
            String className = null;

            if (info.activityInfo != null) {
                packageName = info.activityInfo.packageName;
                className = info.activityInfo.name;
            } else if (info.serviceInfo != null) {
                packageName = info.serviceInfo.packageName;
                className = info.serviceInfo.name;
            }

            if (packageName != null && !existingPackages.contains(packageName)) {
                musicPlayers.add(new MusicPlayer(packageName, className));
                existingPackages.add(packageName);
            }
        }

        // Add predefined top music apps to the musicPlayers list
        String[] listPackageTopMusic = {
                "com.zing.mp3", "com.miui.player", "media.audioplayer.musicplayer",
                "media.music.musicplayer", "com.tencent.wesing", "in.krosbits.musicolet",
                "deezer.android.app", "com.maxmpz.audioplayer", "com.google.android.youtube",
                "com.audiomack", "com.apple.android.music", "com.melodis.midomiMusicIdentifier.freemium",
                "com.shaiban.audioplayer.mplayer", "com.soundcloud.android", "com.spotify.lite",
                "com.spotify.music", "freemusic.download.musicplayer.mp3player", "ht.nct",
                "musicplayer.musicapps.music.mp3player", "ru.yandex.music", "tunein.player",
                "com.google.android.apps.youtube.music"
        };

        for (String packageName : listPackageTopMusic) {
            if (!existingPackages.contains(packageName) && isPackageExisted(context, packageName)) {
                musicPlayers.add(new MusicPlayer(packageName, getAppNameFromPkgName(context, packageName)));
                existingPackages.add(packageName);
            }
        }

        return musicPlayers;
    }


    private boolean checkHasPackage(ArrayList<MusicPlayer> musicPlayers, String packageName) {
        List<String> notMusicPackagesList = Arrays.asList(
                "com.zing.zalo", "com.miui.cit",
                "org.telegram.messenger", "com.sec.android.app.ve.vebgm",
                "com.android.mms", "flipboard.boxer.app", "com.evenwell.fqc"
        );

        for (MusicPlayer player : musicPlayers) {
            if (player.getPackageName().equals(packageName) || notMusicPackagesList.contains(packageName)) {
                return true;
            }
        }
        return false;
    }

    private String getAppNameFromPkgName(Context context, String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo info = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            return (String) packageManager.getApplicationLabel(info);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    private boolean isPackageExisted(Context context, String targetPackage) {
        List<ApplicationInfo> packages = context.getPackageManager().getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals(targetPackage)) {
                return true;
            }
        }
        return false;
    }

//    private boolean checkHasPackage(ArrayList<MusicPlayer> musicPlayers, String packageName) {
//        for (MusicPlayer player : musicPlayers) {
//            if (player.getPackageName().equals(packageName)) {
//                return true;
//            }
//        }
//
//        return false;
//    }

    private ArrayList<AppInstallModel> getListAppRecent(Context context, ArrayList<AppInstallModel> allApp) {
        ArrayList<AppInstallModel> appRecent = new ArrayList<>();

        @SuppressLint("WrongConstant") UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService("usagestats");
        long time = System.currentTimeMillis();
        List<UsageStats> usageStats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 10, time);
//    Timber.e(".");

        if (usageStats != null) {
            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
            for (UsageStats us : usageStats) {
                mySortedMap.put(us.getLastTimeUsed(), us);
            }

            if (usageStats.size() > 0) {
                PackageManager packageManager = context.getPackageManager();
                Collections.sort(usageStats, usageStatsComparator);
                for (int i = 0; i < usageStats.size(); i++) {
                    AppInstallModel appInstallModel = new AppInstallModel();

                    try {
                        ApplicationInfo applicationInfo = packageManager.getApplicationInfo(usageStats.get(i).getPackageName(), 0);
                        String nameApp = (String) ((applicationInfo != null) ? packageManager.getApplicationLabel(applicationInfo) : "???");
                        appInstallModel.setPackageName(usageStats.get(i).getPackageName());
                        appInstallModel.setDrawable(MethodUtils.getIconFromPackageName(context, usageStats.get(i).getPackageName()));
                        appInstallModel.setName(nameApp);

                        if (checkApp(allApp, appInstallModel.getPackageName())) {
                            appRecent.add(appInstallModel);
                        }

                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return appRecent;
    }

    private boolean checkApp(ArrayList<AppInstallModel> allApp, String pka) {
        for (int i = 0; i < allApp.size(); i++) {
            if (pka.equals(allApp.get(i).getPackageName())) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<AppInstallModel> getListAppInstallModels(Context context) {
        ArrayList<AppInstallModel> appInstallModels = new ArrayList<>();
        try {
            PackageManager manager = context.getPackageManager();
            List<ApplicationInfo> applicationInfos = manager.getInstalledApplications(PackageManager.GET_META_DATA);

            for (ApplicationInfo app : applicationInfos) {
                if (manager.getLaunchIntentForPackage(app.packageName) != null) {
                    if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0 || (app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {

                    } else {
                        AppInstallModel appInstallModel = new AppInstallModel();
                        appInstallModel.setName(app.loadLabel(manager).toString());
                        appInstallModel.setPackageName(app.packageName);
                        appInstallModel.setDrawable(MethodUtils.getIconFromPackageName(context, app.packageName));
                        appInstallModels.add(appInstallModel);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return appInstallModels;
    }

    //devphare4


}

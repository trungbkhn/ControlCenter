package com.tapbi.spark.controlcenter.feature.controlios14.manager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.tapbi.spark.controlcenter.data.repository.PackageRepository;
import com.tapbi.spark.controlcenter.feature.controlios14.model.AppInstallModel;
import com.tapbi.spark.controlcenter.receiver.LoadAppSuggestReceiver;

import java.util.ArrayList;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;

public class SuggestAppManager {

    private static SuggestAppManager instance;
    private ArrayList<AppInstallModel> suggestApps = new ArrayList<>();
    private ArrayList<AppInstallModel> allApps = new ArrayList<>();
    private boolean isLoading;
    private boolean isRefresh;


    public static SuggestAppManager getInstance() {
        if (instance == null) {
            instance = new SuggestAppManager();
        }
        return instance;
    }

    public boolean isRefresh() {
        return isRefresh;
    }

    public void setRefresh(boolean refresh) {
        isRefresh = refresh;
    }

    public void loadSuggestApp(final Context context, final OnLoadAppRecentListener onLoadAppRecentListener) {
        new PackageRepository().getListAllAppInstallRx(context).subscribe(new SingleObserver<ArrayList<AppInstallModel>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull ArrayList<AppInstallModel> appInstallModels) {
//                Timber.e("hachung appInstallModels:" + appInstallModels.size());
                allApps = appInstallModels;
                handingListRecent(context, onLoadAppRecentListener);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }

    public void handingListRecent(Context context, OnLoadAppRecentListener onLoadAppRecentListener) {
        if (PermissionManager.getInstance().checkPermissionAppRecent(context) && !isLoading) {
            isLoading = true;
            new PackageRepository().getListAppRecentRx(context, allApps).subscribe(new SingleObserver<ArrayList<AppInstallModel>>() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {

                }

                @Override
                public void onSuccess(@NonNull ArrayList<AppInstallModel> appInstallModels) {
                    for (int i = appInstallModels.size() - 1; i >= 0; i--) {
                        if (appInstallModels.get(i) != null){
                            if (checkAppRecentInList(appInstallModels.get(i))) {
                                removeInListAppRecent(appInstallModels.get(i).getPackageName());
                                suggestApps.add(0, appInstallModels.get(i));
                            } else {
                                suggestApps.add(0, appInstallModels.get(i));
                            }
                        }
                    }
                    isLoading = false;
                    isRefresh = true;
                    if (onLoadAppRecentListener != null) {
                        onLoadAppRecentListener.onSuccess();
                    }

                }

                @Override
                public void onError(@NonNull Throwable e) {

                }
            });
        }
    }

    public void loadSuggestAppAndAlarm(Context context) {
        Intent intent = new Intent(context, LoadAppSuggestReceiver.class);
        int flag = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            flag = PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 17, intent, flag);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1800000, pendingIntent);

        loadSuggestApp(context, null);
    }

    public ArrayList<AppInstallModel> getAllApps() {
        return allApps;
    }

    public ArrayList<AppInstallModel> getSuggestApps() {
        ArrayList<AppInstallModel> ar = new ArrayList<>(suggestApps);
        if (suggestApps.size() < 8) {
            for (int i = 0; i < allApps.size(); i++) {
                if (allApps.get(i) == null){
                    continue;
                }
                if (!checkAppRecentInList(allApps.get(i))) {
                    ar.add(allApps.get(i));
                    if (ar.size() == 8) {
                        break;
                    }
                }
            }
        } else if (suggestApps.size() > 8) {
            for (int i = 0; i < 8; i++) {
                if (suggestApps.get(i) == null){
                    continue;
                }
                if (!checkAppRecentInList(suggestApps.get(i))) {
                    ar.add(suggestApps.get(i));
                }
            }
        }

        return ar;
    }

    private boolean checkAppRecentInList(AppInstallModel appInstallModel) {
        for (int i = 0; i < suggestApps.size(); i++) {
            AppInstallModel app = suggestApps.get(i);
            if (app != null && app.getPackageName() != null && appInstallModel != null && appInstallModel.getPackageName() != null && app.getPackageName().equals(appInstallModel.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    private void removeInListAppRecent(String pka) {
        for (int i = 0; i < suggestApps.size(); i++) {
            if (suggestApps.get(i).getPackageName().equals(pka)) {
                suggestApps.remove(suggestApps.get(i));
                break;
            }
        }
    }

    public interface OnLoadAppRecentListener {
        void onSuccess();
    }
}

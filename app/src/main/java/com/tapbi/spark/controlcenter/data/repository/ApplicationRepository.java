package com.tapbi.spark.controlcenter.data.repository;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.data.db.room.ControlCenterDataBase;
import com.tapbi.spark.controlcenter.data.model.ItemApp;
import com.tapbi.spark.controlcenter.data.model.ItemTurnOn;
import com.tapbi.spark.controlcenter.utils.MethodUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

public class ApplicationRepository {
    private ControlCenterDataBase controlCenterDataBase;

    @Inject
    public ApplicationRepository(ControlCenterDataBase controlCenterDataBase) {
        this.controlCenterDataBase = controlCenterDataBase;
    }

    private void sortByName(List<ItemApp> list) {
        Collections.sort(list, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                ItemApp p1 = (ItemApp) o1;
                ItemApp p2 = (ItemApp) o2;
                return p1.getName().compareToIgnoreCase(p2.getName());
            }
        });
    }

    public Single<ArrayList<ItemApp>> getListAllAppInstall(Context context) {
        return Single.fromCallable(() -> getAllApp(context)).subscribeOn(Schedulers.io());
    }

    public Single<List<ItemApp>> getAllowedApps(String name) {
        return Single.fromCallable(() -> controlCenterDataBase.focusDao().getAllItemAllowedApp(name)).subscribeOn(Schedulers.io());
    }

    public Single<Boolean> deleteAllItemAllowedApp(String name) {
        return Single.fromCallable(() -> deleteAllAllowedApp(name)).subscribeOn(Schedulers.io());
    }

    public Single<Boolean> insertItemAllowedApp(ItemApp itemApp) {
        return Single.fromCallable(() -> insertAllowedApp(itemApp)).subscribeOn(Schedulers.io());
    }

    public Single<Boolean> insertNewCustomAllowApp(List<ItemApp> listItemApp, String nameFocus) {
        return Single.fromCallable(() -> insertCustomAllowApp(listItemApp, nameFocus)).subscribeOn(Schedulers.io());
    }

    public Single<Boolean> deleteItemAppAutomation(String nameFocus) {
        return Single.fromCallable(() -> deleteAutoItemApp(nameFocus)).subscribeOn(Schedulers.io());
    }

    private Boolean deleteAutoItemApp(String nameFocus) {
        controlCenterDataBase.focusDao().deleteAllItemAutomationFocus(nameFocus);
        return true;
    }

    public void updateAutomationAppFocus(String name, String packageName, String nameApp, Long lastModify, String oldApp) {
        controlCenterDataBase.focusDao().updateItemAppAutomation(name, packageName, nameApp, lastModify, oldApp);
    }


    public void insertAppAutomation(ItemTurnOn itemTurnOn) {
        controlCenterDataBase.focusDao().insertItemAutomationFocus(itemTurnOn);
    }

    private boolean deleteAllAllowedApp(String nameFocus) {
        try {
            controlCenterDataBase.focusDao().deleteAllItemAllowedApp(nameFocus);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean insertAllowedApp(ItemApp itemApp) {
        try {
            controlCenterDataBase.focusDao().insertApp(itemApp);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean insertCustomAllowApp(List<ItemApp> listItemApp, String nameFocus) {

        try {
            for (ItemApp item : listItemApp
            ) {
                item.setNameFocus(nameFocus);
                controlCenterDataBase.focusDao().insertApp(item);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private ArrayList<ItemApp> getAllApp(Context context) {
        ArrayList<ItemApp> listApp = new ArrayList<>();
        String myAppName = context.getPackageName();
        PackageManager packageManager = context.getPackageManager();
        List<ApplicationInfo> packs = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo pack : packs) {
            try {
                if (packageManager.getLaunchIntentForPackage(pack.packageName) != null) {
                    if (!pack.packageName.equals(myAppName)) {
                        Drawable iconApp = MethodUtils.getIconFromPackageName(context, pack.packageName);
                        ItemApp itemApp = new ItemApp(packageManager.getApplicationLabel(pack).toString()
                                , pack.packageName, "", false);
                        itemApp.setIconApp(iconApp);
                        listApp.add(itemApp);
                    }
                }
            } catch (Exception e) {
                Timber.e(e);
            }
        }
        sortByName(listApp);
        return listApp;
    }

    public boolean checkNotificationAppAllow(String packageName) {
        boolean isAllowed = false;
        try {
            List<ItemApp> list = controlCenterDataBase.focusDao().getAllItemAllowedApp(App.focusIOSStart.getName());
            Timber.e("");
            for (ItemApp item : list) {
                if (item.getPackageName().equals(packageName)) {
                    isAllowed = true;
                    break;
                }
            }

        } catch (Exception e) {

        }
        return isAllowed;
    }

//    public boolean checkNotiPeopleAllow() {
//        boolean isAllowed = false;
//        if (App.getFocusIOSStart().getModeAllowPeople() != Constant.EVERY_ONE) {
//            if (App.ins.contactReposition.checkAllowPeople(App.mContext,App.getFocusIOSStart().getName(), App.phoneSms)) {
//                isAllowed = true;
//            }
//        } else {
//            isAllowed = true;
//        }
//        return isAllowed;
//    }


    public ItemTurnOn getItemAppOpenCurrent(String packageNameCurrent, String nameFocus) {
        List<ItemTurnOn> listAppAuto;
        if (nameFocus.equals(Constant.GAMING)) {
            listAppAuto = controlCenterDataBase.focusDao().getListAutoAppGame(true, Constant.APPS, Constant.GAMING);
        } else {
            listAppAuto = controlCenterDataBase.focusDao().getListAutoAppNotGame(true, Constant.APPS, Constant.GAMING);
        }
        List<ItemTurnOn> listAppCurrent = new ArrayList<>();
        for (ItemTurnOn item : listAppAuto) {
            if (item.getPackageName().equals(packageNameCurrent)) {
                listAppCurrent.add(item);
            }
        }
        ItemTurnOn itemAppOpen = null;
        if (listAppCurrent.size() > 0) {
            long currentModify = listAppCurrent.get(0).getLastModify();
            itemAppOpen = listAppCurrent.get(0);
            for (ItemTurnOn item : listAppCurrent) {
                if (currentModify < item.getLastModify()) {
                    itemAppOpen = item;
                    currentModify = item.getLastModify();
                }
            }
        }
        return itemAppOpen;
    }


    public void deleteItemAppFocus(String packageName) {
        controlCenterDataBase.focusDao().deleteItemAppFocus(packageName);
    }

    public void deleteItemAllowApp(String packageName) {
        controlCenterDataBase.focusDao().deleteItemAllowedApp(packageName);
    }

    public List<ItemTurnOn> getAllITemAutomationFocus(String name) {
        return controlCenterDataBase.focusDao().getListAutoAppGame(true, Constant.APPS, name);
    }


}

package com.tapbi.spark.controlcenter.data.repository;

import android.content.Context;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.data.db.room.ControlCenterDataBase;
import com.tapbi.spark.controlcenter.data.model.FocusIOS;
import com.tapbi.spark.controlcenter.data.model.ItemApp;
import com.tapbi.spark.controlcenter.data.model.ItemPeople;
import com.tapbi.spark.controlcenter.data.model.ItemTurnOn;
import com.tapbi.spark.controlcenter.feature.controlios14.model.NotyModel;
import com.tapbi.spark.controlcenter.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

public class FocusPresetRepository {

    private ControlCenterDataBase controlCenterDataBase;

    @Inject
    public FocusPresetRepository(ControlCenterDataBase controlCenterDataBase) {
        this.controlCenterDataBase = controlCenterDataBase;
    }

    public List<FocusIOS> getListFocus(List<FocusIOS> listAdded) {
        ArrayList<FocusIOS> list = new ArrayList<>();
        List<String> listAll = listAllFocus();

        for (int i = 0; i < listAll.size(); i++) {
            boolean exist = false;
            for (int j = 0; j < listAdded.size(); j++) {
                if (listAll.get(i).equals(listAdded.get(j).getName())) {
                    exist = true;
                    break;
                }
            }

            if (!exist) {
                list.add(getFocusDefaultByName(listAll.get(i)));
            }

        }

        return list;
    }

    private List<String> listAllFocus() {
        ArrayList<String> list = new ArrayList<>();
        list.add(Constant.DO_NOT_DISTURB);
//        list.add(Constant.DRIVING);
        list.add(Constant.GAMING);
        list.add(Constant.MINDFULNESS);
        list.add(Constant.PERSONAL);
        list.add(Constant.READING);
        list.add(Constant.SLEEP);
        list.add(Constant.WORK);
        return list;
    }

    private FocusIOS getFocusDefaultByName(String name) {
        switch (name) {
            case Constant.GAMING:
                return new FocusIOS(Constant.GAMING, Constant.GAMING, "#3478F6", Constant.NO_ONE, false, false, false, true, false);
            case Constant.MINDFULNESS:
                return new FocusIOS(Constant.MINDFULNESS, Constant.MINDFULNESS, "#59C4BD", Constant.NO_ONE, false, false, false, true, false);
            case Constant.PERSONAL:
                return new FocusIOS(Constant.PERSONAL, Constant.PERSONAL, "#007AFF", Constant.NO_ONE, false, false, false, true, false);
            case Constant.READING:
                return new FocusIOS(Constant.READING, Constant.READING, "#7B66FF", Constant.NO_ONE, false, false, false, true, false);
            case Constant.SLEEP:
                return new FocusIOS(Constant.SLEEP, Constant.SLEEP, "#FF9933", Constant.NO_ONE, false, false, false, true, false);
            case Constant.WORK:
            default:
                return new FocusIOS(Constant.WORK, Constant.WORK, "#59ADC4", Constant.NO_ONE, false, false, false, true, false);
        }
    }


    private void insertFocusDefault() {
        List<FocusIOS> lisFocusDefault = new ArrayList<>();
        lisFocusDefault.add(new FocusIOS(Constant.DO_NOT_DISTURB, Constant.DO_NOT_DISTURB, "#5756CE", Constant.NO_ONE, false, false, false, true, false));
//        lisFocusDefault.add(new FocusIOS(Constant.DRIVING, Constant.DRIVING, "#5756CE", Constant.NO_ONE, false, false, false, true, false));
//        lisFocusDefault.add(new FocusIOS(Constant.GAMING, Constant.GAMING, "#3478F6", Constant.NO_ONE, false, false, false, false, false));
//        lisFocusDefault.add(new FocusIOS(Constant.MINDFULNESS, Constant.MINDFULNESS, "#59C4BD", Constant.NO_ONE, false, false, false, false, false));
//        lisFocusDefault.add(new FocusIOS(Constant.PERSONAL, Constant.PERSONAL, "#A357D6", Constant.NO_ONE, false, false, false, false, false));
        lisFocusDefault.add(new FocusIOS(Constant.READING, Constant.READING, "#7B66FF", Constant.NO_ONE, false, false, false, true, false));
        lisFocusDefault.add(new FocusIOS(Constant.SLEEP, Constant.SLEEP, "#FF9933", Constant.NO_ONE, false, false, false, true, false));
        lisFocusDefault.add(new FocusIOS(Constant.WORK, Constant.WORK, "#59ADC4", Constant.NO_ONE, false, false, false, true, false));
        controlCenterDataBase.focusDao().insertListFocusDefault(lisFocusDefault);
    }

    public void insertTimeCurrentFocus() {
        List<ItemTurnOn> listDefault = listTimeDefault();
        controlCenterDataBase.focusDao().insertListItemAutomationFocus(listDefault);
    }

    private List<ItemTurnOn> listTimeDefault() {
        List<ItemTurnOn> listDefault = new ArrayList<>();
        listDefault.add(new ItemTurnOn(Constant.SLEEP, false, false, TimeUtils.getTimeWithHourStartRepeat(23, 30, true, true, true, true, true, true, true),
                TimeUtils.getTimeWithHourEndRepeat(6, 30, TimeUtils.getTimeWithHourStartRepeat(23, 30, true, true, true, true, true, true, true), true, true, true, true, true, true, true)
                , true, true, true, true, true, true, true, "", 0.0, 0.0, "", "", Constant.TIME, System.currentTimeMillis()));
        listDefault.add(new ItemTurnOn(Constant.WORK, false, false,
                TimeUtils.getTimeWithHourStartRepeat(8, 30, true, true, true, true, true, false, false), TimeUtils.getTimeWithHourEndRepeat(17, 30, TimeUtils.getTimeWithHourStartRepeat(8, 30, true, true, true, true, true, false, false), true, true, true, true, true, false, false)
                , true, true, true, true, true, false, false, "", 0.0, 0.0, "", "", Constant.TIME, System.currentTimeMillis()));
        listDefault.add(new ItemTurnOn(Constant.GAMING, false, false,
                TimeUtils.getTimeWithHourStartRepeat(9, 0, false, false, false, false, false, true, true), TimeUtils.getTimeWithHourEndRepeat(17, 0, TimeUtils.getTimeWithHourStartRepeat(9, 0, false, false, false, false, false, true, true), false, false, false, false, false, true, true), false, false, false, false, false, true, true, "", 0.0, 0.0, "", "", Constant.TIME, System.currentTimeMillis()));
        return listDefault;
    }


    public Single<List<FocusIOS>> getListFocus() {
        return Single.fromCallable(() -> {
            if (!App.tinyDB.getBoolean(Constant.INSERT_DEFAUT, false)) {
                App.tinyDB.putBoolean(Constant.INSERT_DEFAUT, true);
                insertFocusDefault();
                insertTimeCurrentFocus();
                App.tinyDB.putBoolean(Constant.UPDATE_COLOR_FOCUS, true);
            }

            List<FocusIOS> list = controlCenterDataBase.focusDao().getListFocus();

            if (!App.tinyDB.getBoolean(Constant.UPDATE_COLOR_FOCUS, false)) {
                updateColorFocus(list);
                App.tinyDB.putBoolean(Constant.UPDATE_COLOR_FOCUS, true);
            }

            return list;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    private void updateColorFocus(List<FocusIOS> lisFocusDefault) {
        for (FocusIOS focusIOS : lisFocusDefault) {
            switch (focusIOS.getName()) {
                case Constant.READING:
                    focusIOS.setColorFocus("#7B66FF");
                    controlCenterDataBase.focusDao().updateColorFocusIos("#7B66FF", Constant.READING);
                    break;
                case Constant.SLEEP:
                    focusIOS.setColorFocus("#FF9933");
                    controlCenterDataBase.focusDao().updateColorFocusIos("#FF9933", Constant.SLEEP);
                    break;
                case Constant.PERSONAL:
                    focusIOS.setColorFocus("#007AFF");
                    controlCenterDataBase.focusDao().updateColorFocusIos("#007AFF", Constant.PERSONAL);
                    break;
            }
        }

    }

    public Single<List<FocusIOS>> getListFocusAdd(List<FocusIOS> list) {
        return Single.fromCallable(() -> getListFocus(list)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Boolean> deleteFocus(FocusIOS focusIOS) {
        return Single.fromCallable(() -> deleteFocusData(focusIOS)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Boolean> editFocus(String name, String imageLink, String color, String oldName) {
        return Single.fromCallable(() -> updateItemFocus(name, imageLink, color, oldName)).subscribeOn(Schedulers.io());
    }

    public Single<FocusIOS> getFocusById(int id) {
        return Single.fromCallable(() -> controlCenterDataBase.focusDao().getFocusById(id)).subscribeOn(Schedulers.io());
    }

    public Single<FocusIOS> getFocusByName(String name) {
        return Single.fromCallable(() -> controlCenterDataBase.focusDao().getFocusByName(name)).subscribeOn(Schedulers.io());
    }

    public Single<FocusIOS> getFocusStart() {
        return Single.fromCallable(() -> controlCenterDataBase.focusDao().getFocusOnStart(true)).subscribeOn(Schedulers.io());
    }

    public void insertFocus(FocusIOS focusIOS) {
        controlCenterDataBase.focusDao().insertFocus(focusIOS);
    }

    private boolean deleteFocusData(FocusIOS focusIOS) {
        try {
            controlCenterDataBase.focusDao().deleteFocusByName(focusIOS.getName());
            List<ItemPeople> listAllowedPeople = controlCenterDataBase.focusDao().getAllItemAllowedPeople(focusIOS.getName());
            for (ItemPeople itemPeople : listAllowedPeople) {
                controlCenterDataBase.focusDao().deleteItemAllowedPeopleByNameFocus(itemPeople.getNameFocus());
            }
            List<ItemApp> listAllowedApp = controlCenterDataBase.focusDao().getAllItemAllowedApp(focusIOS.getName());
            for (ItemApp itApp : listAllowedApp) {
                controlCenterDataBase.focusDao().deleteAllItemAllowedApp(itApp.getNameFocus());
            }

            List<ItemTurnOn> listAuto = controlCenterDataBase.focusDao().getAllItemAutomationFocus(focusIOS.getName(), false);
            List<ItemTurnOn> listDefault = listTimeDefault();
            for (ItemTurnOn itemAuto : listAuto
            ) {
                controlCenterDataBase.focusDao().deleteAllItemAutomationFocus(itemAuto.getNameFocus());
            }
            for (ItemTurnOn itemDefault : listDefault
            ) {
                if (itemDefault.getNameFocus().equals(focusIOS.getName())) {
                    controlCenterDataBase.focusDao().insertItemAutomationFocus(itemDefault);
                    break;
                }
            }
            return true;
        } catch (Exception e) {
//            Timber.e("hachung: " + e);
            return false;
        }

    }

    private boolean updateItemFocus(String name, String imageLink, String color, String oldName) {
        controlCenterDataBase.focusDao().updateFocusIos(name, imageLink, color, oldName);
        //update people
        List<ItemPeople> listAllowedPeople = controlCenterDataBase.focusDao().getAllItemAllowedPeople(oldName);
        for (ItemPeople itemPeople : listAllowedPeople) {
            controlCenterDataBase.focusDao().updatePeopleChange(itemPeople.getContactId(), name, oldName);
        }
        List<ItemApp> listAllowedApp = controlCenterDataBase.focusDao().getAllItemAllowedApp(oldName);
        for (ItemApp itApp : listAllowedApp) {
            controlCenterDataBase.focusDao().updateAppName(itApp.getPackageName(), name, oldName);
        }
        List<ItemTurnOn> listAuto = controlCenterDataBase.focusDao().getAllItemAutomationFocus(oldName, false);
        for (ItemTurnOn itemAuto : listAuto
        ) {
            controlCenterDataBase.focusDao().updateStartItemTimeAutomation(name, oldName);
        }
        return true;
    }

    public Single<List<ItemTurnOn>> getListTimeDefault(String nameFocus) {
        return Single.fromCallable(() -> controlCenterDataBase.focusDao().getAllItemAutomationFocus(nameFocus, false)).subscribeOn(Schedulers.io());
    }

    public void updateStartItemFocusIos(
            Boolean isStartAutoAppOpen, Boolean isStartCurrent, Boolean isStartAutoLocation, Boolean isStartAutoTime, String name
    ) {
        controlCenterDataBase.focusDao().updateStartItemFocusIos(isStartAutoAppOpen, isStartCurrent, isStartAutoLocation, isStartAutoTime, name);

    }

    public void updatePresetHand(String name) {
        for (FocusIOS focusIOS : App.presetFocusList) {
            if (focusIOS.getName().equals(name)) {
                focusIOS.setStartCurrent(true);
                updateStartItemFocusIos(false, true, false, false, focusIOS.getName());
//                App.tinyDB.putString(Constant.FOCUS_START_OLD, focusIOS.getName());
            } else {
                focusIOS.setStartAutoLocation(false);
                focusIOS.setStartAutoTime(false);
                focusIOS.setStartAutoAppOpen(false);
                focusIOS.setStartCurrent(false);
                updateStartItemFocusIos(false, false, false, false, focusIOS.getName());
            }
        }
    }

    public void turnOffFocusSwitch(String name) {
        for (FocusIOS focusIOS : App.presetFocusList) {
            if (focusIOS.getName().equals(name)) {
                focusIOS.setStartAutoLocation(false);

                focusIOS.setStartAutoTime(false);
                focusIOS.setStartAutoAppOpen(false);
                focusIOS.setStartCurrent(false);
                updateStartItemFocusIos(false, false, false, false, focusIOS.getName());
                break;
            }
        }
        if (App.tinyDB.getString(Constant.FOCUS_START_OLD).equals(name)) {
            App.tinyDB.putString(Constant.FOCUS_START_OLD, "");
        }
    }


    public void startAutoTime(FocusIOS item, Boolean isStartAutoTime) {
        Timber.e("");
        controlCenterDataBase.focusDao().updateStartItemFocusIos(false, false, false, isStartAutoTime, item.getName());
        item.setStartAutoTime(isStartAutoTime);
        item.setStartCurrent(false);
        item.setStartAutoAppOpen(false);
        item.setStartAutoLocation(false);
    }


//    public void turnOffListItemLocation() {
////        Timber.e("hachung off location tung 1");
//        if (!App.tinyDB.getString(Constant.FOCUS_START_OLD).isEmpty()) {
////            Timber.e("hachung off location tung 3");
//            boolean isStart = App.checkIsStartFocus();
//            for (FocusIOS item : App.presetFocusList) {
//                if (App.tinyDB.getString(Constant.FOCUS_START_OLD).equals(item.getName())) {
//                    focusDataBase.focusDao().updateStartItemFocusIos(item.getStartAutoAppOpen(), !isStart, false, item.getStartAutoTime(), item.getName());
//                    item.setStartCurrent(!isStart);
//                } else {
//                    focusDataBase.focusDao().updateStartItemFocusIos(item.getStartAutoAppOpen(), item.getStartCurrent(), false, item.getStartAutoTime(), item.getName());
//                }
//
//                item.setStartAutoLocation(false);
//            }
//        } else {
//            for (FocusIOS item : App.presetFocusList) {
//                focusDataBase.focusDao().updateStartItemFocusIos(item.getStartAutoAppOpen(), item.getStartCurrent(), false, item.getStartAutoTime(), item.getName());
//                item.setStartAutoLocation(false);
//            }
//        }
//
//
//    }


    public void startAutoLocation(FocusIOS item, boolean isStartLocation) {
        controlCenterDataBase.focusDao().updateStartItemFocusIos(false, false, isStartLocation, false, item.getName());
        item.setStartAutoLocation(isStartLocation);
        item.setStartAutoTime(false);
        item.setStartCurrent(false);
        item.setStartAutoAppOpen(false);
    }

    public void insertNotification(NotyModel notyModel) {
        controlCenterDataBase.focusDao().insertNotification(notyModel);
    }

    public void deleteNoti() {
        controlCenterDataBase.focusDao().deleteNotification();
    }

    public void turnOffListAppOpen(Context context) {
        if (!App.tinyDB.getString(Constant.FOCUS_START_OLD).isEmpty()) {
            updateFocusHand(App.tinyDB.getString(Constant.FOCUS_START_OLD));
        } else {
            for (FocusIOS item : App.presetFocusList) {
                controlCenterDataBase.focusDao().updateStartItemFocusIos(
                        false,
                        item.getStartCurrent(),
                        item.getStartAutoLocation(),
                        item.getStartAutoTime(),
                        item.getName()
                );
                item.setStartAutoAppOpen(false);
            }
            App.setFocusStart(null);
        }
        if (App.ins.focusUtils != null) {
            App.ins.focusUtils.sendActionFocus(Constant.TIME_CHANGE, "");
        }

    }

    private void updateFocusHand(String name) {
        for (FocusIOS item : App.presetFocusList) {
            if (name.equals(item.getName())) {
                controlCenterDataBase.focusDao().updateStartItemFocusIos(false, true, item.getStartAutoLocation(), item.getStartAutoTime(), item.getName());
                item.setStartCurrent(true);
                App.setFocusStart(item);
            } else {
                controlCenterDataBase.focusDao().updateStartItemFocusIos(
                        false,
                        item.getStartCurrent(),
                        item.getStartAutoLocation(),
                        item.getStartAutoTime(),
                        item.getName()
                );
            }
            item.setStartAutoAppOpen(false);
        }
    }

    public void startAutoAppOpen(FocusIOS item) {
        controlCenterDataBase.focusDao().updateStartItemFocusIos(true, false, false, false, item.getName());
        item.setStartAutoAppOpen(true);
        item.setStartAutoLocation(false);
        item.setStartAutoTime(false);
        item.setStartCurrent(false);

    }

    public void turnOffItemFocusIos(FocusIOS item) {
        controlCenterDataBase.focusDao().updateStartItemFocusIos(false, false, false, false, item.getName());
        item.setStartAutoAppOpen(false);
        item.setStartAutoLocation(false);
        item.setStartAutoTime(false);
        item.setStartCurrent(false);
    }

    public void insertAutomationFocus(ItemTurnOn itemTurnOn) {
        controlCenterDataBase.focusDao().insertItemAutomationFocus(itemTurnOn);
    }

    public ItemTurnOn getItemTurnOnByControl(int type) {
        return controlCenterDataBase.focusDao().getItemTurnOnByControl(type);
    }

    public void deleteItemTurnOnByControl() {
        controlCenterDataBase.focusDao().deleteItemTurnOnByControl();
    }

    public FocusIOS getFocusIsOn() {
        return controlCenterDataBase.focusDao().getFocusOn(true);
    }


    public void turnOffFocus(String name) {
        controlCenterDataBase.focusDao().turnOffFocus(name, false, false, false, false);
    }

    public void updateStartItemFocusIos(String name) {
        controlCenterDataBase.focusDao().updateStartItemFocusIos(false, false, false, false, name);
        for (FocusIOS item : App.presetFocusList) {
            if (item.getName().equals(name)) {
                item.setStartCurrent(false);
                item.setStartAutoTime(false);
                item.setStartAutoLocation(false);
                item.setStartAutoAppOpen(false);
                App.tinyDB.putString(Constant.FOCUS_START_OLD, "");
                break;
            }
        }
    }

    public void updateStartHand() {
        for (FocusIOS focusIOS : App.presetFocusList) {
            List<ItemTurnOn> listAuto = controlCenterDataBase.focusDao().getAllItemAutomationFocusOn(focusIOS.getName(), true);
            for (ItemTurnOn it : listAuto
            ) {
//                if (it.getTypeEvent().equals(Constant.LOCATION)) {
//                    App.PauseLocation = true;
//                } else

                if (it.getTypeEvent().equals(Constant.TIME)) {
                    if (/*!focusIOS.getName().equals(Constant.WORK)&& */!focusIOS.getName().equals(Constant.GAMING)) {
                        long timeCurrent = System.currentTimeMillis();
                        if (timeCurrent >= it.getTimeStart() && timeCurrent < it.getTimeEnd()) {
                            if (TimeUtils.checkDayOfWeek(it.getMonDay(), it.getTueDay()
                                    , it.getWedDay(), it.getThuDay(), it.getFriDay(),
                                    it.getSatDay(), it.getSunDay())) {
                                updateTimeRepeat(it, it.getLastModify());
                            }
                        }
                    }
                }
            }
        }
    }

//    public void updateStartHandView() {
//        for (FocusIOS focusIOS : App.presetFocusList) {
//            List<ItemTurnOn> listAuto = focusDataBase.focusDao().getAllItemAutomationFocusOn(focusIOS.getName(), true);
//            for (ItemTurnOn it : listAuto
//            ) {
//                if (it.getTypeEvent().equals(Constant.TIME)) {
//                    long timeCurrent = System.currentTimeMillis();
//                    if (timeCurrent >= it.getTimeStart() && timeCurrent < it.getTimeEnd()) {
//                        if (TimeUtils.checkDayOfWeek(it.getMonDay(), it.getTueDay()
//                                , it.getWedDay(), it.getThuDay(), it.getFriDay(),
//                                it.getSatDay(), it.getSunDay())) {
//                            updateTimeRepeat(it, it.getLastModify());
//                        }
//                    }
//                }
//            }
//        }
//        Intent intentAutoService = new Intent(App.mContext, NotificationListener.class);
//        intentAutoService.putExtra(Constant.TIME_CHANGE, Constant.TIME_CHANGE);
//        App.mContext.startService(intentAutoService);
//
//    }

    public void updateTimeRepeat(ItemTurnOn itemTime, long lastModifyTimeOld) {
        controlCenterDataBase.focusDao().updateTimeAutomation(itemTime.getTimeStart() + TimeUtils.getTimeRepeat(itemTime.getMonDay(), itemTime.getTueDay()
                , itemTime.getWedDay(), itemTime.getThuDay(), itemTime.getFriDay(),
                itemTime.getSatDay(), itemTime.getSunDay()), itemTime.getTimeEnd() + TimeUtils.getTimeRepeat(itemTime.getMonDay(), itemTime.getTueDay()
                , itemTime.getWedDay(), itemTime.getThuDay(), itemTime.getFriDay(),
                itemTime.getSatDay(), itemTime.getSunDay()), itemTime.getNameFocus(), lastModifyTimeOld);
    }

    public void updateTimeRepeat(ItemTurnOn itemTime) {
        controlCenterDataBase.focusDao().updateTimeAutomation(TimeUtils.getTimeWithHourStart(TimeUtils.currentHour(itemTime.getTimeStart()), TimeUtils.currentMinute(itemTime.getTimeStart()))
                , TimeUtils.getTimeWithHourEnd(TimeUtils.currentHour(itemTime.getTimeEnd()), TimeUtils.currentMinute(itemTime.getTimeEnd()), itemTime.getTimeStart()), itemTime.getNameFocus(), itemTime.getLastModify());
    }

    public void startHandFocus(Context context, String name) {
        if (!App.tinyDB.getString(Constant.FOCUS_START_OLD).isEmpty()) {
//            boolean isStartOld = App.checkIsStartFocusWithName(name);
            for (FocusIOS item : App.presetFocusList) {
                if (App.tinyDB.getString(Constant.FOCUS_START_OLD).equals(item.getName())) {
                    controlCenterDataBase.focusDao().updateStartItemFocusIos(
                            false,
                            true,
                            false,
                            false,
                            item.getName()
                    );
                    item.setStartCurrent(true);
                    item.setStartAutoLocation(false);
                    item.setStartAutoTime(false);
                    item.setStartAutoAppOpen(false);
                } else {
                    controlCenterDataBase.focusDao().updateStartItemFocusIos(
                            false,
                            false,
                            false,
                            false,
                            item.getName()
                    );
                    item.setStartAutoLocation(false);
                    item.setStartAutoTime(false);
                    item.setStartAutoAppOpen(false);
                    item.setStartCurrent(false);
                }


            }
        } else {
            for (FocusIOS item : App.presetFocusList) {
                controlCenterDataBase.focusDao().updateStartItemFocusIos(
                        false,
                        false,
                        false,
                        false,
                        item.getName()
                );
                item.setStartAutoLocation(false);
                item.setStartAutoTime(false);
                item.setStartAutoAppOpen(false);
                item.setStartCurrent(false);
            }
        }
        if (App.ins.focusUtils!=null){
            App.ins.focusUtils.sendActionFocus(Constant.TIME_CHANGE, "");

        }
    }

    public void updateLocationAutomation(String name, String nameLocation, double latitude, double longitude, long lastModify, String oldLocation) {
        controlCenterDataBase.focusDao().updateItemLocationAutomation(name, nameLocation, latitude, longitude, lastModify, oldLocation);
    }
}

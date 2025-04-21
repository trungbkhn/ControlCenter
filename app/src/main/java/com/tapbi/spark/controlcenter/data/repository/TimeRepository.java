package com.tapbi.spark.controlcenter.data.repository;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.data.db.room.ControlCenterDataBase;
import com.tapbi.spark.controlcenter.data.model.FocusIOS;
import com.tapbi.spark.controlcenter.data.model.ItemTurnOn;
import com.tapbi.spark.controlcenter.utils.TimeUtils;

import java.util.List;

import javax.inject.Inject;

public class
TimeRepository {
    private ControlCenterDataBase controlCenterDataBase;

    @Inject
    public TimeRepository(ControlCenterDataBase controlCenterDataBase) {
        this.controlCenterDataBase = controlCenterDataBase;
    }


    public void updateStartAutomationLocationFocus(boolean isStart, String name, String location, long lastModify) {
        controlCenterDataBase.focusDao().updateStartItemLocationAutomation(
                isStart,
                name,
                location,
                lastModify
        );
    }

    public void updateStartAutomationAppFocus(Boolean isStart, String name, String nameApp, Long lastModify) {
        controlCenterDataBase.focusDao().updateStartItemAppAutomation(isStart, name, nameApp, lastModify);
    }

    public void updateStartAutomationTimeFocus(Boolean isStart, String name, Long lastModify, Long lastModifyOld) {
        controlCenterDataBase.focusDao().updateStartItemTimeAutomation(
                isStart,
                name,
                lastModify,
                lastModifyOld
        );
    }


    public void deleteItemLocationAutomation(String location, String nameFocus) {
        controlCenterDataBase.focusDao().deleteItemLocationAutomationFocus(location, nameFocus);
    }

    public void deleteItemAppAutomation(String packageName, String nameFocus) {
        controlCenterDataBase.focusDao().deleteItemAppAutomationFocus(packageName, nameFocus);
    }

    public void deleteItemTimeAutomation(Long lastModify, String nameFocus) {
        controlCenterDataBase.focusDao().deleteItemTimeAutomationFocus(lastModify, nameFocus);
    }

    public void insertAutomationFocus(ItemTurnOn itemTurnOn) {
        controlCenterDataBase.focusDao().insertItemAutomationFocus(itemTurnOn);
    }

    public List<ItemTurnOn> getListTimeFocus() {
        return controlCenterDataBase.focusDao().getListTimeFocus(true, Constant.TIME);
    }

    public void turnOffListAutoTime() {
//        Timber.e("hachung check FOCUS_START_OLD:" + App.tinyDB.getString(Constant.FOCUS_START_OLD));
        if (!App.tinyDB.getString(Constant.FOCUS_START_OLD).isEmpty()) {
//            boolean isStart = App.checkIsStartFocus();
            for (FocusIOS item : App.presetFocusList) {
                if (App.tinyDB.getString(Constant.FOCUS_START_OLD).equals(item.getName())) {
                    controlCenterDataBase.focusDao().updateStartItemFocusIos(item.getStartAutoAppOpen(), true, item.getStartAutoLocation(), false, item.getName());
                    item.setStartCurrent(true);
                    App.setFocusStart(item);
                } else {
                    controlCenterDataBase.focusDao().updateStartItemFocusIos(item.getStartAutoAppOpen(), item.getStartCurrent(), item.getStartAutoLocation(), false, item.getName());
                }
                item.setStartAutoTime(false);

            }
        } else {
            for (FocusIOS item : App.presetFocusList) {
                controlCenterDataBase.focusDao().updateStartItemFocusIos(item.getStartAutoAppOpen(), item.getStartCurrent(), item.getStartAutoLocation(), false, item.getName());
                item.setStartAutoTime(false);
            }
            App.setFocusStart(null);
        }
        for (ItemTurnOn itemTurnOn : App.timeAutoList) {
            if (itemTurnOn.getType() != -1) {
                long timeCurrent = System.currentTimeMillis();
                if (timeCurrent >= itemTurnOn.getTimeEnd()) {
                    App.ins.focusPresetRepository.deleteItemTurnOnByControl();
                }
            }
        }

    }

//    public ItemTurnOn getNextItemLocationWithWork() {
//        App.timeAutoList = getListTimeFocus();
//        ItemTurnOn itemLocation = null;
//        for (ItemTurnOn it : App.timeAutoList) {
//            if (it.getNameFocus().equals(Constant.WORK)) {
//                long currentTime = System.currentTimeMillis();
//                if (currentTime >= it.getTimeStart() && currentTime < it.getTimeEnd()) {
//                    itemLocation = it;
//                }
//                break;
//            }
//        }
//
//        return itemLocation;
//    }

    public ItemTurnOn getItemTimeNextAppOpenGame() {
        ItemTurnOn itemGame = null;
//        Timber.e("hachung size"+getListTimeFocus().size());
        for (ItemTurnOn it : getListTimeFocus()) {
            if (it.getNameFocus().equals(Constant.GAMING)) {
                long currentTime = System.currentTimeMillis();
//                Timber.e("hachung it currentTime: "+currentTime);
//                Timber.e("hachung it currentTime 1: "+it.getTimeStart() );
//                Timber.e("hachung it currentTime 2: "+it.getTimeEnd());
                if (currentTime >= it.getTimeStart() && currentTime < it.getTimeEnd()) {
//                    Timber.e("hachung khac null");
                    itemGame = it;
                    break;
                }

            }
        }
        return itemGame;
    }

    public void updateTimeRepeat(ItemTurnOn itemTime, long lastModifyTimeOld) {
        controlCenterDataBase.focusDao().updateTimeAutomation(itemTime.getTimeStart() + TimeUtils.getTimeRepeat(itemTime.getMonDay(), itemTime.getTueDay()
                , itemTime.getWedDay(), itemTime.getThuDay(), itemTime.getFriDay(),
                itemTime.getSatDay(), itemTime.getSunDay()), itemTime.getTimeEnd() + TimeUtils.getTimeRepeat(itemTime.getMonDay(), itemTime.getTueDay()
                , itemTime.getWedDay(), itemTime.getThuDay(), itemTime.getFriDay(),
                itemTime.getSatDay(), itemTime.getSunDay()), itemTime.getNameFocus(), lastModifyTimeOld);
    }

    public void updateTimeAutoFocus(String name, long timeStart, long timeEnd, boolean monDay, boolean tueDay,
                                    boolean wedDay, boolean thuDay, boolean friDay, boolean satDay, boolean sunDay, long lastModify, long lastModifyOld) {
        controlCenterDataBase.focusDao().updateItemTimeAutomation(name, timeStart, timeEnd, monDay, tueDay, wedDay, thuDay, friDay, satDay, sunDay, lastModify, lastModifyOld);
    }


}

package com.tapbi.spark.controlcenter.data.repository;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.tapbi.spark.controlcenter.data.db.room.ControlCenterDataBase;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MapRepository {
    private ControlCenterDataBase controlCenterDataBase;

    @Inject
    public MapRepository(ControlCenterDataBase controlCenterDataBase) {
        this.controlCenterDataBase = controlCenterDataBase;
    }

    private boolean isNetworkAvailable(Context context) {
        try {
            return Runtime.getRuntime().exec("ping -c 1 google.com").waitFor() == 0;
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Single<Boolean> isNetwork(Context context) {
        return Single.fromCallable(() -> isNetworkAvailable(context)).subscribeOn(Schedulers.io());
    }

    private Address openMap(Context context, String query) {
        Geocoder geocoder = new Geocoder(context);
        try {
            List<Address> addressList = geocoder.getFromLocationName(query, 15);
            if (addressList != null && addressList.size() > 0) {
                return addressList.get(0);
            } else return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

//    public Single<Address> getMap(Context context, String query) {
//        return Single.fromCallable(() -> openMap(context, query)).subscribeOn(Schedulers.io());
//    }
//
//    public void insertAutomationFocus(ItemTurnOn turnOn) {
//        focusDataBase.focusDao().insertItemAutomationFocus(turnOn);
//    }

//    public List<ItemTurnOn> getAllAutoLocation() {
//        return focusDataBase.focusDao().getListTimeFocus(true, Constant.LOCATION);
//    }
//
//
//    public ItemTurnOn getLastLocationAuto(Location location) {
//        List<ItemTurnOn> listAutoLocation = new ArrayList<>();
//        ItemTurnOn itemCurrent = null;
//        ItemTurnOn item = null;
////        long lastModify = App.locationAutoList.get(0).getLastModify();
//        if ( App.locationAutoList.size()>0){
//            for (ItemTurnOn itemLocation : App.locationAutoList) {
//                Location locationCurrent = new Location("");
//                locationCurrent.setLongitude(itemLocation.getLongitude());
//                locationCurrent.setLatitude(itemLocation.getLatitude());
//                if (locationCurrent.distanceTo(location) < 999) {
//                    listAutoLocation.add(itemLocation);
//                }
//            }
//        }
//
//        for (ItemTurnOn it : App.timeAutoList) {
//            if (it.getNameFocus().equals(Constant.WORK)) {
//                long currentTime = System.currentTimeMillis();
//                if (currentTime >= it.getTimeStart() && currentTime < it.getTimeEnd()) {
//                    item = it;
//                }
//                break;
//            }
//        }
////        Timber.e("hachung it " + item);
//        if (listAutoLocation.size() > 0) {
//            long lastModify = listAutoLocation.get(0).getLastModify();
//            itemCurrent = listAutoLocation.get(0);
//            for (ItemTurnOn it : listAutoLocation) {
//                if (lastModify < it.getLastModify()) {
//                    if (it.getNameFocus().equals(Constant.WORK)) {
//                        if (item != null && item.getStart()) {
//                            itemCurrent = it;
//                            App.itemNextLocationAuto = it;
//                        }
//                    } else {
//                        itemCurrent = it;
//                    }
//                }
//            }
////
////            Timber.e("hachung itemCurrent 1 "+itemCurrent);
//            if (itemCurrent!=null){
//                if (itemCurrent.getNameFocus().equals(Constant.WORK)){
//                    if (item != null && item.getStart()) {
//                        itemCurrent = item;
//                        App.itemNextLocationAuto = item;
//                    }else {
//                        itemCurrent=null;
//                    }
//                }
//            }
//        }
//
//        return itemCurrent;
//    }



}

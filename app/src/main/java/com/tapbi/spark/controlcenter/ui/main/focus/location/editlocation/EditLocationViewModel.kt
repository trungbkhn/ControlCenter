package com.tapbi.spark.controlcenter.ui.main.focus.location.editlocation

import com.tapbi.spark.controlcenter.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditLocationViewModel  //    public MutableLiveData<Address> addressMutableLiveData = new MutableLiveData<>();
@Inject constructor() //    public void getMap(Context context, String query) {
//        Timber.e("query location: " + query);
//        App.ins.mapRepository.getMap(context, query).subscribe(new SingleObserver<Address>() {
//            @Override
//            public void onSubscribe(@NonNull Disposable d) {
//                compositeDisposable.add(d);
//            }
//
//            @Override
//            public void onSuccess(@NonNull Address addresses) {
//                addressMutableLiveData.postValue(addresses);
//            }
//
//            @Override
//            public void onError(@NonNull Throwable e) {
//                Timber.e(e);
//                addressMutableLiveData.postValue(null);
//            }
//        });
//    }
//    public void updateLocationAutomation(String name, String nameLocation, double latitude, double longitude, long lastModify, String oldLocation){
//          Completable.fromRunnable(() -> {
//              App.ins.focusPresetRepository.updateLocationAutomation(name, nameLocation, latitude, longitude, lastModify, oldLocation);
//              App.PauseLocation = false;
//              Intent intentAutoService = new Intent(App.mContext, NotificationListener.class);
//              intentAutoService.putExtra(Constant.TIME_CHANGE, Constant.TIME_CHANGE);
//              App.mContext.startService(intentAutoService);
//                  }).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
//                      @Override
//                      public void onSubscribe(@NonNull Disposable d) {
//                            compositeDisposable.add(d);
//                      }
//
//                      @Override
//                      public void onComplete() {
//
//                      }
//
//                      @Override
//                      public void onError(@NonNull Throwable e) {
//
//                      }
//                  });
//    }
    : BaseViewModel()
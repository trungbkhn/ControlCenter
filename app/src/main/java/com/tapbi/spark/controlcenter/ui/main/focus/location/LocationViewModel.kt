package com.tapbi.spark.controlcenter.ui.main.focus.location

import com.tapbi.spark.controlcenter.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LocationViewModel  //    public MutableLiveData<Address> addressMutableLiveData = new MutableLiveData<>();
//    public MutableLiveData<FocusIOS> saveFocus = new MutableLiveData<>();
////    private final MapRepository mapRepository;
//
@Inject constructor() //
//
//    public void getMap(Context context, String query) {
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
//
//    public void insertAutomationFocus(ItemTurnOn turnOn) {
//        Completable.fromRunnable(() -> {
//            App.ins.mapRepository.insertAutomationFocus(turnOn);
//            App.PauseLocation = false;
//            Intent intentAutoService = new Intent(App.mContext, NotificationListener.class);
//            intentAutoService.putExtra(Constant.TIME_CHANGE, Constant.TIME_CHANGE);
//            App.mContext.startService(intentAutoService);
//        }).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
//            @Override
//            public void onSubscribe(@NonNull Disposable d) {
//                compositeDisposable.add(d);
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//
//            @Override
//            public void onError(@NonNull Throwable e) {
//
//            }
//        });
//
//    }
//
//    public void getFocusFromName(String name){
//        App.ins.focusPresetRepository.getFocusByName(name).subscribe(new SingleObserver<FocusIOS>() {
//            @Override
//            public void onSubscribe(@NonNull Disposable d) {
//
//            }
//
//            @Override
//            public void onSuccess(@NonNull FocusIOS focusIOS) {
//                saveFocus.postValue(focusIOS);
//            }
//
//            @Override
//            public void onError(@NonNull Throwable e) {
//
//            }
//        });
//    }
    : BaseViewModel()
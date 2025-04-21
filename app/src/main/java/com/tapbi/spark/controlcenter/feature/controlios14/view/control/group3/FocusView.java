package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group3;

//import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.common.models.MessageEvent;
import com.tapbi.spark.controlcenter.data.model.FocusIOS;
import com.tapbi.spark.controlcenter.data.model.ItemTurnOn;
import com.tapbi.spark.controlcenter.databinding.FocusLayoutBinding;
import com.tapbi.spark.controlcenter.eventbus.EventSwitchType;
import com.tapbi.spark.controlcenter.eventbus.EventUpdateFocus;
import com.tapbi.spark.controlcenter.feature.controlios14.adapter.FocusAdapter;
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614;
import com.tapbi.spark.controlcenter.utils.MethodUtils;
import com.tapbi.spark.controlcenter.utils.TimeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import timber.log.Timber;

public class FocusView extends ConstraintLayout {

    private FocusLayoutBinding binding;
    private FocusAdapter focusAdapter;
    private OnClickListener onClickListener;
    private String nameFocus = "";
    private FocusIOS focus;
    private String nameFocusRunningOld = "";

    public FocusView(@NonNull Context context) {
        super(context);
        init();
    }

    public FocusView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FocusView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
    }

    private void init() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.focus_layout, this, true);
        focusAdapter = new FocusAdapter();
        binding.rvFocus.setItemAnimator(null);
        binding.rvFocus.setAdapter(focusAdapter);

        focusAdapter.setClickListener(new FocusAdapter.ItemClickListener() {
            @Override
            public void onMenuClick(FocusIOS focusIOS, int position) {
            }

            @Override
            public void onTitleClick(FocusIOS focusIOS, int position) {
                if (NotyControlCenterServicev614.getInstance().isDoubleClick()) {
                    return;
                }

//                if (!PermissionUtils.INSTANCE.checkPermissionPhone(getContext())) {
//                    onClickListener.onRequestPermissionPhone(focusIOS);
//                    return;
//                }

                turnOnOffFocus(focusIOS);
            }

            @Override
            public void onHourClick(FocusIOS focusIOS, int position) {

                if (NotyControlCenterServicev614.getInstance().isDoubleClick()) {
                    return;
                }

//                if (!PermissionUtils.INSTANCE.checkPermissionPhone(getContext())) {
//                    onClickListener.onRequestPermissionPhone(focusIOS);
//                    return;
//                }
                Timber.e("hachung :"+"put");
                App.tinyDB.putString(Constant.FOCUS_START_OLD, "");
                App.ins.focusPresetRepository.updateStartHand();
                ItemTurnOn itemTurnOn = App.ins.focusPresetRepository.getItemTurnOnByControl(-1);
                if (focusIOS.getStartAutoLocation() || focusIOS.getStartAutoTime()
                        || focusIOS.getStartAutoAppOpen() || focusIOS.getStartCurrent()) {

                    if (itemTurnOn == null) {

                        insertItemFocusHour(focusIOS);
                        Timber.e("hachung check: turnOffFocusOn");
                        turnOffFocusOn();
                        App.ins.focusPresetRepository.updateStartItemFocusIos(false
                                , false
                                , false
                                , true
                                , focusIOS.getName());
                        updateUiFocus(focusIOS, true, false, false);
                        App.setFocusStart(focusIOS);
                    } else {
                        deleteItemFocusControl();
                        if (!focusIOS.getName().equals(itemTurnOn.getNameFocus())) {
                            Timber.e("hachung check: turnOffFocusOn");
                            turnOffFocusOn();
                            insertItemFocusHour(focusIOS);
                            App.ins.focusPresetRepository.updateStartItemFocusIos(false
                                    , false
                                    , false
                                    , true
                                    , focusIOS.getName());
                            updateUiFocus(focusIOS, true, false, false);
                            App.setFocusStart(focusIOS);
                        } else {
                            if (itemTurnOn.getType() == ItemTurnOn.TYPE_HOUR) {

                                App.ins.focusPresetRepository.updateStartItemFocusIos(false
                                        , false
                                        , false
                                        , false
                                        , focusIOS.getName());
                                for (FocusIOS fois : App.presetFocusList) {
                                    fois.setStartAutoAppOpen(false);
                                    fois.setStartAutoTime(false);
                                    fois.setStartAutoLocation(false);
                                    fois.setStartCurrent(false);
                                }
                                Timber.e("hachung: check null");
                                App.setFocusStart(null);
                            } else {

                                insertItemFocusHour(focusIOS);
                                App.ins.focusPresetRepository.updateStartItemFocusIos(false
                                        , false
                                        , false
                                        , true
                                        , focusIOS.getName());
                                updateUiFocus(focusIOS, true, false, false);
                                App.setFocusStart(focusIOS);
                            }

                        }
                    }
                    getListFocus();

                } else {

                    if (itemTurnOn == null) {
                        if (focusIOS.getName().equals(Constant.GAMING) /*|| focusIOS.getName().equals(Constant.WORK)*/) {
                            checkHourFocus(focusIOS);
                        } else {

                            insertItemFocusHour(focusIOS);
                            Timber.e("hachung check: turnOffFocusOn");
                            turnOffFocusOn();
                            App.ins.focusPresetRepository.updateStartItemFocusIos(false
                                    , false
                                    , false
                                    , true
                                    , focusIOS.getName());
                            updateUiFocus(focusIOS, true, false, false);
                            App.setFocusStart(focusIOS);
                            getListFocus();

                        }
                    } else {

                        deleteItemFocusControl();
                        if (!focusIOS.getName().equals(itemTurnOn.getNameFocus())) {

                            insertItemFocusHour(focusIOS);
                            Timber.e("hachung check: turnOffFocusOn");
                            turnOffFocusOn();
                            App.ins.focusPresetRepository.updateStartItemFocusIos(false
                                    , false
                                    , false
                                    , true
                                    , focusIOS.getName());
                            updateUiFocus(focusIOS, true, false, false);
                            App.setFocusStart(focusIOS);
                        }
                        getListFocus();
                    }
                }

            }

            @Override
            public void onEveningClick(FocusIOS focusIOS, int position) {

                if (NotyControlCenterServicev614.getInstance().isDoubleClick()) {
                    return;
                }

//                if (!PermissionUtils.INSTANCE.checkPermissionPhone(getContext())) {
//                    onClickListener.onRequestPermissionPhone(focusIOS);
//                    return;
//                }
                Timber.e("hachung :"+"put");
                App.tinyDB.putString(Constant.FOCUS_START_OLD, "");
//                App.ins.focusPresetRepository.updateStartHandView();
                App.ins.focusPresetRepository.updateStartHand();

                ItemTurnOn itemTurnOn = App.ins.focusPresetRepository.getItemTurnOnByControl(-1);
                if (focusIOS.getStartAutoLocation() || focusIOS.getStartAutoTime()
                        || focusIOS.getStartAutoAppOpen() || focusIOS.getStartCurrent()) {

                    if (itemTurnOn == null) {
                        insertItemFocusEvening(focusIOS);
                        Timber.e("hachung check: turnOffFocusOn");
                        turnOffFocusOn();
                        App.ins.focusPresetRepository.updateStartItemFocusIos(false
                                , false
                                , false
                                , true
                                , focusIOS.getName());
                        App.setFocusStart(focusIOS);
                        updateUiFocus(focusIOS, true, false, false);

                    } else {
                        deleteItemFocusControl();
                        if (!focusIOS.getName().equals(itemTurnOn.getNameFocus())) {
                            Timber.e("hachung check: turnOffFocusOn");
                            turnOffFocusOn();
                            insertItemFocusEvening(focusIOS);
                            App.ins.focusPresetRepository.updateStartItemFocusIos(false
                                    , false
                                    , false
                                    , true
                                    , focusIOS.getName());
                            App.setFocusStart(focusIOS);
                            updateUiFocus(focusIOS, true, false, false);
                        } else {

                            if (itemTurnOn.getType() == ItemTurnOn.TYPE_EVENING) {

                                App.ins.focusPresetRepository.updateStartItemFocusIos(false
                                        , false
                                        , false
                                        , false
                                        , focusIOS.getName());
                                for (FocusIOS fios : App.presetFocusList
                                ) {
                                    if (fios.getName().equals(focusIOS.getName())) {
                                        fios.setStartAutoTime(false);
                                        fios.setStartAutoLocation(false);
                                        fios.setStartAutoAppOpen(false);
                                        fios.setStartCurrent(false);
                                    }

                                }
                                Timber.e("hachung: check null");
                                App.setFocusStart(null);
                            } else {

                                insertItemFocusEvening(focusIOS);
                                App.ins.focusPresetRepository.updateStartItemFocusIos(false
                                        , false
                                        , false
                                        , true
                                        , focusIOS.getName());
                                App.setFocusStart(focusIOS);
                                updateUiFocus(focusIOS, true, false, false);
                            }
                        }
                    }

                    getListFocus();

                } else {

                    if (itemTurnOn == null) {
                        if (focusIOS.getName().equals(Constant.GAMING) /*|| focusIOS.getName().equals(Constant.WORK)*/) {
                            checkEveningFocus(focusIOS);
                        } else {

                            insertItemFocusEvening(focusIOS);
                            Timber.e("hachung check: turnOffFocusOn");
                            turnOffFocusOn();
                            App.ins.focusPresetRepository.updateStartItemFocusIos(false
                                    , false
                                    , false
                                    , true
                                    , focusIOS.getName());
                            updateUiFocus(focusIOS, true, false, false);
                            App.setFocusStart(focusIOS);
                            getListFocus();

                        }
                    } else {

                        deleteItemFocusControl();
                        if (!focusIOS.getName().equals(itemTurnOn.getNameFocus())) {

                            insertItemFocusEvening(focusIOS);
                            Timber.e("hachung check: turnOffFocusOn");
                            turnOffFocusOn();
                            App.ins.focusPresetRepository.updateStartItemFocusIos(false
                                    , false
                                    , false
                                    , true
                                    , focusIOS.getName());
                            App.setFocusStart(focusIOS);
                            updateUiFocus(focusIOS, true, false, false);
                        }
                        getListFocus();
                    }

                }
            }

            @Override
            public void onLocationClick(FocusIOS focusIOS, int position) {

//                if (NotyControlCenterServicev614.getInstance().isDoubleClick()) {
//                    return;
//                }
//
//                if (!PermissionUtils.INSTANCE.checkPermissionPhone(getContext())) {
//                    onClickListener.onRequestPermissionPhone(focusIOS);
//                    return;
//                }
//
//                App.ins.focusPresetRepository.updateStartHand();
//
//                if (MethodUtils.isNetworkConnected(getContext())) {
//                    if (MethodUtils.isGPSEnabled(getContext())) {
//                        if (ActivityCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//                                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                            ItemTurnOn itemTurnOn = App.ins.focusPresetRepository.getItemTurnOnByControl(-1);
//                            if (focusIOS.getStartAutoLocation() || focusIOS.getStartAutoTime()
//                                    || focusIOS.getStartAutoAppOpen() || focusIOS.getStartCurrent()) {
//
//                                if (itemTurnOn == null) {
//
//                                    insertItemFocusLocation(focusIOS);
//                                    turnOffFocusOn();
//                                    App.ins.focusPresetRepository.updateStartItemFocusIos(false
//                                            , false
//                                            , true
//                                            , false
//                                            , focusIOS.getName());
//
//                                    updateUiFocus(focusIOS, false, true, false);
//
//                                } else {
//                                    deleteItemFocusControl();
//                                    if (!focusIOS.getName().equals(itemTurnOn.getNameFocus())) {
//
//                                        turnOffFocusOn();
//                                        insertItemFocusLocation(focusIOS);
//                                        App.ins.focusPresetRepository.updateStartItemFocusIos(false
//                                                , false
//                                                , true
//                                                , false
//                                                , focusIOS.getName());
//
//                                        updateUiFocus(focusIOS, false, true, false);
//                                    } else {
//
//                                        if (itemTurnOn.getType() == ItemTurnOn.TYPE_LOCATION) {
//
//                                            App.ins.focusPresetRepository.updateStartItemFocusIos(false
//                                                    , false
//                                                    , false
//                                                    , false
//                                                    , focusIOS.getName());
//                                            for (FocusIOS fios : App.presetFocusList
//                                            ) {
//                                                if (fios.getName().equals(focusIOS.getName())) {
//                                                    fios.setStartAutoTime(false);
//                                                    fios.setStartAutoLocation(false);
//                                                    fios.setStartAutoAppOpen(false);
//                                                    fios.setStartCurrent(false);
//                                                }
//
//                                            }
//                                        } else {
//
//                                            insertItemFocusLocation(focusIOS);
//                                            App.ins.focusPresetRepository.updateStartItemFocusIos(false
//                                                    , false
//                                                    , true
//                                                    , false
//                                                    , focusIOS.getName());
//
//                                            updateUiFocus(focusIOS, false, true, false);
//                                        }
//                                    }
//                                }
//                                getListFocus();
//
//                            } else {
//
//                                if (itemTurnOn == null) {
//                                    if (focusIOS.getName().equals(Constant.GAMING) /*|| focusIOS.getName().equals(Constant.WORK)*/) {
//                                        checkLocationFocus(focusIOS);
//                                    } else {
//
//                                        insertItemFocusLocation(focusIOS);
//                                        turnOffFocusOn();
//                                        App.ins.focusPresetRepository.updateStartItemFocusIos(false
//                                                , false
//                                                , true
//                                                , false
//                                                , focusIOS.getName());
//                                        updateUiFocus(focusIOS, false, true, false);
//                                        getListFocus();
//
//                                    }
//                                } else {
//                                    deleteItemFocusControl();
//                                    if (!focusIOS.getName().equals(itemTurnOn.getNameFocus())) {
//
//                                        insertItemFocusLocation(focusIOS);
//                                        turnOffFocusOn();
//                                        App.ins.focusPresetRepository.updateStartItemFocusIos(false
//                                                , false
//                                                , true
//                                                , false
//                                                , focusIOS.getName());
//                                        updateUiFocus(focusIOS, false, true, false);
//                                    }
//                                    getListFocus();
//                                }
//
//                            }
//                        } else {
//                            if (NotyControlCenterServicev614.getInstance() != null) {
//                                NotyControlCenterServicev614.getInstance()
//                                        .showToast(getContext().getString(R.string.allow_permission_location));
//
//                                onClickListener.onRequestPermissionLocation(focusIOS);
//                            }
//                        }
//                    } else {
//                        if (NotyControlCenterServicev614.getInstance() != null) {
//                            NotyControlCenterServicev614.getInstance()
//                                    .showToast(getContext().getString(R.string.you_need_turn_on_location));
//                        }
//                    }
//                } else {
//                    if (NotyControlCenterServicev614.getInstance() != null) {
//                        NotyControlCenterServicev614.getInstance()
//                                .showToast(getContext().getString(R.string.error_internet));
//                    }
//                }
            }

            @Override
            public void onSettingsClick(FocusIOS focusIOS, int position) {

                if (NotyControlCenterServicev614.getInstance().isDoubleClick()) {
                    return;
                }

                onClickListener.onSettingFocus(focusIOS);
            }

            @Override
            public void onNewClick() {

                if (NotyControlCenterServicev614.getInstance().isDoubleClick()) {
                    return;
                }

                onClickListener.onNewFocus();
            }

            @Override
            public void onCloseView() {
                if (NotyControlCenterServicev614.getInstance().isDoubleClick()) {
                    return;
                }
                onClickListener.onCloseViewFocus();
            }
        });

        getListFocus();

    }

    private void checkTitleFocus(FocusIOS focusIOS) {
        App.ins.focusPresetRepository.getListTimeDefault(focusIOS.getName()).subscribe(new SingleObserver<List<ItemTurnOn>>() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<ItemTurnOn> turnOnList) {
                boolean b = focusIOS.getName().equals(Constant.GAMING) ? checkGame(turnOnList, false) : checkWork(turnOnList, false, false);
                if (b) {
                    Timber.e("hachung check: turnOffFocusOn");
                    turnOffFocusOn();
                    App.ins.focusPresetRepository.updateStartHand();
                    App.ins.focusPresetRepository.updateStartItemFocusIos(false
                            , true
                            , false
                            , false
                            , focusIOS.getName());
                    nameFocus = focusIOS.getName();
                    updateUiFocus(focusIOS, false, false, true);
                    App.setFocusStart(focusIOS);
                    App.tinyDB.putString(Constant.FOCUS_START_OLD, focusIOS.getName());
                    getListFocus();
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (NotyControlCenterServicev614.getInstance() != null) {
                            String message;
                            if (focusIOS.getName().equals(Constant.GAMING)) {
                                message = getContext().getString(R.string.you_need_to_choose_the_game_Application);
                            } else {
                                message = getContext().getString(R.string.you_need_to_choose_the_location);
                            }
                            NotyControlCenterServicev614.getInstance().showToast(message);
                        }
                    });
                }
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

            }
        });
    }

    private void checkHourFocus(FocusIOS focusIOS) {
        App.ins.focusPresetRepository.getListTimeDefault(focusIOS.getName()).subscribe(new SingleObserver<List<ItemTurnOn>>() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<ItemTurnOn> turnOnList) {
                boolean b = focusIOS.getName().equals(Constant.GAMING) ? checkGame(turnOnList, true) : checkWork(turnOnList, false, true);
                if (b) {
                    Timber.e("hachung check: turnOffFocusOn");
                    insertItemFocusHour(focusIOS);
                    turnOffFocusOn();
                    App.ins.focusPresetRepository.updateStartItemFocusIos(false
                            , false
                            , false
                            , true
                            , focusIOS.getName());
                    updateUiFocus(focusIOS, true, false, false);
                    App.setFocusStart(focusIOS);
                    getListFocus();
                } else {

                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (NotyControlCenterServicev614.getInstance() != null) {
                            String message;
                            if (focusIOS.getName().equals(Constant.GAMING)) {
                                message = getContext().getString(R.string.you_need_to_choose_the_game_Application);
                            } else {
                                message = getContext().getString(R.string.you_need_to_choose_the_location);
                            }
                            NotyControlCenterServicev614.getInstance().showToast(message);
                        }
                    });

                }
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

            }
        });
    }

    private void checkEveningFocus(FocusIOS focusIOS) {
        App.ins.focusPresetRepository.getListTimeDefault(focusIOS.getName()).subscribe(new SingleObserver<List<ItemTurnOn>>() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<ItemTurnOn> turnOnList) {
                boolean b = focusIOS.getName().equals(Constant.GAMING) ? checkGame(turnOnList, true) : checkWork(turnOnList, false, true);
                if (b) {
                    Timber.e("hachung check: turnOffFocusOn");
                    insertItemFocusEvening(focusIOS);
                    turnOffFocusOn();
                    App.ins.focusPresetRepository.updateStartItemFocusIos(false
                            , false
                            , false
                            , true
                            , focusIOS.getName());
                    updateUiFocus(focusIOS, true, false, false);
                    App.setFocusStart(focusIOS);
                    getListFocus();
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (NotyControlCenterServicev614.getInstance() != null) {
                            String message;
                            if (focusIOS.getName().equals(Constant.GAMING)) {
                                message = getContext().getString(R.string.you_need_to_choose_the_game_Application);
                            } else {
                                message = getContext().getString(R.string.you_need_to_choose_the_location);
                            }
                            NotyControlCenterServicev614.getInstance().showToast(message);
                        }
                    });
                }
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

            }
        });
    }

    private void checkLocationFocus(FocusIOS focusIOS) {
//        App.ins.focusPresetRepository.getListTimeDefault(focusIOS.getName()).subscribe(new SingleObserver<List<ItemTurnOn>>() {
//            @Override
//            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
//
//            }
//
//            @Override
//            public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<ItemTurnOn> turnOnList) {
//                boolean b = focusIOS.getName().equals(Constant.GAMING) ? checkGame(turnOnList) : checkWork(turnOnList, true);
//                if (b) {
//
//                    insertItemFocusLocation(focusIOS);
//                    turnOffFocusOn();
//                    App.ins.focusPresetRepository.updateStartItemFocusIos(false
//                            , false
//                            , true
//                            , false
//                            , focusIOS.getName());
//                    updateUiFocus(focusIOS, false, true, false);
//                    getListFocus();
//                } else {
//                    new Handler(Looper.getMainLooper()).post(() -> {
//                        if (NotyControlCenterServicev614.getInstance() != null) {
//                            String message;
//                            if (focusIOS.getName().equals(Constant.GAMING)) {
//                                message = getContext().getString(R.string.you_need_to_choose_the_game_Application);
//                            } else {
//                                message = getContext().getString(R.string.you_need_to_choose_the_location);
//                            }
//                            NotyControlCenterServicev614.getInstance().showToast(message);
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
//
//            }
//        });
    }

    public void updateUiFocus(FocusIOS focusIOS, boolean isStartTime, boolean isStartLocation, boolean isStart) {
        for (FocusIOS fios : App.presetFocusList
        ) {
            if (fios.getName().equals(focusIOS.getName())) {
                fios.setStartAutoTime(isStartTime);
                fios.setStartAutoLocation(isStartLocation);
                fios.setStartCurrent(isStart);
                fios.setStartAutoAppOpen(false);
            } else {
                fios.setStartAutoTime(false);
                fios.setStartAutoLocation(false);
                fios.setStartCurrent(false);
                fios.setStartAutoAppOpen(false);
            }

        }
    }

    private void getListFocus() {
        nameFocusRunningOld = App.getNameFocusRunning();

        EventBus.getDefault().post(new MessageEvent(Constant.UPDATE_VIEW_FROM_CONTROL));

        App.ins.focusPresetRepository.getListFocus().subscribe(new SingleObserver<List<FocusIOS>>() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<FocusIOS> focusIOS) {
                ItemTurnOn itemTurnOn = App.ins.focusPresetRepository.getItemTurnOnByControl(-1);
                focusAdapter.setItemTurnOn(itemTurnOn);
                focusAdapter.setList((ArrayList<FocusIOS>) focusIOS);

                FocusIOS focusIsEnable = null;
                FocusIOS focusIsDisable = null;

                for (int i = 0; i < focusIOS.size(); i++) {
                    if (focusIOS.get(i).getStartAutoLocation() || focusIOS.get(i).getStartAutoTime()
                            || focusIOS.get(i).getStartAutoAppOpen() || focusIOS.get(i).getStartCurrent()) {
                        focusIsEnable = focusIOS.get(i);
                        break;
                    }
                    if (focusIOS.get(i).getName().equals(nameFocus)) {
                        focusIsDisable = focusIOS.get(i);
                    }
                }

                if (focusIsEnable != null) {
                    onClickListener.onUpdateView(focusIsEnable);
                    nameFocus = focusIsEnable.getName();
                } else if (focusIsDisable != null) {
                    onClickListener.onUpdateView(focusIsDisable);
                    nameFocus = focusIsDisable.getName();
                } else {
                    onClickListener.onUpdateView(null);
                    nameFocus = "";
                }

                for (int i = 0; i < focusIOS.size(); i++) {
                    if (focusIOS.get(i).getStartAutoLocation() || focusIOS.get(i).getStartAutoTime()
                            || focusIOS.get(i).getStartAutoAppOpen() || focusIOS.get(i).getStartCurrent()) {
                        if (focus == null) {
                            focus = focusIOS.get(i);
//                            Timber.e("hachung 6");
                            MethodUtils.intentToCheckPermission(getContext());
                        } else {
                            if (!focus.getName().equals(focusIOS.get(i).getName())) {
                                focus = focusIOS.get(i);
//                                Timber.e("hachung 7");
                                MethodUtils.intentToCheckPermission(getContext());
                            }
                        }
                        break;
                    } else {
                        if (focus != null) {
                            focus = null;
                            MethodUtils.intentToCheckPermission(getContext());
                            break;
                        }
                    }

                }

            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

            }
        });

    }

    public void turnOnOffFocus(FocusIOS focusIOS) {
        if (focusIOS == null){
            return;
        }
        deleteItemFocusControl();
        App.isPauseApp = true;
//        App.PauseLocation = true;
        App.ins.focusPresetRepository.updateStartHand();
        if (focusIOS.getStartAutoLocation() || focusIOS.getStartAutoTime()
                || focusIOS.getStartAutoAppOpen() || focusIOS.getStartCurrent()) {
            App.tinyDB.putString(Constant.FOCUS_START_OLD, "");
            for (FocusIOS fois : App.presetFocusList) {
                fois.setStartAutoAppOpen(false);
                fois.setStartAutoTime(false);
                fois.setStartAutoLocation(false);
                fois.setStartCurrent(false);
            }

            App.ins.focusPresetRepository.updateStartItemFocusIos(false
                    , false
                    , false
                    , false
                    , focusIOS.getName());
            App.setFocusStart(null);
            getListFocus();
        } else {
            if (focusIOS.getName().equals(Constant.GAMING) /*|| focusIOS.getName().equals(Constant.WORK)*/) {
                checkTitleFocus(focusIOS);
            } else {
                focusIOS.setStartAutoAppOpen(false);
                focusIOS.setStartAutoTime(false);
                focusIOS.setStartAutoLocation(false);
                focusIOS.setStartCurrent(true);
                App.tinyDB.putString(Constant.FOCUS_START_OLD, focusIOS.getName());
                updateUiFocus(focusIOS, false, false, true);
                App.setFocusStart(focusIOS);
                turnOffFocusOn();
                App.ins.focusPresetRepository.updateStartItemFocusIos(false
                        , true
                        , false
                        , false
                        , focusIOS.getName());

                nameFocus = focusIOS.getName();
                App.setFocusStart(focusIOS);
                getListFocus();
            }
        }
    }

    private void turnOffFocusOn() {
        FocusIOS focusIOSOn = App.ins.focusPresetRepository.getFocusIsOn();
        if (focusIOSOn != null) {
            App.ins.focusPresetRepository.updateStartItemFocusIos(false
                    , false
                    , false
                    , false
                    , focusIOSOn.getName());
        }
        App.setFocusStart(null);
    }

    private void deleteItemFocusControl() {
        App.ins.focusPresetRepository.deleteItemTurnOnByControl();
        focusAdapter.setItemTurnOn(null);
    }

    private void insertItemFocusHour(FocusIOS focusIOS) {
        long c = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(c);
        int second = calendar.get(Calendar.SECOND);
        int millisecond = calendar.get(Calendar.MILLISECOND);
        long timeEnd = c - second * 1000 - millisecond + 3600000;
        ItemTurnOn itemTurnOn = new ItemTurnOn(focusIOS.getName(), true, true
                , c, timeEnd, false
                , false, false, false, false
                , false, false, "", 0.0, 0.0
                , "", "", Constant.TIME
                , c);
        itemTurnOn.setType(ItemTurnOn.TYPE_HOUR);

        App.ins.focusPresetRepository.insertAutomationFocus(itemTurnOn);
    }

    private void insertItemFocusEvening(FocusIOS focusIOS) {

        long c = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(c);
        int second = calendar.get(Calendar.SECOND);
        int millisecond = calendar.get(Calendar.MILLISECOND);
        long timeToEvening;
        boolean isEvening = TimeUtils.isEvening();
        if (isEvening) {
            timeToEvening = (((24 - TimeUtils.currentHour(c)) * 60L) - TimeUtils.currentMinute(c)) + 7 * 60;
        } else {
            timeToEvening = ((19 - TimeUtils.currentHour(c)) * 60L) - TimeUtils.currentMinute(c);
        }
        ItemTurnOn itemTurnOn = new ItemTurnOn(focusIOS.getName(), true, true
                , c, c + timeToEvening * 60000 - second * 1000 - millisecond, false
                , false, false, false, false
                , false, false, "", 0.0, 0.0
                , "", "", Constant.TIME
                , c);
        itemTurnOn.setType(ItemTurnOn.TYPE_EVENING);

        App.ins.focusPresetRepository.insertAutomationFocus(itemTurnOn);
    }

    private void insertItemFocusLocation(FocusIOS focusIOS) {

//        if (ActivityCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
//            FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
//            fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY,
//                    cancellationTokenSource.getToken()).addOnSuccessListener(
//                    location -> {
//                        if (location != null) {
//                            String address = "";
//                            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
//                            try {
//                                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//                                if (addresses != null && addresses.size() > 0) {
//                                    address = addresses.get(0).getAddressLine(0);
//                                }
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//
//                            ItemTurnOn itemTurnOn = new ItemTurnOn(focusIOS.getName(), true, true, -1, -1,
//                                    false, false, false, false, false, false, false,
//                                    address, location.getLatitude(), location.getLongitude(), "", "",
//                                    Constant.LOCATION, System.currentTimeMillis());
//
//                            itemTurnOn.setType(ItemTurnOn.TYPE_LOCATION);
//
//                            App.ins.focusPresetRepository.insertAutomationFocus(itemTurnOn);
//
//                            getListFocus();
//                        }
//                    });
//        } else {
//            if (NotyControlCenterServicev614.getInstance() != null) {
//                NotyControlCenterServicev614.getInstance()
//                        .showToast(getContext().getString(R.string.mess_request_permission));
//            }
//        }

    }

    @Subscribe
    public void onEventUpdateFocus(EventUpdateFocus eventUpdateFocus) {
        getListFocus();
    }

    @Subscribe(sticky = true)
    public void onEventSwitchType(EventSwitchType eventSwitchType) {
        getListFocus();
        EventBus.getDefault().removeStickyEvent(eventSwitchType);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent messageEvent) {
        switch (messageEvent.getTypeEvent()) {
            case Constant.UPDATE_TIME_CHANGE:
//            case Constant.UPDATE_LOCATION_CHANGE:
            case Constant.UPDATE_APP_CHANGE:
            case Constant.PACKAGE_APP_REMOVE:
                if (!nameFocusRunningOld.equals(App.getNameFocusRunning())) {
                    getListFocus();
                }
                break;
        }
    }

    private boolean checkGame(List<ItemTurnOn> turnOnList, boolean createTime) {
        boolean isExitApp = false;
        boolean isExitTime = false;
        for (ItemTurnOn itemApp : turnOnList) {
            if (itemApp.getTypeEvent().equals(Constant.APPS) && itemApp.getStart()) {
                if (MethodUtils.packageIsGame(getContext(), itemApp.getPackageName())) {
                    isExitApp = true;
                }
            } else if (itemApp.getTypeEvent().equals(Constant.TIME) && itemApp.getStart()) {
//                long currentTime = System.currentTimeMillis();
//                if (currentTime >= itemApp.getTimeStart() && currentTime < itemApp.getTimeEnd()) {
                isExitTime = true;
//                }
            }
        }
        if (createTime) {
            isExitTime = true;
        }

        return isExitApp && isExitTime;
    }

    private boolean checkWork(List<ItemTurnOn> turnOnList, boolean isChooseLocation, boolean createTime) {
        boolean isExitLocation = false;
        boolean isExitTime = false;
        for (ItemTurnOn itemWork : turnOnList) {
            if (itemWork.getTypeEvent().equals(Constant.LOCATION) && itemWork.getStart()) {
                isExitLocation = true;
            } else if (itemWork.getTypeEvent().equals(Constant.TIME) && itemWork.getStart()) {
                // TODO: 7/21/2022 check time
                long currentTime = System.currentTimeMillis();
                if (currentTime >= itemWork.getTimeStart() && currentTime < itemWork.getTimeEnd()) {
                    isExitTime = true;
                }
            }
        }

        if (createTime) {
            isExitTime = true;
        }

        return /*isExitLocation &&*/ isExitTime;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    public interface OnClickListener {
        void onNewFocus();

        void onSettingFocus(FocusIOS focusIOS);

        void onUpdateView(FocusIOS focusIOS);

        void onCloseViewFocus();

        void onRequestPermissionLocation(FocusIOS focusIOS);

        void onRequestPermissionPhone(FocusIOS focusIOS);

    }

}

package com.tapbi.spark.controlcenter.feature.controlios14.view.control;

import static com.tapbi.spark.controlcenter.App.tinyDB;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.common.models.MessageEvent;
import com.tapbi.spark.controlcenter.data.model.FocusIOS;
import com.tapbi.spark.controlcenter.data.repository.ThemesRepository;
import com.tapbi.spark.controlcenter.eventbus.EventOpen;
import com.tapbi.spark.controlcenter.eventbus.EventSaveControl;
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CallBackUpdateUi;
import com.tapbi.spark.controlcenter.feature.controlcenter.utils.DataMobileUtils;
import com.tapbi.spark.controlcenter.feature.controlios14.manager.AudioManagerUtils;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlBrightnessVolumeIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlCenterIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.ControlMusicIOS18;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.callback.OnClickSettingListener;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.callback.OnLongClickSeekbarListener;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group1.ControlAirplaneRecordSynDataView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group1.ControlAirplaneView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group1.ControlViewTopIOS18;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group1.SettingExpandView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group1.SettingSquareTextView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group1.SettingView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group1.SettingViewHorizontal;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group1.SettingViewSquare;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group1.SettingViewVertical;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group1.SynDataTextView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group2.ControlMusicIosSquareView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group2.ControlMusicIosView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group2.MusicExpandView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group2.MusicView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group3.FocusView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group3.RotateRectangleView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group3.RotateView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group3.ScreenTimeoutSettingView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group3.ScreenTimeoutSquareView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group3.ScreenTimeoutView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group3.SilentImageView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group3.SilentRectangleView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group3.SilentView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group4.BrightnessExpandView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group4.BrightnessTextView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group4.SettingBrightnessView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group4.SettingBrightnessView2;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group4.SettingVolumeTextView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group4.SettingVolumeView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group4.SettingVolumeView2;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group4.VolumeExpandView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5.AlarmTextView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5.CalculatorActionView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5.CalculatorTextView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5.CameraAcitonView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5.CameraTextView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5.ControlRotateRecordFlashDarkmode;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5.CustomControlImageView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5.DarkModeActionView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5.DarkModeTextView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5.EditControlView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5.FlashLightView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5.FlashlightTextView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5.LowPowerActionView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5.LowPowerTextView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5.NoteActionView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5.NoteTextView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5.ScreenRecordActionView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5.ScreenRecordTextView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5.TimeActionView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.status.StatusControlView;
import com.tapbi.spark.controlcenter.ui.splash.SplashActivity;
import com.tapbi.spark.controlcenter.utils.ControlCustomizeManager;
import com.tapbi.spark.controlcenter.utils.MediaUtils;
import com.tapbi.spark.controlcenter.utils.SettingUtils;
import com.tapbi.spark.controlcenter.utils.TinyDB;
import com.tapbi.spark.controlcenter.utils.VibratorUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class CreateItemViewControlCenterIOS {

    public static boolean isTouchDarkmore = false;
    private final long DURATION_ANIMATION = 300;
    private final List<String> customControl = new ArrayList<>();
    public BluetoothAdapter mBluetoothAdapter;
    public SettingVolumeView settingVolumeView;
    public SettingVolumeTextView settingVolumeTextView;
    public SettingVolumeView2 settingVolumeView2;
    private Context context;
    private ConstraintLayout root;
    private ConstraintLayout clControl;
    private ScrollView scrollMain;
    private ConstraintLayout containerControl;
    private SettingView settingView;
    private SettingViewHorizontal settingViewHorizontal;
    private SettingViewSquare settingViewSquare;
    private SettingSquareTextView settingSquareTextView;
    private SettingViewVertical settingViewVertical;
    private RotateView rotateView;
    private RotateRectangleView rotateRectangleView;
    private SilentImageView silentView;
    private SilentRectangleView silentRectangleView;
    private SettingExpandView settingExpandView;
    private boolean isShowSettingExpand;
    private MusicView musicView;
    private ControlMusicIosView controlMusicIosView;
    private ControlMusicIosSquareView controlMusicIosSquareView;
    private ControlViewTopIOS18 controlViewTopIOS18;
    public ControlMusicIOS18 controlMusicIOS18;
    private MusicExpandView musicExpandView;
    private boolean isShowMusicExpand;
    private ScreenTimeoutView screenTimeoutAction;
    private ScreenTimeoutSquareView screenTimeoutSquareView;
    private ConstraintLayout screenTimeoutLayout;
    private boolean isShowScreenTimeout;
    private FocusView focusLayout;
    private boolean isShowFocusLayout;
    private BrightnessExpandView brightnessExpandView;
    private SettingBrightnessView settingBrightnessView;
    private SettingBrightnessView2 settingBrightnessView2;
    private BrightnessTextView brightnessTextView;
    //    private SettingBrightnessView2 settingBrightnessView;
    private boolean showBrightnessExpand;
    private VolumeExpandView volumeExpandView;
    private boolean showVolumeExpanded;
    //    private RelativeLayoutAnimation[] controls;
    private StatusControlView statusControlView;
    private DataSetupViewControlModel dataSetupViewControlModel;
    private Boolean stateWifi = true;
    private Boolean stateAirplane = false;
    private Boolean stateBluetooth = false;

    private MediaUtils controlMusicUtils;
    private final CallBackUpdateUi callBackUpdateUi = new CallBackUpdateUi() {
        @Override
        public void stage(String valueRegister, boolean b, int pos) {
            if (valueRegister.equals("Bluetooth")) {
                updateViewBluetooth(b);
            } else if (valueRegister.equals("Location")) {
                updateViewLocation(b);
            }
        }
    };
    private ControlBrightnessVolumeIosModel controlVolume;
    private ControlBrightnessVolumeIosModel controlBrightness;
    private Boolean stateDataMobile = false;
    private Boolean stateLowPower = false;
    private int volume = 0;
    private int orientation = Configuration.ORIENTATION_PORTRAIT;
    private int styleControl = Constant.STYLE_CONTROL_TOP;
    private FlashLightView flashLightView;
    private EditControlView editControlView;
    private FlashlightTextView flashlightTextView;
    private ControlCenterIOSView.OnControlCenterListener onControlCenterListener;
    private ImageView imageViewClose;
    private ImageView imageViewTouchClose;
    private boolean isSettingTouching = false;
    private boolean isSilentTouching = false;
    private boolean isScreenTimeTouching = false;
    private boolean isBrightnessTouching = false;
    private boolean isVolumeTouching = false;
    private boolean isMusicTouching = false;
    private  ChooseControlView fakeBottomSheetView ;
    private ControlCenterViewIOS18.ShowPage3Listener showPage3Listener;
    public final OnClickSettingListener onClickSettingListener = new OnClickSettingListener() {
        @Override
        public void onClick() {
            hideMainControl();
        }
    };
    private ScreenTimeoutView.OnScreenTimeoutListener onScreenTimeoutListener = new ScreenTimeoutView.OnScreenTimeoutListener() {
        @Override
        public void onDown() {
            if (!isSettingTouching && !isSilentTouching && !isBrightnessTouching && !isVolumeTouching && !isMusicTouching) {
                isScreenTimeTouching = true;
                if (settingView != null) settingView.setViewTouching(true);
                if (settingViewHorizontal != null) settingViewHorizontal.setViewTouching(true);
                if (settingViewSquare != null) settingViewSquare.setViewTouching(true);
                if (controlViewTopIOS18 != null) controlViewTopIOS18.setViewTouching(true);
                if (settingSquareTextView != null) settingSquareTextView.setViewTouching(true);
                if (settingViewVertical != null) settingViewVertical.setViewTouching(true);
                if (controlAirplaneView != null) controlAirplaneView.setViewTouching(true);
                if (controlAirplaneRecordSynDataView != null)
                    controlAirplaneRecordSynDataView.setViewTouching(true);

            }

        }

        @Override
        public void onUp() {
            isScreenTimeTouching = false;
            if (settingView != null) settingView.setViewTouching(false);
            if (settingViewHorizontal != null) settingViewHorizontal.setViewTouching(false);
            if (settingViewSquare != null) settingViewSquare.setViewTouching(false);
            if (controlViewTopIOS18 != null) controlViewTopIOS18.setViewTouching(false);
            if (settingSquareTextView != null) settingSquareTextView.setViewTouching(false);
            if (settingViewVertical != null) settingViewVertical.setViewTouching(false);
            if (controlAirplaneView != null) controlAirplaneView.setViewTouching(false);
            if (controlAirplaneRecordSynDataView != null)
                controlAirplaneRecordSynDataView.setViewTouching(false);
        }

        @Override
        public void onClick() {
            if (isScreenTimeTouching) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.System.canWrite(context)) {
                        onClickSettingListener.onClick();
                        SettingUtils.intentActivityRequestPermission(context, new String[]{Manifest.permission.WRITE_SETTINGS});
                        return;
                    }
                }
                if (tinyDB.getBoolean(Constant.VIBRATOR_CONTROL_LONG_CLICK, Constant.VALUE_DEFAULT_VIBRATOR)) {
                    VibratorUtils.getInstance(context).vibrator(VibratorUtils.TIME_DEFAULT);
                }
                animationShowTimeoutSetting();
            }
        }

        @Override
        public void onLongClick() {
            if (isScreenTimeTouching) {
                SettingUtils.intentChangeDisplay(context);
                hideMainControl();
            }
        }
    };
    private float yDown, yMove;
    private float progress;
    private boolean close;
    private final View.OnTouchListener touchCloseControl = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    yDown = event.getRawY();
                    close = true;
                    progress = 0;
                    break;
                case MotionEvent.ACTION_MOVE:
                    yMove = event.getRawY();
                    progress = yMove - yDown;
                    if (close && progress < 0 && progress < -20) {
                        close = false;
                        closeControl();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (close) {
                        hideMainControl();
                    }

                    break;

            }
            return true;
        }
    };
    private ControlAirplaneView controlAirplaneView;
    private ControlAirplaneRecordSynDataView controlAirplaneRecordSynDataView;
    private ControlRotateRecordFlashDarkmode controlRotateRecordFlashDarkmode;
    private ScreenRecordActionView screenRecordActionView;
    private ScreenRecordTextView screenRecordTextView;
    private CalculatorActionView calculatorActionView;
    private CalculatorTextView calculatorTextView;
    private NoteActionView noteActionView;
    private NoteTextView noteTextView;
    private CameraAcitonView cameraAcitonView;
    private CameraTextView cameraTextView;
    private TimeActionView timeActionView;
    private AlarmTextView alarmTextView;
    private DarkModeActionView darkModeActionView;
    private DarkModeTextView darkModeTextView;
    private LowPowerActionView lowPowerActionView;
    private LowPowerTextView lowPowerTextView;
    private SynDataTextView synDataTextView;
    private ArrayList<CustomControlImageView> customControlImageViewArrayList = new ArrayList<>();
    private OnLongClickSeekbarListener onLongClickSeekbarListener = new OnLongClickSeekbarListener() {
        @Override
        public void onDown() {
            if (!isSettingTouching && !isSilentTouching && !isScreenTimeTouching && !isVolumeTouching && !isMusicTouching) {
                isBrightnessTouching = true;
            }
        }

        @Override
        public void onUp() {
            isBrightnessTouching = false;
        }

        @Override
        public void onLongClick() {
            if (isBrightnessTouching) {
                if (tinyDB.getBoolean(Constant.VIBRATOR_CONTROL_LONG_CLICK, Constant.VALUE_DEFAULT_VIBRATOR)) {
                    VibratorUtils.getInstance(context).vibrator(VibratorUtils.TIME_DEFAULT);
                }
                animationShowBrightnessSettingExpand();
            }
        }
    };
    private OnLongClickSeekbarListener onLongClickVolumeSeekbarListener = new OnLongClickSeekbarListener() {
        @Override
        public void onDown() {
            if (!isSettingTouching && !isSilentTouching && !isScreenTimeTouching && !isBrightnessTouching && !isMusicTouching) {
                isVolumeTouching = true;
            }
        }

        @Override
        public void onUp() {
            isVolumeTouching = false;
        }

        @Override
        public void onLongClick() {
            if (isVolumeTouching) {
                if (tinyDB.getBoolean(Constant.VIBRATOR_CONTROL_LONG_CLICK, Constant.VALUE_DEFAULT_VIBRATOR)) {
                    VibratorUtils.getInstance(context).vibrator(VibratorUtils.TIME_DEFAULT);
                }

                animationShowVolumeSettingExpand();
                if (settingVolumeView != null) settingVolumeView.setLongClick(false);
                if (settingVolumeView2 != null) settingVolumeView2.setLongClick(false);
                if (settingVolumeTextView != null) settingVolumeTextView.setLongClick(false);
            }
        }
    };
    private SettingView.OnSettingListener onSettingListener = new SettingView.OnSettingListener() {
        @Override
        public void onDown() {
            if (!isVolumeTouching && !isSilentTouching && !isScreenTimeTouching && !isBrightnessTouching && !isMusicTouching && !ThemesRepository.isControlEditing()) {
                isSettingTouching = true;
            }
        }

        @Override
        public void onUp() {
            isSettingTouching = false;
        }

        @Override
        public void onHide() {
            hideMainControl();
        }

        @Override
        public void onWifiChange() {
//                statusControlView.updateWifi();
        }

        @Override
        public void onBluetoothChange(boolean change) {
//                statusControlView.updateBluetooth(change);
        }

        @Override
        public void onLongClick(View v) {
            if (isSettingTouching || v instanceof ControlViewTopIOS18) {
                if (tinyDB.getBoolean(Constant.VIBRATOR_CONTROL_LONG_CLICK, Constant.VALUE_DEFAULT_VIBRATOR)) {
                    VibratorUtils.getInstance(context).vibrator(VibratorUtils.TIME_DEFAULT);
                }
                if (v instanceof ControlViewTopIOS18){
                    showPage3Listener.onShowPage3();
                } else {
                    animationShowSettingExpand();
                }

            }
        }

        @Override
        public void onClose() {
            onControlCenterListener.onClose();
        }
    };
    public MusicView.OnMusicViewListener onMusicViewListener = new MusicView.OnMusicViewListener() {
        @Override
        public void onDown() {
            if (!isSettingTouching && !isSilentTouching && !isScreenTimeTouching && !isBrightnessTouching && !isVolumeTouching) {
                isMusicTouching = true;
                if (settingView != null) settingView.setViewTouching(true);
                if (settingViewHorizontal != null) settingViewHorizontal.setViewTouching(true);
                if (settingViewSquare != null) settingViewSquare.setViewTouching(true);
                if (controlViewTopIOS18 != null) controlViewTopIOS18.setViewTouching(true);
                if (settingSquareTextView != null) settingSquareTextView.setViewTouching(true);
                if (settingViewVertical != null) settingViewVertical.setViewTouching(true);
                if (controlAirplaneView != null) controlAirplaneView.setViewTouching(true);
                if (controlAirplaneRecordSynDataView != null)
                    controlAirplaneRecordSynDataView.setViewTouching(true);

            }
        }

        @Override
        public void onUp() {
            isMusicTouching = false;
            if (settingView != null) settingView.setViewTouching(false);
            if (settingViewHorizontal != null) settingViewHorizontal.setViewTouching(false);
            if (settingViewSquare != null) settingViewSquare.setViewTouching(false);
            if (controlViewTopIOS18 != null) controlViewTopIOS18.setViewTouching(false);
            if (settingSquareTextView != null) settingSquareTextView.setViewTouching(false);
            if (settingViewVertical != null) settingViewVertical.setViewTouching(false);
            if (controlAirplaneView != null) controlAirplaneView.setViewTouching(false);
            if (controlAirplaneRecordSynDataView != null)
                controlAirplaneRecordSynDataView.setViewTouching(false);

        }

        @Override
        public void onLongClick() {
            if (isMusicTouching) {
                if (tinyDB.getBoolean(Constant.VIBRATOR_CONTROL_LONG_CLICK, Constant.VALUE_DEFAULT_VIBRATOR)) {
                    VibratorUtils.getInstance(context).vibrator(VibratorUtils.TIME_DEFAULT);
                }
                animationShowMusicExpand();
            }
        }

        @Override
        public void onLongClick(View v) {
            if (isMusicTouching) {
                if (tinyDB.getBoolean(Constant.VIBRATOR_CONTROL_LONG_CLICK, Constant.VALUE_DEFAULT_VIBRATOR)) {
                    VibratorUtils.getInstance(context).vibrator(VibratorUtils.TIME_DEFAULT);
                }
                if (showPage3Listener != null){
                    showPage3Listener.onShowPage2();
                }
            }
        }

        @Override
        public void onClickVerify() {
            hideMainControl();
            SettingUtils.intentPermissionNotificationListener(App.mContext);
        }
    };
    private SilentView.FocusListener focusListener = new SilentView.FocusListener() {
        @Override
        public void onDown() {
            if (!isSettingTouching && !isScreenTimeTouching && !isBrightnessTouching && !isVolumeTouching && !isMusicTouching) {
                isSilentTouching = true;
            }

        }

        @Override
        public void onUp() {
            isSilentTouching = false;
        }

        @Override
        public void onClick() {

//                if (!PermissionUtils.INSTANCE.checkPermissionPhone(context)) {
//                    requestPermissionPhone();
//                    return;
//                }

//            FocusIOS focusIOS = silentView != null ? silentView.getFocusIOS() : silentRectangleView.getFocusIOS();
//            if (focusIOS == null) {
//                App.ins.focusPresetRepository.getFocusByName(Constant.DO_NOT_DISTURB).subscribe(new SingleObserver<FocusIOS>() {
//                    @Override
//                    public void onSubscribe(@NonNull Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onSuccess(@NonNull FocusIOS focusIOS) {
//                        focusLayout.turnOnOffFocus(focusIOS);
//                        EventBus.getDefault().post(new EventUpdateFocus());
////                            MethodUtils.intentToCheckPermission(context);
//                    }
//
//                    @Override
//                    public void onError(@NonNull Throwable e) {
//
//                    }
//                });
//            } else {
//                focusLayout.turnOnOffFocus(focusIOS);
////                    App.tinyDB.putString(Constant.FOCUS_START_OLD,"");
//                EventBus.getDefault().post(new EventUpdateFocus());
////                    MethodUtils.intentToCheckPermission(context);
//
//            }
//

        }

        @Override
        public void onLongClick() {
//            if (isSilentTouching) {
//                if (tinyDB.getBoolean(Constant.VIBRATOR_CONTROL_LONG_CLICK, Constant.VALUE_DEFAULT_VIBRATOR)) {
//                    VibratorUtils.getInstance(context).vibrator(VibratorUtils.TIME_DEFAULT);
//                }
//                animationShowFocusLayout();
//            }

        }
    };

    public CreateItemViewControlCenterIOS(Context context, ConstraintLayout root, ConstraintLayout clControl, SettingExpandView settingExpandView, ScreenTimeoutSettingView screenTimeoutLayout, FocusView focusLayout,
                                          BrightnessExpandView brightnessExpandView, VolumeExpandView volumeExpandView, StatusControlView statusControlView, ImageView imageViewClose, ImageView imageViewTouchClose, MusicExpandView musicExpandView
            ,ChooseControlView fakeBottomSheetView,MediaUtils controlMusicUtils, ControlCenterViewIOS18.ShowPage3Listener showPage3Listener
            , DataSetupViewControlModel dataSetupViewControlModel) {
        this.root = root;
        this.clControl = clControl;
        this.settingExpandView = settingExpandView;
        this.focusLayout = focusLayout;
        this.brightnessExpandView = brightnessExpandView;
        this.volumeExpandView = volumeExpandView;
        this.statusControlView = statusControlView;
        this.imageViewClose = imageViewClose;
        this.imageViewTouchClose = imageViewTouchClose;
        this.screenTimeoutLayout = screenTimeoutLayout;
        this.musicExpandView = musicExpandView;
        this.dataSetupViewControlModel = dataSetupViewControlModel;
        this.statusControlView.setFont(dataSetupViewControlModel.getTypefaceText());
        this.fakeBottomSheetView = fakeBottomSheetView;
        this.showPage3Listener = showPage3Listener;
        this.controlMusicUtils = controlMusicUtils;
        init(context);
    }
    public CreateItemViewControlCenterIOS(Context context, ConstraintLayout root, ConstraintLayout clControl, SettingExpandView settingExpandView, ScreenTimeoutSettingView screenTimeoutLayout, FocusView focusLayout,
                                          BrightnessExpandView brightnessExpandView, VolumeExpandView volumeExpandView, StatusControlView statusControlView, ImageView imageViewClose, ImageView imageViewTouchClose, MusicExpandView musicExpandView
            , DataSetupViewControlModel dataSetupViewControlModel) {
        this.root = root;
        this.clControl = clControl;
        this.settingExpandView = settingExpandView;
        this.focusLayout = focusLayout;
        this.brightnessExpandView = brightnessExpandView;
        this.volumeExpandView = volumeExpandView;
        this.statusControlView = statusControlView;
        this.imageViewClose = imageViewClose;
        this.imageViewTouchClose = imageViewTouchClose;
        this.screenTimeoutLayout = screenTimeoutLayout;
        this.musicExpandView = musicExpandView;
        this.dataSetupViewControlModel = dataSetupViewControlModel;
        this.statusControlView.setFont(dataSetupViewControlModel.getTypefaceText());
        this.fakeBottomSheetView = fakeBottomSheetView;
        init(context);
    }


    public CreateItemViewControlCenterIOS(Context context, DataSetupViewControlModel dataSetupViewControlModel) {
        this.context = context;
        this.dataSetupViewControlModel = dataSetupViewControlModel;
    }

    public void setFontType(Typeface typeface) {
        dataSetupViewControlModel.setTypefaceText(typeface);
    }

    protected SettingViewHorizontal getSettingViewHorizontal(ControlCenterIosModel controlCenterIosModel) {
        if (settingViewHorizontal == null) {
            settingViewHorizontal = new SettingViewHorizontal(context, controlCenterIosModel.getControlSettingIosModel());
            settingViewHorizontal.setOnSettingListener(onSettingListener);
        } else {
            settingViewHorizontal.changeControlSettingIos(controlCenterIosModel.getControlSettingIosModel());
        }
        settingViewHorizontal.updateWifi(stateWifi);
        settingViewHorizontal.updateDataMobile(stateDataMobile);
        settingViewHorizontal.updateBgAirplane(stateAirplane);
        settingViewHorizontal.updateBg(stateBluetooth);
        return settingViewHorizontal;
    }

    protected void removeSettingViewHorizontal() {
        if (settingViewHorizontal != null) {
            ((ViewGroup) settingViewHorizontal.getParent()).removeView(settingViewHorizontal);
            settingViewHorizontal = null;
        }
    }

    protected SettingView getSettingView(ControlCenterIosModel controlCenterIosModel) {
        if (settingView == null) {
            settingView = new SettingView(context, controlCenterIosModel.getControlSettingIosModel(), dataSetupViewControlModel);
            settingView.setOnSettingListener(onSettingListener);
        } else {
            settingView.changeControlSettingIos(controlCenterIosModel.getControlSettingIosModel());
        }
        settingView.updateWifi(stateWifi);
        settingView.updateDataMobile(stateDataMobile);
        settingView.updateBgAirplane(stateAirplane);
        settingView.updateBg(stateBluetooth);
        return settingView;
    }

    protected SettingViewSquare getSettingViewSquare(ControlCenterIosModel controlCenterIosModel) {
        if (settingViewSquare == null) {
            settingViewSquare = new SettingViewSquare(context, controlCenterIosModel.getControlSettingIosModel());
            settingViewSquare.setOnSettingListener(onSettingListener);

        } else {
            settingViewSquare.changeControlSettingIos(controlCenterIosModel.getControlSettingIosModel());
        }
        settingViewSquare.updateWifi(stateWifi);
        settingViewSquare.updateDataMobile(stateDataMobile);
        settingViewSquare.updateBgAirplane(stateAirplane);
        settingViewSquare.updateBg(stateBluetooth);
        return settingViewSquare;
    }



    protected SettingSquareTextView getSettingSquareTextView(ControlCenterIosModel controlCenterIosModel) {
        if (settingSquareTextView == null) {
            settingSquareTextView = new SettingSquareTextView(context, controlCenterIosModel.getControlSettingIosModel(), dataSetupViewControlModel);
            settingSquareTextView.setOnSettingListener(onSettingListener);

        } else {
            settingSquareTextView.changeControlSettingIos(controlCenterIosModel.getControlSettingIosModel());
        }
        settingSquareTextView.updateWifi(stateWifi);
        settingSquareTextView.updateDataMobile(stateDataMobile);
        settingSquareTextView.updateBgAirplane(stateAirplane);
        settingSquareTextView.updateBg(stateBluetooth);
        return settingSquareTextView;
    }



    protected SettingViewVertical getSettingViewVertical(ControlCenterIosModel controlCenterIosModel) {
        if (settingViewVertical == null) {
            settingViewVertical = new SettingViewVertical(context, controlCenterIosModel.getControlSettingIosModel(), dataSetupViewControlModel);
            settingViewVertical.setOnSettingListener(onSettingListener);
        } else {
            settingViewVertical.changeControlSettingIos(controlCenterIosModel.getControlSettingIosModel());
        }
        settingViewVertical.updateWifi(stateWifi);
        settingViewVertical.updateDataMobile(stateDataMobile);
        settingViewVertical.updateBg(stateBluetooth);
        return settingViewVertical;
    }

    protected SilentImageView getSilentView(ControlCenterIosModel controlCenterIosModel) {
        if (silentView == null) {
            silentView = new SilentImageView(context, controlCenterIosModel.getControlSettingIosModel(), dataSetupViewControlModel);
//            silentView.setFocusListener(focusListener);
        } else {
            silentView.changeData(controlCenterIosModel.getControlSettingIosModel());
        }

        return silentView;
    }

    protected SilentRectangleView getSilentRectangleView(ControlCenterIosModel controlCenterIosModel) {
        if (silentRectangleView == null) {
            silentRectangleView = new SilentRectangleView(context, controlCenterIosModel.getControlSettingIosModel(), dataSetupViewControlModel);
//            silentRectangleView.setFocusListener(focusListener);
        } else {
            silentRectangleView.changeData(controlCenterIosModel.getControlSettingIosModel());
        }

        return silentRectangleView;
    }

    protected ScreenTimeoutView getScreenTimeoutAction(ControlCenterIosModel controlCenterIosModel) {
        if (screenTimeoutAction == null) {
            screenTimeoutAction = new ScreenTimeoutView(context, controlCenterIosModel.getControlSettingIosModel(), dataSetupViewControlModel);
            screenTimeoutAction.setOnScreenTimeoutListener(onScreenTimeoutListener);
        } else {
            screenTimeoutAction.changeData(controlCenterIosModel.getControlSettingIosModel(), dataSetupViewControlModel);
        }
        return screenTimeoutAction;
    }

    protected ScreenTimeoutSquareView getScreenTimeoutSquareView(ControlCenterIosModel controlCenterIosModel) {
        if (screenTimeoutSquareView == null) {
            screenTimeoutSquareView = new ScreenTimeoutSquareView(context, controlCenterIosModel.getControlSettingIosModel(), dataSetupViewControlModel);
            screenTimeoutSquareView.setOnScreenTimeoutListener(onScreenTimeoutListener);
        } else {
            screenTimeoutSquareView.changeControlSettingIos(controlCenterIosModel.getControlSettingIosModel());
        }
        return screenTimeoutSquareView;
    }

    protected CalculatorActionView getCalculatorActionView(ControlCenterIosModel controlCenterIosModel) {
        if (calculatorActionView == null) {
            calculatorActionView = new CalculatorActionView(context, controlCenterIosModel.getControlSettingIosModel(), dataSetupViewControlModel);
            calculatorActionView.setOnClickSettingListener(onClickSettingListener);
        } else {
            calculatorActionView.changeData(controlCenterIosModel.getControlSettingIosModel());
        }
        return calculatorActionView;
    }

    protected CalculatorTextView getCalculatorTextView(ControlCenterIosModel controlCenterIosModel) {
        if (calculatorTextView == null) {
            calculatorTextView = new CalculatorTextView(context, controlCenterIosModel.getControlSettingIosModel(), dataSetupViewControlModel);
            calculatorTextView.setOnClickSettingListener(onClickSettingListener);
        } else {
//            calculatorTextView.changeData(controlCenterIosModel.getControlSettingIosModel());
        }
        return calculatorTextView;
    }

    protected NoteActionView getNoteActionView(ControlCenterIosModel controlCenterIosModel) {
        if (noteActionView == null) {
            noteActionView = new NoteActionView(context, controlCenterIosModel.getControlSettingIosModel(), dataSetupViewControlModel);
            noteActionView.setOnClickSettingListener(onClickSettingListener);
        } else {
            noteActionView.changeData(controlCenterIosModel.getControlSettingIosModel());
        }
        return noteActionView;
    }

    protected CustomControlImageView getCustomControlImageView(ControlCenterIosModel controlCenterIosModel) {
        if (customControlImageViewArrayList.isEmpty()) {
            return createCustomControlImageView(controlCenterIosModel);
        } else {
            for (CustomControlImageView imageView : customControlImageViewArrayList) {
                if (imageView.getPackage().equals(controlCenterIosModel.getControlSettingIosModel().getIconControl())) {
                    //imageView.changeData(controlCenterIosModel.getControlSettingIosModel());
                    return imageView;
                }
            }
            return createCustomControlImageView(controlCenterIosModel);
        }
//        return createCustomControlImageView(controlCenterIosModel);
    }

    private CustomControlImageView createCustomControlImageView(ControlCenterIosModel controlCenterIosModel) {
        CustomControlImageView customControlImageView = new CustomControlImageView(context, controlCenterIosModel.getControlSettingIosModel());
        customControlImageView.setOnCustomControlImageViewListener(new CustomControlImageView.OnCustomControlImageViewListener() {
            @Override
            public void onClick() {
                hideMainControl();
            }
        });
        customControlImageViewArrayList.add(customControlImageView);
        return customControlImageView;
    }

    protected NoteTextView getNoteTextView(ControlCenterIosModel controlCenterIosModel) {
        if (noteTextView == null) {
            noteTextView = new NoteTextView(context, controlCenterIosModel.getControlSettingIosModel(), dataSetupViewControlModel);
            noteTextView.setOnClickSettingListener(onClickSettingListener);
        } else {
//            noteTextView.changeData(controlCenterIosModel.getControlSettingIosModel());
        }
        return noteTextView;
    }

    protected CameraAcitonView getCameraAcitonView(ControlCenterIosModel controlCenterIosModel) {
        if (cameraAcitonView == null) {
            cameraAcitonView = new CameraAcitonView(context, controlCenterIosModel.getControlSettingIosModel(), dataSetupViewControlModel);
            cameraAcitonView.setOnClickSettingListener(onClickSettingListener);
        } else {
            cameraAcitonView.changeData(controlCenterIosModel.getControlSettingIosModel());
        }
        return cameraAcitonView;
    }

    protected CameraTextView getCameraTextView(ControlCenterIosModel controlCenterIosModel) {
        if (cameraTextView == null) {
            cameraTextView = new CameraTextView(context, controlCenterIosModel.getControlSettingIosModel(), dataSetupViewControlModel);
            cameraTextView.setOnClickSettingListener(onClickSettingListener);
        } else {
//            cameraTextView.changeData(controlCenterIosModel.getControlSettingIosModel());
        }
        return cameraTextView;
    }

    protected TimeActionView getTimeActionView(ControlCenterIosModel controlCenterIosModel) {
        if (timeActionView == null) {
            timeActionView = new TimeActionView(context, controlCenterIosModel.getControlSettingIosModel(), dataSetupViewControlModel);
            timeActionView.setOnClickSettingListener(onClickSettingListener);
        } else {
            timeActionView.changeData(controlCenterIosModel.getControlSettingIosModel());
        }
        return timeActionView;
    }

    protected AlarmTextView getAlarmTextView(ControlCenterIosModel controlCenterIosModel) {
        if (alarmTextView == null) {
            alarmTextView = new AlarmTextView(context, controlCenterIosModel.getControlSettingIosModel(), dataSetupViewControlModel);
            alarmTextView.setOnClickSettingListener(onClickSettingListener);
        } else {
//            alarmTextView.changeData(controlCenterIosModel.getControlSettingIosModel());
        }
        return alarmTextView;
    }

    protected DarkModeActionView getDarkModeActionView(ControlCenterIosModel controlCenterIosModel) {
        if (darkModeActionView == null) {
            darkModeActionView = new DarkModeActionView(context, controlCenterIosModel.getControlSettingIosModel(), dataSetupViewControlModel);
            darkModeActionView.setOnClickSettingListener(onClickSettingListener);
        } else {
            darkModeActionView.changeData(controlCenterIosModel.getControlSettingIosModel());
        }
        return darkModeActionView;
    }

    protected DarkModeTextView getDarkModeTextView(ControlCenterIosModel controlCenterIosModel) {
        if (darkModeTextView == null) {
            darkModeTextView = new DarkModeTextView(context, controlCenterIosModel.getControlSettingIosModel(), dataSetupViewControlModel);
            darkModeTextView.setOnClickSettingListener(onClickSettingListener);
        } else {
//            darkModeTextView.changeData(controlCenterIosModel.getControlSettingIosModel());
        }
        return darkModeTextView;
    }

    protected LowPowerActionView getLowPowerActionView(ControlCenterIosModel controlCenterIosModel) {
        if (lowPowerActionView == null) {
            lowPowerActionView = new LowPowerActionView(context, controlCenterIosModel.getControlSettingIosModel(), dataSetupViewControlModel);
            lowPowerActionView.setOnClickSettingListener(onClickSettingListener);

        } else {
            lowPowerActionView.changeData(controlCenterIosModel.getControlSettingIosModel());
        }
        lowPowerActionView.setStates(stateLowPower);
        return lowPowerActionView;
    }

    protected LowPowerTextView getLowPowerTextView(ControlCenterIosModel controlCenterIosModel) {
        if (lowPowerTextView == null) {
            lowPowerTextView = new LowPowerTextView(context, controlCenterIosModel.getControlSettingIosModel(), dataSetupViewControlModel);
            lowPowerTextView.setOnClickSettingListener(onClickSettingListener);

        } else {
//            lowPowerTextView.changeData(controlCenterIosModel.getControlSettingIosModel());
        }
        lowPowerTextView.setStates(stateLowPower);
        return lowPowerTextView;
    }

    protected SynDataTextView getSynDataTextView(ControlCenterIosModel controlCenterIosModel) {
        if (synDataTextView == null) {
            synDataTextView = new SynDataTextView(context, controlCenterIosModel.getControlSettingIosModel(), dataSetupViewControlModel);
            synDataTextView.setOnClickSettingListener(onClickSettingListener);

        } else {
//            lowPowerTextView.changeData(controlCenterIosModel.getControlSettingIosModel());
        }
//        synDataTextView.setStates(stateLowPower);
        return synDataTextView;
    }

    protected FlashLightView getFlashLightView(ControlCenterIosModel controlCenterIosModel) {
        if (flashLightView == null) {
            flashLightView = new FlashLightView(context, controlCenterIosModel.getControlSettingIosModel(), dataSetupViewControlModel);
        } else {
            flashLightView.changeData(controlCenterIosModel.getControlSettingIosModel());
        }
        return flashLightView;
    }
    protected EditControlView getViewPlus(ControlCenterIosModel controlCenterIosModel) {
        if (editControlView == null) {
            editControlView = new EditControlView(context, controlCenterIosModel.getControlSettingIosModel());
        } else {
            editControlView.changeData(controlCenterIosModel.getControlSettingIosModel());
        }
        return editControlView;
    }


    protected FlashlightTextView getFlashlightTextView(ControlCenterIosModel controlCenterIosModel) {
        if (flashlightTextView == null) {
            flashlightTextView = new FlashlightTextView(context, controlCenterIosModel.getControlSettingIosModel(), dataSetupViewControlModel);
        } else {
            flashlightTextView.changeData(controlCenterIosModel.getControlSettingIosModel());
        }
        return flashlightTextView;
    }

    protected SettingBrightnessView getSettingBrightnessView(ControlCenterIosModel controlCenterIosModel) {
        if (settingBrightnessView == null) {
            settingBrightnessView = new SettingBrightnessView(context, controlCenterIosModel.getControlBrightnessVolumeIosModel(), dataSetupViewControlModel);
            this.controlBrightness = controlCenterIosModel.getControlBrightnessVolumeIosModel();
            settingBrightnessView.loadUiBrightness(SettingUtils.getValueBrightness(context));
            settingBrightnessView.setOnLongClickSeekbarListener(onLongClickSeekbarListener);
            settingBrightnessView.setOnClickSettingListener(onClickSettingListener);
        }
        return settingBrightnessView;
    }

    protected SettingBrightnessView2 getSettingBrightnessView2(ControlCenterIosModel controlCenterIosModel) {
        if (settingBrightnessView2 == null) {
            settingBrightnessView2 = new SettingBrightnessView2(context, controlCenterIosModel.getControlBrightnessVolumeIosModel());
            this.controlBrightness = controlCenterIosModel.getControlBrightnessVolumeIosModel();
            settingBrightnessView2.setOnLongClickSeekbarListener(onLongClickSeekbarListener);
            settingBrightnessView2.setOnClickSettingListener(onClickSettingListener);
        }
        return settingBrightnessView2;
    }

    protected BrightnessTextView getBrightnessTextView(ControlCenterIosModel controlCenterIosModel) {
        if (brightnessTextView == null) {
            brightnessTextView = new BrightnessTextView(context, controlCenterIosModel.getControlBrightnessVolumeIosModel(), dataSetupViewControlModel);
            this.controlBrightness = controlCenterIosModel.getControlBrightnessVolumeIosModel();
            brightnessTextView.setOnLongClickSeekbarListener(onLongClickSeekbarListener);
            brightnessTextView.setOnClickSettingListener(onClickSettingListener);
        }
        return brightnessTextView;
    }

    protected SettingVolumeView getSettingVolumeView(ControlCenterIosModel controlCenterIosModel) {
        if (settingVolumeView == null) {
            volume = AudioManagerUtils.getInstance(context).getVolume();
            settingVolumeView = new SettingVolumeView(context, controlCenterIosModel.getControlBrightnessVolumeIosModel(), dataSetupViewControlModel);
            this.controlVolume = controlCenterIosModel.getControlBrightnessVolumeIosModel();
            if (volumeExpandView != null) {
                volumeExpandView.changeColor(controlVolume.getColorBackgroundSeekbarDefault(), controlVolume.getColorBackgroundSeekbarProgress(), controlVolume.getColorThumbSeekbar(), controlVolume.getCornerBackgroundSeekbar());
            }
            settingVolumeView.setOnLongClickSeekbarListener(onLongClickVolumeSeekbarListener);
            settingVolumeView.updateVolume(volume);
        }
        return settingVolumeView;
    }

    protected SettingVolumeView2 getSettingVolumeView2(ControlCenterIosModel controlCenterIosModel) {
        if (settingVolumeView2 == null) {
            settingVolumeView2 = new SettingVolumeView2(context, controlCenterIosModel.getControlBrightnessVolumeIosModel());
            this.controlVolume = controlCenterIosModel.getControlBrightnessVolumeIosModel();
            if (volumeExpandView != null) {
                volumeExpandView.changeColor(controlVolume.getColorBackgroundSeekbarDefault(), controlVolume.getColorBackgroundSeekbarProgress(), controlVolume.getColorThumbSeekbar(), controlVolume.getCornerBackgroundSeekbar());
            }
            settingVolumeView2.setOnLongClickSeekbarListener(onLongClickVolumeSeekbarListener);
        }
        settingVolumeView2.updateVolume(AudioManagerUtils.getInstance(context).getVolume());
        return settingVolumeView2;
    }

    protected SettingVolumeTextView getSettingVolumeTextView(ControlCenterIosModel controlCenterIosModel) {
        if (settingVolumeTextView == null) {
            settingVolumeTextView = new SettingVolumeTextView(context, controlCenterIosModel.getControlBrightnessVolumeIosModel(), dataSetupViewControlModel);
            this.controlVolume = controlCenterIosModel.getControlBrightnessVolumeIosModel();
            if (volumeExpandView != null) {
                volumeExpandView.changeColor(controlVolume.getColorBackgroundSeekbarDefault(), controlVolume.getColorBackgroundSeekbarProgress(), controlVolume.getColorThumbSeekbar(), controlVolume.getCornerBackgroundSeekbar());

            }
            settingVolumeTextView.setOnLongClickSeekbarListener(onLongClickVolumeSeekbarListener);
        }
        settingVolumeTextView.updateVolume(volume);
        return settingVolumeTextView;
    }

    protected RotateView getRotateView(ControlCenterIosModel controlCenterIosModel) {
        Timber.e("hachung getRotateView:"+rotateView+"/controlCenterIosModel: "+controlCenterIosModel.getControlSettingIosModel().getBackgroundImageViewItem());
        if (rotateView == null) {
            rotateView = new RotateView(context, controlCenterIosModel.getControlSettingIosModel(), dataSetupViewControlModel);
            rotateView.setOnClickSettingListener(onClickSettingListener);
            rotateView.setOnRotateChangeListener(() -> {
                if (statusControlView != null) {
                    statusControlView.updateRotate();
                }
            });
        } else {
            rotateView.changeData(controlCenterIosModel.getControlSettingIosModel());
        }

        return rotateView;
    }

    protected RotateRectangleView getRotateRectangleView(ControlCenterIosModel controlCenterIosModel) {
        if (rotateRectangleView == null) {
            rotateRectangleView = new RotateRectangleView(context, controlCenterIosModel.getControlSettingIosModel(), dataSetupViewControlModel);
            rotateRectangleView.setOnClickSettingListener(onClickSettingListener);
            rotateRectangleView.setOnRotateChangeListener(() -> statusControlView.updateRotate());
        }

        return rotateRectangleView;
    }

    protected ControlRotateRecordFlashDarkmode getControlRotateRecordFlashDarkmode(ControlCenterIosModel controlCenterIosModel) {
        if (controlRotateRecordFlashDarkmode == null) {
            controlRotateRecordFlashDarkmode = new ControlRotateRecordFlashDarkmode(context, controlCenterIosModel.getControlSettingIosModel(), dataSetupViewControlModel);
            controlRotateRecordFlashDarkmode.getRotateView().setOnClickSettingListener(onClickSettingListener);
            controlRotateRecordFlashDarkmode.getRotateView().setOnRotateChangeListener(() -> statusControlView.updateRotate());
        }

        return controlRotateRecordFlashDarkmode;
    }

    protected MusicView getMusicView(ControlCenterIosModel controlCenterIosModel) {
        if (musicView == null) {
            musicView = new MusicView(context, controlCenterIosModel.getControlMusicIosModel(), dataSetupViewControlModel);
            musicView.setOnClickSettingListener(onClickSettingListener);
            musicView.setOnMusicViewListener(onMusicViewListener);
        }
        return musicView;
    }

    protected ControlMusicIosView getControlMusicIosView(ControlCenterIosModel controlCenterIosModel) {
        if (controlMusicIosView == null) {
            controlMusicIosView = new ControlMusicIosView(context, controlCenterIosModel.getControlMusicIosModel(), dataSetupViewControlModel);
            controlMusicIosView.setOnClickSettingListener(onClickSettingListener);
            controlMusicIosView.setOnMusicViewListener(onMusicViewListener);
        } else {
            controlMusicIosView.changeControlMusicIOS(controlCenterIosModel.getControlMusicIosModel(), dataSetupViewControlModel);
        }
        return controlMusicIosView;
    }

    protected ControlMusicIosSquareView getControlMusicIosSquareView(ControlCenterIosModel controlCenterIosModel) {
        if (controlMusicIosSquareView == null) {
            controlMusicIosSquareView = new ControlMusicIosSquareView(context, controlCenterIosModel.getControlMusicIosModel(), dataSetupViewControlModel);
            controlMusicIosSquareView.setOnClickSettingListener(onClickSettingListener);
            controlMusicIosSquareView.setOnMusicViewListener(onMusicViewListener);
        }
        return controlMusicIosSquareView;
    }
    protected ControlViewTopIOS18 getControlViewTopIOS18(ControlCenterIosModel controlCenterIosModel) {
        if (controlViewTopIOS18 == null) {
            controlViewTopIOS18 = new ControlViewTopIOS18(context, controlCenterIosModel.getControlSettingIosModel());
            controlViewTopIOS18.setOnSettingListener(onSettingListener);
        }else {
            controlViewTopIOS18.changeControlSettingIos(controlCenterIosModel.getControlSettingIosModel());
        }
        controlViewTopIOS18.updateWifi(stateWifi);
        controlViewTopIOS18.updateDataMobile(stateDataMobile);
        controlViewTopIOS18.updateBgAirplane(stateAirplane);
        controlViewTopIOS18.updateBg(stateBluetooth);
        controlViewTopIOS18.updateSync();
        controlViewTopIOS18.updateLocation();
        return controlViewTopIOS18;
    }
    protected ControlMusicIOS18 getControlMusicIOS18(ControlCenterIosModel controlCenterIosModel) {
        if (controlMusicIOS18 == null) {
            controlMusicIOS18 = new ControlMusicIOS18(context, controlCenterIosModel.getControlMusicIosModel(), dataSetupViewControlModel,controlMusicUtils);
            controlMusicIOS18.setOnClickSettingListener(onClickSettingListener);
            controlMusicIOS18.setOnMusicViewListener(onMusicViewListener);
        } else {
            controlMusicIOS18.changeControlMusicIOS(controlCenterIosModel.getControlMusicIosModel(), dataSetupViewControlModel);
        }
        return controlMusicIOS18;

    }


    protected ControlAirplaneView getControlAirplaneView(ControlCenterIosModel controlCenterIosModel) {
        if (controlAirplaneView == null) {
            controlAirplaneView = new ControlAirplaneView(context, controlCenterIosModel.getControlSettingIosModel(), dataSetupViewControlModel);
            controlAirplaneView.setOnSettingListener(onSettingListener);
        } else {
            controlAirplaneView.changeControlSettingIos(controlCenterIosModel.getControlSettingIosModel());
        }
        controlAirplaneView.updateBgAirplane(stateAirplane);
        return controlAirplaneView;
    }

    protected ScreenRecordActionView getScreenRecordActionView(ControlCenterIosModel controlCenterIosModel) {
        if (screenRecordActionView == null) {
            screenRecordActionView = new ScreenRecordActionView(context, controlCenterIosModel.getControlSettingIosModel(), dataSetupViewControlModel);
            screenRecordActionView.setOnClickSettingListener(onClickSettingListener);
        } else {
            screenRecordActionView.changeData(controlCenterIosModel.getControlSettingIosModel());
        }
        return screenRecordActionView;
    }

    protected ScreenRecordTextView getScreenRecordTextView(ControlCenterIosModel controlCenterIosModel) {
        if (screenRecordTextView == null) {
            screenRecordTextView = new ScreenRecordTextView(context, controlCenterIosModel.getControlSettingIosModel(), dataSetupViewControlModel);
            screenRecordTextView.setOnClickSettingListener(onClickSettingListener);
        } else {
            screenRecordTextView.changeData(controlCenterIosModel.getControlSettingIosModel());
        }
        return screenRecordTextView;
    }

    protected ControlAirplaneRecordSynDataView getControlAirplaneRecordSynDataView(ControlCenterIosModel controlCenterIosModel) {
        if (controlAirplaneRecordSynDataView == null) {
            controlAirplaneRecordSynDataView = new ControlAirplaneRecordSynDataView(context, controlCenterIosModel.getControlSettingIosModel(), dataSetupViewControlModel);
            controlAirplaneRecordSynDataView.setOnSettingListener(onSettingListener);
        } else {
//            controlAirplaneRecordSynDataView.changeControlSettingIos(controlCenterIosModel.getControlSettingIosModel());
        }
        controlAirplaneRecordSynDataView.updateBgAirplane(stateAirplane);
        controlAirplaneRecordSynDataView.updateStateDataSyn();
        return controlAirplaneRecordSynDataView;
    }

    private void init(Context context) {
        this.context = context;
        tinyDB = new TinyDB(context);
        styleControl = tinyDB.getInt(Constant.STYLE_CONTROL, Constant.STYLE_CONTROL_TOP);
        findViews();
//        setBgNew();
    }

    private void closeControl() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageViewTouchClose, "translationY", 0f, -30f);
        animator.setDuration(300).addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@androidx.annotation.NonNull Animator animation) {

            }

            @Override
            public void onAnimationEnd(@androidx.annotation.NonNull Animator animation) {
                imageViewTouchClose.setTranslationY(0);
                hideMainControl();
            }

            @Override
            public void onAnimationCancel(@androidx.annotation.NonNull Animator animation) {

            }

            @Override
            public void onAnimationRepeat(@androidx.annotation.NonNull Animator animation) {

            }
        });
        animator.start();
    }

    private void registerLocation() {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        updateViewLocation(checkIfLocationOpened());
    }

    private boolean checkIfLocationOpened() {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) || manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            return true;
        }
        // otherwise return false
        return false;
    }

    private void registerBluetooth() {
        if (mBluetoothAdapter != null) {
            updateViewBluetooth(mBluetoothAdapter.isEnabled());
            return;
        }
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            updateViewBluetooth(mBluetoothAdapter.isEnabled());
        }
    }

    private void registerLowPowerMode() {
        //power mode


    }

    public void setStatesLowPower(boolean turnOn) {
        stateLowPower = turnOn;
        if (lowPowerActionView != null) {
            lowPowerActionView.setStates(turnOn);
        }
        if (lowPowerTextView != null) {
            lowPowerTextView.setStates(turnOn);
        }
    }

    public void updateViewWifi(boolean b) {
        statusControlView.updateWifi(b);
        stateWifi = b;
        if (settingView != null) settingView.updateWifi(b);
        if (settingViewHorizontal != null) settingViewHorizontal.updateWifi(b);
        if (settingViewSquare != null) settingViewSquare.updateWifi(b);
        if (controlViewTopIOS18 != null) controlViewTopIOS18.updateWifi(b);
        if (settingSquareTextView != null) settingSquareTextView.updateWifi(b);
        if (settingViewVertical != null) settingViewVertical.updateWifi(b);
        settingExpandView.updateWifi(b);

    }


    public void updateViewDarkMode(boolean b) {
        if (darkModeActionView != null) {
            darkModeActionView.changeIsSelect(b);
        }
        if (darkModeTextView != null) {
            darkModeTextView.changeIsSelect(b);
        }

    }

    public void updateFlash(boolean b) {
        if (flashLightView != null) flashLightView.setBg(b);
        if (flashlightTextView != null) flashlightTextView.setBg(b);
        if (controlRotateRecordFlashDarkmode != null)
            controlRotateRecordFlashDarkmode.setBgFlash(b);


    }

    public void updateVolume(int volume) {
        this.volume = volume;
        Log.e("duongcvc", "updateVolume: " + volume);
        if (settingVolumeView != null) {
            settingVolumeView.updateVolume(volume);
        }
        if (settingVolumeTextView != null) {
            settingVolumeTextView.updateVolume(volume);
        }
        if (settingVolumeView2 != null) {
            settingVolumeView2.updateVolume(volume);
        }
        if (volumeExpandView != null) {

            volumeExpandView.updateVolumeAudio(volume);
        }
        if (musicExpandView != null) {
            musicExpandView.updateVolume(volume);
        }

    }

    public void updateViewDataMobile(boolean b) {
        stateDataMobile = b;
        if (settingView != null) settingView.updateDataMobile(b);
        if (settingViewHorizontal != null) settingViewHorizontal.updateDataMobile(b);
        if (settingViewSquare != null) settingViewSquare.updateDataMobile(b);
        if (controlViewTopIOS18 != null) controlViewTopIOS18.updateDataMobile(b);
        if (settingSquareTextView != null) settingSquareTextView.updateDataMobile(b);
        if (settingViewVertical != null) settingViewVertical.updateDataMobile(b);
        settingExpandView.updateDataMobile(b);
        statusControlView.updateDataMobile();
    }

    public void updateViewAirplane(boolean b) {
        stateAirplane = b;
        if (settingView != null) settingView.updateBgAirplane(b);
        if (settingViewHorizontal != null) settingViewHorizontal.updateBgAirplane(b);
        if (settingViewSquare != null) settingViewSquare.updateBgAirplane(b);
        if (controlViewTopIOS18 != null) controlViewTopIOS18.updateBgAirplane(b);
        if (settingSquareTextView != null) settingSquareTextView.updateBgAirplane(b);
//        if(settingViewVertical != null)settingViewVertical.updateBgAirplane(b);
        if (controlAirplaneView != null) controlAirplaneView.updateBgAirplane(b);
        if (controlAirplaneRecordSynDataView != null)
            controlAirplaneRecordSynDataView.updateBgAirplane(b);
        settingExpandView.updateAriMode(b);
        statusControlView.updatePlaneMode(b);
    }

    public void updateViewLocation(boolean b) {
        statusControlView.updateBgLocation(b);
        settingExpandView.updateLocation(b);
        if (controlViewTopIOS18 != null) controlViewTopIOS18.updateLocation();
    }

    public void updateSync(){
        if (controlViewTopIOS18 != null) controlViewTopIOS18.updateSync();
    }

    public void updateViewBluetooth(boolean b) {
        stateBluetooth = b;
        statusControlView.updateBgBluetooth(b);
        if (settingView != null) settingView.updateBg(b);
        if (settingViewHorizontal != null) settingViewHorizontal.updateBg(b);
        if (settingViewSquare != null) settingViewSquare.updateBg(b);
        if (controlViewTopIOS18 != null) controlViewTopIOS18.updateBg(b);
        if (settingSquareTextView != null) settingSquareTextView.updateBg(b);
        if (settingViewVertical != null) settingViewVertical.updateBg(b);
        settingExpandView.enableBluetooth = b;
        settingExpandView.updateBluetooth();
    }

    public void setonSignalsChange(int lever) {
        if (statusControlView != null) {
            statusControlView.setonSignalsChange(lever);
        }
    }

    public void setChangeBattery(boolean isChange, int lever) {
        if (statusControlView != null) {
            statusControlView.changeBattery(isChange, lever);
        }
    }

    private void findViews() {

        imageViewTouchClose.setOnTouchListener(touchCloseControl);

        ConstraintLayout.LayoutParams paramsStatus = (ConstraintLayout.LayoutParams) statusControlView.getLayoutParams();
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            paramsStatus.topMargin = (int) (1.5f * App.statusBarHeight);
        } else {
            paramsStatus.topMargin = (int) (1f * App.statusBarHeight);
        }
        statusControlView.requestLayout();

        root.setOnClickListener(v -> setHideViewExpand());

        settingExpandView.setOnSettingExpandListener(() -> {
            animationHideSettingExpand();
            new Handler().postDelayed(this::hideMainControl, 300);
        });

        focusLayout.setOnClickListener(new FocusView.OnClickListener() {
            @Override
            public void onNewFocus() {
                animationHideFocusLayout();
                boolean running = App.isStartActivity;
                if (running) {
                    EventBus.getDefault().post(new EventOpen(Constant.ADD_FOCUS, -1));
                } else {
                    Intent intent = new Intent(context, SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setAction(Constant.ADD_FOCUS);
                    context.startActivity(intent);
                }
                new Handler().postDelayed(() -> hideMainControl(), 500);
            }

            @Override
            public void onSettingFocus(FocusIOS focusIOS) {
                animationHideFocusLayout();
                boolean running = App.isStartActivity;
                if (running) {
                    EventBus.getDefault().post(new EventOpen(Constant.SETTING_FOCUS, focusIOS.getId()));
                } else {
                    Intent intent = new Intent(context, SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setAction(Constant.SETTING_FOCUS);
                    intent.putExtra(Constant.ID_FOCUS_SETTING, focusIOS.getId());
                    context.startActivity(intent);
                }

                new Handler().postDelayed(() -> hideMainControl(), 500);

            }

            @Override
            public void onUpdateView(FocusIOS focusIOS) {
//                if (silentView != null) silentView.setFocusIOS(focusIOS);
//                if (silentRectangleView != null) silentRectangleView.setFocusIOS(focusIOS);
            }

            @Override
            public void onCloseViewFocus() {
                animationHideFocusLayout();
            }

            @Override
            public void onRequestPermissionLocation(FocusIOS focusIOS) {
//                animationHideFocusLayout();
//                EventBus.getDefault().post(new EventFinishMain());
//
//                new Handler().postDelayed(() -> {
//                    hide();
//
//                    Intent intent = new Intent(context, MainActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.setAction(Constant.REQUEST_PERMISSION_LOCATION);
//                    intent.putExtra(Constant.ID_FOCUS_SETTING, focusIOS.getId());
//                    context.startActivity(intent);
//
//                }, 500);
            }

            @Override
            public void onRequestPermissionPhone(FocusIOS focusIOS) {
//                requestPermissionPhone();
            }

        });


//        controls = new RelativeLayoutAnimation[]{findViewById(R.id.view_control_1), findViewById(R.id.view_control_2), findViewById(R.id.view_control_3), findViewById(R.id.view_control_4), findViewById(R.id.view_control_5), findViewById(R.id.view_control_6), findViewById(R.id.view_control_7), findViewById(R.id.view_control_8), findViewById(R.id.view_control_9), findViewById(R.id.view_control_10), findViewById(R.id.view_control_11)};
        registerBluetooth();
        registerLocation();
        setStatesLowPower(SettingUtils.isLocationTurnOn(context));


//        setBackground();
        updateControlCustom();
    }

    /**
     * Gets the layout container of view control. Layout ORIENTATION_PORTRAIT use ScrollView contain ConstraintLayout, ORIENTATION_LANDSCAPE use ConstraintLayout
     *
     * @return layout container of view control, ScrollView or ConstraintLayout
     */
    private View getViewLayoutControl() {
//        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
//            return scrollMain;
//        } else {
//            return containerControl;
//        }
        return clControl;
    }

    private void updateControlCustom() {
        //int paddingIcon = (int) DensityUtils.pxFromDp(context, 15);
//        removeAll();
//        customControl.clear();
//        customControl.addAll(Arrays.asList(ControlCustomizeManager.getInstance(context).getListControlsSave()));
//        customControl.add(Constant.ACTION_CUSTOMIZE_CONTROL);
//        customControl.add(Constant.ACTION_COLOR);
//        customControl.add(Constant.ACTION_EDGE_TRIGGERS);
//
//        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        for (int i = 0; i < customControl.size(); i++) {
//            controls[i].setVisibility(VISIBLE);
//            switch (customControl.get(i)) {
//                case Constant.STRING_ACTION_FLASH_LIGHT:
//                    flashLightView = new FlashLightView(context);
//                    flashLightView.setParentView(controls[i]);
//                    controls[i].addView(flashLightView, params);
//                    //controls[i].setBackgroundColor(Color.TRANSPARENT);
//                    break;
//                case Constant.CLOCK:
//                    TimeActionView timeActionView = new TimeActionView(context);
//                    timeActionView.setOnClickSettingListener(onClickSettingListener);
//                    timeActionView.setParentView(controls[i]);
//                    controls[i].addView(timeActionView, params);
//                    break;
//                case Constant.CALCULATOR:
//                    CalculatorActionView calculatorActionView = new CalculatorActionView(context);
//                    calculatorActionView.setOnClickSettingListener(onClickSettingListener);
//                    calculatorActionView.setParentView(controls[i]);
//                    controls[i].addView(calculatorActionView, params);
//                    break;
//                case Constant.CAMERA:
//                    CameraAcitonView cameraAcitonView = new CameraAcitonView(context);
//                    cameraAcitonView.setOnClickSettingListener(onClickSettingListener);
//                    cameraAcitonView.setParentView(controls[i]);
//                    controls[i].addView(cameraAcitonView, params);
//                    break;
//                case Constant.RECORD:
//                    ScreenRecordActionView recordActionView = new ScreenRecordActionView(context);
//                    recordActionView.setOnClickSettingListener(onClickSettingListener);
//                    recordActionView.setParentView(controls[i]);
////                    recordActionView.setPadding(paddingIcon);
//                    controls[i].addView(recordActionView, params);
//                    break;
//                case Constant.DARK_MODE:
//                    DarkModeActionView darkModeActionView = new DarkModeActionView(context);
//                    darkModeActionView.setOnClickSettingListener(onClickSettingListener);
//                    darkModeActionView.setParentView(controls[i]);
//                    controls[i].addView(darkModeActionView, params);
//                    break;
//                case Constant.STRING_ACTION_BATTERY:
//                    lowPowerActionView = new LowPowerActionView(context);
//                    lowPowerActionView.setOnClickSettingListener(onClickSettingListener);
//                    lowPowerActionView.setParentView(controls[i]);
//                    controls[i].addView(lowPowerActionView, params);
//                    break;
//                case Constant.NOTE:
//                    NoteActionView noteActionView = new NoteActionView(context);
//                    noteActionView.setOnClickSettingListener(onClickSettingListener);
//                    noteActionView.setParentView(controls[i]);
//                    controls[i].addView(noteActionView, params);
//                    break;
//                case Constant.ACTION_CUSTOMIZE_CONTROL:
//                    CustomizeActionView customizeActionView = new CustomizeActionView(context);
//                    customizeActionView.setOnClickSettingListener(onClickSettingListener);
//                    customizeActionView.setParentView(controls[i]);
//                    controls[i].addView(customizeActionView, params);
//                    break;
//                case Constant.ACTION_COLOR:
//                    ColorActionView colorActionView = new ColorActionView(context);
//                    colorActionView.setOnClickSettingListener(onClickSettingListener);
//                    colorActionView.setParentView(controls[i]);
//                    controls[i].addView(colorActionView, params);
//                    break;
//                case Constant.ACTION_EDGE_TRIGGERS:
//                    EdgeActionView edgeActionView = new EdgeActionView(context);
//                    edgeActionView.setOnClickSettingListener(onClickSettingListener);
//                    edgeActionView.setParentView(controls[i]);
//                    controls[i].addView(edgeActionView, params);
//                    break;
//                default:
//                    if (customControl.get(i).isEmpty()) {
//                        controls[i].setVisibility(GONE);
//                    } else {
//                        CustomControlImageView imageView = new CustomControlImageView(context);
//                        imageView.setParentView(controls[i]);
//                        Drawable drawable = MethodUtils.getIconFromPackageName(context, customControl.get(i));
//                        if (drawable != null) {
//                            imageView.setImageDrawable(drawable);
//                        }
//                        final int finalI = i;
//
//                        imageView.setOnCustomControlImageViewListener(() -> {
//                            hideMainControl();
//                            SettingUtils.intentOtherApp(context, customControl.get(finalI));
//                        });
//                        controls[i].addView(imageView, params);
//                    }
//                    break;
//            }
//        }
    }

    public void setHideViewExpand() {
        Timber.e("NVQ setHideViewExpand");
        if (fakeBottomSheetView != null && fakeBottomSheetView.getParent() != null){
            fakeBottomSheetView.hideView();
            return;
        }

        if (!showBrightnessExpand && !isShowScreenTimeout && !showVolumeExpanded && !isShowSettingExpand && !isShowMusicExpand && !isShowFocusLayout) {
            hideMainControl();
            return;
        }

        if (isShowMusicExpand) {
            animationHideMusicExpand();
            return;
        }

        if (isShowSettingExpand) {
            animationHideSettingExpand();
            return;
        }

        if (isShowScreenTimeout) {
            animationHideTimeoutSetting();
            return;
        }

        if (showBrightnessExpand) {
            animationHideBrightnessSettingExpand();
            return;
        }

        if (isShowFocusLayout) {
            animationHideFocusLayout();
            return;
        }

        if (showVolumeExpanded) {
            animationHideVolumeSettingExpand();
        }

    }

    private void animationShowMusicExpand() {
        isShowMusicExpand = true;
        animationHideLayoutMain();

        musicExpandView.clearAnimation();

        musicExpandView.setAlpha(0f);
        musicExpandView.setScaleX(0.5f);
        musicExpandView.setScaleY(0.5f);
        musicExpandView.setVisibility(View.VISIBLE);

        musicExpandView.animate().alpha(1f)/*.translationX(0).translationY(0)*/.scaleX(1).scaleY(1).setDuration(DURATION_ANIMATION).setListener(null).start();
    }

    private void animationHideMusicExpand() {
        isShowMusicExpand = false;
        animationShowLayoutMain();

        musicExpandView.clearAnimation();

        musicExpandView.animate().alpha(0).scaleX(0.5f).scaleY(0.5f)/*.translationX(270).translationY(-470)*/.setDuration(DURATION_ANIMATION).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                musicExpandView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    private void animationShowSettingExpand() {
        isShowSettingExpand = true;
        settingExpandView.update();
        animationHideLayoutMain();

        settingExpandView.clearAnimation();

        settingExpandView.setAlpha(0f);
        settingExpandView.setAlpha(0f);
        settingExpandView.setScaleX(0.5f);
        settingExpandView.setScaleY(0.5f);
//        settingExpandView.setTranslationX(-350);
//        settingExpandView.setTranslationY(-400);
        settingExpandView.setVisibility(View.VISIBLE);

        settingExpandView.animate().alpha(1f)/*.translationX(0).translationY(0)*/.scaleX(1).scaleY(1).setDuration(DURATION_ANIMATION).setListener(null).start();
    }

    private void animationHideSettingExpand() {
        isShowSettingExpand = false;
        if (controlAirplaneRecordSynDataView != null)
            controlAirplaneRecordSynDataView.updateStateDataSyn();
        if (synDataTextView != null) synDataTextView.updateData();
        if (settingView != null) settingView.updateState();
        if (settingViewHorizontal != null) settingViewHorizontal.updateState();
        if (settingViewSquare != null) settingViewSquare.updateState();
        if (controlViewTopIOS18 != null) controlViewTopIOS18.updateState();
        if (settingSquareTextView != null) settingSquareTextView.updateState();
        if (settingViewVertical != null) settingViewVertical.updateState();


        animationShowLayoutMain();

        settingExpandView.clearAnimation();

        settingExpandView.animate().alpha(0).scaleX(0.5f).scaleY(0.5f)/*.translationX(-300).translationY(-350)*/.setDuration(DURATION_ANIMATION).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                settingExpandView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    private void animationShowVolumeSettingExpand() {
        showVolumeExpanded = true;
//        if (controlVolume != null) {
//            volumeExpandView.changeColor(controlVolume.getColorBackgroundSeekbarDefault(), controlVolume.getColorBackgroundSeekbarProgress(), controlVolume.getColorThumbSeekbar(), controlVolume.getCornerBackgroundSeekbar());
//        }


        volumeExpandView.clearAnimation();
        volumeExpandView.setAlpha(0f);
        volumeExpandView.setScaleX(0.7f);
        volumeExpandView.setScaleY(0.7f);
        volumeExpandView.setVisibility(View.VISIBLE);

        volumeExpandView.animate().alpha(1f).scaleX(1f).scaleY(1f)/*.translationX(0).translationY(0)*/.setDuration(DURATION_ANIMATION).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                Log.d("duongcvc", "onAnimationEnd: ");
                volumeExpandView.updateVolumeAudio(AudioManagerUtils.getInstance(context).getVolume());
//                volumeExpandView.updateVolumeAudio(volume);
                volumeExpandView.updateVolumeSystem();
                volumeExpandView.updateVolumeRingtone();
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        }).start();

        animationHideLayoutMain();
    }

    private void animationHideVolumeSettingExpand() {
        showVolumeExpanded = false;
        animationShowLayoutMain();

        volumeExpandView.clearAnimation();
        volumeExpandView.animate().alpha(0).scaleX(0.7f).scaleY(0.7f)/*.translationX(100).translationY(100)*/.setListener(null).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                volumeExpandView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    private void animationShowBrightnessSettingExpand() {
        showBrightnessExpand = true;
        if (controlBrightness != null) {
            brightnessExpandView.changeColor(controlBrightness.getColorBackgroundSeekbarDefault(), controlBrightness.getColorBackgroundSeekbarProgress(), controlBrightness.getColorThumbSeekbar(), controlBrightness.getCornerBackgroundSeekbar());
        }


        animationHideLayoutMain();
        brightnessExpandView.clearAnimation();

        brightnessExpandView.setVisibility(View.VISIBLE);
        brightnessExpandView.setAlpha(0f);
        brightnessExpandView.setScaleX(0.7f);
        brightnessExpandView.setScaleY(0.7f);
//        brightnessExpandView.setTranslationX(100);
//        brightnessExpandView.setTranslationY(100);

        brightnessExpandView.animate().alpha(1f).scaleX(1f).scaleY(1f)/*.translationX(0).translationY(0)*/.setDuration(DURATION_ANIMATION).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                brightnessExpandView.updateBrightness();
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        }).start();
    }

//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        EventBus.getDefault().register(this);
//
//    }
//
//
//    @Override
//    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        unregister();
//        removeAllViews();
//        EventBus.getDefault().unregister(this);
//    }

    private void animationHideBrightnessSettingExpand() {
        showBrightnessExpand = false;
        if (settingBrightnessView != null) settingBrightnessView.updateIconBrightness();
        if (settingBrightnessView2 != null) settingBrightnessView2.updateIconBrightness();
        if (brightnessTextView != null) brightnessTextView.updateIconBrightness();

        animationShowLayoutMain();

        brightnessExpandView.clearAnimation();
        brightnessExpandView.animate().alpha(0).scaleX(0.7f).scaleY(0.7f)/*.translationX(100).translationY(100)*/.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                brightnessExpandView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    private void animationShowFocusLayout() {
        isShowFocusLayout = true;
        focusLayout.clearAnimation();
        focusLayout.setAlpha(0f);
        focusLayout.setScaleX(0.7f);
        focusLayout.setScaleY(0.7f);
        focusLayout.setVisibility(View.VISIBLE);
        focusLayout.animate().alpha(1f).scaleX(1).scaleY(1).setDuration(DURATION_ANIMATION).setListener(null).start();

        animationHideLayoutMain();
    }

    private void animationHideFocusLayout() {
        isShowFocusLayout = false;
        animationShowLayoutMain();

        focusLayout.clearAnimation();
        focusLayout.animate().alpha(0f).scaleX(0.7f).scaleY(0.7f).setDuration(DURATION_ANIMATION).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                focusLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
//            setHideViewExpand();
//        }
//        return super.dispatchKeyEvent(event);
//    }

    private void animationShowTimeoutSetting() {
        isShowScreenTimeout = true;
        screenTimeoutLayout.clearAnimation();
        screenTimeoutLayout.setAlpha(0f);
        screenTimeoutLayout.setScaleX(0.7f);
        screenTimeoutLayout.setScaleY(0.7f);
        screenTimeoutLayout.setVisibility(View.VISIBLE);
        screenTimeoutLayout.animate().alpha(1f).scaleX(1).scaleY(1).setDuration(DURATION_ANIMATION).setListener(null).start();


        animationHideLayoutMain();
    }

    private void animationHideTimeoutSetting() {
        isShowScreenTimeout = false;

        animationShowLayoutMain();

        screenTimeoutLayout.clearAnimation();
        screenTimeoutLayout.animate().alpha(0f).scaleX(0.7f).scaleY(0.7f).setDuration(DURATION_ANIMATION).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                screenTimeoutLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    private void animationShowLayoutMain() {
        statusControlView.clearAnimation();
        statusControlView.setAlpha(0);
        statusControlView.setVisibility(View.VISIBLE);
        statusControlView.animate().alpha(1).setDuration(DURATION_ANIMATION).start();

        getViewLayoutControl().clearAnimation();
        getViewLayoutControl().setAlpha(0);
        getViewLayoutControl().setVisibility(View.VISIBLE);
        getViewLayoutControl().animate().alpha(1).setDuration(DURATION_ANIMATION).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                getViewLayoutControl().setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {
            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        }).start();
    }

    private void animationHideLayoutMain() {
        statusControlView.clearAnimation();
        statusControlView.animate().alpha(0).setDuration(DURATION_ANIMATION).start();
        getViewLayoutControl().clearAnimation();
        getViewLayoutControl().animate().alpha(0).setDuration(DURATION_ANIMATION).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                getViewLayoutControl().setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    public void setOnControlCenterListener(ControlCenterIOSView.OnControlCenterListener onControlCenterListener) {
        this.onControlCenterListener = onControlCenterListener;
    }

    public boolean isViewShowing(View view) {
        return (view.getVisibility() == View.VISIBLE && view.getAlpha() == 1);
    }

    public void show(ControlCenterIOSView controlCenterIOSView) {
        controlCenterIOSView.animate().cancel();
        controlCenterIOSView.setAlpha(0f);
        controlCenterIOSView.setVisibility(View.VISIBLE);
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            scrollMain.post(() -> scrollMain.fullScroll(styleControl == Constant.STYLE_CONTROL_TOP ? View.FOCUS_UP : View.FOCUS_DOWN));
        }
        controlCenterIOSView.animate().alpha(1).setDuration(200).withEndAction(() -> {
//            statusControlView.animationShow();
//            settingView.animationShow();
//            seekbarBrightness.animationShow();
//            seekbarVolume.animationShow();
//            musicView.animationShow();
//            rotateView.animationShow();
//            silentView.animationShow();
//            screenTimeoutAction.animationShow();
////            screenTimeoutAction.animationShow(listener);
//
//            getViewLayoutControl().setVisibility(VISIBLE);
//
////            for (RelativeLayoutAnimation control : controls) {
////                control.animationShow();
////            }
        }).start();
    }

    private void showView(View view) {
        if (!isViewShowing(view)) {
            view.setScaleX(1f);
            view.setScaleY(1f);
            view.setAlpha(1f);
            view.setVisibility(View.VISIBLE);
        }
    }

    public void hideMainControl() {
//        AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0.1f);
//        alphaAnimation.setDuration(250);
//        root.startAnimation(alphaAnimation);

//        statusControlView.animationHide();
//        if(settingView != null)settingView.animationHide();
//        if(settingViewHorizontal != null)settingViewHorizontal.animationHide();
//        if(settingViewSquare != null)settingViewSquare.animationHide();
//        if(settingViewVertical != null)settingViewVertical.animationHide();
//        if (musicView != null)musicView.animationHide();
//        if (rotateView != null) rotateView.animationHide();
//        if (controlRotateRecordFlashDarkmode != null) controlRotateRecordFlashDarkmode.getRotateView().animationHide();
//        if (silentView != null)silentView.animationHide();
//        if (silentRectangleView != null)silentRectangleView.animationHide();
//        if (screenTimeoutAction != null)screenTimeoutAction.animationHide();
//        if (screenTimeoutSquareView != null)screenTimeoutSquareView.animationHide();
//        if (settingBrightnessView != null )settingBrightnessView.animationHide();
//        if (brightnessTextView != null )brightnessTextView.animationHide();
//        if(settingVolumeView != null )settingVolumeView.animationHide();
//        if(settingVolumeTextView != null )settingVolumeTextView.animationHide();

//        for (RelativeLayoutAnimation control : controls) {
//            control.animationHide();
//        }
        new Handler().postDelayed(() -> {
            if (onControlCenterListener != null) {
                isBrightnessTouching = false;
                isSettingTouching = false;
                isSilentTouching = false;
                isVolumeTouching = false;
                isMusicTouching = false;
                onControlCenterListener.onClose();
            }
        }, 250);

    }

    public void updateState() {
        if (settingView != null) settingView.updateState();
        if (settingViewHorizontal != null) settingViewHorizontal.updateState();
        if (settingViewSquare != null) settingViewSquare.updateState();
        if (controlViewTopIOS18 != null) controlViewTopIOS18.updateState();
        if (settingSquareTextView != null) settingSquareTextView.updateState();
        if (settingViewVertical != null) settingViewVertical.updateState();
        if (rotateView != null) rotateView.updateRotateState(false);
        if (rotateRectangleView != null) rotateRectangleView.updateRotateState(false);
        if (controlRotateRecordFlashDarkmode != null)
            controlRotateRecordFlashDarkmode.getRotateView().updateRotateState(false);
        if (silentView != null) silentView.updateDoNotDisturbState();
        if (silentRectangleView != null) silentRectangleView.updateDoNotDisturbState();
        statusControlView.update();
        if (flashLightView != null) {
            flashLightView.updateFlash();
        }
        if (flashlightTextView != null) {
            flashlightTextView.updateFlash();
        }
        if (controlAirplaneRecordSynDataView != null) {
            controlAirplaneRecordSynDataView.updateStateDataSyn();
            controlAirplaneRecordSynDataView.updateBgAirplane(stateAirplane);
        }
        if (synDataTextView != null) {
            synDataTextView.changeIsSelect(SettingUtils.isSyncAutomaticallyEnable());
        }

        if (settingExpandView != null) {
            settingExpandView.update();
        }
    }

    public void setDoNotDisturb(boolean doNotDisturb) {
        if (silentView != null) silentView.changeIsSelect(doNotDisturb);
        if (silentRectangleView != null) silentRectangleView.updateDoNotDisturbState();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent messageEvent) {
        if (messageEvent.getTypeEvent() == Constant.PACKAGE_APP_REMOVE) {
            ControlCustomizeManager.getInstance(context).replaceCustomControl(customControl, messageEvent.getStringValue());
            updateControlCustom();
        }
    }

    public void unregister() {
        mBluetoothAdapter = null;
    }

    public void updateProcessBrightness() {
        if (settingBrightnessView != null) settingBrightnessView.updateIconBrightness();
        if (settingBrightnessView2 != null) settingBrightnessView2.updateIconBrightness();
        if (brightnessTextView != null) brightnessTextView.updateIconBrightness();
    }

    public void updateStateSim() {
        statusControlView.updateStateSim();
    }

    public void eventUpdate(EventSaveControl eventSaveControl) {
        if (eventSaveControl.getAction() != null && !eventSaveControl.getAction().isEmpty()) {
            if (eventSaveControl.getAction().equals(Constant.ACTION_CHANGE_ITEM_CONTROL)) {
                updateControlCustom();
            } else if (eventSaveControl.getAction().equals(Constant.ACTION_CHANGE_LAYOUT_CONTROL)) {
                unregister();
                styleControl = tinyDB.getInt(Constant.STYLE_CONTROL, Constant.STYLE_CONTROL_TOP);
//                removeAllViews();
//                orientation = new DensityUtils().getOrientationWindowManager(context);
//                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
//                    LayoutInflater.from(context).inflate(styles[styleControl], ControlCenterViewOS.this, true);
//                } else {
//                    LayoutInflater.from(context).inflate(stylesLand[styleControl], ControlCenterViewOS.this, true);
//                }
                findViews();
                updateViewAirplane(SettingUtils.isAirplaneModeOn(context));
                updateViewWifi(SettingUtils.isEnableWifi(context));
                updateViewDataMobile(new DataMobileUtils(context).isDataEnable());
            }
        }
    }

    public interface OnControlCenterListener {
        void onExit();

        void onClose();
    }
}

package com.tapbi.spark.controlcenter.feature.controlios14.view.control;


import static com.tapbi.spark.controlcenter.App.tinyDB;
import static com.tapbi.spark.controlcenter.utils.SettingUtils.checkIfLocationOpened;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.common.models.MessageEvent;
import com.tapbi.spark.controlcenter.data.model.FocusIOS;
import com.tapbi.spark.controlcenter.eventbus.EventOpen;
import com.tapbi.spark.controlcenter.eventbus.EventSaveControl;
import com.tapbi.spark.controlcenter.eventbus.EventUpdateFocus;
import com.tapbi.spark.controlcenter.feature.controlios14.helper.BlurBackground;
import com.tapbi.spark.controlcenter.feature.controlios14.manager.AudioManagerUtils;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.RelativeLayoutAnimation;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.callback.OnClickSettingListener;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.callback.OnLongClickSeekbarListener;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group1.SettingExpandView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group1.SettingView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group2.MusicExpandView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group2.MusicView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group3.FocusView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group3.RotateView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group3.ScreenTimeoutView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group3.SilentImageView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group3.SilentView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group4.BrightnessExpandView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group4.SettingBrightnessView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group4.SettingVolumeView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group4.VolumeExpandView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5.CalculatorActionView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5.CameraAcitonView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5.ColorActionView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5.CustomControlImageView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5.CustomizeActionView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5.DarkModeActionView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5.EdgeActionView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5.FlashLightView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5.LowPowerActionView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5.NoteActionView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5.ScreenRecordActionView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5.TimeActionView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.status.StatusControlView;
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CallBackUpdateUi;
import com.tapbi.spark.controlcenter.feature.controlcenter.utils.DataMobileUtils;
import com.tapbi.spark.controlcenter.ui.splash.SplashActivity;
import com.tapbi.spark.controlcenter.utils.ControlCustomizeManager;
import com.tapbi.spark.controlcenter.utils.DensityUtils;
import com.tapbi.spark.controlcenter.utils.MethodUtils;
import com.tapbi.spark.controlcenter.utils.SettingUtils;
import com.tapbi.spark.controlcenter.utils.VibratorUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import timber.log.Timber;


public class ControlCenterViewOS extends ConstraintLayout {

    private final int[] styles = {R.layout.layout_control_center, R.layout.layout_control_center_style2};
    private final int[] stylesLand = {R.layout.layout_control_center_land, R.layout.layout_control_center_style2_land};
    private final long DURATION_ANIMATION = 300;
    private final List<String> customControl = new ArrayList<>();
    public BluetoothAdapter mBluetoothAdapter;
    public SettingVolumeView seekbarVolume;
    private Context context;
    private ConstraintLayout root;
    private ScrollView scrollMain;
    private ConstraintLayout containerControl;
    private SettingView settingView;
    private RotateView rotateView;
    private SilentImageView silentView;
    private SettingExpandView settingExpandView;
    private boolean isShowSettingExpand;
    private MusicView musicView;
    private MusicExpandView musicExpandView;
    private boolean isShowMusicExpand;
    private ScreenTimeoutView screenTimeoutAction;
    private ConstraintLayout screenTimeoutLayout;
    private boolean isShowScreenTimeout;
    private FocusView focusLayout;
    private boolean isShowFocusLayout;
    private BrightnessExpandView brightnessExpandView;
    private SettingBrightnessView seekbarBrightness;
    private boolean showBrightnessExpand;
    private VolumeExpandView volumeExpandView;
    private boolean showVolumeExpanded;
    private RelativeLayoutAnimation[] controls;
    private StatusControlView statusControlView;

    private final CallBackUpdateUi callBackUpdateUi=new CallBackUpdateUi() {


        @Override
        public void stage(String valueRegister, boolean b, int pos) {
            if (valueRegister.equals("Bluetooth")) {
                updateViewBluetooth(b);
            } else if (valueRegister.equals("Location")) {
                updateViewLocation(b);
            }
        }
    };
    private int orientation = Configuration.ORIENTATION_PORTRAIT;
    private int styleControl = Constant.STYLE_CONTROL_TOP;
    private FlashLightView flashLightView;
    private OnControlCenterListener onControlCenterListener;
    private ImageView background;
    private ImageView imageViewClose;
    private ImageView imageViewTouchClose;
    private LowPowerActionView lowPowerActionView;
    private boolean isSettingTouching = false;
    private boolean isSilentTouching = false;
    private boolean isScreenTimeTouching = false;
    private boolean isBrightnessTouching = false;
    private boolean isVolumeTouching = false;
    private boolean isMusicTouching = false;
    private final OnClickSettingListener onClickSettingListener = new OnClickSettingListener() {
        @Override
        public void onClick() {
            hideMainControl();
        }
    };
    private float yDown, yMove;
    private float progress;
    private boolean close;

    private final OnTouchListener touchCloseControl = new OnTouchListener() {
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

    //private ContentObserver observerVolume;


    public ControlCenterViewOS(Context context) {
        super(context);
        init(context);
    }

    public ControlCenterViewOS(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ControlCenterViewOS(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        //Timber.e(".");
        try {
            removeAllViews();
        } catch (Exception e) {
            Timber.e(e);
        }


        this.context = context;
        styleControl = tinyDB.getInt(Constant.STYLE_CONTROL, Constant.STYLE_CONTROL_TOP);
        orientation = DensityUtils.getOrientationWindowManager(getContext());
        Timber.e("NVQ ControlCenterViewOS");
        LayoutInflater li;
        try {
            li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        } catch (Exception e) {
            li = (LayoutInflater) App.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (li != null) {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                li.inflate(styles[styleControl], this, true);
            } else {
                li.inflate(stylesLand[styleControl], this, true);
            }
            findViews();
            setBgNew();
        }
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

    private void registerBluetooth() {
        if (mBluetoothAdapter != null) {
            updateViewBluetooth(mBluetoothAdapter.isEnabled());
            return;
        }
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        updateViewBluetooth(mBluetoothAdapter.isEnabled());
    }

    private void registerLowPowerMode() {
        //power mode


    }

    public void setStatesLowPower(boolean turnOn) {
        if (lowPowerActionView != null) {
            lowPowerActionView.setStates(turnOn);
        }
    }

    public void updateViewWifi(boolean b) {
        statusControlView.updateWifi(b);
        settingView.updateWifi(b);
        settingExpandView.updateWifi(b);

    }

    public void updateVolume(int volume) {
        if (seekbarVolume != null) {
            seekbarVolume.updateVolume(volume);
        }
        if (volumeExpandView != null) {
            volumeExpandView.updateVolumeAudio(volume);
        }
        if (musicExpandView != null) {
            musicExpandView.updateVolume(volume);
        }

    }

    public void updateViewDataMobile(boolean b) {
        settingView.updateDataMobile(b);
        settingExpandView.updateDataMobile(b);
        statusControlView.updateDataMobile();
    }

    public void updateViewAirplane(boolean b) {
        settingView.updateBgAirplane(b);
        settingExpandView.updateAriMode(b);
        statusControlView.updatePlaneMode(b);
    }

    public void updateViewLocation(boolean b) {
        statusControlView.updateBgLocation(b);
        settingExpandView.updateLocation(b);
    }

    public void updateViewBluetooth(boolean b) {
        statusControlView.updateBgBluetooth(b);
        settingView.updateBg(b);
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
        root = findViewById(R.id.root);
        background = findViewById(R.id.background);
        imageViewClose = findViewById(R.id.actionTouchCloseControl);
        imageViewTouchClose = findViewById(R.id.imgViewTouch);
        imageViewClose.setOnTouchListener(touchCloseControl);
        statusControlView = findViewById(R.id.statusView);
        LayoutParams paramsStatus = (LayoutParams) statusControlView.getLayoutParams();
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            paramsStatus.topMargin = (int) (1.5f * App.statusBarHeight);
        } else {
            paramsStatus.topMargin = (int) (1f * App.statusBarHeight);
        }
        statusControlView.requestLayout();

        settingView = findViewById(R.id.settingView);
        rotateView = findViewById(R.id.rotateView);
        silentView = findViewById(R.id.silientView);
        settingExpandView = findViewById(R.id.settingExpand);

        musicView = findViewById(R.id.musicView);
        musicExpandView = findViewById(R.id.musicExpand);

        containerControl = findViewById(R.id.containerControl);
        screenTimeoutLayout = findViewById(R.id.screenTimeoutLayout);
        focusLayout = findViewById(R.id.view_focus);
        screenTimeoutAction = findViewById(R.id.screenTimeoutAction);
        brightnessExpandView = findViewById(R.id.brightnessExpand);
        seekbarBrightness = findViewById(R.id.seekbarBrightness);
        seekbarVolume = findViewById(R.id.seekbarVolume);
        volumeExpandView = findViewById(R.id.volumeExpand);

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            containerControl.setOnClickListener(v -> setHideViewExpand());
            scrollMain = findViewById(R.id.scrollMain);
        } else {
//            ScrollView scrollView = findViewById(R.id.scrollViewLayoutControl);
//            if (scrollView != null) {
//                OverScrollDecoratorHelper.setUpOverScroll(scrollView);
//            }
        }

        root.setOnClickListener(v -> setHideViewExpand());

        getViewLayoutControl().setVisibility(INVISIBLE);

//        silentView.setFocusListener(new SilentView.FocusListener() {
//            @Override
//            public void onDown() {
//                if (!isSettingTouching && !isScreenTimeTouching && !isBrightnessTouching && !isVolumeTouching && !isMusicTouching) {
//                    isSilentTouching = true;
//                }
//
//            }
//
//            @Override
//            public void onUp() {
//                isSilentTouching = false;
//            }
//
//            @Override
//            public void onClick() {
//
////                if (!PermissionUtils.INSTANCE.checkPermissionPhone(getContext())) {
////                    requestPermissionPhone();
////                    return;
////                }
//
//                FocusIOS focusIOS = silentView.getFocusIOS();
//                if (focusIOS == null) {
//                    App.ins.focusPresetRepository.getFocusByName(Constant.DO_NOT_DISTURB).subscribe(new SingleObserver<FocusIOS>() {
//                        @Override
//                        public void onSubscribe(@NonNull Disposable d) {
//
//                        }
//
//                        @Override
//                        public void onSuccess(@NonNull FocusIOS focusIOS) {
//                            focusLayout.turnOnOffFocus(focusIOS);
//                            EventBus.getDefault().post(new EventUpdateFocus());
////                            MethodUtils.intentToCheckPermission(getContext());
//                        }
//
//                        @Override
//                        public void onError(@NonNull Throwable e) {
//
//                        }
//                    });
//                } else {
//                    focusLayout.turnOnOffFocus(focusIOS);
////                    App.tinyDB.putString(Constant.FOCUS_START_OLD,"");
//                    EventBus.getDefault().post(new EventUpdateFocus());
////                    MethodUtils.intentToCheckPermission(getContext());
//
//                }
//
//
//            }
//
//            @Override
//            public void onLongClick() {
//                if (isSilentTouching) {
//                    if (tinyDB.getBoolean(Constant.VIBRATOR_CONTROL_LONG_CLICK, Constant.VALUE_DEFAULT_VIBRATOR)) {
//                        VibratorUtils.getInstance(context).vibrator(VibratorUtils.TIME_DEFAULT);
//                    }
//                    animationShowFocusLayout();
//                }
//
//            }
//        });

        screenTimeoutAction.setOnScreenTimeoutListener(new ScreenTimeoutView.OnScreenTimeoutListener() {
            @Override
            public void onDown() {
                if (!isSettingTouching && !isSilentTouching && !isBrightnessTouching && !isVolumeTouching && !isMusicTouching) {
                    isScreenTimeTouching = true;
                    settingView.setViewTouching(true);
                }

            }

            @Override
            public void onUp() {
                isScreenTimeTouching = false;
                settingView.setViewTouching(false);
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
                    SettingUtils.intentChangeDisplay(getContext());
                    hideMainControl();
                }
            }
        });

        seekbarBrightness.setOnLongClickSeekbarListener(new OnLongClickSeekbarListener() {
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
        });

        seekbarBrightness.setOnClickSettingListener(onClickSettingListener);

        seekbarVolume.setOnLongClickSeekbarListener(new OnLongClickSeekbarListener() {
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
                    seekbarVolume.setLongClick(false);
                }
            }
        });

//        settingView.setOnSettingListener(new SettingView.OnSettingListener() {
//            @Override
//            public void onDown() {
//                if (!isVolumeTouching && !isSilentTouching && !isScreenTimeTouching && !isBrightnessTouching && !isMusicTouching) {
//                    isSettingTouching = true;
//                }
//            }
//
//            @Override
//            public void onUp() {
//                isSettingTouching = false;
//            }
//
//            @Override
//            public void onHide() {
//                hideMainControl();
//            }
//
//            @Override
//            public void onWifiChange() {
////                statusControlView.updateWifi();
//            }
//
//            @Override
//            public void onBluetoothChange(boolean change) {
////                statusControlView.updateBluetooth(change);
//            }
//
//            @Override
//            public void onLongClick() {
//                if (isSettingTouching) {
//                    if (tinyDB.getBoolean(Constant.VIBRATOR_CONTROL_LONG_CLICK, Constant.VALUE_DEFAULT_VIBRATOR)) {
//                        VibratorUtils.getInstance(context).vibrator(VibratorUtils.TIME_DEFAULT);
//                    }
//                    animationShowSettingExpand();
//                }
//            }
//
//            @Override
//            public void onClose() {
//                onControlCenterListener.onClose();
//            }
//        });

        rotateView.setOnClickSettingListener(onClickSettingListener);
        rotateView.setOnRotateChangeListener(() -> statusControlView.updateRotate());

        musicView.setOnClickSettingListener(onClickSettingListener);
        musicView.setOnMusicViewListener(new MusicView.OnMusicViewListener() {
            @Override
            public void onDown() {
                if (!isSettingTouching && !isSilentTouching && !isScreenTimeTouching && !isBrightnessTouching && !isVolumeTouching) {
                    isMusicTouching = true;
                    settingView.setViewTouching(true);
                }
            }

            @Override
            public void onUp() {
                isMusicTouching = false;
                settingView.setViewTouching(false);
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
            }

            @Override
            public void onClickVerify() {
                hideMainControl();
                SettingUtils.intentPermissionNotificationListener(App.mContext);
            }
        });

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
                    Intent intent = new Intent(getContext(), SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setAction(Constant.ADD_FOCUS);
                    getContext().startActivity(intent);
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
                    Intent intent = new Intent(getContext(), SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setAction(Constant.SETTING_FOCUS);
                    intent.putExtra(Constant.ID_FOCUS_SETTING, focusIOS.getId());
                    getContext().startActivity(intent);
                }

                new Handler().postDelayed(() -> hideMainControl(), 500);

            }

            @Override
            public void onUpdateView(FocusIOS focusIOS) {
//                silentView.setFocusIOS(focusIOS);
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
//                    Intent intent = new Intent(getContext(), MainActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.setAction(Constant.REQUEST_PERMISSION_LOCATION);
//                    intent.putExtra(Constant.ID_FOCUS_SETTING, focusIOS.getId());
//                    getContext().startActivity(intent);
//
//                }, 500);
            }

            @Override
            public void onRequestPermissionPhone(FocusIOS focusIOS) {
//                requestPermissionPhone();
            }

        });

        controls = new RelativeLayoutAnimation[]{findViewById(R.id.view_control_1), findViewById(R.id.view_control_2), findViewById(R.id.view_control_3), findViewById(R.id.view_control_4), findViewById(R.id.view_control_5), findViewById(R.id.view_control_6), findViewById(R.id.view_control_7), findViewById(R.id.view_control_8), findViewById(R.id.view_control_9), findViewById(R.id.view_control_10), findViewById(R.id.view_control_11)};
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
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            return scrollMain;
        } else {
            return containerControl;
        }
    }

    private void updateControlCustom() {
        //int paddingIcon = (int) DensityUtils.pxFromDp(context, 15);
        removeAll();
        customControl.clear();
        customControl.addAll(Arrays.asList(ControlCustomizeManager.getInstance(context).getListControlsSave()));
        customControl.add(Constant.ACTION_CUSTOMIZE_CONTROL);
        customControl.add(Constant.ACTION_COLOR);
        customControl.add(Constant.ACTION_EDGE_TRIGGERS);

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        for (int i = 0; i < customControl.size(); i++) {
            controls[i].setVisibility(VISIBLE);
            switch (customControl.get(i)) {
                case Constant.STRING_ACTION_FLASH_LIGHT:
                    flashLightView = new FlashLightView(context);
                    flashLightView.setParentView(controls[i]);
                    controls[i].addView(flashLightView, params);
                    //controls[i].setBackgroundColor(Color.TRANSPARENT);
                    break;
                case Constant.CLOCK:
                    TimeActionView timeActionView = new TimeActionView(context);
                    timeActionView.setOnClickSettingListener(onClickSettingListener);
                    timeActionView.setParentView(controls[i]);
                    controls[i].addView(timeActionView, params);
                    break;
                case Constant.CALCULATOR:
                    CalculatorActionView calculatorActionView = new CalculatorActionView(context);
                    calculatorActionView.setOnClickSettingListener(onClickSettingListener);
                    calculatorActionView.setParentView(controls[i]);
                    controls[i].addView(calculatorActionView, params);
                    break;
                case Constant.CAMERA:
                    CameraAcitonView cameraAcitonView = new CameraAcitonView(context);
                    cameraAcitonView.setOnClickSettingListener(onClickSettingListener);
                    cameraAcitonView.setParentView(controls[i]);
                    controls[i].addView(cameraAcitonView, params);
                    break;
                case Constant.RECORD:
                    ScreenRecordActionView recordActionView = new ScreenRecordActionView(context);
                    recordActionView.setOnClickSettingListener(onClickSettingListener);
                    recordActionView.setParentView(controls[i]);
//                    recordActionView.setPadding(paddingIcon);
                    controls[i].addView(recordActionView, params);
                    break;
                case Constant.DARK_MODE:
                    DarkModeActionView darkModeActionView = new DarkModeActionView(context);
                    darkModeActionView.setOnClickSettingListener(onClickSettingListener);
                    darkModeActionView.setParentView(controls[i]);
                    controls[i].addView(darkModeActionView, params);
                    break;
                case Constant.STRING_ACTION_BATTERY:
                    lowPowerActionView = new LowPowerActionView(context);
                    lowPowerActionView.setOnClickSettingListener(onClickSettingListener);
                    lowPowerActionView.setParentView(controls[i]);
                    controls[i].addView(lowPowerActionView, params);
                    break;
                case Constant.NOTE:
                    NoteActionView noteActionView = new NoteActionView(context);
                    noteActionView.setOnClickSettingListener(onClickSettingListener);
                    noteActionView.setParentView(controls[i]);
                    controls[i].addView(noteActionView, params);
                    break;
                case Constant.ACTION_CUSTOMIZE_CONTROL:
                    CustomizeActionView customizeActionView = new CustomizeActionView(context);
                    customizeActionView.setOnClickSettingListener(onClickSettingListener);
                    customizeActionView.setParentView(controls[i]);
                    controls[i].addView(customizeActionView, params);
                    break;
                case Constant.ACTION_COLOR:
                    ColorActionView colorActionView = new ColorActionView(context);
                    colorActionView.setOnClickSettingListener(onClickSettingListener);
                    colorActionView.setParentView(controls[i]);
                    controls[i].addView(colorActionView, params);
                    break;
                case Constant.ACTION_EDGE_TRIGGERS:
                    EdgeActionView edgeActionView = new EdgeActionView(context);
                    edgeActionView.setOnClickSettingListener(onClickSettingListener);
                    edgeActionView.setParentView(controls[i]);
                    controls[i].addView(edgeActionView, params);
                    break;
                default:
                    if (customControl.get(i).isEmpty()) {
                        controls[i].setVisibility(GONE);
                    } else {
                        CustomControlImageView imageView = new CustomControlImageView(context);
                        imageView.setParentView(controls[i]);
                        Drawable drawable = MethodUtils.getIconFromPackageName(context, customControl.get(i));
                        if (drawable != null) {
                            imageView.setImageDrawable(drawable);
                        }
                        final int finalI = i;

                        imageView.setOnCustomControlImageViewListener(() -> {
                            hideMainControl();
                            SettingUtils.intentOtherApp(context, customControl.get(finalI));
                        });
                        controls[i].addView(imageView, params);
                    }
                    break;
            }
        }
    }

    private void removeAll() {
        flashLightView = null;
        for (RelativeLayoutAnimation control : controls) {
            control.removeAllViews();
            control.setVisibility(GONE);
        }
    }

    public void setHideViewExpand() {
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
        musicExpandView.setVisibility(VISIBLE);

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
                musicExpandView.setVisibility(GONE);
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
        settingExpandView.setVisibility(VISIBLE);

        settingExpandView.animate().alpha(1f)/*.translationX(0).translationY(0)*/.scaleX(1).scaleY(1).setDuration(DURATION_ANIMATION).setListener(null).start();
    }

    private void animationHideSettingExpand() {
        isShowSettingExpand = false;

        settingView.updateState();

        animationShowLayoutMain();

        settingExpandView.clearAnimation();

        settingExpandView.animate().alpha(0).scaleX(0.5f).scaleY(0.5f)/*.translationX(-300).translationY(-350)*/.setDuration(DURATION_ANIMATION).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                settingExpandView.setVisibility(GONE);
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
        volumeExpandView.updateVolumeAudio(AudioManagerUtils.getInstance(context).getVolume());
        volumeExpandView.updateVolumeSystem();
        volumeExpandView.updateVolumeRingtone();

        volumeExpandView.clearAnimation();
        volumeExpandView.setAlpha(0f);
        volumeExpandView.setScaleX(0.7f);
        volumeExpandView.setScaleY(0.7f);
        volumeExpandView.setVisibility(View.VISIBLE);
        volumeExpandView.animate().alpha(1f).scaleX(1f).scaleY(1f)/*.translationX(0).translationY(0)*/.setDuration(DURATION_ANIMATION).setListener(null).start();

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
        brightnessExpandView.updateBrightness();

        animationHideLayoutMain();
        brightnessExpandView.clearAnimation();

        brightnessExpandView.setVisibility(View.VISIBLE);
        brightnessExpandView.setAlpha(0f);
        brightnessExpandView.setScaleX(0.7f);
        brightnessExpandView.setScaleY(0.7f);
//        brightnessExpandView.setTranslationX(100);
//        brightnessExpandView.setTranslationY(100);

        brightnessExpandView.animate().alpha(1f).scaleX(1f).scaleY(1f)/*.translationX(0).translationY(0)*/.setDuration(DURATION_ANIMATION).setListener(null).start();
    }

    private void animationHideBrightnessSettingExpand() {
        showBrightnessExpand = false;
        seekbarBrightness.updateIconBrightness();

        animationShowLayoutMain();

        brightnessExpandView.clearAnimation();
        brightnessExpandView.animate().alpha(0).scaleX(0.7f).scaleY(0.7f)/*.translationX(100).translationY(100)*/.setListener(null).setListener(new Animator.AnimatorListener() {
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
                focusLayout.setVisibility(GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

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
                screenTimeoutLayout.setVisibility(GONE);
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
        getViewLayoutControl().animate().alpha(1).setDuration(DURATION_ANIMATION).setListener(null).start();
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

    public void setBgNew() {
        if (MethodUtils.isTypeRealTimeBg(tinyDB)) {
            background.setColorFilter(ContextCompat.getColor(getContext(), R.color.color_background_real_time));
        } else {
            background.clearColorFilter();
        }
        background.setImageBitmap(BlurBackground.getInstance().getBitmapBgBlur());
    }


    @Override
    protected void onVisibilityChanged(@androidx.annotation.NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
    }

    public void setOnControlCenterListener(OnControlCenterListener onControlCenterListener) {
        this.onControlCenterListener = onControlCenterListener;
    }

    public boolean isViewShowing(View view) {
        Timber.e("NVQ view.getVisibility " + view.getVisibility() + " view.getAlpha() " + (view.getAlpha() == 1));
        return (view.getVisibility() == View.VISIBLE && view.getAlpha() == 1);
    }

    public void show() {
        animate().cancel();
        setAlpha(0f);
        setVisibility(View.VISIBLE);
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            scrollMain.post(() -> scrollMain.fullScroll(styleControl == Constant.STYLE_CONTROL_TOP ? View.FOCUS_UP : View.FOCUS_DOWN));
        }
        animate().alpha(1).setDuration(200).withEndAction(() -> {
            statusControlView.animationShow();
            settingView.animationShow();
            seekbarBrightness.animationShow();
            seekbarVolume.animationShow();
            musicView.animationShow();
            rotateView.animationShow();
            silentView.animationShow();
            screenTimeoutAction.animationShow();
//            screenTimeoutAction.animationShow(listener);

            getViewLayoutControl().setVisibility(VISIBLE);

            for (RelativeLayoutAnimation control : controls) {
                control.animationShow();
            }
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
        AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0.1f);
        alphaAnimation.setDuration(250);
        root.startAnimation(alphaAnimation);

        statusControlView.animationHide();
        settingView.animationHide();
        musicView.animationHide();
        rotateView.animationHide();
        silentView.animationHide();
        screenTimeoutAction.animationHide();
        seekbarBrightness.animationHide();
        seekbarVolume.animationHide();

        for (RelativeLayoutAnimation control : controls) {
            control.animationHide();
        }
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
        settingView.updateState();
        rotateView.updateRotateState(false);
        silentView.updateDoNotDisturbState();
        statusControlView.update();
        if (flashLightView != null) {
            flashLightView.updateFlash();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);

    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unregister();
        removeAllViews();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent messageEvent) {
        if (messageEvent.getTypeEvent() == Constant.PACKAGE_APP_REMOVE) {
            ControlCustomizeManager.getInstance(context).replaceCustomControl(customControl, messageEvent.getStringValue());
            updateControlCustom();
        }
    }

    private void unregister() {
        mBluetoothAdapter = null;
    }

    public void updateProcessBrightness() {
        if (seekbarBrightness != null) {
            seekbarBrightness.updateIconBrightness();
        }
    }


    public void updateStateSim() {
        statusControlView.updateStateSim();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            setHideViewExpand();
        }
        return super.dispatchKeyEvent(event);
    }

    @Subscribe
    public void onEventUpdateControl(EventSaveControl eventSaveControl) {
        if (eventSaveControl.getAction() != null && !eventSaveControl.getAction().isEmpty()) {
            if (eventSaveControl.getAction().equals(Constant.ACTION_CHANGE_ITEM_CONTROL)) {
                updateControlCustom();
            } else if (eventSaveControl.getAction().equals(Constant.ACTION_CHANGE_LAYOUT_CONTROL)) {
                unregister();
                styleControl = tinyDB.getInt(Constant.STYLE_CONTROL, Constant.STYLE_CONTROL_TOP);
                removeAllViews();
                orientation = DensityUtils.getOrientationWindowManager(getContext());
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    LayoutInflater.from(context).inflate(styles[styleControl], ControlCenterViewOS.this, true);
                } else {
                    LayoutInflater.from(context).inflate(stylesLand[styleControl], ControlCenterViewOS.this, true);
                }
                findViews();
                setBgNew();
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

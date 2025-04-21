package com.tapbi.spark.controlcenter.feature.controlcenter.view.control.micenter.view;

import static android.provider.Settings.ACTION_CAST_SETTINGS;
import static android.provider.Settings.System.SCREEN_OFF_TIMEOUT;

import android.Manifest;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.AlarmClock;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CallBackUpdateSound;
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CallBackUpdateUi;
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CloseMiControlView;
import com.tapbi.spark.controlcenter.feature.controlcenter.utils.DataMobileUtils;
import com.tapbi.spark.controlcenter.feature.controlios14.manager.AudioManagerUtils;
import com.tapbi.spark.controlcenter.interfaces.IListenActionClick;
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614;
import com.tapbi.spark.controlcenter.ui.RequestPermissionActivity;
import com.tapbi.spark.controlcenter.ui.transparent.TransparentActivity;
import com.tapbi.spark.controlcenter.utils.SettingUtils;

import timber.log.Timber;

public class BaseItemRecyclerView extends ConstraintLayout implements View.OnClickListener {
    private final Handler handlerBattery = new Handler();
    private final Handler handlerDataMobile = new Handler();
    private final Handler handlerDarkmode = new Handler();

//    public FlashUtils flashUtils;

    public BluetoothAdapter mBluetoothAdapter;
    //    public boolean clickFlash = false;
    public DataMobileUtils dataMobileUtils;
    public AudioManager audioManager;
    public LocationManager lm;
    private String stringAction = "";
    private String uri = "";
    private CloseMiControlView closeMiControlView;
    private CallBackUpdateSound callBackUpdateSound;
    private CallBackUpdateUi callBackUpdateUi;
    private int countRunnableBattery = 0;
    private ScaleAnimation fade_in;
    private int pos;
    private int countRunnableData = 0;
    private View viewAni;
    public static boolean isPressDarkmode = false;
    private Runnable runnableDarkmode = new Runnable() {
        @Override
        public void run() {
            BaseItemRecyclerView.isPressDarkmode = false;
        }
    };


    public BaseItemRecyclerView(@NonNull Context context) {
        super(context);
        init();
    }

    public BaseItemRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseItemRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }    private final Runnable runnableBattery = new Runnable() {
        @Override
        public void run() {
            if (callBackUpdateUi != null) {
                countRunnableBattery++;
                callBackUpdateUi.stage(stringAction, SettingUtils.isPowerSaveMode(getContext()), pos);
                handlerBattery.postDelayed(runnableBattery, 1000);
                if (countRunnableBattery > 5) {
                    handlerBattery.removeCallbacks(this);
                }
            }
        }
    };

    private void init() {
    }

    public boolean onLongClickFromChild() {
        if (stringAction.equals(Constant.STRING_ACTION_HOST_POST)) {
            SettingUtils.intentChangeHostPost(getContext());
            return true;
        } else if (stringAction.equals(Constant.STRING_ACTION_DATA_MOBILE)) {
            if (!SettingUtils.hasSimCard(getContext())) {
                NotyControlCenterServicev614.getInstance().showDialogContent(() -> {
                    closeMiControlView.close();
                });
            } else {
                NotyControlCenterServicev614.getInstance().closeCenterWhenClick3G();
                SettingUtils.intentChangeDataMobile(getContext());
            }
            return true;
        } else {
            if (uri != null && !uri.isEmpty()) {
                intentSettingForUri();
                return true;
            }
        }
        return false;
    }

    public void enableListener(String uri, String s, int pos, CallBackUpdateSound callBackUpdateSound, CallBackUpdateUi callBackUpdateUi, CloseMiControlView closeMiControlView) {
        this.closeMiControlView = closeMiControlView;
        this.callBackUpdateSound = callBackUpdateSound;
        this.callBackUpdateUi = callBackUpdateUi;
        this.uri = uri;
        this.pos = pos;
        stringAction = s;
        if (stringAction.equals("Sound")) {
            AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
            switch (audioManager.getRingerMode()) {
                case AudioManager.RINGER_MODE_VIBRATE:
                    stringAction = "Vibrate";
                    break;
                case AudioManager.RINGER_MODE_NORMAL:
                    stringAction = "Sound";
                    break;
                case AudioManager.RINGER_MODE_SILENT:
                    stringAction = "Silent";
                    break;
            }
        }

        //Timber.e("." +stringAction);
        handingAction();
    }

    private void handingAction() {
        if (callBackUpdateUi == null) {
            return;
        }
        switch (stringAction) {
            case "Sync":
            case "Wifi":
            case "Auto-rotate":
            case "Airplane mode":
                break;
            case "Do not disturb":


            case "Host post":

            case "KeyBroad Picker":

            case Constant.STRING_ACTION_BATTERY:

                break;
            case "Bluetooth":
                if (mBluetoothAdapter != null) return;
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    return;
                }
                break;
            case "Data mobile":
                dataMobileUtils = new DataMobileUtils(getContext());
                break;

            case Constant.STRING_ACTION_FLASH_LIGHT:
//                if (flashUtils != null) return;
//                flashUtils = new FlashUtils(getContext(), callBackUpdateUi, Constant.STRING_ACTION_FLASH_LIGHT, pos);
//
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//                    clickFlash = flashUtils.isEnabledApi22();
//                } else {
//                    clickFlash = FlashUtils.enabled;
//                }
//                if (NotyControlCenterServicev614.getInstance().flashUtils!=null){
//                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//                        clickFlash = NotyControlCenterServicev614.getInstance().flashUtils.isEnabledApi22();
//                    } else {
//                        clickFlash = FlashUtils.enabled;
//                    }
//                }

                break;
            case "Location":
                if (lm == null) {
                    lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
                }
                break;
            case "Silent":
            case "Vibrate":
            case "Sound":
                if (audioManager == null) {
                    audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
                }
                break;
        }
    }

    public void intentSetting(String action) {
        try {
            Intent intent = new Intent(action);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public void onClick(View v) {
        switch (stringAction) {
            case Constant.STRING_ACTION_WIFI:
                Timber.e("NVQ STRING_ACTION_WIFI1");
                NotyControlCenterServicev614.getInstance().setWifiNoty();
                break;

            case Constant.STRING_ACTION_DATA_MOBILE:
                clickAction(Constant.STRING_ACTION_DATA_MOBILE);
                break;

            case Constant.STRING_ACTION_FLASH_LIGHT:
//                if (NotyControlCenterServicev614.getInstance().flashUtils == null) {
//                    return;
//                }
//                if (clickFlash) {
//                    clickFlash = false;
//                    NotyControlCenterServicev614.getInstance().flashUtils.flashOff();
//                } else {
//                    clickFlash = true;
//                    NotyControlCenterServicev614.getInstance().flashUtils.flashOn();
//                }
                if (NotyControlCenterServicev614.getInstance() != null) {
                    NotyControlCenterServicev614.getInstance().setFlashOnOff();
                }
                break;

            case Constant.STRING_ACTION_BLUETOOTH:
                if (mBluetoothAdapter == null) {
                    break;
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    SettingUtils.intentActivityRequestPermission(v.getContext(), new String[]{Manifest.permission.BLUETOOTH_CONNECT});
                    NotyControlCenterServicev614.getInstance().closeNotyCenter();
                    return;
                }
                if (mBluetoothAdapter.isEnabled() && mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        SettingUtils.setOffBluetoothApi33(getContext());
                        NotyControlCenterServicev614.getInstance().closeNotyCenter();
                    } else {
                        try {
                            mBluetoothAdapter.disable();
                        } catch (Exception e) {
                            setOnOffBluetoothWithIntent(getContext(), false);
                        }

                    }
                } else {
                    if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF || mBluetoothAdapter.getState() == BluetoothAdapter.STATE_TURNING_OFF) {
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            SettingUtils.setOnBluetoothApi33(getContext());
                            NotyControlCenterServicev614.getInstance().closeNotyCenter();
                        } else {
                            try {
                                mBluetoothAdapter.enable();
                            } catch (Exception e) {
                                setOnOffBluetoothWithIntent(getContext(), true);
                            }
                        }
                    }
                }
                break;

            case Constant.STRING_ACTION_AUTO_ROTATE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.System.canWrite(getContext())) {
                        SettingUtils.intentActivityRequestPermission(getContext(), new String[]{Manifest.permission.WRITE_SETTINGS});
                        closeMiControlView.close();
                        return;
                    }
                }
                SettingUtils.settingRotate(getContext());
                callBackUpdateUi.stage(Constant.STRING_ACTION_AUTO_ROTATE, android.provider.Settings.System.getInt(getContext().getContentResolver(),
                        Settings.System.ACCELEROMETER_ROTATION, 0) == 1, pos);
                break;

            case Constant.STRING_ACTION_SOUND:
                callBackUpdateSound.updateSound(Constant.STRING_ACTION_SOUND, AudioManager.RINGER_MODE_VIBRATE);
                stringAction = Constant.STRING_ACTION_VIBRATE;
                AudioManagerUtils.getInstance(getContext()).setRingMode( AudioManager.RINGER_MODE_VIBRATE);

                break;

            case Constant.STRING_ACTION_VIBRATE:
                callBackUpdateSound.updateSound(Constant.STRING_ACTION_VIBRATE, AudioManager.RINGER_MODE_SILENT);
                stringAction = Constant.STRING_ACTION_SILENT;
                AudioManagerUtils.getInstance(getContext()).setRingMode( AudioManager.RINGER_MODE_SILENT);

                break;

            case Constant.STRING_ACTION_SILENT:
                callBackUpdateSound.updateSound(Constant.STRING_ACTION_SILENT, AudioManager.RINGER_MODE_NORMAL);
                stringAction = Constant.STRING_ACTION_SOUND;
                AudioManagerUtils.getInstance(getContext()).setRingMode( AudioManager.RINGER_MODE_NORMAL);
                break;

            case Constant.STRING_ACTION_AIRPLANE_MODE:
                clickAction(Constant.STRING_ACTION_AIRPLANE_MODE);
                break;

            case Constant.STRING_ACTION_DO_NOT_DISTURB:
                setInterruptionFilter();
                break;

            case Constant.STRING_ACTION_LOCATION:
                clickAction(Constant.STRING_ACTION_LOCATION);
                break;

            case Constant.STRING_ACTION_NIGHT_LIGHT:

            case Constant.DARK_MODE:
                clickAction(Constant.DARK_MODE);
                break;

            case Constant.STRING_ACTION_HOST_POST:
                clickAction(Constant.STRING_ACTION_HOST_POST);
                break;
            case Constant.STRING_ACTION_SCREEN_CAST:
                intentSetting(ACTION_CAST_SETTINGS);
                closeMiControlView.close();
                break;

            case Constant.STRING_ACTION_CLOCK:
                intentSetting(AlarmClock.ACTION_SHOW_ALARMS);
                closeMiControlView.close();
                break;

            case Constant.STRING_ACTION_CAMERA:
                SettingUtils.openCamera(getContext());
                closeMiControlView.close();
                break;

            case Constant.STRING_ACTION_KEYBOARD_PICKER:
                closeMiControlView.close();
                Timber.e("hoangld ");
                Intent intent = new Intent(getContext(), TransparentActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setAction(TransparentActivity.ACTION_SHOW_PICK_KEYBOARD);
                getContext().startActivity(intent);
                break;
            case Constant.STRING_ACTION_OPEN_SYSTEM:
                intentSetting(Settings.ACTION_SETTINGS);
                closeMiControlView.close();
                break;

            case Constant.STRING_ACTION_SCREEN_LOCK:
                intentSetting(SCREEN_OFF_TIMEOUT);
                closeMiControlView.close();
                break;
            case Constant.STRING_ACTION_SCREEN_RECODING:
                SettingUtils.intentActivityRequestPermission(getContext(), new String[]{RequestPermissionActivity.RECORDING});
                closeMiControlView.close();
                break;
            case Constant.STRING_ACTION_BATTERY:
                clickAction(Constant.STRING_ACTION_BATTERY);

                countRunnableBattery = 0;
                handlerBattery.removeCallbacks(runnableBattery);
                handlerBattery.postDelayed(runnableBattery, 1000);
                break;

            case Constant.STRING_ACTION_SYNC:
//        intentSetting(Settings.ACTION_SYNC_SETTINGS);
//        closeMiControlView.close();
                SettingUtils.setSyncAutomatically();

                callBackUpdateUi.stage(Constant.STRING_ACTION_SYNC, SettingUtils.isSyncAutomaticallyEnable(), pos);
                break;

            case Constant.STRING_ACTION_SCREEN_SHOT:
                intentSetting(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                closeMiControlView.close();
                break;
        }
    }

    private void intentSettingForUri() {
        try {
            Intent panelIntent = new Intent(uri);
            panelIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(panelIntent);
            closeMiControlView.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setInterruptionFilter() {
        NotificationManager notificationManager = (NotificationManager) getContext().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                if (Settings.Global.getInt(getContext().getContentResolver(), "zen_mode") == 0) {
                    notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY);
                } else {
                    notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                }
            } catch (Exception e) {
                Timber.d(e);
            }
        }
    }    private final Runnable runnableData = new Runnable() {
        @Override
        public void run() {
            if (dataMobileUtils != null) {
                countRunnableData++;
                callBackUpdateUi.stage(Constant.STRING_ACTION_DATA_MOBILE, dataMobileUtils.isDataEnable(), pos);
                handlerDataMobile.postDelayed(runnableData, 1000);
                if (countRunnableData > 5) {
                    handlerDataMobile.removeCallbacks(this);
                }
            }
        }
    };

    private void setOnOffBluetoothWithIntent(Context context, boolean isEnable) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            SettingUtils.intentActivityRequestPermission(context, new String[]{Manifest.permission.BLUETOOTH_CONNECT});
            NotyControlCenterServicev614.getInstance().closeNotyCenter();
            return;
        }
        Intent intent;
        if (isEnable) {
            intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        } else {
            intent = new Intent("android.bluetooth.adapter.action.REQUEST_DISABLE");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        NotyControlCenterServicev614.getInstance().closeNotyCenter();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (getVisibility() == View.VISIBLE) {
            if (stringAction.contains(Constant.STRING_ACTION_DATA_MOBILE) && dataMobileUtils != null) {
                callBackUpdateUi.stage(stringAction, dataMobileUtils.isDataEnable(), pos);
            } else if (stringAction.contains(Constant.STRING_ACTION_BATTERY)) {
                callBackUpdateUi.stage(stringAction, SettingUtils.isPowerSaveMode(getContext()), pos);
            }
        } else {
            if (stringAction.contains(Constant.STRING_ACTION_BATTERY)) {
                handlerBattery.removeCallbacks(runnableBattery);
            }
        }

        if (visibility != VISIBLE) {
            stopAniZoom();
        }
    }

    protected void statAniZoom(View view) {
        this.viewAni = view;
        stopAniZoom();
        if (fade_in == null) {
            fade_in = new ScaleAnimation(0.8f, 1.1f, 0.8f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            fade_in.setDuration(1000);     // animation duration in milliseconds
            fade_in.setFillAfter(true);    // If fillAfter is true, the transformation that this animation performed will persist when it is finished.
            fade_in.setRepeatMode(Animation.REVERSE);
            fade_in.setRepeatCount(Animation.INFINITE);
        }

        view.startAnimation(fade_in);
    }

    protected void stopAniZoom() {
        if (viewAni != null) {
            viewAni.clearAnimation();
        }
    }

    private void clickAction(String action) {
        if (NotyControlCenterServicev614.getInstance().allowClickAction()) {
            if (NotyControlCenterServicev614.getInstance() != null) {
                handlerDataMobile.removeCallbacksAndMessages(null);
                countRunnableData = 0;
                if (action.equals(Constant.DARK_MODE)){
                    handlerDarkmode.removeCallbacks(runnableDarkmode);
                    BaseItemRecyclerView.isPressDarkmode = true;
                }
                handlerDataMobile.postDelayed(runnableData, 1000);
                NotyControlCenterServicev614.getInstance().setHandingAction(new IListenActionClick() {
                    @Override
                    public void noFindAction() {
                        stopAniZoom();
                        if (action.equals(Constant.DARK_MODE)){
                            handlerDarkmode.postDelayed(runnableDarkmode, 1000);
                        }
                    }

                    @Override
                    public void actionClicked() {
                        stopAniZoom();
                        if (action.equals(Constant.DARK_MODE)){
                            handlerDarkmode.postDelayed(runnableDarkmode, 1000);
                        }
                    }
                }, action);
            }
        } else {
            if (NotyControlCenterServicev614.getInstance() != null) {
                stopAniZoom();
                NotyControlCenterServicev614.getInstance().showToast(getContext().getString(R.string.wait_until_job_done));
            }
        }
    }

    public void unRegisterReceiver() {
//        try {
//            if (contentObserverAutoRotate != null) {
//                getContext().getContentResolver().unregisterContentObserver(contentObserverAutoRotate);
//
//                contentObserverAutoRotate = null;
//            }
//        } catch (Exception e) {
//            Timber.d(e);
//        }
//
//        try {
//            if (actionRingerMode != null) {
//                getContext().unregisterReceiver(actionRingerMode);
//
//                actionRingerMode = null;
//            }
//        } catch (Exception e) {
//            Timber.d(e);
//        }
//
//        try {
//            if (actionAirplaneModeChange != null) {
//                getContext().unregisterReceiver(actionAirplaneModeChange);
//
//                actionAirplaneModeChange = null;
//            }
//        } catch (Exception e) {
//            Timber.d(e);
//        }
//
//        try {
//            if (actionDoNotDisturb != null) {
//                getContext().unregisterReceiver(actionDoNotDisturb);
//
//                actionDoNotDisturb = null;
//            }
//        } catch (Exception e) {
//            Timber.d(e);
//        }
//
//        try {
//            if (actionLocationBroadcastReceiver != null) {
//                getContext().unregisterReceiver(actionLocationBroadcastReceiver);
//
//                actionLocationBroadcastReceiver = null;
//            }
//        } catch (Exception e) {
//            Timber.d(e);
//        }
//
//        try {
//            if (actionWifiHostPostReceiver != null) {
//                getContext().unregisterReceiver(actionWifiHostPostReceiver);
//
//                actionWifiHostPostReceiver = null;
//            }
//        } catch (Exception e) {
//            Timber.d(e);
//        }
//
//        try {
//            if (wifiBroadcastReceiver != null) {
//                getContext().unregisterReceiver(wifiBroadcastReceiver);
//
//                wifiBroadcastReceiver = null;
//            }
//        } catch (Exception e) {
//            Timber.d(e);
//        }
//
//        try {
//            if (contentDataMobile != null) {
//                getContext().getContentResolver().unregisterContentObserver(contentDataMobile);
//
//                contentDataMobile = null;
//            }
//        } catch (Exception e) {
//            Timber.d(e);
//        }
//
//
//
//        try {
//            if (lowPowerModeChange != null) {
//                getContext().unregisterReceiver(lowPowerModeChange);
//
//                lowPowerModeChange = null;
//            }
//        } catch (Exception e) {
//            Timber.d(e);
//        }
//
//        try {
//            if (receiverChangeSim != null) {
//                getContext().unregisterReceiver(receiverChangeSim);
//
//                receiverChangeSim = null;
//            }
//        } catch (Exception e) {
//            Timber.d(e);
//        }
//
//        try {
//            if (flashUtils != null) {
//                flashUtils.unRegisterListener();
//                flashUtils = null;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
    }

    private class ReceiverChangeSim extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            callBackUpdateUi.stage(stringAction, false, pos);
        }
    }







}

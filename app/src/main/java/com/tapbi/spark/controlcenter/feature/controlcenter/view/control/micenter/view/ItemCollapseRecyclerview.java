package com.tapbi.spark.controlcenter.feature.controlcenter.view.control.micenter.view;

import android.app.UiModeManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.media.AudioManager;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.data.repository.ThemeHelper;
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CallBackUpdateSound;
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CallBackUpdateUi;
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CloseMiControlView;
import com.tapbi.spark.controlcenter.feature.controlcenter.textview.TextViewAutoRun;
import com.tapbi.spark.controlcenter.feature.controlcenter.utils.DataMobileUtils;
import com.tapbi.spark.controlcenter.feature.controlcenter.utils.HostPostUtils;
import com.tapbi.spark.controlcenter.feature.controlios14.model.InfoSystem;
import com.tapbi.spark.controlcenter.receiver.SyncStatusOb;
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614;
import com.tapbi.spark.controlcenter.utils.MethodUtils;
import com.tapbi.spark.controlcenter.utils.SettingUtils;

public class ItemCollapseRecyclerview extends BaseItemRecyclerView implements SyncStatusOb.Callback {
    public boolean first = true;
    public boolean firstLoad = true;
    public TextViewAutoRun nameAction;
    public Context context;
    private ConstraintLayout parentItemExpand;
    private InfoSystem infoSystem;
    private ImageView imgIcon;
    private int pos;

    private int typeChose;
    private CloseMiControlView closeMiControlView;
    private String action = "";
    private CallBackUpdateSound callBackUpdateSound = new CallBackUpdateSound() {
        @Override
        public void updateSound(String valueRegister, int value) {
            if (valueRegister.equals("Sound") || valueRegister.equals("Vibrate") || valueRegister.equals("Silent")) {
                changeTypeSound(value);
            }
        }
    };
    private CallBackUpdateUi callBackUpdateUi = new CallBackUpdateUi() {

        @Override
        public void stage(String valueRegister, boolean b, int pos) {
            if (valueRegister.equals(infoSystem.getName())) {
                if (infoSystem.getName().equals(Constant.STRING_ACTION_DATA_MOBILE)) {
                    boolean enabled = SettingUtils.hasSimCard(context) && !SettingUtils.isAirplaneModeOn(getContext()) && new DataMobileUtils(getContext()).isDataEnable();
                    setBg(enabled);
                } else {
                    setBg(b);
                }
            }
        }
    };

    public ItemCollapseRecyclerview(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ItemCollapseRecyclerview(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    public ItemCollapseRecyclerview(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }

    public void setStageAction(String valueRegister, boolean b) {
        if (valueRegister.equals(infoSystem.getName())) {
            if (infoSystem.getName().equals(Constant.STRING_ACTION_DATA_MOBILE)) {
                boolean enabled = SettingUtils.hasSimCard(context) && !SettingUtils.isAirplaneModeOn(getContext()) && new DataMobileUtils(getContext()).isDataEnable();
                setBg(enabled);
            } else {
                setBg(b);
            }
        }
    }

    private void init(Context context) {
        this.context = context;
        setLayerType(LAYER_TYPE_NONE, null);
        LayoutInflater.from(getContext()).inflate(R.layout.view_item_collapse, this);
        findLayout();


    }

    @Override
    public void onClick(View v) {
        switch (action) {
            case Constant.STRING_ACTION_DATA_MOBILE:
            case Constant.STRING_ACTION_WIFI:
            case Constant.STRING_ACTION_BLUETOOTH:
            case Constant.STRING_ACTION_AIRPLANE_MODE:
            case Constant.STRING_ACTION_LOCATION:
            case Constant.STRING_ACTION_HOST_POST:
            case Constant.STRING_ACTION_BATTERY:
            case Constant.STRING_ACTION_NIGHT_LIGHT:
            case Constant.DARK_MODE:
                statAniZoom(imgIcon);
        }
        super.onClick(v);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        enableListener(infoSystem.getUri(), nameAction.getText().toString(), callBackUpdateSound, callBackUpdateUi, closeMiControlView);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (getVisibility() == VISIBLE) {
            updateStatusActionSync();
        }
    }

    private void updateStatusActionSync() {
        if (action.equals(Constant.STRING_ACTION_SYNC)) {
            setBg(SettingUtils.isSyncAutomaticallyEnable());
        }
    }

    public void data(InfoSystem infoSystem, int pos, CloseMiControlView closeMiControlView) {
        this.pos = pos;
        this.infoSystem = infoSystem;
        //Timber.e("hoangld infoSystem " + infoSystem.getAction() + " -- " + infoSystem.getUri());
        this.closeMiControlView = closeMiControlView;
        if (firstLoad) {
            setBg(false);
            firstLoad = false;
            imgIcon.setImageResource(infoSystem.getIcon());
            nameAction.setText(MethodUtils.getNameActionShowTextView(getContext(), infoSystem.getName()));
            setUpData();
        }

    }

    private void findLayout() {
        typeChose = ThemeHelper.itemControl.getIdCategory();
        parentItemExpand = findViewById(R.id.layoutItemCollapse);
        imgIcon = findViewById(R.id.imgIcon);
        nameAction = findViewById(R.id.tvNameAction);
        nameAction.setTypeface(Typeface.createFromAsset(getContext().getAssets(), Constant.FOLDER_FONT_CONTROL_ASSETS + ThemeHelper.itemControl.getFont()));
        parentItemExpand.setOnClickListener(this);
        parentItemExpand.setOnLongClickListener(v -> onLongClickFromChild());
    }

    private void setUpData() {
        action = MethodUtils.getAction(getContext(), nameAction.getText().toString());

        enableListener(infoSystem.getUri(), action, pos, callBackUpdateSound, callBackUpdateUi, closeMiControlView);
        switch (action) {
            case Constant.STRING_ACTION_AUTO_ROTATE:
                setBg(android.provider.Settings.System.getInt(getContext().getContentResolver(),
                        Settings.System.ACCELEROMETER_ROTATION, 0) == 1);
                break;
            case Constant.STRING_ACTION_SOUND:
            case Constant.STRING_ACTION_VIBRATE:
            case Constant.STRING_ACTION_SILENT:
                if (audioManager != null) {
                    changeTypeSound(audioManager.getRingerMode());
                }
                break;
            case Constant.STRING_ACTION_AIRPLANE_MODE:
                setBg(Settings.System.getInt(
                        getContext().getContentResolver(),
                        Settings.Global.AIRPLANE_MODE_ON, 0) == 1);
                break;

            case Constant.STRING_ACTION_DO_NOT_DISTURB:
                try {
                    boolean value = Settings.Global.getInt(getContext().getContentResolver(), "zen_mode") != 0;
                    setBg(value);
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case Constant.STRING_ACTION_LOCATION:
//                setBg(actionLocationBroadcastReceiver.lm.isProviderEnabled(LocationManager.GPS_PROVIDER));
                setBg(lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
                break;

            case Constant.STRING_ACTION_HOST_POST:
//                setBg(actionWifiHostPostReceiver.getStateWifi());
                setBg(new HostPostUtils(context).getStateWifi());
                break;

            case Constant.STRING_ACTION_KEYBOARD_PICKER:


            case Constant.STRING_ACTION_SCREEN_CAST:
            case Constant.STRING_ACTION_OPEN_SYSTEM:
            case Constant.STRING_ACTION_SCREEN_SHOT:
            case Constant.STRING_ACTION_SYNC:
            case Constant.STRING_ACTION_SCREEN_LOCK:
            case Constant.CLOCK:
            case Constant.STRING_ACTION_CAMERA:
                setBg(false);
                break;
            case Constant.STRING_ACTION_BATTERY:
                setBg(SettingUtils.isPowerSaveMode(context));
                break;

            case Constant.STRING_ACTION_DATA_MOBILE:
//                callBackUpdateUi.stage(action, contentDataMobile.dataMobileUtils.isDataEnable());

                break;
            case Constant.STRING_ACTION_BLUETOOTH:
                callBackUpdateUi.stage(action, mBluetoothAdapter != null && mBluetoothAdapter.isEnabled(), 0);
                break;
            case Constant.DARK_MODE:
                if (NotyControlCenterServicev614.getInstance() != null) {
                    setBg(NotyControlCenterServicev614.getInstance().isDarkModeOn);
                }
                break;
        }

//        AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);

//        switch (audioManager.getRingerMode()) {
//          case AudioManager.RINGER_MODE_VIBRATE:
//            stringAction = "Vibrate";
//          case AudioManager.RINGER_MODE_NORMAL:
//            stringAction = "Sound";
//            break;
//          case AudioManager.RINGER_MODE_SILENT:
//            stringAction=  "Silent";
//            break;
//        }
//        callBackUpdateSound.updateSound(stringAction, );

    }

    private void changeTypeSound(int value) {
        setIconText(value);
        switch (value) {
            case AudioManager.RINGER_MODE_VIBRATE:
            case AudioManager.RINGER_MODE_NORMAL:
                setBg(true);
                break;
            case AudioManager.RINGER_MODE_SILENT:
                setBg(false);
                break;
        }
    }

    private void setIconText(int value) {
        int resource = R.drawable.ic_mi_sounds;
        String text = getContext().getString(R.string.text_sound);
        switch (value) {
            case AudioManager.RINGER_MODE_VIBRATE:
                resource = R.drawable.ic_mi_vibrate;
                text = getContext().getString(R.string.text_vibrate);
                break;
            case AudioManager.RINGER_MODE_SILENT:
                resource = R.drawable.ic_mi_sound_silent;
                text = getContext().getString(R.string.text_silent);
                break;
        }
        nameAction.setText(text);
        imgIcon.setImageResource(resource);
    }

    private void setBg(boolean b) {
        stopAniZoom();
        int resourceBg = getResources().getIdentifier(
                ThemeHelper.itemControl.getControlCenter().getIconControl(),
                "drawable",
                context.getPackageName()
        );
        imgIcon.setBackgroundResource(resourceBg);

        if (b) {
            imgIcon.setColorFilter(Color.parseColor(ThemeHelper.itemControl.getControlCenter().getIconColorSelectControl()));
            imgIcon.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(ThemeHelper.itemControl.getControlCenter().getBackgroundColorSelectControl2())));
        } else {
            imgIcon.clearColorFilter();
            imgIcon.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(ThemeHelper.itemControl.getControlCenter().getBackgroundColorDefaultControl())));
        }

    }

    @Override
    public void onSyncsStarted() {

    }

    @Override
    public void onSyncsFinished() {

    }
}

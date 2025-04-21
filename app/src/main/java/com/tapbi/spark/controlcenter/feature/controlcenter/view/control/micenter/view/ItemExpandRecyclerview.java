package com.tapbi.spark.controlcenter.feature.controlcenter.view.control.micenter.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.media.AudioManager;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.data.repository.ThemeHelper;
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CallBackUpdateUi;
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CloseMiControlView;
import com.tapbi.spark.controlcenter.feature.controlcenter.utils.DataMobileUtils;
import com.tapbi.spark.controlcenter.feature.controlcenter.utils.HostPostUtils;
import com.tapbi.spark.controlcenter.feature.controlios14.model.InfoSystem;
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614;
import com.tapbi.spark.controlcenter.utils.DensityUtils;
import com.tapbi.spark.controlcenter.utils.MethodUtils;
import com.tapbi.spark.controlcenter.utils.SettingUtils;

import timber.log.Timber;

public class ItemExpandRecyclerview extends BaseItemRecyclerView {
    private final Path path = new Path();
    public boolean isFirst = true;
    public float width;
    public Context context;
    public TextView nameAction;
    private ConstraintLayout parentItemExpand;
    private InfoSystem infoSystem;
    private ImageView imgIcon;
    private TextView stateAction;
    private Paint paintRadius;
    private int idCategory;
    private final CallBackUpdateUi callBackUpdateUi = new CallBackUpdateUi() {
        @Override
        public void stage(String valueRegister, boolean b, int pos) {
            if (valueRegister.equals(MethodUtils.getAction(getContext(), nameAction.getText().toString()))) {
                if (infoSystem.getName().equals(Constant.STRING_ACTION_DATA_MOBILE)) {
                    boolean enabled = SettingUtils.hasSimCard(context) && !SettingUtils.isAirplaneModeOn(getContext()) && new DataMobileUtils(getContext()).isDataEnable();
                    setBg(enabled, pos);
                    updateText(enabled);
                } else {
                    setBg(b, pos);
                    updateText(b);
                }
            }
        }


    };
    private RectF rectFRadius;
    private int pos;
    private CloseMiControlView closeMiControlView;
    private String action = "";

    public ItemExpandRecyclerview(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ItemExpandRecyclerview(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    public ItemExpandRecyclerview(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setStageAction(String valueRegister, boolean b, int pos) {
        if (valueRegister.equals(infoSystem.getName())) {
            if (infoSystem.getName().equals(Constant.STRING_ACTION_DATA_MOBILE)) {
                boolean enabled = SettingUtils.hasSimCard(context) && !SettingUtils.isAirplaneModeOn(getContext()) && new DataMobileUtils(getContext()).isDataEnable();
                setBg(enabled, pos);
                updateText(enabled);
            } else {
                setBg(b, pos);
                updateText(b);
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //enableListener(infoSystem.getUri(), nameAction.getText().toString(), callBackUpdateSound, callBackUpdateUi, closeMiControlView);
    }

    public void setData(InfoSystem infoSystem, int pos, CloseMiControlView closeMiControlView) {
        if (isFirst) {
            isFirst = false;
            data(infoSystem, pos, closeMiControlView);
        }
    }


    public void data(InfoSystem infoSystem, int pos, CloseMiControlView closeMiControlView) {
        this.pos = pos;
        setBg(false, pos);
        this.infoSystem = infoSystem;
        this.closeMiControlView = closeMiControlView;
        imgIcon.setImageResource(infoSystem.getIcon());
        nameAction.setText(MethodUtils.getNameActionShowTextView(getContext(), infoSystem.getName()));
        updateText(false);
        setUpData();
    }

    private void init(Context context) {
        idCategory = ThemeHelper.itemControl.getIdCategory();
        this.context = context;
        setLayerType(LAYER_TYPE_NONE, null);
        LayoutInflater.from(getContext()).inflate(R.layout.view_item_expand, this);
        findLayout();
        paintRadius = new Paint();
        if (idCategory == Constant.VALUE_CONTROL_CENTER) {
            if (ThemeHelper.itemControl.getControlCenter() != null && ThemeHelper.itemControl.getControlCenter().getBackgroundColorDefaultControl() != null) {
                paintRadius.setColor(Color.parseColor(ThemeHelper.itemControl.getControlCenter().getBackgroundColorDefaultControl()));
            }
        } else {
            if (ThemeHelper.itemControl.getPixel() != null && ThemeHelper.itemControl.getPixel().getBackgroundDefaultControl() != null) {
                paintRadius.setColor(Color.parseColor(ThemeHelper.itemControl.getPixel().getBackgroundDefaultControl()));
            }
        }
        paintRadius.setAntiAlias(true);
        rectFRadius = new RectF(0f, 0f, 0f, 0f);
        parentItemExpand.setOnClickListener(this);
        parentItemExpand.setOnLongClickListener(v -> onLongClickFromChild());
    }

    private void findLayout() {
        parentItemExpand = findViewById(R.id.parentItemExpand);
        imgIcon = findViewById(R.id.imgIcon);
        nameAction = findViewById(R.id.nameAction);
        stateAction = findViewById(R.id.stateAction);
        try {
            Typeface typeface = Typeface.createFromAsset(App.mContext.getAssets(), Constant.FOLDER_FONT_CONTROL_ASSETS + ThemeHelper.itemControl.getFont());
            if (typeface != null) {
                nameAction.setTypeface(typeface);
                stateAction.setTypeface(typeface);
            }
            if (idCategory == Constant.VALUE_CONTROL_CENTER) {
                if (ThemeHelper.itemControl.getControlCenter() != null && ThemeHelper.itemControl.getControlCenter().getColorTextStatusControl() != null) {
                    stateAction.setTextColor(Color.parseColor(ThemeHelper.itemControl.getControlCenter().getColorTextStatusControl()));
                }
            } else {
                if (ThemeHelper.itemControl.getPixel() != null && ThemeHelper.itemControl.getPixel().getColorTextStatusControl() != null) {
                    stateAction.setTextColor(Color.parseColor(ThemeHelper.itemControl.getPixel().getColorTextStatusControl()));
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }


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
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (getVisibility() == VISIBLE) {
            updateStatusActionSync();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        rectFRadius.bottom = getHeight();
        rectFRadius.right = width;
        if (idCategory == Constant.VALUE_CONTROL_CENTER) {
            path.addRoundRect(
                    rectFRadius,
                    DensityUtils.pxFromDp(getContext(), 15f),
                    DensityUtils.pxFromDp(getContext(), 15f),
                    Path.Direction.CW
            );
        } else if (ThemeHelper.itemControl.getIdCategory() == Constant.VALUE_PIXEL) {
            path.addRoundRect(
                    rectFRadius,
                    DensityUtils.pxFromDp(getContext(), 20f),
                    DensityUtils.pxFromDp(getContext(), 20f),
                    Path.Direction.CW);
        }

        canvas.clipPath(path);
        canvas.drawRect(rectFRadius, paintRadius);
    }

    private void updateStatusActionSync() {
        if (action.equals(Constant.STRING_ACTION_SYNC)) {
            setBg(SettingUtils.isSyncAutomaticallyEnable(), pos);
        }
    }

    private void setUpData() {
        action = MethodUtils.getAction(getContext(), nameAction.getText().toString());
        enableListener(infoSystem.getUri(), action, pos, (valueRegister, value) -> {
            if (valueRegister.equals("Sound") || valueRegister.equals("Vibrate") || valueRegister.equals("Silent")) {
                changeTypeSound(value, pos);
            }
        }, callBackUpdateUi, closeMiControlView);
        switch (action) {
            case "Flash light" -> {
                if (NotyControlCenterServicev614.getInstance() != null) {
                    setBg(NotyControlCenterServicev614.getInstance().isFlashOn, pos);
                }
            }
            case "Wifi" -> callBackUpdateUi.stage(action, SettingUtils.isEnableWifi(context), pos);
            case "Data mobile" ->
                    callBackUpdateUi.stage(action, new DataMobileUtils(context).isDataEnable(), pos);
            case "Bluetooth" ->
                    callBackUpdateUi.stage(action, mBluetoothAdapter != null && mBluetoothAdapter.isEnabled(), pos);
            case Constant.DARK_MODE -> {
                if (NotyControlCenterServicev614.getInstance() != null) {
                    setBg(NotyControlCenterServicev614.getInstance().isDarkModeOn, pos);
                }
            }
            case Constant.STRING_ACTION_AUTO_ROTATE -> {
                setBg(Settings.System.getInt(getContext().getContentResolver(),
                        Settings.System.ACCELEROMETER_ROTATION, 0) == 1, pos);
            }


            case Constant.STRING_ACTION_DO_NOT_DISTURB -> {
                try {
                    boolean value = Settings.Global.getInt(getContext().getContentResolver(), "zen_mode") != 0;
                    setBg(value, pos);
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
            }

            case Constant.STRING_ACTION_LOCATION -> {
                setBg(lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER), pos);

            }
            case Constant.STRING_ACTION_HOST_POST -> {
                setBg(new HostPostUtils(context).getStateWifi(), pos);

            }
            case Constant.STRING_ACTION_SOUND, Constant.STRING_ACTION_VIBRATE,
                 Constant.STRING_ACTION_SILENT -> {
                if (audioManager != null) {
                    changeTypeSound(audioManager.getRingerMode(), pos);
                }
            }
            case Constant.STRING_ACTION_AIRPLANE_MODE -> {
                setBg(Settings.System.getInt(
                        getContext().getContentResolver(),
                        Settings.Global.AIRPLANE_MODE_ON, 0) == 1, pos);
            }
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

    private void changeTypeSound(int value, int pos) {
        setIconText(value);
        switch (value) {
            case AudioManager.RINGER_MODE_VIBRATE:
            case AudioManager.RINGER_MODE_NORMAL:
                setBg(true, pos);
                break;
            case AudioManager.RINGER_MODE_SILENT:
                setBg(false, pos);
                break;
        }
        updateText(true);
    }

    private void updateText(boolean b) {
        stateAction.setText(b ? getContext().getString(R.string.text_on) : getContext().getString(R.string.text_off));
        if (ThemeHelper.itemControl.getIdCategory() == Constant.VALUE_CONTROL_CENTER) {
            if (b) {
                stateAction.setVisibility(VISIBLE);
            } else {
                stateAction.setVisibility(GONE);
            }
        } else {
            if (nameAction.getText().equals(getContext().getString(R.string.open_system)) ||
                    nameAction.getText().equals(getContext().getString(R.string.clock)) ||
                    nameAction.getText().equals(getContext().getString(R.string.screen_cast))) {
                stateAction.setVisibility(GONE);
            } else {
                stateAction.setVisibility(VISIBLE);
            }
        }
    }

    public void setBg(boolean b, int pos) {
        stopAniZoom();
//        clickFlash = b;
        updateText(b);
        switch (idCategory) {
            case Constant.VALUE_CONTROL_CENTER:
                if (ThemeHelper.itemControl.getControlCenter() != null && ThemeHelper.itemControl.getControlCenter().getBackgroundColorDefaultControl() != null) {
                    if (b) {
                        switch (pos) {
                            case 0:
                                paintRadius.setColor(Color.parseColor(ThemeHelper.itemControl.getControlCenter().getBackgroundColorSelectControl1()));
                                break;
                            case 1:
                            case 2:
                                paintRadius.setColor(Color.parseColor(ThemeHelper.itemControl.getControlCenter().getBackgroundColorSelectControl2()));
                                break;

                            case 3:
                                paintRadius.setColor(Color.parseColor(ThemeHelper.itemControl.getControlCenter().getBackgroundColorSelectControl3()));
                                break;
                        }

                    } else {
                        paintRadius.setColor(Color.parseColor(ThemeHelper.itemControl.getControlCenter().getBackgroundColorDefaultControl()));
                    }
                    imgIcon.setColorFilter(b ? Color.parseColor(ThemeHelper.itemControl.getControlCenter().getIconColorSelectControl()) : Color.parseColor(ThemeHelper.itemControl.getControlCenter().getIconColorDefaultControl()));
                    nameAction.setTextColor(b ? Color.parseColor(ThemeHelper.itemControl.getControlCenter().getTextColorSelectControl()) : Color.parseColor(ThemeHelper.itemControl.getControlCenter().getTextColorDefaultControl()));
                }
                break;
            case Constant.VALUE_PIXEL:
                if (ThemeHelper.itemControl.getPixel() != null && ThemeHelper.itemControl.getPixel().getBackgroundSelectControl() != null) {
                    if (b) {
                        paintRadius.setColor(Color.parseColor(ThemeHelper.itemControl.getPixel().getBackgroundSelectControl()));
                    } else {
                        paintRadius.setColor(Color.parseColor(ThemeHelper.itemControl.getPixel().getBackgroundDefaultControl()));
                    }
                    imgIcon.setColorFilter(Color.parseColor(ThemeHelper.itemControl.getPixel().getColorTextControl()));
                    stateAction.setTextColor(Color.parseColor(ThemeHelper.itemControl.getPixel().getColorTextControl()));
                    nameAction.setTextColor(Color.parseColor(ThemeHelper.itemControl.getPixel().getColorTextControl()));
                }
                break;
        }


    }
}

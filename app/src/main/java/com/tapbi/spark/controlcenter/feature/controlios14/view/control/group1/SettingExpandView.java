package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group1;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.databinding.LayoutSettingExpandBinding;
import com.tapbi.spark.controlcenter.feature.controlcenter.utils.DataMobileUtils;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel;
import com.tapbi.spark.controlcenter.interfaces.IListenActionClick;
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614;
import com.tapbi.spark.controlcenter.utils.DensityUtils;
import com.tapbi.spark.controlcenter.utils.SettingUtils;
import com.tapbi.spark.controlcenter.utils.VibratorUtils;

import timber.log.Timber;


public class SettingExpandView extends ConstraintLayout {

    private final Handler handlerAniWifi = new Handler(Looper.getMainLooper());
    public boolean enableWifi, enableData, enableBluetooth, enableSync;
    private Context context;
    private OnSettingExpandListener onSettingExpandListener;
    private DataMobileUtils dataMobileUtils;
    private ScaleAnimation fade_in;
    private int statusHandlerWifi = 3;
    private LayoutSettingExpandBinding binding;
    private final Runnable runnableAniWifi = new Runnable() {
        @Override
        public void run() {
            if (binding != null && binding.wifiExpand != null) {
                if (statusHandlerWifi == 3) {
                    statusHandlerWifi = 1;
                    binding.wifiExpand.setImageResource(R.drawable.ic_wifi_on_ios_1);
                } else if (statusHandlerWifi == 1) {
                    statusHandlerWifi = 2;
                    binding.wifiExpand.setImageResource(R.drawable.ic_wifi_on_ios_2);
                } else {
                    statusHandlerWifi = 3;
                    binding.wifiExpand.setImageResource(R.drawable.wifi_on);
                }
                handlerAniWifi.postDelayed(this, 400);
            }
        }
    };
    private Paint paint;
    private String colorDefaultBackground = "#80000000";
    private Boolean isSelect = false;
    private Float cornerBackground = 0.8f;
    private DataSetupViewControlModel dataSetupViewControlModel;
    private String textColor = "#000000";
    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Timber.e("hoangld v " + v);
            if (v == binding.airplaneExpand || v == binding.dataExpand || v == binding.wifiExpand || v == binding.locationExpand) {
                if (!NotyControlCenterServicev614.getInstance().allowClickAction()) {
                    if (NotyControlCenterServicev614.getInstance() != null) {
                        NotyControlCenterServicev614.getInstance().showToast(context.getString(R.string.wait_until_job_done));
                    }
                    return;
                }
            }


            if (v == binding.airplaneExpand) {
                statAniZoom(v);
                NotyControlCenterServicev614.getInstance().setHandingAction(new IListenActionClick() {
                    @Override
                    public void noFindAction() {
                        stopAniZoom(binding.airplaneExpand);
                    }

                    @Override
                    public void actionClicked() {
                        stopAniZoom(binding.airplaneExpand);
                    }
                }, Constant.STRING_ACTION_AIRPLANE_MODE);
            } else if (v == binding.dataExpand) {
                if (!NotyControlCenterServicev614.getInstance().isAirPlaneModeEnabled()) {
                    statAniZoom(v);
                }
                NotyControlCenterServicev614.getInstance().setHandingAction(new IListenActionClick() {
                    @Override
                    public void noFindAction() {
                        stopAniZoom(binding.dataExpand);
                    }

                    @Override
                    public void actionClicked() {
                        stopAniZoom(binding.dataExpand);
                    }
                }, Constant.STRING_ACTION_DATA_MOBILE);

            } else if (v == binding.wifiExpand) {
                //animationClickWifi();
                //NotyControlCenterServicev614.getInstance().setHandingAction(() -> cancelAniWifi(), Constant.STRING_ACTION_WIFI);
                Timber.e("NVQ STRING_ACTION_WIFI2");
                NotyControlCenterServicev614.getInstance().setWifiNoty();
                onSettingExpandListener.onHideControl();
            } else if (v == binding.bluetoothExpand) {
                statAniZoom(v);
                SettingUtils.setOnOffBluetooth(context);
                enableBluetooth = !enableBluetooth;
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    onSettingExpandListener.onHideControl();
                } else if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    onSettingExpandListener.onHideControl();
                }
            } else if (v == binding.syncExpand) {
//                SettingUtils.setDataSaver(context);
                SettingUtils.setSyncAutomatically();
                enableSync = !enableSync;
                updateSync();
            } else if (v == binding.locationExpand) {
                statAniZoom(v);
                NotyControlCenterServicev614.getInstance().setHandingAction(new IListenActionClick() {
                    @Override
                    public void noFindAction() {
                        stopAniZoom(binding.locationExpand);
                    }

                    @Override
                    public void actionClicked() {
                        stopAniZoom(binding.locationExpand);
                    }
                }, Constant.STRING_ACTION_LOCATION);
            }
        }
    };

    public SettingExpandView(Context context) {
        super(context);
        init(context);
    }

    public SettingExpandView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SettingExpandView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setOnSettingExpandListener(OnSettingExpandListener onSettingExpandListener) {
        this.onSettingExpandListener = onSettingExpandListener;
    }

    private void init(Context context) {
        this.context = context;
//        if (new DensityUtils().getOrientationWindowManager(getContext()) == Configuration.ORIENTATION_PORTRAIT) {
//            LayoutInflater.from(context).inflate(R.layout.layout_setting_expand, this, true);
//        } else {
//            LayoutInflater.from(context).inflate(R.layout.layout_setting_expand_land, this, true);
//        }
        binding = LayoutSettingExpandBinding.inflate(LayoutInflater.from(context), this, true);


        binding.airplaneExpand.setOnClickListener(onClickListener);
        binding.tvAirPlane.setSelected(true);
        binding.dataExpand.setOnClickListener(onClickListener);
        binding.wifiExpand.setOnClickListener(onClickListener);
        binding.bluetoothExpand.setOnClickListener(onClickListener);
        binding.syncExpand.setOnClickListener(onClickListener);
        binding.locationExpand.setOnClickListener(onClickListener);

        binding.airplaneExpand.setOnLongClickListener(v -> {
            if (App.tinyDB.getBoolean(Constant.VIBRATOR_CONTROL_LONG_CLICK, Constant.VALUE_DEFAULT_VIBRATOR)) {
                VibratorUtils.getInstance(context).vibrator(VibratorUtils.TIME_DEFAULT);
            }
            SettingUtils.intentChangeAirPlane(getContext());
            onSettingExpandListener.onHideControl();
            return true;
        });
        binding.dataExpand.setOnLongClickListener(v -> {
            if (App.tinyDB.getBoolean(Constant.VIBRATOR_CONTROL_LONG_CLICK, Constant.VALUE_DEFAULT_VIBRATOR)) {
                VibratorUtils.getInstance(context).vibrator(VibratorUtils.TIME_DEFAULT);
            }
            if (!SettingUtils.hasSimCard(getContext())) {
                NotyControlCenterServicev614.getInstance().showDialogContent(() -> {
                    onSettingExpandListener.onHideControl();
                });
                return true;
            }
            SettingUtils.intentChangeDataMobile(getContext());
            onSettingExpandListener.onHideControl();
            return true;
        });
        binding.wifiExpand.setOnLongClickListener(v -> {
            if (App.tinyDB.getBoolean(Constant.VIBRATOR_CONTROL_LONG_CLICK, Constant.VALUE_DEFAULT_VIBRATOR)) {
                VibratorUtils.getInstance(context).vibrator(VibratorUtils.TIME_DEFAULT);
            }
            SettingUtils.intentChangeWifi(getContext());
            Timber.e("NVQ onHideControl1");
            onSettingExpandListener.onHideControl();
            return true;
        });
        binding.bluetoothExpand.setOnLongClickListener(v -> {
            if (App.tinyDB.getBoolean(Constant.VIBRATOR_CONTROL_LONG_CLICK, Constant.VALUE_DEFAULT_VIBRATOR)) {
                VibratorUtils.getInstance(context).vibrator(VibratorUtils.TIME_DEFAULT);
            }
            SettingUtils.intentChangeBlueTooth(getContext());
            onSettingExpandListener.onHideControl();
            return true;
        });
        binding.syncExpand.setOnLongClickListener(v -> {
            if (App.tinyDB.getBoolean(Constant.VIBRATOR_CONTROL_LONG_CLICK, Constant.VALUE_DEFAULT_VIBRATOR)) {
                VibratorUtils.getInstance(context).vibrator(VibratorUtils.TIME_DEFAULT);
            }
            SettingUtils.intentChangeSync(getContext());
            onSettingExpandListener.onHideControl();
            return true;
        });
        binding.locationExpand.setOnLongClickListener(v -> {
            if (App.tinyDB.getBoolean(Constant.VIBRATOR_CONTROL_LONG_CLICK, Constant.VALUE_DEFAULT_VIBRATOR)) {
                VibratorUtils.getInstance(context).vibrator(VibratorUtils.TIME_DEFAULT);
            }
            SettingUtils.intentChangeLocation(getContext());
            onSettingExpandListener.onHideControl();
            return true;
        });

        enableSync = SettingUtils.isSyncAutomaticallyEnable();
        enableBluetooth = SettingUtils.isEnableBluetooth(getContext());
        enableWifi = SettingUtils.isEnableWifi(context);

        dataMobileUtils = new DataMobileUtils(context);
        updateDataMobile(dataMobileUtils.isDataEnable());
    }

    public void update() {
        enableSync = SettingUtils.isSyncAutomaticallyEnable();
        enableBluetooth = SettingUtils.isEnableBluetooth(getContext());
        enableWifi = SettingUtils.isEnableWifi(context);
//        updateWifi();
//        updateBluetooth();
        updateSync();
    }

    private void updateSync() {
        if (binding != null && binding.syncExpand != null) {
            if (enableSync) {
                binding.syncExpand.setImageResource(R.drawable.sync_on);
            } else {
                binding.syncExpand.setImageResource(R.drawable.sync_off);
            }
        }
    }

    public void updateLocation(boolean b) {
        if (binding != null && binding.locationExpand != null) {
            stopAniZoom(binding.locationExpand);
            if (b) {
                binding.locationExpand.setImageResource(R.drawable.location_on);
            } else {
                binding.locationExpand.setImageResource(R.drawable.location_off);
            }
        }
    }

    public void updateAriMode(boolean b) {
        if (binding != null && binding.airplaneExpand != null) {
            stopAniZoom(binding.airplaneExpand);
            if (b) {
                binding.airplaneExpand.setImageResource(R.drawable.airplane_on);
            } else {
                binding.airplaneExpand.setImageResource(R.drawable.airplane_off);
            }
        }
    }

    public void updateBluetooth() {
        if (binding != null && binding.bluetoothExpand != null) {
            stopAniZoom(binding.bluetoothExpand);
            if (enableBluetooth) {
                binding.bluetoothExpand.setImageResource(R.drawable.bluetooth_on);
            } else {
                binding.bluetoothExpand.setImageResource(R.drawable.bluetooth_off);
            }
        }
    }

    public void updateWifi(boolean b) {
        this.enableWifi = b;
        cancelAniWifi();
        setViewWifi(b);
    }

    private void setViewWifi(boolean b) {
        if (binding != null && binding.wifiExpand != null) {
            if (b) {
                binding.wifiExpand.setImageResource(R.drawable.wifi_on);
            } else {
                binding.wifiExpand.setImageResource(R.drawable.wifi_off);
            }
        }
    }

    public void updateDataMobile(boolean b) {
        this.enableData = b;
        if (binding != null && binding.dataExpand != null) {
            stopAniZoom(binding.dataExpand);
        }
        setViewDataMobile(b);
    }

    private void setViewDataMobile(boolean b) {
        if (binding != null && binding.dataExpand != null) {
            if (b) {
                binding.dataExpand.setImageResource(R.drawable.anten_on);
            } else {
                binding.dataExpand.setImageResource(R.drawable.anten_off);
            }
        }
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (binding != null) {
            if (getVisibility() == View.VISIBLE && dataMobileUtils != null) {
                updateDataMobile(!SettingUtils.isAirplaneModeOn(getContext()) && dataMobileUtils.isDataEnable());
            } else if (visibility != View.VISIBLE) {
                cancelAniWifi();
                setViewWifi(enableWifi);

                stopAniZoom(binding.dataExpand);
                setViewDataMobile(enableData);
            }
            stopAniZoom(binding.airplaneExpand);
            stopAniZoom(binding.bluetoothExpand);
            stopAniZoom(binding.locationExpand);
        }

    }


    private void animationClickWifi() {
        cancelAniWifi();
        if (handlerAniWifi != null && runnableAniWifi != null) {
            handlerAniWifi.postDelayed(runnableAniWifi, 300);
        }
    }

    private void cancelAniWifi() {
        if (handlerAniWifi != null && runnableAniWifi != null) {
            handlerAniWifi.removeCallbacks(runnableAniWifi);
        }
    }

    protected void statAniZoom(View view) {
        if (view != null) {
            view.clearAnimation();
            if (fade_in == null) {
                fade_in = new ScaleAnimation(0.8f, 1.1f, 0.8f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                fade_in.setDuration(1000);     // animation duration in milliseconds
                fade_in.setFillAfter(
                        true);    // If fillAfter is true, the transformation that this animation performed will persist when it is finished.
                fade_in.setRepeatMode(Animation.REVERSE);
                fade_in.setRepeatCount(Animation.INFINITE);
            }
            view.startAnimation(fade_in);
        }
    }

    protected void stopAniZoom(View view) {
        if (view != null) {
            view.clearAnimation();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int width = MeasureSpec.getSize(widthMeasureSpec);
//        int height = (int) (width / 0.8f);
//        int newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void changeFont(Typeface typeface, String textColor){
        if (binding!=null){
            binding.tvAirPlane.setTypeface(typeface);
            binding.tvWifi.setTypeface(typeface);
            binding.tvData.setTypeface(typeface);
            binding.tvBluetooth.setTypeface(typeface);
            binding.tvLocation.setTypeface(typeface);
            binding.tvSynData.setTypeface(typeface);
        }

//        binding.tvAirPlane.setTextColor(Color.parseColor(textColor));
//        binding.tvWifi.setTextColor(Color.parseColor(textColor));
//        binding.tvData.setTextColor(Color.parseColor(textColor));
//        binding.tvBluetooth.setTextColor(Color.parseColor(textColor));
//        binding.tvLocation.setTextColor(Color.parseColor(textColor));
//        binding.tvSynData.setTextColor(Color.parseColor(textColor));
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (paint == null) {
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
        paint.setColor(Color.parseColor(colorDefaultBackground));
        float corner = DensityUtils.pxFromDp(getContext(), 20);
        canvas.drawRoundRect(0, 0, getWidth(), getHeight(), corner, corner, paint);
        super.dispatchDraw(canvas);
    }

    public void changeColorBackground(String colorDefaultBackground) {
//        this.colorDefaultBackground = colorDefaultBackground;
//        invalidate();
    }

    public interface OnSettingExpandListener {
        void onHideControl();
    }
}

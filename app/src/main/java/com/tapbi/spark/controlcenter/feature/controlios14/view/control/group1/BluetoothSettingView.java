package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group1;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Handler;
import androidx.annotation.Nullable;

import android.util.AttributeSet;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.utils.SettingUtils;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ImageBase;
import com.tapbi.spark.controlcenter.utils.VibratorUtils;


@SuppressLint("AppCompatCustomView")
public class BluetoothSettingView extends ImageBase {

    private Context context;
    private OnAnimationListener onAnimationListener;

//    private BluetoothReceiver bluetoothReceiver;
//    private IntentFilter filter;
    private Handler handler;

    public boolean enableBluetooth;

    public boolean isEnableBluetooth() {
        return enableBluetooth;
    }

    public BluetoothSettingView(Context context) {
        super(context);
        init(context);
    }

    public BluetoothSettingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BluetoothSettingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        handler = new Handler();
        updateBluetoothState();
    }

    public void setViewTouching(boolean touching) {
        anotherViewTouching = touching;
    }


    @Override
    protected void click() {
        statAniZoom();
        enableBluetooth = !enableBluetooth;
        SettingUtils.setOnOffBluetooth(context);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (onAnimationListener != null) {
                    onAnimationListener.onClick();
                }
            }
        }, 300);
    }

    @Override
    protected void longClick() {
        if (App.tinyDB.getBoolean(Constant.VIBRATOR_CONTROL_LONG_CLICK, Constant.VALUE_DEFAULT_VIBRATOR)) {
            VibratorUtils.getInstance(context).vibrator(VibratorUtils.TIME_DEFAULT);
        }
        SettingUtils.intentChangeBlueTooth(getContext());
        if (onAnimationListener != null) {
            onAnimationListener.onLongClick();
        }
    }

    @Override
    protected void onDown() {
        if (onAnimationListener != null) {
            onAnimationListener.onDown();
        }
        animationDown();
    }

    @Override
    protected void onUp() {
        if (onAnimationListener != null) {
            onAnimationListener.onUp();
        }
        animationUp();
    }

    public void setOnAnimationListener(OnAnimationListener onAnimationListener) {
        this.onAnimationListener = onAnimationListener;
    }

    public void updateBluetoothState() {
        updateEnableBluetooth();
//        updateImage();
    }

    private void updateEnableBluetooth() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter==null){
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            enableBluetooth = false;
        } else {
            enableBluetooth = true;
        }
    }



    public void updateImage() {
        stopAniZoom();
//        if (!enableBluetooth) {
//            setImageResource(R.drawable.bluetooth_off);
//        } else {
//            setImageResource(R.drawable.bluetooth_on);
//        }
        changeIsSelect(enableBluetooth);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int paddingIcon = (int) (w * 0.267);
        setPadding(paddingIcon, paddingIcon, paddingIcon, paddingIcon);
    }

//    private class BluetoothReceiver extends BroadcastReceiver {
//        public void onReceive(Context context, Intent intent) {
////            updateBluetoothState();
//        }
//    }
}

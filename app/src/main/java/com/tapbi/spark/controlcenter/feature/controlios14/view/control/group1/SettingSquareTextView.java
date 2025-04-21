package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group1;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tapbi.spark.controlcenter.databinding.LayoutControlSettingSquareTextBinding;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlSettingIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ConstraintLayoutBase;

public class SettingSquareTextView extends ConstraintLayoutBase {

    private final Handler handler = new Handler();
    public SettingView.OnSettingListener onSettingListener;
    private final Runnable runnable = this::onLongClick;
    private ControlSettingIosModel controlSettingIOS;
    private LayoutControlSettingSquareTextBinding binding;

    public SettingSquareTextView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public SettingSquareTextView(@NonNull Context context, ControlSettingIosModel controlSettingIosModel, DataSetupViewControlModel dataSetupViewControlModel) {
        super(context);
        this.controlSettingIOS = controlSettingIosModel;
        this.dataSetupViewControlModel = dataSetupViewControlModel;
        init(context);
    }

    public SettingSquareTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SettingSquareTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void changeControlSettingIos(ControlSettingIosModel controlSettingIOS) {
        this.controlSettingIOS = controlSettingIOS;
        initView();
    }

    public void initView() {
        if (controlSettingIOS != null) {
            changeColorBackground(controlSettingIOS.getBackgroundDefaultColorViewParent(), controlSettingIOS.getBackgroundSelectColorViewParent(), controlSettingIOS.getCornerBackgroundViewParent());
            binding.airplaneAction.changeData(controlSettingIOS);
            binding.dataAction.changeData(controlSettingIOS);
            binding.wifiAction.changeData(controlSettingIOS);
            binding.bluetoothAction.changeData(controlSettingIOS);
            binding.tvWifi.setTextColor(Color.parseColor(controlSettingIOS.getColorTextTitle()));
            binding.tvWifi.setTypeface(dataSetupViewControlModel.getTypefaceText());
            binding.tvDataMobile.setTextColor(Color.parseColor(controlSettingIOS.getColorTextTitle()));
            binding.tvDataMobile.setTypeface(dataSetupViewControlModel.getTypefaceText());
            binding.tvBluetooth.setTextColor(Color.parseColor(controlSettingIOS.getColorTextTitle()));
            binding.tvBluetooth.setTypeface(dataSetupViewControlModel.getTypefaceText());
            binding.tvAirPlane.setTextColor(Color.parseColor(controlSettingIOS.getColorTextTitle()));
            binding.tvAirPlane.setTypeface(dataSetupViewControlModel.getTypefaceText());
        }
        binding.tvAirPlane.setSelected(true);
        binding.tvDataMobile.setSelected(true);
    }

    void init(Context context) {
        binding = LayoutControlSettingSquareTextBinding.inflate(LayoutInflater.from(context), this, true);

        initView();
        binding.airplaneAction.setOnAnimationListener(new AirPlaneSettingView.OnAnimationListener() {
            @Override
            public void onDown() {
                if (onSettingListener != null) {
                    onSettingListener.onDown();
                }
            }

            @Override
            public void onUp() {
                if (onSettingListener != null) {
                    onSettingListener.onUp();
                }
            }

            @Override
            public void onClick() {
                if (onSettingListener != null) {
                    onSettingListener.onHide();
                }
            }

            @Override
            public void onLongClick() {
                if (onSettingListener != null) {
                    onSettingListener.onHide();
                }
            }

            @Override
            public void onClose() {

            }
        });

        binding.dataAction.setOnAnimationListener(new DataSettingView.OnAnimationListener() {
            @Override
            public void onDown() {
                if (onSettingListener != null) {
                    onSettingListener.onDown();
                }
            }

            @Override
            public void onUp() {
                if (onSettingListener != null) {
                    onSettingListener.onUp();
                }
            }

            @Override
            public void onClick() {
                if (onSettingListener != null) {
                    onSettingListener.onHide();
                }
            }

            @Override
            public void onLongClick() {
                if (onSettingListener != null) {
                    onSettingListener.onHide();
                }
            }

            @Override
            public void onClose() {

            }
        });

        binding.wifiAction.setOnAnimationListener(new WifiSettingView.OnAnimationListener() {
            @Override
            public void onDown() {
                if (onSettingListener != null) {
                    onSettingListener.onDown();
                }
            }

            @Override
            public void onUp() {
                if (onSettingListener != null) {
                    onSettingListener.onUp();
                }
            }

            @Override
            public void onClick() {
                if (onSettingListener != null) {
                    onSettingListener.onWifiChange();
                }
            }

            @Override
            public void onLongClick() {
                if (onSettingListener != null) {
                    onSettingListener.onHide();
                }
            }

            @Override
            public void onClose() {
                onSettingListener.onClose();
            }
        });

        binding.bluetoothAction.setOnAnimationListener(new BluetoothSettingView.OnAnimationListener() {
            @Override
            public void onDown() {
                if (onSettingListener != null) {
                    onSettingListener.onDown();
                }
            }

            @Override
            public void onUp() {
                if (onSettingListener != null) {
                    onSettingListener.onUp();
                }
            }

            @Override
            public void onClick() {
                if (onSettingListener != null) {
                    onSettingListener.onBluetoothChange(binding.bluetoothAction.isEnableBluetooth());
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        onSettingListener.onHide();
                    }
                }
            }

            @Override
            public void onLongClick() {
                if (onSettingListener != null) {
                    onSettingListener.onHide();
                }
            }

            @Override
            public void onClose() {

            }
        });
    }

    public void onLongClick() {
        if (onSettingListener != null) {
            onSettingListener.onLongClick(this);
        }
    }

    public void updateBgAirplane(boolean b) {
        binding.airplaneAction.updateAirPlaneState(b);
    }

    public void updateBg(boolean b) {
        binding.bluetoothAction.enableBluetooth = b;
        binding.bluetoothAction.updateImage();
    }

    public void updateWifi(boolean b) {
        binding.wifiAction.updateState(b);
    }

    public void updateDataMobile(boolean b) {
        binding.dataAction.updateState(b);
    }

    public void setViewTouching(boolean touching) {
        binding.airplaneAction.setViewTouching(touching);
        binding.wifiAction.setViewTouching(touching);
        binding.dataAction.setViewTouching(touching);
        binding.bluetoothAction.setViewTouching(touching);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (onSettingListener != null) {
                    onSettingListener.onDown();
                }
                handler.postDelayed(runnable, ViewConfiguration.getLongPressTimeout());
                animationDown();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (onSettingListener != null) {
                    onSettingListener.onUp();
                }
                handler.removeCallbacks(runnable);
                animationUp();
                break;
        }
        return true;
    }

    public void setOnSettingListener(SettingView.OnSettingListener onSettingListener) {
        this.onSettingListener = onSettingListener;
    }

    public void updateState() {
//        dataAction.updateState();
//        airPlaneAction.updateAirPlaneState();
        binding.bluetoothAction.updateBluetoothState();
    }

    public void destroy() {
        //wifiAction.destroy();
        //bluetoothAction.destroy();
        //dataAction.destroy();
        //airPlaneAction.destroy();
    }


    public interface OnSettingListener {
        void onDown();

        void onUp();

        void onHide();

        void onWifiChange();

        void onBluetoothChange(boolean change);

        void onLongClick();

        void onClose();
    }
}

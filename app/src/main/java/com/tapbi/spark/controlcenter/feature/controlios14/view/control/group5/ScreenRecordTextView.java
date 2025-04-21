package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.tapbi.spark.controlcenter.databinding.LayoutControlRecordTextViewBinding;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlSettingIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ConstraintLayoutBase;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.callback.OnClickSettingListener;
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614;
import com.tapbi.spark.controlcenter.ui.RequestPermissionActivity;
import com.tapbi.spark.controlcenter.utils.SettingUtils;

import timber.log.Timber;

public class ScreenRecordTextView extends ConstraintLayoutBase {

    private static ScreenRecordTextView instance;
    private Context context;
    private Handler handler;
    private LayoutControlRecordTextViewBinding binding;
    private OnClickSettingListener onClickSettingListener;
    private CountDownTimer countDownTimer;
    private ControlSettingIosModel controlSettingIosModel;
    private boolean isSelect = false;
    private int dem = 3;

    private boolean isClick = false;

    private Runnable runnableClick = () -> isClick = false;

    public ScreenRecordTextView(Context context) {
        super(context);
        init(context);
    }

    public ScreenRecordTextView(Context context, ControlSettingIosModel controlSettingIosModel, DataSetupViewControlModel dataSetupViewControlModel) {
        super(context);
        this.controlSettingIosModel = controlSettingIosModel;
        this.dataSetupViewControlModel = dataSetupViewControlModel;

        init(context);
    }

    public ScreenRecordTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScreenRecordTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public static ScreenRecordTextView getInstance() {
        return instance;
    }

    public void setOnClickSettingListener(OnClickSettingListener onClickSettingListener) {
        this.onClickSettingListener = onClickSettingListener;
    }

    public void changeData(ControlSettingIosModel controlSettingIosModel) {
        this.controlSettingIosModel = controlSettingIosModel;
        initView();
        invalidate();
    }


    private void init(Context context) {
        this.context = context;
        instance = this;
        handler = new Handler();
        binding = LayoutControlRecordTextViewBinding.inflate(LayoutInflater.from(context), this, true);
        initView();
        setIconView();
        binding.tvRecordd.setTypeface(dataSetupViewControlModel.getTypefaceText());
        binding.tvCountDown.setTypeface(dataSetupViewControlModel.getTypefaceText());
    }

    private void initView() {
        if (controlSettingIosModel != null) {
            initColorIcon();
            changeColorBackground(controlSettingIosModel.getBackgroundDefaultColorViewParent(), controlSettingIosModel.getBackgroundSelectColorViewParent(), controlSettingIosModel.getCornerBackgroundViewParent());
        }
    }


    public void setPadding(int p) {
        binding.circleBig.setPadding(p, p, p, p);
        binding.circleSmall.setPadding(p, p, p, p);
        binding.circleBig.requestLayout();
        binding.circleSmall.requestLayout();
    }

    @Override
    protected void onTouchDown() {
        super.onTouchDown();
        if (!isClick) {
            isClick = true;
            boolean requestPermissionsAudio;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionsAudio = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED;
            } else {
                requestPermissionsAudio = (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) ||
                        (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED);
            }
            if (requestPermissionsAudio) {
                if (onClickSettingListener != null) {
                    onClickSettingListener.onClick();
                }
                handler.postDelayed(() -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        SettingUtils.intentActivityRequestPermission(context, new String[]{Manifest.permission.RECORD_AUDIO});
                    } else {
                        SettingUtils.intentActivityRequestPermission(context, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE});
                    }
                }, 300);
            } else {
                if (NotyControlCenterServicev614.getInstance() != null && NotyControlCenterServicev614.getInstance().getResultDataMediaProjection() == null) {
                    intentAcRecord();

                } else {
                    if (NotyControlCenterServicev614.getInstance() != null) {
                        if (NotyControlCenterServicev614.getInstance().stateRecordScreen == ScreenRecordActionView.STATE.NONE) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                intentAcRecord();
                            } else {
                                NotyControlCenterServicev614.getInstance().setStatusRecordScreen(ScreenRecordActionView.STATE.PREPARE);
                                setIconPrepapre();
                                prepareRecord();
                            }

                        } else if (NotyControlCenterServicev614.getInstance().stateRecordScreen == ScreenRecordActionView.STATE.PREPARE) {
                            NotyControlCenterServicev614.getInstance().setStatusRecordScreen(ScreenRecordActionView.STATE.NONE);
                            setIconStopRecord();
                        } else if (NotyControlCenterServicev614.getInstance().stateRecordScreen == ScreenRecordActionView.STATE.RECORD) {
                            NotyControlCenterServicev614.getInstance().setStatusRecordScreen(ScreenRecordActionView.STATE.NONE);
                            NotyControlCenterServicev614.getInstance().stopRecord();
                        }
                    }

//                    NotyControlCenterService.getInstance().record_circle_small();
                }
            }
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (onClickSettingListener != null) {
//                    onClickSettingListener.onClick();
//                }
//                // todo: lam gi do o day
//            }
//        }, 300);
            handler.postDelayed(runnableClick, 400);

        }

    }

    private void intentAcRecord() {
        if (onClickSettingListener != null) {
            onClickSettingListener.onClick();
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SettingUtils.intentActivityRequestPermission(context, new String[]{RequestPermissionActivity.RECORD_VIDEO});
            }
        }, 300);
    }


    public void setColorTextCount(int color) {
        binding.tvCountDown.setTextColor(color);
    }

    public void setIconStopRecord() {
        isSelect = false;
        changeIsSelect(false);
        initColorIcon();
        binding.circleSmall.setVisibility(VISIBLE);
//        binding.circleSmall.clearColorFilter();
//        binding.circleBig.clearColorFilter();
        binding.tvCountDown.setVisibility(INVISIBLE);
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        NotyControlCenterServicev614.getInstance().setStatusRecordScreen(ScreenRecordActionView.STATE.NONE);
    }

    private void setIconRecord() {
        isSelect = true;
        changeIsSelect(true);
        initColorIcon();
        binding.circleSmall.setVisibility(VISIBLE);
        binding.tvCountDown.setVisibility(INVISIBLE);
    }


    private void initColorIcon() {
        if (controlSettingIosModel != null) {
            if (isSelect) {
                if (controlSettingIosModel.getColorSelectIcon() != null) {
                    binding.circleBig.setColorFilter(Color.parseColor(controlSettingIosModel.getColorSelectIcon()));
                    binding.circleSmall.setColorFilter(Color.parseColor(controlSettingIosModel.getColorSelectIcon()));
                } else {
                    binding.circleBig.setColorFilter(null);
                    binding.circleSmall.setColorFilter(null);
                }
                if (controlSettingIosModel.getColorTextTitleSelect() != null) {
                    binding.tvRecordd.setTextColor(Color.parseColor(controlSettingIosModel.getColorTextTitleSelect()));
                }
            } else {
                if (controlSettingIosModel.getColorDefaultIcon() != null) {
                    binding.circleBig.setColorFilter(Color.parseColor(controlSettingIosModel.getColorDefaultIcon()));
                    binding.circleSmall.setColorFilter(Color.parseColor(controlSettingIosModel.getColorDefaultIcon()));
                } else {
                    binding.circleBig.setColorFilter(null);
                    binding.circleSmall.setColorFilter(null);
                }
                if (controlSettingIosModel.getColorTextTitle() != null) {
                    binding.tvRecordd.setTextColor(Color.parseColor(controlSettingIosModel.getColorTextTitle()));
                }
            }
            if (controlSettingIosModel.getColorDefaultIcon()!=null){
                binding.tvCountDown.setTextColor(Color.parseColor(controlSettingIosModel.getColorDefaultIcon()));
            }

        }
    }

    private void setIconPrepapre() {
        binding.circleSmall.clearColorFilter();
        binding.circleBig.clearColorFilter();
        binding.circleSmall.setVisibility(GONE);
        binding.tvCountDown.setVisibility(VISIBLE);
    }

    private void prepareRecord() {
        dem = 3;
        binding.tvCountDown.setText(dem + "");
        countDownTimer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.tvCountDown.setText(dem + "");
                 dem--;
            }

            @Override
            public void onFinish() {
                setIconRecord();
                NotyControlCenterServicev614.getInstance().record();
            }
        };
        countDownTimer.start();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (getVisibility() == VISIBLE) {
            setIconView();
        }
    }

    private void setIconView() {
        if (NotyControlCenterServicev614.getInstance() != null) {
            if (NotyControlCenterServicev614.getInstance().stateRecordScreen == ScreenRecordActionView.STATE.RECORD) {
                setIconRecord();
            } else if (NotyControlCenterServicev614.getInstance().stateRecordScreen == ScreenRecordActionView.STATE.NONE) {
                setIconStopRecord();
            }
        }
    }

    public enum STATE {NONE, PREPARE, RECORD}
}

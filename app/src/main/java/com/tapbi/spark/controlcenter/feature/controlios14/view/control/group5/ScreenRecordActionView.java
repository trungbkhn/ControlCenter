package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlSettingIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.RelativeLayoutBase;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.callback.OnClickSettingListener;
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614;
import com.tapbi.spark.controlcenter.ui.RequestPermissionActivity;
import com.tapbi.spark.controlcenter.utils.SettingUtils;

import timber.log.Timber;


public class ScreenRecordActionView extends RelativeLayoutBase {

    private static ScreenRecordActionView instance;
    private Context context;
    private Handler handler;
    private ImageView circleBig;
    private ImageView circleSmall;
    private TextView tvCountDown;
    private OnClickSettingListener onClickSettingListener;
    private CountDownTimer countDownTimer;
    private int dem = 3;

    private IListenerUpdateViewRecord iListenerUpdateViewRecord;
    private boolean isClick = false;

    private Runnable runnableClick = () -> isClick = false;


    public ScreenRecordActionView(Context context) {
        super(context);
        init(context);
    }

    public ScreenRecordActionView(Context context, ControlSettingIosModel controlSettingIosModel, DataSetupViewControlModel dataSetupViewControlModel) {
        super(context);
        this.controlSettingIosModel = controlSettingIosModel;
        this.dataSetupViewControlModel = dataSetupViewControlModel;
        init(context);
    }

    public ScreenRecordActionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScreenRecordActionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public static ScreenRecordActionView getInstance() {
        return instance;
    }

    public void setListenerUpdateViewRecord(IListenerUpdateViewRecord iListenerUpdateViewRecord) {
        this.iListenerUpdateViewRecord = iListenerUpdateViewRecord;
    }

    public void setOnClickSettingListener(OnClickSettingListener onClickSettingListener) {
        this.onClickSettingListener = onClickSettingListener;
    }

    public void changeData(ControlSettingIosModel controlSettingIosModel) {
        this.controlSettingIosModel = controlSettingIosModel;
        initColorIcon();
        invalidate();
    }


    private void init(Context context) {
        this.context = context;
        instance = this;
        handler = new Handler();
        LayoutInflater.from(context).inflate(R.layout.layout_action_record, this, true);

        circleBig = findViewById(R.id.circleBig);
        circleSmall = findViewById(R.id.circleSmall);
        tvCountDown = findViewById(R.id.tvCountDown);

        setIconView();
        initColorIcon();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        int paddingIcon = (int) (w * 0.25f);
//        setPadding(paddingIcon);
        int paddingIcon = (int) (w * 0.267);
        setPadding(paddingIcon);
        tvCountDown.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (0.23 * h));

    }

    public void setPadding(int p) {
        circleBig.setPadding(p, p, p, p);
        circleSmall.setPadding(p, p, p, p);
        circleBig.requestLayout();
        circleSmall.requestLayout();
    }

    @Override
    protected void click() {
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
                Timber.e("hachung onClickSettingListener:" + onClickSettingListener);
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
                        if (NotyControlCenterServicev614.getInstance().stateRecordScreen == STATE.NONE) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                intentAcRecord();
                            } else {
                                NotyControlCenterServicev614.getInstance().setStatusRecordScreen(STATE.PREPARE);
                                prepareRecord();
                                setIconPrepapre();
                            }

                        } else if (NotyControlCenterServicev614.getInstance().stateRecordScreen == STATE.PREPARE) {
                            NotyControlCenterServicev614.getInstance().setStatusRecordScreen(STATE.NONE);
                            setIconStopRecord();
                        } else if (NotyControlCenterServicev614.getInstance().stateRecordScreen == STATE.RECORD) {
                            NotyControlCenterServicev614.getInstance().setStatusRecordScreen(STATE.NONE);
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
        Timber.e("hachung onClickSettingListener:" + onClickSettingListener);
        if (onClickSettingListener != null) {
            onClickSettingListener.onClick();
        }
        handler.postDelayed(() -> SettingUtils.intentActivityRequestPermission(context, new String[]{RequestPermissionActivity.RECORD_VIDEO}), 300);
    }

    @Override
    protected void onLongClick() {

    }

    public void setColorTextCount(int color) {
        tvCountDown.setTextColor(color);
    }

    public void setIconStopRecord() {
        changeIsSelect(false);
        circleSmall.setVisibility(VISIBLE);

        if (iListenerUpdateViewRecord != null) {
            iListenerUpdateViewRecord.turnOnOffRecord(false);
        }
        initColorIcon();
        tvCountDown.setVisibility(GONE);
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        NotyControlCenterServicev614.getInstance().setStatusRecordScreen(STATE.NONE);
    }

    private void setIconRecord() {
        changeIsSelect(true);
        if (iListenerUpdateViewRecord != null) {
            iListenerUpdateViewRecord.turnOnOffRecord(true);
        }
        circleSmall.setVisibility(VISIBLE);
        tvCountDown.setVisibility(GONE);
    }

    @Override
    public void initColorIcon() {
        super.initColorIcon();
        if (controlSettingIosModel != null) {
            if (isSelect) {
                if (controlSettingIosModel.getColorSelectIcon() != null) {
                    circleBig.setColorFilter(Color.parseColor(controlSettingIosModel.getColorSelectIcon()));
                    circleSmall.setColorFilter(Color.parseColor(controlSettingIosModel.getColorSelectIcon()));
                } else {
                    circleBig.setColorFilter(null);
                    circleSmall.setColorFilter(null);
                }
            } else {
                if (controlSettingIosModel.getColorDefaultIcon() != null) {
                    circleBig.setColorFilter(Color.parseColor(controlSettingIosModel.getColorDefaultIcon()));
                    circleSmall.setColorFilter(Color.parseColor(controlSettingIosModel.getColorDefaultIcon()));

                } else {
                    circleBig.setColorFilter(null);
                    circleSmall.setColorFilter(null);
                }
            }
            tvCountDown.setTextColor(Color.parseColor(controlSettingIosModel.getColorDefaultIcon()));
        }
    }

    private void setIconPrepapre() {
        initColorIcon();
        circleSmall.setVisibility(GONE);
        tvCountDown.setVisibility(VISIBLE);
    }

    private void prepareRecord() {
        dem = 3;
        tvCountDown.setText(dem + "");
        countDownTimer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvCountDown.setText(dem + "");
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
    protected void onDown() {
        animationDown();
    }

    @Override
    protected void onUp() {
        animationUp();
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
            if (NotyControlCenterServicev614.getInstance().stateRecordScreen == STATE.RECORD) {
                setIconRecord();
            } else if (NotyControlCenterServicev614.getInstance().stateRecordScreen == STATE.NONE) {
                setIconStopRecord();
            }
        }
    }

    public enum STATE {NONE, PREPARE, RECORD}

    public interface IListenerUpdateViewRecord {
        void turnOnOffRecord(boolean b);
    }
}

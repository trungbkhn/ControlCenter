package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group3;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.bumptech.glide.Glide;
import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.databinding.LayoutControlScreenTimeOutSquareBinding;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlSettingIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ConstraintLayoutBase;
import com.tapbi.spark.controlcenter.utils.VibratorUtils;

import java.io.File;

public class ScreenTimeoutSquareView extends ConstraintLayoutBase {

    private Context context;
    private LayoutControlScreenTimeOutSquareBinding binding;
    private ViewPropertyAnimator scaleY = null;
    private boolean down;
    private Handler handler;
    private boolean isLongClick;
    private ControlSettingIosModel controlSettingIOS;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            isLongClick = true;
            longClick();
        }
    };

    private ScreenTimeoutView.OnScreenTimeoutListener onScreenTimeoutListener;

    public ScreenTimeoutSquareView(Context context) {
        super(context);
        init(context);
    }

    public ScreenTimeoutSquareView(Context context, ControlSettingIosModel controlSettingIOS, DataSetupViewControlModel dataSetupViewControlModel) {
        super(context);
        this.controlSettingIOS = controlSettingIOS;
        this.dataSetupViewControlModel = dataSetupViewControlModel;
        init(context);
    }

    public ScreenTimeoutSquareView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScreenTimeoutSquareView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        handler = new Handler();
        binding = LayoutControlScreenTimeOutSquareBinding.inflate(LayoutInflater.from(context), this, true);
        binding.tvScreenTimeOut.setSelected(true);
        initView();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onScreenTimeoutListener.onDown();
                onDown();
                handler.postDelayed(runnable, ViewConfiguration.getLongPressTimeout());
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                handler.removeCallbacks(runnable);
                onUp();

                if (checkClick(event.getX(), event.getY()) && !isLongClick) {
                    click();
                }
                isLongClick = false;
                onScreenTimeoutListener.onUp();
                break;
        }
        return true;
    }



    private void onDown() {
        if (scaleY != null) {
            scaleY.cancel();
        }
        down = true;
        scaleY = animate().scaleX(1.15f).scaleY(1.15f);
        scaleY.setDuration(200).setInterpolator(new DecelerateInterpolator());
        scaleY.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (down) {
                    ViewPropertyAnimator scaleY = animate().scaleX(1.1f).scaleY(1.1f);
                    scaleY.start();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                down = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        scaleY.start();
    }

    private void onUp() {
        if (scaleY != null) {
            scaleY.cancel();
        }
        down = false;
        scaleY = animate().scaleX(1.0f).scaleY(1.0f);
        scaleY.setDuration(200).setInterpolator(new AccelerateInterpolator());
        scaleY.start();
    }
    public void animationShow(Animator.AnimatorListener listener) {
        if (scaleY != null) {
            scaleY.cancel();
        }
        setScaleX(0.8f);
        setScaleY(0.8f);
        setAlpha(0f);
        scaleY = animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(300).setInterpolator(new DecelerateInterpolator());
        if (this instanceof ScreenTimeoutSquareView) {setAnimationListener(listener);}
        scaleY.start();
    }


    public void setOnScreenTimeoutListener(ScreenTimeoutView.OnScreenTimeoutListener onScreenTimeoutListener) {
        this.onScreenTimeoutListener = onScreenTimeoutListener;
    }

    private void click() {
        if (onScreenTimeoutListener != null) {
            onScreenTimeoutListener.onClick();
        }
    }

    private void longClick() {
        if (App.tinyDB.getBoolean(Constant.VIBRATOR_CONTROL_LONG_CLICK, Constant.VALUE_DEFAULT_VIBRATOR)) {
            VibratorUtils.getInstance(context).vibrator(VibratorUtils.TIME_DEFAULT);
        }
        if (onScreenTimeoutListener != null) {
            onScreenTimeoutListener.onLongClick();
        }
    }


    public void initView(){
        if (controlSettingIOS != null){
            changeColorBackground(controlSettingIOS.getBackgroundDefaultColorViewParent(), controlSettingIOS.getBackgroundSelectColorViewParent(), controlSettingIOS.getCornerBackgroundViewParent());
            binding.tvScreenTimeOut.setTextColor(Color.parseColor(controlSettingIOS.getColorTextTitle()));
            binding.tvScreenTimeOut.setTypeface(dataSetupViewControlModel.getTypefaceText());
            binding.imgScreenTimeout.setColorFilter(Color.parseColor(controlSettingIOS.getColorDefaultIcon()));
//            binding.imgScreenTimeout.changeData(controlSettingIOS);
            if (controlSettingIOS.getIconControl() != null && !controlSettingIOS.getIconControl().equals(Constant.ICON_DEFAULT)){
                String pathIcon = Constant.PATH_ASSET_THEME.concat(dataSetupViewControlModel.getIdCategory()+"/"+dataSetupViewControlModel.getId()+"/"+controlSettingIOS.getIconControl());
                if (dataSetupViewControlModel.getId() > 10000){
                    File file = new File(context.getFilesDir(), Constant.FOLDER_THEMES_ASSETS +"/"+ dataSetupViewControlModel.getIdCategory()+"/"+dataSetupViewControlModel.getId()+"/"+controlSettingIOS.getIconControl());
                    pathIcon =  file.getAbsolutePath();
                }
                Glide.with(context).load(pathIcon).placeholder(R.drawable.ic_screen_timeout_ios).into(binding.imgScreenTimeout);
            }
        }
    }

    public void changeControlSettingIos(ControlSettingIosModel controlSettingIosModel){
        this.controlSettingIOS = controlSettingIosModel;
        initView();
    }


}

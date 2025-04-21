package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group3;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.databinding.LayoutScreenTimeoutBinding;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlSettingIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ConstraintLayoutBase;
import com.tapbi.spark.controlcenter.utils.VibratorUtils;

import java.io.File;

import timber.log.Timber;

public class ScreenTimeoutView extends ConstraintLayoutBase {

    private Context context;

    private ViewPropertyAnimator scaleY = null;
    private boolean down;
    private Handler handler;
    private boolean isLongClick;
    private ControlSettingIosModel controlSettingIosModel;
    private LayoutScreenTimeoutBinding binding;
    private OnScreenTimeoutListener onScreenTimeoutListener;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            isLongClick = true;
            longClick();
        }
    };

    public ScreenTimeoutView(Context context) {
        super(context);
        init(context);
    }

    public ScreenTimeoutView(Context context, ControlSettingIosModel controlSettingIosModel, DataSetupViewControlModel dataSetupViewControlModel) {
        super(context);
        this.controlSettingIosModel = controlSettingIosModel;
        this.dataSetupViewControlModel = dataSetupViewControlModel;
        init(context);
    }

    public ScreenTimeoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScreenTimeoutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void changeData(ControlSettingIosModel controlSettingIosModel, DataSetupViewControlModel dataSetupViewControlModel) {
        this.controlSettingIosModel = controlSettingIosModel;
        this.dataSetupViewControlModel = dataSetupViewControlModel;
        initView();
        invalidate();
    }

    private void init(Context context) {
        this.context = context;
        handler = new Handler();
        binding = LayoutScreenTimeoutBinding.inflate(LayoutInflater.from(context), this, true);
        initView();
    }

    private void initView() {
        binding.iconScreenTimeout.setImageDrawable(context.getDrawable(R.drawable.ic_screen_timeout_ios));
        if (controlSettingIosModel != null) {
            changeColorBackground(
                    controlSettingIosModel.getBackgroundDefaultColorViewParent(),
                    controlSettingIosModel.getBackgroundSelectColorViewParent(),
                    controlSettingIosModel.getCornerBackgroundViewParent()
            );

            String colorDefaultIcon = controlSettingIosModel.getColorDefaultIcon();
            String colorTextTitle = controlSettingIosModel.getColorTextTitle();

            binding.iconScreenTimeout.setColorFilter(Color.parseColor(colorDefaultIcon));
            binding.iconScreenTimeout.changeData(controlSettingIosModel);
            binding.tvName.setTextColor(Color.parseColor(colorTextTitle));
            binding.tvName.setTypeface(dataSetupViewControlModel.getTypefaceText());

            if (controlSettingIosModel.getIconControl() != null && !controlSettingIosModel.getIconControl().equals(Constant.ICON_DEFAULT)) {
                String pathIcon = Constant.PATH_ASSET_THEME.concat(dataSetupViewControlModel.getIdCategory() + "/" + dataSetupViewControlModel.getId() + "/" + controlSettingIosModel.getIconControl());
                if (dataSetupViewControlModel.getId() > 10000) {
                    File file = new File(context.getFilesDir(), Constant.FOLDER_THEMES_ASSETS + "/" + dataSetupViewControlModel.getIdCategory() + "/" + dataSetupViewControlModel.getId() + "/" + controlSettingIosModel.getIconControl());
                    pathIcon = file.getAbsolutePath();
                }
//                Glide.with(context).load(pathIcon).into(binding.iconScreenTimeout);
                loadImage(context, pathIcon, pathIcon.contains(Constant.PATH_ASSET_THEME), R.drawable.ic_darkmode_ios, binding.iconScreenTimeout);
            }
        }

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
        Timber.e("hachung onDown:");
        if (scaleY != null) {
            scaleY.cancel();
        }
        down = true;
        scaleY = animate().scaleX(0.85f).scaleY(0.85f);
        scaleY.setDuration(200).setInterpolator(new DecelerateInterpolator());
        scaleY.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (down) {
                    ViewPropertyAnimator scaleY = animate().scaleX(1f).scaleY(1f);
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
        if (this instanceof ScreenTimeoutView) {
            setAnimationListener(listener);
        }
        scaleY.start();
    }

    public void setOnScreenTimeoutListener(OnScreenTimeoutListener onScreenTimeoutListener) {
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


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        binding.tvName.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (0.184 * h));
    }

    public interface OnScreenTimeoutListener {
        void onDown();

        void onUp();

        void onClick();

        void onLongClick();
    }
}

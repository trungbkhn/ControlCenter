package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group4;

import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlBrightnessVolumeIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.callback.OnClickSettingListener;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.callback.OnLongClickSeekbarListener;
import com.tapbi.spark.controlcenter.utils.SettingUtils;

import java.io.File;

import timber.log.Timber;


public class SettingBrightnessView extends ConstraintLayout {


    private Context context;
    private CustomSeekbarVerticalView seekBarBrightnes;
    private CustomSeekbarHorizontalView seekBarBrightnesHorizontal;
    private ImageView iconBrightness;
    private ImageView iconBrightnessHorizontal;
    private ViewPropertyAnimator scale = null;
    private boolean down;
    private int brightness = 0;
    private OnClickSettingListener onClickSettingListener;
    private OnLongClickSeekbarListener onLongClickSeekbarListener;
    private int maxBrightness = 255;
    private ValueAnimator valueAnimator;
    private boolean animationRunning = false;
    private boolean isHorizontal = false;
    private ControlBrightnessVolumeIosModel controlBrightnessVolumeIosModel;
    private DataSetupViewControlModel dataSetupViewControlModel;

    private boolean isLongClick = false;
    private Runnable runnableInitProgress = new Runnable() {
        @Override
        public void run() {
            updateIconBrightness();
        }
    };

    public SettingBrightnessView(Context context) {
        super(context);
        init(context, null);
    }

    public SettingBrightnessView(Context context, ControlBrightnessVolumeIosModel controlBrightnessVolumeIosModel, DataSetupViewControlModel dataSetupViewControlModel) {
        super(context);
        this.controlBrightnessVolumeIosModel = controlBrightnessVolumeIosModel;
        this.dataSetupViewControlModel = dataSetupViewControlModel;
        init(context, null);
    }

    public SettingBrightnessView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SettingBrightnessView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.layout_setting_brightness, this, true);
        @SuppressLint("CustomViewStyleable") TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingViewVolume);
        isHorizontal = typedArray.getBoolean(R.styleable.SettingViewVolume_isHorizontal, false);
        typedArray.recycle();

        seekBarBrightnes = findViewById(R.id.seekBarBrightness);
        seekBarBrightnesHorizontal = findViewById(R.id.seekbarBrightnessHorizontal);
        iconBrightness = findViewById(R.id.iconBrightness);
        iconBrightnessHorizontal = findViewById(R.id.iconBrightnessHorizontal);

        maxBrightness = SettingUtils.getMaxBrightness(getContext());

        if (controlBrightnessVolumeIosModel != null) {
            iconBrightness.setColorFilter(Color.parseColor(controlBrightnessVolumeIosModel.getColorIcon()));
            iconBrightnessHorizontal.setColorFilter(Color.parseColor(controlBrightnessVolumeIosModel.getColorIcon()));
//            seekBarBrightnes.changeColorAndCornerProgess(controlBrightnessVolumeIosModel.getColorBackgroundSeekbarProgress(), controlBrightnessVolumeIosModel.getCornerBackgroundSeekbar(), false);
//            seekBarBrightnesHorizontal.changeColorAndCornerProgess(controlBrightnessVolumeIosModel.getColorBackgroundSeekbarProgress(), controlBrightnessVolumeIosModel.getCornerBackgroundSeekbar(), true);
            seekBarBrightnesHorizontal.changeColor(controlBrightnessVolumeIosModel.getColorBackgroundSeekbarDefault(), controlBrightnessVolumeIosModel.getColorBackgroundSeekbarProgress(), controlBrightnessVolumeIosModel.getColorThumbSeekbar(), controlBrightnessVolumeIosModel.getCornerBackgroundSeekbar());
            seekBarBrightnes.changeColor(controlBrightnessVolumeIosModel.getColorBackgroundSeekbarDefault(), controlBrightnessVolumeIosModel.getColorBackgroundSeekbarProgress(), controlBrightnessVolumeIosModel.getColorThumbSeekbar(), controlBrightnessVolumeIosModel.getCornerBackgroundSeekbar());
            if (controlBrightnessVolumeIosModel.getIconControl() != null && !controlBrightnessVolumeIosModel.getIconControl().equals(Constant.ICON_DEFAULT)) {
                String pathIcon = Constant.PATH_ASSET_THEME.concat(dataSetupViewControlModel.getIdCategory() + "/" + dataSetupViewControlModel.getId() + "/" + controlBrightnessVolumeIosModel.getIconControl());
                if (dataSetupViewControlModel.getId() > 10000) {
                    File file = new File(context.getFilesDir(), Constant.FOLDER_THEMES_ASSETS + "/" + dataSetupViewControlModel.getIdCategory() + "/" + dataSetupViewControlModel.getId() + "/" + controlBrightnessVolumeIosModel.getIconControl());
                    pathIcon = file.getAbsolutePath();
                }
                Glide.with(context).load(pathIcon).placeholder(R.drawable.ic_brightness_black).into(iconBrightnessHorizontal);
            }
        }
        changeIsHorizontal(isHorizontal);
//        seekBarBrightnesHorizontal.setMax(maxBrightness);
//        seekBarBrightnes.setMax(maxBrightness);


//        seekBarBrightnes.setOnSeekBarChangeListener(new VerticalSeekBar.OnVerticalSeekBarListener() {
//            @Override
//            public void onStartTrackingTouch(VerticalSeekBar verticalSeekBar) {
//                onStartTouch();
//            }
//
//            @Override
//            public void onProgressChanged(VerticalSeekBar verticalSeekBar, int i, boolean z) {
//                onProgressTouch(i, z);
//            }
//
//            @Override
//            public void onStopTrackingTouch(VerticalSeekBar verticalSeekBar) {
//                onStopTouch();
//            }
//
//            @Override
//            public void onLongPress(VerticalSeekBar verticalSeekBar) {
//                onLongTouch();
//            }
//        });
        seekBarBrightnesHorizontal.setOnCustomSeekbarHorizontalListener(new CustomSeekbarHorizontalView.OnCustomSeekbarHorizontalListener() {
            @Override
            public void onStartTrackingTouch(CustomSeekbarHorizontalView horizontalSeekBar) {
                onStartTouch();
            }

            @Override
            public void onProgressChanged(CustomSeekbarHorizontalView horizontalSeekBar, float i) {
                onProgressTouch(i, true);
            }

            @Override
            public void onStopTrackingTouch(CustomSeekbarHorizontalView horizontalSeekBar) {
                onStopTouch();
            }

            @Override
            public void onLongPress(CustomSeekbarHorizontalView horizontalSeekBar) {
                onLongTouch();
            }
        });

        seekBarBrightnes.setCustomSeekbarVerticalListener(new CustomSeekbarVerticalView.OnCustomSeekbarVerticalListener() {
            @Override
            public void onStartTrackingTouch(CustomSeekbarVerticalView horizontalSeekBar) {
                onStartTouch();
            }

            @Override
            public void onProgressChanged(CustomSeekbarVerticalView horizontalSeekBar, float i) {
                if (!isLongClick) {
                    onProgressTouch(i, true);
                }

            }

            @Override
            public void onStopTrackingTouch(CustomSeekbarVerticalView horizontalSeekBar) {
                onStopTouch();
            }

            @Override
            public void onLongPress(CustomSeekbarVerticalView horizontalSeekBar) {
                onLongTouch();
            }
        });

    }

    public void changeIsHorizontal(boolean isHorizontal) {
        this.isHorizontal = isHorizontal;
        if (isHorizontal) {
            seekBarBrightnes.setVisibility(GONE);
            seekBarBrightnesHorizontal.setVisibility(VISIBLE);
            iconBrightness.setVisibility(GONE);
            iconBrightnessHorizontal.setVisibility(VISIBLE);
            seekBarBrightnesHorizontal.post(runnableInitProgress);
        } else {
            seekBarBrightnes.setVisibility(VISIBLE);
            seekBarBrightnesHorizontal.setVisibility(GONE);
            iconBrightness.setVisibility(VISIBLE);
            iconBrightnessHorizontal.setVisibility(GONE);
            seekBarBrightnes.post(runnableInitProgress);
        }
    }

    private void onLongTouch() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.System.canWrite(context)) {
            if (onLongClickSeekbarListener != null) {
                isLongClick = true;
                onLongClickSeekbarListener.onLongClick();
            }
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (onLongClickSeekbarListener != null) {
                isLongClick = true;
                onLongClickSeekbarListener.onLongClick();
            }
        }
    }

    private void onStopTouch() {
        if (onLongClickSeekbarListener != null) {
            onLongClickSeekbarListener.onUp();
        }
        if (!isLongClick) {
            setBrightnessEnd();
        }
        isLongClick = false;
//        onUpAniamtion();
    }

    private void onProgressTouch(float i, boolean z) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.System.canWrite(context)) {
            if (z) {
                brightness = (int) (i * maxBrightness);
                changeBrightness();
            }
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (z) {
                brightness = (int) (i * maxBrightness);
                changeBrightness();
            }
        }
    }

    private void onStartTouch() {
        getParent().requestDisallowInterceptTouchEvent(true);
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        if (onLongClickSeekbarListener != null) {
            onLongClickSeekbarListener.onDown();
        }
//        onDownAnimation();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(context)) {
                if (onClickSettingListener != null) {
                    onClickSettingListener.onClick();
                }
                SettingUtils.intentActivityRequestPermission(context, new String[]{Manifest.permission.WRITE_SETTINGS});
                seekBarBrightnes.changeIsPermission(false);
                seekBarBrightnesHorizontal.changeIsPermission(false);
            } else {
                seekBarBrightnes.changeIsPermission(true);
                seekBarBrightnesHorizontal.changeIsPermission(true);
            }
        } else {
            seekBarBrightnes.changeIsPermission(true);
            seekBarBrightnesHorizontal.changeIsPermission(true);
        }
    }

    /**
     * Some device (ex Vivo v2111) can't change setValueBrightness too quickly
     * So animate change slow to fix this
     */
    private void setBrightnessEnd() {
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        valueAnimator = ValueAnimator.ofInt(brightness, brightness + 2);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.setRepeatCount(1);
        valueAnimator.setDuration(300);
        valueAnimator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            SettingUtils.setValueBrightness(context, value);
        });

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {
                animationRunning = true;
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                animationRunning = false;
                updateIconBrightness();
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        });
        valueAnimator.start();
    }

    private void changeBrightness() {
        SettingUtils.setValueBrightness(context, brightness);
        updateIconBrightness();
    }

    public void setOnClickSettingListener(OnClickSettingListener onClickSettingListener) {
        this.onClickSettingListener = onClickSettingListener;
    }


    public void updateIconBrightness() {
        if (animationRunning) {
            return;
        }
        brightness = SettingUtils.getValueBrightness(context);
        loadUiBrightness(brightness);
    }
    public void loadUiBrightness(float brightness){
        float f = (float) brightness / maxBrightness;
        ImageView imageView;
        if (iconBrightness.getVisibility() == VISIBLE) {
            imageView = iconBrightness;
        } else {
            imageView = iconBrightnessHorizontal;
        }
        if (f < 0.3f) {
            imageView.setImageResource(R.drawable.ic_brightness_black1);
        } else if (0.3f < f && f < 0.6f) {
            imageView.setImageResource(R.drawable.ic_brightness_black2);
        } else if (0.6f < f) {
            imageView.setImageResource(R.drawable.ic_brightness_black);
        }
        seekBarBrightnes.setCurrentProgress(f);
        seekBarBrightnesHorizontal.setCurrentProgress(f);
    }
    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (getVisibility() == VISIBLE && visibility == VISIBLE) {
            post(this::updateIconBrightness);
        }
    }


    private void onDownAnimation() {
        if (scale != null) {
            scale.cancel();
        }
        down = true;
        scale = animate().scaleX(0.85f).scaleY(0.85f);
        scale.setDuration(200).setInterpolator(new DecelerateInterpolator());
        scale.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (down) {
                    ViewPropertyAnimator scaleY = animate().scaleX(1.0f).scaleY(1.0f);
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
        scale.start();
    }

    private void onUpAniamtion() {
        down = false;
        scale = animate().scaleX(1.0f).scaleY(1.0f);
        scale.setDuration(200).setInterpolator(new AccelerateInterpolator());
        scale.start();
    }

    public void animationShow() {
        if (scale != null) {
            scale.cancel();
        }
        setScaleX(0.8f);
        setScaleY(0.8f);
        setAlpha(0f);
        scale = animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(300).setInterpolator(new DecelerateInterpolator());
        scale.start();
    }

    public void animationHide() {
        if (scale != null) {
            scale.cancel();
        }
        setScaleX(1f);
        setScaleY(1f);
        setAlpha(1f);
        scale = animate().scaleX(0.8f).scaleY(0.8f).alpha(0f).setDuration(300).setInterpolator(new AccelerateInterpolator());
        scale.start();
    }

    public void setOnLongClickSeekbarListener(OnLongClickSeekbarListener onLongClickSeekbarListener) {
        this.onLongClickSeekbarListener = onLongClickSeekbarListener;
    }


}

package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group4;

import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tapbi.spark.controlcenter.databinding.LayoutControlBrightnessBinding;
import com.tapbi.spark.controlcenter.feature.controlios14.manager.AudioManagerUtils;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlBrightnessVolumeIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ConstraintLayoutBase;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.callback.OnClickSettingListener;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.callback.OnLongClickSeekbarListener;
import com.tapbi.spark.controlcenter.utils.SettingUtils;

import timber.log.Timber;

public class BrightnessTextView extends ConstraintLayoutBase {

    private LayoutControlBrightnessBinding binding;
    private ControlBrightnessVolumeIosModel controlBrightnessVolumeIosModel;
    private Context context;
    private ViewPropertyAnimator scale = null;
    private boolean down;
    private int brightness = 0;
    private OnClickSettingListener onClickSettingListener;
    private OnLongClickSeekbarListener onLongClickSeekbarListener;
    private int maxBrightness = 255;
    private ValueAnimator valueAnimator;
    private boolean animationRunning = false;


    public BrightnessTextView(Context context) {
        super(context);
        init(context);
    }

    public BrightnessTextView(Context context, ControlBrightnessVolumeIosModel controlBrightnessVolumeIosModel, DataSetupViewControlModel dataSetupViewControlModel) {
        super(context);
        this.controlBrightnessVolumeIosModel = controlBrightnessVolumeIosModel;
        this.dataSetupViewControlModel = dataSetupViewControlModel;
        init(context);
    }

    public BrightnessTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BrightnessTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        binding = LayoutControlBrightnessBinding.inflate(LayoutInflater.from(context), this, true);

        changeColorBackground(controlBrightnessVolumeIosModel.getBackgroundDefaultColorViewParent(), controlBrightnessVolumeIosModel.getBackgroundSelectColorViewParent(), controlBrightnessVolumeIosModel.getCornerBackgroundViewParent());
        binding.tvDislay.setTextColor(Color.parseColor(controlBrightnessVolumeIosModel.getColorText()));
        binding.tvDislay.setTypeface(dataSetupViewControlModel.getTypefaceText());
        binding.imgBrightness.setColorFilter(Color.parseColor(controlBrightnessVolumeIosModel.getColorIcon()));
//        binding.sbBrightness.setThumb(context.getDrawable(R.drawable.custom_thumb_seekbar), controlBrightnessVolumeIosModel.getColorThumbSeekbar());
//        binding.sbBrightness.changeColorAndCornerProgess(controlBrightnessVolumeIosModel.getColorBackgroundSeekbarProgress(), controlBrightnessVolumeIosModel.getCornerBackgroundSeekbar(), true);
        binding.sbBrightness.changeColor(controlBrightnessVolumeIosModel.getColorBackgroundSeekbarDefault(), controlBrightnessVolumeIosModel.getColorBackgroundSeekbarProgress(), controlBrightnessVolumeIosModel.getColorThumbSeekbar(), controlBrightnessVolumeIosModel.getCornerBackgroundSeekbar());
        maxBrightness = SettingUtils.getMaxBrightness(getContext());
//        binding.sbBrightness.setMax(maxBrightness);
        binding.sbBrightness.post(new Runnable() {
            @Override
            public void run() {
                updateIconBrightness();
            }
        });

        binding.sbBrightness.setOnCustomSeekbarHorizontalListener(new CustomSeekbarHorizontalView.OnCustomSeekbarHorizontalListener() {
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

    }

    private void onLongTouch() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.System.canWrite(context)) {
            if (onLongClickSeekbarListener != null) {
                onLongClickSeekbarListener.onLongClick();
            }
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (onLongClickSeekbarListener != null) {
                onLongClickSeekbarListener.onLongClick();
            }
        }
    }

    private void onStopTouch() {
        if (onLongClickSeekbarListener != null) {
            onLongClickSeekbarListener.onUp();
        }
        setBrightnessEnd();
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
                binding.sbBrightness.changeIsPermission(false);
                SettingUtils.intentActivityRequestPermission(context, new String[]{Manifest.permission.WRITE_SETTINGS});
            } else {
                binding.sbBrightness.changeIsPermission(true);
            }
        } else {
            binding.sbBrightness.changeIsPermission(true);
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
//                updateIconBrightness();
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
        Timber.e("Duongcv " + brightness);
        SettingUtils.setValueBrightness(context, brightness);
//        updateIconBrightness();
    }

    public void setOnClickSettingListener(OnClickSettingListener onClickSettingListener) {
        this.onClickSettingListener = onClickSettingListener;
    }

    public void updateIconBrightness() {
        if (animationRunning) {
            return;
        }
        brightness = SettingUtils.getValueBrightness(context);
        float f = (float) brightness / maxBrightness;
        Timber.e("hachung f:"+f);
        binding.sbBrightness.setCurrentProgress(f);
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

package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group4;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.databinding.LayoutControlVolumeBinding;
import com.tapbi.spark.controlcenter.feature.controlios14.manager.AudioManagerUtils;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlBrightnessVolumeIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.CreateItemViewControlCenterIOS;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.BaseView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ConstraintLayoutBase;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.horizontalseekbar.HorizontalSeekBar;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.horizontalseekbar.HorizontalThumbSeekbar;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.verticalseekbar.VerticalSeekBar;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.callback.OnLongClickSeekbarListener;

public class SettingVolumeTextView extends ConstraintLayoutBase {

    private LayoutControlVolumeBinding binding;
    private ControlBrightnessVolumeIosModel controlBrightnessVolumeIosModel;
    private Context context;


    private ViewPropertyAnimator scale = null;
    private boolean down;
    private int maxVolume;
    private boolean isLongClick = true;
    private boolean progressChanging;

    private boolean isHorizontal = false;
    private OnLongClickSeekbarListener onLongClickSeekbarListener;
    private float volume = 0f;


    public SettingVolumeTextView(Context context) {
        super(context);
        init(context);
    }
    public SettingVolumeTextView(Context context, ControlBrightnessVolumeIosModel controlBrightnessVolumeIosModel, DataSetupViewControlModel dataSetupViewControlModel) {
        super(context);
        this.controlBrightnessVolumeIosModel =controlBrightnessVolumeIosModel;
        this.dataSetupViewControlModel = dataSetupViewControlModel;
        init(context);
    }

    public SettingVolumeTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SettingVolumeTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        this.context = context;
        binding = LayoutControlVolumeBinding.inflate(LayoutInflater.from(context), this, true);

        changeColorBackground(controlBrightnessVolumeIosModel.getBackgroundDefaultColorViewParent(), controlBrightnessVolumeIosModel.getBackgroundSelectColorViewParent(), controlBrightnessVolumeIosModel.getCornerBackgroundViewParent());
        binding.tvDislay.setTextColor(Color.parseColor(controlBrightnessVolumeIosModel.getColorText()));
        binding.tvDislay.setTypeface(dataSetupViewControlModel.getTypefaceText());
        binding.imgSound.setColorFilter(Color.parseColor(controlBrightnessVolumeIosModel.getColorIcon()));
        binding.sbSound.changeColor(controlBrightnessVolumeIosModel.getColorBackgroundSeekbarDefault(), controlBrightnessVolumeIosModel.getColorBackgroundSeekbarProgress(), controlBrightnessVolumeIosModel.getColorThumbSeekbar(), controlBrightnessVolumeIosModel.getCornerBackgroundSeekbar());
        maxVolume = AudioManagerUtils.getInstance(context).getMaxVolume();
        binding.sbSound.post(new Runnable() {
            @Override
            public void run() {
                updateVolume(AudioManagerUtils.getInstance(context).getVolume());
            }
        });


        binding.sbSound.setOnCustomSeekbarHorizontalListener(new CustomSeekbarHorizontalView.OnCustomSeekbarHorizontalListener() {
            @Override
            public void onStartTrackingTouch(CustomSeekbarHorizontalView horizontalSeekBar) {
                onStartTouch();
            }

            @Override
            public void onProgressChanged(CustomSeekbarHorizontalView horizontalSeekBar, float i) {
                volume = i;
                onProgressTouch(i,true);
            }

            @Override
            public void onStopTrackingTouch(CustomSeekbarHorizontalView horizontalSeekBar) {
                onStopTouch();
            }

            @Override
            public void onLongPress(CustomSeekbarHorizontalView horizontalSeekBar) {
                onLongPressTouch();
            }
        });

    }

    public void setLongClick(boolean longClick) {
        isLongClick = longClick;
    }

    public void showVolumeExpandView() {
        setScaleX(1f);
        setScaleY(1f);
        setAlpha(1f);
        setVisibility(View.VISIBLE);
    }

    private void onLongPressTouch() {
        if (onLongClickSeekbarListener != null) {
            isLongClick = true;
            onLongClickSeekbarListener.onLongClick();
        }
    }

    private void onStopTouch() {
        progressChanging = false;
        if (onLongClickSeekbarListener != null) {
            onLongClickSeekbarListener.onUp();
        }
        if (!CreateItemViewControlCenterIOS.isTouchDarkmore) {
            int volume = Math.round((this.volume * maxVolume));
            AudioManagerUtils.getInstance(context).changeVolumeInForeground(getContext(), AudioManager.STREAM_MUSIC, volume);
        }
//        onUpAnimation();
    }

    private void onProgressTouch(float i, boolean z) {
        if (z) {
            int volume = Math.round((float) (i * maxVolume));
            AudioManagerUtils.getInstance(context).setVolume(volume);
        }
    }

    private void onStartTouch() {
        getParent().requestDisallowInterceptTouchEvent(true);
        progressChanging = true;
        if (onLongClickSeekbarListener != null) {
            onLongClickSeekbarListener.onDown();
        }
        binding.sbSound.changeIsPermission(true);
//        onDownAnimation();
    }


    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (getVisibility() == VISIBLE && visibility == VISIBLE) {
            post(() -> updateVolume(AudioManagerUtils.getInstance(context).getVolume()));
        }
    }

    public void updateVolume(int volumeNew) {
        if (progressChanging) {
            return;
        }
        float progress = ((float)volumeNew) / ((float) maxVolume);
        binding.sbSound.setCurrentProgress(progress);
    }

    private void updateStateIcon(ImageView imageView, int progress) {
        float f = (float) progress / 255f;
        if (f == 0) {
            imageView.setImageResource(R.drawable.ic_volume_black_mute);
        } else if (f < 0.33f) {
            imageView.setImageResource(R.drawable.ic_volume_black1);
        } else if (f < 0.66f) {
            imageView.setImageResource(R.drawable.ic_volume_black2);
        } else {
            imageView.setImageResource(R.drawable.ic_volume_black);
        }
    }


    public void setOnLongClickSeekbarListener(OnLongClickSeekbarListener onLongClickSeekbarListener) {
        this.onLongClickSeekbarListener = onLongClickSeekbarListener;
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
        scale.start();
    }

    private void onUpAnimation() {
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
        scale = animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(200).withEndAction(() -> {
            if (getAlpha() != 1f) {
                setAlpha(1f);
            }
        }).setInterpolator(new DecelerateInterpolator());
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

    public interface updateUI {
        void update();
    }



}

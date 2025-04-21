package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group4;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.util.Log;
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
import com.tapbi.spark.controlcenter.feature.controlios14.manager.AudioManagerUtils;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlBrightnessVolumeIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.callback.OnLongClickSeekbarListener;

import java.io.File;

import timber.log.Timber;


public class SettingVolumeView extends ConstraintLayout {

    private Context context;

    private CustomSeekbarVerticalView seekBarVolume;
    private CustomSeekbarHorizontalView seekBarVolumeHorizontal;
    private ImageView iconVolume;
    private ImageView iconVolumeHorizontal;

    private ViewPropertyAnimator scale = null;
    private boolean down;
    private int maxVolume;
    private boolean isLongClick = false;
    private boolean progressChanging;

    private boolean isHorizontal = false;
    private OnLongClickSeekbarListener onLongClickSeekbarListener;
    private ControlBrightnessVolumeIosModel controlBrightnessVolumeIosModel;
    private DataSetupViewControlModel dataSetupViewControlModel;
    private float currentVolume = 0;
    private Runnable runnableInitProgress = new Runnable() {
        @Override
        public void run() {
            updateVolume(AudioManagerUtils.getInstance(context).getVolume());
        }
    };


    public SettingVolumeView(Context context) {
        super(context);
        init(context, null);
    }


    public SettingVolumeView(Context context, ControlBrightnessVolumeIosModel controlBrightnessVolumeIosModel, DataSetupViewControlModel dataSetupViewControlModel) {
        super(context);
        this.controlBrightnessVolumeIosModel = controlBrightnessVolumeIosModel;
        this.dataSetupViewControlModel = dataSetupViewControlModel;
        init(context, null);
    }

    public SettingVolumeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SettingVolumeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void setLongClick(boolean longClick) {
//        isLongClick = longClick;
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
        if (!isLongClick) {
            int volume = Math.round((float) (currentVolume * maxVolume));
            AudioManagerUtils.getInstance(context).changeVolumeInForeground(getContext(), AudioManager.STREAM_MUSIC, volume);
        }
        isLongClick = false;
//        onUpAnimation();
    }

    private void onProgressTouch(float i) {
        Timber.e("NVQ updateStateIcon " + i);
        int volume = (int) (i * maxVolume);
        AudioManagerUtils.getInstance(context).setVolume(volume);
        updateImageIcon(i);
    }

    private void onStartTouch() {
        getParent().requestDisallowInterceptTouchEvent(true);
        progressChanging = true;
        if (onLongClickSeekbarListener != null) {
            onLongClickSeekbarListener.onDown();
        }
        seekBarVolume.changeIsPermission(true);
        seekBarVolumeHorizontal.changeIsPermission(true);
//        onDownAnimation();
    }

    public void changeIsHorizontal(boolean isHorizontal) {
        this.isHorizontal = isHorizontal;
        if (isHorizontal) {
            seekBarVolume.setVisibility(View.GONE);
            iconVolume.setVisibility(GONE);
            iconVolumeHorizontal.setVisibility(VISIBLE);
            seekBarVolumeHorizontal.setVisibility(View.VISIBLE);
            seekBarVolumeHorizontal.post(runnableInitProgress);
        } else {
            seekBarVolume.setVisibility(View.VISIBLE);
            seekBarVolumeHorizontal.setVisibility(View.GONE);
            iconVolume.setVisibility(VISIBLE);
            iconVolumeHorizontal.setVisibility(GONE);
            seekBarVolume.post(runnableInitProgress);
        }
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.layout_setting_volume, this, true);
        @SuppressLint("CustomViewStyleable") TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingViewVolume);
        isHorizontal = typedArray.getBoolean(R.styleable.SettingViewVolume_isHorizontal, false);
        typedArray.recycle();
        seekBarVolume = findViewById(R.id.seekBarVolume);
        seekBarVolumeHorizontal = findViewById(R.id.seekBarVolumeHorizontal);
        iconVolume = findViewById(R.id.iconVolume);
        iconVolumeHorizontal = findViewById(R.id.iconVolumeHorizontal);
        changeIsHorizontal(isHorizontal);

        if (controlBrightnessVolumeIosModel != null) {
            iconVolume.setColorFilter(Color.parseColor(controlBrightnessVolumeIosModel.getColorIcon()));
            iconVolumeHorizontal.setColorFilter(Color.parseColor(controlBrightnessVolumeIosModel.getColorIcon()));
//            seekBarVolume.changeColorAndCornerProgess(controlBrightnessVolumeIosModel.getColorBackgroundSeekbarProgress(), controlBrightnessVolumeIosModel.getCornerBackgroundSeekbar(), false);
//            seekBarVolumeHorizontal.changeColorAndCornerProgess(controlBrightnessVolumeIosModel.getColorBackgroundSeekbarProgress(), controlBrightnessVolumeIosModel.getCornerBackgroundSeekbar(), true);
            seekBarVolume.changeColor(controlBrightnessVolumeIosModel.getColorBackgroundSeekbarDefault(), controlBrightnessVolumeIosModel.getColorBackgroundSeekbarProgress(), controlBrightnessVolumeIosModel.getColorThumbSeekbar(), controlBrightnessVolumeIosModel.getCornerBackgroundSeekbar());
            seekBarVolumeHorizontal.changeColor(controlBrightnessVolumeIosModel.getColorBackgroundSeekbarDefault(), controlBrightnessVolumeIosModel.getColorBackgroundSeekbarProgress(), controlBrightnessVolumeIosModel.getColorThumbSeekbar(), controlBrightnessVolumeIosModel.getCornerBackgroundSeekbar());
            if (controlBrightnessVolumeIosModel.getIconControl() != null && !controlBrightnessVolumeIosModel.getIconControl().equals(Constant.ICON_DEFAULT)) {
                String pathIcon = Constant.PATH_ASSET_THEME.concat(dataSetupViewControlModel.getIdCategory() + "/" + dataSetupViewControlModel.getId() + "/" + controlBrightnessVolumeIosModel.getIconControl());
                if (dataSetupViewControlModel.getId() > 10000) {
                    File file = new File(context.getFilesDir(), Constant.FOLDER_THEMES_ASSETS + "/" + dataSetupViewControlModel.getIdCategory() + "/" + dataSetupViewControlModel.getId() + "/" + controlBrightnessVolumeIosModel.getIconControl());
                    pathIcon = file.getAbsolutePath();
                }
                Glide.with(context).load(pathIcon).placeholder(R.drawable.ic_volume_black).into(iconVolumeHorizontal);
            }

        }
        maxVolume = AudioManagerUtils.getInstance(context).getMaxVolume();


        seekBarVolume.setCustomSeekbarVerticalListener(new CustomSeekbarVerticalView.OnCustomSeekbarVerticalListener() {
            @Override
            public void onStartTrackingTouch(CustomSeekbarVerticalView horizontalSeekBar) {
                onStartTouch();
            }

            @Override
            public void onProgressChanged(CustomSeekbarVerticalView horizontalSeekBar, float i) {
                if (!isLongClick) {
                    currentVolume = i;
                    onProgressTouch(i);
                }

            }

            @Override
            public void onStopTrackingTouch(CustomSeekbarVerticalView horizontalSeekBar) {
                onStopTouch();
            }

            @Override
            public void onLongPress(CustomSeekbarVerticalView horizontalSeekBar) {
                onLongPressTouch();
            }
        });

        seekBarVolumeHorizontal.setOnCustomSeekbarHorizontalListener(new CustomSeekbarHorizontalView.OnCustomSeekbarHorizontalListener() {
            @Override
            public void onStartTrackingTouch(CustomSeekbarHorizontalView horizontalSeekBar) {
                onStartTouch();
            }

            @Override
            public void onProgressChanged(CustomSeekbarHorizontalView horizontalSeekBar, float i) {
                currentVolume = i;
                onProgressTouch(i);
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

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (getVisibility() == VISIBLE && visibility == VISIBLE) {
            post(() -> updateVolume(AudioManagerUtils.getInstance(context).getVolume()));
        }
    }

    public void updateVolume(int volumeNew) {
        Timber.e("NVQ updateStateIcon " + volumeNew);
        if (progressChanging) {
            return;
        }
        float progress = (float) volumeNew / (float) maxVolume;
        seekBarVolume.setCurrentProgress(progress);
        seekBarVolumeHorizontal.setCurrentProgress(progress);
        updateImageIcon(progress);
    }

    private void updateStateIcon(ImageView imageView, float progress) {
        Timber.e("NVQ updateStateIcon " + progress);
//        float f = (float) progress / 255f;
        if (progress == 0) {
            imageView.setImageResource(R.drawable.ic_volume_black_mute);
        } else if (progress < 0.33f) {
            imageView.setImageResource(R.drawable.ic_volume_black1);
        } else if (progress < 0.66f) {
            imageView.setImageResource(R.drawable.ic_volume_black2);
        } else {
            imageView.setImageResource(R.drawable.ic_volume_black);
        }
    }

    private void updateImageIcon(float progress) {
        if (iconVolume.getVisibility() == VISIBLE) {
            updateStateIcon(iconVolume, progress);
        } else {
            updateStateIcon(iconVolumeHorizontal, progress);
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

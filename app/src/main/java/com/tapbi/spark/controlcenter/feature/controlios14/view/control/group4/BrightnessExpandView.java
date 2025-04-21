package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group4;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.verticalseekbar.VerticalSeekBar;
import com.tapbi.spark.controlcenter.utils.DensityUtils;
import com.tapbi.spark.controlcenter.utils.SettingUtils;




public class BrightnessExpandView extends ConstraintLayout {

    private Context context;

    private CustomSeekbarVerticalView seekBar;
    private ImageView autoBrightnessAction;
    private ImageView iconBrightnessExpand;

    private int brightness;
    private int maxBrightness = 255;
    private ValueAnimator valueAnimator;
    private boolean animationRunning = false;
    private Handler handler;

    public BrightnessExpandView(Context context) {
        super(context);
        init(context);
    }

    public BrightnessExpandView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BrightnessExpandView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context ctx) {
        this.context = ctx;
        handler = new Handler();
        if (DensityUtils.getOrientationWindowManager(getContext()) == Configuration.ORIENTATION_PORTRAIT){
            LayoutInflater.from(context).inflate(R.layout.layout_brightness_expanded, this, true);
        }else {
            LayoutInflater.from(context).inflate(R.layout.layout_brightness_expanded_land, this, true);
        }



        seekBar = findViewById(R.id.seekbar);
        autoBrightnessAction = findViewById(R.id.autoBrightnessAction);
        iconBrightnessExpand = findViewById(R.id.iconBrightnessExpand);

        maxBrightness = SettingUtils.getMaxBrightness(getContext());
//        Timber.e("hachung getMaxBrightness:"+maxBrightness);
//        seekBar.setMax(maxBrightness);
        updateBrightness();
        seekBar.changeIsPermission(true);
        seekBar.setCustomSeekbarVerticalListener(new CustomSeekbarVerticalView.OnCustomSeekbarVerticalListener() {
            @Override
            public void onStartTrackingTouch(CustomSeekbarVerticalView horizontalSeekBar) {
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
            }

            @Override
            public void onProgressChanged(CustomSeekbarVerticalView horizontalSeekBar, float i) {
                brightness = (int) (i*maxBrightness);
                SettingUtils.setValueBrightness(context, brightness);
                updateBrightness();
            }

            @Override
            public void onStopTrackingTouch(CustomSeekbarVerticalView horizontalSeekBar) {
                setBrightnessEnd();
            }

            @Override
            public void onLongPress(CustomSeekbarVerticalView horizontalSeekBar) {

            }
        });


        autoBrightnessAction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int mode;
                try {
                    mode = SettingUtils.getModeBrightness(context);
                    if (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                        SettingUtils.setModeBrightness(context, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                        seekBar.setEnabled(true);


                    } else if (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL) {
                        SettingUtils.setModeBrightness(context, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
                        seekBar.setEnabled(false);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                updateBrightness();
                            }
                        }, 1500);
                    }
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void changeColor(String colorBackground, String colorProgress, String colorThumb, float cornerProgress){
        seekBar.changeColor(colorBackground, colorProgress, colorThumb, cornerProgress);
    }

    public void updateBrightness() {
        brightness  = SettingUtils.getValueBrightness(context);
        Log.d("duongcv", "updateBrightness: " + brightness +"/"+maxBrightness);
        seekBar.setCurrentProgress(brightness/(float)maxBrightness);
        seekBar.setEnabled(true);

        float f = (float) brightness / maxBrightness;

        if (f < 0.3f) {
            iconBrightnessExpand.setImageResource(R.drawable.ic_brightness_black1);
        } else if (0.3f < f && f < 0.6f) {
            iconBrightnessExpand.setImageResource(R.drawable.ic_brightness_black2);
        } else if (0.6f < f) {
            iconBrightnessExpand.setImageResource(R.drawable.ic_brightness_black);
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
                updateBrightness();
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
}

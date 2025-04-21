package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group4;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.feature.controlios14.manager.AudioManagerUtils;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.verticalseekbar.VerticalSeekBar;
import com.tapbi.spark.controlcenter.utils.DensityUtils;


public class VolumeExpandView extends ConstraintLayout {
    private Context context;

    private CustomSeekbarVerticalView seekbarSystem;
    private CustomSeekbarVerticalView seekbarAudio;
    private CustomSeekbarVerticalView seekbarRingtone;

    private ImageView iconVolumeSystem;
    private ImageView iconAudio;
    private ImageView iconRingtone;
    private TextView tvRingtone, tvMusic, tvAlarm;

    private int maxVolume;
    private int maxVolumeRingtone;
    private int minVolumeSystem;
    private int maxVolumeSystem;
    private Typeface typeface;

    public VolumeExpandView(Context context) {
        super(context);
        init(context);
    }

    public VolumeExpandView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VolumeExpandView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context ctx) {
        this.context = ctx;
        if (DensityUtils.getOrientationWindowManager(getContext()) == Configuration.ORIENTATION_PORTRAIT) {
            LayoutInflater.from(context).inflate(R.layout.layout_volume_expanded, this, true);
        } else {
            LayoutInflater.from(context).inflate(R.layout.layout_volume_expanded_land, this, true);
        }

        maxVolume = AudioManagerUtils.getInstance(context).getMaxVolume();
        maxVolumeRingtone = AudioManagerUtils.getInstance(context).getMaxVolumeRingtone();
        maxVolumeSystem = AudioManagerUtils.getInstance(context).getMaxVolumeAlarm();
        minVolumeSystem = AudioManagerUtils.getInstance(context).getMinVolumeAlarm();

        seekbarSystem = findViewById(R.id.seekbarSystem);
        seekbarSystem.changeIsPermission(true);
        seekbarSystem.setMinProgress(minVolumeSystem/ (float)maxVolumeSystem);
        seekbarAudio = findViewById(R.id.seekbarAudio);
        seekbarAudio.changeIsPermission(true);
        seekbarRingtone = findViewById(R.id.seekbarRingtone);
        seekbarRingtone.changeIsPermission(true);
        tvRingtone = findViewById(R.id.tvRingtone);
        tvAlarm = findViewById(R.id.tvAlarm);
        tvMusic = findViewById(R.id.tvMusic);

        iconVolumeSystem = findViewById(R.id.iconVolumeSystem);
        iconAudio = findViewById(R.id.iconAudio);
        iconRingtone = findViewById(R.id.iconRingtone);
        setFont();
        seekbarAudio.setCustomSeekbarVerticalListener(new CustomSeekbarVerticalView.OnCustomSeekbarVerticalListener() {
            @Override
            public void onStartTrackingTouch(CustomSeekbarVerticalView horizontalSeekBar) {

            }

            @Override
            public void onProgressChanged(CustomSeekbarVerticalView horizontalSeekBar, float i) {
                int volumeAudio = Math.round((float) (i * maxVolume));
                Log.d("duongcvc", "onProgressChanged: "+ volumeAudio);
                AudioManagerUtils.getInstance(context).setVolume(volumeAudio);
                updateIconAudio(i);
            }

            @Override
            public void onStopTrackingTouch(CustomSeekbarVerticalView horizontalSeekBar) {
                int volume = Math.round((seekbarAudio.getCurrentProgress() * maxVolume));
                Log.d("duongcvc", "onStopTrackingTouch: "+ volume);
                AudioManagerUtils.getInstance(context).changeVolumeInForeground(getContext(), AudioManager.STREAM_MUSIC, volume);
            }

            @Override
            public void onLongPress(CustomSeekbarVerticalView horizontalSeekBar) {

            }
        });

        seekbarSystem.setCustomSeekbarVerticalListener(new CustomSeekbarVerticalView.OnCustomSeekbarVerticalListener() {
            @Override
            public void onStartTrackingTouch(CustomSeekbarVerticalView horizontalSeekBar) {

            }

            @Override
            public void onProgressChanged(CustomSeekbarVerticalView horizontalSeekBar, float i) {
                    int volumeSystem = Math.round((float) (i * maxVolumeSystem));
                    AudioManagerUtils.getInstance(context).setVolumeAlarm(volumeSystem);
                    updateIconSystem(i);
            }

            @Override
            public void onStopTrackingTouch(CustomSeekbarVerticalView horizontalSeekBar) {
//                int volume = Math.round((float) (horizontalSeekBar.getCurrentProgress() * maxVolumeSystem));
//                AudioManagerUtils.getInstance(context).changeVolumeInForeground(getContext(), AudioManager.STREAM_ALARM, volume);
            }

            @Override
            public void onLongPress(CustomSeekbarVerticalView horizontalSeekBar) {

            }
        });

        seekbarRingtone.setCustomSeekbarVerticalListener(new CustomSeekbarVerticalView.OnCustomSeekbarVerticalListener() {
            @Override
            public void onStartTrackingTouch(CustomSeekbarVerticalView horizontalSeekBar) {

            }

            @Override
            public void onProgressChanged(CustomSeekbarVerticalView horizontalSeekBar, float i) {
                int volumeRingtone = Math.round((float) (i * maxVolumeRingtone));
                AudioManagerUtils.getInstance(context).setVolumeRingtone(volumeRingtone);
                updateIconRingtone(i);
            }

            @Override
            public void onStopTrackingTouch(CustomSeekbarVerticalView horizontalSeekBar) {

            }

            @Override
            public void onLongPress(CustomSeekbarVerticalView horizontalSeekBar) {

            }
        });

    }

    public void changeColor(String colorBackground, String colorProgress, String colorThumb, float cornerProgress){
        seekbarSystem.changeColor(colorBackground, colorProgress, colorThumb, cornerProgress);
        seekbarRingtone.changeColor(colorBackground, colorProgress, colorThumb, cornerProgress);
        seekbarAudio.changeColor(colorBackground, colorProgress, colorThumb, cornerProgress);
    }

    public void updateVolumeAudio(float volumeNew) {
        Log.d("duongcvc", "updateVolumeAudio: "+ volumeNew);
        float volumeAudio = (volumeNew / (float) maxVolume);
        seekbarAudio.setCurrentProgress(volumeAudio);
        updateIconAudio(volumeAudio);
    }

    private void updateIconAudio(float progress) {
        float f = (float) progress;
        if (f == 0) {
            iconAudio.setImageResource(R.drawable.ic_volume_black_mute);
        } else if (f < 0.3f) {
            iconAudio.setImageResource(R.drawable.ic_volume_black1);
        } else if (0.3f < f && f < 0.6f) {
            iconAudio.setImageResource(R.drawable.ic_volume_black2);
        } else if (0.6f < f) {
            iconAudio.setImageResource(R.drawable.ic_volume_black);
        }
    }

    public void updateVolumeSystem() {
        float volumeSystem = (float) (AudioManagerUtils.getInstance(context).getVolumeAlarm() / (float)maxVolumeSystem);
        Log.d("duongcvc", "updateVolumeSystem: "+ volumeSystem);
        seekbarSystem.setCurrentProgress(volumeSystem);
        updateIconSystem(volumeSystem);
    }

    private void updateIconSystem(float progress) {
        float f = (float) progress;
        if (f == 0) {
            iconVolumeSystem.setImageResource(R.drawable.ic_volume_black_mute);
        } else if (f < 0.3f) {
            iconVolumeSystem.setImageResource(R.drawable.ic_volume_black1);
        } else if (0.3f < f && f < 0.6f) {
            iconVolumeSystem.setImageResource(R.drawable.ic_volume_black2);
        } else if (0.6f < f) {
            iconVolumeSystem.setImageResource(R.drawable.ic_volume_black);
        }
    }

    public void updateVolumeRingtone() {
        float volumeRingtone = (AudioManagerUtils.getInstance(context).getVolumeRingtone()  / (float) maxVolumeRingtone);
        Log.d("duongcvc", "updateVolumeRingtone: "+ volumeRingtone);
        seekbarRingtone.setCurrentProgress(volumeRingtone);
        updateIconRingtone(volumeRingtone);
    }

    private void updateIconRingtone(float progress) {
        float f = (float) progress;
        if (f == 0) {
            iconRingtone.setImageResource(R.drawable.ic_volume_black_mute);
        } else if (f < 0.3f) {
            iconRingtone.setImageResource(R.drawable.ic_volume_black1);
        } else if (0.3f < f && f < 0.6f) {
            iconRingtone.setImageResource(R.drawable.ic_volume_black2);
        } else if (0.6f < f) {
            iconRingtone.setImageResource(R.drawable.ic_volume_black);
        }
    }

    public void changeFont(Typeface typeface){
        this.typeface = typeface;
        setFont();
    }

    private void setFont(){
        if (typeface !=null){
            if(tvMusic != null) tvMusic.setTypeface(typeface);
            if(tvAlarm != null) tvAlarm.setTypeface(typeface);
            if(tvRingtone != null) tvRingtone.setTypeface(typeface);
        }
    }
}

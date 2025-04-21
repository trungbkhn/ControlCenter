package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group2;

import static android.media.session.PlaybackState.STATE_PLAYING;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.feature.controlios14.manager.AudioManagerUtils;
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614;
import com.tapbi.spark.controlcenter.utils.DensityUtils;
import com.tapbi.spark.controlcenter.utils.MediaUtils;
import com.tapbi.spark.controlcenter.utils.MethodUtils;

import timber.log.Timber;


public class MusicExpandView extends ConstraintLayout implements MediaUtils.IMediaListener {

    private Context context;

    private SeekBar seekbarVolumeExpand;
    private ImageView preExpand, playExpand, nextExpand, thumbImage;
    private TextView tvName, tvArtist, tvTitle;

    private int volume;
    private Handler handler;
    private boolean isUpdateSeekbarVolume = true;
    private final SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                volume = progress / 10;
                AudioManagerUtils.getInstance(context).setVolume(volume);
                Timber.e("Duongcvv " + volume);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isUpdateSeekbarVolume = false;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isUpdateSeekbarVolume = true;
                }
            }, 200);

        }
    };
    private MediaUtils controlMusicUtils;
    private boolean isClickPausePlay = false;


    private Runnable runnablePlayPause = () -> isClickPausePlay=false;
    private final OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == preExpand) {
                if (controlMusicUtils != null) {
                    controlMusicUtils.controlMusic(AudioManagerUtils.PREVIOUS);
                }
            } else if (v == playExpand) {
                if (!isClickPausePlay) {
                    isClickPausePlay = true;
                    if (controlMusicUtils != null) {
                        controlMusicUtils.controlMusic(AudioManagerUtils.PLAYPAUSE);
                    }
                    handler.postDelayed(runnablePlayPause, 1000);
                }
            } else if (v == nextExpand) {
                if (controlMusicUtils != null) {
                    controlMusicUtils.controlMusic(AudioManagerUtils.NEXT);
                }
            }
        }
    };

    public MusicExpandView(Context context) {
        super(context);
        init(context);
    }

    public MusicExpandView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MusicExpandView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        handler = new Handler();
        LayoutInflater.from(context).inflate(R.layout.layout_music_expand, this, true);
        seekbarVolumeExpand = findViewById(R.id.seekbarVolumeExpand);
        seekbarVolumeExpand.setOnSeekBarChangeListener(onSeekBarChangeListener);

        preExpand = findViewById(R.id.preExpand);
        playExpand = findViewById(R.id.playExpand);
        nextExpand = findViewById(R.id.nextExpand);
        thumbImage = findViewById(R.id.thumbExpand);
        tvName = findViewById(R.id.titleExpand);
        tvArtist = findViewById(R.id.artistExpand);
        tvTitle = findViewById(R.id.title);

        preExpand.setOnClickListener(onClickListener);
        playExpand.setOnClickListener(onClickListener);
        nextExpand.setOnClickListener(onClickListener);

        int maxVolume = AudioManagerUtils.getInstance(context).getMaxVolume();
        volume = AudioManagerUtils.getInstance(context).getVolume();
        seekbarVolumeExpand.setMax(maxVolume * 10);
        updateVolume(volume);
    }

    public void updateVolume(int volume) {
        if (isUpdateSeekbarVolume) {
            seekbarVolumeExpand.setProgress(volume * 10);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (controlMusicUtils == null) {
            controlMusicUtils = new MediaUtils(context, this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (controlMusicUtils != null) {
            controlMusicUtils.releaseListener();
        }
    }

    @Override
    public void stateChange(int state) {
        if (state == STATE_PLAYING) {
            playExpand.setImageResource(R.drawable.pause);
        } else {
            playExpand.setImageResource(R.drawable.play);
        }
    }

    @Override
    public void contentChange(String artist, String track, Bitmap thumb, String packageName) {
        if (track.equals(context.getString(R.string.audio_music)) && artist.isEmpty()) {
            tvTitle.setVisibility(VISIBLE);
            tvName.setVisibility(GONE);
            tvArtist.setVisibility(GONE);
        } else {
            tvTitle.setVisibility(GONE);
            tvName.setVisibility(VISIBLE);
            tvArtist.setVisibility(VISIBLE);
        }

        if (thumb != null && !thumb.isRecycled()) {
            thumbImage.setImageBitmap(thumb);
        } else {
            thumbImage.setImageDrawable(MethodUtils.getIconFromPackageName(context, packageName));
        }
        tvName.setText(track);
        tvTitle.setText(track);
        tvArtist.setText(artist);
    }

    @Override
    public void checkPermissionNotificationListener(boolean isCheck) {
        if (isCheck){

        }else {

        }
    }

    @Override
    public void timeMediaChange(int state) {

    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (getVisibility() == VISIBLE && visibility == VISIBLE) {
            updateVolume(AudioManagerUtils.getInstance(context).getVolume());
        }
    }

    public void changeFont(Typeface typeface){
        tvName.setTypeface(typeface);
        tvArtist.setTypeface(typeface);
        tvTitle.setTypeface(typeface);
    }

}

package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group2;

import static android.media.session.PlaybackState.STATE_PLAYING;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.session.PlaybackState;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.models.InfoTimeMedia;
import com.tapbi.spark.controlcenter.databinding.LayoutControlMusicIosSquareBinding;
import com.tapbi.spark.controlcenter.feature.controlios14.manager.AudioManagerUtils;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlMusicIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ConstraintLayoutBase;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ImageBase;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.callback.OnClickSettingListener;
import com.tapbi.spark.controlcenter.utils.MediaUtils;
import com.tapbi.spark.controlcenter.utils.Utils;

import timber.log.Timber;

public class ControlMusicIosSquareView extends ConstraintLayoutBase implements MediaUtils.IMediaListener {

    private LayoutControlMusicIosSquareBinding binding;
    private Context context;

    private boolean isCheckNoty = false;
    private ControlMusicIosModel controlMusicIOS;

    private OnClickSettingListener onClickSettingListener;
    private MusicView.OnMusicViewListener onMusicViewListener;
    private MediaUtils controlMusicUtils;
    private long durationMusic = 1;
    private long currentTimeMusic = 1;


    private Handler handler = new Handler(Looper.getMainLooper());

    private boolean isClickPausePlay = false;

    private Runnable runnablePlayPause = () -> isClickPausePlay = false;
    //    private void setProgressColor(int progressColor, int id) {
//        LayerDrawable progressDrawable = (LayerDrawable) binding.progressTime.getProgressDrawable();
//        Drawable progressLayer = progressDrawable.findDrawableByLayerId(id);
//
//        if (progressLayer instanceof GradientDrawable) {
//            ((GradientDrawable) progressLayer).setColor(progressColor);
//        } else if (progressLayer instanceof ClipDrawable) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                Drawable innerDrawable = ((ClipDrawable) progressLayer).getDrawable();
//                if (innerDrawable instanceof GradientDrawable) {
//                    ((GradientDrawable) innerDrawable).setColor(progressColor);
//                }
//            }
//        }
//    }
    private CountDownTimer downTimer;

    public ControlMusicIosSquareView(Context context) {
        super(context);
        init(context);
    }

    public ControlMusicIosSquareView(Context context, ControlMusicIosModel controlMusicIOS, DataSetupViewControlModel dataSetupViewControlModel) {
        super(context);
        this.controlMusicIOS = controlMusicIOS;
        this.dataSetupViewControlModel = dataSetupViewControlModel;
        init(context);
    }

    public ControlMusicIosSquareView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ControlMusicIosSquareView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setOnMusicViewListener(MusicView.OnMusicViewListener onMusicViewListener) {
        this.onMusicViewListener = onMusicViewListener;
    }

    public void checkNotyPermission(boolean isCheck) {
        isCheckNoty = isCheck;
        if (binding.groupNoty != null && binding.groupMusic != null) {
            if (isCheck) {
                binding.groupMusic.setVisibility(VISIBLE);
                binding.groupNoty.setVisibility(INVISIBLE);
            } else {
                binding.groupMusic.setVisibility(INVISIBLE);
                binding.groupNoty.setVisibility(VISIBLE);
            }
        }
    }

    private void changeControlMusicIOS(ControlMusicIosModel controlMusicIOS) {
        this.controlMusicIOS = controlMusicIOS;
        initView();
    }

    private void initView() {
        if (controlMusicIOS != null) {
            changeColorBackground(controlMusicIOS.getBackgroundDefaultColorViewParent(), controlMusicIOS.getBackgroundSelectColorViewParent(), controlMusicIOS.getCornerBackgroundViewParent());
            binding.tvName.setTextColor(Color.parseColor(controlMusicIOS.getColorTextName()));
            binding.tvName.setTypeface(dataSetupViewControlModel.getTypefaceText(), Typeface.BOLD);
            binding.tvDes.setTextColor(Color.parseColor(controlMusicIOS.getColorTextName()));
            binding.tvDes.setTypeface(dataSetupViewControlModel.getTypefaceText());
            binding.tvArtist.setTextColor(Color.parseColor(controlMusicIOS.getColorTextArtists()));
            binding.tvArtist.setTypeface(dataSetupViewControlModel.getTypefaceText());
            binding.nextAction.setColorFilter(Color.parseColor(controlMusicIOS.getColorIcon()));
            binding.playPauseAction.setColorFilter(Color.parseColor(controlMusicIOS.getColorIcon()));
            binding.previousAction.setColorFilter(Color.parseColor(controlMusicIOS.getColorIcon()));
//            setProgressColor(Color.parseColor(controlMusicIOS.getColorProgressSeekbar()), android.R.id.progress);
//            setProgressColor(Color.parseColor(controlMusicIOS.getColorDefaultSeekbar()), android.R.id.background);
            binding.controlTimeMusic.changeIsPermission(true);
            binding.controlTimeMusic.changeIsTouch(false);
            binding.controlTimeMusic.changeColor(controlMusicIOS.getColorDefaultSeekbar(), controlMusicIOS.getColorProgressSeekbar(), controlMusicIOS.getColorThumbSeekbar(), 0.5f);
        }
    }

    private void init(Context ctx) {
        this.context = ctx;
        binding = LayoutControlMusicIosSquareBinding.inflate(LayoutInflater.from(context), this, true);

        initView();
        binding.tvName.setSelected(true);
        binding.tvArtist.setSelected(true);

        binding.background.setOnAnimationListener(new ImageBase.OnAnimationListener() {
            @Override
            public void onDown() {
                if (onMusicViewListener != null) {
                    onMusicViewListener.onDown();
                }
                animationDown();
            }

            @Override
            public void onUp() {
                if (onMusicViewListener != null) {
                    onMusicViewListener.onUp();
                }
                animationUp();
            }

            @Override
            public void onClick() {

            }

            @Override
            public void onLongClick() {
                if (onMusicViewListener != null && isCheckNoty) {
                    onMusicViewListener.onLongClick();
                }
            }

            @Override
            public void onClose() {

            }
        });

        binding.previousAction.setOnAnimationListener(new ImageBase.OnAnimationListener() {
            @Override
            public void onDown() {
                if (onMusicViewListener != null) {
                    onMusicViewListener.onDown();
                }
                animationDown();
            }

            @Override
            public void onUp() {
                if (onMusicViewListener != null) {
                    onMusicViewListener.onUp();
                }
                animationUp();
            }

            @Override
            public void onClick() {
                if (controlMusicUtils != null) {
                    controlMusicUtils.controlMusic(AudioManagerUtils.PREVIOUS);
                }
            }

            @Override
            public void onLongClick() {
                if (onMusicViewListener != null && isCheckNoty) {
                    onMusicViewListener.onLongClick();
                }
            }

            @Override
            public void onClose() {

            }
        });

        binding.playPauseAction.setOnAnimationListener(new ImageBase.OnAnimationListener() {
            @Override
            public void onDown() {
                if (onMusicViewListener != null) {
                    onMusicViewListener.onDown();
                }
                animationDown();
            }

            @Override
            public void onUp() {
                if (onMusicViewListener != null) {
                    onMusicViewListener.onUp();
                }
                animationUp();
            }

            @Override
            public void onClick() {
                if (!isClickPausePlay) {
                    isClickPausePlay = true;
                    if (controlMusicUtils != null) {
                        controlMusicUtils.controlMusic(AudioManagerUtils.PLAYPAUSE);
                    }
                    handler.postDelayed(runnablePlayPause, 1000);
                }
            }

            @Override
            public void onLongClick() {
                if (onMusicViewListener != null && isCheckNoty) {
                    onMusicViewListener.onLongClick();
                }
            }

            @Override
            public void onClose() {

            }
        });

        binding.nextAction.setOnAnimationListener(new ImageBase.OnAnimationListener() {
            @Override
            public void onDown() {
                if (onMusicViewListener != null) {
                    onMusicViewListener.onDown();
                }
                animationDown();
            }

            @Override
            public void onUp() {
                if (onMusicViewListener != null) {
                    onMusicViewListener.onUp();
                }
                animationUp();
            }

            @Override
            public void onClick() {
                if (controlMusicUtils != null) {
                    controlMusicUtils.controlMusic(AudioManagerUtils.NEXT);
                }
            }

            @Override
            public void onLongClick() {
                if (onMusicViewListener != null && isCheckNoty) {
                    onMusicViewListener.onLongClick();
                }
            }

            @Override
            public void onClose() {

            }
        });

        binding.tvVerify.setOnClickListener(v -> {
            if (onMusicViewListener != null) {
                onMusicViewListener.onClickVerify();
            }
        });
    }

    public void setOnClickSettingListener(OnClickSettingListener onClickSettingListener) {
        this.onClickSettingListener = onClickSettingListener;
    }

    @Override
    public void stateChange(int state) {
        if (state == STATE_PLAYING) {
            binding.playPauseAction.setImageResource(R.drawable.pause);
        } else {
            binding.playPauseAction.setImageResource(R.drawable.play);
        }
        updateDuration(state);
    }

    @Override
    public void contentChange(String artist, String track, Bitmap thumb, String packageName) {
        binding.tvName.setText(track);
        binding.tvArtist.setText(artist);
        if (packageName == null || packageName.isEmpty()) {
//            binding.playerIcon.setImageResource(R.drawable.ic_music);
//            binding.progressTime.setProgress(0);
            currentTimeMusic = 0;
            durationMusic = 1;
            binding.controlTimeMusic.setCurrentProgress(0);
            binding.tvTimeCurrent.setText("0:00");
            binding.tvDuration.setText("0:00");
        } else {
//            if (thumb != null) {
//                binding.playerIcon.setImageBitmap(thumb);
//            } else {
//                binding.playerIcon.setImageDrawable(MethodUtils.getIconFromPackageName(context, packageName));
//            }
        }

    }

    private void updateDuration(int state) {
        if (controlMusicUtils != null) {
            InfoTimeMedia infoTimeMedia = controlMusicUtils.getInfoTimeMedia();
            if (controlMusicUtils.mediaController != null) {
                PlaybackState playbackState = controlMusicUtils.mediaController.getPlaybackState();
                if (playbackState != null) {
                    infoTimeMedia.setCurrentPosition(playbackState.getPosition());
                }
            }
            if (downTimer != null) {
                downTimer.cancel();
            }
            long millisInFuture = infoTimeMedia.getDuration() - infoTimeMedia.getCurrentPosition();
            if (state == PlaybackState.STATE_PLAYING) {
                downTimer = new CountDownTimer(millisInFuture, 1000) {
                    @Override
                    public void onTick(long l) {
                        long currentTime = millisInFuture - l + infoTimeMedia.getCurrentPosition();
                        updateViewSeekbarPosition(currentTime);
                    }

                    @Override
                    public void onFinish() {
                        updateViewSeekbarPosition(infoTimeMedia.getDuration());
                    }
                }.start();
            }
            String duration = Utils.INSTANCE.milliSecondsToTimer(infoTimeMedia.getDuration());
            Timber.e("NVQ milliSecondsToTimer " + duration);
            binding.tvDuration.setText(duration);
            setViewSeekbarPosition(infoTimeMedia.getCurrentPosition(), infoTimeMedia.getDuration());
        }
    }


    private void setViewSeekbarPosition(long position, long duration) {
        durationMusic = (duration / 1000);
//        binding.progressTime.setMax((int) (duration / 1000));
        updateViewSeekbarPosition(position);
    }

    private void updateViewSeekbarPosition(long position) {
        binding.tvTimeCurrent.setText(Utils.INSTANCE.milliSecondsToTimer(position));
        currentTimeMusic = position / 1000;
        binding.controlTimeMusic.setCurrentProgress((float) currentTimeMusic / (float) durationMusic);
    }

    @Override
    public void checkPermissionNotificationListener(boolean isCheck) {
        checkNotyPermission(isCheck);
    }

    @Override
    public void timeMediaChange(int state) {
        updateDuration(state);
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


}

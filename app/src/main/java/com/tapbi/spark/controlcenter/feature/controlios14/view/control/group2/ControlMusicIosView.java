package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group2;

import static android.media.session.PlaybackState.STATE_PLAYING;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.session.PlaybackState;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.models.InfoTimeMedia;
import com.tapbi.spark.controlcenter.databinding.LayoutControlMusicIosBinding;
import com.tapbi.spark.controlcenter.feature.controlios14.manager.AudioManagerUtils;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlMusicIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ConstraintLayoutBase;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ImageBase;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.callback.OnClickSettingListener;
import com.tapbi.spark.controlcenter.receiver.HHeadsetReceiver;
import com.tapbi.spark.controlcenter.utils.MediaUtils;
import com.tapbi.spark.controlcenter.utils.MethodUtils;
import com.tapbi.spark.controlcenter.utils.Utils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

public class ControlMusicIosView extends ConstraintLayoutBase implements MediaUtils.IMediaListener, HHeadsetReceiver.HeadsetReceiverCallback {

    private LayoutControlMusicIosBinding binding;
    private Context context;

    private boolean isCheckNoty = false;
    private ControlMusicIosModel controlMusicIOS;

    private OnClickSettingListener onClickSettingListener;
    private MusicView.OnMusicViewListener onMusicViewListener;
    private MediaUtils controlMusicUtils;


    private CountDownTimer downTimer;
    private long durationMusic = 1;
    private long currentTimeMusic = 1;


    private Handler handler = new Handler(Looper.getMainLooper());

    private boolean isCheck = false;

    private Runnable runnablePlayPause = () -> isCheck = false;


    public ControlMusicIosView(Context context) {
        super(context);
        init(context);
    }

    public ControlMusicIosView(Context context, ControlMusicIosModel controlMusicIOS, DataSetupViewControlModel dataSetupViewControlModel) {
        super(context);
        this.controlMusicIOS = controlMusicIOS;
        this.dataSetupViewControlModel = dataSetupViewControlModel;
        init(context);
    }

    public ControlMusicIosView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ControlMusicIosView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

    public void changeControlMusicIOS(ControlMusicIosModel controlMusicIOS, DataSetupViewControlModel dataSetupViewControlModel) {
        this.controlMusicIOS = controlMusicIOS;
        this.dataSetupViewControlModel = dataSetupViewControlModel;
        initView();
    }

    private void initView() {
        if (controlMusicIOS != null) {
            changeColorBackground(controlMusicIOS.getBackgroundDefaultColorViewParent(), controlMusicIOS.getBackgroundSelectColorViewParent(), controlMusicIOS.getCornerBackgroundViewParent());
            binding.cvPlayerIcon.setCornerBackground(controlMusicIOS.getCornerImageAvatarMusic());
            binding.tvName.setTextColor(Color.parseColor(controlMusicIOS.getColorTextName()));
            binding.tvName.setTypeface(dataSetupViewControlModel.getTypefaceText());
            binding.tvDes.setTextColor(Color.parseColor(controlMusicIOS.getColorTextName()));
            binding.tvDes.setTypeface(dataSetupViewControlModel.getTypefaceText());
            binding.tvArtist.setTextColor(Color.parseColor(controlMusicIOS.getColorTextArtists()));
            binding.tvArtist.setTypeface(dataSetupViewControlModel.getTypefaceText());
            binding.tvDuration.setTypeface(dataSetupViewControlModel.getTypefaceText());
            binding.tvTimeCurrent.setTypeface(dataSetupViewControlModel.getTypefaceText());
            binding.nextAction.setColorFilter(Color.parseColor(controlMusicIOS.getColorIcon()));
            binding.playPauseAction.setColorFilter(Color.parseColor(controlMusicIOS.getColorIcon()));
            binding.previousAction.setColorFilter(Color.parseColor(controlMusicIOS.getColorIcon()));
            binding.tvTimeCurrent.setTextColor(Color.parseColor(controlMusicIOS.getColorTextTime()));
            binding.tvDuration.setTextColor(Color.parseColor(controlMusicIOS.getColorTextDuration()));
//            setProgressColor(Color.parseColor(controlMusicIOS.getColorProgressSeekbar()), android.R.id.progress);
//            setProgressColor(Color.parseColor(controlMusicIOS.getColorDefaultSeekbar()), android.R.id.background);
            binding.controlTimeMusic.changeIsPermission(true);
            binding.controlTimeMusic.changeIsTouch(false);
            binding.controlTimeMusic.changeColor(controlMusicIOS.getColorDefaultSeekbar(), controlMusicIOS.getColorProgressSeekbar(), controlMusicIOS.getColorThumbSeekbar(), 0.5f);
            binding.imgHeadphone.setImageResource(R.drawable.ic_phone);
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float size = Math.min((int) (0.104 * h), 60);
        float sizeM = Math.min((int) (0.093 * h), 60);
        Timber.e("Duongcv " + sizeM + ":" + size);
        binding.tvName.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        binding.tvArtist.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        binding.tvDuration.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        binding.tvTimeCurrent.setTextSize(TypedValue.COMPLEX_UNIT_PX, sizeM);
        binding.tvDuration.setVisibility(VISIBLE);
    }

    private void init(Context ctx) {
        this.context = ctx;
        binding = LayoutControlMusicIosBinding.inflate(LayoutInflater.from(context), this, true);
        setDrawableDefault();
        initView();
        binding.tvName.setSelected(true);
        binding.tvArtist.setSelected(true);
        binding.tvDuration.setSelected(true);


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
                if (!isCheck) {
                    isCheck = true;
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

        binding.cvPlayerIcon.setOnAnimationListener(new ImageBase.OnAnimationListener() {
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
                    boolean p = controlMusicUtils.openAppMusic();
                    if (p) {
                        if (onClickSettingListener != null) {
                            onClickSettingListener.onClick();
                        }
                    }
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
            setDrawableDefault();
            binding.tvTimeCurrent.setText("0:00");
            binding.tvDuration.setText("0:00");
            durationMusic = 1;
            currentTimeMusic = 0;
            binding.controlTimeMusic.setCurrentProgress(0);
        } else {
            if (thumb != null && !thumb.isRecycled()) {
                binding.cvPlayerIcon.setSrc(thumb);
            } else {
                Completable.fromRunnable(() -> {
                    BitmapDrawable drawable = MethodUtils.getIconFromPackageNameMusic(context, packageName);
                    binding.cvPlayerIcon.setSrc(drawable.getBitmap());
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                });


            }

        }

    }

    private void setDrawableDefault() {
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_music);
        if (drawable != null) {
            binding.cvPlayerIcon.setSrc(((BitmapDrawable) drawable).getBitmap());
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
            binding.tvDuration.setText(duration);
            setViewSeekbarPosition(infoTimeMedia.getCurrentPosition(), infoTimeMedia.getDuration());
        }
    }


    private void setViewSeekbarPosition(long position, long duration) {
//        binding.progressTime.setMax((int) (duration / 1000));
        durationMusic = (duration / 1000);
        updateViewSeekbarPosition(position);
    }

    private void updateViewSeekbarPosition(long position) {
        if (!binding.tvDuration.getText().equals("0:00")) {
            binding.tvTimeCurrent.setText(Utils.INSTANCE.milliSecondsToTimer(position));
            long oldCurrent = currentTimeMusic;
            currentTimeMusic = position / 1000;
            binding.controlTimeMusic.setCurrentProgress((float) currentTimeMusic / (float) durationMusic);
        }else {
            binding.tvTimeCurrent.setText("0:00");
        }
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
        listenerHeadset();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (controlMusicUtils != null) {
            controlMusicUtils.releaseListener();
        }
        HHeadsetReceiver.Companion.getInstance().unregister(getContext());
    }

    private void listenerHeadset() {
        HHeadsetReceiver.Companion.getInstance().register(getContext());
        HHeadsetReceiver.Companion.getInstance().setCallback(this);
    }


    @Override
    public void onHeadsetConnected() {
        Timber.e("hachung onHeadsetConnected:");
        binding.imgHeadphone.setImageResource(R.drawable.ic_headphone);
    }

    @Override
    public void onHeadsetDisconnected() {
        Timber.e("hachung onHeadsetDisconnected:");
        binding.imgHeadphone.setImageResource(R.drawable.ic_phone);
    }
}

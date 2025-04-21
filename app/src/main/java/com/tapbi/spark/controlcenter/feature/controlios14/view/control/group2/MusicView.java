package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group2;

import static android.media.session.PlaybackState.STATE_PLAYING;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.databinding.LayoutControlMusicBinding;
import com.tapbi.spark.controlcenter.feature.controlios14.manager.AudioManagerUtils;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlMusicIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ConstraintLayoutBase;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ImageBase;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.callback.OnClickSettingListener;
import com.tapbi.spark.controlcenter.utils.MediaUtils;
import com.tapbi.spark.controlcenter.utils.MethodUtils;


public class MusicView extends ConstraintLayoutBase implements MediaUtils.IMediaListener {

    private Context context;
    private boolean isCheckNoty = false;
    private OnClickSettingListener onClickSettingListener;
    private OnMusicViewListener onMusicViewListener;
    private MediaUtils controlMusicUtils;
    private LayoutControlMusicBinding binding;
    private ControlMusicIosModel controlMusicIOS;


    private Handler handler = new Handler(Looper.getMainLooper());

    private boolean isClickPausePlay = false;

    private Runnable runnablePlayPause = () -> isClickPausePlay = false;


    public MusicView(Context context) {
        super(context);
        init(context);
    }

    public MusicView(Context context, ControlMusicIosModel controlMusicIOS, DataSetupViewControlModel dataSetupViewControlModel) {
        super(context);
        this.controlMusicIOS = controlMusicIOS;
        this.dataSetupViewControlModel = dataSetupViewControlModel;
        init(context);
    }

    public MusicView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MusicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setOnMusicViewListener(OnMusicViewListener onMusicViewListener) {
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

    private void init(Context ctx) {
        this.context = ctx;
        binding = LayoutControlMusicBinding.inflate(LayoutInflater.from(context), this, true);

        initView();
        binding.tvName.setSelected(true);
        binding.tvName.setTypeface(dataSetupViewControlModel.getTypefaceText());
        binding.tvArtist.setSelected(true);
        binding.tvArtist.setTypeface(dataSetupViewControlModel.getTypefaceText());

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

    private void changeControlMusicIOS(ControlMusicIosModel controlMusicIOS) {
        this.controlMusicIOS = controlMusicIOS;
        initView();
    }

    private void initView() {
        if (controlMusicIOS != null) {
            changeColorBackground(controlMusicIOS.getBackgroundDefaultColorViewParent(), controlMusicIOS.getBackgroundSelectColorViewParent(), controlMusicIOS.getCornerBackgroundViewParent());
//            binding.cvPlayerIcon.setRadius(controlMusicIOS.getCornerImageAvatarMusic());
            binding.tvName.setTextColor(Color.parseColor(controlMusicIOS.getColorTextName()));
            binding.tvDes.setTextColor(Color.parseColor(controlMusicIOS.getColorTextName()));
            binding.tvArtist.setTextColor(Color.parseColor(controlMusicIOS.getColorTextArtists()));
            binding.nextAction.setColorFilter(Color.parseColor(controlMusicIOS.getColorIcon()));
            binding.playPauseAction.setColorFilter(Color.parseColor(controlMusicIOS.getColorIcon()));
            binding.previousAction.setColorFilter(Color.parseColor(controlMusicIOS.getColorIcon()));

        }
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
    }

    @Override
    public void contentChange(String artist, String track, Bitmap thumb, String packageName) {
        binding.tvName.setText(track);
        binding.tvArtist.setText(artist);
        if (thumb != null && !thumb.isRecycled()) {
            binding.playerIcon.setImageBitmap(thumb);
        } else if (packageName != null && !packageName.isEmpty()) {
            binding.playerIcon.setImageDrawable(MethodUtils.getIconFromPackageName(context, packageName));
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float size = (float) (0.21 * h);
        binding.tvName.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        binding.tvArtist.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);

    }

    @Override
    public void checkPermissionNotificationListener(boolean isCheck) {
        checkNotyPermission(isCheck);
    }

    @Override
    public void timeMediaChange(int state) {

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

    public interface OnMusicViewListener {
        void onDown();

        void onUp();

        void onLongClick();
        void onLongClick(View v);

        void onClickVerify();
    }


}

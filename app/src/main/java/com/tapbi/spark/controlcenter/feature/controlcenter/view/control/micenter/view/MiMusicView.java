package com.tapbi.spark.controlcenter.feature.controlcenter.view.control.micenter.view;

import static android.media.session.PlaybackState.STATE_PAUSED;
import static android.media.session.PlaybackState.STATE_PLAYING;
import static android.media.session.PlaybackState.STATE_STOPPED;
import static com.tapbi.spark.controlcenter.common.Constant.NEXT;
import static com.tapbi.spark.controlcenter.common.Constant.PAUSE;
import static com.tapbi.spark.controlcenter.common.Constant.PLAY;
import static com.tapbi.spark.controlcenter.common.Constant.PREVIOUS;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.data.repository.ThemeHelper;
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.ShowHideViewMusicMi;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.ControlCenterIOSView;
import com.tapbi.spark.controlcenter.receiver.HHeadsetReceiver;
import com.tapbi.spark.controlcenter.utils.DensityUtils;
import com.tapbi.spark.controlcenter.utils.MediaUtils;
import com.tapbi.spark.controlcenter.utils.MethodUtils;

public class MiMusicView extends ConstraintLayout implements HHeadsetReceiver.HeadsetReceiverCallback,
        MediaUtils.IMediaListener {
    public float width;
    public float widthScreen;
    public MiMusicListener miMusicListener;
    private ShowHideViewMusicMi showHideViewMusicMi;
    private TextView tvNameAppPlayMusic, typeVolumePlay, tvNamePlay, tvSinger;
    private ShapeableImageView imgDetailMusic;
    private ImageView imgPause;
    private ControlCenterIOSView.OnControlCenterListener onControlCenterListener;
    private float dX;
    private boolean isMove = false;
    private MediaUtils controlMusicUtils;
    private float downx = 0;
    private boolean close = false;
    private Group groupMusic;
    private Group groupNoty;
    private TextView tvVerify;

    private Typeface typeface;

    public MiMusicView(Context context) {
        super(context);
    }

    public MiMusicView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MiMusicView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMiMusicListener(MiMusicListener miMusicListener) {
        this.miMusicListener = miMusicListener;
    }

    public void setShowHideViewMusicMi(ShowHideViewMusicMi showHideViewMusicMi, ControlCenterIOSView.OnControlCenterListener onControlCenterListener) {
        this.showHideViewMusicMi = showHideViewMusicMi;
        this.onControlCenterListener = onControlCenterListener;
        init();
    }

    private void init() {
        //Timber.e(".");
        LayoutInflater.from(getContext()).inflate(R.layout.view_mi_music, this);
        findView();
        getHeightScreen();
        listenerHeadset();
        tvVerify.setOnClickListener(v -> {
            if (miMusicListener != null) {
                miMusicListener.onClinkVerify();
            }
        });
        if (ThemeHelper.itemControl.getControlCenter() != null) {
            typeface = Typeface.createFromAsset(getContext().getAssets(), Constant.FOLDER_FONT_CONTROL_ASSETS + ThemeHelper.itemControl.getFont());
            tvNameAppPlayMusic.setTypeface(typeface);
            tvNamePlay.setTypeface(typeface);
            tvSinger.setTypeface(typeface);
            tvVerify.setTypeface(typeface);
            typeVolumePlay.setTypeface(typeface);
        }

    }


    private void getHeightScreen() {
        widthScreen = App.widthHeightScreenCurrent.w;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (controlMusicUtils == null) {
            controlMusicUtils = new MediaUtils(getContext(), this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        HHeadsetReceiver.Companion.getInstance().unregister(getContext());
        if (controlMusicUtils != null) {
            controlMusicUtils.releaseListener();
        }
    }

    private void listenerHeadset() {
//    Timber.e(".");
        HHeadsetReceiver.Companion.getInstance().register(getContext());
        HHeadsetReceiver.Companion.getInstance().setCallback(this);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (controlMusicUtils.isMusicActive()) {
            return super.onInterceptTouchEvent(ev);
        } else {
            //Timber.e("hoangld getAction: " + ev.getAction());
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    close = true;
                    isMove = false;
                    dX = this.getX() - ev.getRawX();
                    downx = ev.getX();
                    return false;
                case MotionEvent.ACTION_MOVE:
                    if (downx - ev.getX() > 10 || downx - ev.getX() < -10) {
                        isMove = true;
                        clearAnimation();
                        setX(ev.getRawX() + dX);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (isMove) {
                        float valueAnimate;
                        if (ev.getRawX() + dX < widthScreen * 0.5 && ev.getRawX() + dX > -widthScreen * 0.5) {
                            valueAnimate = getLeft();
                        } else {
                            if (ev.getRawX() + dX > 0) {
                                valueAnimate = widthScreen;
                            } else {
                                valueAnimate = -widthScreen;
                            }
                        }
                        clearAnimation();
                        this.animate()
                                .x(valueAnimate)
                                .setDuration(0)
                                .setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        if (valueAnimate != getLeft()) {
                                            if (close) {
                                                close = false;
                                                showHideViewMusicMi.showHide(false);
                                            }
                                            isMove = false;
                                        }
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                })
                                .start();
                        return true;
                    }
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void actionMusic(int value) {
        if (value == NEXT) {
            controlMusicUtils.controlMusic(KeyEvent.KEYCODE_MEDIA_NEXT);
        } else if (value == PLAY) {
            controlMusicUtils.controlMusic(KeyEvent.KEYCODE_MEDIA_PLAY);
        } else if (value == PREVIOUS) {
            controlMusicUtils.controlMusic(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
        } else if (value == PAUSE) {
            controlMusicUtils.controlMusic(KeyEvent.KEYCODE_MEDIA_PAUSE);
        }
    }

    private void findView() {
        CardView parentMusic = findViewById(R.id.parentMusic);
        parentMusic.setFocusable(true);
        parentMusic.setFocusableInTouchMode(true);
        parentMusic.setOnClickListener(v -> {
            if (controlMusicUtils != null) {
                onControlCenterListener.onClose();
                controlMusicUtils.openAppMusic();
            }
        });

        if (ThemeHelper.itemControl.getControlCenter() != null) {
            parentMusic.setCardBackgroundColor(Color.parseColor(ThemeHelper.itemControl.getControlCenter().getBackgroundColorDefaultControl()));
        }
        tvNameAppPlayMusic = findViewById(R.id.tvNameAppPlayMusic);
        typeVolumePlay = findViewById(R.id.typeVolumePlay);
        tvNamePlay = findViewById(R.id.tvNamePlay);
        tvSinger = findViewById(R.id.tvSinger);
        imgDetailMusic = findViewById(R.id.imgDetailMusic);
        ImageView imgPre = findViewById(R.id.imgPre);
        imgPause = findViewById(R.id.imgPause);
        ImageView imgNext = findViewById(R.id.imgNext);
        groupMusic = findViewById(R.id.groupMusic);
        groupNoty = findViewById(R.id.groupNoty);
        imgNext.setOnClickListener(v -> actionMusic(NEXT));
        imgPre.setOnClickListener(v -> actionMusic(PREVIOUS));
        imgPause.setOnClickListener(v -> {
            if (controlMusicUtils.isMusicActive()) {
                actionMusic(PAUSE);
            } else {
                actionMusic(PLAY);
            }
        });
        tvVerify = findViewById(R.id.tvVerify);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
    }


    @Override
    public void onHeadsetConnected() {
        int padding = (int) DensityUtils.pxFromDp(getContext(), 4f);
        typeVolumePlay.setText(getContext().getString(R.string.text_headset));
        typeVolumePlay.setPadding(padding + padding, padding, padding + padding, padding);
        typeVolumePlay.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_info_headset, 0, 0, 0);
    }

    @Override
    public void onHeadsetDisconnected() {
        int padding = (int) DensityUtils.pxFromDp(getContext(), 4f);
        typeVolumePlay.setText(getContext().getString(R.string.text_phone_speak));
        typeVolumePlay.setPadding(padding + padding, padding, padding + padding, padding);
        typeVolumePlay.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_info_volume, 0, 0, 0);
    }

    @Override
    public void stateChange(int state) {
        switch (state) {
            case STATE_PLAYING:
                imgPause.setImageResource(R.drawable.ic_mi_music_play);
                setX(getLeft());
                showHideViewMusicMi.showHide(true);
                close = false;
                break;
            case STATE_STOPPED:
                imgPause.setImageResource(R.drawable.ic_mi_music_pause);
                tvNameAppPlayMusic.setText("");
                tvNamePlay.setText("");
                tvSinger.setText("");
                imgDetailMusic.setImageDrawable(null);
                break;
            case STATE_PAUSED:
                imgPause.setImageResource(R.drawable.ic_mi_music_pause);
                break;
        }
    }

    @Override
    public void contentChange(String artist, String track, Bitmap thumb, String packageName) {
        imgDetailMusic.setScaleType(ImageView.ScaleType.CENTER_CROP);
        tvNamePlay.setText(track);
        tvSinger.setText(artist);
        if (packageName == null || packageName.isEmpty()) {
            imgDetailMusic.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_music));
            tvNameAppPlayMusic.setText(getContext().getString(R.string.unknown));
        } else {
            String appName = MethodUtils.getAppNameFromPackageName(getContext(), packageName);
            if (appName.equals("(unknown)")) {
                tvNameAppPlayMusic.setText(getContext().getString(R.string.unknown));
            } else {
                tvNameAppPlayMusic.setText(appName);
            }
            if (thumb != null && !thumb.isRecycled()) {
                imgDetailMusic.setImageBitmap(thumb);
            } else {
                imgDetailMusic.setImageDrawable(MethodUtils.getIconFromPackageName(getContext(), packageName));
            }
            imgDetailMusic.setShapeAppearanceModel(imgDetailMusic.getShapeAppearanceModel().toBuilder().setAllCorners(CornerFamily.ROUNDED, DensityUtils.pxFromDp(getContext(), 16)).build());
        }


    }

    @Override
    public void checkPermissionNotificationListener(boolean isCheck) {
        checkNotyPermission(isCheck);
    }

    @Override
    public void timeMediaChange(int state) {

    }

    public void checkNotyPermission(boolean isCheck) {
        if (groupMusic != null && groupNoty != null) {
            if (isCheck) {
                groupMusic.setVisibility(VISIBLE);
                groupNoty.setVisibility(INVISIBLE);
            } else {
                groupMusic.setVisibility(INVISIBLE);
                groupNoty.setVisibility(VISIBLE);
            }
        }
    }

    public interface MiMusicListener {
        void onClinkVerify();


    }


}

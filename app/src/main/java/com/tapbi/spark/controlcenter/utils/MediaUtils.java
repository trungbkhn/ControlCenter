package com.tapbi.spark.controlcenter.utils;

import static android.media.session.PlaybackState.STATE_PAUSED;
import static android.media.session.PlaybackState.STATE_PLAYING;
import static android.media.session.PlaybackState.STATE_STOPPED;

import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.models.InfoTimeMedia;
import com.tapbi.spark.controlcenter.feature.controlios14.manager.AudioManagerUtils;
import com.tapbi.spark.controlcenter.service.NotificationListener;

import java.util.List;

import timber.log.Timber;

public class MediaUtils extends MediaController.Callback implements MediaSessionManager.OnActiveSessionsChangedListener {

    private final Context context;
    private final IMediaListener iMediaListener;
    private final Handler handler = new Handler(Looper.getMainLooper());
    public MediaController mediaController;
    private MediaSessionManager mediaSessionManager;
    private ComponentName componentName;
    private String artist = "", track = "";
    private Bitmap thumb;


    private boolean isPlay = false;
    private String packageName = "";

    private InfoTimeMedia infoTimeMedia = new InfoTimeMedia(0, 0);
    private final Runnable runnable = () -> {
        List<MediaController> activeSessions = getActiveSessions();
        if (activeSessions == null || activeSessions.isEmpty()) {
            clearMedia();
            return;
        }
        MediaController mediaController = processMediaControllers(activeSessions);
        if (mediaController == null) {
            clearMedia();
        }


    };


    public MediaUtils(Context context, IMediaListener iMediaListener) {
        this.context = context;
        this.iMediaListener = iMediaListener;
        init();
    }

    private List<MediaController> getActiveSessions() {
        try {
            return mediaSessionManager.getActiveSessions(componentName);
        } catch (Exception e) {
            // Log the exception if needed
            return null;
        }
    }

    private MediaController processMediaControllers(List<MediaController> mediaControllers) {
        for (int i = 0; i < mediaControllers.size(); i++) {
            MediaController controller = mediaControllers.get(i);
            if (controller != null) {
                boolean lister = processMedia(controller);
                if (lister) {
                    return controller;
                }
            }

        }
        return null;
    }

    private boolean processMedia(MediaController mediaControllers) {
        if (AppUtils.isPackageInstalled(mediaControllers.getPackageName(), context.getPackageManager())) {
            mediaController = mediaControllers;
            packageName = mediaControllers.getPackageName();
            mediaControllers.registerCallback(MediaUtils.this);
            updateStateMedia(mediaControllers.getPlaybackState());
            setInfoMusicWhenMetadataChanged(mediaControllers.getMetadata());
            return true;
        }
        return false;
    }

    private boolean isCheckMediaControllerPlay(MediaController mediaController) {
        try {
            if (mediaController == null) {
                return false;
            }
            PlaybackState playbackState = mediaController.getPlaybackState();
            if (playbackState != null) {
                return playbackState.getState() == STATE_PLAYING;
            }
            return false;
        } catch (Exception e) {
            return false;
        }

    }

    private void init() {
        track = context.getString(R.string.audio_music);
        try {
            mediaSessionManager = (MediaSessionManager) context.getSystemService(Context.MEDIA_SESSION_SERVICE);
            componentName = new ComponentName(context, NotificationListener.class);
            mediaSessionManager.addOnActiveSessionsChangedListener(this, componentName);
            getMediaController();
        } catch (SecurityException e) {
            //SettingUtils.intentToPermissionActivity(context);
        } catch (Exception e) {
            Timber.d(e);
        }
    }


    private void getMediaController() {
        boolean isPermissionNotificationListener = SettingUtils.checkPermissionNotificationListener(context);
        if (isPermissionNotificationListener) {
            unRegisterController();
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 400);
        }
        iMediaListener.checkPermissionNotificationListener(isPermissionNotificationListener);

    }

    private void unRegisterController() {
        if (mediaController != null) {

            mediaController.unregisterCallback(this);
        }
    }

    private void clearMedia() {
        artist = "";
        track = context.getString(R.string.audio_music);
        packageName = "";
        if (thumb != null) {
//            thumb.recycle();
            thumb = null;
        }
        updateMusicControl();
        iMediaListener.stateChange(STATE_STOPPED);
        iMediaListener.contentChange(artist, track, thumb, packageName);
    }

    @Override
    public void onMetadataChanged(@Nullable MediaMetadata metadata) {
        super.onMetadataChanged(metadata);
        setInfoMusicWhenMetadataChanged(metadata);
    }

    @Override
    public void onSessionDestroyed() {
        super.onSessionDestroyed();
        clearMedia();
    }

    @Override
    public void onPlaybackStateChanged(@Nullable PlaybackState state) {
        super.onPlaybackStateChanged(state);
        updateStateMedia(state);
    }

    private void updateStateMedia(PlaybackState state) {
        if (state != null) {
            if (state.getState() == STATE_PLAYING) {
                iMediaListener.stateChange(STATE_PLAYING);
            } else {
                iMediaListener.stateChange(STATE_PAUSED);
            }
            infoTimeMedia.setCurrentPosition(state.getPosition());
        }else {
            clearMedia();
        }
    }


    //mediaSessionManager
    @Override
    public void onActiveSessionsChanged(@Nullable List<MediaController> controllers) {
        getMediaController();
    }


    private void setInfoMusicWhenMetadataChanged(@Nullable MediaMetadata metadata) {
        if (metadata != null) {
            artist = "";
            track = context.getString(R.string.audio_music);
            String artistCurrent = null;
            String titleCurrent = null;
            try {
                artistCurrent = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST);
            } catch (Exception ignored) {
            }
            try {
                titleCurrent = metadata.getString(MediaMetadata.METADATA_KEY_TITLE);
            } catch (Exception ignored) {
            }

            if (artistCurrent != null && !artistCurrent.isEmpty()) {
                artist = artistCurrent;
            }

            if (titleCurrent != null && !titleCurrent.isEmpty()) {
                track = titleCurrent;
            }
            Bitmap art = null;
            try {
                art = metadata.getBitmap(MediaMetadata.METADATA_KEY_ART);
            } catch (Exception e) {
                Timber.d(e);
            }
            if (art != null) {
                thumb = art;
            } else {
                Bitmap albumArt = null;
                try {
                    albumArt = metadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART);
                } catch (Exception e) {
                    Timber.d(e);
                }
                thumb = albumArt;
            }
            try {
                infoTimeMedia.setDuration(metadata.getLong(MediaMetadata.METADATA_KEY_DURATION));
            } catch (Exception e) {
                infoTimeMedia.setDuration(0);
            }
            if (mediaController != null) {
                PlaybackState playbackState = mediaController.getPlaybackState();
                if (playbackState != null) {
                    iMediaListener.timeMediaChange(playbackState.getState());
                } else {
                    iMediaListener.timeMediaChange(STATE_PAUSED);
                }
            } else {
                iMediaListener.timeMediaChange(STATE_PAUSED);
            }
            iMediaListener.contentChange(artist, track, thumb, packageName);
        }else {
            clearMedia();
        }


    }

    public void controlMusic(int action) {
        if (mediaController != null) {
            AudioManagerUtils.getInstance(context).controlMusic(mediaController, action);
        }
    }

    public InfoTimeMedia getInfoTimeMedia() {
        return infoTimeMedia;
    }

    public void updateMusicControl() {
        if (AudioManagerUtils.getInstance(context).isMusicPlay() && mediaController != null) {
            iMediaListener.stateChange(STATE_PLAYING);
        } else if (!AudioManagerUtils.getInstance(context).isMusicPlay()) {
            iMediaListener.stateChange(STATE_PAUSED);
        }
    }

    public boolean openAppMusic() {
        if (mediaController != null && mediaController.getPackageName() != null) {
            SettingUtils.intentOtherApp(context, mediaController.getPackageName());
            return true;
        }
        return false;
    }

    public void releaseListener() {
        if (mediaSessionManager != null) {
            mediaSessionManager.removeOnActiveSessionsChangedListener(this);
        }

        if (mediaController != null) {
            mediaController.unregisterCallback(this);
        }
    }

    public boolean isMusicActive() {
        return AudioManagerUtils.getInstance(context).isMusicPlay();
    }

    public interface IMediaListener {
        void stateChange(int state);

        void contentChange(String artist, String track, Bitmap thumb, String packageName);


        void checkPermissionNotificationListener(boolean isCheck);

        void timeMediaChange(int state);

    }
}

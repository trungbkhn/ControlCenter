package com.tapbi.spark.controlcenter.feature.controlios14.manager;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.session.MediaController;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.KeyEvent;

import com.tapbi.spark.controlcenter.ui.transparent.TransparentActivity;

import timber.log.Timber;


public class AudioManagerUtils {

    public static final int PREVIOUS = 88;
    public static final int NEXT = 87;
    public static final int PLAYPAUSE = 79;
    public static boolean isChangingRingerMode = false;
    private static AudioManagerUtils instance;
    private static AudioManager audioManager;
    private Handler handler = new Handler(Looper.getMainLooper());

    private Runnable runnable = () -> isChangingRingerMode = false;

    public static AudioManagerUtils getInstance(Context ctx) {
        if (instance == null) {
            instance = new AudioManagerUtils();
            audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        }
        return instance;
    }

    public void settingSilient() {
        int n = audioManager.getRingerMode();
        switch (n) {
            case AudioManager.RINGER_MODE_NORMAL:
                try {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case AudioManager.RINGER_MODE_SILENT:
                try {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                try {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public int getRingerMode() {
        try {
            return audioManager.getRingerMode();
        } catch (Exception e) {
            Timber.d(e);
            return 0;
        }
    }

    public int getVolume() {
        try {
            return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        } catch (Exception e) {
            Timber.d(e);
            return 0;
        }
    }

    public void setVolume(int value) {
        try {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, value, 0);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public int getMaxVolume() {
        try {
            return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        } catch (Exception e) {
            Timber.d(e);
            return 10;
        }
    }

    public int getVolumeAlarm() {
        try {
            return audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        } catch (Exception e) {
            Timber.d(e);
            return 0;
        }
    }

    public void setVolumeAlarm(int value) {
        try {
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, value, 0);
        } catch (Exception e) {
            Timber.d(e);
        }
    }

    public int getMaxVolumeAlarm() {
        try {
            return audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        } catch (Exception e) {
            Timber.d(e);
            return 10;
        }
    }

    public int getMinVolumeAlarm() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                return audioManager.getStreamMinVolume(AudioManager.STREAM_ALARM);
            } else {
                return 0;
            }
        } catch (Exception e) {
            Timber.d(e);
            return 10;
        }
    }


    public int getVolumeRingtone() {
        try {
            return audioManager.getStreamVolume(AudioManager.STREAM_RING);
        } catch (Exception e) {
            Timber.d(e);
            return 0;
        }
    }

    public void setVolumeRingtone(int value) {
        try {
            audioManager.setStreamVolume(AudioManager.STREAM_RING, value, 0);
        } catch (Exception e) {
            Timber.d(e);
        }
    }


    public void setRingMode(int value) {
        try {
            isChangingRingerMode = true;
            audioManager.setRingerMode(value);
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 1000);
        } catch (Exception e) {
            Timber.d(e);
        }
    }


    public int getMaxVolumeRingtone() {
        try {
            return audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        } catch (Exception e) {
            Timber.d(e);
            return 0;
        }
    }

    public boolean isMusicPlay() {
        try {
            return audioManager.isMusicActive();
        } catch (Exception e) {
            Timber.d(e);
            return false;
        }

    }

    public void controlMusic(MediaController mediaController, int action) {
        if (mediaController != null) {
            long uptimeMillis = SystemClock.uptimeMillis();
            mediaController.dispatchMediaButtonEvent(new KeyEvent(uptimeMillis, uptimeMillis, KeyEvent.ACTION_DOWN, action, 0));
            long uptimeMillis3 = SystemClock.uptimeMillis();
            mediaController.dispatchMediaButtonEvent(new KeyEvent(uptimeMillis3, uptimeMillis3, KeyEvent.ACTION_UP, action, 0));
        }
    }

    public void changeVolumeInForeground(Context context, int type, int valueVolume) {
        if (context == null) return;
        Intent intent = new Intent(context, TransparentActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setAction(TransparentActivity.ACTION_CHANGE_VOLUME);
        intent.putExtra(TransparentActivity.KEY_VALUE_VOLUME_CHANGE, valueVolume);
        intent.putExtra(TransparentActivity.KEY_TYPE_VOLUME, type);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

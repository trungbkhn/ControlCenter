package com.tapbi.spark.controlcenter.receiver;

import static com.tapbi.spark.controlcenter.common.Constant.EXTRA_VOLUME_STREAM_TYPE;
import static com.tapbi.spark.controlcenter.common.Constant.EXTRA_VOLUME_STREAM_VALUE;
import static com.tapbi.spark.controlcenter.common.Constant.VOLUME_CHANGED_ACTION;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import timber.log.Timber;


public class VolumeReceiver extends BroadcastReceiver {


    private final IVolumeChange iVolumeChange;

    public VolumeReceiver(IVolumeChange iVolumeChange) {
        this.iVolumeChange = iVolumeChange;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(VOLUME_CHANGED_ACTION)) {
            if (intent.getExtras() != null) {
                int streamType = intent.getExtras().getInt(EXTRA_VOLUME_STREAM_TYPE);
                if (streamType == AudioManager.STREAM_MUSIC) {
                    int volume = intent.getExtras().getInt(EXTRA_VOLUME_STREAM_VALUE);
                    // Do something with the new volume value
                    if (iVolumeChange != null) {
                        iVolumeChange.volumeChange(volume);
                    }
                    intent.getExtras().remove(EXTRA_VOLUME_STREAM_TYPE);
                }
            }


        }

    }

    public interface IVolumeChange {
        void volumeChange(int volume);
    }


}



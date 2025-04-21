package com.tapbi.spark.controlcenter.ui.transparent

import android.app.Activity
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.inputmethod.InputMethodManager
import com.tapbi.spark.controlcenter.feature.controlios14.manager.AudioManagerUtils

class TransparentActivity : Activity() {
    private val DEFAULT_VALUE_INT = -1
    private val handler = Handler(Looper.getMainLooper())
    private var action: String? = ""
    private val runnable = Runnable {
        if (action != null) {
            if (action == ACTION_SHOW_PICK_KEYBOARD) {
                (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).showInputMethodPicker()
            }
        }
    }
    private var countFocus = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        action = intent.action
        if (action != null) {
            handingAction()
        }
    }

    private fun handingAction() {
        if (action == ACTION_CHANGE_TIME_OUT_SCREEN) {
            val valueTimeChange = intent.getIntExtra(KEY_VALUE_TIME_OUT_SCREEN, DEFAULT_VALUE_INT)
            if (valueTimeChange != DEFAULT_VALUE_INT) {
                Settings.System.putInt(
                    contentResolver,
                    Settings.System.SCREEN_OFF_TIMEOUT,
                    valueTimeChange
                )
                finish()
            }
        } else if (action == ACTION_CHANGE_VOLUME) {
            val valueVolume = intent.getIntExtra(KEY_VALUE_VOLUME_CHANGE, DEFAULT_VALUE_INT)
            val typeChange = intent.getIntExtra(KEY_TYPE_VOLUME, DEFAULT_VALUE_INT)
            if (valueVolume != DEFAULT_VALUE_INT && typeChange != DEFAULT_VALUE_INT) {
                when (typeChange) {
                    AudioManager.STREAM_MUSIC -> AudioManagerUtils.getInstance(this).volume =
                        valueVolume

                    AudioManager.STREAM_ALARM -> AudioManagerUtils.getInstance(this).volumeAlarm =
                        valueVolume

                    AudioManager.STREAM_RING -> AudioManagerUtils.getInstance(this).volumeRingtone =
                        valueVolume
                }
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //handler.postDelayed(runnable, 1000);
        handler.postDelayed(runnable, 2000)
    }

    override fun onDestroy() {
        super.onDestroy()
        //Timber.e("hoangld ");
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (action != null && hasFocus) {
            if (action == ACTION_SHOW_PICK_KEYBOARD) {
                countFocus++
                if (countFocus != 1) {
                    //finish();
                }
            }
        }
        //Timber.e("hoangld hasFocus " + hasFocus);
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        //Timber.e("hoangld ");
    }

    companion object {
        var KEY_ACTION = "key_action"
        @JvmField
        var KEY_VALUE_VOLUME_CHANGE = "key_value_volume_change"
        @JvmField
        var KEY_TYPE_VOLUME = "key_type_volume"
        @JvmField
        var KEY_VALUE_TIME_OUT_SCREEN = "key_value_time_out_screen"
        @JvmField
        var ACTION_SHOW_PICK_KEYBOARD = "action_show_pick_keyboard"
        @JvmField
        var ACTION_CHANGE_TIME_OUT_SCREEN = "action_change_time_out_screen"
        @JvmField
        var ACTION_CHANGE_VOLUME = "action_change_volume"
    }
}

package com.tapbi.spark.controlcenter.ui.base

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.eventbus.EventOpen
import com.tapbi.spark.controlcenter.ui.splash.SplashActivity
import com.tapbi.spark.controlcenter.utils.SettingUtils
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

open class BaseConstraintLayout : ConstraintLayout {
    private var callBackIntent: CallBackIntent? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun setCallBackIntent(callBackIntent: CallBackIntent?) {
        this.callBackIntent = callBackIntent
    }

    fun intentAction(action: String?) {
        val intent1 = Intent(action)
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            this.context.startActivity(intent1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        callBackIntent?.success()
    }

    fun checkValueBrightness(value: Int): Int {
        return if (value == Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL) {
            SettingUtils.setModeBrightness(
                context,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
            )
            Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
        } else {
            SettingUtils.setModeBrightness(
                context,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
            )
            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
        }

    }


    fun openSplashApp() {
//        if (App.isStartActivity) {
//            EventBus.getDefault().post(EventOpen(Constant.ACTION_START_SPLASH))
//        } else {
//            val intent = Intent(context, SplashActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            context.startActivity(intent)
//        }
        val intent = Intent(context, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        Handler(Looper.getMainLooper()).postDelayed({
            callBackIntent?.success()
        }, 300)

    }

    interface CallBackIntent {
        fun success()
    }
}
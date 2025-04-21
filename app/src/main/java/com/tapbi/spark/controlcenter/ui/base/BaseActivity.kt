package com.tapbi.spark.controlcenter.ui.base

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.ads.nativetemplates.OnDecorationAds
import com.google.android.ads.nativetemplates.TemplateViewMultiAds
import com.ironman.trueads.admob.ControlAds
import com.ironman.trueads.common.Common
import com.ironman.trueads.multiads.MultiAdsControl
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.common.Constant.IS_FAVORITE_SELECTED
import com.tapbi.spark.controlcenter.common.Constant.IS_ONBOARD_STARED
import com.tapbi.spark.controlcenter.data.local.SharedPreferenceHelper
import com.tapbi.spark.controlcenter.ui.dialog.DialogRequestPermissionWriteSetting
import com.tapbi.spark.controlcenter.utils.LocaleUtils
import com.tapbi.spark.controlcenter.utils.MethodUtils
import com.tapbi.spark.controlcenter.utils.StatusBarUtils
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity() {
    private var dialogPermissionAlertDialog: DialogRequestPermissionWriteSetting? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        StatusBarUtils.setColorStatusBarTransparent(this)
        StatusBarUtils.setColorTextStatusBarBlack(this)
        LocaleUtils.setCurrentResources(this, this)
        super.onCreate(savedInstanceState)
        adjustFontScale(resources.configuration)
    }

    override fun attachBaseContext(newBase: Context) {
        LocaleUtils.setCurrentResources(newBase, this)
        super.attachBaseContext(newBase)
    }

    protected fun showDialog(finishWhenIntentSetting: Boolean, content: String) {
        dialogPermissionAlertDialog?.dismissAllowingStateLoss()
        dialogPermissionAlertDialog =
            MethodUtils.showDialogPermission(this, false, content, finishWhenIntentSetting) {
                dialogPermissionAlertDialog?.dismissAllowingStateLoss()
                onBackPressed()
            }
        dialogPermissionAlertDialog?.dialog?.window?.setDimAmount(0f)
        if (!isFinishing && !isDestroyed) {
            dialogPermissionAlertDialog?.showDialog(
                supportFragmentManager,
                Constant.DIALOG_REQUEST_PERMISSION_WRITE_SETTING
            )
        }

    }

    val isDialogShowing: Boolean
        get() = if (dialogPermissionAlertDialog != null) {
            dialogPermissionAlertDialog?.dialog?.isShowing == true
        } else false

    fun dismissDialog() {
        if (dialogPermissionAlertDialog != null) {
            dialogPermissionAlertDialog?.dismissAllowingStateLoss()
        }
    }

    protected open fun setColorNavigation(color: Int) {
        window.navigationBarColor = resources.getColor(color)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val decorView = window.decorView
            var flags = decorView.systemUiVisibility
            flags = if (MethodUtils.isColorDark(R.color.white)) {
                flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            } else {
                flags and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
            }
            decorView.systemUiVisibility = flags
        }
    }

    protected open fun nextAfterFullScreen() {}
    private fun adjustFontScale(configuration: Configuration) {
        var fontScaleSystem = configuration.fontScale
        if (fontScaleSystem >= 1.1f) {
            fontScaleSystem = 1.1f
        }
        configuration.fontScale = fontScaleSystem
        val metrics = resources.displayMetrics
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(metrics)
        metrics.scaledDensity = configuration.fontScale * metrics.density
        //        int snap = 20;
//        float exactDpi = (metrics.xdpi + metrics.ydpi) / 2;
//        int targetDpi = (int) (Math.round(exactDpi / snap) * snap);
//        metrics.densityDpi = targetDpi;
//        configuration.densityDpi = targetDpi;
        metrics.setTo(metrics)
        baseContext.resources.updateConfiguration(configuration, metrics)
    }

    fun autoLoadsAds() {
        if (ControlAds.admobInitialized) {
            if (!SharedPreferenceHelper.getBoolean(IS_ONBOARD_STARED, false)) {
                Log.d("duongcv", "autoLoadsAds: load onboard")
                autoLoadAdsInternal(
                    listOf(getString(R.string.tag_native_onboard_1)),
                    R.array.admob_native_id_onboard_1,
                    R.array.applovin_id_native_onboard_1
                )
                autoLoadAdsInternal(
                    listOf(getString(R.string.tag_native_onboard_2)),
                    R.array.admob_native_id_onboard_2,
                    R.array.applovin_id_native_onboard_2
                )
                autoLoadAdsInternal(
                    listOf(getString(R.string.tag_native_onboard_full)),
                    R.array.admob_native_id_onboard_full,
                    R.array.applovin_id_native_onboard_full
                )
            }
            if (!SharedPreferenceHelper.getBoolean(IS_FAVORITE_SELECTED, false)) {
                autoLoadAdsInternal(
                    listOf(getString(R.string.tag_native_favorite_theme)),
                    R.array.admob_native_id_favorite_theme,
                    R.array.applovin_id_native_favorite_theme
                )
            }
            autoLoadAdsInternal(
                listOf(getString(R.string.tag_native_user_manual_control)),
                R.array.admob_native_id_user_manual_control,
                R.array.applovin_id_native_user_manual_control
            )
            autoLoadAdsInternal(
                listOf(getString(R.string.tag_native_store_wallpaper)),
                R.array.admob_native_id_store_wallpaper,
                R.array.applovin_id_native_store_wallpaper
            )
            autoLoadAdsInternal(
                listOf(getString(R.string.tag_native_preview_my_theme)),
                R.array.admob_native_id_preview_my_theme,
                R.array.applovin_id_native_preview_my_theme
            )
            autoLoadAdsInternal(
                listOf(getString(R.string.tag_native_choose_style)),
                R.array.admob_native_id_choose_style,
                R.array.applovin_id_native_choose_style
            )
            autoLoadAdsInternal(
                listOf(getString(R.string.tag_native_language)),
                R.array.admob_native_id_language,
                R.array.applovin_id_native_language
            )
            autoLoadAdsInternal(
                listOf(getString(R.string.tag_native_setting_touch)),
                R.array.admob_native_id_setting_touch,
                R.array.applovin_id_native_setting_touch
            )
            autoLoadAdsInternal(
                listOf(getString(R.string.tag_native_request_permission)),
                R.array.admob_native_id_request_permission,
                R.array.applovin_id_native_request_permission
            )

            MultiAdsControl.loadAdsInterstitialDetectInternet(application, this)
        }
    }

    fun autoLoadAdsInternal(tags: List<String>, idAdmob: Int, idApplovin: Int) {
        val mapIds = Common.getMapIdAdmobApplovin(this, idAdmob, idApplovin)
        MultiAdsControl.loadAdsNativeDetectInternet(
            this, tags, mapIds
        )
    }

    protected fun loadAdsNative(
        templateView: TemplateViewMultiAds,
        mapIds: HashMap<String, String>
    ) {
        MultiAdsControl.showNativeAdNoMedia(
            this as AppCompatActivity,
            mapIds,
            templateView,
            null, null, object : OnDecorationAds {
                override fun onDecoration(network: String?) {
                    templateView.getNativeAdView(network)?.apply {
                        setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.color_F3F3F3
                            )
                        )
                    }
                }
            })
    }
}
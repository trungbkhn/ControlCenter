package com.tapbi.spark.controlcenter.feature.controlios14.view

import android.provider.Settings
import com.tapbi.spark.controlcenter.utils.SettingUtils.checkIfLocationOpened
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.databinding.LayoutControlIosPage3Binding
import com.tapbi.spark.controlcenter.feature.controlcenter.utils.DataMobileUtils
import com.tapbi.spark.controlcenter.utils.DensityUtils
import com.tapbi.spark.controlcenter.utils.SettingUtils
import timber.log.Timber

class ControlCenterIOS18Page3 :ConstraintLayout {
    private var isAirplaneModeOn: Boolean = false
    var binding : LayoutControlIosPage3Binding? = null
    private var dataMobileUtils: DataMobileUtils? = null
    constructor(context: Context) : super(context){initView()}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){initView()}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){initView()}

    fun initView(){
        val orientation2 = DensityUtils.getOrientationWindowManager(context)
        context.resources.configuration.orientation = orientation2
        val configuration = context.resources.configuration
        configuration.orientation = orientation2
        val themedContext = context.createConfigurationContext(configuration)
        binding = LayoutControlIosPage3Binding.inflate(LayoutInflater.from(themedContext), this, true)
        dataMobileUtils = DataMobileUtils(App.ins)
        initFirstState()
    }
    private fun initFirstState(){
        updateSync()
        updateViewBluetooth(SettingUtils.isEnableBluetooth(context))
        updateViewWifi(SettingUtils.isEnableWifi(context))
        updateViewAirplane(SettingUtils.isAirplaneModeOn(context))
        updateViewLocation(checkIfLocationOpened())
        val value = Settings.Global.getInt(context.contentResolver, "zen_mode") != 0
        updateDoNotDisturb(value)
    }

    fun updateSync() {
        binding?.actionSync?.setBackgroundState(SettingUtils.isSyncAutomaticallyEnable())
    }

    fun updateViewAirplane(state: Boolean) {
        isAirplaneModeOn = state
        binding?.actionAirplane?.setBackgroundState(state)
    }

    fun updateViewWifi(state: Boolean) {
        binding?.actionWifi?.setBackgroundState(state)
    }

    fun updateViewDataMobile(state: Boolean) {
        binding?.actionData?.setBackgroundState(state && !isAirplaneModeOn)
    }

    fun updateViewBluetooth(state: Boolean) {
        binding?.actionBlueTooth?.setBackgroundState(state)
    }

    fun updateDoNotDisturb(b: Boolean) {
        binding?.actionSilent?.setBackgroundState(b)
    }

    fun updateViewLocation(state: Boolean) {
        binding?.actionLocation?.setBackgroundState(state)
    }

}


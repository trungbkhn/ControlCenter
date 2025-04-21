package com.tapbi.spark.controlcenter.feature.controlios14.view.control.base

import android.content.Context
import android.content.res.Configuration
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.tapbi.spark.controlcenter.data.model.ItemControl
import com.tapbi.spark.controlcenter.eventbus.EventSaveControl
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlCenterIosModel
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.ControlCenterIOSAdapter
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.ControlCenterIOSView
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.CreateItemViewControlCenterIOS
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

open class BaseControlCenterIos : ConstraintLayout {
    @JvmField
    protected var controlCenterIOSAdapter1: ControlCenterIOSAdapter? = null

    @JvmField
    protected var controlCenterIOSAdapter2: ControlCenterIOSAdapter? = null

    @JvmField
    protected var listControl1 = java.util.ArrayList<ControlCenterIosModel>()

    @JvmField
    protected var listControl2 = java.util.ArrayList<ControlCenterIosModel>()

    @JvmField
    protected var orientation = Configuration.ORIENTATION_PORTRAIT

    @JvmField
    protected var itemControl: ItemControl? = null

    @JvmField
    protected var typeface: Typeface? = null

    @JvmField
    protected var createItemViewControlCenterIOS: CreateItemViewControlCenterIOS? = null

    constructor(context: Context) : super(context){
        init(context)
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){
        init(context)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){
        init(context)
    }


    open fun init(context: Context) {}
    open fun removeItem(pos: Int) {}
    open fun changeData(itemControl: ItemControl) {}
    open fun reloadTheme(itemControl: ItemControl) {}
    open fun initViewControl() {}
    open fun initAdapterControl(
        recyclerView: RecyclerView, controlCenterIOSAdapter: ControlCenterIOSAdapter?,
        list: java.util.ArrayList<ControlCenterIosModel>
    ) {
    }

    open fun setBgNew() {}
    open fun show(){}
    open fun hide(){}

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        EventBus.getDefault().register(this)
    }
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        unregister()
        removeAllViews()
        EventBus.getDefault().unregister(this)
    }

    fun setOnControlCenterListener(onControlCenterListener: ControlCenterIOSView.OnControlCenterListener?) {
        controlCenterIOSAdapter1?.createItemViewControlCenterIOS?.setOnControlCenterListener(
            onControlCenterListener
        )
        controlCenterIOSAdapter2?.createItemViewControlCenterIOS?.setOnControlCenterListener(
            onControlCenterListener
        )
    }
    open fun updateSync(){
        controlCenterIOSAdapter1?.createItemViewControlCenterIOS?.updateSync()
        controlCenterIOSAdapter2?.createItemViewControlCenterIOS?.updateSync()
    }
    open fun updateVolume(volume: Int) {
        controlCenterIOSAdapter1?.createItemViewControlCenterIOS?.updateVolume(volume)
        controlCenterIOSAdapter2?.createItemViewControlCenterIOS?.updateVolume(volume)
    }
    fun setHideViewExpand() {
        controlCenterIOSAdapter1?.createItemViewControlCenterIOS?.setHideViewExpand()
        controlCenterIOSAdapter2?.createItemViewControlCenterIOS?.setHideViewExpand()
    }
    fun setChangeBattery(isChange: Boolean, lever: Int) {
        controlCenterIOSAdapter1?.createItemViewControlCenterIOS?.setChangeBattery(isChange, lever)
        controlCenterIOSAdapter2?.createItemViewControlCenterIOS?.setChangeBattery(isChange, lever)
    }
    fun setonSignalsChange(lever: Int) {
        controlCenterIOSAdapter1?.createItemViewControlCenterIOS?.setonSignalsChange(lever)
        controlCenterIOSAdapter2?.createItemViewControlCenterIOS?.setonSignalsChange(lever)
    }
    open fun updateViewBluetooth(state: Boolean) {
        controlCenterIOSAdapter1?.createItemViewControlCenterIOS?.updateViewBluetooth(state)
        controlCenterIOSAdapter2?.createItemViewControlCenterIOS?.updateViewBluetooth(state)
    }

    fun setStatesLowPower(state: Boolean) {
        controlCenterIOSAdapter1?.createItemViewControlCenterIOS?.setStatesLowPower(state)
        controlCenterIOSAdapter2?.createItemViewControlCenterIOS?.setStatesLowPower(state)
    }

    open fun updateViewLocation(state: Boolean) {
        controlCenterIOSAdapter1?.createItemViewControlCenterIOS?.updateViewLocation(state)
        controlCenterIOSAdapter2?.createItemViewControlCenterIOS?.updateViewLocation(state)
    }

    open fun updateViewDataMobile(state: Boolean) {
        controlCenterIOSAdapter1?.createItemViewControlCenterIOS?.updateViewDataMobile(state)
        controlCenterIOSAdapter2?.createItemViewControlCenterIOS?.updateViewDataMobile(state)
    }

    open fun updateViewWifi(state: Boolean) {
        controlCenterIOSAdapter1?.createItemViewControlCenterIOS?.updateViewWifi(state)
        controlCenterIOSAdapter2?.createItemViewControlCenterIOS?.updateViewWifi(state)
    }

    fun updateViewDarkMode(state: Boolean) {
        controlCenterIOSAdapter1?.createItemViewControlCenterIOS?.updateViewDarkMode(state)
        controlCenterIOSAdapter2?.createItemViewControlCenterIOS?.updateViewDarkMode(state)
    }


    fun updateViewFlashlight(state: Boolean) {
        controlCenterIOSAdapter1?.createItemViewControlCenterIOS?.updateFlash(state)
        controlCenterIOSAdapter2?.createItemViewControlCenterIOS?.updateFlash(state)
    }

    open fun updateViewAirplane(state: Boolean) {
        controlCenterIOSAdapter1?.createItemViewControlCenterIOS?.updateViewAirplane(state)
        controlCenterIOSAdapter2?.createItemViewControlCenterIOS?.updateViewAirplane(state)
    }


    fun updateProcessBrightness() {
        controlCenterIOSAdapter1?.createItemViewControlCenterIOS?.updateProcessBrightness()
        controlCenterIOSAdapter2?.createItemViewControlCenterIOS?.updateProcessBrightness()
    }

    open fun updateDoNotDisturb(b: Boolean) {
        controlCenterIOSAdapter1?.createItemViewControlCenterIOS?.setDoNotDisturb(b)
        controlCenterIOSAdapter2?.createItemViewControlCenterIOS?.setDoNotDisturb(b)
    }

    fun updateStateSim() {
        controlCenterIOSAdapter1?.createItemViewControlCenterIOS?.updateStateSim()
        controlCenterIOSAdapter2?.createItemViewControlCenterIOS?.updateStateSim()
    }

    fun unregister() {
        controlCenterIOSAdapter1?.createItemViewControlCenterIOS?.unregister()
        controlCenterIOSAdapter2?.createItemViewControlCenterIOS?.unregister()
    }
    protected fun eventUpdate(eventSaveControl: EventSaveControl) {
        CreateItemViewControlCenterIOS.isTouchDarkmore = false
        controlCenterIOSAdapter1?.createItemViewControlCenterIOS?.eventUpdate(eventSaveControl)
        controlCenterIOSAdapter2?.createItemViewControlCenterIOS?.eventUpdate(eventSaveControl)
    }


}
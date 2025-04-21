package com.tapbi.spark.controlcenter.feature.mishade.view

import android.content.Context
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.feature.controlcenter.adapter.AdapterSettingMiControl.TouchItemView
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CloseMiControlView
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.ControlCenterIOSView.OnControlCenterListener

import com.tapbi.spark.controlcenter.feature.mishade.adapter.AdapterRccTopShade
import com.tapbi.spark.controlcenter.feature.mishade.adapter.ViewPagerAdapterNotification
import com.tapbi.spark.controlcenter.feature.mishade.interfaces.DataAction
import com.tapbi.spark.controlcenter.utils.DensityUtils
import com.tapbi.spark.controlcenter.utils.helper.rcvhepler.NpaGridLayoutManager
import com.tapbi.spark.controlcenter.utils.helper.rcvhepler.NpaLinearLayoutManager

class LayoutActionMiShade : ConstraintLayout, CloseMiControlView, TouchItemView {
    var adapterRccTopShade: AdapterRccTopShade? = null
    var adapterRccBotShade: AdapterRccTopShade? = null
    var adapterRccCenterShade: AdapterRccTopShade? = null
    private var w = 0f
    private var oldDownY = 0f
    private var newDownY = 0f
    private var totalDownY = 0f
    private var updateHeight: ViewPagerAdapterNotification.UpdateHeight? = null
    private var onControlCenterListener: OnControlCenterListener? = null
    private var percent = 0f
    var isExpand: Boolean = false
    private var wItem = 0f
    private var dataAction: DataAction? = null
    private var orientation = 0
    var binding: CommonNotificationBinding? = null

    fun setOnControlCenterListener(onControlCenterListener: OnControlCenterListener?) {
        this.onControlCenterListener = onControlCenterListener
    }

    constructor(context: Context) : super(context) {
        init()
    }

    private var pos = 0

    constructor(context: Context, dataAction: DataAction?, pos: Int) : super(context) {
        this.dataAction = dataAction
        this.pos = pos
        init()
    }

    fun setCallBackUpdateHeight(updateHeight: ViewPagerAdapterNotification.UpdateHeight?) {
        this.updateHeight = updateHeight
    }

    init {
        orientation = DensityUtils.getOrientationWindowManager(context)
        widthHeight
        binding = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            NotificationPortraitBinding(
                DataBindingUtil.inflate(
                    LayoutInflater.from(context),
                    R.layout.fragment_notification_one,
                    this,
                    true
                )
            )
        } else {
            NotificationLandscapeBinding(
                DataBindingUtil.inflate(
                    LayoutInflater.from(context),
                    R.layout.fragment_notification_one_land,
                    this,
                    true
                )
            )
        }

    }


    private fun init() {
        setUpAdapter()
        setUpRcc()
    }


    fun updateActionView(action: String?, b: Boolean) {
        adapterRccTopShade?.updateActionView(action, b)
        adapterRccBotShade?.updateActionView(action, b)
        adapterRccCenterShade?.updateActionView(action, b)

    }




    private fun setUpRcc() {
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding?.rccTop?.layoutManager = NpaLinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        } else {
            binding?.rccTop?.layoutManager = NpaGridLayoutManager(
                context,
                if (orientation == Configuration.ORIENTATION_PORTRAIT) 4 else 3
            )
        }
        binding?.rccTop?.adapter = adapterRccTopShade
        binding?.rccTop?.animation = null
        binding?.rccTop?.itemAnimator = null

        binding?.rccBot?.layoutManager = NpaGridLayoutManager(
            context,
            if (orientation == Configuration.ORIENTATION_PORTRAIT) 4 else 3
        )
        binding?.rccBot?.adapter = adapterRccBotShade
        binding?.rccBot?.animation = null
        binding?.rccBot?.itemAnimator = null

        binding?.rccCenter?.layoutManager = NpaGridLayoutManager(
            context,
            if (orientation == Configuration.ORIENTATION_PORTRAIT) 4 else 3
        )
        binding?.rccCenter?.adapter = adapterRccCenterShade
        binding?.rccCenter?.animation = null
        binding?.rccCenter?.itemAnimator = null
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) return super.dispatchTouchEvent(ev)
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                oldDownY = ev.rawY
                totalDownY = (wItem * 2) + wItem * 0.2f * 5f
                if (pos == 1) return super.dispatchTouchEvent(ev)
            }

            MotionEvent.ACTION_MOVE -> {
                newDownY = ev.rawY
                val lineDistance = newDownY - oldDownY
                val newPercent = (lineDistance / totalDownY * 100f)
                var newHeight: Int

                //                if (newDownY - oldDownY < -15 || newDownY - oldDownY > 15) {
                if (pos == 1) return super.dispatchTouchEvent(ev)
                //                }
                if (isExpand) {
                    percent = 100 + newPercent
                    if (newDownY - oldDownY < -5) {
                        adapterRccTopShade?.valueF = percent
                        adapterRccBotShade?.valueF = percent
                        adapterRccCenterShade?.valueF = percent
                    }
                } else {
                    percent = newPercent
                    if (newDownY - oldDownY > 5) {
                        adapterRccTopShade?.valueF = percent
                        adapterRccBotShade?.valueF = percent
                        adapterRccCenterShade?.valueF = percent
                    }
                }
                if (percent > 100) {
                    percent = 100f
                    isExpand = true
                } else if (percent < 0) {
                    isExpand = false
                    percent = 0f
                }
                newHeight = (wItem + percent / 100 * totalDownY).toInt()
                if (newHeight < wItem + wItem * 0.2f) {
                    newHeight = (wItem + wItem * 0.2f).toInt()
                }
                updateHeight?.updateNewHeight(newHeight, percent / 100f, false)
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                adapterRccTopShade?.let {
                    if (it.valueF > 50) {
                        if (it.valueF != 100f) {
                            isExpand = true
                            updateHeight?.animationView(true)
                        }
                    } else {
                        if (it.valueF != 0f) {
                            isExpand = false
                            updateHeight?.animationView(false)
                        }
                    }
                }

            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun setUpAdapter() {
        if (pos == 0) {

            dataAction?.let {
                adapterRccTopShade = AdapterRccTopShade(
                    it.dataRcc1(),
                    this,
                    if (orientation == Configuration.ORIENTATION_PORTRAIT) w * 0.94f else w * 0.35f,
                    this,
                    it.dataRcc1().size,
                    orientation,
                    0
                )
                adapterRccBotShade = AdapterRccTopShade(
                    it.dataRcc2(),
                    this,
                    if (orientation == Configuration.ORIENTATION_PORTRAIT) w * 0.94f else w * 0.35f,
                    this,
                    it.dataRcc2().size,
                    orientation,
                    1
                )
                adapterRccCenterShade = AdapterRccTopShade(
                    it.dataRcc3(),
                    this,
                    if (orientation == Configuration.ORIENTATION_PORTRAIT) w * 0.94f else w * 0.35f,
                    this,
                    it.dataRcc3().size,
                    orientation,
                    2
                )
            }

            adapterRccTopShade?.valueF =
                if (orientation == Configuration.ORIENTATION_PORTRAIT) 0f else 100f

            adapterRccBotShade?.valueF =
                if (orientation == Configuration.ORIENTATION_PORTRAIT) 0f else 100f

            adapterRccCenterShade?.valueF =
                if (orientation == Configuration.ORIENTATION_PORTRAIT) 0f else 100f
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                adapterRccTopShade?.valueF = 0f
                adapterRccCenterShade?.valueF = 0f
                adapterRccBotShade?.valueF = 0f
            }
        } else {
            dataAction?.let {
                adapterRccTopShade = AdapterRccTopShade(
                    it.dataRcc4(),
                    this,
                    if (orientation == Configuration.ORIENTATION_PORTRAIT) w * 0.94f else w * 0.35f,
                    this,
                    it.dataRcc4().size,
                    orientation,
                    0
                )
                adapterRccBotShade = AdapterRccTopShade(
                    it.dataRcc5(),
                    this,
                    if (orientation == Configuration.ORIENTATION_PORTRAIT) w * 0.94f else (w * 0.35f),
                    this,
                    it.dataRcc5().size,
                    orientation,
                    1
                )

                adapterRccCenterShade = AdapterRccTopShade(
                    it.dataRcc6(),
                    this,
                    if (orientation == Configuration.ORIENTATION_PORTRAIT) w * 0.94f else w * 0.35f,
                    this,
                    it.dataRcc6().size,
                    orientation,
                    2
                )
            }
            adapterRccTopShade?.valueF = 100f

            adapterRccBotShade?.valueF = 100f
            //rccBot.setVisibility(orientation == Configuration.ORIENTATION_PORTRAIT?INVISIBLE: VISIBLE);

            adapterRccCenterShade?.valueF = 100f
            //rccCenter.setVisibility(orientation == Configuration.ORIENTATION_PORTRAIT ? INVISIBLE: VISIBLE);
        }
    }


    private val widthHeight: Unit
        get() {
            App.widthHeightScreenCurrent.let {
                w = it.w.toFloat()
                wItem = w * 0.94f / 5
            }

        }


    fun unregisterItemBaseRcv() {
        adapterRccTopShade?.unregister()

        adapterRccBotShade?.unregister()

        adapterRccCenterShade?.unregister()
    }


    override fun close() {
        onControlCenterListener?.onClose()
    }

    override fun down(value: Float) {
    }

    override fun up() {
    }
}
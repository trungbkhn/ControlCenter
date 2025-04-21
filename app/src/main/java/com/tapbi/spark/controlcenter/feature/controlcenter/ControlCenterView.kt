package com.tapbi.spark.controlcenter.feature.controlcenter

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.data.repository.ThemeHelper
import com.tapbi.spark.controlcenter.databinding.LayoutControlCenterViewBinding
import com.tapbi.spark.controlcenter.feature.controlcenter.view.control.micenter.ControlMiCenterView
import com.tapbi.spark.controlcenter.feature.controlcenter.view.control.pixel.ControlPixel
import com.tapbi.spark.controlcenter.feature.controlcenter.view.control.samsung.ControlSamSung
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.ControlCenterIOSView
import com.tapbi.spark.controlcenter.ui.base.BaseConstraintLayout
import timber.log.Timber

class ControlCenterView : BaseConstraintLayout {

    private var binding =
        LayoutControlCenterViewBinding.inflate(LayoutInflater.from(context), this, true)

    private var controlMiCenterView: ControlMiCenterView? = null

    private var controlPixel: ControlPixel? = null
    private var controlSamSung: ControlSamSung? = null
    private var typeChoose: Int = 0


    private var onControlCenterListener: ControlCenterIOSView.OnControlCenterListener? = null

    fun setOnControlCenterListener(onControlCenterListener: ControlCenterIOSView.OnControlCenterListener) {
        this.onControlCenterListener = onControlCenterListener
        controlMiCenterView?.setOnControlCenterListener(onControlCenterListener)
        controlPixel?.setOnControlCenterListener(onControlCenterListener)
        controlSamSung?.setOnControlCenterListener(onControlCenterListener)
    }


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        typeChoose = ThemeHelper.itemControl.idCategory
        when (typeChoose) {
            Constant.VALUE_CONTROL_CENTER -> {
                controlMiCenterView = ControlMiCenterView(context)
                binding.flAddView.addView(controlMiCenterView)
            }

            Constant.VALUE_PIXEL -> {
                controlPixel = ControlPixel(context)
                binding.flAddView.addView(controlPixel)
            }

            Constant.VALUE_SAMSUNG -> {
                controlSamSung = ControlSamSung(context)
                binding.flAddView.addView(controlSamSung)
            }
        }
    }

    fun updateTextTitle() {
        if (typeChoose == Constant.VALUE_CONTROL_CENTER) {
            controlMiCenterView?.setTextTitle()
        }
    }

    fun updateIcon() {
        if (typeChoose == Constant.VALUE_CONTROL_CENTER) {
            if (App.tinyDB.getBoolean(Constant.ENABLE_CONTROL, Constant.DEFAULT_ENABLE_CONTROL)) {
                controlMiCenterView?.let {
                    if (it.adapterSettingMiControl != null) {
                        it.reloadRcc()
                    }
                }

            }
        }
    }

    fun setUpBg() {
        when (typeChoose) {
            Constant.VALUE_CONTROL_CENTER -> {
                controlMiCenterView?.setUpBg()
            }

            Constant.VALUE_PIXEL -> {
                controlPixel?.setUpBg()
            }

            Constant.VALUE_SAMSUNG -> {
                controlSamSung?.setUpBg()
            }
        }

    }

    fun show() {
        controlMiCenterView?.show()
        controlSamSung?.show()
    }

    fun updateActionView(action: String, b: Boolean) {
        when (typeChoose) {
            Constant.VALUE_CONTROL_CENTER -> {
                controlMiCenterView?.updateActionView(action, b)
            }

            Constant.VALUE_PIXEL -> {
                controlPixel?.updateActionView(action, b)
            }

            else -> {
                controlSamSung?.updateActionView(action, b)
            }
        }

    }


    fun updateActionViewExpand(action: String, b: Boolean) {
        when (typeChoose) {
            Constant.VALUE_CONTROL_CENTER -> {
                controlMiCenterView?.updateActionViewExpand(action, b)
            }

            Constant.VALUE_PIXEL -> {
                controlPixel?.updateActionView(action, b)
            }

            else -> {
                controlSamSung?.updateActionView(action, b)
            }


        }

    }

    fun updateActionDataView(b: Boolean) {
        controlMiCenterView?.updateActionViewExpand(
            Constant.STRING_ACTION_DATA_MOBILE,
            b
        )
    }

    fun updateProcessBrightness() {
        when (typeChoose) {
            Constant.VALUE_CONTROL_CENTER -> {
                controlMiCenterView?.updateProcessBrightness()
            }

            Constant.VALUE_PIXEL -> {
                controlPixel?.updateProcessBrightness()
            }

            Constant.VALUE_SAMSUNG -> {
                controlSamSung?.setProgressBrightness()
            }

        }


    }

    fun setChangeBattery(isCharging: Boolean, level: Int, pct: Float) {
        if (typeChoose == Constant.VALUE_SAMSUNG) {
            controlSamSung?.setBattery(isCharging, level, pct)
        }
    }

    fun updateVolume(volume: Int) {
        controlSamSung?.setProgressVolume(volume)
    }


}
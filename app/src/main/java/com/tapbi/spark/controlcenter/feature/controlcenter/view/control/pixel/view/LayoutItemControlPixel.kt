package com.tapbi.spark.controlcenter.feature.controlcenter.view.control.pixel.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.data.repository.ThemeHelper
import com.tapbi.spark.controlcenter.databinding.LayoutItemControlPixelBinding
import com.tapbi.spark.controlcenter.feature.controlcenter.adapter.AdapterControlPixel
import com.tapbi.spark.controlcenter.feature.controlcenter.adapter.AdapterSettingsSamSungControl
import com.tapbi.spark.controlcenter.feature.controlios14.model.InfoSystem
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.ControlCenterIOSView
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.ControlCenterViewOS
import com.tapbi.spark.controlcenter.ui.base.BaseConstraintLayout

class LayoutItemControlPixel : BaseConstraintLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var closeListener: ControlCenterIOSView.OnControlCenterListener? = null

    fun setCloseMiControlView(closeMiControlView: ControlCenterIOSView.OnControlCenterListener) {
        this.closeListener = closeMiControlView
        adapterPixel?.setListener { closeMiControlView.onClose() }
        adapterSS?.setListener{ closeMiControlView.onClose() }
    }

    private var binding =
        LayoutItemControlPixelBinding.inflate(LayoutInflater.from(context), this, true)
    private var adapterPixel: AdapterControlPixel? = null
    private var adapterSS: AdapterSettingsSamSungControl? = null

    var listControl = mutableListOf<InfoSystem>()
        set(value) {
            field = value
            adapterPixel?.setData(value)
            adapterSS?.setData(value)
        }


    init {
        if (ThemeHelper.itemControl.idCategory == Constant.VALUE_PIXEL) {
            adapterPixel = AdapterControlPixel()
            adapterPixel?.setListener {
                closeListener?.onClose()
            }
            val girdLayoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 2)
            binding.recyclerViewAction.layoutManager = girdLayoutManager
            binding.recyclerViewAction.adapter = adapterPixel
        }else{
            adapterSS = AdapterSettingsSamSungControl()
            adapterSS?.let {
                closeListener?.onClose()
            }
            val girdLayoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 3)
            binding.recyclerViewAction.layoutManager = girdLayoutManager
            binding.recyclerViewAction.adapter = adapterSS

        }


    }

    fun updateActionView(action: String, b: Boolean) {
        adapterPixel?.updateActionView(action, b)
        adapterSS?.updateActionView(action,b)
    }

    fun clearViewList() {
        adapterPixel?.clearViewList()
        adapterSS?.clearViewList()
    }


}
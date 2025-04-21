package com.tapbi.spark.controlcenter.ui.main.customcontrol.preview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tapbi.spark.controlcenter.App.Companion.tinyDB
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.data.model.ItemControl
import com.tapbi.spark.controlcenter.databinding.LayoutPreviewControlMiShadeBinding
import com.tapbi.spark.controlcenter.feature.controlios14.model.InfoSystem
import com.tapbi.spark.controlcenter.ui.main.customcontrol.preview.adapter.PreviewControlMiShadeAdapter

class PreviewControlMiShade : ConstraintLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    var binding =
        LayoutPreviewControlMiShadeBinding.inflate(LayoutInflater.from(context), this, true)

    private var infoSystems: MutableList<InfoSystem> = mutableListOf()

    private val adapterPreviewControlMiShade: PreviewControlMiShadeAdapter by lazy {
        PreviewControlMiShadeAdapter()
    }

    var itemControl: ItemControl? = null
        set(value) {
            field = value
            post {
                field?.let {it1->
                    it1.miShade?.let {
                    adapterPreviewControlMiShade.setDataMiShade(
                        infoSystems,
                        it1, width
                    )
                }
                }

            }
            value?.let { control ->
                if (value?.id  == null || value.id < 10000){
                    Glide.with(context)
                        .load("file:///android_asset/themes/${control.idCategory}/${control.id}/${control.background}")
                        .into(binding.imBackground)
                }
            }
        }

    init {
        infoSystems = Gson().fromJson(
            tinyDB.getString(Constant.ACTION_SHADE_SELECT),
            object : TypeToken<List<InfoSystem?>?>() {}.type
        ) ?: mutableListOf()
        binding.rvControl.itemAnimator = null
        binding.rvControl.animation = null
        binding.rvControl.adapter = adapterPreviewControlMiShade
    }

    private fun notifyAdapter(payload: Any) {
        itemControl?.let {
            adapterPreviewControlMiShade.setItemMiShade(it, payload)
        }
    }

    // Các phương thức cập nhật thông số và thông báo adapter
    fun setIconSettingsControl(icon: String) {
        itemControl?.miShade?.iconControl = icon
        notifyAdapter(adapterPreviewControlMiShade.PAYLOAD_UPDATE_BACKGROUND)
    }

    fun setColorSelectControl(color: String) {
        itemControl?.miShade?.backgroundColorSelectControl = color
        notifyAdapter(adapterPreviewControlMiShade.PAYLOAD_UPDATE_COLOR)
    }

    fun setFontControl(font: String) {
        itemControl?.font = font
        notifyAdapter(adapterPreviewControlMiShade.PAYLOAD_UPDATE_FONT)
    }

    fun setConnerBackgroundControl(corner: Float) {
        itemControl?.miShade?.cornerBackgroundControl = corner / 100
        notifyAdapter(adapterPreviewControlMiShade.PAYLOAD_UPDATE_BACKGROUND)
    }


}
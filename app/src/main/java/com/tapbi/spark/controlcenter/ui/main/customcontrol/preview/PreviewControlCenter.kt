package com.tapbi.spark.controlcenter.ui.main.customcontrol.preview

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.data.model.GroupColor
import com.tapbi.spark.controlcenter.data.model.ItemControl
import com.tapbi.spark.controlcenter.databinding.LayoutPreviewControlCenterBinding
import com.tapbi.spark.controlcenter.feature.controlios14.model.InfoSystem
import com.tapbi.spark.controlcenter.ui.custom.SyncFontTextView
import com.tapbi.spark.controlcenter.ui.main.customcontrol.preview.adapter.PreviewControlMiShadeAdapter

class PreviewControlCenter : ConstraintLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    var binding =
        LayoutPreviewControlCenterBinding.inflate(LayoutInflater.from(context), this, true)

    private val adapterPreviewControlMiShade: PreviewControlMiShadeAdapter by lazy {
        PreviewControlMiShadeAdapter()
    }
    private var infoSystems: MutableList<InfoSystem> = mutableListOf()
    var itemControl: ItemControl? = null
        set(value) {
            field = value
            value?.let {control ->
            post {
                field?.controlCenter?.let {
                    adapterPreviewControlMiShade.setDataControlCenter(
                        infoSystems,
                        control,
                        width
                    )
                }
            }

            if (value?.id  == null || value.id < 10000){
                Glide.with(context)
                    .load("file:///android_asset/themes/${control.idCategory}/${control.id}/${control.background}")
                    .into(binding.imBackground)
            }
            control.controlCenter?.let {
                binding.ic4g.setColorFilter(Color.parseColor(it.backgroundColorSelectControl1))
                binding.icWifi.setColorFilter(Color.parseColor(it.backgroundColorSelectControl2))
                binding.icBluetooth.setColorFilter(Color.parseColor(it.backgroundColorSelectControl2))
                binding.icFlashlight.setColorFilter(Color.parseColor(it.backgroundColorSelectControl3))
                binding.icA.setBackground(it.iconControl)
                binding.icA.setRatioRadius(it.cornerBackgroundControl)
                binding.icA.setBackgroundC(Color.parseColor(it.backgroundColorDefaultControl))
            }

            }
        }

    init {
        infoSystems = Gson().fromJson(
            App.tinyDB.getString(Constant.ACTION_SHADE_SELECT),
            object : TypeToken<List<InfoSystem?>?>() {}.type
        ) ?: mutableListOf()
        binding.rvControl.itemAnimator = null
        binding.rvControl.animation = null
        binding.rvControl.adapter = adapterPreviewControlMiShade
        binding.icA.setIcon(R.drawable.ic_a)

    }

    private fun notifyAdapter(payload: Any) {
        itemControl?.let {
            adapterPreviewControlMiShade.setItemControlCenter(it, payload)
        }
    }

    fun setIconSettingsControl(icon: String) {
        itemControl?.controlCenter?.iconControl = icon
        binding.icA.setBackground(icon)
        notifyAdapter(adapterPreviewControlMiShade.PAYLOAD_UPDATE_BACKGROUND)
    }

    fun setColorSelectControl(groupColor: GroupColor) {
        itemControl?.controlCenter?.let {
            it.backgroundColorSelectControl1 =
                String.format("#%06X", (0xFFFFFF and groupColor.color1))
            it.backgroundColorSelectControl2 =
                String.format("#%06X", (0xFFFFFF and groupColor.color2))
            it.backgroundColorSelectControl3 =
                String.format("#%06X", (0xFFFFFF and groupColor.color3))
        }
        binding.ic4g.setColorFilter(groupColor.color1)
        binding.icWifi.setColorFilter(groupColor.color2)
        binding.icBluetooth.setColorFilter(groupColor.color2)
        binding.icFlashlight.setColorFilter(groupColor.color3)
        notifyAdapter(adapterPreviewControlMiShade.PAYLOAD_UPDATE_COLOR)
    }

    fun setFontControl(font: String) {
        itemControl?.font = font
        val typeface =
            Typeface.createFromAsset(context.assets, Constant.FOLDER_FONT_CONTROL_ASSETS + font)
        binding.layoutPreview.let { preview ->
            for (i in 0 until preview.childCount) {
                val childView = preview.getChildAt(i)
                if (childView is SyncFontTextView) {
                    childView.setTypefaceFont(typeface)
                }
            }
        }

        notifyAdapter(adapterPreviewControlMiShade.PAYLOAD_UPDATE_FONT)
    }

    fun setConnerBackgroundControl(corner: Float) {
        itemControl?.controlCenter?.cornerBackgroundControl = corner / 100
        binding.icA.setRatioRadius(corner / 100)
        notifyAdapter(adapterPreviewControlMiShade.PAYLOAD_UPDATE_BACKGROUND)
    }

}
package com.tapbi.spark.controlcenter.ui.main.customcontrol.preview

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.data.model.ItemControl
import com.tapbi.spark.controlcenter.data.repository.ThemesRepository
import com.tapbi.spark.controlcenter.databinding.LayoutPreviewControlOsBinding
import com.tapbi.spark.controlcenter.feature.controlios14.model.ControlCustomize
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlCenterIosModel
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlSettingIosModel
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel
import com.tapbi.spark.controlcenter.feature.controlios14.view.SpanSize
import com.tapbi.spark.controlcenter.feature.controlios14.view.SpannedGridLayoutManager
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.ControlCenterIOSAdapter
import timber.log.Timber
class PreviewControlOS : ConstraintLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    var binding = LayoutPreviewControlOsBinding.inflate(LayoutInflater.from(context), this, true)


    var itemControl: ItemControl? = null
        set(value) {
            field = value
            controlCenterIOSAdapter1?.changeListControl(field?.controlCenterOS?.listControlCenterStyleVerticalTop as ArrayList<ControlCenterIosModel>?)
            Timber.e("NVQ 123456 ${value?.id}")
            if (value?.id  == null || value.id < 10000){
                Glide.with(context)
                    .load("file:///android_asset/themes/" + itemControl?.idCategory + "/" + itemControl?.id + "/" + itemControl?.background)
                    .into(binding.imBg)
            }
        }

    private var controlCenterIOSAdapter1: ControlCenterIOSAdapter? = null


    init {
        controlCenterIOSAdapter1 = ControlCenterIOSAdapter(
            context,
            DataSetupViewControlModel(
                6004,
                6000,
                Typeface.createFromAsset(context.assets, "fontControls/sf_pro_text_regular.ttf")
            )
        )
        val spannedGridLayoutManager =
            SpannedGridLayoutManager(SpannedGridLayoutManager.Orientation.VERTICAL, 16)
        binding.rvControl.setLayoutManager(spannedGridLayoutManager)
        binding.rvControl.setAdapter(controlCenterIOSAdapter1)
        spannedGridLayoutManager.spanSizeLookup =
            SpannedGridLayoutManager.SpanSizeLookup { integer: Int? ->
                val model = controlCenterIOSAdapter1!!.listControl[integer!!]
                SpanSize(model.ratioWidght, model.ratioHeight)
            }


    }

    fun setIconSettingsControl(icon: String) {
        val newIcon = if (icon == Constant.STRING_ICON_SHADE_1) Constant.SHAPE_DEFAULT else icon

        itemControl?.controlCenterOS?.apply {
            listControlCenterStyleVerticalTop?.let { list ->
                list.forEachIndexed { index, item ->
                    item.controlSettingIosModel?.apply {
                        if (backgroundImageViewItem != null) {
                            backgroundImageViewItem = newIcon
                            controlCenterIOSAdapter1?.notifyItemChanged(index, list[index])
                        }
                    }
                }
            }

            controlCenterStyleHorizontal?.apply {
                updateIconForList(listControlLeft, newIcon)
                updateIconForList(listControlRight, newIcon)
            }
        }
    }



    private fun updateIconForList(list: List<ControlCenterIosModel>?, icon: String) {
        list?.forEach {
            it.controlSettingIosModel?.apply {
                if (backgroundImageViewItem != null) backgroundImageViewItem = icon
            }
        }
    }

    fun setColorSelectControl(color: String) {
        itemControl?.controlCenterOS?.apply {
            listControlCenterStyleVerticalTop?.let {list->
                list.forEachIndexed { index, item ->
                    item.controlSettingIosModel?.apply {
                        if (backgroundImageViewItem != null) {
                            backgroundColorSelectViewItem = color
                            controlCenterIOSAdapter1?.notifyItemChanged(index, list[index])
                        }
                    }
                }
            }
            controlCenterStyleHorizontal?.apply {
                updateColorForList(listControlLeft, color)
                updateColorForList(listControlRight, color)
            }
        }
    }

    private fun updateColorForList(list: List<ControlCenterIosModel>?, color: String) {
        list?.forEach {
            it.controlSettingIosModel?.apply {
                if (backgroundColorSelectViewItem != null) backgroundColorSelectViewItem = color
            }
        }
    }

    fun setFontControl(font: String) {
        itemControl?.apply {
            this.font = font
            controlCenterIOSAdapter1?.setFontTypeFace(
                if (font != "font_default") Typeface.createFromAsset(
                    context.assets, Constant.PATH_FOLDER_FONT + font
                ) else null
            )
        }
    }

    fun setConnerBackgroundControl(corner: Float) {
        val newCorner = corner / 100

        itemControl?.controlCenterOS?.apply {
            listControlCenterStyleVerticalTop?.let {list->
                list.forEachIndexed { index, item ->
                    item.controlSettingIosModel?.apply {
                        if (backgroundImageViewItem != null) {
                            cornerBackgroundViewItem = newCorner
                            controlCenterIOSAdapter1?.notifyItemChanged(index, list[index])
                        }
                    }
                }
            }
            controlCenterStyleHorizontal?.apply {
                updateCornerForList(listControlLeft, newCorner)
                updateCornerForList(listControlRight, newCorner)
            }
        }
    }

    private fun updateCornerForList(list: List<ControlCenterIosModel>?, corner: Float) {
        list?.forEach {
            it.controlSettingIosModel?.apply {
                if (backgroundColorSelectViewItem != null) cornerBackgroundViewItem = corner
            }
        }
    }


    fun setListControlSettings(listAppControl: ArrayList<*>) {
        itemControl?.controlCenterOS?.listControlCenterStyleVerticalTop?.let { rawList ->
            val list = ThemesRepository.updateListControlVsApp(rawList, listAppControl)
            controlCenterIOSAdapter1?.changeListControl(list)
        }
        itemControl?.controlCenterOS?.controlCenterStyleHorizontal?.listControlRight?.let { rawList ->
            ThemesRepository.updateListControlVsApp(rawList, listAppControl)
        }

    }


}
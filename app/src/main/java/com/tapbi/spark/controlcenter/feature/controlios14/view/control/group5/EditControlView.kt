package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5

import android.content.Context
import android.util.AttributeSet
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlSettingIosModel
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.ControlCenterViewIOS18
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ImageBase
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper
import java.io.File

class EditControlView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ImageBase(context, attrs) {
     var itemControlCenterListener: ControlCenterViewIOS18.ItemControlCenterListener? = null

    constructor(context: Context,controlSettingIosModel : ControlSettingIosModel?) : this(context){
        this.controlSettingIosModel = controlSettingIosModel
        init()
    }


    private fun init() {
        setImageResource(R.drawable.plus)
        setBg(false)
    }
    fun setBg(b: Boolean) {
        changeIsSelect(b)
    }
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val paddingIcon = (w * 0.267).toInt()
        setPadding(paddingIcon, paddingIcon, paddingIcon, paddingIcon)
    }
    private var lastClick = 0L
    override fun click() {
        val time = System.currentTimeMillis()
        if ( time - lastClick > 500){
            lastClick = time
            itemControlCenterListener?.onClickAdd()
        }

    }

    override fun longClick() {
        itemControlCenterListener?.onClickAdd()
    }

    override fun onDown() {

    }

    override fun onUp() {

    }
}
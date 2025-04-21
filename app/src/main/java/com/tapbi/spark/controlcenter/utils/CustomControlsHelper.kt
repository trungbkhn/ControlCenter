package com.tapbi.spark.controlcenter.utils

import android.graphics.Color
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant.POS_IOS
import com.tapbi.spark.controlcenter.common.Constant.POS_MI_CONTROL
import com.tapbi.spark.controlcenter.common.Constant.POS_MI_SHADE
import com.tapbi.spark.controlcenter.common.Constant.TYPE_SINGLE_COLOR
import com.tapbi.spark.controlcenter.data.model.GroupColor
import javax.inject.Inject

class CustomControlsHelper  @Inject constructor() {
    var currentTypeCustom = POS_IOS
    var currentFont: Typeface = Typeface.DEFAULT
    var currentTypeColor = TYPE_SINGLE_COLOR
    val groupColorDefault = GroupColor(Color.parseColor("#03924D"),Color.parseColor("#2073F8"),Color.parseColor("#EF8023"))





    companion object {
        lateinit var instance: CustomControlsHelper
        private fun getCurrentInstance(): CustomControlsHelper {
            if (::instance.isInitialized) {
                return instance
            } else {
                instance = CustomControlsHelper()
                return instance
            }
        }
        fun setCurrentFontPreview(newFont: Typeface?){
            newFont?.let {
                getCurrentInstance().currentFont = it
            }
        }

        fun getCurrentFontPreview(): Typeface{
            return getCurrentInstance().currentFont
        }
        fun getCurrentTypeColor(): Int{
            return getCurrentInstance().currentTypeColor
        }
        fun setCurrentTypeColor(newTypeColor: Int){
            getCurrentInstance().currentTypeColor = newTypeColor
        }
        fun getCurrentTypeCustom(): Int{
            return getCurrentInstance().currentTypeCustom
        }
        fun setCurrentTypeCustom(newTypeCustom: Int){
            getCurrentInstance().currentTypeCustom = newTypeCustom
        }
        fun getDefaultGroupColor(): GroupColor{
            return getCurrentInstance().groupColorDefault
        }
        fun getColorUnselect(): Int{
            return when(getCurrentTypeCustom()){
                POS_MI_CONTROL ->{
                    Color.parseColor("#33F2F2F2")
                }
                POS_MI_SHADE ->{
                    Color.parseColor("#E8E8E8")
                }
                else ->{
                    Color.parseColor("#E8E8E8")
                }
            }
        }

    }

    init {
        instance = this
        currentFont = ResourcesCompat.getFont(App.ins, R.font.roboto_regular) ?: Typeface.DEFAULT
    }
}
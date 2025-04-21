package com.tapbi.spark.controlcenter.ui.custom

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class SyncFontTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {


    fun setTypefaceFont(typeface: Typeface) {
        this.typeface = typeface

    }
}
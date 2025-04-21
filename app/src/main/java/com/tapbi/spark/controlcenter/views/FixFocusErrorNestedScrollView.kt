package com.tapbi.spark.controlcenter.views

import android.content.Context
import android.util.AttributeSet
import androidx.core.widget.NestedScrollView

class FixFocusErrorNestedScrollView(
    context: Context,
    attrs: AttributeSet?
) : NestedScrollView(context, attrs) {
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        try {
            super.onSizeChanged(w, h, oldw, oldh)
        } catch (ignore: Exception) {
        }
    }
}

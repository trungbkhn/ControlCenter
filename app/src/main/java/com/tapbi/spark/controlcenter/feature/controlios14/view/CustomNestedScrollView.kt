package com.tapbi.spark.controlcenter.feature.controlios14.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.widget.NestedScrollView

class CustomNestedScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : NestedScrollView(context, attrs, defStyleAttr) {

    var isScrollEnabled: Boolean = true

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return if (isScrollEnabled) {
            super.onInterceptTouchEvent(ev)
        } else {
            false
        }
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return if (isScrollEnabled) {
            super.onTouchEvent(ev)
        } else {
            false
        }
    }
}

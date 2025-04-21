package com.tapbi.spark.controlcenter.feature.mishade.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class NoScrollViewPager : ViewPager {
    private var enabled: Boolean = false
    private var haveTwoPage: Boolean = false

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        this.enabled = true
        this.haveTwoPage = true
    }

    constructor(context: Context) : super(context)

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (this.enabled && haveTwoPage) {
            try {
                return super.onTouchEvent(event)
            } catch (ex: IllegalArgumentException) {
                ex.printStackTrace()
            }
        }
        return false
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (this.enabled && haveTwoPage) {
            try {
                return super.onInterceptTouchEvent(event)
            } catch (ex: IllegalArgumentException) {
                ex.printStackTrace()
            }
        }
        return false
    }

    fun setPagingEnabled(enabled: Boolean) {
        this.enabled = enabled
    }

    fun setHaveTwoPage(haveTwoPage: Boolean) {
        this.haveTwoPage = haveTwoPage
    }
}

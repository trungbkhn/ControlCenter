package com.tapbi.spark.controlcenter.ui.custom

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

class CustomLayoutManager(
    context: Context,
    private var isScrollEnabled: Boolean = true
) : LinearLayoutManager(context) {

    override fun canScrollVertically(): Boolean {
        return isScrollEnabled && super.canScrollVertically()
    }

    override fun canScrollHorizontally(): Boolean {
        return isScrollEnabled && super.canScrollHorizontally()
    }

    fun setScrollEnabled(enabled: Boolean) {
        isScrollEnabled = enabled
    }
}

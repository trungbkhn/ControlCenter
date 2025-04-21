package com.tapbi.spark.controlcenter.views.helper

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tapbi.spark.controlcenter.utils.MethodUtils

class BottomMarginItemDecoration(var margin: Float) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter?.itemCount ?: 0
        outRect.set(0, 0, 0, 0)


        if (position == itemCount - 1) {
            outRect.bottom = MethodUtils.dpToPx(margin)
        }
    }
}
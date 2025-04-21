package com.tapbi.spark.controlcenter.feature.mishade.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class NoScrollRecycler : RecyclerView {
    constructor(context: Context?) : super(context!!)

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    )

    constructor(context: Context?, attrs: AttributeSet?, style: Int) : super(
        context!!, attrs, style
    )

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        //        if (ev.getAction() == MotionEvent.ACTION_MOVE)
//            return true;

        return super.dispatchTouchEvent(ev)
    }
}
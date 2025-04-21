package com.tapbi.spark.controlcenter.views

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.tapbi.spark.controlcenter.R

class ViewToastTextNoty : LinearLayout {
    private var tvToast: TextView? = null
    private val handlerShow = Handler(Looper.getMainLooper())
    private val runnableHide = Runnable { visibility = GONE }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
        visibility = GONE
    }

    private fun init(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.layout_toast_in_noty, this, true)
        tvToast = findViewById(R.id.tvToast)
        visibility = GONE
    }

    fun setContentToast(contentToast: String?) {
        handlerShow.removeCallbacks(runnableHide)
        visibility = VISIBLE
        tvToast!!.text = contentToast
        handlerShow.postDelayed(runnableHide, 1000)
    }
}
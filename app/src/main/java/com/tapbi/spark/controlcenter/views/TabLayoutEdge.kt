package com.tapbi.spark.controlcenter.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.databinding.LayoutTabEdgeBinding
import com.tapbi.spark.controlcenter.ui.main.edgetriggers.SettingTouchFragment.TabEdge

class TabLayoutEdge : LinearLayout {
    private var binding: LayoutTabEdgeBinding? = null
    private var tabEdgeSelected = TabEdge.TOP
    private var onTabListener: OnTabListener? = null

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    fun setOnTabListener(onTabListener: OnTabListener?) {
        this.onTabListener = onTabListener
    }

    private fun initView() {
        binding = LayoutTabEdgeBinding.inflate(
            LayoutInflater.from(
                context
            ), this, true
        )
        binding!!.tvTop.setTextColor(ContextCompat.getColor(context, R.color.color_007AFF))
        binding!!.tvTop.setOnClickListener { _: View? -> updateView(TabEdge.TOP) }
        binding!!.tvLeft.setOnClickListener { _: View? -> updateView(TabEdge.LEFT) }
        binding!!.tvRight.setOnClickListener { _: View? -> updateView(TabEdge.RIGHT) }
        binding!!.tvBottom.setOnClickListener { _: View? -> updateView(TabEdge.BOTTOM) }
    }

    private fun updateView(tabEdgeSelected: TabEdge) {
        if (this.tabEdgeSelected == tabEdgeSelected) {
            return
        }
        this.tabEdgeSelected = tabEdgeSelected
        val textColorNormal = ContextCompat.getColor(context, R.color.color_77777B)
        binding!!.tvTop.setTextColor(textColorNormal)
        binding!!.tvLeft.setTextColor(textColorNormal)
        binding!!.tvRight.setTextColor(textColorNormal)
        binding!!.tvBottom.setTextColor(textColorNormal)
        when (tabEdgeSelected) {
            TabEdge.TOP -> {
                binding!!.tvTop.setTextColor(ContextCompat.getColor(context, R.color.color_007AFF))
                if (onTabListener != null) {
                    onTabListener!!.onTop()
                }
            }

            TabEdge.LEFT -> {
                binding!!.tvLeft.setTextColor(ContextCompat.getColor(context, R.color.color_007AFF))
                if (onTabListener != null) {
                    onTabListener!!.onLeft()
                }
            }

            TabEdge.RIGHT -> {
                binding!!.tvRight.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.color_007AFF
                    )
                )
                if (onTabListener != null) {
                    onTabListener!!.onRight()
                }
            }

            TabEdge.BOTTOM -> {
                binding!!.tvBottom.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.color_007AFF
                    )
                )
                if (onTabListener != null) {
                    onTabListener!!.onBottom()
                }
            }
        }
    }

    interface OnTabListener {
        fun onTop()
        fun onLeft()
        fun onRight()
        fun onBottom()
    }
}
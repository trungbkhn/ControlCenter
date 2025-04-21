package com.tapbi.spark.controlcenter.ui.main.focus.add

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.adapter.FocusAddAdapter
import com.tapbi.spark.controlcenter.data.model.FocusIOS
import com.tapbi.spark.controlcenter.databinding.LayoutAddFocusBinding

class AddFocusLayout : ConstraintLayout, View.OnClickListener {
    private var binding: LayoutAddFocusBinding? = null
    private var clickListener: ClickListener? = null
    private var focusAddAdapter: FocusAddAdapter? = null
    fun setClickListener(clickListener: ClickListener?) {
        this.clickListener = clickListener
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_add_focus, this, true)
        initView()
        listener()
    }

    private fun initView() {
        focusAddAdapter = FocusAddAdapter()
        binding?.rvFocus?.itemAnimator = null
        binding?.rvFocus?.adapter = focusAddAdapter
        focusAddAdapter?.setClickListener { focusIOS: FocusIOS? ->
            clickListener?.onFocusClick(
                focusIOS
            )
        }
    }

    fun setListFocus(list: List<FocusIOS?>?) {
        if (focusAddAdapter != null) {
            focusAddAdapter?.setList(list)
        }
    }

    fun scrollToTop() {
        binding?.rvFocus?.scrollToPosition(0)
    }

    private fun listener() {
        binding?.tvCancel?.setOnClickListener(this)
    }

    @SuppressLint("NonConstantResourceId")
    override fun onClick(view: View) {
        when (view.id) {
            R.id.tv_cancel -> clickListener?.onCancel()
        }
    }

    interface ClickListener {
        fun onCancel()
        fun onFocusClick(focusIOS: FocusIOS?)
    }
}
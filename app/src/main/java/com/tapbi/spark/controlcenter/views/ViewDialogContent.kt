package com.tapbi.spark.controlcenter.views

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.tapbi.spark.controlcenter.databinding.LayoutToastContentBinding
import com.tapbi.spark.controlcenter.utils.SettingUtils

class ViewDialogContent(context: Context) : FrameLayout(context) {

    private var binding: LayoutToastContentBinding
    private var listener: Listener? = null

    interface Listener {
        fun onClose()
    }

    init {
        binding = LayoutToastContentBinding.inflate(LayoutInflater.from(context), this, true)
        binding.root.setOnClickListener {
            setHide()
        }

        binding.tvOpenSystem.setOnClickListener {
            listener?.onClose()
            setHide()
            SettingUtils.intentSetting(context)
        }

    }

    fun show() {

    }

    fun setHide() {
        visibility = GONE
    }

    fun setShow(listener: Listener) {
        this.listener = listener
        visibility = VISIBLE
    }

}
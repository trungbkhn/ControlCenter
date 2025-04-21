package com.tapbi.spark.controlcenter.ui.dialog

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.LinearLayout
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.databinding.DialogRequestPermissionWriteSettingBinding
import com.tapbi.spark.controlcenter.ui.base.BaseBindingDialogFragment
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper

open class DialogRequestPermissionWriteSetting :
    BaseBindingDialogFragment<DialogRequestPermissionWriteSettingBinding>() {
    private var content: String = ""
    private var btnOk: String = ""
    private var btnNotNow: String = ""
    public var isShow = false

    var listener: ClickListener? = null

    fun setDialogListener(listener: ClickListener?) {
        this.listener = listener
    }

    interface ClickListener {
        fun onClickOke()
        fun onClickCancel()
        fun onBackPress()
    }

    override val layoutId: Int
        get() = R.layout.dialog_request_permission_write_setting

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        initView()
        binding.btnNotNow.setOnClickListener {
            ViewHelper.preventTwoClick(it, 500)
            listener?.onClickCancel()
        }
        binding.btnOk.setOnClickListener {
            ViewHelper.preventTwoClick(it, 500)
            listener?.onClickOke()
        }
        if (dialog != null) {
            dialog!!.setOnKeyListener { _: DialogInterface?, keyCode: Int, event: KeyEvent ->
                if (keyCode == KeyEvent.KEYCODE_BACK
                    && event.action == KeyEvent.ACTION_UP
                ) {
                    listener?.onBackPress()
                    return@setOnKeyListener true
                }
                false
            }
        }

    }


    private fun initView() {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }


    fun setText(content: String, yes: String, no: String) {
        this.content = content
        this.btnOk = yes
        this.btnNotNow = no

    }

    private fun updateView() {
        try {
            binding.tvContent.text = content
            binding.btnOk.text = btnOk
            if (btnNotNow.isEmpty()) {
                binding.btnNotNow.visibility = View.INVISIBLE
            } else {
                binding.btnNotNow.visibility = View.VISIBLE
                binding.btnNotNow.text = btnNotNow
            }
        } catch (_: Exception) {
        }
    }


    override fun onResume() {
        super.onResume()
        isShow = true
        updateView()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        isShow = false
    }

}
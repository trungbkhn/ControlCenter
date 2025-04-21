package com.tapbi.spark.controlcenter.ui.dialog

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.databinding.DialogLottieClickBinding
import com.tapbi.spark.controlcenter.ui.base.BaseBindingDialogFragment

class DialogLottieClick : BaseBindingDialogFragment<DialogLottieClickBinding>() {
    override val layoutId: Int
        get() = R.layout.dialog_lottie_click


    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        dialog?.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        binding.root.setOnClickListener {
            dismiss()
        }
    }
}
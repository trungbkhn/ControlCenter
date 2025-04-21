package com.tapbi.spark.controlcenter.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.common.models.MessageEvent
import com.tapbi.spark.controlcenter.databinding.DialogAutostartBinding
import com.tapbi.spark.controlcenter.ui.base.BaseBindingDialogFragment
import com.tapbi.spark.controlcenter.utils.XiaomiUtilities
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper

class DialogAutoStartXiaomi : BaseBindingDialogFragment<DialogAutostartBinding>() {


    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        initView()
        initListener()
    }

    private fun initListener() {
        binding.tvSetAutoStart.setOnClickListener {
            ViewHelper.preventTwoClick(it)
            XiaomiUtilities.goToXiaomiPermissionsAutoStart(requireContext())
        }
    }


    override fun onResume() {
        super.onResume()
        //Timber.e("hoangld: " + XiaomiUtilities.isCustomPermissionGranted(XiaomiUtilities.OP_AUTO_START) + " / "  + XiaomiUtilities.needAutoStartup(requireContext()))
        dismiss()
        mMainViewModel.messageEventLiveEvent.postValue(
            MessageEvent(
                Constant.TYPE_AUTO_START, true
            )
        )
    }

    override val layoutId: Int
        get() = R.layout.dialog_autostart

    private fun initView() {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }
}
package com.tapbi.spark.controlcenter.ui.dialog

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.LinearLayout
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.databinding.DialogPermissionDataUsageBinding
import com.tapbi.spark.controlcenter.ui.base.BaseBindingDialogFragment
import com.tapbi.spark.controlcenter.utils.PermissionUtils.isAccessGranted
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper

class DialogPermissionUsage : BaseBindingDialogFragment<DialogPermissionDataUsageBinding>() {
    @JvmField
    var navigateFragment = ""
    override val layoutId: Int
        get() = R.layout.dialog_permission_data_usage

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        initView()
        initListener()
    }

    override fun onResume() {
        super.onResume()
        if (isAccessGranted(requireContext())) {
            dismiss()
            if (navigateFragment.isNotEmpty()) {
                if (navigateFragment == Constant.EDIT_AUTO_APP) {
                    mMainViewModel.openEditAutomationApp.postValue(true)
                } else {
                    mMainViewModel.openNewAutomationApp.postValue(true)
                }
            }
        }
    }

    private fun initListener() {
        binding.tvGoSetting.setOnClickListener { view: View? ->
            ViewHelper.preventTwoClick(view)
            try {
                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            } catch (e: Exception) {
                try {
                    startActivity(Intent(Settings.ACTION_SETTINGS))
                } catch (ignored: Exception) {
                }
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
}

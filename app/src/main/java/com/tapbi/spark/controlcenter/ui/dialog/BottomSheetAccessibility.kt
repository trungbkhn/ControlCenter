package com.tapbi.spark.controlcenter.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.databinding.DialogANoteOnPrivacyBinding
import com.tapbi.spark.controlcenter.ui.base.BaseBindingBottomSheetDialogFragment
import com.tapbi.spark.controlcenter.ui.main.MainViewModel
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper
import com.tapbi.spark.controlcenter.utils.PermissionUtils


class BottomSheetAccessibility :
    BaseBindingBottomSheetDialogFragment<DialogANoteOnPrivacyBinding, MainViewModel>() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        ViewHelper.setUpWrapHeight(dialog)
        return dialog
    }

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        binding.tvBoost.setOnClickListener {
            ViewHelper.preventTwoClick(it, 500)
//            mainViewModel.messageEventAccessibility.postValue(
//                MessageEvent(
//                    Constant.TYPE_ACCEESSIBILITY, true
//                )
//            )
            dismissAllowingStateLoss()
            ViewHelper.preventTwoClick(it, 600)
            PermissionUtils.openSettingsAccessibility(context)
        }
        binding.tvCancel.setOnClickListener {
            ViewHelper.preventTwoClick(it, 500)
            dismissAllowingStateLoss()
        }

    }

//    override fun onResume() {
//        super.onResume()
//        if (SettingUtils.isAccessibilitySettingsOn(requireContext())) {
//            dismiss()
//        }
//    }

    override fun getViewModel(): Class<MainViewModel> {
        return MainViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.dialog_a_note_on_privacy

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }
}
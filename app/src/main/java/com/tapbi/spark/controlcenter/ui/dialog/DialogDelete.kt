package com.tapbi.spark.controlcenter.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.common.Constant.KEY_IS_DELETE_FOCUS
import com.tapbi.spark.controlcenter.data.model.FocusIOS
import com.tapbi.spark.controlcenter.databinding.DialogDeleteBinding
import com.tapbi.spark.controlcenter.ui.base.BaseBindingDialogFragment
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper
import timber.log.Timber

class DialogDelete(private val clickListener: ClickListener) :
    BaseBindingDialogFragment<DialogDeleteBinding>() {
    private var focusIOS: FocusIOS? = null

    private var typeDelete = ""
    fun setFocusIOS(focusIOS: FocusIOS?) {
        this.focusIOS = focusIOS
    }

    override val layoutId: Int
        get() = R.layout.dialog_delete

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
//        if (savedInstanceState != null) {
//            focusIOS = Gson().fromJson(
//                savedInstanceState.getString(Constant.ITEM_FOCUS_DETAIL_DIALOG),
//                object : TypeToken<FocusIOS?>() {}.type
//            )
//        }


        initView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
//        outState.putString(Constant.ITEM_FOCUS_DETAIL_DIALOG, Gson().toJson(focusIOS))
    }

    override fun onPause() {
        super.onPause()
        dismiss()
    }

    private fun initView() {
        requireActivity().window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setGravity(Gravity.BOTTOM)
        arguments?.let {
            typeDelete = it.getString(KEY_DELETE_DIALOG).toString()
        }
        when (typeDelete) {
            KEY_IS_DELETE_FOCUS -> {
                binding.tvTitleDialog.text = getString(R.string.do_you_want_to_delete_this_focus)
                binding.tvDelete.text = getString(R.string.delete_focus)
            }

            else -> {
                binding.tvTitleDialog.text = getString(R.string.do_you_want_to_delete_this_theme)
                binding.tvDelete.text = getString(R.string.delete_theme)

            }

        }
        initListener()
    }

    private fun initListener() {
        binding.tvCancel.setOnClickListener { v: View? ->
            ViewHelper.preventTwoClick(v)
            dismiss()
        }
        binding.tvDelete.setOnClickListener {
            if (typeDelete == KEY_IS_DELETE_FOCUS) {
                focusIOS?.let {
                    mMainViewModel.deleteFocus(focusIOS)
                    App.presetFocusList.removeAll {
                        it.name == focusIOS?.name
                    }
                    if (focusIOS?.name == App.tinyDB.getString(Constant.FOCUS_START_OLD)) {
                        App.tinyDB.putString(Constant.FOCUS_START_OLD, "")
                    }
                    App.ins.setIsResetLocation(true)
                    App.ins.focusUtils?.sendActionFocus(Constant.TIME_CHANGE, "")
                }

            } else {
                clickListener.onClickDelete()
            }
            dismiss()
        }
    }


    interface ClickListener {
        fun onClickDelete()

    }


    companion object {

        private const val KEY_DELETE_DIALOG = "KEY_DELETE_DIALOG"


        fun newInstance(typeDelete: String, clickListener: ClickListener): DialogDelete {
            val args = Bundle()
            args.putString(KEY_DELETE_DIALOG, typeDelete)
            val dialog = DialogDelete(clickListener)
            dialog.arguments = args
            return dialog
        }
    }
}

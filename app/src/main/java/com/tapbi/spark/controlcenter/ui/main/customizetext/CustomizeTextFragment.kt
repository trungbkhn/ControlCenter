package com.tapbi.spark.controlcenter.ui.main.customizetext

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.tapbi.spark.controlcenter.App.Companion.tinyDB
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.common.models.TitleMiControlChange
import com.tapbi.spark.controlcenter.databinding.ActivityCustomTextBinding
import com.tapbi.spark.controlcenter.ui.base.BaseBindingFragment
import com.tapbi.spark.controlcenter.ui.main.MainActivity
import com.tapbi.spark.controlcenter.utils.StringUtils.isEmptyString

class CustomizeTextFragment :
    BaseBindingFragment<ActivityCustomTextBinding, CustomizeTextViewModel>() {
    private var textOld = ""
    override fun getViewModel(): Class<CustomizeTextViewModel> {
        return CustomizeTextViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.activity_custom_text

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        textOld = tinyDB.getString(Constant.TEXT_SHOW)
    }

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        setUpPaddingStatusBar(binding.layoutParent)
        (requireActivity() as MainActivity).setColorNavigation(R.color.colorPrimary)
        binding.edtCustomText.setText(textOld)
        binding.viewHeader.tvTitle.setText(R.string.text_edit_text)
        binding.viewHeader.imBack.setOnClickListener {
            activity?.onBackPressed()
        }
        if (isEmptyString(binding.edtCustomText.text.toString())) {
            binding.imgClear.visibility = View.INVISIBLE
        } else {
            binding.imgClear.visibility = View.VISIBLE
        }
        binding.imgClear.setOnClickListener { binding.edtCustomText.setText("") }
        binding.edtCustomText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (isEmptyString(s.toString())) {
                    binding.imgClear.visibility = View.INVISIBLE
                } else {
                    binding.imgClear.visibility = View.VISIBLE
                }
                tinyDB.putString(Constant.TEXT_SHOW, s.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (textOld != tinyDB.getString(Constant.TEXT_SHOW)) {
            viewModel.setChangeTextTitleMiControl()
            mainViewModel.titleMiControlChangeLiveEvent.postValue(
                TitleMiControlChange(
                    tinyDB.getString(
                        Constant.TEXT_SHOW
                    )
                )
            )
        }
    }

    override fun onPermissionGranted() {}
}

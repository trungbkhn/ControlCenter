package com.tapbi.spark.controlcenter.ui.main.layout.icon

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.App.Companion.tinyDB
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.common.models.IconNotyEvent
import com.tapbi.spark.controlcenter.common.models.ScaleViewMainEvent
import com.tapbi.spark.controlcenter.databinding.LayoutSelectIconBinding
import com.tapbi.spark.controlcenter.feature.controlcenter.model.InfoIcon
import com.tapbi.spark.controlcenter.ui.base.BaseBindingBottomSheetDialogFragment
import com.tapbi.spark.controlcenter.ui.main.layout.adapter.AdapterIcon
import com.tapbi.spark.controlcenter.ui.main.layout.adapter.AdapterIcon.ClickIcon
import com.tapbi.spark.controlcenter.utils.MethodUtils
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper
import com.tapbi.spark.controlcenter.utils.helper.rcvhepler.NpaLinearLayoutManager
import java.util.Locale

class IconBottomSheetFragment :
    BaseBindingBottomSheetDialogFragment<LayoutSelectIconBinding, IconBottomSheetViewModel>() {
    private var adapterIcon: AdapterIcon? = null
    private var iconAction: String? = null
    private val clickIcon = ClickIcon { icon ->
        iconAction = icon
        tinyDB.putString(Constant.ICON_ACTION_SELECT, icon)
        mainViewModel.iconNotyLiveEvent.postValue(IconNotyEvent(true))
    }

    override fun getViewModel(): Class<IconBottomSheetViewModel> {
        return IconBottomSheetViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.layout_select_icon

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        mainViewModel.scaleViewMainLiveEvent.postValue(ScaleViewMainEvent(false, true, 10f))
        iconAction = App.tinyDB.getString(Constant.ICON_ACTION_SELECT)
        if (iconAction == null || iconAction?.isEmpty() == true) {
            iconAction = Constant.ICON_ACTION_DEFAULT
        }
        findView(view)
        setUpRccAdapter()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialogInterface: DialogInterface? ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog?
            if (activity != null) {
                ViewHelper.setUpWrapHeight(bottomSheetDialog)
            }
            val bottomSheetBehavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(
                binding.root.parent as View
            )
            bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {}
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    mainViewModel.scaleViewMainLiveEvent.postValue(
                        ScaleViewMainEvent(
                            false,
                            false,
                            (1f - (slideOffset + 1) * 0.1 / 2).toFloat()
                        )
                    )
                }
            })
        }
        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainViewModel.scaleViewMainLiveEvent.postValue(ScaleViewMainEvent(true, false, 0f))
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpRccAdapter() {
        adapterIcon = iconAction?.let { AdapterIcon(clickIcon, it) }
        binding.rccIcon.animation = null
        binding.rccIcon.itemAnimator = null
        binding.rccIcon.layoutManager =
            NpaLinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rccIcon.adapter = adapterIcon
        binding.rccIcon.setOnTouchListener { _: View?, _: MotionEvent? ->
            MethodUtils.closeKeyboard(requireContext(), binding.edtSearch)
            false
        }
    }

    private fun findView(view: View?) {
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().isEmpty()) {
                    var poss = -1
                    adapterIcon?.let {
                        for (i in it.stringIcon.addString().indices) {
                            if (it.iconSelect == it.stringIcon.addString()[i].nameRes) {
                                poss = i
                                break
                            }
                            it.setNewData(it.stringIcon.addString(), poss)
                        }
                    }
                    binding.tvNoData.visibility = View.GONE
                } else {
                    val newInfoIcons: MutableList<InfoIcon> = ArrayList()
                    var poss = -1
                    adapterIcon?.let {
                        for (i in it.stringIcon.addString().indices) {
                            if (it.stringIcon.addString()[i].name.lowercase(Locale.getDefault())
                                    .contains(
                                        s.toString().lowercase(
                                            Locale.getDefault()
                                        )
                                    )
                            ) {
                                newInfoIcons.add(it.stringIcon.addString()[i])
                                if (it.iconSelect == it.stringIcon.addString()[i].nameRes) {
                                    poss = newInfoIcons.size - 1
                                    break
                                }
                            }
                        }
                        it.setNewData(newInfoIcons, poss)
                        binding.tvNoData.visibility =
                            if (newInfoIcons.size > 0) View.GONE else View.VISIBLE
                    }

                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        setClick()
    }

    private fun setClick() {
        binding.imgBack.setOnClickListener { dismiss() }
    }

    override fun onPause() {
        super.onPause()
        onBack()
    }

    private fun onBack() {
        dismiss()
    }
}

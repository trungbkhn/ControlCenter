package com.tapbi.spark.controlcenter.ui.dialog

import android.os.Bundle
import android.view.View
import com.ironman.trueads.common.Common
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.adapter.ViewPageAdapterUserManual
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.databinding.DialogUserManualControlBinding
import com.tapbi.spark.controlcenter.ui.base.BaseBindingDialogFragment

class DialogUserManual : BaseBindingDialogFragment<DialogUserManualControlBinding>() {
    override val layoutId: Int
        get() = R.layout.dialog_user_manual_control


    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        initView()
        initListener()
    }

    private fun initView() {
        initViewPagerAdapter()
        loadAdsNative(
            binding.flAds, Common.getMapIdAdmobApplovin(
                requireActivity(),
                R.array.admob_native_id_user_manual_control,
                R.array.applovin_id_native_user_manual_control
            )
        )
    }

    private fun initListener() {
        binding.imClose.setOnClickListener {
            dismiss()
        }
    }

    private fun initViewPagerAdapter() {
        context?.let {
            binding.viewpager.adapter = ViewPageAdapterUserManual(it)
            binding.wormDotsIndicator.setViewPager2(binding.viewpager)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        App.tinyDB.putBoolean(Constant.IS_SHOW_DIALOG_USER_MANUAL, true)
        mMainViewModel.liveDataShowDialogPermissionNoty.postValue(true)
    }
}
package com.tapbi.spark.controlcenter.ui.main.homemain.mycustomization

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.adapter.MyCustomizationAdapter
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.data.model.ThemeControl
import com.tapbi.spark.controlcenter.databinding.FragmentCustiomizeBinding
import com.tapbi.spark.controlcenter.eventbus.EventSelectThemes
import com.tapbi.spark.controlcenter.ui.base.BaseBindingFragment
import com.tapbi.spark.controlcenter.ui.main.MainActivity
import com.tapbi.spark.controlcenter.views.helper.BottomMarginItemDecoration
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

class MyCustomizationFragment :
    BaseBindingFragment<FragmentCustiomizeBinding, MyCustomizationViewModel>() {
    override fun getViewModel(): Class<MyCustomizationViewModel> {
        return MyCustomizationViewModel::class.java
    }

    private var myCustomizationAdapter: MyCustomizationAdapter? = null

    private var listThemeControl = mutableListOf<ThemeControl>()
        set(value) {
            if (isAdded) {
                field = value
                if (value.isEmpty()) {
                    binding.tvNoControl.visibility = View.VISIBLE
                    binding.ryMyCustomization.visibility = View.INVISIBLE
                    return
                }

                binding.tvNoControl.visibility = View.GONE
                binding.ryMyCustomization.visibility = View.VISIBLE
                // Set adapter data and handle click listener
                myCustomizationAdapter?.setIdThemes(
                    App.tinyDB.getLong(
                        Constant.KEY_ID_CURRENT_APPLY_THEME,
                        Constant.KEY_ID_CURRENT_APPLY_THEME_DEFAULT
                    )
                )
                myCustomizationAdapter?.setData(field, object : MyCustomizationAdapter.IListener {
                    override fun onClick(themeControl: ThemeControl, position: Int) {
                        if (isAdded) {
                            showDialogPreviewControl(requireContext(), themeControl, true)
                        }
                    }
                })
                // Find the selected theme index
                (activity as MainActivity).binding.loading.visibility = View.GONE
            }


        }

    override val layoutId: Int
        get() = R.layout.fragment_custiomize

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        initView()
        initListener()
        onObserve()
    }

    override fun onPause() {
        super.onPause()
        dismissDialogPreviewControl()
    }

    private fun onObserve() {

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                mainViewModel.themeControlsStateFlow.collectLatest { themeControls ->
                    listThemeControl = themeControls.toMutableList()
                }
            }
        }
    }

    private fun initListener() {

    }

    private fun initView() {
        myCustomizationAdapter = MyCustomizationAdapter()
        binding.ryMyCustomization.adapter = myCustomizationAdapter
        binding.ryMyCustomization.addItemDecoration(BottomMarginItemDecoration(100f))
    }

    override fun onPermissionGranted() {

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventBusSelectThemes(event: EventSelectThemes) {
        myCustomizationAdapter?.let {
            it.setIdThemes(event.id)
            for (i in 0 until listThemeControl.size) {
                it.notifyItemChanged(i, listThemeControl[i])
            }
        }
    }
}
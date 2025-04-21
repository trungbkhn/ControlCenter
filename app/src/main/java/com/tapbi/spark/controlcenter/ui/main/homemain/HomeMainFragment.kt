package com.tapbi.spark.controlcenter.ui.main.homemain

import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.ironman.trueads.common.Common
import com.simform.custombottomnavigation.Model
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.adapter.HomeMainViewPager
import com.tapbi.spark.controlcenter.databinding.FragmentHomeMainBinding
import com.tapbi.spark.controlcenter.ui.base.BaseBindingFragment
import timber.log.Timber

class HomeMainFragment : BaseBindingFragment<FragmentHomeMainBinding, HomeMainViewModel>() {

    private var adapterViewPager: HomeMainViewPager? = null

    private var idBottomNavigation: Int = -1
    override fun getViewModel(): Class<HomeMainViewModel> {
        return HomeMainViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_home_main

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        setUpPaddingStatusBar(binding.layoutParent)
        initViewPager()
        initBottomNavigation()
        observerData()

    }

    private fun observerData() {
        mainViewModel.liveDataSetCurrentViewPager.observe(viewLifecycleOwner) {
            if (it) {
                mainViewModel.currentPositionHome.value = 1
                binding.bottomNavigation.setSelectedIndex(1)
                binding.vgHomeMain.setCurrentItem(1, false)
                mainViewModel.liveDataSetCurrentViewPager.postValue(false)
            }
        }

        loadAdsBanner(
            binding.bannerContainer,
            Common.getMapIdAdmobApplovin(requireContext(),R.string.admob_banner_id_home_collapsible,R.string.applovin_banner_id_home),
            Common.getMapIdAdmobApplovin(
                requireActivity(),
                R.array.admob_native_id_home,
                R.array.applovin_id_native_home
            ),
            Common.enableAdsBannerCollapsible()
        )
    }

    private fun initBottomNavigation() {
        binding.bottomNavigation.apply {
            add(
                Model(
                    icon = R.drawable.ic_bn_home, text = R.string.home, id = 0,
                    allowShowView = true
                )
            )
            add(
                Model(
                    icon = R.drawable.ic_bn_my_customization,
                    text = R.string.my_customization,
                    id = 1,
                    allowShowView = true
                )
            )
            add(
                Model(
                    icon = R.drawable.ic_bn_settings,
                    text = R.string.setting,
                    id = 2,
                    allowShowView = true
                )
            )
            setOnShowListener {
                if (idBottomNavigation != it.id) {
                    idBottomNavigation = it.id
                    binding.vgHomeMain.setCurrentItem(it.id, false)
                }

            }
        }
    }


    private fun initViewPager() {
        adapterViewPager = HomeMainViewPager(childFragmentManager, lifecycle)
        binding.vgHomeMain.isUserInputEnabled = false
        binding.vgHomeMain.isSaveEnabled = false
        binding.vgHomeMain.adapter = adapterViewPager
        binding.vgHomeMain.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mainViewModel.currentPositionHome.value = position
                binding.bottomNavigation.setSelectedIndex(position)
            }
        });
        if (mainViewModel.currentPositionHome.value != null) {
            binding.vgHomeMain.setCurrentItem(mainViewModel.currentPositionHome.value!!, false)
        } else {
            mainViewModel.currentPositionHome.value = 0
            binding.vgHomeMain.setCurrentItem(0, false)
        }
    }

    override fun onPermissionGranted() {

    }


}

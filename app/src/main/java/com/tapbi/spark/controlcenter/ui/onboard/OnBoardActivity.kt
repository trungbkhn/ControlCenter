package com.tapbi.spark.controlcenter.ui.onboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.google.android.ads.nativetemplates.TemplateViewMultiAds
import com.ironman.trueads.common.Common
import com.ironman.trueads.multiads.MultiAdsControl
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.adapter.ViewPagerOnBoardAdapter
import com.tapbi.spark.controlcenter.common.Constant.IS_ONBOARD_STARED
import com.tapbi.spark.controlcenter.data.local.SharedPreferenceHelper
import com.tapbi.spark.controlcenter.databinding.ActivityOnboardBinding
import com.tapbi.spark.controlcenter.ui.base.BaseBindingActivity
import com.tapbi.spark.controlcenter.ui.favoritetheme.FavoriteThemeActivity
import com.tapbi.spark.controlcenter.utils.gone
import com.tapbi.spark.controlcenter.utils.inv
import com.tapbi.spark.controlcenter.utils.show
import timber.log.Timber

class OnBoardActivity : BaseBindingActivity<ActivityOnboardBinding, OnBoardViewModel>() {
    var viewPager: ViewPagerOnBoardAdapter? = null
    private var isShowAdsFullScreen = false

    override val layoutId: Int
        get() = R.layout.activity_onboard

    override fun getViewModel(): Class<OnBoardViewModel> {
        return OnBoardViewModel::class.java
    }


    override fun setupData() {
        isShowAdsFullScreen =
            !Common.checkAdsIsDisable(
                getString(R.string.tag_native_onboard_full),
                Common.TYPE_ADS_NATIVE
            )
        Timber.e("hachung isShowAdsFullScreen: $isShowAdsFullScreen")
        viewModel.getListItemOnboard(this, isShowAdsFullScreen)
        viewModel.liveDataListOnboard.observe(this) {
            if (it != null) {
                viewPager?.setDataList(it)
            }
        }
    }


    override fun setupView(savedInstanceState: Bundle?) {
        viewPager = ViewPagerOnBoardAdapter()
        viewPager?.setOnClickCloseAdsFull {
            binding.viewPager.currentItem = 4
        }
        binding.viewPager.adapter = viewPager
        binding.viewPager.offscreenPageLimit = 4
        binding.circleIndicator.setViewPager(binding.viewPager)
        loadAds(0)
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                initOnboard(position)

            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })

        binding.tvNextBottom.setOnClickListener {
            clickNext()
        }
        binding.tvNextTop.setOnClickListener {
            clickNext()
        }
    }

    private fun initOnboard(position: Int) {
        if (position == 3 && isShowAdsFullScreen) {
            binding.circleIndicator.gone()
            binding.tvNextBottom.gone()
            binding.tvNextTop.gone()
            binding.frAds.gone()
            val templateViewMultiAds = viewPager?.getViewAds()
            templateViewMultiAds?.let {
                val hashMapIdAdsNative = Common.getMapIdAdmobApplovin(
                    this@OnBoardActivity,
                    R.array.admob_native_id_onboard_full,
                    R.array.applovin_id_native_onboard_full
                )
                loadAdsFull(it, hashMapIdAdsNative)
            }
        } else {
            binding.circleIndicator.show()
            binding.frAds.show()
            if (position == (viewPager?.count?.minus(1))) {
                binding.tvNextTop.text = getString(R.string.start)
            }
            if (position == (viewPager?.count?.minus(1)) || position == 0) {
                binding.tvNextTop.show()
                binding.tvNextBottom.inv()
            } else {
                binding.tvNextTop.inv()
                binding.tvNextBottom.show()
            }
            loadAds(position)
        }
    }

    private fun loadAdsFull(templateView: TemplateViewMultiAds, unitId: HashMap<String, String>) {
        templateView.visibility = View.VISIBLE
        MultiAdsControl.showNativeAdNoMedia(
            this,
            unitId,
            templateView
        )
    }

    private fun loadAds(position: Int) {
        when (position) {
            0 -> {
                binding.flAds1.visibility = View.VISIBLE
                binding.flAds2.visibility = View.GONE
                loadAdsNative(
                    binding.flAds1, Common.getMapIdAdmobApplovin(
                        this,
                        R.array.admob_native_id_onboard_1,
                        R.array.applovin_id_native_onboard_1
                    )
                )
            }

            3,4 -> {
                binding.flAds1.visibility = View.GONE
                binding.flAds2.visibility = View.VISIBLE
                loadAdsNative(
                    binding.flAds2, Common.getMapIdAdmobApplovin(
                        this,
                        R.array.admob_native_id_onboard_2,
                        R.array.applovin_id_native_onboard_2
                    )
                )
            }

            else -> {
                binding.flAds1.visibility = View.GONE
                binding.flAds2.visibility = View.GONE
            }
        }

    }

    private fun clickNext() {
        try {
            if (binding.viewPager.currentItem == viewPager?.count?.minus(1)) {
                SharedPreferenceHelper.storeBoolean(IS_ONBOARD_STARED, true)
                val intent = Intent(this, FavoriteThemeActivity::class.java);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            } else {
                binding.viewPager.currentItem += 1
            }
        } catch (e: Exception) {
        }
    }
}
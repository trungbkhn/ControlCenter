package com.tapbi.spark.controlcenter.ui.main.customcontrol.color.previewwallpaper

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.google.android.material.tabs.TabLayoutMediator
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.adapter.PreviewWallpaperAdapter
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.databinding.FragmentPreviewWallpaperBinding
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614
import com.tapbi.spark.controlcenter.ui.base.BaseBindingFragment
import com.tapbi.spark.controlcenter.ui.main.MainActivity
import jp.wasabeef.glide.transformations.BlurTransformation

class PreviewWallpaperFragment :
    BaseBindingFragment<FragmentPreviewWallpaperBinding, PreviewWallpaperViewModel>() {

    companion object {
        const val KEY_PATH_BACKGROUND_PREVIEW = "key_path_background_preview"
    }

    private var previewAdapter: PreviewWallpaperAdapter? = null
    private var idBackground = -1
    private var typeNoty = Constant.VALUE_CONTROL_CENTER_OS
    override fun getViewModel(): Class<PreviewWallpaperViewModel> {
        return PreviewWallpaperViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_preview_wallpaper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        typeNoty = App.tinyDB.getInt(Constant.TYPE_NOTY, Constant.VALUE_CONTROL_CENTER_OS)
    }

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        setPaddingStatusBar()
        loadBackground()
        initPager()

        eventClick()
    }

    private fun initPager() {
        if (typeNoty == Constant.VALUE_SHADE) {
            binding.tvType.visibility = View.GONE
            binding.tabLayout.visibility = View.GONE
        }
        previewAdapter = PreviewWallpaperAdapter(viewModel.getListPreview(typeNoty))
        binding.vpPreview.adapter = previewAdapter
        binding.vpPreview.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    PreviewWallpaperAdapter.POS_NOTIFICATION -> binding.tvType.setText(R.string.text_cb_notification)
                    PreviewWallpaperAdapter.POS_CONTROL_CENTER -> binding.tvType.setText(R.string.text_cb_control)
                }
            }

            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if (typeNoty == Constant.VALUE_CONTROL_CENTER_OS) {
                    if (position == PreviewWallpaperAdapter.POS_NOTIFICATION) {
                        binding.imBackgroundBlur.alpha = positionOffset
                    } else {
                        binding.imBackgroundBlur.alpha = 1f
                    }
                }
            }
        })
        TabLayoutMediator(binding.tabLayout, binding.vpPreview) { _, _ ->
        }.attach()
    }

    private fun setPaddingStatusBar() {
        val lp = binding.imBack.layoutParams as ConstraintLayout.LayoutParams
        lp.topMargin = App.statusBarHeight
        binding.imBack.requestLayout()
    }

    private fun loadBackground() {
        arguments?.run {
            getInt(KEY_PATH_BACKGROUND_PREVIEW).let {
                idBackground = it
                val pathBackground = viewModel.getPathBackground(idBackground)
                Glide.with(this@PreviewWallpaperFragment).load(pathBackground)
                    .into(binding.imBackground)
                if (typeNoty == Constant.VALUE_CONTROL_CENTER_OS) {
                    binding.imBackgroundBlur.alpha = 0f
                    binding.imBackgroundBlur.visibility = View.VISIBLE
                    Glide.with(this@PreviewWallpaperFragment).load(pathBackground)
                        .apply(bitmapTransform(BlurTransformation(25, 7)))
                        .into(binding.imBackgroundBlur)
                } else {
                    binding.imBackgroundBlur.visibility = View.GONE
                }
            }
        }

    }

    private fun eventClick() {
        binding.tvApply.setOnClickListener {
            App.tinyDB.putInt(Constant.ID_STORE_WALLPAPER_SELECTED, idBackground)
            if (NotyControlCenterServicev614.getInstance() != null) {
                NotyControlCenterServicev614.getInstance().updateBg()
            }
            (activity as? MainActivity)?.navControllerMain?.popBackStack(R.id.homeFragment, false)
        }

        binding.imBack.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

    }

    override fun onPermissionGranted() {

    }
}
package com.tapbi.spark.controlcenter.ui.main.customcontrol.color.storewallpaper

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.ironman.trueads.common.Common
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.adapter.WallpaperAdapter
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.common.models.Wallpaper
import com.tapbi.spark.controlcenter.databinding.FragmentWallpaperBinding
import com.tapbi.spark.controlcenter.ui.base.BaseBindingDialogFragment
import com.tapbi.spark.controlcenter.ui.main.MainActivity
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import timber.log.Timber

class DialogWallpaperFragment(
    private val iListenerWallpaper: IListenerWallpaper,
    private var idWallpaper: Int = -1
) :
    BaseBindingDialogFragment<FragmentWallpaperBinding>() {

    private var adapterWallpaper: WallpaperAdapter? = null


    override val layoutId: Int
        get() = R.layout.fragment_wallpaper


    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        initView()
        initData()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setCanceledOnTouchOutside(false)
        return dialog

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    private fun initData() {
        mMainViewModel.getListWallPaperAssets(requireContext())

        mMainViewModel.wallpaperLiveData.observe(viewLifecycleOwner) { list ->
            if (list != null) {
                adapterWallpaper?.setData(list)
                Timber.e("hachung : $idWallpaper")
                val index = list.indexOfFirst { it.id == idWallpaper }
                adapterWallpaper?.setIdSelect(
                    index
                )
            }
        }
    }

    private fun initView() {
        (activity as MainActivity).setColorNavigation(R.color.colorWhite)
        binding.viewHeader.tvTitle.setText(R.string.store_wallpaper)
        binding.viewHeader.tvDone.visibility =  View.GONE
        binding.viewHeader.imgDone.visibility = if (idWallpaper == -1) View.GONE else View.VISIBLE
        binding.viewHeader.imBack.setOnClickListener {
            dismiss()
        }
        binding.viewHeader.imgDone.setOnClickListener {
            MainActivity.isDispatchTouchEvent()
            if (isAdded) {
                mMainViewModel.chooseBackGroundInStoreLiveData.postValue(idWallpaper)
                dismiss()
            }
        }
        adapterWallpaper = WallpaperAdapter(requireContext()) { wallpaper: Wallpaper, _: Int ->
            run {
                idWallpaper = wallpaper.id
                iListenerWallpaper.onClickWallpaper(wallpaper.pathLight)
                binding.viewHeader.imgDone.visibility = View.VISIBLE
            }
        }



        binding.rcvWallpaper.adapter = adapterWallpaper
        (binding.rcvWallpaper.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        OverScrollDecoratorHelper.setUpOverScroll(
            binding.rcvWallpaper, OverScrollDecoratorHelper.ORIENTATION_VERTICAL
        )
        loadAds()
    }

    private fun loadAds() {
        loadAdsNative(
            binding.flAds,
            Common.getMapIdAdmobApplovin(
                requireActivity(),
                        R.array.admob_native_id_store_wallpaper,
                        R.array.applovin_id_native_store_wallpaper
                    ), Color.BLACK, Color.GRAY
                )

    }



    interface IListenerWallpaper {
        fun onLoadAdsNative(frameLayout: FrameLayout)

        fun onClickWallpaper(path: String)

    }
}
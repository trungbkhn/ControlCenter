package com.tapbi.spark.controlcenter.ui.base

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.google.android.ads.nativetemplates.OnDecorationAds
import com.google.android.ads.nativetemplates.TemplateViewMultiAds
import com.ironman.trueads.multiads.InterstitialAdsLiteListener
import com.ironman.trueads.multiads.MultiAdsControl
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.ui.main.MainViewModel
import timber.log.Timber

abstract class BaseBindingDialogFragment<B : ViewDataBinding> : BaseDialogFragment() {
    lateinit var binding: B
    lateinit var mMainViewModel: MainViewModel
    abstract val layoutId: Int

    protected abstract fun onCreatedView(view: View?, savedInstanceState: Bundle?)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mMainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        onCreatedView(view, savedInstanceState)
    }
    fun showDialog(fm : FragmentManager, tag :String){
        try {
            val oldFragment: Fragment? = fm.findFragmentByTag(tag)

            if (oldFragment != null && oldFragment.isAdded) {
                fm.beginTransaction().remove(oldFragment).commit()
            }

            if (oldFragment == null && !isAdded && !isVisible) {
                fm.executePendingTransactions()
                show(fm, tag)
            }

        } catch (_:Exception){}
    }

    protected fun loadAdsNative(
        templateView: TemplateViewMultiAds,
        mapIds: HashMap<String, String>,
        colorTvHeadline: Int = Color.WHITE,
        colorTvBody: Int = Color.WHITE
    ) {
        MultiAdsControl.showNativeAdNoMedia(
            requireActivity() as AppCompatActivity,
            mapIds,
            templateView,
            null, null, object : OnDecorationAds {
                override fun onDecoration(network: String?) {
                    templateView.getNativeAdView(network)?.apply {
                        setBackgroundColor(
                            ContextCompat.getColor(
                                requireActivity(),
                                R.color.color_F3F3F3
                            )
                        )
                    }
//                    templateView.getTvHeadline(network)?.apply {
//                        (this as TextView).setTextColor(colorTvHeadline)
//                    }
//                    templateView.getTvBody(network)?.apply {
//                        (this as TextView).setTextColor(colorTvBody)
//                    }
                }
            })
    }

    protected open fun loadAdsBanner(
        bannerContainer: FrameLayout, unitId: HashMap<String, String>, nativeId: HashMap<String, String>, isCollapsible: Boolean
    ) {
        MultiAdsControl.setupAdsBanner(
            requireActivity(), bannerContainer,
            unitId, nativeId,true, isCollapsible, null
        )
    }


}
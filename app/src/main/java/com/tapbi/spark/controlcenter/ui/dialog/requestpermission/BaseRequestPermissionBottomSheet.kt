package com.tapbi.spark.controlcenter.ui.dialog.requestpermission

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.google.android.ads.nativetemplates.OnDecorationAds
import com.google.android.ads.nativetemplates.TemplateViewMultiAds
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.ironman.trueads.multiads.MultiAdsControl
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.common.Constant.TYPE_ADS_1
import com.tapbi.spark.controlcenter.common.Constant.TYPE_ADS_2
import com.tapbi.spark.controlcenter.databinding.DialogRequestPermissionType1Binding
import com.tapbi.spark.controlcenter.databinding.DialogRequestPermissionType2Binding
import com.tapbi.spark.controlcenter.databinding.DialogRequestPermissionType3Binding
import com.tapbi.spark.controlcenter.ui.base.BaseBottomSheetDialogFragment
import com.tapbi.spark.controlcenter.ui.main.MainActivity
import com.tapbi.spark.controlcenter.ui.main.MainViewModel
import com.tapbi.spark.controlcenter.utils.RemoteConfigHelper

abstract class BaseRequestPermissionBottomSheet : BaseBottomSheetDialogFragment() {
    private lateinit var binding: ViewDataBinding
    protected val mainViewModel: MainViewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    protected val mainActivity: MainActivity?
        get() =
            if (activity != null && activity is MainActivity) {
                (activity as MainActivity)
            } else
                null

    protected abstract fun onCreatedView(view: View?, savedInstanceState: Bundle?)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return when (FirebaseRemoteConfig.getInstance().getLong(Constant.TYPE_ADS_REQUEST_PERMISSION)) {
            TYPE_ADS_1 -> {
                binding = DialogRequestPermissionType1Binding.inflate(inflater, container, false)
                binding.root
            }

            TYPE_ADS_2 -> {
                binding = DialogRequestPermissionType2Binding.inflate(inflater, container, false)
                binding.root
            }

            else -> {
                binding = DialogRequestPermissionType3Binding.inflate(inflater, container, false)
                binding.root
            }
        }
    }

    protected val flAds: TemplateViewMultiAds by lazy {
        binding.root.findViewById(R.id.flAds)
    }

    protected val tvTitle: TextView by lazy {
        binding.root.findViewById(R.id.tvTitle)
    }
    protected val tvAccessibility: TextView by lazy {
        binding.root.findViewById(R.id.tvAccessibility)
    }
    protected val tvOverDraw: TextView by lazy {
        binding.root.findViewById(R.id.tvOverDraw)
    }
    protected val tvNotification: TextView by lazy {
        binding.root.findViewById(R.id.tvNotification)
    }
    protected val tvContent: TextView by lazy {
        binding.root.findViewById(R.id.tvContent)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onCreatedView(view, savedInstanceState)
    }

    protected fun loadAdsNative(
        templateView: TemplateViewMultiAds,
        mapIds: HashMap<String, String>,
        hasMedia: Boolean = false,
        colorTvHeadline: Int = Color.WHITE,
        colorTvBody: Int = Color.WHITE
    ) {
        MultiAdsControl.showNativeAd(
            requireActivity() as AppCompatActivity,
            mapIds,
            templateView,
            hasMedia,
            null, null, object : OnDecorationAds {
                override fun onDecoration(network: String?) {
                    templateView.getNativeAdView(network)?.apply {
                        setBackgroundColor(
                            ContextCompat.getColor(
                                requireActivity(),
                                R.color.color_f5f5f5
                            )
                        )
                    }
                }
            })
    }

}
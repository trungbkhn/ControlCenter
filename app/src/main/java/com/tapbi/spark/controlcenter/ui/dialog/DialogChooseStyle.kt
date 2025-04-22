package com.tapbi.spark.controlcenter.ui.dialog

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import androidx.constraintlayout.motion.widget.MotionLayout
import com.google.android.ads.nativetemplates.TemplateViewMultiAds
import com.google.android.material.tabs.TabLayout
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.ironman.trueads.common.Common
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.databinding.DialogChooseStyleBinding
import com.tapbi.spark.controlcenter.ui.base.BaseBindingDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DialogChooseStyle : BaseBindingDialogFragment<DialogChooseStyleBinding>() {


    var icLickDialogChooseStyle: ICLickDialogChooseStyle? = null
    private var type = Constant.VALUE_CONTROL_CENTER
    var isProgrammaticSelection: Boolean = false

    override val layoutId: Int
        get() = R.layout.dialog_choose_style

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        initView()
        initListener()

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        for (i in 0 until 3) {
            binding.tabLayout.addTab(binding.tabLayout.newTab())
        }
        startTransition()
        if (binding.imgView1 != null ){
            binding.imgView1.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    binding.imgView1.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    val imgView1Width = binding.imgView1.width

                    val margin = -(imgView1Width * 1) / 2
                    val layoutParams = binding.motion.layoutParams as ViewGroup.MarginLayoutParams
                    layoutParams.marginStart = margin
                    layoutParams.marginEnd = margin

                    binding.motion.layoutParams = layoutParams

                }
            })
        }

        if (Common.checkAdsIsDisable(
                requireContext().getString(R.string.tag_native_choose_style),
                Common.TYPE_ADS_NATIVE
            )
        ) {
            binding.cvAdsWrap.visibility = View.GONE
            binding.cvAdsWrap1.visibility = View.GONE
        } else {
            val flAds: TemplateViewMultiAds
            if (FirebaseRemoteConfig.getInstance().getLong(Constant.TYPE_CHOOSE_STYLE) == 1L) {
                binding.cvAdsWrap.visibility = View.VISIBLE
                binding.cvAdsWrap1.visibility = View.GONE
                flAds = binding.flAds
            } else {
                binding.cvAdsWrap1.visibility = View.VISIBLE
                binding.cvAdsWrap.visibility = View.GONE
                flAds = binding.flAds1
            }
            loadAdsNative(
                flAds, Common.getMapIdAdmobApplovin(
                    requireActivity(),
                    R.array.admob_native_id_choose_style,
                    R.array.applovin_id_native_choose_style
                )
            )
        }


    }


    @SuppressLint("ClickableViewAccessibility")
    private fun initListener() {
        binding.tvEditTheme.setOnClickListener {
            icLickDialogChooseStyle?.onClick(type)
            dismiss()
        }
        binding.motion.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(
                motionLayout: MotionLayout,
                startId: Int,
                endId: Int
            ) {
            }

            override fun onTransitionChange(
                motionLayout: MotionLayout,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout, currentId: Int) {
                when (currentId) {
                    R.layout.activity_main_start -> {
                        type = Constant.VALUE_CONTROL_CENTER
                    }

                    R.layout.activity_main_center -> {
                        type = Constant.VALUE_CONTROL_CENTER_OS
                    }

                    R.layout.activity_main_end -> {
                        type = Constant.VALUE_SHADE
                    }
                }
                setSelectTab(type)
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {
            }
        })
        binding.motion.setOnTouchListener { _, event ->
            if (event?.action == MotionEvent.ACTION_DOWN) {
                stopTransition()
            }
            false
        }
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    if (!isProgrammaticSelection) {
                        when (it.position) {
                            0 -> {
                                binding.motion.transitionToState(R.layout.activity_main_start)
                            }

                            1 -> {
                                binding.motion.transitionToState(R.layout.activity_main_end)
                            }

                            2 -> {
                                binding.motion.transitionToState(R.layout.activity_main_center)
                            }
                        }
                    }

                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        binding.viewClickDismiss.setOnClickListener {
            dismiss()
        }

    }

    private var job: Job? = null


    private fun startTransition() {
        job = CoroutineScope(Dispatchers.Main).launch {
            var iteration = 0
            var posTransToSate = 0
            val delays = listOf(250L, 80L)
            while (iteration < delays.size) {
                when (posTransToSate) {
                    0 -> {
                        binding.motion.transitionToState(R.layout.activity_main_center)
                        posTransToSate = 1
                    }

                    1 -> {
                        binding.motion.transitionToState(R.layout.activity_main_end)
                        posTransToSate = 0
                    }
                }

                delay(delays[iteration]) // Sử dụng thời gian delay tương ứng
                iteration++ // Tăng bộ đếm sau mỗi lần lặp
            }
        }


    }

    // Hàm hủy nếu cần
    private fun stopTransition() {
        job?.cancel()
        job = null
    }

    fun setSelectTab(type: Int) {

        val pos = when (type) {
            Constant.VALUE_CONTROL_CENTER -> 0
            Constant.VALUE_CONTROL_CENTER_OS -> 2
            Constant.VALUE_SHADE -> 1
            else -> {
                0
            }
        }
        if (isAdded) {
            isProgrammaticSelection = true
            binding.tabLayout.selectTab(binding.tabLayout.getTabAt(pos))
            isProgrammaticSelection = false
        }
    }

    interface ICLickDialogChooseStyle {
        fun onClick(type: Int)
    }
}
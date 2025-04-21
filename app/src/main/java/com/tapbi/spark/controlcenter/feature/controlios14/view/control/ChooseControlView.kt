package com.tapbi.spark.controlcenter.feature.controlios14.view.control

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.adapter.ChooseControlAdapter
import com.tapbi.spark.controlcenter.adapter.ChooseControlAdapter.IControlClick
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.common.models.CustomizeControlApp
import com.tapbi.spark.controlcenter.data.repository.PackageRepository
import com.tapbi.spark.controlcenter.databinding.LayoutFakeBottomSheetBinding
import com.tapbi.spark.controlcenter.feature.controlios14.model.ControlCustomize
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlCenterIosModel
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614
import com.tapbi.spark.controlcenter.utils.DensityUtils
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper
import com.tapbi.spark.controlcenter.utils.safeDelay
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import timber.log.Timber

@SuppressLint("ClickableViewAccessibility")
class ChooseControlView(context: Context) : FrameLayout(context) {
    var iClick: IControlClick? = null
    private val binding = LayoutFakeBottomSheetBinding.inflate(LayoutInflater.from(App.ins), this, true)
    private val behavior: BottomSheetBehavior<View> = BottomSheetBehavior.from(binding.bottomSheetContainer)
    private val listCustomizeCurrentApp = ArrayList<ControlCustomize>()
    private val listExceptCurrentApp = ArrayList<ControlCustomize>()
    private var controlAdapter : ChooseControlAdapter? = null
    private var params: WindowManager.LayoutParams

    init {
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        behavior.isHideable = true

        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                   hideView()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.BOTTOM

        binding.icTop2.setOnTouchListener { _, event ->
            binding.root.dispatchTouchEvent(event)
            false
        }
        binding.rootOverlay.setOnClickListener {
            ViewHelper.preventTwoClick(it)
            hideView()
        }
        initAdapter()
    }

    private fun initAdapter() {
        if (controlAdapter == null){
            controlAdapter = ChooseControlAdapter()
            val layoutManager = GridLayoutManager(context,4)
            binding.rclControl1.layoutManager = layoutManager
            binding.rclControl1.adapter = controlAdapter
        }
    }
    fun setListener(iControlClick: IControlClick){
        this.iClick = iControlClick;
        controlAdapter?.iClick = iClick
    }

    fun setListControl(listControl1: ArrayList<ControlCenterIosModel>?) {
        val list = listControl1?.let { ArrayList(it) } ?: emptyList()
        val list1 = list?.mapNotNull {
                Timber.e("NVQ getListAppForCustomize//// ${it.controlSettingIosModel?.iconControl}")
                if (((it.keyControl != Constant.KEY_CONTROL_OPEN_APP) && (it.keyControl != Constant.KEY_CONTROL_ADD) && ((it.ratioWidght == 4 && it.ratioHeight == 4) || it.keyControl == Constant.KEY_CONTROL_SCREEN_TIME_OUT))) {
                    it.keyControl
                } else if (it.keyControl == Constant.KEY_CONTROL_OPEN_APP){
                    it.controlSettingIosModel?.iconControl
                } else null
            } ?: emptyList()
        getListAppForCustomize(App.ins,list1.toTypedArray())
   }
    fun showView(){
        try {
            alpha = 1f
            NotyControlCenterServicev614.getInstance().windowManager.addView(this@ChooseControlView, params)
        } catch (e:Exception){}

    }
    fun hideView(){
        try {
            (NotyControlCenterServicev614.getInstance().windowManager)?.removeView(this@ChooseControlView)
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } catch (e:Exception){}
    }
    fun removeClickView(pos : Int){
        controlAdapter?.removeItem(pos)
    }
    private fun getListAppForCustomize(
        context: Context?,
        customControlCurrent: Array<String?>
    ) {
        Timber.e("NVQ getListAppForCustomize ++++ ${customControlCurrent.toList().toString()}")
        PackageRepository().getListAppCustomizeEditRx(context, customControlCurrent)
            .subscribe(object : SingleObserver<CustomizeControlApp> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(customizeControlApp: CustomizeControlApp) {
                    Timber.e("NVQ getListAppForCustomize onSuccess ${customizeControlApp.listExceptCurrentApp.size}")
                    val list =  customizeControlApp.listCustomizeCurrentApp
                    list.addAll(customizeControlApp.listExceptCurrentApp)
                    controlAdapter?.setData(list)
                    safeDelay(100){
                        binding.rclControl1.post {
                            binding.layoutProgress.visibility = View.GONE
                        }
                    }
                }

                override fun onError(e: Throwable) {
                }
            })
    }
}

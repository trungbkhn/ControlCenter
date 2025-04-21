package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group1


import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import com.bumptech.glide.Glide
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.data.repository.ThemesRepository.isControlEditing
import com.tapbi.spark.controlcenter.databinding.LayoutControl2x2TopIos18Binding
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlSettingIosModel
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ConstraintLayoutBase
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ImageBase.OnAnimationListener
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5.EditControlView
import com.tapbi.spark.controlcenter.utils.SettingUtils
import com.tapbi.spark.controlcenter.utils.SettingUtils.checkIfLocationOpened
import timber.log.Timber

class ControlViewTopIOS18(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayoutBase(context, attrs) {

    private val handler = Handler()
    var onSettingListener: SettingView.OnSettingListener? = null
    private val runnable = Runnable { this.onLongClick() }
    private var controlSettingIOS: ControlSettingIosModel? = null

    constructor(context: Context,controlSettingIosModel :ControlSettingIosModel?) : this(context){
        this.controlSettingIOS = controlSettingIosModel
        init(context)
    }

    var binding : LayoutControl2x2TopIos18Binding? = null


    fun changeControlSettingIos(controlSettingIOS: ControlSettingIosModel?) {
        this.controlSettingIOS = controlSettingIOS
        initView()
    }
    @SuppressLint("ClickableViewAccessibility")
    fun init(context: Context?) {
        val layoutInflater = LayoutInflater.from(context)
        binding = LayoutControl2x2TopIos18Binding.inflate(layoutInflater,this,true)

        initView()
        binding?.airplaneAction?.setOnAnimationListener(object : OnAnimationListener {
            override fun onDown() {
                if (onSettingListener != null) {
                    onSettingListener?.onDown()
                }
            }

            override fun onUp() {
                if (onSettingListener != null) {
                    onSettingListener?.onUp()
                }
            }

            override fun onClick() {
                if (onSettingListener != null) {
                    onSettingListener?.onHide()
                }
            }

            override fun onLongClick() {
                if (onSettingListener != null) {
                    onSettingListener?.onHide()
                }
            }

            override fun onClose() {
            }
        })

        binding?.dataAction?.setOnAnimationListener(object : OnAnimationListener {
            override fun onDown() {
                if (onSettingListener != null) {
                    onSettingListener?.onDown()
                }
            }

            override fun onUp() {
                if (onSettingListener != null) {
                    onSettingListener?.onUp()
                }
            }

            override fun onClick() {
                if (onSettingListener != null) {
                    onSettingListener?.onHide()
                }
            }

            override fun onLongClick() {
                if (onSettingListener != null) {
                    onSettingListener?.onHide()
                }
            }

            override fun onClose() {
            }
        })

        binding?.wifiAction?.setOnAnimationListener(object : OnAnimationListener {
            override fun onDown() {
                if (onSettingListener != null) {
                    onSettingListener?.onDown()
                }
            }

            override fun onUp() {
                if (onSettingListener != null) {
                    onSettingListener?.onUp()
                }
            }

            override fun onClick() {
                if (onSettingListener != null) {
                    onSettingListener?.onWifiChange()
                }
            }

            override fun onLongClick() {
                if (onSettingListener != null) {
                    onSettingListener?.onHide()
                }
            }

            override fun onClose() {
                onSettingListener?.onClose()
            }
        })

        binding?.viewOverlay?.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    if (checkClick(v,event.x, event.y) && !isControlEditing) {
                        onSettingListener?.onLongClick(this);
                    }
                }
            }
            true
        }
    }

    private fun checkClick(view: View, x: Float, y: Float): Boolean {
        if ((x < view.paddingLeft) || (x > view.width - view.paddingRight) || (y > (view.height - view.paddingBottom)) || (y < view.paddingTop)) {
            return false
        }
        return true
    }
    fun initView() {
        Timber.e("NVQ initView123465 ${controlSettingIOS == null}")
        if (controlSettingIOS != null) {
            changeColorBackground(
                controlSettingIOS!!.backgroundDefaultColorViewParent,
                controlSettingIOS!!.backgroundSelectColorViewParent,
                controlSettingIOS!!.cornerBackgroundViewParent
            )
            binding?.airplaneAction?.changeData(controlSettingIOS)
            binding?.dataAction?.changeData(controlSettingIOS)
            binding?.wifiAction?.changeData(controlSettingIOS)
            binding?.bluetoothAction?.changeData(controlSettingIOS)

            updateSync()
            updateLocation()
            binding?.silentAction?.changeData(controlSettingIOS)
        }
    }
    fun onLongClick() {
        onSettingListener?.onLongClick(this)
    }
    fun updateBgAirplane(b: Boolean) {
        binding?.airplaneAction?.updateAirPlaneState(b)
    }

    fun updateBg(b: Boolean) {
        binding?.bluetoothAction?.enableBluetooth = b
        binding?.bluetoothAction?.updateImage()
    }

    fun updateSync(){
        binding?.let {
            if (SettingUtils.isSyncAutomaticallyEnable()){
                Glide.with(App.ins).load(R.drawable.sync_on).into(binding!!.syncAction)
            } else {
                Glide.with(App.ins).load(R.drawable.sync_off).into(binding!!.syncAction)
            }
        }

    }
    fun updateLocation(){
        binding?.let {
            if (checkIfLocationOpened()){
                Glide.with(App.ins).load(R.drawable.location_on).into(binding!!.locationAction)
            } else {
                Glide.with(App.ins).load(R.drawable.location_off).into(binding!!.locationAction)
            }
        }
    }

    fun updateWifi(b: Boolean) {
        binding?.wifiAction?.updateState(b)
    }

    fun updateDataMobile(b: Boolean) {
        binding?.dataAction?.updateState(b)
    }

    fun setViewTouching(touching: Boolean) {
        binding?.airplaneAction?.setViewTouching(touching)
        binding?.wifiAction?.setViewTouching(touching)
        binding?.dataAction?.setViewTouching(touching)
        binding?.bluetoothAction?.setViewTouching(touching)
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (onSettingListener != null) {
                    onSettingListener?.onDown()
                }
                handler.postDelayed(runnable, ViewConfiguration.getLongPressTimeout().toLong())
                animationDown()
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (onSettingListener != null) {
                    onSettingListener?.onUp()
                }
                handler.removeCallbacks(runnable)
                animationUp()
            }
        }
        return true
    }


    fun updateState() {
        binding?.bluetoothAction?.updateBluetoothState()
    }

    fun destroy() {

    }


}
package com.tapbi.spark.controlcenter.utils

import android.graphics.PixelFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614

class ToastTextManager constructor(private val service: NotyControlCenterServicev614) {
    private lateinit var viewsContainer: FrameLayout
    private lateinit var params: WindowManager.LayoutParams
    private lateinit var tvMessage: TextView
    private var isShowing = false
    init {
        setUp()
    }
    companion object {
        private var instance: ToastTextManager? = null
        fun getCurrentInstance(): ToastTextManager? {
            if (instance == null ) {
                if (NotyControlCenterServicev614.getInstance() != null){
                    instance = ToastTextManager(NotyControlCenterServicev614.getInstance())
                }
            }
            return instance
        }
    }

    fun setUp() {
        viewsContainer = FrameLayout(service)
        val inflater = LayoutInflater.from(service)
        inflater.inflate(R.layout.floating_toast_layout, viewsContainer)
        tvMessage = viewsContainer.findViewById(R.id.toast_message)
        initParam()
    }

    fun initParam() {
        params = WindowManager.LayoutParams()
        var flags =
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        flags = flags or params.flags
        params = WindowManager.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            flags,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.START or Gravity.TOP

        params.flags = params.flags or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
    }


    fun show(message: String) {
            if (!isShowing) {
                tvMessage.text = message
                service.windowManager.addLayout(viewsContainer, params)
                isShowing = true
                safeDelay(1500) {
                    isShowing = false
                    service.windowManager.removeLayout(viewsContainer)
                }
            }
    }


}
package com.tapbi.spark.controlcenter.feature.controlcenter.view.control.base

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.ControlCenterIOSView
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.ControlCenterViewOS
import com.tapbi.spark.controlcenter.ui.base.BaseConstraintLayout

abstract class BaseControlView(context: Context, attrs: AttributeSet?) :
    BaseConstraintLayout(context, attrs) {
    protected var limitedDistance = 0f
    protected var initialY = 0f
    protected val maxDistance = 500f // Khoảng cách tối đa để làm mờ
    protected var isDragging = false // Để kiểm tra xem người dùng có đang kéo không
    protected var maxBrightness = 255
    protected var maxVolume = 0
    protected var onControlCenterListener: ControlCenterIOSView.OnControlCenterListener? = null

    abstract fun initUI()
    abstract fun setUpBg()
    abstract fun initSeekBar()

    protected fun setupOnTouchListener(layoutControl: ConstraintLayout) {
        layoutControl.setOnTouchListener { _, event ->
            handleTouch(event, layoutControl)
            true
        }
    }

    private fun handleTouch(event: MotionEvent, layoutControl: ConstraintLayout) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialY = event.rawY
                limitedDistance = 0f
                isDragging = true
            }

            MotionEvent.ACTION_MOVE -> {
                if (isDragging) {
                    updateAlpha(event.rawY, layoutControl)
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (limitedDistance > 0) {
                    resetDragState()
                    animationHideMain(layoutControl)
                }
            }
        }
    }

    private fun updateAlpha(rawY: Float, layoutControl: ConstraintLayout) {
        val distance = initialY - rawY
        limitedDistance = 0f.coerceAtLeast(distance.coerceAtMost(maxDistance))
        val alpha = 1.0f - (limitedDistance / maxDistance)
        layoutControl.alpha = alpha
        if (limitedDistance >= maxDistance) {
            animationHideMain(layoutControl)
        }
    }

    private fun resetDragState() {
        isDragging = false
        limitedDistance = 0f
    }

    protected fun animationHideMain(layoutControl: ConstraintLayout) {
        onControlCenterListener?.onClose()
        layoutControl.alpha = 1f
    }
}
package com.tapbi.spark.controlcenter.feature.edge

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614
import com.tapbi.spark.controlcenter.utils.MethodUtils

/**
 * Check to allow show Notification Panel System with action AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS
 *
 * Handing when #BaseTouchView invisible, follow KEY @see #Constant.ENABLE_NOTY and @see #Constant.ENABLE_CONTROL
 */
class TouchInvisibleView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val spaceShowNotySystem = MethodUtils.dpToPx(30f)

    var isTouch = false

    private val runnableTouch = Runnable {
        isTouch = false
    }

    private val handleTOuch = Handler(Looper.getMainLooper())
    private var yDown = 0f
    private var isShowSystemNoty = false

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isShowSystemNoty = false
                yDown = event.y
                countTimeTouch()
            }

            MotionEvent.ACTION_MOVE -> {
                if (isShowSystemNoty) {
                    return super.onTouchEvent(event)
                }
                val spaceTouch = event.y - yDown
                if (spaceTouch > spaceShowNotySystem) {
                    countTimeTouch()
                    NotyControlCenterServicev614.getInstance().performGlobalAction(
                        AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS
                    )
                    isShowSystemNoty = true
                }
            }
        }
        return true
    }

    private fun countTimeTouch() {
        isTouch = true
        handleTOuch.removeCallbacksAndMessages(null)
        handleTOuch.postDelayed(runnableTouch, 500)
    }
}
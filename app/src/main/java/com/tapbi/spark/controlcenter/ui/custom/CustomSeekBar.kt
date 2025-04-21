package com.tapbi.spark.controlcenter.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.utils.MethodUtils.dpToPx

class CustomSeekBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyle) {

    private val thumbRadius = dpToPx(10f).toFloat()
    private val progressHeight = dpToPx(4f).toFloat()
    private val progressCornerRadius = dpToPx(100f).toFloat()
    private val progressColor = ContextCompat.getColor(App.ins, R.color.text_splash)
    private val backgroundColor = ContextCompat.getColor(App.ins, R.color._29787880)

    private val paint = Paint()
    private val thumbBounds = RectF()
    private var progress = 0
    private var max = 50
    private var isDragging = false
    private var isEnabled = true
    var listener: OnSeekBarChangeListener? = null

    init {
        paint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width.toFloat()
        val height = height.toFloat()

        // Draw background (phần sau thumb)
        paint.color = backgroundColor
        canvas.drawRoundRect(
            thumbRadius,
            (height - progressHeight) / 2,
            width - thumbRadius,
            (height + progressHeight) / 2,
            progressCornerRadius, progressCornerRadius,
            paint
        )

        // Draw progress (phần trước thumb)
        paint.color = progressColor
        canvas.drawRoundRect(
            thumbRadius,
            (height - progressHeight) / 2,
            thumbRadius + (width - 2 * thumbRadius) * progress / max,
            (height + progressHeight) / 2,
            progressCornerRadius, progressCornerRadius,
            paint
        )

        // Draw thumb
        paint.color = if (isEnabled) progressColor else backgroundColor

        thumbBounds.set(
            thumbRadius + (width - 2 * thumbRadius) * progress / max - thumbRadius,
            (height - thumbRadius * 2) / 2,
            thumbRadius + (width - 2 * thumbRadius) * progress / max + thumbRadius,
            (height + thumbRadius * 2) / 2
        )
        canvas.drawOval(thumbBounds, paint)
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) return false

        val width = width.toFloat()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isDragging = true
                listener?.onStartTrackingTouch(this)
                updateProgress(event.x, width)
            }

            MotionEvent.ACTION_MOVE -> {
                if (isDragging) {
                    updateProgress(event.x, width)
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isDragging) {
                    listener?.onStopTrackingTouch(this)
                    isDragging = false
                }
            }
        }
        return true
    }

    private fun updateProgress(x: Float, width: Float) {
        val newProgress =
            ((x - thumbRadius) / (width - 2 * thumbRadius) * max).toInt().coerceIn(0, max)
        if (newProgress != progress) {
            progress = newProgress
            invalidate()
            listener?.onProgressChanged(this, progress, true)
        }
    }

    private fun disable() {
        isEnabled = false
        invalidate()
    }

    private fun enable() {
        isEnabled = true
        invalidate()
    }


    fun setProgress(value: Int) {
        progress = value.coerceIn(0, max)
        invalidate()
    }

    fun setMax(value: Int) {
        max = value
        invalidate()
    }

    fun setState(enableSeekBar: Boolean) {
        if (!enableSeekBar) {
            progress = 0
            listener?.onProgressChanged(this, 0, true)
            disable()
        } else {
            enable()
        }
    }

    interface OnSeekBarChangeListener {
        fun onProgressChanged(seekBar: CustomSeekBar, progress: Int, fromUser: Boolean)
        fun onStartTrackingTouch(seekBar: CustomSeekBar)
        fun onStopTrackingTouch(seekBar: CustomSeekBar)
    }
}

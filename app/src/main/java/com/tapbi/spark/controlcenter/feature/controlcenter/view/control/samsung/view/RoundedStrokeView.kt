package com.tapbi.spark.controlcenter.feature.controlcenter.view.control.samsung.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout

class RoundedStrokeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.WHITE // Background color
    }


    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.TRANSPARENT
        strokeWidth = 2f
    }

    private var radius: Float = 0f
    private val rect = RectF()

    init {

        setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        rect.set(
            strokePaint.strokeWidth / 2,
            strokePaint.strokeWidth / 2,
            width - strokePaint.strokeWidth / 2,
            height - strokePaint.strokeWidth / 2
        )

        val conner = radius * height
        canvas.drawRoundRect(rect, conner, conner, backgroundPaint)
        // Draw border with rounded corners
        canvas.drawRoundRect(rect, conner, conner, strokePaint)
    }

    // Methods to change properties without allocating new objects
    fun setRadius(radius: Float) {
        this.radius = radius
        invalidate()
    }

    fun setStrokeColor(color: Int) {
        if (strokePaint.color != color) {
            strokePaint.color = color
            invalidate()
        }
    }

    fun setBackgroundColorView(color: Int) {
        if (backgroundPaint.color != color) {
            backgroundPaint.color = color
            invalidate()
        }
    }
}
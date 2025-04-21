package com.tapbi.spark.controlcenter.feature.base

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ImageBase
import org.checkerframework.checker.units.qual.h
import kotlin.math.min

class CustomImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ImageBase(context, attrs, defStyleAttr) {

    private var icon: Drawable? = ContextCompat.getDrawable(context, R.drawable.flashlight_off)
    private var backgroundColor: Int = Color.parseColor("#38FFFFFF")
    private var cornerRadius: Float = 100f
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rectF = RectF()

    private var isCircle = true

    init {
        // Init block if needed
    }

    override fun onDraw(canvas: Canvas) {
        // Set background
        paint.color = backgroundColor
        if (isCircle) {
            canvas.drawCircle(width / 2f, height / 2f, min(width, height) / 2f, paint)
        } else {
            rectF.set(0f, 0f, width.toFloat(), height.toFloat())
            canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint)
        }

        val paddingIcon = (width * 0.285).toInt()
        // Draw icon
        icon?.let {
            it.setBounds(
                0 + paddingIcon,
                0 + paddingIcon,
                width - paddingIcon,
                height - paddingIcon
            )
            it.draw(canvas)
        }
    }


    override fun click() {

    }

    override fun longClick() {

    }

    override fun onDown() {
        animationDown()
    }

    override fun onUp() {
        animationUp()
    }

    // Setters
    fun setIcon(drawable: Drawable) {
        this.icon = drawable
        invalidate() // Redraw view
    }

    fun setBackground(color: Int) {
        this.backgroundColor = color
        invalidate()
    }

    fun setCornerRadius(radius: Float) {
        this.cornerRadius = radius
        invalidate()
    }
}
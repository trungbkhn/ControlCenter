package com.tapbi.spark.controlcenter.ui.custom

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.utils.Utils.loadImageFromAssetsDrawable
import timber.log.Timber

class RoundedImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var isSelect = false
    private var cornerRadius: Float = 0F
    private var rect = RectF()
    private var colorBackground = Color.TRANSPARENT
    private var colorBackgroundImage = Color.TRANSPARENT
    private var icon: Drawable? = null

    private var stringBackground: String = Constant.STRING_ICON_SHADE_1
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorBackground
    }

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.RoundedImageView,
            0, 0
        ).apply {
            try {
                cornerRadius = getDimension(R.styleable.RoundedImageView_cornerRadius_cs, 100f)
                isSelect = getBoolean(R.styleable.RoundedImageView_customIsSelect_cs, false)
            } finally {
                recycle()
            }
        }

        paint.apply {
            isAntiAlias = true
            color = colorBackground
        }
    }


    override fun onDraw(canvas: Canvas) {
        drawBackground(canvas)
        drawIcon(canvas)
    }

    private fun drawBackground(canvas: Canvas) {
        paint.color = colorBackground
        if (stringBackground == Constant.STRING_ICON_SHADE_1) {
            backgroundTintList = null
            rect.set(0f, 0f, width.toFloat(), height.toFloat())
            val radius = cornerRadius * height
            canvas.drawRoundRect(rect, radius, radius, paint)
        } else {
            backgroundTintList = ColorStateList.valueOf(colorBackgroundImage)
        }
    }

    private fun drawIcon(canvas: Canvas) {
        icon?.let {
            it.setBounds(
                paddingIcon,
                paddingIcon,
                width - paddingIcon,
                height - paddingIcon
            )
            it.draw(canvas)
        }
    }

    fun setBackgroundC(color: Int) {
        if (stringBackground != Constant.STRING_ICON_SHADE_1) {
            colorBackground = Color.TRANSPARENT
            colorBackgroundImage = color
            invalidate()
        } else {
            if (colorBackground != color) {
                colorBackground = color
                colorBackgroundImage = color
                invalidate()
            }
        }

    }

    fun setIcon(resId: Int) {
        try {
            this.icon = ContextCompat.getDrawable(App.ins, resId)?.apply {
                setBounds(
                    0 + paddingIcon,
                    0 + paddingIcon,
                    width - paddingIcon,
                    height - paddingIcon
                )
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
        invalidate()
    }

    fun setBackground(fileName: String) {
        if (stringBackground != fileName) {
            stringBackground = fileName
            if (stringBackground == Constant.STRING_ICON_SHADE_1) {
                background = null
                invalidate()
            } else {
                background = loadImageFromAssetsDrawable(fileName)
            }

        }

    }

    fun setRatioRadius(percentage: Float) {
        cornerRadius = percentage
        invalidate()
    }

    fun setIconColor(color: Int) {
        icon?.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
        invalidate()
    }

    private val paddingIcon: Int
        get() = (width * 0.286f).toInt()

}




package com.tapbi.spark.controlcenter.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.FrameLayout
import com.tapbi.spark.controlcenter.utils.DensityUtils
import timber.log.Timber

class LayoutRounder : FrameLayout {
    private var maskBitmap: Bitmap? = null
    private var paint: Paint? = null
    private var maskPaint: Paint? = null
    private var cornerRadius = 0f

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(context, attrs, defStyle)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyle: Int) {
        cornerRadius = DensityUtils().dpFromPx(context,15f)
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        maskPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        maskPaint!!.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        setWillNotDraw(false)
    }

    override fun draw(canvas: Canvas) {
        Timber.e(".")
        val offscreenBitmap =
            Bitmap.createBitmap(canvas.width, canvas.height, Bitmap.Config.ARGB_8888)
        val offscreenCanvas = Canvas(offscreenBitmap)
        super.draw(offscreenCanvas)
        if (maskBitmap == null) {
            maskBitmap = createMask(canvas.width, canvas.height)
        }
        offscreenCanvas.drawBitmap(maskBitmap!!, 0f, 0f, maskPaint)
        canvas.drawBitmap(offscreenBitmap, 0f, 0f, paint)
    }

    private fun createMask(width: Int, height: Int): Bitmap {
        val mask = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8)
        val canvas = Canvas(mask)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.WHITE
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        canvas.drawRoundRect(
            RectF(0f, 0f, width.toFloat(), height.toFloat()),
            cornerRadius,
            cornerRadius,
            paint
        )
        return mask
    }
}
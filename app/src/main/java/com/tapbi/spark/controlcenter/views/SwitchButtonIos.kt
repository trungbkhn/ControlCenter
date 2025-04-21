package com.tapbi.spark.controlcenter.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.tapbi.spark.controlcenter.R

@SuppressLint("CustomViewStyleable")
class SwitchButtonIos @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    View(context, attrs) {
    private val mPaint = Paint()
    private var mHeight = 0
    private var mAnimate = 0f
    private var checked = false
    private var mScale = 0f
    private val mSelectColor: Int
    private val isPermission: Boolean
    var onCheckedChangeListener: OnCheckedChangeListener? = null

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SwitchButton)
        mSelectColor =
            typedArray.getColor(R.styleable.SwitchButton_buttonColor, Color.parseColor("#2eaa57"))
        isPermission = typedArray.getBoolean(R.styleable.SwitchButton_isPermission, false)
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        mHeight = (MBTNHEIGHT * width).toInt()
        setMeasuredDimension(width, mHeight)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mPaint.style = Paint.Style.FILL
        mPaint.isAntiAlias = true
        mPaint.color = mSelectColor
        val rect = Rect(0, 0, width, height)
        val rectf = RectF(rect)
        canvas.drawRoundRect(rectf, (mHeight / 2).toFloat(), (mHeight / 2).toFloat(), mPaint)
        canvas.save()
        mPaint.color = ContextCompat.getColor(context, R.color.bg_switch)
        mAnimate = if (mAnimate - 0.1f > 0) mAnimate - 0.1f else 0f
        mScale = if (!checked) 1 - mAnimate else mAnimate
        canvas.scale(mScale, mScale, (width - height / 2).toFloat(), rect.centerY().toFloat())
        canvas.drawRoundRect(rectf, (mHeight / 2).toFloat(), (mHeight / 2).toFloat(), mPaint)
        mPaint.color = ContextCompat.getColor(context, R.color.bg_switch)
        val rect_inner = Rect(OFFSET, OFFSET, width - OFFSET, height - OFFSET)
        val rect_f_inner = RectF(rect_inner)
        canvas.drawRoundRect(
            rect_f_inner,
            ((mHeight - 8) / 2).toFloat(),
            ((mHeight - 8) / 2).toFloat(),
            mPaint
        )
        canvas.restore()
        val sWidth = width
        val bTranslateX = sWidth - height
        val translate = bTranslateX * if (!checked) mAnimate else 1 - mAnimate
        canvas.translate(translate, 0f)
        mPaint.color = Color.parseColor("#E6E6E6")
        canvas.drawCircle(
            (height / 2).toFloat(),
            (height / 2).toFloat(),
            (height / 2 - OFFSET / 2 - 2).toFloat(),
            mPaint
        )
        mPaint.color = Color.WHITE
        canvas.drawCircle(
            (height / 2).toFloat(),
            (height / 2).toFloat(),
            (height / 2 - OFFSET - 2).toFloat(),
            mPaint
        )
        if (mScale > 0) {
            mPaint.reset()
            invalidate()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> return true
            MotionEvent.ACTION_MOVE -> {}
            MotionEvent.ACTION_UP -> {
                if (isPermission) {
                    if ( /*XiaomiUtilities.isCustomPermissionGranted(XiaomiUtilities.OP_AUTO_START)*/true) {
                        mAnimate = 1f
                        checked = !checked
                    }
                } else {
                    mAnimate = 1f
                    checked = !checked
                }
                invalidate()
                //                Timber.e("hachung checked 4 :" + checked);
                if (onCheckedChangeListener != null) {
                    onCheckedChangeListener!!.OnCheckedChanged(checked)
                }
            }
        }
        return super.onTouchEvent(event)
    }

    fun isChecked(): Boolean {
        return checked
    }

    fun setChecked(checked: Boolean) {
        this.checked = checked
        invalidate()
    }

    fun interface OnCheckedChangeListener {
        fun OnCheckedChanged(isChecked: Boolean)
    }

    companion object {
        private const val MBTNHEIGHT = 0.55
        private const val OFFSET = 3
    }
}
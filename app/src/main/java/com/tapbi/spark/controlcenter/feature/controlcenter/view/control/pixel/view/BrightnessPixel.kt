package com.tapbi.spark.controlcenter.feature.controlcenter.view.control.pixel.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Build
import android.provider.Settings
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.ControlCenterIOSView.OnControlCenterListener
import com.tapbi.spark.controlcenter.utils.DensityUtils
import com.tapbi.spark.controlcenter.utils.MethodUtils
import com.tapbi.spark.controlcenter.utils.SettingUtils
import timber.log.Timber
import kotlin.math.abs
import kotlin.math.max

class BrightnessPixel : View {

    private val paint = Paint()
    var width = 0f
    var height = 0f
    private var paintRect: Paint? = null
    private var valueX = 0f
    var rect: RectF? = null
    private var valueBrightness = 0f
    private var callBackUpdateBg: CallBackUpdateBg? = null
    private var onControlCenterListener: OnControlCenterListener? = null

    private var orientation = Configuration.ORIENTATION_PORTRAIT
    private var oldDown = 0f
    private var oldValueX = 0f

    private var isTouching = false
    private var maxBrightness = 255
    private val spaceMove = MethodUtils.dpToPx(6f)


    private var backgroundColor = Color.WHITE

    private var cornerRadius = 0f
    private val paintBackground = Paint(Paint.ANTI_ALIAS_FLAG)

    private val pathBackground = Path()


    private var colorProgress = Color.WHITE


    private var imgBriness: Bitmap? = null


    constructor(context: Context?) : super(context) {
        init(null)
    }


    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        init(attrs)
    }


    private fun getBitmap(context: Context, drawableId: Int): Bitmap {
        return when (val drawable = ContextCompat.getDrawable(context, drawableId)) {
            is BitmapDrawable -> {
                BitmapFactory.decodeResource(context.resources, drawableId)
            }

            is VectorDrawable -> {
                getBitmap(drawable)
            }

            else -> {
                throw IllegalArgumentException("unsupported drawable type")
            }
        }
    }

    fun setBackgroundRadius(cornerRadius: Float, backgroundColor: Int) {
        this.cornerRadius = cornerRadius
        this.backgroundColor = backgroundColor
        invalidate()
    }

    fun setOrientation(i: Int) {
        this.orientation = i
        invalidate()
    }

    fun setValueBrightnessMax(maxBrightness: Int) {
        this.maxBrightness = maxBrightness
    }

    fun setCallBackUpdateBg(callBackUpdateBg: CallBackUpdateBg) {
        this.callBackUpdateBg = callBackUpdateBg
    }

    fun init(attributeSet: AttributeSet?) {
        if (attributeSet != null) {
            val array: TypedArray = context.obtainStyledAttributes(
                attributeSet, R.styleable.BrightnessMi
            )
            orientation = array.getInteger(R.styleable.BrightnessMi_orientationView, 1)
        }

        setAnimation(null)

        paintRect = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        paintRect?.let {
            it.isAntiAlias = false
            it.color = colorProgress
            it.style = Paint.Style.FILL
            it
        }
        imgBriness = getBitmap(context, R.drawable.ic_brightness_pixel)
        rect = RectF(0f, 0f, getWidth().toFloat(), getHeight().toFloat())


        setBackgroundColor(ContextCompat.getColor(context, R.color.colorBackgroundContentWidget))


    }


    fun setColorProgress(colorProgress: Int) {
        this.colorProgress = colorProgress
        paintRect?.color = colorProgress
    }


    fun setOnControlCenterListener(onControlCenterListener: OnControlCenterListener) {
        this.onControlCenterListener = onControlCenterListener
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        width = getWidth().toFloat()
        height = getHeight().toFloat()
        valueBrightness = SettingUtils.getValueBrightness(context).toFloat()
//        valueX = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
//            (valueBrightness / maxBrightness.toFloat()) * 100f / 100f * width
//        } else {
//            (valueBrightness / maxBrightness.toFloat()) * 100f / 100f * height
//        }
        valueX = (valueBrightness / maxBrightness.toFloat()) * 100f / 100f * width
    }

    fun setValueProcess(value: Float) {
        valueX = value
        invalidate()
    }

    private val path = Path()

    private var isDrawRadius = false

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //Timber.e(".");
        clearAnimation()

        if (cornerRadius > 0 && !isDrawRadius) {
            isDrawRadius = true
            paintBackground.color = backgroundColor
            val radius = cornerRadius * height
            pathBackground.addRoundRect(
                RectF(0f, 0f, width, height),
                radius, radius, // Adjust radius as needed
                Path.Direction.CW
            )
        }
        canvas.drawPath(pathBackground, paintBackground)
        callBackUpdateBg?.onChange()
        imgBriness?.let { bitmap ->
            var adjustedValueX = max(valueX, bitmap.width / 2f + size30)
            Timber.e("hachung adjustedValueX: $adjustedValueX  /width: $width")
            if (adjustedValueX > width) {
                adjustedValueX = width
            }
            rect?.let {
                it.bottom = getHeight().toFloat()
                it.right = adjustedValueX.toInt().toFloat()
                path.reset()
                path.addRoundRect(
                    it,
                    DensityUtils.pxFromDp(context, 100f),
                    DensityUtils.pxFromDp(context, 100f),
                    Path.Direction.CW
                )
                canvas.clipPath(path)
                paintRect?.let { paintRect ->
                    canvas.drawRect(it, paintRect)
                }
            }
            canvas.drawBitmap(
                bitmap,
                adjustedValueX - bitmap.width / 2f - size30, // Bitmap moves horizontally with progress
                height / 2f - bitmap.height / 2f, // Keep Bitmap centered vertically
                paint
            )
        }


    }

    private var size30 = DensityUtils.pxFromDp(
        context,
        30f
    )


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                oldDown = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    event.x
                } else {
                    event.y
                }
                oldValueX = valueX
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.System.canWrite(context)) {
                        SettingUtils.intentActivityRequestPermission(
                            context, arrayOf<String>(
                                Manifest.permission.WRITE_SETTINGS
                            )
                        )
                        onControlCenterListener?.onExit()
                        return true
                    }
                }
                callBackUpdateBg?.onBrightnessDown()
                return true
            }

            MotionEvent.ACTION_MOVE -> {

//                isTouching = true
//                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
//                    if (abs((oldDown - event.x).toDouble()) < spaceMove) {
//                        return true
//                    }
//                    valueX = oldValueX + event.x - oldDown
//                    if (valueX <= 0 || valueX >= width) {
//                        return true
//                    }
//                    Timber.e("hachung width: $width valueX: $valueX  maxBrightness: $maxBrightness")
//                    valueBrightness = ((valueX / width) * 100f / 100f) * maxBrightness.toFloat()
//                } else {
//                    if (abs((oldDown - event.y).toDouble()) < spaceMove) {
//                        return true
//                    }
//                    valueX = oldValueX - event.y + oldDown
//                    if (valueX <= 0 || valueX >= height) {
//                        return true
//                    }
//                    valueBrightness = (valueX / height * 100f / 100f) * maxBrightness.toFloat()
//                }

                if (abs((oldDown - event.x).toDouble()) < spaceMove) {
                    return true
                }
                valueX = oldValueX + event.x - oldDown
//                if (valueX <= 0 || valueX >= width) {
//                    return true
//                }
                valueBrightness = ((valueX / width) * 100f / 100f) * maxBrightness.toFloat()
//                setValueProcess(valueX)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.System.canWrite(context)) {
                        SettingUtils.setValueBrightness(context, valueBrightness.toInt())
                    }
                } else {
                    SettingUtils.setValueBrightness(context, valueBrightness.toInt())
                }
                return true
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isTouching = false
                callBackUpdateBg?.onBrightnessUp()
            }
        }
        return true
    }

    fun isTouching(): Boolean {
        return isTouching
    }

    companion object {
        private fun getBitmap(vectorDrawable: VectorDrawable): Bitmap {
            val bitmap = Bitmap.createBitmap(
                vectorDrawable.intrinsicWidth,
                vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
            vectorDrawable.draw(canvas)
            return bitmap
        }
    }


    interface CallBackUpdateBg {
        fun onChange()

        fun onBrightnessDown()

        fun onBrightnessUp()
    }
}
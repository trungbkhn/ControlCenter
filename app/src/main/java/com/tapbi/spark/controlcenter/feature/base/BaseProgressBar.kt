package com.tapbi.spark.controlcenter.feature.base

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.ControlCenterIOSView
import com.tapbi.spark.controlcenter.utils.MethodUtils
import com.tapbi.spark.controlcenter.utils.SettingUtils
import timber.log.Timber
import kotlin.math.abs

class BaseProgressBar : View {
    private val paint = Paint()
    private var paintRect: Paint? = null
    private var valueX = 0f
    private var rect: RectF? = null
    private var valueBrightness = 0f
    var width: Float = 0f
    var height: Float = 0f
    private var callBackUpdateBg: CallBackUpdateBg? = null
    private var onControlCenterListener: ControlCenterIOSView.OnControlCenterListener? = null
    private var imgBriness: Bitmap? = null
    private var orientation = Configuration.ORIENTATION_PORTRAIT
    private var oldDown = 0f
    private var oldValueX = 0f

    var isTouching: Boolean = false
        private set
    private var maxBrightness = 255
    private val spaceMove = MethodUtils.dpToPx(6f)

    var colorRect = Color.WHITE
        set(value) {
            field = value
            paintRect?.color = value
        }

    var colorShadow = Color.parseColor("#D5d5d5")
        set(value) {
            field = value
            paintThumbStroke?.setShadowLayer(4f, 0f, 0f, value)
        }

    var colorBackgroundThumb = Color.parseColor("#9DC966")
        set(value) {
            field = value
            paintThumbBackground?.color = value
        }

    var colorStrokeThumb = Color.parseColor("#7EA152")
        set(value) {
            field = value
            paintThumbStroke?.color = value
        }

    private var paintThumbBackground: Paint? = null
    private var paintThumbStroke: Paint? = null

    var colorBackground = Color.parseColor("#E0EbC3")
        set(value) {
            field = value
            setBackgroundColor(value)
        }
    var radius = MethodUtils.dpToPx(16f).toFloat()

    constructor(context: Context?) : super(context) {
        init(null)
    }

    fun setOrientation(i: Int) {
        this.orientation = i
        invalidate()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs)
    }

    fun setValueBrightnessMax(maxBrightness: Int) {
        this.maxBrightness = maxBrightness
    }


    fun setCallBackUpdateBg(callBackUpdateBg: CallBackUpdateBg?) {
        this.callBackUpdateBg = callBackUpdateBg
    }

    private fun init(attributeSet: AttributeSet?) {
        if (attributeSet != null) {
            val array = context.obtainStyledAttributes(
                attributeSet,
                R.styleable.BrightnessMi
            )
            orientation = array.getInteger(R.styleable.BrightnessMi_orientationView, 1)
        }

        animation = null

        paintRect = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        paintRect!!.isAntiAlias = false
        paintRect!!.color =      colorRect
        paintRect!!.style = Paint.Style.FILL
        rect = RectF(0f, 0f, getWidth().toFloat(), getHeight().toFloat())

        imgBriness = getBitmap(context, R.drawable.ic_img_icon_brigness)


        // Paint cho background (fill)
        paintThumbBackground = Paint(Paint.ANTI_ALIAS_FLAG)
        paintThumbBackground?.color = colorBackgroundThumb
        paintThumbBackground?.style = Paint.Style.FILL

        // Paint cho viền (stroke)
        paintThumbStroke = Paint(Paint.ANTI_ALIAS_FLAG)
        paintThumbStroke!!.color = colorStrokeThumb
        paintThumbStroke!!.style = Paint.Style.STROKE
        paintThumbStroke!!.strokeWidth = 1f
        paintThumbStroke!!.setShadowLayer(4f, 0f, 0f, colorShadow)

        setLayerType(LAYER_TYPE_SOFTWARE, paintThumbStroke)
        setBackgroundColor(colorBackground)
    }

    private fun getBitmap(context: Context, drawableId: Int): Bitmap {
        return when (val  drawable = ContextCompat.getDrawable(context, drawableId)) {
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

    fun setOnControlCenterListener(onControlCenterListener: ControlCenterIOSView.OnControlCenterListener?) {
        this.onControlCenterListener = onControlCenterListener
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        width = getWidth().toFloat()
        height = getHeight().toFloat()
        valueBrightness = SettingUtils.getValueBrightness(context).toFloat()
        valueX = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            (valueBrightness / maxBrightness.toFloat()) * 100f / 100f * width
        } else {
            (valueBrightness / maxBrightness.toFloat()) * 100f / 100f * height
        }
    }

    fun setValueProcess(value: Float) {
        valueX = value
        invalidate()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //Timber.e(".");
        clearAnimation()

//        if (callBackUpdateBg != null) {
//            callBackUpdateBg!!.onChange()
//        }

        rect!!.bottom = getHeight().toFloat()
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            rect!!.right = valueX.toInt().toFloat()
        } else {
            rect!!.right = width.toInt().toFloat()
            rect!!.top = (height.toInt() - valueX).toInt().toFloat()
        }
        canvas.drawRect(rect!!, paintRect!!)
        // Vẽ viền với cùng góc bo tròn
        val thumbX: Float
        val thumbY: Float
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            thumbX = valueX
            thumbY = getHeight() / 2f
        } else {
            thumbX = getWidth() / 2f
            thumbY = height - valueX
        }

        canvas.drawCircle(thumbX, thumbY, thumbY, paintThumbBackground!!)
        canvas.drawCircle(thumbX, thumbY, thumbY, paintThumbStroke!!)
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            canvas.drawBitmap(
                imgBriness!!,
                imgBriness!!.width / 2f,
                getHeight() / 2f - imgBriness!!.height /  2f,
                paint
            )
        } else {
            canvas.drawBitmap(
                imgBriness!!,
                getWidth() / 2f - imgBriness!!.width / 2f,
                getHeight() - imgBriness!!.height * 1.5f,
                paint
            )
        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                oldDown = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    event.x
                } else {
                    event.y
                }
                oldValueX = valueX
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    if (!Settings.System.canWrite(context)) {
//                        SettingUtils.intentActivityRequestPermission(
//                            context,
//                            arrayOf(Manifest.permission.WRITE_SETTINGS)
//                        )
//                        onControlCenterListener?.onExit()
//                        return true
//                    }
//                }
//                if (callBackUpdateBg != null) {
//                    callBackUpdateBg!!.onDown()
//                }
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                isTouching = true
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    if (abs((oldDown - event.x).toDouble()) < spaceMove) {
                        return true
                    }
                    valueX = oldValueX + event.x - oldDown
                    if (valueX <= 0 || valueX >= width) {
                        return true
                    }
//                    valueBrightness = ((valueX / width) * 100f / 100f) * maxBrightness.toFloat()
                } else {
                    if (abs((oldDown - event.y).toDouble()) < spaceMove) {
                        return true
                    }
                    valueX = oldValueX - event.y + oldDown
                    if (valueX <= 0 || valueX >= height) {
                        return true
                    }
//                    valueBrightness = (valueX / height * 100f / 100f) * maxBrightness.toFloat()
                }
                setValueProcess(valueX)

//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    if (Settings.System.canWrite(context)) {
//                        SettingUtils.setValueBrightness(context, valueBrightness.toInt())
//                    }
//                } else {
//                    SettingUtils.setValueBrightness(context, valueBrightness.toInt())
//                }

                return true
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isTouching = false
//                if (callBackUpdateBg != null) {
//                    callBackUpdateBg!!.onUp()
//                }
            }
        }
        return true
    }



    interface CallBackUpdateBg {
        fun onChange()

        fun onDown()

        fun onUp()


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
}

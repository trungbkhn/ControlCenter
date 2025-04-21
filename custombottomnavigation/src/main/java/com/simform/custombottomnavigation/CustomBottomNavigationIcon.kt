package com.simform.custombottomnavigation

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.simform.custombottomnavigation.databinding.CustomBottomNavigationIconBinding


@Suppress("unused")
class CustomBottomNavigationIcon : ConstraintLayout {

    companion object {
        const val EMPTY_VALUE = "empty"
    }

    var defaultIconColor = 0
        set(value) {
            field = value
            if (allowDraw) {
                binding.iv.setColorFilter(if (!isEnabledCell) defaultIconColor else selectedIconColor)
            }
            //binding.iv.color = if (!isEnabledCell) defaultIconColor else selectedIconColor
        }
    var selectedIconColor = Color.parseColor("#00C957")
        set(value) {
            field = value
            if (allowDraw) {
                binding.iv.setColorFilter(if (isEnabledCell) selectedIconColor else defaultIconColor)
            }
            //binding.iv.color = if (isEnabledCell) selectedIconColor else defaultIconColor
        }
    var circleColor = 0
        set(value) {
            field = value
            if (allowDraw)
                isEnabledCell = isEnabledCell
        }

    var icon = 0
        set(value) {
            field = value
            if (allowDraw) {
                binding.iv.setImageResource(value)
            }
        }

    var iconText = ""
        set(value) {
            field = value
            if (allowDraw) {
                binding.tv.text = value
                binding.tvSelected.text = value
            }
        }

    var iconTextColor = 0
        set(value) {
            field = value
//            if (allowDraw) {
//                if (!isEnabledCell) tv.setTextColor(iconTextColor) else tv.setTextColor(
//                    selectedIconTextColor
//                )
//
//            }
        }

    var selectedIconTextColor = 0
        set(value) {
            field = value
//            if (allowDraw)
//                if (isEnabledCell) tv.setTextColor(selectedIconTextColor) else tv.setTextColor(
//                    iconTextColor
//                )
        }

    var iconTextTypeface: Typeface? = null
        set(value) {
            field = value
//            if (allowDraw && field != null)
//                tv.typeface = field
        }

    var iconTextSize = 10f
        set(value) {
            field = value
            if (allowDraw) {
                binding.tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, field)
                binding.tvSelected.setTextSize(TypedValue.COMPLEX_UNIT_PX, field)
            }
        }

    private var iconSize = dip(context, 40)
        set(value) {
            field = value
            if (allowDraw) {
                //binding.iv.size = value
                binding.iv.pivotX = iconSize / 2f
                binding.iv.pivotY = iconSize / 2f
            }
        }


    var rippleColor = 0
        set(value) {
            field = value
            if (allowDraw) {
                isEnabledCell = isEnabledCell
            }
        }

    var isFromLeft = false
    var duration = 0L
    private var progress = 0f
        set(value) {
            field = value
            binding.iv.translationY = (1f - progress) * DimensionHelper.dpToPx(10)
            binding.cv.translationY = (1f - progress) * DimensionHelper.dpToPx(10)
            binding.iv.setColorFilter(if (progress == 1f) selectedIconColor else iconTextColor)
            binding.tv.visibility = (if (progress == 1f) View.INVISIBLE else View.VISIBLE)
            binding.tvSelected.visibility = (if (progress == 1f) View.VISIBLE else View.INVISIBLE)
            binding.cv.alpha = 1 * progress
            binding.shadow.alpha = 1*progress
            val scale = 2f - (1f - progress) * (1f)
            binding.cv.scaleX = scale
            binding.cv.scaleY = scale
            if (progress == 1f) {
                binding.vClickNormal.isClickable = false
                binding.vClickSelected.isClickable = true
            } else {
                binding.vClickNormal.isClickable = true
                binding.vClickSelected.isClickable = false
            }
            /*val d = GradientDrawable()
            d.setColor(circleColor)
            d.shape = GradientDrawable.OVAL

            ViewCompat.setBackground(v_circle, d)

            ViewCompat.setElevation(v_circle, if (progress > 0.7f) dipf(context, progress * 4f) else 0f)

            val m = dip(context, 24)
            v_circle.x = (1f - progress) * (if (isFromLeft) -m else m) + ((measuredWidth - dip(context, 48)) / 2f)
            v_circle.y = (1f - progress) * measuredHeight + dip(context, 6)*/
        }

    var isEnabledCell = false
        set(value) {
            field = value
            val d = GradientDrawable()
            d.setColor(circleColor)
            d.shape = GradientDrawable.OVAL
        }

    var onClickListener: () -> Unit = {}
        set(value) {
            field = value
            binding.vClickNormal?.setOnClickListener {
                onClickListener()
            }
            binding.vClickSelected?.setOnClickListener {
                onClickListener()
            }
        }

    lateinit var binding: CustomBottomNavigationIconBinding
    private var allowDraw = false

    constructor(context: Context) : super(context) {
        initializeView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setAttributeFromXml(context, attrs)
        initializeView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setAttributeFromXml(context, attrs)
        initializeView()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun setAttributeFromXml(context: Context, attrs: AttributeSet) {
    }

    private fun initializeView() {
        allowDraw = true
        binding =
            CustomBottomNavigationIconBinding.inflate(LayoutInflater.from(context), this, true)
        draw()
    }

    private fun draw() {
        if (!allowDraw)
            return

        icon = icon
        iconSize = iconSize
        iconTextTypeface = iconTextTypeface
        iconTextColor = iconTextColor
        selectedIconTextColor = selectedIconTextColor
        iconTextSize = iconTextSize
        rippleColor = rippleColor
        onClickListener = onClickListener
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        progress = progress
    }

    fun disableCell() {
        if (isEnabledCell)
            animateProgress(false)
        isEnabledCell = false
    }

    fun enableCell(isAnimate: Boolean = true) {
        if (!isEnabledCell)
            animateProgress(true, isAnimate)
        isEnabledCell = true
    }

    private fun animateProgress(enableCell: Boolean, isAnimate: Boolean = true) {
        val d = if (enableCell) duration else 250
        val anim = ValueAnimator.ofFloat(0f, 1f)
        anim.apply {
            startDelay = if (enableCell) d / 4 else 0L
            duration = if (isAnimate) d else 1L
            interpolator = FastOutSlowInInterpolator()
            addUpdateListener {
                val f = it.animatedFraction
                progress = if (enableCell)
                    f
                else
                    1f - f
            }
            start()
        }
    }
}
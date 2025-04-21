package com.tapbi.spark.controlcenter.views

import android.animation.Animator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

class ExpandableConstraintLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr) {

    var animationDuration: Int = 0
    private val isVertical: Boolean
    private var expansion: Float = 0.toFloat()
    private val displacement: Float
    var interpolator: TimeInterpolator? = null
    private var listenerExpand: IListenerExpand? = null
    private var valueAnimator: ValueAnimator? = null
    private var currentStatus = ExpandableConstraintLayoutStatus.IDLE

    val isExpanded: Boolean
        get() = expansion == 1f

    init {
        // default values
        animationDuration = 300
        interpolator = FastOutSlowInInterpolator()
        isVertical = false
        displacement = 1f
        expansion = 1f
    }

    // Overridden Methods
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measuredWidth
        val height = measuredHeight
        val size = if (isVertical) height else width
        visibility = if (expansion == 0f && size == 0) View.INVISIBLE else View.VISIBLE
        val expansionDelta = size - Math.round(size * expansion)
        // translate all children before measuring the parent
        if (displacement > 0) {
            val displacementDelta = expansionDelta * displacement
            for (i in 0 until childCount) {
                if (isVertical) {
                    getChildAt(i).translationY = -displacementDelta
                } else {
                    val direction = -1
                    getChildAt(i).translationX = direction * displacementDelta
                }
            }
        }
        if (isVertical) {
            setMeasuredDimension(width, height - expansionDelta)
        } else {
            setMeasuredDimension(width - expansionDelta, height)
        }
    }



    fun expand() {
        shouldExpand(true, true)
    }

    fun collapse() {
        shouldExpand(false, true)
    }

    private fun shouldExpand(shouldExpand: Boolean, shouldAnimate: Boolean) {
        if (shouldExpand && (currentStatus == ExpandableConstraintLayoutStatus.EXPANDING || expansion == 1f)) {
            return
        }
        if (!shouldExpand && (currentStatus == ExpandableConstraintLayoutStatus.COLLAPSING || expansion == 0f)) {
            return
        }
        val newExpansion = if (shouldExpand) 1F else 0F
        if (shouldAnimate) {
            animateExpansion(newExpansion)
        } else {
            setExpansion(newExpansion)
        }
    }

    private fun setExpansion(newExpansion: Float) {
        // Nothing to do here
        if (this.expansion == newExpansion) {
            return
        }
        this.expansion = newExpansion
        visibility = if (expansion == 0f) View.INVISIBLE else View.VISIBLE
        requestLayout()
    }

    private fun animateExpansion(newExpansion: Float) {
        if (valueAnimator != null) {
            valueAnimator?.cancel()
            valueAnimator = null
        }
        valueAnimator = ValueAnimator.ofFloat(expansion, newExpansion)
        valueAnimator?.interpolator = interpolator
        valueAnimator?.duration = animationDuration.toLong()
        valueAnimator?.addUpdateListener { valueAnimator -> setExpansion(valueAnimator.animatedValue as Float) }
        valueAnimator?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                currentStatus = if (newExpansion <= 0) {
                    ExpandableConstraintLayoutStatus.COLLAPSING
                } else {
                    ExpandableConstraintLayoutStatus.EXPANDING
                }
                listenerExpand?.onExpand(currentStatus==ExpandableConstraintLayoutStatus.EXPANDING)

            }

            override fun onAnimationEnd(animation: Animator) {

                currentStatus = ExpandableConstraintLayoutStatus.IDLE
            }

            override fun onAnimationCancel(animation: Animator) {
                currentStatus = ExpandableConstraintLayoutStatus.IDLE
            }

            override fun onAnimationRepeat(animation: Animator) {}
        })
        valueAnimator?.start()
    }

    // Listener Related
    fun setListenerExpand(listenerExpand: IListenerExpand) {
        this.listenerExpand = listenerExpand
    }
    fun interface IListenerExpand{
        fun onExpand(isExpanded: Boolean)
    }

}// Constructors

enum class ExpandableConstraintLayoutStatus {
    IDLE, EXPANDING, COLLAPSING
}
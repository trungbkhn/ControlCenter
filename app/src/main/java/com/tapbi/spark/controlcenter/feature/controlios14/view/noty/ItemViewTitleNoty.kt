package com.tapbi.spark.controlcenter.feature.controlios14.view.noty

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver.OnScrollChangedListener
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.eventbus.EventShowHideRootView
import com.tapbi.spark.controlcenter.feature.controlios14.adapter.notygroup.NotyGroupAdapter
import com.tapbi.spark.controlcenter.utils.MethodUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class ItemViewTitleNoty(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {
    private var tvTitle: TextView? = null
    private var tvClearAllNoty: TextView? = null
    private var tvClearAllNotyInvisible: TextView? = null
    private var imgClearAllNoty: ImageView? = null
    private var rlDelete: ConstraintLayout? = null
    private var listener: NotyGroupAdapter.OnGroupNotyClickListener? = null
    private var sizeImgClear = 0
    private var allowStartAnimHide = true


    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    fun setListener(listener: NotyGroupAdapter.OnGroupNotyClickListener) {
        this.listener = listener
    }

    private fun initView() {
        sizeImgClear = MethodUtils.dpToPx(context, 30f);
        tvTitle = findViewById(R.id.tvNotificaionCenter)
        tvClearAllNoty = findViewById(R.id.tvClearAllNoty)
        tvClearAllNotyInvisible = findViewById(R.id.tvClearAllNotyInvisible)
        imgClearAllNoty = findViewById(R.id.imgClearAllNoty)
        rlDelete = findViewById(R.id.rlDelete)

        tvClearAllNoty?.setOnClickListener {
            //Timber.e("tvClearAllNoty")
            listener?.onClearAllNoty()
            startAniHide()
        }

        imgClearAllNoty?.setOnClickListener {
            //Timber.e("imgClearAllNoty")
            startAniShow()
        }

    }

//    override fun onVisibilityChanged(changedView: View, visibility: Int) {
//        super.onVisibilityChanged(changedView, visibility)
//        if (visibility == VISIBLE && tvClearAllNoty?.visibility == VISIBLE) {
//            allowStartAnimHide = true
//            val lp = rlDelete!!.layoutParams
//            lp.width = sizeImgClear
//            rlDelete?.requestLayout()
//            tvClearAllNoty?.visibility = GONE
//            imgClearAllNoty?.visibility = VISIBLE
//        }
//    }

    @Subscribe
    public fun eventOnShowHideRootView(eventShowHideRootView: EventShowHideRootView) {
        if (eventShowHideRootView.isShow && tvClearAllNoty?.visibility == VISIBLE) {
            allowStartAnimHide = true
            val lp = rlDelete!!.layoutParams
            lp.width = sizeImgClear
            rlDelete?.requestLayout()
            tvClearAllNoty?.visibility = GONE
            imgClearAllNoty?.visibility = VISIBLE
        }
    }

    var aniShow: ValueAnimator? = null
    private fun startAniShow() {
        aniShow?.cancel()
        aniShow = ValueAnimator.ofInt(rlDelete!!.width, tvClearAllNotyInvisible!!.width)
        aniShow?.addUpdateListener { animation ->
            val translation = animation.animatedValue as Int
            val lp = rlDelete!!.layoutParams
            lp.width = translation
            rlDelete?.requestLayout()
        }
        aniShow?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {

            }

            override fun onAnimationEnd(p0: Animator) {
                imgClearAllNoty!!.visibility = View.GONE
                tvClearAllNoty!!.visibility = VISIBLE
            }

            override fun onAnimationCancel(p0: Animator) {

            }

            override fun onAnimationRepeat(p0: Animator) {

            }
        })
        aniShow?.setDuration(200)?.start()
    }

    private fun startAniHide() {
        aniShow?.cancel()
        aniShow = ValueAnimator.ofInt(rlDelete!!.width, sizeImgClear)
        aniShow?.addUpdateListener { animation ->
            val translation = animation.animatedValue as Int
            val lp = rlDelete!!.layoutParams
            lp.width = translation
            rlDelete?.requestLayout()
        }

        aniShow?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {
                allowStartAnimHide = false
            }

            override fun onAnimationEnd(p0: Animator) {
                allowStartAnimHide = true
            }

            override fun onAnimationCancel(p0: Animator) {

            }

            override fun onAnimationRepeat(p0: Animator) {

            }
        })
        imgClearAllNoty!!.visibility = VISIBLE
        tvClearAllNoty!!.visibility = GONE
        aniShow?.setDuration(200)?.start()
    }



    private val listenerScroll = OnScrollChangedListener {
        if (allowStartAnimHide && rlDelete?.width != sizeImgClear) {
            startAniHide()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        EventBus.getDefault().register(this)
        viewTreeObserver.addOnScrollChangedListener(listenerScroll)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        EventBus.getDefault().unregister(this)
        viewTreeObserver.removeOnScrollChangedListener(listenerScroll)
    }
}
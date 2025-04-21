package com.tapbi.spark.controlcenter.feature.controlios14.view.noty

import android.animation.ValueAnimator
import android.content.Context
import android.text.TextPaint
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.addListener
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.databinding.ViewTitleChildNotyBinding
import com.tapbi.spark.controlcenter.interfaces.IListenerTitleChildNoty
import com.tapbi.spark.controlcenter.utils.MethodUtils
import timber.log.Timber

class ViewTitleChildNoty : ConstraintLayout {
    private var binding: ViewTitleChildNotyBinding? = null
    private var listener: IListenerTitleChildNoty? = null
    private var animClear: ValueAnimator? = null
    private var animShowLess: ValueAnimator? = null
    private var sizeLayoutShowLessExpand = 0
    private var sizeImgClear = 0

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        binding = ViewTitleChildNotyBinding.inflate(LayoutInflater.from(context), this, true)

        setLayoutDefault()

        binding?.bgShowLess?.setItemView(
            binding!!.lnShowLess,
            ImageBackgroundItemView.TypeView.ITEM_TITLE_CHILD
        )
        binding?.bgClear?.setItemView(
            binding!!.lnDelete,
            ImageBackgroundItemView.TypeView.ITEM_TITLE_CHILD
        )

        binding?.viewClickShowLess?.setOnClickListener {
            //Timber.e("...")
            if (binding?.lnShowLess?.contentDescription == Constant.SHOW) {
                listener?.showLess()
            } else {
                aniExpandShowLess()
            }
        }

        binding?.viewClickClear?.setOnClickListener {
            //Timber.e("...")
            if (binding?.lnDelete?.contentDescription == Constant.SHOW) {
                listener?.clearAll()
                binding?.lnDelete?.visibility = GONE
                binding?.lnShowLess?.contentDescription = Constant.SHOW
                binding?.lnDelete?.contentDescription = Constant.HIDE
                showTvShowLess(true)
                showTvClear(false)
                binding!!.lnShowLess.layoutParams.width = sizeLayoutShowLessExpand
                binding!!.lnShowLess.requestLayout()

            } else {
                aniExpandClear()
            }
        }
    }

    fun setData(title: String, isCanDeleteGroup: Boolean, listener: IListenerTitleChildNoty) {
        this.listener = listener
        binding?.tvAppNameExpand?.text = title
        setLayoutDefault()
        if (isCanDeleteGroup) {
            binding?.lnDelete?.visibility = VISIBLE
        } else {
            binding?.lnDelete?.visibility = GONE
        }
    }

    private fun setLayoutDefault() {
        sizeImgClear = MethodUtils.dpToPx(context, 30f)
        sizeLayoutShowLessExpand =
            (sizeImgClear + MethodUtils.dpToPx(context, 10f) + getWidthText()).toInt()
        binding!!.lnShowLess.layoutParams.width = sizeLayoutShowLessExpand
        binding!!.lnShowLess.requestLayout()

        binding!!.lnDelete.layoutParams.width = sizeImgClear
        binding!!.lnDelete.requestLayout()

        showTvShowLess(true)
        showTvClear(false)

        binding?.lnShowLess?.contentDescription = Constant.SHOW
        binding?.lnDelete?.contentDescription = Constant.HIDE
    }

    private fun aniExpandClear() {

        binding?.lnShowLess?.contentDescription = Constant.HIDE
        binding?.lnDelete?.contentDescription = Constant.SHOW

        animClear?.cancel()
        animClear = ValueAnimator.ofInt(binding!!.lnDelete.width, sizeLayoutShowLessExpand)
        animClear?.addUpdateListener {
            val value = it.animatedValue as Int
            binding!!.lnDelete.layoutParams.width = value
            binding!!.lnDelete.requestLayout()
        }
        showTvClear(true)
        animClear?.setDuration(200)?.start()


        animShowLess?.cancel()
        animShowLess = ValueAnimator.ofInt(binding!!.lnShowLess.width, sizeImgClear)
        animShowLess?.addUpdateListener {
            val value = it.animatedValue as Int
            binding!!.lnShowLess.layoutParams.width = value
            binding!!.lnShowLess.requestLayout()
        }

        showTvShowLess(false)
        animShowLess?.setDuration(200)?.start()

    }

    private fun aniExpandShowLess() {

        binding?.lnShowLess?.contentDescription = Constant.SHOW
        binding?.lnDelete?.contentDescription = Constant.HIDE

        animClear?.cancel()
        animClear = ValueAnimator.ofInt(binding!!.lnDelete.width, sizeImgClear)
        animClear?.addUpdateListener {
            val value = it.animatedValue as Int
            val lp = binding!!.lnDelete.layoutParams
            lp.width = value
            binding!!.lnDelete.requestLayout()
        }
        animClear?.addListener(onEnd = {
            Timber.e("hachung : ")
            showTvClear(false)
        })
        animClear?.setDuration(200)?.start()


        animShowLess?.cancel()
        animShowLess = ValueAnimator.ofInt(binding!!.lnShowLess.width, sizeLayoutShowLessExpand)
        animShowLess?.addUpdateListener {
            val value = it.animatedValue as Int
            val lp = binding!!.lnShowLess.layoutParams
            lp.width = value
            binding!!.lnShowLess.requestLayout()
        }
        showTvShowLess(true)
        animShowLess?.setDuration(200)?.start()

    }

    private fun showTvClear(isShow: Boolean) {

        binding?.tvDelete?.visibility = if (isShow) VISIBLE else GONE
        binding?.imgClearAllNoty?.visibility = if (isShow) GONE else VISIBLE
    }

    private fun showTvShowLess(isShow: Boolean) {

        binding?.tvShowLess?.visibility = if (isShow) VISIBLE else GONE
    }

    private fun getWidthText(): Float {
        val text =
            if (context.getString(R.string.show_less).length > context.getString(R.string.text_clear).length) context.getString(
                R.string.show_less
            ) else context.getString(R.string.delete_noty)
        val textPaint = TextPaint()
        textPaint.textSize = 13F * resources.displayMetrics.density
        var widthRs = textPaint.measureText(text)
        App.widthHeightScreenCurrent.let {
            if (widthRs > it.w / 4) {
                widthRs = (it.w / 4).toFloat()
            }
        }
        return widthRs
    }

    fun updateBlur() {
        binding?.bgShowLess?.updateBackgroundHorizontal()
        binding?.bgClear?.updateBackgroundHorizontal()
    }
}
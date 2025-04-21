package com.tapbi.spark.controlcenter.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.ads.nativetemplates.TemplateViewMultiAds
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.data.model.ItemOnboarding
import com.tapbi.spark.controlcenter.databinding.ItemAdsOnboardBinding
import com.tapbi.spark.controlcenter.databinding.ItemOnboardBinding
import com.tapbi.spark.controlcenter.utils.MethodUtils.dpToPx
import com.tapbi.spark.controlcenter.utils.setCenterBottomCrop

class ViewPagerOnBoardAdapter : PagerAdapter() {
    val list: MutableList<ItemOnboarding> = mutableListOf()
    private var bindingAds: ItemAdsOnboardBinding? = null
    private var binding: ItemOnboardBinding? = null



    private var onClickCloseAdsFull: () -> Unit = { }
    fun setOnClickCloseAdsFull(onClickCloseAdsFull: () -> Unit = { }) {
        this.onClickCloseAdsFull = onClickCloseAdsFull
    }

    override fun getCount(): Int {
        return list.size
    }

    fun setDataList(list: List<ItemOnboarding>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    override fun isViewFromObject(view: View, ob: Any): Boolean {
        return view == ob
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        if (list[position].isFullAds) {
            bindingAds = ItemAdsOnboardBinding.inflate(
                LayoutInflater.from(container.context),
                container,
                false
            )
                .apply {
                    val params = imClose.layoutParams as ViewGroup.MarginLayoutParams
                    params.topMargin = App.statusBarHeight + 20
                    imClose.layoutParams = params
                    container.addView(root)
                    imClose.setOnClickListener {
                        onClickCloseAdsFull()
                    }
                }
            return bindingAds!!.root
        } else {
            binding =
                ItemOnboardBinding.inflate(LayoutInflater.from(container.context), container, false)
                    .apply {
                        tvTitle.text = list[position].title
                        tvContent.text = list[position].description
                        imPreview.scaleType =
                            if (position == 2) ImageView.ScaleType.MATRIX else ImageView.ScaleType.FIT_CENTER
                        val params = imPreview.layoutParams as ViewGroup.MarginLayoutParams
                        params.topMargin = if (position == 2) 0 else dpToPx(84f)
                        imPreview.layoutParams = params

                        val glideRequest = Glide.with(container.context)
                            .load(list[position].image)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)

                        if (position == 2) {
                            glideRequest.into(object : CustomTarget<Drawable>() {
                                override fun onResourceReady(
                                    resource: Drawable,
                                    transition: Transition<in Drawable>?
                                ) {
                                    imPreview.setImageDrawable(resource)
                                    imPreview.setCenterBottomCrop()
                                }

                                override fun onLoadCleared(placeholder: Drawable?) {
                                    imPreview.setBackgroundResource(R.color.color_bg_card1)
                                }
                            })
                        } else {
                            glideRequest.error(R.color.color_bg_card1).into(imPreview)
                        }

                        container.addView(root)
                    }
            return binding!!.root
        }


    }

    fun getViewAds(): TemplateViewMultiAds? {
        return if (bindingAds != null) {
            bindingAds!!.frAds
        } else {
            null
        }

    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}
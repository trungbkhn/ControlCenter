package com.tapbi.spark.controlcenter.feature.controlcenter.view.control.pixel.view

import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.tapbi.spark.controlcenter.databinding.LayoutControlPixelBinding
import com.tapbi.spark.controlcenter.databinding.LayoutControlPixelLandBinding
import com.tapbi.spark.controlcenter.feature.mishade.view.NoScrollViewPager
import com.tapbi.spark.controlcenter.feature.mishade.view.customdot.WormDotsIndicator

abstract class CommonPixelBinding {
    abstract val imgBg: ImageView
    abstract val imgSetting: ImageView
    abstract val imgEdit: ImageView
    abstract val tvDate: TextView
    abstract val tvTime: TextView
    abstract val viewProcessBrightness: BrightnessPixel
    abstract val layoutControl: ConstraintLayout
}

class PixelPortraitBinding(private val binding: LayoutControlPixelBinding) :
    CommonPixelBinding() {
    override val imgBg: ImageView
        get() = binding.bg
    override val imgSetting: ImageView
        get() = binding.imgSettings
    override val imgEdit: ImageView
        get() = binding.imEdit
    override val tvDate: TextView
        get() = binding.tvDate
    override val tvTime: TextView
        get() = binding.tvTime
    override val viewProcessBrightness: BrightnessPixel
        get() = binding.viewProcessBrightness
    override val layoutControl: ConstraintLayout
        get() = binding.layoutControl
    val viewPager: NoScrollViewPager
        get() = binding.vgControl
    val wormDotsIndicator: WormDotsIndicator
        get() = binding.wormDotsIndicator
    val layout: ConstraintLayout
        get() = binding.layoutControl
}


class PixelLandscapeBinding(private val binding: LayoutControlPixelLandBinding) :
    CommonPixelBinding() {
    override val imgBg: ImageView
        get() = binding.bg
    override val imgSetting: ImageView
        get() = binding.imgSettings
    override val imgEdit: ImageView
        get() = binding.imEdit
    override val tvDate: TextView
        get() = binding.tvDate
    override val tvTime: TextView
        get() = binding.tvTime
    override val viewProcessBrightness: BrightnessPixel
        get() = binding.viewProcessBrightness

    override val layoutControl: ConstraintLayout
        get() = binding.layoutControl
    val rvControl1: RecyclerView
        get() = binding.rvControl1
    val rvControl2: RecyclerView
        get() = binding.rvControl2

}
package com.tapbi.spark.controlcenter.feature.controlcenter.view.control.samsung.view

import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.tapbi.spark.controlcenter.databinding.LayoutControlSamsungBinding
import com.tapbi.spark.controlcenter.databinding.LayoutControlSamsungLandBinding
import com.tapbi.spark.controlcenter.feature.mishade.view.NoScrollViewPager

abstract class CommonSamSungBinding {

    abstract val imgBg: ImageView
    abstract val layoutControl: ConstraintLayout
    abstract val seekBarVolume: SeekBar
    abstract val seekBarBrightness: SeekBar
    abstract val cardView: CardView

}

class SamSungPortraitBinding(val binding: LayoutControlSamsungBinding) : CommonSamSungBinding() {
    override val imgBg: ImageView
        get() = binding.imBg
    override val layoutControl: ConstraintLayout
        get() = binding.layoutControl
    override val seekBarVolume: SeekBar
        get() = binding.seekBarVolume
    override val seekBarBrightness: SeekBar
        get() = binding.seekBarBrightness
    override val cardView: CardView
        get() = binding.cardView

}

class SamSungLandscapeBinding(val binding: LayoutControlSamsungLandBinding) :
    CommonSamSungBinding() {
    override val imgBg: ImageView
        get() = binding.imBg
    override val layoutControl: ConstraintLayout
        get() = binding.layoutControl
    override val seekBarVolume: SeekBar
        get() = binding.seekBarVolume
    override val seekBarBrightness: SeekBar
        get() = binding.seekBarBrightness
    override val cardView: CardView
        get() = binding.cardView


}
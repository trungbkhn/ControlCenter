package com.tapbi.spark.controlcenter.feature.controlcenter.view.control.micenter.view

import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.databinding.ViewStubProxy
import androidx.recyclerview.widget.RecyclerView
import com.tapbi.spark.controlcenter.databinding.LayoutMiControlCenterBinding
import com.tapbi.spark.controlcenter.databinding.LayoutMiControlCenterLandBinding
import com.tapbi.spark.controlcenter.ui.custom.RoundedImageView

abstract class CommonMiCenterBinding {
    abstract val layoutParent: ConstraintLayout
    abstract val layoutControl: ConstraintLayout
    abstract val imgBg: ImageView
    abstract val imgMusic: MiMusicView
    abstract val imgSetting: ImageView
    abstract val imgEdit: ImageView
    abstract val imgScroll: ImageView
    abstract val tvDate: TextView
    abstract val tvTime: TextView
    abstract val viewProcessBrightness: BrightnessMi
    abstract val imgProcessBrightness: ImageView
    abstract val imgAutoBrightness: RoundedImageView
    abstract val flProcessBrightness: CardView
    abstract val rccExpand: RecyclerView
    abstract val rccCollapse: RecyclerView
    abstract val gdCenter: Guideline
    abstract val titleScreen: TextView

    abstract val viewStubChangeAction: ViewStubProxy


}

class MiCenterPortraitBinding(private val binding: LayoutMiControlCenterBinding) :
    CommonMiCenterBinding() {
    override val layoutParent: ConstraintLayout
        get() = binding.root
    override val layoutControl: ConstraintLayout
        get() = binding.layoutControl
    override val imgBg: ImageView
        get() = binding.bg
    override val imgMusic: MiMusicView
        get() = binding.imgMusic
    override val imgSetting: ImageView
        get() = binding.imgSetting
    override val imgEdit: ImageView
        get() = binding.imgEdit
    override val imgScroll: ImageView
        get() = binding.imgScroll
    override val tvDate: TextView
        get() = binding.tvDate
    override val tvTime: TextView
        get() = binding.tvTime
    override val viewProcessBrightness: BrightnessMi
        get() = binding.imgProcessBrightness
    override val imgProcessBrightness: ImageView
        get() = binding.imgBrightness
    override val imgAutoBrightness: RoundedImageView
        get() = binding.imgAutoBrightness
    override val flProcessBrightness: CardView
        get() = binding.flProcessBrightness
    override val rccExpand: RecyclerView
        get() = binding.rccExpand
    override val rccCollapse: RecyclerView
        get() = binding.rccCollapse
    override val gdCenter: Guideline
        get() = binding.gdCenter
    override val titleScreen: TextView
        get() = binding.titleScreen
    override val viewStubChangeAction: ViewStubProxy
        get() = binding.viewSubLayoutChangeAction


}


class MiCenterLandscapeBinding(private val binding: LayoutMiControlCenterLandBinding) :
    CommonMiCenterBinding() {
    override val layoutParent: ConstraintLayout
        get() = binding.root
    override val layoutControl: ConstraintLayout
        get() = binding.layoutControl
    override val imgBg: ImageView
        get() = binding.bg
    override val imgMusic: MiMusicView
        get() = binding.imgMusic
    override val imgSetting: ImageView
        get() = binding.imgSetting
    override val imgEdit: ImageView
        get() = binding.imgEdit
    override val imgScroll: ImageView
        get() = binding.imgScroll
    override val tvDate: TextView
        get() = binding.tvDate
    override val tvTime: TextView
        get() = binding.tvTime
    override val viewProcessBrightness: BrightnessMi
        get() = binding.imgProcessBrightness
    override val imgProcessBrightness: ImageView
        get() = binding.imgBrightness
    override val imgAutoBrightness: RoundedImageView
        get() = binding.imgAutoBrightness
    override val flProcessBrightness: CardView
        get() = binding.flProcessBrightness
    override val rccExpand: RecyclerView
        get() = binding.rccExpand
    override val rccCollapse: RecyclerView
        get() = binding.rccCollapse
    override val gdCenter: Guideline
        get() = binding.gdCenter
    override val titleScreen: TextView
        get() = binding.titleScreen
    override val viewStubChangeAction: ViewStubProxy
        get() = binding.viewSubLayoutChangeAction


}
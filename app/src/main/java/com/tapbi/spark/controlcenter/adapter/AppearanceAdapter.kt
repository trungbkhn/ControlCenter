package com.tapbi.spark.controlcenter.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant.POS_IOS
import com.tapbi.spark.controlcenter.common.Constant.POS_MI_CONTROL
import com.tapbi.spark.controlcenter.common.Constant.POS_MI_SHADE
import com.tapbi.spark.controlcenter.databinding.ItemAppearanceBinding

class AppearanceAdapter(private val viewPager: ViewPager2, private var posSelected: Int) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    inner class ViewHolder(val binding: ItemAppearanceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(pos: Int) {
            val resource: Int
            when (pos) {
                POS_MI_CONTROL -> {
                    resource = R.drawable.preview_control_mi_appearance
                }

                POS_IOS -> {
                    resource = R.drawable.preview_ios_control_appearance
                }

                POS_MI_SHADE -> {
                    resource = R.drawable.preview_control_mi_shade_appearance
                }

                else -> {
                    resource = R.drawable.preview_ios_control_appearance
                }
            }
            binding.vBgSelect.visibility = if (posSelected == pos) View.VISIBLE else View.INVISIBLE
            Glide.with(binding.imThumb).load(resource).into(binding.imThumb)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): RecyclerView.ViewHolder {
        return ViewHolder(
            ItemAppearanceBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(position)
    }

    override fun getItemCount(): Int {
        return 3
    }

    fun changePageSelected(posSelected: Int) {
        this.posSelected = posSelected
        val rcv = viewPager[0] as RecyclerView
        val holderMiControl = rcv.findViewHolderForAdapterPosition(POS_MI_CONTROL)
        val holderIos = rcv.findViewHolderForAdapterPosition(POS_IOS)
        val holderMiShade = rcv.findViewHolderForAdapterPosition(POS_MI_SHADE)

        if (holderMiControl is ViewHolder) {
            holderMiControl.binding.vBgSelect.visibility =
                if (posSelected == POS_MI_CONTROL) View.VISIBLE else View.INVISIBLE
        }
        if (holderIos is ViewHolder) {
            holderIos.binding.vBgSelect.visibility =
                if (posSelected == POS_IOS) View.VISIBLE else View.INVISIBLE
        }
        if (holderMiShade is ViewHolder) {
            holderMiShade.binding.vBgSelect.visibility =
                if (posSelected == POS_MI_SHADE) View.VISIBLE else View.INVISIBLE
        }
    }
}
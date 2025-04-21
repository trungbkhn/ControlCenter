package com.tapbi.spark.controlcenter.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.databinding.ItemMiControlsBinding
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper
import com.tapbi.spark.controlcenter.utils.hide
import com.tapbi.spark.controlcenter.utils.show
import timber.log.Timber


class ColorControlsAdapter : RecyclerView.Adapter<ColorControlsAdapter.ColorControlsHolder>() {
    private var clickListener: ClickListener? = null
    private var previousSelect = -1
    private var currentSelect = -1
    private val listColor = mutableListOf<Int>()
    fun setClickListener(clickListener: ClickListener) {
        this.clickListener = clickListener
    }

    fun setData(listColor: MutableList<Int>) {
        this.listColor.clear()
        this.listColor.addAll(listColor)
        notifyDataSetChanged()
    }

    fun setSelect(currentSelect: Int, color: Int) {
        this.previousSelect = this.currentSelect
        if (currentSelect == -1) {
            this.listColor.add(0, color)
            this.currentSelect = 0
        } else {
            this.currentSelect = currentSelect
        }
        if (previousSelect != RecyclerView.NO_POSITION) {
            notifyItemChanged(previousSelect)
        }
        if (currentSelect != RecyclerView.NO_POSITION) {
            notifyItemChanged(currentSelect)

        }
    }

    class ColorControlsHolder(var binding: ItemMiControlsBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorControlsHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemMiControlsBinding =
            DataBindingUtil.inflate(inflater, R.layout.item_mi_controls, parent, false)
        return ColorControlsHolder(binding)
    }

    @Suppress("DEPRECATION")
    override fun onBindViewHolder(holder: ColorControlsHolder, position: Int) {
        ViewCompat.setBackgroundTintList(
            holder.binding.icColor,
            ColorStateList.valueOf(listColor[holder.adapterPosition])
        )
        if (currentSelect == holder.adapterPosition) {
            holder.binding.viewBorder.show()
        } else {
            holder.binding.viewBorder.hide()
        }
        holder.binding.icColor.setOnClickListener {
            ViewHelper.preventTwoClick(it, 500)
            if (currentSelect != holder.adapterPosition) {
                previousSelect = currentSelect
                currentSelect = holder.adapterPosition
                notifyItemChanged(previousSelect)
                notifyItemChanged(currentSelect)
                clickListener?.onChooseColor(listColor[holder.adapterPosition])
            }
        }
    }

    override fun getItemCount(): Int {
        return listColor.size
    }

    interface ClickListener {
        fun onChooseColor(resColor: Int)
    }
}
package com.tapbi.spark.controlcenter.utils

import androidx.recyclerview.widget.DiffUtil
import com.tapbi.spark.controlcenter.feature.controlios14.model.ControlCustomize

class ControlCustomizeDiffCallback : DiffUtil.ItemCallback<ControlCustomize>() {
    override fun areItemsTheSame(oldItem: ControlCustomize, newItem: ControlCustomize): Boolean {
        return oldItem.packageName == newItem.packageName
    }

    override fun areContentsTheSame(oldItem: ControlCustomize, newItem: ControlCustomize): Boolean {
        return oldItem == newItem
    }
}

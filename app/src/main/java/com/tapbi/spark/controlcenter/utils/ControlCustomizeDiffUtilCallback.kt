package com.tapbi.spark.controlcenter.utils

import androidx.recyclerview.widget.DiffUtil
import com.tapbi.spark.controlcenter.feature.controlios14.model.ControlCustomize

class ControlCustomizeDiffUtilCallback (private val oldList: List<ControlCustomize>, private val newList: List<ControlCustomize>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].packageName == newList[newItemPosition].packageName

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}

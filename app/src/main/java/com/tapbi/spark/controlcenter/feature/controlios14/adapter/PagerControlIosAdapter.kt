package com.tapbi.spark.controlcenter.feature.controlios14.adapter

import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView


class PagerControlIosAdapter(val page1: View, val page2: View, val page3: View) : RecyclerView.Adapter<PagerControlIosAdapter.ViewHolder>() {

    companion object {
        private const val TYPE_PAGE_1 = 0
        private const val TYPE_PAGE_2 = 1
        private const val TYPE_PAGE_3 = 2
    }

    override fun getItemCount(): Int = 3

    override fun getItemViewType(position: Int): Int = position
    inner class ViewHolder(val itemViewBinding: View) : RecyclerView.ViewHolder(itemViewBinding)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            TYPE_PAGE_1 -> {
                page1.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                ViewHolder(page1)
            }
            TYPE_PAGE_2 -> {
                page2.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                ViewHolder(page2)
            }
            else -> {
                page3.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                ViewHolder(page3)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Nếu cần binding dữ liệu, xử lý ở đây
    }

}



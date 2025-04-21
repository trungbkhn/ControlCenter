package com.tapbi.spark.controlcenter.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.data.model.ItemUserManual
import com.tapbi.spark.controlcenter.databinding.ItemUserManualBinding

class ViewPageAdapterUserManual(context: Context) :
    RecyclerView.Adapter<ViewPageAdapterUserManual.ViewHolder>() {
    private val listPage = mutableListOf<ItemUserManual>()

    init {
        listPage.add(
            ItemUserManual(
                context.getString(
                    R.string._1_turn_on_,
                    context.getString(R.string.app_name)
                ),
                context.getString(
                    R.string.click_the_button_to_active_,
                    context.getString(R.string.app_name)
                ),
                R.drawable.img_user_manual_1
            )
        )
        listPage.add(
            ItemUserManual(
                context.getString(
                    R.string._2_swipe_to_open_the_,
                    context.getString(R.string.app_name)
                ),
                context.getString(
                    R.string.swipe_down_from_the_top_to_open_the_,
                    context.getString(R.string.app_name)
                ),
                R.drawable.img_user_manual_2
            )
        )
        listPage.add(
            ItemUserManual(
                context.getString(
                    R.string._3_customize_your_,
                    context.getString(R.string.app_name)
                ),
                context.getString(R.string.click_the_button_to_customize),
                R.drawable.img_user_manual_3
            )
        )
    }

    inner class ViewHolder(val binding: ItemUserManualBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.tvTitle.text = listPage[position].title
            binding.tvContent.text = listPage[position].description
            binding.imPreview.setImageResource(listPage[position].image)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewPageAdapterUserManual.ViewHolder {
        return ViewHolder(
            ItemUserManualBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewPageAdapterUserManual.ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return listPage.size
    }
}
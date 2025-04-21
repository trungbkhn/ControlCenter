package com.tapbi.spark.controlcenter.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.data.model.GroupColor
import com.tapbi.spark.controlcenter.databinding.ItemGroupColorBinding
import com.tapbi.spark.controlcenter.utils.Utils
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper
import com.tapbi.spark.controlcenter.utils.hide
import com.tapbi.spark.controlcenter.utils.invisible
import com.tapbi.spark.controlcenter.utils.show

class GroupColorControlsAdapter : RecyclerView.Adapter<GroupColorControlsAdapter.GroupColorControlsHolder>() {
    private var clickListener : ClickListener? = null
    private var previousSelect = 0
    private var currentSelect = 0
    private val listGroupColor = mutableListOf<GroupColor>()

    fun setClickListener(clickListener: ClickListener){
        this.clickListener = clickListener
    }
    fun setData(list : List<GroupColor>){
        listGroupColor.clear()
        listGroupColor.addAll(list)
        notifyDataSetChanged()
    }
    fun setSelect(select : Int){
        previousSelect = currentSelect
        currentSelect = select
        notifyItemChanged(previousSelect)
        notifyItemChanged(currentSelect)
    }

    class GroupColorControlsHolder(var binding : ItemGroupColorBinding) : RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupColorControlsHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemGroupColorBinding = DataBindingUtil.inflate(inflater, R.layout.item_group_color, parent, false)
        return GroupColorControlsHolder(binding)
    }

    @Suppress("DEPRECATION")
    override fun onBindViewHolder(holder: GroupColorControlsHolder, position: Int) {
        ViewCompat.setBackgroundTintList(holder.binding.icColor1, ColorStateList.valueOf(
            listGroupColor[holder.adapterPosition].color1
        ))
        ViewCompat.setBackgroundTintList(holder.binding.icColor2, ColorStateList.valueOf(
            listGroupColor[holder.adapterPosition].color2
        ))
        ViewCompat.setBackgroundTintList(holder.binding.icColor3, ColorStateList.valueOf(
            listGroupColor[holder.adapterPosition].color3
        ))
        if (currentSelect == holder.adapterPosition){
            holder.binding.viewBorder.show()
        } else {
            holder.binding.viewBorder.invisible()
        }
        holder.binding.layoutGroupColor.setOnClickListener {
            ViewHelper.preventTwoClick(it,500)
            if (currentSelect != holder.adapterPosition){
                previousSelect = currentSelect
                currentSelect = holder.adapterPosition
                notifyItemChanged(previousSelect)
                notifyItemChanged(currentSelect)
                clickListener?.onChooseColor(listGroupColor[holder.adapterPosition])
            }
        }
    }

    override fun getItemCount(): Int {
        return listGroupColor.size
    }
    interface ClickListener{
        fun onChooseColor(group: GroupColor)
    }
}
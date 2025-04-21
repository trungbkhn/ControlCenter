package com.tapbi.spark.controlcenter.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.adapter.ChooseControlAdapter.ViewHolderItem
import com.tapbi.spark.controlcenter.databinding.ItemChooseControlBinding
import com.tapbi.spark.controlcenter.feature.controlios14.model.ControlCustomize
import com.tapbi.spark.controlcenter.utils.ControlCustomizeDiffCallback
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper
import timber.log.Timber

//class ChooseControlAdapter : RecyclerView.Adapter<ViewHolderItem>() {
//    private var list: MutableList<ControlCustomize> = mutableListOf()
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderItem {
//        val inflater = LayoutInflater.from(parent.context)
//        val binding: ItemChooseControlBinding =
//            DataBindingUtil.inflate(inflater, R.layout.item_choose_control, parent, false)
//        return ViewHolderItem(binding)
//    }
//    fun setData(list: MutableList<ControlCustomize>){
//        this.list = list
//        notifyDataSetChanged()
//    }
//    override fun getItemCount(): Int {
//        return list.size
//    }
//
//    override fun onBindViewHolder(holder: ViewHolderItem, position: Int) {
//        holder.binData(holder.adapterPosition)
//    }
//
//    inner class ViewHolderItem(var binding :ItemChooseControlBinding) : RecyclerView.ViewHolder(binding.root){
//        fun binData(position: Int){
//            val controlCustomize: ControlCustomize = list[position]
//            binding.name.text = controlCustomize.name
//            Glide.with(itemView.context).load(controlCustomize.icon).into(binding.iconControl)
//            binding.name.isSelected = true
//        }
//    }
//}
class ChooseControlAdapter : ListAdapter<ControlCustomize, ViewHolderItem>(ControlCustomizeDiffCallback()) {
    var iClick: IControlClick? = null
    fun setData(newList: List<ControlCustomize>) {
        submitList(newList.toList())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderItem {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemChooseControlBinding =
            DataBindingUtil.inflate(inflater, R.layout.item_choose_control, parent, false)
        return ViewHolderItem(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderItem, position: Int) {
        holder.bind()
    }

    @Suppress("DEPRECATION")
    inner class ViewHolderItem(var binding: ItemChooseControlBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.name.text = currentList[this.adapterPosition].name
            Glide.with(itemView.context)
                .load(currentList[this.adapterPosition].icon)
                .signature(ObjectKey(currentList[this.adapterPosition].packageName)).into(binding.iconControl)
            binding.name.isSelected = true
            binding.iconControl.setOnClickListener {
                iClick?.onClick(this.adapterPosition,currentList[this.adapterPosition])
            }
        }
    }
    fun removeItem(position: Int) {
        val currentList = currentList.toMutableList()
        if (position in currentList.indices) {
            currentList.removeAt(position)
            submitList(currentList)
        }
    }

    interface IControlClick {
        fun onClick(position: Int, controlCustomize: ControlCustomize)
    }
}

package com.tapbi.spark.controlcenter.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.tapbi.spark.controlcenter.data.model.Language
import com.tapbi.spark.controlcenter.databinding.ItemLanguageBinding
import com.tapbi.spark.controlcenter.utils.gone
import com.tapbi.spark.controlcenter.utils.show
import com.tapbi.spark.controlcenter.R

class LanguageAdapter : RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {
    var listener: LanguageListener? = null
    private var listLanguage: MutableList<Language> = mutableListOf()
    private var indexCheck: Int = -1

    fun setData(listLanguage: MutableList<Language>, indexCheck: Int) {
        this.listLanguage.clear()
        this.listLanguage.addAll(listLanguage)
        this.indexCheck = indexCheck
        notifyDataSetChanged()
    }

    interface LanguageListener {
        fun onClick(position: Int, language: Language)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LanguageViewHolder {
        return LanguageViewHolder(
            ItemLanguageBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(
        holder: LanguageViewHolder,
        position: Int
    ) {
        holder.binding.tvLanguage.text = listLanguage[position].nameLanguage
        holder.binding.imgCheck.show()
        if (holder.adapterPosition == listLanguage.size - 1) {
            holder.binding.viewTab.gone()
        } else {
            holder.binding.viewTab.show()
        }
        holder.binding.root.setOnClickListener {
            if (position != indexCheck) {
                holder.binding.imgCheck.setBackgroundResource(R.drawable.ic_circle_slice)
                notifyItemChanged(indexCheck, indexCheck)
                indexCheck = position
                listener?.onClick(position, listLanguage[position])
            }
        }
        if (position == indexCheck){
            holder.binding.imgCheck.setBackgroundResource(R.drawable.ic_circle_slice)
        } else {
            holder.binding.imgCheck.setBackgroundResource(R.drawable.ic_unselect_language)
        }
        when (position) {
            0 -> {
                holder.binding.cslItemFont.background = ResourcesCompat.getDrawable(
                    holder.binding.root.resources,
                    R.drawable.bg_item_language_top,
                    null
                )
            }

            listLanguage.size - 1 -> {
                holder.binding.cslItemFont.background = ResourcesCompat.getDrawable(
                    holder.binding.root.resources,
                    R.drawable.bg_item_language_bottom,
                    null
                )
            }

            else -> {
                holder.binding.cslItemFont.background = ResourcesCompat.getDrawable(
                    holder.binding.root.resources, R.drawable.bg_item_language_default, null
                )
            }
        }
    }

    override fun getItemCount(): Int {
       return listLanguage.size
    }

    inner class LanguageViewHolder(val binding: ItemLanguageBinding) :
        RecyclerView.ViewHolder(binding.root)
}
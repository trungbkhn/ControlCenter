package com.tapbi.spark.controlcenter.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.databinding.ItemFontBinding
import com.tapbi.spark.controlcenter.utils.Utils
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper
import com.tapbi.spark.controlcenter.utils.hide
import com.tapbi.spark.controlcenter.utils.invisible
import com.tapbi.spark.controlcenter.utils.show
import timber.log.Timber


class FontControlsAdapter : RecyclerView.Adapter<FontControlsAdapter.FontViewHolder>() {
    private var clickListener: ClickListener? = null
    private var previousSelect = 0
    private var currentSelect = 0
    private val listFont = mutableListOf<String>()
    fun setClickListener(clickListener: ClickListener){
        this.clickListener = clickListener
    }
    fun setListFont(listFont: MutableList<String>){
        this.listFont.clear()
        this.listFont.addAll(listFont)
        notifyDataSetChanged()
    }

    fun setSelect(currentSelect: Int, font: String) {
        this.previousSelect = this.currentSelect
        if (currentSelect == -1) {
            this.listFont.add(0, font)
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

    class FontViewHolder(var binding: ItemFontBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FontViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemFontBinding = DataBindingUtil.inflate(inflater, R.layout.item_font, parent, false)
        return FontViewHolder(binding)
    }

    @Suppress("DEPRECATION")
    override fun onBindViewHolder(holder: FontViewHolder, position: Int) {
        Utils.setFontForTextView(holder.binding.tvFont, listFont[holder.adapterPosition], App.ins)
        if (currentSelect == holder.adapterPosition){
            holder.binding.viewBorder.show()
        } else {
            holder.binding.viewBorder.invisible()
        }
        holder.binding.tvFont.setOnClickListener {
            ViewHelper.preventTwoClick(it,500)
            if (currentSelect != holder.adapterPosition){
               setSelect(holder.adapterPosition, listFont[holder.adapterPosition])
                clickListener?.onChooseFont(listFont[holder.adapterPosition])
            }
        }
    }

    override fun getItemCount(): Int {
        return listFont.size
    }
    interface ClickListener{
        fun onChooseFont(fontName: String)
    }
}
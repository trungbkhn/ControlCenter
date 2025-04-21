package com.tapbi.spark.controlcenter.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.common.Constant.LAST_TIME_EDIT_THEME
import com.tapbi.spark.controlcenter.data.local.SharedPreferenceHelper
import com.tapbi.spark.controlcenter.data.model.ThemeControl
import com.tapbi.spark.controlcenter.databinding.ItemAppearanceHomeBinding
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper


class MyCustomizationAdapter : RecyclerView.Adapter<MyCustomizationAdapter.ViewHolder>() {


    private var listThemeCustom = mutableListOf<ThemeControl>()

    private var listener: IListener? = null

    private var idSelectThemes = 0L


    fun setIdThemes(id: Long) {
        idSelectThemes = id
    }


    override fun getItemCount(): Int {
        return listThemeCustom.size
    }


    fun setData(arrayFolderAll: MutableList<ThemeControl>, listener: IListener) {
        this.listThemeCustom.clear()
        this.listThemeCustom.addAll(arrayFolderAll)
        this.listener = listener
        notifyDataSetChanged()
    }

    class ViewHolder(var binding: ItemAppearanceHomeBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAppearanceHomeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

       val theme = listThemeCustom[holder.absoluteAdapterPosition]
        val path = "${App.ins.filesDir}/${Constant.FOLDER_THEMES_ASSETS}/${theme.idCategory}/${theme.id}/${theme.preview}"
        val combinedKey: String = path + "_" + SharedPreferenceHelper.getLong(LAST_TIME_EDIT_THEME, 0)
        val signature = ObjectKey(combinedKey)
        Glide.with(holder.itemView.context)
            .load(path)
            .signature(signature)
            .placeholder(R.drawable.ic_loading_theme)
            .into(holder.binding.imageView)
        holder.itemView.rootView.setOnClickListener {
            ViewHelper.preventTwoClick(it,800)
            listener?.onClick(
                theme,
                holder.absoluteAdapterPosition
            )
        }
        holder.binding.tick.visibility =
            if (idSelectThemes == theme.id) View.VISIBLE else View.GONE
    }

    interface IListener {
        fun onClick(themeControl: ThemeControl, position: Int)
    }
}
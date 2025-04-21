package com.tapbi.spark.controlcenter.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant.MAX_SELECT_FAVORITE
import com.tapbi.spark.controlcenter.data.model.ThemeControlFavorite
import com.tapbi.spark.controlcenter.databinding.ItemFavoriteThemeBinding
import com.tapbi.spark.controlcenter.utils.MethodUtils
import com.tapbi.spark.controlcenter.utils.inv
import com.tapbi.spark.controlcenter.utils.show

class FavoriteAdapter : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {
    val list: MutableList<ThemeControlFavorite> = mutableListOf()

    var clickListener: ClickListener? = null

    private val margin4 = MethodUtils.dpToPx(4f)

    @SuppressLint("NotifyDataSetChanged")
    fun setListFavorites(list: MutableList<ThemeControlFavorite>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
        clickListener?.setStateBtnNext(getSelectCount() >= 1)
    }

    inner class FavoriteViewHolder(var binding: ItemFavoriteThemeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setStateSelect(isSelect: Boolean) {
            val context = binding.root.context
            val paramsImage = binding.imageFavorite.layoutParams as ConstraintLayout.LayoutParams

            binding.backgroundSelect.apply { if (isSelect) show() else inv() }
            val margin = if (isSelect) margin4 else 0
            paramsImage.setMargins(margin, margin, margin, margin)

            val textColor =
                ContextCompat.getColor(context, if (isSelect) R.color.color_undo else R.color.black)
            val typefaceRes =
                if (isSelect) R.font.sf_pro_text_semi_bold else R.font.sf_pro_text_regular
            val typeface = ResourcesCompat.getFont(context, typefaceRes)

            binding.tvName.apply {
                setTextColor(textColor)
                this.typeface = typeface
            }

            binding.imageFavorite.layoutParams = paramsImage
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding =
            ItemFavoriteThemeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val item = list.getOrNull(position)
        item?.let {
            Glide.with(holder.itemView.context)
                .load("file:///android_asset/themes/" + item.idCategory + "/" + item.id + "/" + item.preview)
                .placeholder(R.drawable.ic_loading_theme)
                .into(holder.binding.imageFavorite)
            holder.setStateSelect(item.isSelect)
            holder.binding.tvName.text = item.name
            holder.itemView.setOnClickListener { view ->
                if (item.isSelect || getSelectCount() < MAX_SELECT_FAVORITE) {
                    item.isSelect = !item.isSelect
                    clickListener?.setStateBtnNext(getSelectCount() >= 1)
                    clickListener?.onClickItem(item, position)
                } else {
                    clickListener?.onFullSelect()
                }
            }
        }
    }

    private fun getSelectCount(): Int {
        return list.filter { it.isSelect }.size
    }

    fun getSelectedThemes(): List<ThemeControlFavorite> {
        return list.filter { it.isSelect }
    }

    interface ClickListener {
        fun onClickItem(item: ThemeControlFavorite, position: Int)
        fun setStateBtnNext(isEnable: Boolean)
        fun onFullSelect()
    }
}
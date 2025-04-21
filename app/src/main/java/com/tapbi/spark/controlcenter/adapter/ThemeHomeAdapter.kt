package com.tapbi.spark.controlcenter.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.data.model.ThemeControl
import com.tapbi.spark.controlcenter.databinding.ItemAppearanceHomeBinding
import com.tapbi.spark.controlcenter.databinding.ItemTitleApdapterBinding
import com.tapbi.spark.controlcenter.ui.main.MainActivity.Companion.isDispatchTouchEvent
import com.tapbi.spark.controlcenter.utils.MethodUtils

class ThemeHomeAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    var clickListener: ClickListener? = null

    private val mDiffer: AsyncListDiffer<ThemeControl>

    private val padStart = MethodUtils.dpToPx(10f)


    val ITEM_TITLE_APPEARANCE = 0
    private val ITEM_APPEARANCE = 1

    private var idThemes = -1


    fun setIdThemes(id: Int) {
        val oldPosition: Int = this.idThemes
        this.idThemes = id
        if (oldPosition >= 0) {
            notifyItemChanged(
                oldPosition,
                mDiffer.currentList[oldPosition]
            )
        }
        if (idThemes >= 0) {
            notifyItemChanged(idThemes, mDiffer.currentList[idThemes])
        } else {
            notifyDataSetChanged()
        }

    }


    fun setData(arrayFolderAll: MutableList<ThemeControl>) {
        val list = mutableListOf<ThemeControl>()
        list.add(ThemeControl(0, 0, ""))
        list.addAll(arrayFolderAll)
        mDiffer.submitList(list)
    }

    class AppearanceHomeHolder(var binding: ItemAppearanceHomeBinding) :
        RecyclerView.ViewHolder(binding.root)

    class ItemTitleAppearanceHolder(var binding: ItemTitleApdapterBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == ITEM_TITLE_APPEARANCE) {
            return ItemTitleAppearanceHolder(
                ItemTitleApdapterBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
        return AppearanceHomeHolder(
            ItemAppearanceHomeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            ITEM_TITLE_APPEARANCE
        } else {
            ITEM_APPEARANCE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AppearanceHomeHolder) {
            if (position % 2 != 0) {
                holder.binding.root.setPadding(padStart, 0, 0, 0)
            } else {
                holder.binding.root.setPadding(0, 0, padStart, 0)
            }
            val theme = mDiffer.currentList[position]
            Glide.with(holder.itemView.context)
                .load("file:///android_asset/themes/" + theme.idCategory + "/" + theme.id + "/" + theme.preview)
                .placeholder(R.drawable.ic_loading_theme)
                .error(R.drawable.thumb_control_ios_new)
                .into(holder.binding.imageView)
            holder.binding.tick.visibility =
                if (position == idThemes) View.VISIBLE else View.GONE
            holder.binding.clAppearance.setOnClickListener {
                isDispatchTouchEvent()
                clickListener?.onClick(theme)

            }
        }
    }


    private val differCallback: DiffUtil.ItemCallback<ThemeControl> =
        object : DiffUtil.ItemCallback<ThemeControl>() {
            override fun areItemsTheSame(
                oldItem: ThemeControl,
                newItem: ThemeControl
            ): Boolean {
                return oldItem.id == newItem.id
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: ThemeControl,
                newItem: ThemeControl
            ): Boolean {
                return oldItem == newItem
            }

        }

    init {
        mDiffer = AsyncListDiffer(this, differCallback)
    }

    override fun getItemCount(): Int {
        return mDiffer.currentList.size
    }

    interface ClickListener {
        fun onClick(themeControl: ThemeControl)
    }
}
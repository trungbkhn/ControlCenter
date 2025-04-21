package com.tapbi.spark.controlcenter.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tapbi.spark.controlcenter.common.models.Wallpaper
import com.tapbi.spark.controlcenter.databinding.ItemWallpaperBinding
import com.tapbi.spark.controlcenter.utils.DensityUtils

class WallpaperAdapter(val context: Context, val listener: (Wallpaper, Int) -> Unit) :
    RecyclerView.Adapter<WallpaperAdapter.Holder>() {
    var list: ArrayList<Wallpaper> = ArrayList()
    var idSelected = -1

    fun setData(list: ArrayList<Wallpaper>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun setIdSelect(idWallpaper: Int) {
        val oldPosition = this.idSelected
        this.idSelected = idWallpaper
        if (oldPosition >= 0) notifyItemChanged(oldPosition, list[oldPosition])
        if (idSelected >= 0) {
            notifyItemChanged(idSelected, list[idSelected])
        }
    }


    inner class Holder(var binding: ItemWallpaperBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(wallpaper: Wallpaper, pos: Int) {

            binding.viewLayerTop.visibility =
                if (pos == idSelected) View.VISIBLE else View.GONE
            binding.imgSelected.visibility =
                if (pos == idSelected) View.VISIBLE else View.GONE

            Glide.with(context).load(wallpaper.pathThumb).into(binding.imgThumb)

            val lp: RecyclerView.LayoutParams =
                binding.root.layoutParams as RecyclerView.LayoutParams
            if (pos == 0 || pos == 1 || pos == 2) {
                lp.topMargin = DensityUtils.pxFromDp(context, 24F).toInt()
            } else {
                lp.topMargin = DensityUtils.pxFromDp(context, 8F).toInt()
            }
            binding.root.requestLayout()

            itemView.setOnClickListener {
                setIdSelect(pos)
                listener(wallpaper, pos)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding: ItemWallpaperBinding =
            ItemWallpaperBinding.inflate(LayoutInflater.from(context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, pos: Int) {
        val position = holder.bindingAdapterPosition
        holder.bind(list[position], position)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
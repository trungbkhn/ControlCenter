package com.tapbi.spark.controlcenter.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.tapbi.spark.controlcenter.databinding.ItemPreviewWallpaperBinding

class PreviewWallpaperAdapter(private val listPreview: List<Int>) :
    RecyclerView.Adapter<PreviewWallpaperAdapter.ViewHolder>() {

    companion object {
        const val POS_NOTIFICATION = 0
        const val POS_CONTROL_CENTER = 1
    }

    inner class ViewHolder(val binding: ItemPreviewWallpaperBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            Glide.with(binding.imPreview).load(listPreview[position]).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).into(binding.imPreview)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): PreviewWallpaperAdapter.ViewHolder {
        return ViewHolder(
            ItemPreviewWallpaperBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return listPreview.size
    }

    override fun onBindViewHolder(holder: PreviewWallpaperAdapter.ViewHolder, position: Int) {
        holder.bind(position)
    }
}
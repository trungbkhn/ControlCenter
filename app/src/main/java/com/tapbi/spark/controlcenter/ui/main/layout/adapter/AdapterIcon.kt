package com.tapbi.spark.controlcenter.ui.main.layout.adapter

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.feature.controlcenter.model.InfoIcon
import com.tapbi.spark.controlcenter.utils.DensityUtils
import com.tapbi.spark.controlcenter.utils.StringIcon

class AdapterIcon(clickIcon: ClickIcon, var iconSelect: String) :
    RecyclerView.Adapter<AdapterIcon.Holder?>() {
    private val densityUtils = DensityUtils()
    private var infoIcons: List<InfoIcon>
    private var valueOld = 0
    private var value = 1
    private val clickIcon: ClickIcon
    var stringIcon = StringIcon()
    fun setNewData(infoIcons: List<InfoIcon>, valueSelect: Int) {
        this.infoIcons = infoIcons
        value = valueSelect
        if (valueSelect >= 0) {
            valueOld = value
        }
        notifyDataSetChanged()
    }

    init {
        infoIcons = stringIcon.addString()
        for (i in infoIcons.indices) {
            if (infoIcons[i].nameRes == iconSelect) {
                value = i
                valueOld = value
            }
        }
        this.clickIcon = clickIcon
    }

    fun setValue(value: Int) {
        this.value = value
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_icon, parent, false)
        )
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: Holder, pos: Int) {
        val position = holder.layoutPosition
        val infoIcon = infoIcons[position]
        try {
            holder.imgIcon.setImageResource(infoIcon.icon)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        holder.tvNameIcon.text = infoIcon.name
        val item = ConstraintLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            DensityUtils.pxFromDp(holder.itemView.context, 50f).toInt()
        )
        val layoutLine = holder.lineBottom.layoutParams as ConstraintLayout.LayoutParams
        if (position == infoIcons.size - 1) {
            item.bottomMargin = DensityUtils.pxFromDp(holder.itemView.context, 50f).toInt()
            layoutLine.leftMargin = DensityUtils.pxFromDp(holder.itemView.context, 0f).toInt()
        } else {
            layoutLine.leftMargin = DensityUtils.pxFromDp(holder.itemView.context, 78f).toInt()
            item.bottomMargin = 0
        }
        holder.lineBottom.layoutParams = layoutLine
        holder.layoutIcon.layoutParams = item
        if (iconSelect == infoIcon.nameRes) {
            holder.icSelect.visibility = View.VISIBLE
        } else {
            holder.icSelect.visibility = View.INVISIBLE
        }
        holder.itemView.setOnClickListener {
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                if (infoIcon.nameRes == iconSelect) return@Runnable
                iconSelect = infoIcon.nameRes
                clickIcon.clickIcon(infoIcon.nameRes)
                value = position
                notifyItemChanged(value)
                notifyItemChanged(valueOld)
                valueOld = value
            }, 100)
        }
    }

    override fun getItemCount(): Int {
        return infoIcons.size
    }

    fun interface ClickIcon {
        fun clickIcon(icon: String?)
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var layoutIcon: ConstraintLayout = itemView.findViewById(R.id.layoutIcon)
        var imgIcon: ImageView = itemView.findViewById(R.id.imgIcon)
        var icSelect: ImageView = itemView.findViewById(R.id.icSelect)
        var tvNameIcon: TextView = itemView.findViewById(R.id.tvNameIcon)
        var lineBottom: View = itemView.findViewById(R.id.lineBottom)
    }
}

package com.tapbi.spark.controlcenter.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant.EVENT_CHANGE_STATE_SEEK_BAR
import com.tapbi.spark.controlcenter.databinding.ItemIconShapeBinding
import com.tapbi.spark.controlcenter.eventbus.EventCustomControls
import com.tapbi.spark.controlcenter.utils.Utils
import com.tapbi.spark.controlcenter.utils.Utils.loadImageFromAssetsDrawable
import com.tapbi.spark.controlcenter.utils.hide
import com.tapbi.spark.controlcenter.utils.show
import org.greenrobot.eventbus.EventBus
import timber.log.Timber


class IconShapeAdapter : RecyclerView.Adapter<IconShapeAdapter.IconShapeViewHolder>() {
    private var clickListener: ClickListener? = null
    private var previousSelect = 0
    private var currentSelect = 0
    private val listIconShape = mutableListOf<String>()

    fun setClickListener(clickListener: ClickListener) {
        this.clickListener = clickListener
    }

    fun setData(list: MutableList<String>) {
        listIconShape.clear()
        listIconShape.addAll(list)
        notifyDataSetChanged()
    }
    fun setSelect(currentSelect: Int) {
        this.previousSelect = this.currentSelect
        this.currentSelect = currentSelect
        if (previousSelect != RecyclerView.NO_POSITION) {
            notifyItemChanged(previousSelect)
        }
        if (currentSelect != RecyclerView.NO_POSITION) {
            notifyItemChanged(currentSelect)

        }
    }


    class IconShapeViewHolder(var binding: ItemIconShapeBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconShapeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemIconShapeBinding =
            DataBindingUtil.inflate(inflater, R.layout.item_icon_shape, parent, false)
        return IconShapeViewHolder(binding)
    }

    @Suppress("DEPRECATION")
    override fun onBindViewHolder(holder: IconShapeViewHolder, position: Int) {
        holder.binding.icShape.setImageDrawable(loadImageFromAssetsDrawable(listIconShape[position]))
        if (currentSelect == holder.adapterPosition) {
            holder.binding.viewBorder.show()
        } else {
            holder.binding.viewBorder.hide()
        }
        holder.binding.icShape.setOnClickListener {
            if (currentSelect != holder.adapterPosition) {
                previousSelect = currentSelect
                currentSelect = holder.adapterPosition
                if (previousSelect == 0 && currentSelect != 0) {
                    EventBus.getDefault()
                        .post(EventCustomControls(EVENT_CHANGE_STATE_SEEK_BAR, false))
                } else if (previousSelect != 0 && currentSelect == 0) {
                    EventBus.getDefault()
                        .post(EventCustomControls(EVENT_CHANGE_STATE_SEEK_BAR, true))
                }
                notifyItemChanged(previousSelect)
                notifyItemChanged(currentSelect)
                clickListener?.onChooseShape(listIconShape[currentSelect])
            }
        }
    }

    override fun getItemCount(): Int {
        return listIconShape.size
    }

    interface ClickListener {
        fun onChooseShape(iconName: String)
    }
}
package com.tapbi.spark.controlcenter.ui.main.customcontrol.preview.adapter

import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.data.model.ItemControl
import com.tapbi.spark.controlcenter.databinding.ItemControlPreviewMiShadeBinding
import com.tapbi.spark.controlcenter.feature.controlios14.model.InfoSystem
import com.tapbi.spark.controlcenter.utils.MethodUtils

class PreviewControlMiShadeAdapter :
    RecyclerView.Adapter<PreviewControlMiShadeAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemControlPreviewMiShadeBinding) :
        RecyclerView.ViewHolder(binding.root)

    private var listControl = listOf<InfoSystem>()

    private var itemControl: ItemControl? = null
    private var width = 0
    val PAYLOAD_UPDATE_BACKGROUND = "PAYLOAD_UPDATE_BACKGROUND"
    val PAYLOAD_UPDATE_FONT = "PAYLOAD_UPDATE_FONT"
    val PAYLOAD_UPDATE_COLOR = "PAYLOAD_UPDATE_COLOR"
    val PAYLOAD_UPDATE_CONNER = "PAYLOAD_UPDATE_CONNER"

    private var font = ""

    private var typeface: Typeface? = null

    fun setDataMiShade(list: List<InfoSystem>, itemControl: ItemControl, width: Int) {
        this.listControl = list.take(12)
        this.itemControl = itemControl
        this.width = width
        notifyDataSetChanged() // Reset toàn bộ data khi thay đổi
    }

    fun setDataControlCenter(
        list: List<InfoSystem>,
        itemControl: ItemControl,
        width: Int
    ) {
        this.listControl = list.drop(4).take(12) // Lấy từ index 4 đến 16
        this.itemControl = itemControl
        this.width = width
        notifyDataSetChanged()
    }


    fun setItemControlCenter(itemControl: ItemControl, payload: Any) {
        this.itemControl = itemControl
        listControl.indices.forEach {
            notifyItemChanged(
                it,
                payload
            )
        } // Cập nhật từng item với payload
    }

    fun setItemMiShade(itemControl: ItemControl, payload: Any) {
        this.itemControl = itemControl
        listControl.indices.forEach {
            notifyItemChanged(
                it,
                payload
            )
        } // Cập nhật từng item với payload
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemControlPreviewMiShadeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        bindItem(holder, position)
    }

    private fun bindItem(holder: ViewHolder, position: Int) {
        val item = listControl[position]
        val w = if (itemControl?.miShade?.backgroundColorSelectControl != null) 0.12 else 0.151
        with(holder.binding.imControl) {
            setIcon(item.icon)
            layoutParams.width = (w * this@PreviewControlMiShadeAdapter.width).toInt()
        }

        holder.binding.tvControl.text =
            MethodUtils.getNameActionShowTextView(holder.itemView.context, item.name)
        itemControl?.let {
            applyControlSettings(holder, position, it)
        }

    }


    private fun applyControlSettings(
        holder: ViewHolder,
        position: Int,
        item: ItemControl
    ) {
        with(holder.binding) {
            font = item.font
            try {
                typeface = Typeface.createFromAsset(
                    App.mContext.assets,
                    Constant.FOLDER_FONT_CONTROL_ASSETS + font
                )
            } catch (e: Exception) {

            }
            if (typeface != null) {
                tvControl.typeface = typeface
            }

            tvControl.setTextSize(TypedValue.COMPLEX_UNIT_SP, 4.5f)
            if (!itemControl?.controlCenter?.iconControl.isNullOrEmpty()) {
                item.controlCenter?.let {

                    tvControl.setTextColor(Color.parseColor(it.textColorDefaultControl))

                    val (iconColor, backgroundColor) = if (position in listOf(0, 1)) {
                        Color.parseColor(it.iconColorSelectControl) to Color.parseColor(it.backgroundColorSelectControl2)
                    } else {
                        Color.parseColor(it.iconColorDefaultControl) to Color.parseColor(it.backgroundColorDefaultControl)
                    }
                    imControl.apply {
                        setIconColor(iconColor)
                        setBackgroundC(backgroundColor)
                        setBackground(it.iconControl)
                        setRatioRadius(it.cornerBackgroundControl)
                    }

                }
            } else {
                item.miShade?.let {
                    tvControl.setTextColor(Color.parseColor(it.colorTextControl))
                    val (iconColor, backgroundColor) = if (position in listOf(0, 1, 4, 8, 9)) {
                        Color.parseColor(it.iconColorSelectControl) to Color.parseColor(it.backgroundColorSelectControl)
                    } else {
                        Color.parseColor(it.iconColorDefaultControl) to Color.parseColor(it.backgroundColorDefaultControl)
                    }
                    imControl.apply {
                        setIconColor(iconColor)
                        setBackgroundC(backgroundColor)
                        setBackground(it.iconControl)
                        setRatioRadius(it.cornerBackgroundControl)
                    }

                }
            }


        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            bindItem(holder, position)
        } else {
            for (payload in payloads) {
                when (payload) {
                    PAYLOAD_UPDATE_BACKGROUND -> updateBackground(holder, position)
                    PAYLOAD_UPDATE_FONT -> updateFont(holder)
                    PAYLOAD_UPDATE_COLOR -> updateColor(holder, position)
                    else -> bindItem(holder, position)
                }
            }
        }
    }

    private fun updateBackground(holder: ViewHolder, position: Int) {
        if (itemControl?.controlCenter?.iconControl.isNullOrEmpty()) {
            itemControl?.miShade?.let {
                holder.binding.imControl.setBackground(it.iconControl)
                holder.binding.imControl.setRatioRadius(it.cornerBackgroundControl)
            }
        } else {
            itemControl?.controlCenter?.let {
                holder.binding.imControl.setBackground(it.iconControl)
                holder.binding.imControl.setRatioRadius(it.cornerBackgroundControl)
            }
        }
        updateColor(holder, position)

    }

    private fun updateColor(holder: ViewHolder, position: Int) {
        if (itemControl?.controlCenter?.iconControl.isNullOrEmpty()) {
            itemControl?.miShade?.let {
                if (position in listOf(0, 1, 4, 8, 9)) {
                    holder.binding.imControl.setBackgroundC(Color.parseColor(it.backgroundColorSelectControl))
                }
            }
        } else {
            itemControl?.controlCenter?.let {
                if (position in listOf(0, 1)) {
                    holder.binding.imControl.setBackgroundC(Color.parseColor(it.backgroundColorSelectControl2))
                }

            }
        }
    }

    private fun updateFont(holder: ViewHolder) {
        itemControl?.let {
            if (font.isEmpty() || font != it.font) {
                font = it.font
                if (font.isNotEmpty()) {
                    try {
                        typeface = Typeface.createFromAsset(
                            App.mContext.assets,
                            Constant.FOLDER_FONT_CONTROL_ASSETS + font
                        )
                    } catch (e: Exception) {
                    }
                }
            }
            if (typeface != null) {
                holder.binding.tvControl.typeface = typeface
            }


        }

    }

    override fun getItemCount(): Int = listControl.size
}

package com.tapbi.spark.controlcenter.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant.EVENT_CHANGE_COLOR
import com.tapbi.spark.controlcenter.common.Constant.EVENT_CHANGE_GROUP_COLOR
import com.tapbi.spark.controlcenter.common.Constant.EVENT_CHANGE_ICON_SHADE
import com.tapbi.spark.controlcenter.common.Constant.VIEW_TYPE_1
import com.tapbi.spark.controlcenter.common.Constant.VIEW_TYPE_2
import com.tapbi.spark.controlcenter.common.Constant.VIEW_TYPE_3
import com.tapbi.spark.controlcenter.data.model.GroupColor
import com.tapbi.spark.controlcenter.databinding.ItemControlTypeColorBinding
import com.tapbi.spark.controlcenter.databinding.ItemControlTypeCornersBinding
import com.tapbi.spark.controlcenter.databinding.ItemControlTypeIconShapeBinding
import com.tapbi.spark.controlcenter.databinding.ItemControlTypePositionBinding
import com.tapbi.spark.controlcenter.eventbus.EventCustomControls
import com.tapbi.spark.controlcenter.ui.custom.CustomSeekBar
import org.greenrobot.eventbus.EventBus

class ControlsAdapter(
    var iconShapeAdapter: IconShapeAdapter,
    var colorControlsAdapter: ColorControlsAdapter? = null,
    var groupColorAdapter: GroupColorControlsAdapter? = null,
    var progressSeekBar: Int = 0
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var isEnableSeekBar = true


    private var listener: ClickListener? = null

    fun setListener(listener: ClickListener) {
        this.listener = listener
    }


    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> VIEW_TYPE_1
            1 -> VIEW_TYPE_2
            else -> VIEW_TYPE_3
            //else -> VIEW_TYPE_4
        }
    }

    fun setStateSeekBar(isEnable: Boolean) {
        try {
            isEnableSeekBar = isEnable
            notifyItemChanged(0)
        } catch (e: Exception) {
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_1 -> {
                val binding1: ItemControlTypeCornersBinding =
                    DataBindingUtil.inflate(
                        inflater,
                        R.layout.item_control_type_corners,
                        parent,
                        false
                    )
                ControlType1ViewHolder(binding1)
            }

            VIEW_TYPE_2 -> {
                val binding2: ItemControlTypeIconShapeBinding =
                    DataBindingUtil.inflate(
                        inflater,
                        R.layout.item_control_type_icon_shape,
                        parent,
                        false
                    )
                ControlType2ViewHolder(binding2)
            }

            else -> {
                val binding3: ItemControlTypeColorBinding =
                    DataBindingUtil.inflate(
                        inflater,
                        R.layout.item_control_type_color,
                        parent,
                        false
                    )
                ControlType3ViewHolder(binding3)
            }

//            else -> {
//                val binding4: ItemControlTypePositionBinding=
//                    DataBindingUtil.inflate(inflater, R.layout.item_control_type_position, parent, false)
//                ControlType4ViewHolder(binding4)
//            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(holder.adapterPosition)) {
            VIEW_TYPE_1 -> {
                (holder as ControlType1ViewHolder).binData()
            }

            VIEW_TYPE_2 -> {
                (holder as ControlType2ViewHolder).binData()
            }

            else -> {
                (holder as ControlType3ViewHolder).binData()
            }

//            else -> {
//                (holder as ControlType4ViewHolder).binData()
//            }
        }
    }


    override fun getItemCount(): Int {
        return 3
    }

    inner class ControlType1ViewHolder(private var binding1: ItemControlTypeCornersBinding) :
        RecyclerView.ViewHolder(binding1.root) {
        fun binData() {
            binding1.seekBar.setState(isEnableSeekBar)
            if (isEnableSeekBar) {
                binding1.seekBar.setProgress(progressSeekBar)
            } else {
                progressSeekBar = 0
            }
            binding1.seekBar.listener = object : CustomSeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: CustomSeekBar,
                    progress: Int,
                    fromUser: Boolean,
                ) {

                    if (fromUser) {
                        progressSeekBar = progress
                        listener?.onChangeConner(progress)
                    }

                }

                override fun onStartTrackingTouch(seekBar: CustomSeekBar) {
                    listener?.onStartTrackingTouch()
                }

                override fun onStopTrackingTouch(seekBar: CustomSeekBar) {
                    listener?.onStopTrackingTouch()
                }
            }
        }
    }

    inner class ControlType2ViewHolder(private var binding2: ItemControlTypeIconShapeBinding) :
        RecyclerView.ViewHolder(binding2.root) {

        fun binData() {
            binding2.rcvShape.itemAnimator = null
            binding2.rcvShape.adapter = iconShapeAdapter
            iconShapeAdapter.setClickListener(object : IconShapeAdapter.ClickListener {
                override fun onChooseShape(iconName: String) {
                    EventBus.getDefault().post(EventCustomControls(EVENT_CHANGE_ICON_SHADE, iconName))
                }

            })

        }
    }

    inner class ControlType3ViewHolder(private var binding3: ItemControlTypeColorBinding) :
        RecyclerView.ViewHolder(binding3.root) {
        fun binData() {
            // bổ sung sau
            colorControlsAdapter?.let {
                binding3.rcvColorMiControls.itemAnimator = null
                binding3.rcvColorMiControls.adapter = it
                it.setClickListener(object : ColorControlsAdapter.ClickListener {
                    override fun onChooseColor(resColor: Int) {
                        EventBus.getDefault()
                            .post(EventCustomControls(EVENT_CHANGE_COLOR, resColor))
                    }
                })
            }
            groupColorAdapter?.let {
                binding3.rcvColorMiControls.itemAnimator = null
                binding3.rcvColorMiControls.adapter = it
                it.setClickListener(object : GroupColorControlsAdapter.ClickListener {
                    override fun onChooseColor(group: GroupColor) {
                        EventBus.getDefault()
                            .post(EventCustomControls(EVENT_CHANGE_GROUP_COLOR, group))
                    }
                })
            }
        }
    }

    inner class ControlType4ViewHolder(private var binding4: ItemControlTypePositionBinding) :
        RecyclerView.ViewHolder(binding4.root) {
        fun binData() {
            binding4.icTickTop.setOnClickListener {
                binding4.icTickTop.setImageResource(R.drawable.ic_select)
                binding4.icTickBottom.setImageResource(R.drawable.ic_unslelect)
            }
            binding4.icTickBottom.setOnClickListener {
                binding4.icTickTop.setImageResource(R.drawable.ic_unslelect)
                binding4.icTickBottom.setImageResource(R.drawable.ic_select)
            }
        }
    }

    interface ClickListener {
        fun onChangeConner(progress: Int)
        fun onStartTrackingTouch()
        fun onStopTrackingTouch()
    }
}
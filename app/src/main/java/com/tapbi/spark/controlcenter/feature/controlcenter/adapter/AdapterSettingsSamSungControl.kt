package com.tapbi.spark.controlcenter.feature.controlcenter.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.common.models.MessageEvent
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CloseMiControlView
import com.tapbi.spark.controlcenter.feature.controlcenter.view.control.samsung.view.ItemRecyclerViewSS
import com.tapbi.spark.controlcenter.feature.controlios14.model.InfoSystem
import com.tapbi.spark.controlcenter.utils.MethodUtils
import org.greenrobot.eventbus.EventBus

class AdapterSettingsSamSungControl :RecyclerView.Adapter<AdapterSettingsSamSungControl.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_setting_control_samsung, parent, false)
        )
    }

    private var infoSystems: List<InfoSystem> = mutableListOf()

    private val holderViewList = ArrayList<ItemRecyclerViewSS>()
    private var closeMiControlView: CloseMiControlView? = null

    fun setListener(closeMiControlView: CloseMiControlView) {
        this.closeMiControlView = closeMiControlView
    }

    fun setData(infoSystems: MutableList<InfoSystem>) {
        this.infoSystems = infoSystems
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val itemExpandRecyclerview =
            holder.itemView.findViewById<ItemRecyclerViewSS>(R.id.parentItemExpand)
        if (itemExpandRecyclerview.first) {
            holderViewList.add(itemExpandRecyclerview)
            itemExpandRecyclerview.first = false

        }
        itemExpandRecyclerview.data(infoSystems[position], position, closeMiControlView)
    }


    fun clearViewList() {
        holderViewList.clear()
    }


    fun updateActionView(action: String, b: Boolean) {
        for (i in holderViewList.indices) {
            val view: ItemRecyclerViewSS = holderViewList[i]
            if (action == MethodUtils.getAction(view.context, view.binding.tvNameAction.text.toString())) {
                view.setStageAction(action, b)
                notifyItemChanged(i, holderViewList[i])
                break
            }
        }
    }


    override fun getItemCount(): Int {
        return infoSystems.size
    }


    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
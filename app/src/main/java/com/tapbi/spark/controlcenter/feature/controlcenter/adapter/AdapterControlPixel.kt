package com.tapbi.spark.controlcenter.feature.controlcenter.adapter

import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CloseMiControlView
import com.tapbi.spark.controlcenter.feature.controlcenter.view.control.micenter.view.ItemExpandRecyclerview
import com.tapbi.spark.controlcenter.feature.controlios14.model.InfoSystem
import com.tapbi.spark.controlcenter.utils.DensityUtils
import com.tapbi.spark.controlcenter.utils.MethodUtils

class AdapterControlPixel() :
    RecyclerView.Adapter<AdapterControlPixel.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_expand_setting_control_mi, parent, false)
        )
    }

    private var infoSystems: List<InfoSystem> = mutableListOf()

    private val holderViewList = ArrayList<ItemExpandRecyclerview>()
    private var closeMiControlView: CloseMiControlView? = null
    private var orientation = 1
    fun setListener(closeMiControlView: CloseMiControlView) {
        this.closeMiControlView = closeMiControlView
    }

    fun setData(infoSystems: MutableList<InfoSystem>, orientation: Int = 1) {
        this.infoSystems = infoSystems
        this.orientation = orientation

    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val valueMarin = DensityUtils.pxFromDp(holder.itemView.context, 8f).toInt()
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            val layoutParams = ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (0.094 * App.widthHeightScreenCurrent.h).toInt()
            )
            layoutParams.setMargins(
                if (position % 2 == 0) valueMarin * 3 else valueMarin,
                if (position == 0 || position == 1) 0 else valueMarin,
                if (position % 2 == 0) valueMarin else valueMarin * 3,
                valueMarin
            )
            holder.itemView.layoutParams = layoutParams
        } else {
            val layoutParams = ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (0.203 * App.widthHeightScreenCurrent.h).toInt()
            )
            layoutParams.setMargins(
                if (position % 2 == 0) valueMarin else valueMarin,
                if (position == 0 || position == 1) 0 else valueMarin,
                if (position % 2 == 0) valueMarin else valueMarin,
                valueMarin
            )
            holder.itemView.layoutParams = layoutParams
        }
        val itemExpandRecyclerview =
            holder.itemView.findViewById<ItemExpandRecyclerview>(R.id.parentItemExpand)

        if (itemExpandRecyclerview.isFirst) {
            holderViewList.add(itemExpandRecyclerview)
            itemExpandRecyclerview.isFirst = false
        }
        itemExpandRecyclerview.data(infoSystems[position], position, closeMiControlView)

    }

    fun clearViewList() {
        holderViewList.clear()
    }


    fun updateActionView(action: String, b: Boolean) {
        for (i in holderViewList.indices) {
            val view: ItemExpandRecyclerview = holderViewList[i]
            if (action == MethodUtils.getAction(view.context, view.nameAction.text.toString())) {
                view.setStageAction(action, b, i)
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

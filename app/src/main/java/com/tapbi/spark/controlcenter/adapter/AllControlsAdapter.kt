package com.tapbi.spark.controlcenter.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tapbi.spark.controlcenter.App.Companion.all
import com.tapbi.spark.controlcenter.App.Companion.included
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.databinding.ItemMoreControlCustomBinding
import com.tapbi.spark.controlcenter.feature.controlios14.model.ControlCustomize
import com.tapbi.spark.controlcenter.interfaces.ItemTouchHelperAdapter
import com.tapbi.spark.controlcenter.utils.MethodUtils
import com.tapbi.spark.controlcenter.utils.Utils.setBackgroundIcon
import com.tapbi.spark.controlcenter.utils.safeDelay
import timber.log.Timber
import java.util.Collections

@Suppress("DEPRECATION")
class AllControlsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    ItemTouchHelperAdapter {
    private var list: MutableList<ControlCustomize> = mutableListOf()
    private var context: Context? = null
    private var iClick: ICustomizeControlClick? = null
    private var recyclerView: RecyclerView? = null
    private var isFirstLoad = true


    fun setParameters(context: Context, rcv: RecyclerView, iClick: ICustomizeControlClick) {
        if (recyclerView == null) {
            recyclerView = rcv
        }
        this.context = context
        this.iClick = iClick
    }

    fun setData(newList: List<ControlCustomize>) {
        all.clear()
        all.addAll(newList)
        if (isFirstLoad) {
            isFirstLoad = false
            loadData(true)
        }
    }

    fun setNewData(newList: List<ControlCustomize>) {
        all.clear()
        all.addAll(newList)
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    fun addItem(position: Int, newItems: ControlCustomize) {
        list.add(position, newItems)
        notifyItemInserted(position)
    }

    fun removeItem(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemMoreControlCustomBinding =
            DataBindingUtil.inflate(inflater, R.layout.item_more_control_custom, parent, false)
        return HolderMoreControl(binding)
    }

    fun loadData(isFirstLoad: Boolean = false) {
        if (list.size < all.size) {
            if (isFirstLoad) {
                loadToList()
            } else {
                iClick?.onLoading(true)
                safeDelay(1000) {
                    loadToList()
                    iClick?.onLoading(false)
                }
            }
        }

    }

    private fun loadToList() {
        val start = list.size
        var end = start + 30
        if (all.size < 30) {
            end = start + all.size
        }
        for (i in start until end) {
            try {
                list.add(all[i])
            } catch (_: Exception) {
                break
            }
        }
        if (start > 1) {
            notifyItemRangeChanged(start - 1, list.size)
        } else {
            notifyItemRangeChanged(start, list.size)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as HolderMoreControl).bindMoreApp(holder)
    }

    override fun getItemCount(): Int {
        return list.size //+ 1
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Collections.swap(
            included,
            fromPosition /*- CustomizeControlAdapter.ITEM_BEFORE_VIEW_INCLUDED*/,
            toPosition /*- CustomizeControlAdapter.ITEM_BEFORE_VIEW_INCLUDED*/
        )
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    fun loadIconAfterChange() {
        if (itemCount >= 2) {
            notifyItemRangeChanged(0, 2)
        }
    }

    override fun onItemDismiss(position: Int) {}

    override fun onItemChange() {
        recyclerView!!.post {
            for (i in included.indices) {
                notifyItemChanged(i, included[i])
            }
        }
    }

    interface ICustomizeControlClick {
        fun styleClick(style: Int)
        fun onDelete(position: Int)
        fun onAdd(position: Int)
        fun onLoading(it: Boolean)
        fun onLoadingIncludeDone()
    }

    inner class HolderMoreControl(var moreBinding: ItemMoreControlCustomBinding) :
        RecyclerView.ViewHolder(moreBinding.root) {

        fun bindMoreApp(holder: HolderMoreControl) {
            //val position: Int = holder.adapterPosition /*- CustomizeControlAdapter.ITEM_BEFORE_VIEW_INCLUDED - included.size*/
            val controlCustomize: ControlCustomize = list[holder.adapterPosition]
            val view: View = moreBinding.lineBottom
            if (holder.adapterPosition == itemCount - 1/*- CustomizeControlAdapter.ITEM_BEFORE_VIEW_INCLUDED*/) {
                view.visibility = View.INVISIBLE
            } else {
                view.visibility = View.VISIBLE
            }
            moreBinding.name.text = controlCustomize.name
            if (controlCustomize.isDefault != 0) {
                moreBinding.icon.setPadding(12, 12, 12, 12)
            } else {
                moreBinding.icon.setPadding(0, 0, 0, 0)
            }
            if (controlCustomize.icon == null) {
                controlCustomize.icon =
                    MethodUtils.getIconFromPackageName(context, controlCustomize.packageName)
            }
//            moreBinding.icon.setImageDrawable(controlCustomize.icon)
            Glide.with(itemView.context).load(controlCustomize.icon).into(moreBinding.icon)
            setBackgroundIcon(context, moreBinding.icon, controlCustomize)
            moreBinding.add.setOnClickListener { v ->
                if (iClick != null) {
                    iClick?.onAdd(position)
                }
            }
        }
    }
}
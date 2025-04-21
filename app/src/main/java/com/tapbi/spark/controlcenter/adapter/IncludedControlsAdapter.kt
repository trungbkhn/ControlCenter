package com.tapbi.spark.controlcenter.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tapbi.spark.controlcenter.App.Companion.included
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.databinding.ItemControlCustomiezeBinding
import com.tapbi.spark.controlcenter.eventbus.EventCustomControls
import com.tapbi.spark.controlcenter.feature.controlios14.model.ControlCustomize
import com.tapbi.spark.controlcenter.interfaces.ItemTouchHelperAdapter
import com.tapbi.spark.controlcenter.interfaces.OnStartDragListener
import com.tapbi.spark.controlcenter.utils.MethodUtils
import com.tapbi.spark.controlcenter.utils.Utils.setBackgroundIcon
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.util.Collections

class IncludedControlsAdapter :
    RecyclerView.Adapter<IncludedControlsAdapter.HolderControlCustomize>(), ItemTouchHelperAdapter {

    private var context: Context? = null
    private var iClick: AllControlsAdapter.ICustomizeControlClick? = null
    private var onStartDragListener: OnStartDragListener? = null
    private var recyclerView: RecyclerView? = null

    private var isLoadDone = false

    fun setParameters(
        context: Context,
        rcv: RecyclerView,
        onStartDragListener: OnStartDragListener,
        iClick: AllControlsAdapter.ICustomizeControlClick
    ) {
        if (recyclerView == null) {
            recyclerView = rcv
        }
        this.context = context
        this.onStartDragListener = onStartDragListener
        this.iClick = iClick
    }
//    fun setData(newList: List<ControlCustomize>){
//        val diffCallback = ControlCustomizeDiffUtilCallback(included,newList)
//        val diffResult = DiffUtil.calculateDiff(diffCallback)
//        included.clear()
//        included.addAll(newList)
//        diffResult.dispatchUpdatesTo(this)
//    }

    fun changeList(controlCustomizes: MutableList<ControlCustomize>) {
        included = controlCustomizes
        if (included.size == 0) {
            if (!isLoadDone) {
                isLoadDone = true
                iClick?.onLoadingIncludeDone()
            }
        }
        notifyDataSetChanged()
    }

    fun setNewData(controlCustomizes: MutableList<ControlCustomize>) {
        included = controlCustomizes
        notifyDataSetChanged()
    }



    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Collections.swap(
            included,
            fromPosition /*- CustomizeControlAdapter.ITEM_BEFORE_VIEW_INCLUDED*/,
            toPosition /*- CustomizeControlAdapter.ITEM_BEFORE_VIEW_INCLUDED*/
        )
        notifyItemMoved(fromPosition, toPosition)
        EventBus.getDefault().post(EventCustomControls(Constant.EVENT_CHANGE_GALLERY, included))
        return true
    }

    override fun onItemDismiss(position: Int) {}

    override fun onItemChange() {
        recyclerView?.post(Runnable {
            for (i in included.indices) {
                notifyItemChanged(i, included[i])
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderControlCustomize {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemControlCustomiezeBinding =
            DataBindingUtil.inflate(inflater, R.layout.item_control_customieze, parent, false)
        return HolderControlCustomize(binding)
    }

    override fun getItemCount(): Int {
        return included.size
    }

    override fun onBindViewHolder(holder: HolderControlCustomize, position: Int) {
        holder.bindIncludeControls(holder)
        if (included.size == position + 1 && !isLoadDone) {
            isLoadDone = true
            iClick?.onLoadingIncludeDone()
        }
    }

    @Suppress("DEPRECATION")
    inner class HolderControlCustomize(binding: ItemControlCustomiezeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val includeBinding: ItemControlCustomiezeBinding = binding

        @SuppressLint("ClickableViewAccessibility")
        fun bindIncludeControls(holder: HolderControlCustomize) {
            //val position = pos //- CustomizeControlAdapter.ITEM_BEFORE_VIEW_INCLUDED
            val controlCustomize: ControlCustomize = included.get(holder.adapterPosition)
            val view: View = includeBinding.lineBottom
            if (holder.adapterPosition == included.size - 1) {
                view.visibility = View.INVISIBLE
            } else {
                view.visibility = View.VISIBLE
            }
            includeBinding.name.text = controlCustomize.name
            if (controlCustomize.isDefault != 0) {
                includeBinding.icon.setPadding(12, 12, 12, 12)
            } else {
                includeBinding.icon.setPadding(0, 0, 0, 0)
            }
            if (controlCustomize.icon == null) {
                controlCustomize.icon =
                    MethodUtils.getIconFromPackageName(context, controlCustomize.packageName)
            }
            //includeBinding.icon.setImageDrawable(controlCustomize.icon)
            Glide.with(itemView.context).load(controlCustomize.icon).into(includeBinding.icon)

            setBackgroundIcon(context, includeBinding.icon, controlCustomize)
            includeBinding.drag.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    onStartDragListener?.onStartDrag(holder)
                }
                true
            }
            includeBinding.delete.setOnClickListener { v ->
                iClick?.onDelete(position)
            }
        }
    }
}
package com.tapbi.spark.controlcenter.feature.controlcenter.view.control.pixel.view

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.tapbi.spark.controlcenter.feature.controlios14.model.InfoSystem
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.ControlCenterIOSView
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.ControlCenterViewOS
import timber.log.Timber

class ViewPagePixel : PagerAdapter() {

    private var itemControlPixel: LayoutItemControlPixel? = null
    private var itemControlPixel2: LayoutItemControlPixel? = null

    private var listControl: MutableList<InfoSystem> = mutableListOf()
    private var listControl2: MutableList<InfoSystem> = mutableListOf()

    private var onControlCenterListener: ControlCenterIOSView.OnControlCenterListener? = null

    override fun getCount(): Int {
        return 2
    }

    fun setListInfoSystem(list: List<InfoSystem>, list2: List<InfoSystem>) {
        this.listControl = list.toMutableList()
        this.listControl2 = list2.toMutableList()
        notifyDataSetChanged()

    }

    fun setOnControlCenterListener(onControlCenterListener: ControlCenterIOSView.OnControlCenterListener) {
        this.onControlCenterListener = onControlCenterListener
        itemControlPixel?.setCloseMiControlView(onControlCenterListener)
        itemControlPixel2?.setCloseMiControlView(onControlCenterListener)
    }


    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemControl: LayoutItemControlPixel = if (position == 0) {
            itemControlPixel = LayoutItemControlPixel(container.context)
            itemControlPixel?.apply {
                listControl = this@ViewPagePixel.listControl
            }
            itemControlPixel!!
        } else {
            itemControlPixel2 = LayoutItemControlPixel(container.context)
            itemControlPixel2?.apply {
                listControl = this@ViewPagePixel.listControl2
            }
            itemControlPixel2!!
        }
        onControlCenterListener?.let { itemControl.setCloseMiControlView(it) }
        container.addView(itemControl)
        return itemControl
    }

    fun updateActionView(action: String, b: Boolean) {
        itemControlPixel?.updateActionView(action, b)
        itemControlPixel2?.updateActionView(action, b)
    }

    fun clearViewList(){
        itemControlPixel?.clearViewList()
        itemControlPixel2?.clearViewList()

    }

}
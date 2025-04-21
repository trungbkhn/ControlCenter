package com.tapbi.spark.controlcenter.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.ui.main.customcontrol.color.ColorFragment
import com.tapbi.spark.controlcenter.ui.main.customcontrol.controls.ControlsFragment
import com.tapbi.spark.controlcenter.ui.main.customcontrol.font.FontFragment
import com.tapbi.spark.controlcenter.ui.main.customcontrol.gallery.GalleryFragment

class CustomizeControlViewPage(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    var type: Int
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    private var controlFragment: ControlsFragment? = null
    private var galleryFragment: GalleryFragment? = null
    private var fontFragment: FontFragment? = null
    private var colorFragment: ColorFragment? = null

    override fun getItemCount() = if (type == Constant.VALUE_CONTROL_CENTER_OS) 4 else 3

    override fun createFragment(position: Int): Fragment {
        if (type != Constant.VALUE_CONTROL_CENTER_OS) {
            return when (position) {
                0 -> {
                    //controls
                    getControlsFragment( type)
                }

                1 -> {
                    //font
                    getFontFragment()
                }

                else -> {
                    //background
                    getBackgroundFragment()
                }
            }
        } else {
            return when (position) {
                0 -> {
                    //controls
                    getControlsFragment(type)
                }

                1 -> {
                    //gallery
                    getGalleryFragment()
                }

                2 -> {
                    //font
                    getFontFragment()
                }

                else -> {
                    //background
                    getBackgroundFragment()
                }
            }
        }
    }

    private fun getBackgroundFragment(): ColorFragment {
        if (colorFragment == null) {
            colorFragment = ColorFragment()
        }
        return colorFragment!!
    }

    private fun getFontFragment(): FontFragment {
        if (fontFragment == null) {
            fontFragment = FontFragment()
        }
        return fontFragment!!
    }

    private fun getGalleryFragment(): GalleryFragment {
        if (galleryFragment == null) {
            galleryFragment = GalleryFragment()
        }
        return galleryFragment!!
    }

    private fun getControlsFragment(type: Int): ControlsFragment {
        if (controlFragment == null) {
            controlFragment = ControlsFragment.newInstance(type)
        }
        return controlFragment!!
    }
}
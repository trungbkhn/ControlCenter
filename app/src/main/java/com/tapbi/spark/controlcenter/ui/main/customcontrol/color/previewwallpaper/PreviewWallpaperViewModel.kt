package com.tapbi.spark.controlcenter.ui.main.customcontrol.color.previewwallpaper

import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PreviewWallpaperViewModel @Inject constructor() : BaseViewModel() {
    fun getListPreview(typeNoty: Int): List<Int> {
        val list: ArrayList<Int> = ArrayList()
        when (typeNoty) {
            Constant.VALUE_CONTROL_CENTER -> {
                list.add(R.drawable.preview_mi_noty)
                list.add(R.drawable.preview_mi_control)
            }

            Constant.VALUE_SHADE -> {
                list.add(R.drawable.preview_mi_shade)
            }

            else -> {
                list.add(R.drawable.preview_ios_noty)
                list.add(R.drawable.preview_ios_control_new)
            }
        }
        return list
    }

    fun getPathBackground(idWallpaper: Int): String {
        return if (App.tinyDB.getInt(Constant.STYLE_SELECTED, Constant.LIGHT) == Constant.LIGHT) {
            "file:///android_asset/" + Constant.FOLDER_BACKGROUND_ASSETS + "/" + idWallpaper + "/" + Constant.FILE_NAME_BACKGROUND_LIGHT
        } else {
            "file:///android_asset/" + Constant.FOLDER_BACKGROUND_ASSETS + "/" + idWallpaper + "/" + Constant.FILE_NAME_BACKGROUND_DARK
        }
    }
}
package com.tapbi.spark.controlcenter.data.repository

import android.content.Context
import com.google.gson.Gson
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.common.models.Wallpaper
import java.nio.charset.StandardCharsets
import javax.inject.Inject

class BackgroundRepository @Inject constructor() {
    fun getListBackground(context: Context): ArrayList<Wallpaper> {
        val inputStream = context.assets.open("${Constant.FOLDER_BACKGROUND_ASSETS}/${Constant.FILE_NAME_BACKGROUND_ASSETS}")
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        val json = String(buffer, StandardCharsets.UTF_8)

        val list: ArrayList<Wallpaper> = ArrayList()
        val gson = Gson()
        val objectList = gson.fromJson(json, Array<Wallpaper>::class.java).asList()
        list.addAll(objectList)
        return list
    }




}
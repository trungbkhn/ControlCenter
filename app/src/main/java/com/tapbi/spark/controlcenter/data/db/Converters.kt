package com.tapbi.spark.controlcenter.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.tapbi.spark.controlcenter.data.model.ItemControlCenter
import com.tapbi.spark.controlcenter.data.model.ItemControlPixel
import com.tapbi.spark.controlcenter.data.model.ItemControlSamSung
import com.tapbi.spark.controlcenter.data.model.ItemMiShade
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlCenterOS

class Converters {

    @TypeConverter
    fun fromItemMiShade(value: ItemMiShade): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toItemMiShade(value: String): ItemMiShade {
        return Gson().fromJson(value, ItemMiShade::class.java)
    }

    @TypeConverter
    fun fromItemControlCenter(value: ItemControlCenter): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toItemControlCenter(value: String): ItemControlCenter {
        return Gson().fromJson(value, ItemControlCenter::class.java)
    }

    @TypeConverter
    fun fromItemControlPixel(value: ItemControlPixel): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toItemControlPixel(value: String): ItemControlPixel {
        return Gson().fromJson(value, ItemControlPixel::class.java)
    }

    @TypeConverter
    fun fromItemControlSamSung(value: ItemControlSamSung): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun fromItemControlOS(value: ControlCenterOS?): String {
        if (value == null) return ""
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toItemControlSamSung(value: String): ItemControlSamSung {
        return Gson().fromJson(value, ItemControlSamSung::class.java)
    }

    @TypeConverter
    fun toItemControlOS(value: String): ControlCenterOS? {
        if (value.isEmpty()) return null
        return Gson().fromJson(value, ControlCenterOS::class.java)
    }

}

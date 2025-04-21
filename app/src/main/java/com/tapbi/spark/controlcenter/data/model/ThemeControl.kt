package com.tapbi.spark.controlcenter.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tapbi.spark.controlcenter.common.Constant

@Entity(tableName = Constant.TABLE_THEME_CONTROL)
data class ThemeControl(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    val idCategory: Int,
    var preview: String,
)
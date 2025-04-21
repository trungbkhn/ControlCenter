package com.tapbi.spark.controlcenter.data.model

data class ThemeControlFavorite(
    var id: Long,
    val idCategory: Int,
    var preview: String,
    var name: String,
    var isSelect: Boolean
)
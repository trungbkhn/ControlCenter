package com.simform.custombottomnavigation

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes

data class Model(
    @DrawableRes
    val icon: Int = 0,
    @IdRes
    val destinationId: Int = -1,
    val id: Int = -1,
    @StringRes
    val text: Int = 0,
    @StringRes
    var count: Int = R.string.empty_value,
    var allowShowView: Boolean = false
){
    constructor(icon: Int, destinationId: Int, id: Int, text: Int, count: Int) : this(icon, destinationId, id, text, count, true)
}
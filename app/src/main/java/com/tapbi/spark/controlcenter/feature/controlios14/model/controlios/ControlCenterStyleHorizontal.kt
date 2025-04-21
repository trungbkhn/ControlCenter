package com.tapbi.spark.controlcenter.feature.controlios14.model.controlios

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ControlCenterStyleHorizontal {
    @SerializedName("list_control_left")
    @Expose
    var listControlLeft : List<ControlCenterIosModel>? = null

    @SerializedName("list_control_right")
    @Expose
    var listControlRight : List<ControlCenterIosModel>? = null
}
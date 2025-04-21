package com.tapbi.spark.controlcenter.feature.controlios14.model.controlios

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ControlCenterOS {
    @SerializedName("orientation_vertical_top")
    @Expose
    var listControlCenterStyleVerticalTop : List<ControlCenterIosModel>? = null
    @SerializedName("orientation_vertical_bottom")
    @Expose
    var listControlCenterStyleVerticalBottom : List<ControlCenterIosModel>? = null
    @SerializedName("orientation_horizontal")
    @Expose
    var controlCenterStyleHorizontal : ControlCenterStyleHorizontal? = null
}
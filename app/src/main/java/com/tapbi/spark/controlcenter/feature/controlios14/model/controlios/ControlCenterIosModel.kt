package com.tapbi.spark.controlcenter.feature.controlios14.model.controlios

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ControlCenterIosModel {
    @SerializedName("keyControl")
    @Expose
    var keyControl: String = ""

    @SerializedName("ratioWidget")
    @Expose
    var ratioWidght: Int = 1

    @SerializedName("ratioHeight")
    @Expose
    var ratioHeight: Int = 1

    @SerializedName("controlSettingIosModel")
    @Expose
    var controlSettingIosModel: ControlSettingIosModel? = null

    @SerializedName("controlBrightnessVolumeIosModel")
    @Expose
    var controlBrightnessVolumeIosModel: ControlBrightnessVolumeIosModel? = null

    @SerializedName("controlMusicIosModel")
    @Expose
    var controlMusicIosModel: ControlMusicIosModel? = null

    override fun toString(): String {
        return keyControl + "/" + controlSettingIosModel.toString() + "/" + controlBrightnessVolumeIosModel.toString() + "/" + controlMusicIosModel.toString()
    }


    constructor(controlCenterIosModel :ControlCenterIosModel?){
        controlCenterIosModel?.let {
            this.keyControl = controlCenterIosModel.keyControl
            this.ratioWidght = controlCenterIosModel.ratioWidght
            this.ratioHeight = controlCenterIosModel.ratioHeight
            this.controlSettingIosModel = controlCenterIosModel.controlSettingIosModel
            this.controlBrightnessVolumeIosModel = controlCenterIosModel.controlBrightnessVolumeIosModel
            this.controlMusicIosModel = controlCenterIosModel.controlMusicIosModel
        }
    }


    constructor()

}
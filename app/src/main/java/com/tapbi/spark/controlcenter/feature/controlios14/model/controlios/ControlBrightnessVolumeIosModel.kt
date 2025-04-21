package com.tapbi.spark.controlcenter.feature.controlios14.model.controlios

class ControlBrightnessVolumeIosModel : BaseControlModel {

    var colorBackgroundSeekbarDefault : String = "#80FFFFFF"
    var colorBackgroundSeekbarProgress : String = "#80FFFFFF"
    var cornerBackgroundSeekbar : Float = 21f
    var colorThumbSeekbar : String = "#FFFFFF"
    var colorIcon : String = "#000000"
    var colorText : String = "#000000"

    constructor(
        backgroundDefaultColorViewParent: String,
        backgroundSelectColorViewParent: String,
        backgroundImageViewParent: String,
        cornerBackgroundViewParent: Float,
        iconControl: String,
        colorBackgroundSeekbarDefault: String,
        colorBackgroundSeekbarProgress: String,
        cornerBackgroundSeekbar: Float,
        colorThumbSeekbar: String,
        colorIcon: String,
        colorText: String
    ) : super(
        backgroundDefaultColorViewParent,
        backgroundSelectColorViewParent,
        backgroundImageViewParent,
        cornerBackgroundViewParent,
        iconControl
    ) {
        this.colorBackgroundSeekbarDefault = colorBackgroundSeekbarDefault
        this.colorBackgroundSeekbarProgress = colorBackgroundSeekbarProgress
        this.cornerBackgroundSeekbar = cornerBackgroundSeekbar
        this.colorThumbSeekbar = colorThumbSeekbar
        this.colorIcon = colorIcon
        this.colorText = colorText
    }
    fun clone(): ControlBrightnessVolumeIosModel {
        return ControlBrightnessVolumeIosModel(
            backgroundDefaultColorViewParent = this.backgroundDefaultColorViewParent ?: "",
            backgroundSelectColorViewParent = this.backgroundSelectColorViewParent ?: "",
            backgroundImageViewParent = this.backgroundImageViewParent ?: "",
            cornerBackgroundViewParent = this.cornerBackgroundViewParent,
            iconControl = this.iconControl ?: "",
            colorBackgroundSeekbarDefault = this.colorBackgroundSeekbarDefault ?: "#80FFFFFF",
            colorBackgroundSeekbarProgress = this.colorBackgroundSeekbarProgress ?: "#80FFFFFF",
            cornerBackgroundSeekbar = this.cornerBackgroundSeekbar,
            colorThumbSeekbar = this.colorThumbSeekbar ?: "#FFFFFF",
            colorIcon = this.colorIcon ?: "#000000",
            colorText = this.colorText ?: "#000000"
        )
    }

}
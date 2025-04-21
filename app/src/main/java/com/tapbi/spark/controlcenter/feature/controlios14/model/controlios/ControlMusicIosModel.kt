package com.tapbi.spark.controlcenter.feature.controlios14.model.controlios

class ControlMusicIosModel : BaseControlModel {

    var cornerImageAvatarMusic : Float = 0f
    var colorIcon : String = "#FFFFFF"
    var colorTextName : String = "#FFFFFF"
    var colorTextArtists : String = "#80FFFFFF"
    var colorTextTime : String = "#FFFFFF"
    var colorTextDuration : String = "#80FFFFFF"
    var colorDefaultSeekbar : String = "#80FFFFFF"
    var colorProgressSeekbar : String = "#FFFFFF"
    var colorThumbSeekbar : String? = null

    constructor(
        backgroundDefaultColorViewParent: String,
        backgroundSelectColorViewParent: String,
        backgroundImageViewParent: String,
        cornerBackgroundViewParent: Float,
        iconControl: String,
        cornerImageAvatarMusic: Float,
        colorIcon: String,
        colorTextName: String,
        colorTextArtists: String,
        colorTextTime: String,
        colorTextDuration: String,
        colorDefaultSeekbar: String,
        colorProgressSeekbar: String,
        colorThumbSeekbar: String?
    ) : super(
        backgroundDefaultColorViewParent,
        backgroundSelectColorViewParent,
        backgroundImageViewParent,
        cornerBackgroundViewParent,
        iconControl
    ) {
        this.cornerImageAvatarMusic = cornerImageAvatarMusic
        this.colorIcon = colorIcon
        this.colorTextName = colorTextName
        this.colorTextArtists = colorTextArtists
        this.colorTextTime = colorTextTime
        this.colorTextDuration = colorTextDuration
        this.colorDefaultSeekbar = colorDefaultSeekbar
        this.colorProgressSeekbar = colorProgressSeekbar
        this.colorThumbSeekbar = colorThumbSeekbar
    }
    fun clone(): ControlMusicIosModel {
        return ControlMusicIosModel(
            backgroundDefaultColorViewParent = this.backgroundDefaultColorViewParent ?: "",
            backgroundSelectColorViewParent = this.backgroundSelectColorViewParent ?: "",
            backgroundImageViewParent = this.backgroundImageViewParent ?: "",
            cornerBackgroundViewParent = this.cornerBackgroundViewParent,
            iconControl = this.iconControl ?: "",
            cornerImageAvatarMusic = this.cornerImageAvatarMusic,
            colorIcon = this.colorIcon ?: "#FFFFFF",
            colorTextName = this.colorTextName ?: "#FFFFFF",
            colorTextArtists = this.colorTextArtists ?: "#80FFFFFF",
            colorTextTime = this.colorTextTime ?: "#FFFFFF",
            colorTextDuration = this.colorTextDuration ?: "#80FFFFFF",
            colorDefaultSeekbar = this.colorDefaultSeekbar ?: "#80FFFFFF",
            colorProgressSeekbar = this.colorProgressSeekbar ?: "#FFFFFF",
            colorThumbSeekbar = this.colorThumbSeekbar
        )
    }

}
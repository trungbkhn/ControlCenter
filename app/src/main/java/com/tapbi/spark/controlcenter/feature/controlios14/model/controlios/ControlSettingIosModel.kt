package com.tapbi.spark.controlcenter.feature.controlios14.model.controlios

class ControlSettingIosModel : BaseControlModel {
    var backgroundColorDefaultViewItem : String = ""
    var backgroundColorSelectViewItem : String = ""
    var backgroundImageViewItem : String = ""
    var isFilterBackgroundViewItem : Boolean = false
    var cornerBackgroundViewItem : Float = 0f
    var colorDefaultIcon : String = "#FFFFFF"
    var colorSelectIcon : String = "#0000FF"
    var colorTextTitle : String = "#FFFFFF"
    var colorTextTitleSelect : String = "#FFFFFF"
    var colorTextDescription : String = "#80FFFFFF"
    var colorTextDescriptionSelect : String = "#80FFFFFF"



    constructor(
        backgroundDefaultColorViewParent: String,
        backgroundSelectColorViewParent: String,
        backgroundImageViewParent: String,
        cornerBackgroundViewParent: Float,
        iconControl: String,
        backgroundColorDefaultViewItem: String,
        backgroundColorSelectViewItem: String,
        backgroundImageViewItem: String,
        isFilterBackgroundViewItem: Boolean,
        cornerBackgroundViewItem: Float,
        colorDefaultIcon: String,
        colorSelectIcon: String,
        colorTextTitle: String,
        colorTextTitleSelect: String,
        colorTextDescription: String,
        colorTextDescriptionSelect: String
    ) : super(
        backgroundDefaultColorViewParent,
        backgroundSelectColorViewParent,
        backgroundImageViewParent,
        cornerBackgroundViewParent,
        iconControl
    ) {
        this.backgroundColorDefaultViewItem = backgroundColorDefaultViewItem
        this.backgroundColorSelectViewItem = backgroundColorSelectViewItem
        this.backgroundImageViewItem = backgroundImageViewItem
        this.isFilterBackgroundViewItem = isFilterBackgroundViewItem
        this.cornerBackgroundViewItem = cornerBackgroundViewItem
        this.colorDefaultIcon = colorDefaultIcon
        this.colorSelectIcon = colorSelectIcon
        this.colorTextTitle = colorTextTitle
        this.colorTextTitleSelect = colorTextTitleSelect
        this.colorTextDescription = colorTextDescription
        this.colorTextDescriptionSelect = colorTextDescriptionSelect
    }

    override fun toString(): String {
        return "ControlSettingIosModel(backgroundImageViewItem='$backgroundImageViewItem', backgroundColorSelectViewItem='$backgroundColorSelectViewItem', colorSelectIcon" +colorSelectIcon
    }
    fun clone(): ControlSettingIosModel {
        return ControlSettingIosModel(
            backgroundDefaultColorViewParent = this.backgroundDefaultColorViewParent,
            backgroundSelectColorViewParent = this.backgroundSelectColorViewParent,
            backgroundImageViewParent = this.backgroundImageViewParent,
            cornerBackgroundViewParent = this.cornerBackgroundViewParent,
            iconControl = this.iconControl,
            backgroundColorDefaultViewItem = this.backgroundColorDefaultViewItem,
            backgroundColorSelectViewItem = this.backgroundColorSelectViewItem,
            backgroundImageViewItem = this.backgroundImageViewItem,
            isFilterBackgroundViewItem = this.isFilterBackgroundViewItem,
            cornerBackgroundViewItem = this.cornerBackgroundViewItem,
            colorDefaultIcon = this.colorDefaultIcon,
            colorSelectIcon = this.colorSelectIcon,
            colorTextTitle = this.colorTextTitle,
            colorTextTitleSelect = this.colorTextTitleSelect,
            colorTextDescription = this.colorTextDescription,
            colorTextDescriptionSelect = this.colorTextDescriptionSelect
        )
    }

}
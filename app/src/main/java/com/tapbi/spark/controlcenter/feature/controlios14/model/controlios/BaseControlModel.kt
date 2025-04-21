package com.tapbi.spark.controlcenter.feature.controlios14.model.controlios

import com.tapbi.spark.controlcenter.common.Constant

open class BaseControlModel {
    var backgroundDefaultColorViewParent : String = ""
    var backgroundSelectColorViewParent : String = ""
    var backgroundImageViewParent : String = ""
    var cornerBackgroundViewParent : Float = 0f
    var iconControl : String = Constant.ICON_DEFAULT

    constructor(
        backgroundDefaultColorViewParent: String,
        backgroundSelectColorViewParent: String,
        backgroundImageViewParent: String,
        cornerBackgroundViewParent: Float,
        iconControl: String
    ) {
        this.backgroundDefaultColorViewParent = backgroundDefaultColorViewParent
        this.backgroundSelectColorViewParent = backgroundSelectColorViewParent
        this.backgroundImageViewParent = backgroundImageViewParent
        this.cornerBackgroundViewParent = cornerBackgroundViewParent
        this.iconControl = iconControl
    }
}
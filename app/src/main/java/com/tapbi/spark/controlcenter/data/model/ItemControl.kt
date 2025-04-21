package com.tapbi.spark.controlcenter.data.model

import androidx.room.ColumnInfo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlCenterOS

//class ItemThemeControl {
//    var id = 1
//    var id_category = 2000
//    var mi_shade = ItemMiShade()
//    var preview = "file:///android_asset/themes/1/thumb.webp"
//    var control_center = ItemControlCenter()
//    var pixel = ItemControlPixel()
//    var samsung = ItemControlSamSung()
//
//}
data class ItemControl(
    var id: Long = 0,
    @SerializedName("id_category")
    @Expose
    var idCategory: Int = 2000,
    @SerializedName("type_background")
    @Expose
    var typeBackground: String = Constant.DEFAULT,
    var idStoreWallpaper: Int = -1,
    var background: String = "background.webp",
    var backgroundColor: String = "",
    @SerializedName("mi_shade")
    @Expose
    var miShade: ItemMiShade? = null,
    @SerializedName("control_center")
    @Expose
    var controlCenter: ItemControlCenter? = null,
    var pixel: ItemControlPixel? = null,
    var samsung: ItemControlSamSung? = null,
    @ColumnInfo(name = "control_os")
    @SerializedName("control_os")
    @Expose
    var controlCenterOS: ControlCenterOS? = null,
    var colorStatus: String = "#FFFFFF",
    var font: String = "",
    @SerializedName("isThemeIos18")
    @Expose
    var isThemeIos18: Boolean = false
)

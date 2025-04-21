package com.tapbi.spark.controlcenter.feature.controlios14.model.controlios

import android.graphics.Typeface

class DataSetupViewControlModel {
    var id : Long = 0
    var idCategory : Int = 1000
    var typefaceText : Typeface? = null

    constructor(id: Long, idCategory: Int, typefaceText: Typeface?) {
        this.id = id
        this.idCategory = idCategory
        this.typefaceText = typefaceText
    }
}
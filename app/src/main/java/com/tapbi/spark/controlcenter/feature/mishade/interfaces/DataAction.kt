package com.tapbi.spark.controlcenter.feature.mishade.interfaces

import com.tapbi.spark.controlcenter.feature.controlios14.model.InfoSystem

interface DataAction {
    fun dataRcc1(): List<InfoSystem>
    fun dataRcc2(): List<InfoSystem>
    fun dataRcc3(): List<InfoSystem>
    fun dataRcc4(): List<InfoSystem>
    fun dataRcc5(): List<InfoSystem>
    fun dataRcc6(): List<InfoSystem>
}

package com.tapbi.spark.controlcenter.ui.main.customizetext

import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614
import com.tapbi.spark.controlcenter.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CustomizeTextViewModel @Inject constructor() : BaseViewModel() {
    fun setChangeTextTitleMiControl() {
        if (NotyControlCenterServicev614.getInstance() != null && NotyControlCenterServicev614.getInstance().typeChoose == Constant.VALUE_CONTROL_CENTER && NotyControlCenterServicev614.getInstance().controlCenterView != null) {
            NotyControlCenterServicev614.getInstance().controlCenterView.updateTextTitle()
        }
    }
}

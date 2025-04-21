package com.tapbi.spark.controlcenter.feature.controlcenter.view.control.oppo.view

import android.content.Context
import android.provider.Settings
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.databinding.ViewSettingsOppoBinding
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CloseMiControlView
import com.tapbi.spark.controlcenter.feature.controlios14.model.InfoSystem

class SettingsViewOppo : ConstraintLayout {

    var binding = ViewSettingsOppoBinding.inflate(LayoutInflater.from(context), this, true)
    private var closeMiControlView: CloseMiControlView? = null

    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {

    }


    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {

    }

    init {


        binding.itemWifi.data(
            InfoSystem(
                Constant.STRING_ACTION_WIFI,
                "",
                Settings.ACTION_WIFI_SETTINGS,
                R.drawable.ic_mi_wifi
            ), 0, closeMiControlView
        )


        binding.itemDataMobi.data(
            InfoSystem(
                Constant.STRING_ACTION_DATA_MOBILE,
                "",
                Settings.ACTION_DATA_ROAMING_SETTINGS,
                R.drawable.ic_mi_mobile_data
            ), 1, closeMiControlView
        )

        binding.itemBluetooth.data(
            InfoSystem(
                Constant.STRING_ACTION_BLUETOOTH,
                "",
                Settings.ACTION_BLUETOOTH_SETTINGS,
                R.drawable.ic_mi_bluetooth
            ), 2, closeMiControlView
        )

        binding.itemAirplane.data(
            InfoSystem(
                Constant.STRING_ACTION_AIRPLANE_MODE,
                "",
                Settings.ACTION_AIRPLANE_MODE_SETTINGS,
                R.drawable.ic_mi_airplane
            ), 3, closeMiControlView
        )



    }
}
package com.tapbi.spark.controlcenter.utils

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.ironman.trueads.common.RemoteConfigControl
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.common.Constant.TYPE_ADS_1
import com.tapbi.spark.controlcenter.common.Constant.TYPE_CHOOSE_STYLE_1
import com.tapbi.spark.controlcenter.common.Constant.TYPE_PREVIEW_THEMECONTROL_1

class RemoteConfigHelper {
    var typeAdsRequestPermission: Long = TYPE_ADS_1
    var typePreviewThemeControl: Long = TYPE_PREVIEW_THEMECONTROL_1
    var typeChooseStyle: Long = TYPE_CHOOSE_STYLE_1

    companion object {
        @JvmStatic
        val instance: RemoteConfigHelper by lazy {
            RemoteConfigHelper()
        }

//        fun getTypeAdsRequestPermission(): Long {
//            return instance.typeAdsRequestPermission
//        }
//
//        fun getTypePreviewThemeControl(): Long {
//            return instance.typePreviewThemeControl
//        }


    }


//    fun initRemoteConfig(onCompleteListener: () -> Unit) {
//        loadNewDataRemoteConfig {
//            typeAdsRequestPermission = FirebaseRemoteConfig.getInstance()
//                .getLong(Constant.TYPE_ADS_REQUEST_PERMISSION)
//            typePreviewThemeControl =
//                FirebaseRemoteConfig.getInstance().getLong(Constant.TYPE_PREVIEW_THEME_CONTROL)
//            typePreviewThemeControl =
//                FirebaseRemoteConfig.getInstance().getLong(Constant.TYPE_PREVIEW_THEME_CONTROL)
//            onCompleteListener()
//        }
//    }

    private fun loadNewDataRemoteConfig(onCompleteListener: () -> Unit) {
        FirebaseRemoteConfig.getInstance().reset().addOnCompleteListener {
            FirebaseRemoteConfig.getInstance().setDefaultsAsync(R.xml.remote_config_defaults)
                .addOnCompleteListener {
                    RemoteConfigControl.initRemoteConfig(App.ins)
                    FirebaseRemoteConfig.getInstance().fetchAndActivate()
                        .addOnCompleteListener {
                            onCompleteListener()
                        }
                }
        }
    }
}

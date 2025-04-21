package com.tapbi.spark.controlcenter.ui.splash

import com.tapbi.spark.controlcenter.common.LiveEvent
import com.tapbi.spark.controlcenter.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor() : BaseViewModel() {
    var eventLoadAdsOpen: LiveEvent<Boolean> = LiveEvent()

}
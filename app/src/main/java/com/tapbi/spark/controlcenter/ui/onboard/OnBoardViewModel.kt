package com.tapbi.spark.controlcenter.ui.onboard

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tapbi.spark.controlcenter.data.model.ItemOnboarding
import com.tapbi.spark.controlcenter.ui.base.BaseViewModel
import com.tapbi.spark.controlcenter.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


@HiltViewModel
class OnBoardViewModel @Inject constructor() : BaseViewModel() {
    var liveDataListOnboard = MutableLiveData<List<ItemOnboarding>>()
    fun getListItemOnboard(context: Context, isShowFullAds: Boolean) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler(fun(
            _: CoroutineContext,
            throwable: Throwable
        ) {
            run {
                Timber.e(throwable)
            }
        })) {
            liveDataListOnboard.postValue(Utils.getListItemOnboard(context, isShowFullAds))
        }
    }
}
package com.tapbi.spark.controlcenter.ui.main.customcontrol.font

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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
class FontViewModel @Inject constructor() : BaseViewModel() {

    var liveDataListFont = MutableLiveData<MutableList<String>>()

    fun getListFontAssets() {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler(fun(
            _: CoroutineContext,
            throwable: Throwable
        ) {
            run {
                Timber.e(throwable)
            }
        })) {
            liveDataListFont.postValue(Utils.getListFont().toMutableList())
        }
    }
}
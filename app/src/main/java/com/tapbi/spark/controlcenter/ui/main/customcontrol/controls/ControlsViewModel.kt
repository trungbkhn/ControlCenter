package com.tapbi.spark.controlcenter.ui.main.customcontrol.controls

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tapbi.spark.controlcenter.data.model.GroupColor
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
class ControlsViewModel @Inject constructor() : BaseViewModel() {

    var listLiveDataIconShape = MutableLiveData<MutableList<String>>()
    var listLiveDataColor = MutableLiveData<MutableList<Int>>()
    var listLiveDataGroupColor = MutableLiveData<MutableList<GroupColor>>()


    fun getListIconShapeControl() {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler(fun(
            _: CoroutineContext,
            throwable: Throwable
        ) {
            run {
                Timber.e(throwable)
            }
        })) {
            listLiveDataIconShape.postValue(Utils.getListIconShape().toMutableList())
        }
    }

    fun getListColor() {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler(fun(
            _: CoroutineContext,
            throwable: Throwable
        ) {
            run {
                Timber.e(throwable)
            }
        })) {
            listLiveDataColor.postValue(Utils.getListColorControls().toMutableList())
        }
    }

    fun getListGroupColor() {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler(fun(
            _: CoroutineContext,
            throwable: Throwable
        ) {
            run {
                Timber.e(throwable)
            }
        })) {
            listLiveDataGroupColor.postValue(Utils.getListGroupColor().toMutableList())
        }
    }
}
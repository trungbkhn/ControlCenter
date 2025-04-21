package com.tapbi.spark.controlcenter.ui.main.focus

import androidx.lifecycle.MutableLiveData
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.data.model.FocusIOS
import com.tapbi.spark.controlcenter.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FocusViewModel @Inject constructor() : BaseViewModel() {
    var focusSetting = MutableLiveData<FocusIOS>()
    var nextAfterFullScreenLiveData = MutableLiveData<Boolean>()
    fun getFocusById(id: Int) {
        App.ins.focusPresetRepository.getFocusById(id).subscribe(object : SingleObserver<FocusIOS> {
            override fun onSubscribe(d: Disposable) {
                compositeDisposable.add(d)
            }
            override fun onSuccess(focusIOS: FocusIOS) {
                focusSetting.postValue(focusIOS)
            }

            override fun onError(e: Throwable) {}
        })
    }
}
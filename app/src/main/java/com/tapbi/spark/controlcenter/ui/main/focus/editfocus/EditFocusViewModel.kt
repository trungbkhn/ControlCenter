package com.tapbi.spark.controlcenter.ui.main.focus.editfocus

import androidx.lifecycle.MutableLiveData
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

@HiltViewModel
class EditFocusViewModel @Inject constructor() : BaseViewModel() {
    var editFocusMutableLiveData = MutableLiveData<Boolean?>()
    fun editFocus(name: String?, imageLink: String?, color: String?, oldName: String?) {
        App.ins.focusPresetRepository.editFocus(name, imageLink, color, oldName)
            .subscribe(object : SingleObserver<Boolean> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(aBoolean: Boolean) {
                    editFocusMutableLiveData.postValue(aBoolean)
                }

                override fun onError(e: Throwable) {}
            })
    }
}
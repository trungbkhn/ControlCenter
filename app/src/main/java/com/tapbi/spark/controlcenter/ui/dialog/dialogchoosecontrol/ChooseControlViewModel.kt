package com.tapbi.spark.controlcenter.ui.dialog.dialogchoosecontrol

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.tapbi.spark.controlcenter.common.models.CustomizeControlApp
import com.tapbi.spark.controlcenter.data.repository.PackageRepository
import com.tapbi.spark.controlcenter.ui.base.BaseViewModel
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable

class ChooseControlViewModel : BaseViewModel() {
    var customizeControlAppLiveData = MutableLiveData<CustomizeControlApp>()

    fun getListAppForCustomize(
        context: Context?,
        customControlCurrent: Array<String?>
    ) {

        PackageRepository().getListAppCustomizeRx(context, customControlCurrent)
            .subscribe(object : SingleObserver<CustomizeControlApp> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(customizeControlApp: CustomizeControlApp) {
                    customizeControlAppLiveData.postValue(customizeControlApp)
                }

                override fun onError(e: Throwable) {
                }
            })
    }
}
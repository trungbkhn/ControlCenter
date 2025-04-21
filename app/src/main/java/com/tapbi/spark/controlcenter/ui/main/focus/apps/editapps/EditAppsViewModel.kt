package com.tapbi.spark.controlcenter.ui.main.focus.apps.editapps

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.data.model.ItemApp
import com.tapbi.spark.controlcenter.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.CompletableObserver
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EditAppsViewModel @Inject constructor() : BaseViewModel() {
    var listAppLiveData = MutableLiveData<MutableList<ItemApp?>?>()
    var deleteAutomationFocus = MutableLiveData<Boolean>()
    fun getAllApp(context: Context?) {
        App.ins.applicationRepository.getListAllAppInstall(context)
            .subscribe(object : SingleObserver<MutableList<ItemApp?>> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(itemApps: MutableList<ItemApp?>) {
                    listAppLiveData.postValue(itemApps)
                }

                override fun onError(e: Throwable) {
                    Timber.e(e)
                }
            })
    }

    fun updateAppAutomation(
        name: String?,
        packageName: String?,
        nameApp: String?,
        lastModify: Long?,
        oldApp: String?
    ) {
        Completable.fromRunnable {
            App.ins.applicationRepository.updateAutomationAppFocus(
                name,
                packageName,
                nameApp,
                lastModify,
                oldApp
            )
        }
            .subscribeOn(Schedulers.io()).subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }
                override fun onComplete() {}
                override fun onError(e: Throwable) {}
            })
    }
}
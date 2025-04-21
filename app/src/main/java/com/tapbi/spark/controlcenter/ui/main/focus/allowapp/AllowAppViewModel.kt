package com.tapbi.spark.controlcenter.ui.main.focus.allowapp

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.data.model.ItemApp
import com.tapbi.spark.controlcenter.data.repository.ApplicationRepository
import com.tapbi.spark.controlcenter.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AllowAppViewModel @Inject constructor(private val applicationRepository: ApplicationRepository) :
    BaseViewModel() {
    var listAppLiveData = MutableLiveData<MutableList<ItemApp>>()
    var deleteAllowedAppsLiveData = MutableLiveData<Boolean>()
    fun getAllApp(context: Context?) {
        applicationRepository.getListAllAppInstall(context)
            .subscribe(object : SingleObserver<MutableList<ItemApp>> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(itemApps: MutableList<ItemApp>) {
                    App.listAppDevice = itemApps
                    listAppLiveData.postValue(itemApps)
                }

                override fun onError(e: Throwable) {
                    Timber.e(e)
                }
            })
    }

    fun deleteAllowedApps(nameFocus: String?) {
        applicationRepository.deleteAllItemAllowedApp(nameFocus)
            .subscribe(object : SingleObserver<Boolean> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(aBoolean: Boolean) {
                    deleteAllowedAppsLiveData.postValue(aBoolean)
                }

                override fun onError(e: Throwable) {}
            })
    }

    fun insertItemApps(itemApp: ItemApp?) {
        applicationRepository.insertItemAllowedApp(itemApp)
            .subscribe(object : SingleObserver<Boolean> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(aBoolean: Boolean) {}
                override fun onError(e: Throwable) {}
            })
    }
}
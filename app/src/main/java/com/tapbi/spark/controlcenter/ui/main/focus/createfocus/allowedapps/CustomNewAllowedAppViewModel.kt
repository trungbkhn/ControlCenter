package com.tapbi.spark.controlcenter.ui.main.focus.createfocus.allowedapps

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.tapbi.spark.controlcenter.data.model.ItemApp
import com.tapbi.spark.controlcenter.data.model.ItemPeople
import com.tapbi.spark.controlcenter.data.repository.ApplicationRepository
import com.tapbi.spark.controlcenter.data.repository.ContactReposition
import com.tapbi.spark.controlcenter.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CustomNewAllowedAppViewModel @Inject constructor(
    private val contactReposition: ContactReposition,
    private val applicationRepository: ApplicationRepository
) : BaseViewModel() {
    var insertAllowPeople = MutableLiveData<Boolean?>()
    var insertAllowApp = MutableLiveData<Boolean?>()
    var listApp = MutableLiveData<MutableList<ItemApp?>>()
    var deleteAllowedAppsLiveData = MutableLiveData<Boolean?>()
    fun insertItemAllowedPeople(listItemPeople: MutableList<ItemPeople>, nameFocus: String?) {
        contactReposition.insertCustomNewAllowedPeople(listItemPeople, nameFocus)
            .subscribe(object : SingleObserver<Boolean> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(aBoolean: Boolean) {
                    insertAllowPeople.postValue(aBoolean)
                }

                override fun onError(e: Throwable) {
                    Timber.e(e)
                }
            })
    }

    fun insertItemAllowedApp(listItemApp: MutableList<ItemApp>, nameFocus: String?) {
        applicationRepository.insertNewCustomAllowApp(listItemApp, nameFocus)
            .subscribe(object : SingleObserver<Boolean> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(aBoolean: Boolean) {
                    insertAllowApp.postValue(aBoolean)
                }

                override fun onError(e: Throwable) {
                    Timber.e(e)
                }
            })
    }

    fun getAllApp(context: Context?) {
        applicationRepository.getListAllAppInstall(context)
            .subscribe(object : SingleObserver<MutableList<ItemApp?>> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(itemApps: MutableList<ItemApp?>) {
                    listApp.postValue(itemApps)
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
}
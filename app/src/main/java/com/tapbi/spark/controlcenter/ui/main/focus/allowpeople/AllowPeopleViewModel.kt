package com.tapbi.spark.controlcenter.ui.main.focus.allowpeople

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.tapbi.spark.controlcenter.data.model.ItemPeople
import com.tapbi.spark.controlcenter.data.repository.ContactReposition
import com.tapbi.spark.controlcenter.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AllowPeopleViewModel @Inject constructor(private val contactReposition: ContactReposition) :
    BaseViewModel() {
    var listAllPeopleLiveData = MutableLiveData<MutableList<ItemPeople>>()
    var listFavoritePeopleLiveData = MutableLiveData<MutableList<ItemPeople>>()
    var deleteItemAllowedPeopleLiveData = MutableLiveData<Boolean?>()
    fun getAllPeople(context: Context?) {
        contactReposition.getListAllPeople(context, false)
            .subscribe(object : SingleObserver<MutableList<ItemPeople>> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(itemPeople: MutableList<ItemPeople>) {
                    listAllPeopleLiveData.postValue(itemPeople)
                }

                override fun onError(e: Throwable) {
                    Timber.e(e)
                }
            })
    }

    fun getFavoritePeople(context: Context?) {
        contactReposition.getListAllPeople(context, true)
            .subscribe(object : SingleObserver<MutableList<ItemPeople>> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(itemPeople: MutableList<ItemPeople>) {
                    listFavoritePeopleLiveData.postValue(itemPeople)
                }

                override fun onError(e: Throwable) {
                    Timber.e(e)
                }
            })
    }

    fun insertAllowedPeople(itemPeople: ItemPeople?) {
        contactReposition.insertAllowedPeople(itemPeople)
            .subscribe(object : SingleObserver<Boolean> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }
                override fun onSuccess(aBoolean: Boolean) {}
                override fun onError(e: Throwable) {}
            })
    }

    fun deleteItemAllowedPeople(name: String?) {
        contactReposition.deleteItemAllowPeople(name).subscribe(object : SingleObserver<Boolean> {
            override fun onSubscribe(d: Disposable) {
                compositeDisposable.add(d)
            }

            override fun onSuccess(aBoolean: Boolean) {
                deleteItemAllowedPeopleLiveData.postValue(aBoolean)
            }

            override fun onError(e: Throwable) {}
        })
    }

    fun updateFocusIOS(mode: Int, name: String?) {
        contactReposition.updateFocusIOS(mode, name).subscribe(object : SingleObserver<Boolean> {
            override fun onSubscribe(d: Disposable) {
                compositeDisposable.add(d)
            }

            override fun onSuccess(aBoolean: Boolean) {}
            override fun onError(e: Throwable) {}
        })
    }
}
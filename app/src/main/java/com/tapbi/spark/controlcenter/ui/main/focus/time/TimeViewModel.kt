package com.tapbi.spark.controlcenter.ui.main.focus.time

import android.content.Context
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.data.model.ItemTurnOn
import com.tapbi.spark.controlcenter.data.repository.TimeRepository
import com.tapbi.spark.controlcenter.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.CompletableObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class TimeViewModel @Inject constructor(private val timeRepository: TimeRepository) :
    BaseViewModel() {
    fun insertAutomationFocus(itemTurnOn: ItemTurnOn?, context: Context?) {
        Completable.fromRunnable {
            timeRepository.insertAutomationFocus(itemTurnOn)
            App.ins.focusUtils!!.sendActionFocus(Constant.TIME_CHANGE, "")
        }.subscribeOn(Schedulers.io()).subscribe(object : CompletableObserver {
            override fun onSubscribe(d: Disposable) {
                compositeDisposable.add(d)
            }

            override fun onComplete() {}
            override fun onError(e: Throwable) {}
        })
    }
}
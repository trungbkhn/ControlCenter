package com.tapbi.spark.controlcenter.ui.main.focus.time.edittime

import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.CompletableObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class EditTimeViewModel @Inject constructor() : BaseViewModel() {
    fun updateTimeFocus(
        name: String?,
        timeStart: Long,
        timeEnd: Long,
        monDay: Boolean,
        tueDay: Boolean,
        wedDay: Boolean,
        thuDay: Boolean,
        friDay: Boolean,
        satDay: Boolean,
        sunDay: Boolean,
        lastModify: Long,
        lastModifyOld: Long
    ) {
        Completable.fromRunnable {
            App.ins.timeRepository.updateTimeAutoFocus(
                name,
                timeStart,
                timeEnd,
                monDay,
                tueDay,
                wedDay,
                thuDay,
                friDay,
                satDay,
                sunDay,
                lastModify,
                lastModifyOld
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
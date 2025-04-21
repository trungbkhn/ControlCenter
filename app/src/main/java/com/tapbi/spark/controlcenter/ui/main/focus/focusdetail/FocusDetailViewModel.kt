package com.tapbi.spark.controlcenter.ui.main.focus.focusdetail

import android.content.Context
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.data.model.ItemTurnOn
import com.tapbi.spark.controlcenter.data.repository.TimeRepository
import com.tapbi.spark.controlcenter.eventbus.EventUpdateFocus
import com.tapbi.spark.controlcenter.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.CompletableObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@HiltViewModel
class FocusDetailViewModel @Inject constructor(private val timeRepository: TimeRepository) :
    BaseViewModel() {
    fun updateStartLocationAutomation(
        isStart: Boolean?,
        name: String?,
        location: String?,
        lastModify: Long?
    ) {
        Completable.fromRunnable {
            timeRepository.updateStartAutomationLocationFocus(
                isStart!!, name, location, lastModify!!
            )
        }.subscribeOn(Schedulers.io()).subscribe(object : CompletableObserver {
            override fun onSubscribe(d: Disposable) {
                compositeDisposable.add(d)
            }

            override fun onComplete() {}
            override fun onError(e: Throwable) {}
        })
    }

    fun updateStartAutomationAppFocus(
        isStart: Boolean?,
        name: String?,
        nameApp: String?,
        lastModify: Long?
    ) {
        Completable.fromRunnable {
            timeRepository.updateStartAutomationAppFocus(
                isStart,
                name,
                nameApp,
                lastModify
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

    fun updateStartAutomationTimeFocus(
        isStart: Boolean?,
        name: String?,
        lastModify: Long?,
        lastModifyOld: Long?
    ) {
        Completable.fromRunnable {
            timeRepository.updateStartAutomationTimeFocus(
                isStart,
                name,
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

    fun deleteItemLocationAutomation(location: String?, nameFocus: String?) {
        Completable.fromRunnable {
            timeRepository.deleteItemLocationAutomation(
                location,
                nameFocus
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

    fun deleteItemAppAutomation(packageName: String?, nameFocus: String?) {
        Completable.fromRunnable { timeRepository.deleteItemAppAutomation(packageName, nameFocus) }
            .subscribeOn(
                Schedulers.io()
            ).subscribe(object : CompletableObserver {
            override fun onSubscribe(d: Disposable) {
                compositeDisposable.add(d)
            }

            override fun onComplete() {}
            override fun onError(e: Throwable) {}
        })
    }

    fun deleteItemTimeAutomation(lastModify: Long?, nameFocus: String?) {
        Completable.fromRunnable { timeRepository.deleteItemTimeAutomation(lastModify, nameFocus) }
            .subscribeOn(
                Schedulers.io()
            ).subscribe(object : CompletableObserver {
            override fun onSubscribe(d: Disposable) {
                compositeDisposable.add(d)
            }

            override fun onComplete() {}
            override fun onError(e: Throwable) {}
        })
    }

    fun updateStartItemFocusIos(
        isStartAutoAppOpen: Boolean?,
        isStartCurrent: Boolean?,
        isStartAutoLocation: Boolean?,
        isStartAutoTime: Boolean?,
        name: String?
    ) {
        Completable.fromRunnable {
            App.ins.focusPresetRepository.updateStartItemFocusIos(
                isStartAutoAppOpen,
                isStartCurrent,
                isStartAutoLocation,
                isStartAutoTime,
                name
            )
        }
            .subscribeOn(Schedulers.io()).subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onComplete() {
                    EventBus.getDefault().post(EventUpdateFocus())
                }

                override fun onError(e: Throwable) {}
            })
    }

    fun updatePresetHand(name: String?) {
        Completable.fromRunnable { App.ins.focusPresetRepository.updatePresetHand(name) }
            .subscribeOn(
                Schedulers.io()
            ).subscribe(object : CompletableObserver {
            override fun onSubscribe(d: Disposable) {
                compositeDisposable.add(d)
            }

            override fun onComplete() {
                EventBus.getDefault().post(EventUpdateFocus())
            }

            override fun onError(e: Throwable) {}
        })
    }

    fun updatePresetFocus() {
        Completable.fromRunnable { App.ins.focusPresetRepository.updateStartHand() }.subscribeOn(
            Schedulers.io()
        ).subscribe(object : CompletableObserver {
            override fun onSubscribe(d: Disposable) {
                compositeDisposable.add(d)
            }

            override fun onComplete() {
                EventBus.getDefault().post(EventUpdateFocus())
            }

            override fun onError(e: Throwable) {}
        })
    }

    fun updateTimeRepeat(itemTurnOn: ItemTurnOn?) {
        Completable.fromRunnable { App.ins.focusPresetRepository.updateTimeRepeat(itemTurnOn) }
            .subscribeOn(
                Schedulers.io()
            ).subscribe(object : CompletableObserver {
            override fun onSubscribe(d: Disposable) {
                compositeDisposable.add(d)
            }
            override fun onComplete() {}
            override fun onError(e: Throwable) {}
        })
    }

    fun startHandFocus(context: Context?, name: String?) {
        Completable.fromRunnable { App.ins.focusPresetRepository.startHandFocus(context, name) }
            .subscribeOn(
                Schedulers.io()
            ).subscribe(object : CompletableObserver {
            override fun onSubscribe(d: Disposable) {
                compositeDisposable.add(d)
            }
            override fun onComplete() {}
            override fun onError(e: Throwable) {}
        })
    }

    fun deleteItemTurnOnByControl() {
        Completable.fromRunnable { App.ins.focusPresetRepository.deleteItemTurnOnByControl() }
            .subscribeOn(
                Schedulers.io()
            ).subscribe(object : CompletableObserver {
            override fun onSubscribe(d: Disposable) {
                compositeDisposable.add(d)
            }
            override fun onComplete() {}
            override fun onError(e: Throwable) {}
        })
    }
}
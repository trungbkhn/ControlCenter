package com.tapbi.spark.controlcenter.ui.choosemusic

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.tapbi.spark.controlcenter.data.repository.PackageRepository
import com.tapbi.spark.controlcenter.feature.controlios14.model.MusicPlayer
import com.tapbi.spark.controlcenter.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ChooseMusicPlayerViewModel @Inject constructor(private val packageRepository: PackageRepository) :
    BaseViewModel() {
    @JvmField
    var listAppMusicPlayerLiveData = MutableLiveData<ArrayList<MusicPlayer>>()
    fun getListAppMusicPlayer(context: Context?) {
        packageRepository.getListAppMusicPlayerRx(context)
            .subscribe(object : SingleObserver<ArrayList<MusicPlayer>> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(musicPlayers: ArrayList<MusicPlayer>) {
                    listAppMusicPlayerLiveData.postValue(musicPlayers)
                }

                override fun onError(e: Throwable) {
                }
            })
    }
}

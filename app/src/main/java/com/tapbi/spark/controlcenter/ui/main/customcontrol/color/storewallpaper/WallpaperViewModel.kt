package com.tapbi.spark.controlcenter.ui.main.customcontrol.color.storewallpaper

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tapbi.spark.controlcenter.common.models.Wallpaper
import com.tapbi.spark.controlcenter.data.repository.BackgroundRepository
import com.tapbi.spark.controlcenter.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WallpaperViewModel @Inject constructor(private var backgroundRepository: BackgroundRepository) :
    BaseViewModel() {
    var wallpaperLiveData: MutableLiveData<ArrayList<Wallpaper>> =
        MutableLiveData<ArrayList<Wallpaper>>()

    fun getListWallPaperAssets(context: Context) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            run {
                Timber.e(throwable)
            }
        }) {
            wallpaperLiveData.postValue(backgroundRepository.getListBackground(context))
        }
    }
}
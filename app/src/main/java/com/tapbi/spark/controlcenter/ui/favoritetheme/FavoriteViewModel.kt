package com.tapbi.spark.controlcenter.ui.favoritetheme

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.tapbi.spark.controlcenter.data.model.ThemeControlFavorite
import com.tapbi.spark.controlcenter.data.repository.ThemesRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class FavoriteViewModel @Inject constructor() :
    BaseViewModel() {

    var listThemesFavorite = MutableLiveData<MutableList<ThemeControlFavorite>>()
    fun getListThemesFavorite(context: Context) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler(fun(
            _: CoroutineContext,
            throwable: Throwable
        ) {
            run {
                Timber.e(throwable)
            }
        })) {
            listThemesFavorite.postValue(ThemesRepository.getAllListThemesFavorite(context))
        }
    }
}
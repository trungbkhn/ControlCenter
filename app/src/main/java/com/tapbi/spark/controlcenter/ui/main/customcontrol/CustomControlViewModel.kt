package com.tapbi.spark.controlcenter.ui.main.customcontrol

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.data.model.ItemControl
import com.tapbi.spark.controlcenter.data.model.ThemeControl
import com.tapbi.spark.controlcenter.data.repository.ThemesRepository
import com.tapbi.spark.controlcenter.ui.base.BaseViewModel
import com.tapbi.spark.controlcenter.utils.Utils.readItemControlFromJson2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class CustomControlViewModel @Inject constructor() : BaseViewModel() {

    var itemControlTheme: MutableLiveData<ItemControl> = MutableLiveData()
    fun getControlThemeDefaultById(context: Context, idCategory: Int) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler(fun(
            _: CoroutineContext,
            throwable: Throwable
        ) {
            run {
                Timber.e(throwable)
            }
        })) {
            itemControlTheme.postValue(
                ThemesRepository.getThemeControlDefault(
                    context,
                    idCategory
                )
            )
        }
    }
    fun getThemeEdit(context: Context,theme: ThemeControl) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler(fun(
            _: CoroutineContext,
            throwable: Throwable
        ) {
            run {
                Timber.e(throwable)
            }
        })) {
            readItemControlFromJson2(context,theme)?.let {
                itemControlTheme.postValue(it)
            }

        }
    }
}
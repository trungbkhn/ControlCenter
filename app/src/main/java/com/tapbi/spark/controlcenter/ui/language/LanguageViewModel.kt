package com.tapbi.spark.controlcenter.ui.language

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tapbi.spark.controlcenter.data.model.ListLanguage
import com.tapbi.spark.controlcenter.data.repository.LanguageRepository
import com.tapbi.spark.controlcenter.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class LanguageViewModel @Inject constructor(private val languageRepository: LanguageRepository) :
    BaseViewModel() {

    var listLanguage = MutableLiveData<ListLanguage>()

    fun getLanguage(context: Context) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler(fun(
            _: CoroutineContext,
            throwable: Throwable
        ) {
            run {
                Timber.e(throwable)
            }
        })) {
            listLanguage.postValue(languageRepository.getListLanguage(context))
        }
    }
}
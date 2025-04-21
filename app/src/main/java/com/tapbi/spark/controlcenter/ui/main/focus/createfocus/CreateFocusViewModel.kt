package com.tapbi.spark.controlcenter.ui.main.focus.createfocus

import com.tapbi.spark.controlcenter.data.repository.ColorRepository
import com.tapbi.spark.controlcenter.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateFocusViewModel @Inject constructor(private val colorRepository: ColorRepository) :
    BaseViewModel()
package com.tapbi.spark.controlcenter.ui.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.tapbi.spark.controlcenter.utils.LocaleUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class BaseFragment : Fragment(){
    override fun onCreate(savedInstanceState: Bundle?) {
        LocaleUtils.setCurrentResources(context)
        super.onCreate(savedInstanceState)
    }
}
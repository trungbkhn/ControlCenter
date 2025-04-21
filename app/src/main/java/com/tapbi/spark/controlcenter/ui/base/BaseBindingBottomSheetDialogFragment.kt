package com.tapbi.spark.controlcenter.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.tapbi.spark.controlcenter.ui.main.MainViewModel

abstract class BaseBindingBottomSheetDialogFragment<B : ViewDataBinding, T : BaseViewModel> :
    BaseBottomSheetDialogFragment() {

    lateinit var binding: B
    lateinit var viewModel: T
    lateinit var mainViewModel: MainViewModel
    protected abstract fun getViewModel(): Class<T>
    abstract val layoutId: Int
    protected abstract fun onCreatedView(view: View?, savedInstanceState: Bundle?)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get<T>(getViewModel())
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        onCreatedView(view, savedInstanceState)
    }
}
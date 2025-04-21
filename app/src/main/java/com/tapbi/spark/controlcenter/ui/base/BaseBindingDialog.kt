package com.tapbi.spark.controlcenter.ui.base

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import java.lang.Exception

@Suppress("DEPRECATION")
abstract class BaseBindingDialog<B : ViewDataBinding> : BaseDialog() {

    lateinit var binding: B
    override fun initData() {
        try {
            binding = DataBindingUtil.bind(getDialogView())!!
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            initVMData()
        }

    }

    inline fun <reified T> setPendingBindings(variableId: Int) {
        val t = T::class.java.newInstance()
        binding.setVariable(variableId, t)
        binding.executePendingBindings()
    }

    abstract fun initVMData()
}
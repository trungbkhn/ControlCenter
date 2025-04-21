package com.tapbi.spark.controlcenter.ui.base

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.tapbi.spark.controlcenter.ui.onboard.OnBoardActivity

abstract class BaseBindingActivity<B : ViewDataBinding, VM : BaseViewModel> : BaseActivity() {
    lateinit var binding: B
    lateinit var viewModel: VM
    abstract val layoutId: Int
    abstract fun getViewModel(): Class<VM>
    abstract fun setupView(savedInstanceState: Bundle?)
    abstract fun setupData()
    var  windowInsetsController : WindowInsetsControllerCompat?=null
    var isShowNavigationBar =true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
        viewModel = ViewModelProvider(this)[getViewModel()]
        windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().decorView)
        windowInsetsController?.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        setupView(savedInstanceState)
        if (!(this is OnBoardActivity)) {
            autoLoadsAds()
        }
        setupData()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun showHide(isShow: Boolean){
        if (Build.VERSION.SDK_INT> Build.VERSION_CODES.S){
            windowInsetsController?.apply {
                if(isShow){
                    show(WindowInsetsCompat.Type.navigationBars())
                }else{
                    hide(WindowInsetsCompat.Type.navigationBars())
                }
            }
        }else{
            if (isShow){
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_VISIBLE)
            }else{
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            }
        }
    }
}
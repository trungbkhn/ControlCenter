package com.tapbi.spark.controlcenter.ui.language

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.ironman.trueads.common.Common
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.adapter.LanguageAdapter
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.common.Constant.CHANGE_LANGUAGE
import com.tapbi.spark.controlcenter.data.model.Language
import com.tapbi.spark.controlcenter.databinding.ActivityLanguageBinding
import com.tapbi.spark.controlcenter.ui.base.BaseBindingActivity
import com.tapbi.spark.controlcenter.utils.LocaleUtils
import com.tapbi.spark.controlcenter.utils.gone
import com.tapbi.spark.controlcenter.utils.inv
import com.tapbi.spark.controlcenter.utils.show
import timber.log.Timber

class LanguageActivity : BaseBindingActivity<ActivityLanguageBinding,LanguageViewModel>(){
    private var clickedLanguageCode = LocaleUtils.codeLanguageCurrent
    private var languageAdapter: LanguageAdapter? = null
    var isFirstTime = false

    override val layoutId: Int
        get() = R.layout.activity_language

    override fun getViewModel(): Class<LanguageViewModel> {
        return LanguageViewModel::class.java
    }
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (isFirstTime){
                finish()
            } else {
                finish()
                overridePendingTransition(R.anim.from_left, R.anim.to_right)
            }
        }
    }

    override fun setupView(savedInstanceState: Bundle?) {
        onBackPressedDispatcher.addCallback(
            onBackPressedCallback
        )
        isFirstTime = intent?.getBooleanExtra(Constant.IS_FIRST_TIME_SHOW_LANGUAGE_ACTIVITY, false) ?: false
        initView()
        initAdapter()
        initListener()
        loadAds()
    }
    fun initView(){
        if (isFirstTime){
            binding.tvBack.inv()
        } else {
            binding.tvBack.show()
        }
    }
    fun  loadAds(){
        loadAdsNative(binding.flAds, Common.getMapIdAdmobApplovin(
            this,
            R.array.admob_native_id_language,
            R.array.applovin_id_native_language
        ) )
    }

    private fun initListener() {
        binding.tvBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.from_left, R.anim.to_right)
        }
        binding.icDone.setOnClickListener {
//            App.tinyDB.putBoolean(Constant.IS_SHOW_DIALOG_USER_MANUAL, false)
            if (isFirstTime){
                LocaleUtils.applyLocaleAndRestartFirstTime(this,clickedLanguageCode);
            } else {
                if (clickedLanguageCode != LocaleUtils.codeLanguageCurrent) {
                    LocaleUtils.applyLocaleAndRestart(this@LanguageActivity, clickedLanguageCode, action = CHANGE_LANGUAGE);
                } else {
                    finish()
                    overridePendingTransition(R.anim.from_left, R.anim.to_right)
                }
            }

        }
    }

    private fun initAdapter() {
        languageAdapter = LanguageAdapter()
        languageAdapter?.listener = object : LanguageAdapter.LanguageListener {
            override fun onClick(
                position: Int,
                language: Language
            ) {
                clickedLanguageCode = language.codeLocale
            }
        }

        binding.rvLanguage.adapter = languageAdapter
    }

    override fun setupData() {
        viewModel.getLanguage(this)
        viewModel.listLanguage.observe(this) {
            it?.let {
                Timber.e("NVQ listLanguage ${it.list.size}")
                languageAdapter?.setData(listLanguage = it.list, it.posLanguageSelected)
                binding.rvLanguage.scrollToPosition(it.posLanguageSelected)
            }
        }
    }

}
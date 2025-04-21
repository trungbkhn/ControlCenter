package com.tapbi.spark.controlcenter.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.lifecycle.Lifecycle
import com.applovin.mediation.MaxAd
import com.ironman.trueads.admob.ControlAds
import com.ironman.trueads.applovin.ControlAdsMAX
import com.ironman.trueads.common.RemoteConfigControl
import com.ironman.trueads.multiads.InitMultiAdsListener
import com.ironman.trueads.multiads.MultiAdsControl
import com.ironman.trueads.multiads.ShowOpenAdsListener
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.common.Constant.CHANGE_LANGUAGE
import com.tapbi.spark.controlcenter.common.Constant.IS_FAVORITE_SELECTED
import com.tapbi.spark.controlcenter.common.Constant.IS_ONBOARD_STARED
import com.tapbi.spark.controlcenter.data.local.SharedPreferenceHelper
import com.tapbi.spark.controlcenter.data.repository.ThemeHelper
import com.tapbi.spark.controlcenter.databinding.LayoutSplashActivityBinding
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614
import com.tapbi.spark.controlcenter.ui.base.BaseBindingActivity
import com.tapbi.spark.controlcenter.ui.favoritetheme.FavoriteThemeActivity
import com.tapbi.spark.controlcenter.ui.language.LanguageActivity
import com.tapbi.spark.controlcenter.ui.main.MainActivity
import com.tapbi.spark.controlcenter.ui.onboard.OnBoardActivity
import com.tapbi.spark.controlcenter.utils.LocaleUtils
import kotlin.math.abs

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseBindingActivity<LayoutSplashActivityBinding, SplashViewModel>() {
    private var idFocusSetting = -1
    private var canGoMain = false
    private val startTime = System.currentTimeMillis()
    private val handlerGoMain = Handler(Looper.getMainLooper())
    private var actionIntent: String? = ""
    private var isChangeLanguage = false;
    private val runnableGoMain = Runnable { goMain() }
    override val layoutId: Int
        get() = R.layout.layout_splash_activity

    override fun getViewModel(): Class<SplashViewModel> {
        return SplashViewModel::class.java
    }


    override fun setupView(savedInstanceState: Bundle?) {
        showHide(false)
        RemoteConfigControl.initRemoteConfig(App.ins)
        if (intent.flags != Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY or Intent.FLAG_ACTIVITY_NEW_TASK) {
            actionIntent = intent.action
            if (actionIntent?.isNotEmpty() == true) {
                if (actionIntent == Constant.SETTING_FOCUS) {
                    idFocusSetting = intent.getIntExtra(Constant.ID_FOCUS_SETTING, -1)
                }
            }
        }
        if (!isTaskRoot && intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intent.action != null && intent.action == Intent.ACTION_MAIN) {
            finish()
            return
        }
        intent?.action?.let { action ->
            if (action == CHANGE_LANGUAGE) {
                NotyControlCenterServicev614.getInstance()
                    ?.let { ins -> LocaleUtils.setCurrentResources(ins) }
                ThemeHelper.Companion.enableWindow();
            }
        }

//        binding.lottieSplash.addAnimatorListener(object : Animator.AnimatorListener{
//            override fun onAnimationStart(p0: Animator) {}
//
//            override fun onAnimationEnd(p0: Animator) {
//                initAds()
//            }
//
//            override fun onAnimationCancel(p0: Animator) {
//                initAds()
//            }
//
//            override fun onAnimationRepeat(p0: Animator) {}
//        })
//        binding.lottieSplash.playAnimation()
        initAds()

    }

    fun initAds() {
        App.ins.initAds(this, object : InitMultiAdsListener {
            override fun onInitAllAdsCompleted(networkAdsStateAll: Long, canNextScreen: Boolean) {
                binding.progressLoadingAds.visibility = View.VISIBLE
                autoLoadsAds()
                ControlAds.enableAutoRefresh = true
                ControlAdsMAX.enableAutoRefresh = true
            }

            override fun onLoadAdsOpen(networkAdsOpen: String?) {
                viewModel.eventLoadAdsOpen.postValue(true)
            }

        })
    }

    override fun setupData() {
        viewModel.eventLoadAdsOpen.observe(this) { aBoolean ->
            if (aBoolean != null && aBoolean as Boolean) {
                showAdsOpen()
            }
        }

    }


    fun showAdsOpen() {
        binding.progressLoadingAds.visibility = View.VISIBLE
        MultiAdsControl.loadAndShowOpenAds(this,
            true,
            object : ShowOpenAdsListener {
                override fun onAdRevenuePaid(ad: MaxAd?) {

                }

                override fun onAdsOpenClicked() {}
                override fun onLoadedAdsOpenApp() {}
                override fun onPrepareShowAdsOpenApp() {

                }

                override fun onLoadFailAdsOpenApp() {
                    checkGoToMain()
                }

                override fun onShowAdsOpenAppDismissed() {
                    checkGoToMain()
                }

                override fun onAdsOpenLoadedButNotShow() {
                    checkGoToMain()
                }
            })
    }

    private fun checkGoToMain() {
        binding.progressLoadingAds.visibility = View.GONE
        canGoMain = true
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            nextActivity()
        }
    }

    private fun nextActivity() {
        val deltaTime = abs(System.currentTimeMillis() - startTime)
        if (deltaTime >= 2500) {
            handlerGoMain.postDelayed(runnableGoMain, 200)
        } else {
            handlerGoMain.postDelayed(runnableGoMain, deltaTime)
        }
    }

    private fun goMain() {
        //Timber.e("goMain  " + this + canGoMain);
        if (canGoMain) {
            canGoMain = false
            App.ins.timeRequestPermission = System.currentTimeMillis()
            val isShowAppearance = App.tinyDB.getBoolean(
                Constant.KEY_SHOW_APPEARANCE_NEW,
                Constant.DEFAULT_VALUE_SHOW_APPEARANCE
            )
            val intent: Intent
//            App.ins.updateKeyControlPhase9Policy()
            if (SharedPreferenceHelper.getStringWithDefault(Constant.PREF_SETTING_LANGUAGE, "")
                    .isEmpty()
            ) {
                intent = Intent(this, LanguageActivity::class.java);
                intent.putExtra(Constant.IS_FIRST_TIME_SHOW_LANGUAGE_ACTIVITY, true)
            } else if (!SharedPreferenceHelper.getBoolean(IS_ONBOARD_STARED, false)) {
                intent = Intent(this, OnBoardActivity::class.java);
            } else if (!SharedPreferenceHelper.getBoolean(IS_FAVORITE_SELECTED, false)) {
                intent = Intent(this, FavoriteThemeActivity::class.java);
            } else {
                intent = Intent(this@SplashActivity, MainActivity::class.java)
                if (actionIntent?.isNotEmpty() == true) {
                    intent.setAction(actionIntent)
                    if (actionIntent == Constant.SETTING_FOCUS) {
                        intent.putExtra(Constant.ID_FOCUS_SETTING, idFocusSetting)
                    }
                }
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        handlerGoMain.removeCallbacks(runnableGoMain)
    }

    override fun onResume() {
        super.onResume()
        if (canGoMain) {
            Handler(Looper.getMainLooper()).post {
                nextActivity()
            }
        }
    }

}

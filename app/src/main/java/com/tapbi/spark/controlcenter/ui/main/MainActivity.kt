package com.tapbi.spark.controlcenter.ui.main

import android.Manifest
import android.animation.ValueAnimator
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.ironman.trueads.admob.ControlAds
import com.ironman.trueads.admob.interstital.InterstitialAdAdmob
import com.ironman.trueads.admob.open.AppOpenAdAdmob
import com.ironman.trueads.applovin.ControlAdsMAX
import com.ironman.trueads.internetdetect.networkchecker.NetworkLiveData
import com.ironman.trueads.internetdetect.networkchecker.NetworkState
import com.ironman.trueads.ironsource.InterstitialAdIronSource
import com.ironman.trueads.multiads.InitMultiAdsListener
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.common.Constant.REQUEST_CODE_READ_PHONE_STATE
import com.tapbi.spark.controlcenter.common.models.ScaleViewMainEvent
import com.tapbi.spark.controlcenter.databinding.ActivityMainBinding
import com.tapbi.spark.controlcenter.eventbus.EventCheckControlState
import com.tapbi.spark.controlcenter.eventbus.EventCheckOpen
import com.tapbi.spark.controlcenter.eventbus.EventHideNav
import com.tapbi.spark.controlcenter.eventbus.EventOpen
import com.tapbi.spark.controlcenter.eventbus.EventShowNav
import com.tapbi.spark.controlcenter.ui.base.BaseBindingActivity
import com.tapbi.spark.controlcenter.ui.dialog.DialogRequestReadPhone
import com.tapbi.spark.controlcenter.ui.dialog.requestpermission.RequestPermissionBottomSheet
import com.tapbi.spark.controlcenter.utils.Analytics
import com.tapbi.spark.controlcenter.utils.DensityUtils
import com.tapbi.spark.controlcenter.utils.MethodUtils
import com.tapbi.spark.controlcenter.utils.PermissionUtils.checkAllPermissionWithOutReadPhoneState
import com.tapbi.spark.controlcenter.utils.PermissionUtils.checkPermissionService
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber

@Suppress("DEPRECATION")
class MainActivity : BaseBindingActivity<ActivityMainBinding, MainViewModel>() {
    lateinit var navControllerMain: NavController
    private var navHostFragmentMain: NavHostFragment? = null

    @JvmField
    var actionIntent: String? = ""


    @JvmField
    var idFocusSetting = -1
    private var countShowAdsCollapsible: Long = 0
    private var permissonActivityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback<ActivityResult> { result: ActivityResult ->
                if (result.resultCode == RESULT_OK) {
                    // There are no request codes
                    val data = result.data
                }
            })


    override val layoutId: Int
        get() = R.layout.activity_main

    override fun getViewModel(): Class<MainViewModel> {
        return MainViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.decorView.importantForAutofill =
                View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS;
        }
        EventBus.getDefault().register(this)
        Analytics.init(this)
        MethodUtils.intentToCheckPermission(this)
        ControlAds.configDelayShowAdsInterAdmob(App.ins)

        Log.d("duongcvcc", "onCreate: ")
    }

    //    @SuppressLint("WrongConstant")
    //    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun setupView(savedInstanceState: Bundle?) {
        navHostFragmentMain =
            supportFragmentManager.findFragmentById(R.id.fragment) as? NavHostFragment?
        if (navHostFragmentMain != null) {
            navControllerMain = navHostFragmentMain!!.navController

        }
//        if (NotyControlCenterServicev614.getInstance() != null) {
//            if (NotyControlCenterServicev614.getInstance().isChangeLocale) {
////                NotyControlCenterServicev614.getInstance()
////                    .setNewLocale(Locale.getDefault().language)
////                NotyControlCenterServicev614.getInstance().isChangeLocale = false
//            } else if (App.widthHeightScreenCurrent.toString() != WidthHeightScreen(this).toString()) {
//                setUpWidthHeightAndBitMapTransparent(this)
//                NotyControlCenterServicev614.getInstance()
//                    .newConfig(Configuration.ORIENTATION_PORTRAIT)
//            }
//        }
        if (intent.flags != Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY or Intent.FLAG_ACTIVITY_NEW_TASK) {
            actionIntent = intent.action
            if (actionIntent?.isNotEmpty() == true) {
                if (actionIntent == Constant.SETTING_FOCUS) {
                    idFocusSetting = intent.getIntExtra(Constant.ID_FOCUS_SETTING, -1)
                }
            }
        }

//        binding.btnCrash.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                throw new RuntimeException("test crash");
//            }
//        });
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return !isDispatchTouchEvent || super.dispatchTouchEvent(ev)
//        return super.dispatchTouchEvent(ev)
    }

    override fun setupData() {
        //even scale view main
        viewModel.scaleViewMainLiveEvent.observe(this) { o: Any? ->
            if (o is ScaleViewMainEvent) {
                if (o.isZoomIn) {
                    scaleZoomInFragmentAuto()
                } else if (o.isZoomOut) {
                    scaleZoomOutFragmentAuto()
                } else {
                    setScale(o.scale)
                }
            }
        }
        viewModel.getListFocusAdded()
        viewModel.getListThemes(this)
        viewModel.getThemesDatabase()
//        viewModel.getAllThemeControls()
        NetworkLiveData.get().observe(this) { networkState: NetworkState? ->
            if (networkState != null && networkState.isConnected) {
                if (!ControlAds.admobInitialized) {
                    initAdAndLoadAds()
                }
            }
        }
        viewModel.eventDismissLoadingAds.observe(this) { aBoolean: Any? ->
            if (aBoolean != null && aBoolean as Boolean) {
                setVisibilityProgressAds(View.GONE)
                viewModel.eventDismissLoadingAds.value = false
            }
        }
        viewModel.liveDataShowDialogPermissionNoty.observe(this) {
            if (it) {
                if (!MethodUtils().areNotificationsEnabled(this) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        REQUEST_CODE_NOTIFICATION
                    )
                }
                viewModel.liveDataShowDialogPermissionNoty.value = false
            }
        }
    }

    private fun scaleZoomInFragmentAuto() {
        setRadius(0f)
        val scale = ValueAnimator.ofFloat(binding.cvMain.scaleX, 1f)
        scale.addUpdateListener { animation: ValueAnimator ->
            val scale1 = animation.animatedValue.toString().toFloat()
            setScale(scale1)
        }
        scale.start()
    }

    private fun scaleZoomOutFragmentAuto() {
        setRadius(10f)
        val scale = ValueAnimator.ofFloat(1f, 0.9f)
        scale.addUpdateListener { animation: ValueAnimator ->
            val scale1 = animation.animatedValue.toString().toFloat()
            setScale(scale1)
        }
        scale.start()
    }

    private fun setScale(scale: Float) {
        binding.cvMain.scaleX = scale
        binding.cvMain.scaleY = scale
    }

    private fun setRadius(radius: Float?) {
        binding.cvMain.radius = DensityUtils.pxFromDp(this, radius!!)
    }

    override fun onStart() {
        super.onStart()
        App.isStartActivity = true
    }

    override fun onStop() {
        super.onStop()
        App.isStartActivity = false
    }

    override fun onPause() {
        super.onPause()
        InterstitialAdIronSource.onPauseInterstitialAdIronSource(this)
    }

    override fun onResume() {
        super.onResume()
        InterstitialAdIronSource.resumeInterstitialAdIronSource(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        MethodUtils.ICON_CACHE.clear()
    }

    public override fun setColorNavigation(color: Int) {
        try {
            window.navigationBarColor = resources.getColor(color)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val decorView = window.decorView
                var flags = decorView.systemUiVisibility
                flags = if (MethodUtils.isColorDark(color)) {
                    flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                } else {
                    flags and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
                }
                decorView.systemUiVisibility = flags
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun clearBackStack() {
        navControllerMain.popBackStack(R.id.homeFragment, false)
    }

    @Subscribe
    fun onEventOpen(eventOpen: EventOpen) {
        actionIntent = eventOpen.action
        if (actionIntent == Constant.SETTING_FOCUS) {
            idFocusSetting = eventOpen.idFocus
        }
        clearBackStack()
        EventBus.getDefault().post(EventCheckOpen())
    }

    fun navigate(id: Int, current: Int) {
        if (navControllerMain.currentDestination != null && navControllerMain.currentDestination!!.id == current) {
            navControllerMain.navigate(id)
        }
    }

    fun navigateWithBundler(id: Int, current: Int, bundle: Bundle?) {
        if (navControllerMain.currentDestination != null && navControllerMain.currentDestination!!.id == current) {
            navControllerMain.navigate(id, bundle)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun initAdAndLoadAds() {
        if (!ControlAds.admobInitialized) {
            App.ins.initAds(this, object : InitMultiAdsListener {
                override fun onInitAllAdsCompleted(
                    networkAdsStateAll: Long,
                    canNextScreen: Boolean
                ) {
                    ControlAds.enableAutoRefresh = true
                    ControlAdsMAX.enableAutoRefresh = true
                    autoLoadsAds()
                    InterstitialAdAdmob.loadInterstitialAdmob(App.ins)
                    AppOpenAdAdmob.getInstance(App.ins).enableShowByEvent = true
                }

                override fun onLoadAdsOpen(networkAdsOpen: String?) {

                }

            })
        }
    }

    @Subscribe
    fun onEventHideNav(eventHideNav: EventHideNav) {
        Timber.e("NVQ onEventHideNav++++++++++")
        if (eventHideNav.isHide) {
            showHide(false)
            eventHideNav.isHide = false
        }
    }

    @Subscribe
    fun onEventShowNav(eventShowNav: EventShowNav) {
        Timber.e("NVQ onEventShowNav++++++++++")
        if (eventShowNav.isShow) {
            showHide(true)
            eventShowNav.isShow = false
        }
    }

    //    @Override
    //    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    //        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    //        PermissionUtils.INSTANCE.onRequestPermissionsResult(this, requestCode, permissions, grantResults, new PermissionUtils.PermissionsCallBack() {
    //            @Override
    //            public void permissionsGranted() {
    //                checkPermissionContact();
    //                new Handler(Looper.getMainLooper()).postDelayed(() ->
    //                        navigate(R.id.action_focusDetailFragment_to_allowPeopleFragment, R.id.focusDetailFragment), 200);
    //            }
    //
    //            @Override
    //            public void permissionsDenied() {
    //
    //            }
    //        });
    //    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        try {
            if (requestCode == REQUEST_CODE_READ_PHONE_STATE) {
                if (checkPermissionService()) {
                    EventBus.getDefault().post(EventCheckControlState())
                } else {
                    if (checkAllPermissionWithOutReadPhoneState()) {
                        showDialogPermissionOpenSettings()
                    }

                }
            }
        } catch (e: StackOverflowError) {
            Timber.e("Duongcv " + e.message);
        }
    }

    fun showDialogPermissionOpenSettings() {
        val dialog = DialogRequestReadPhone()
        val existingFragment =
            supportFragmentManager.findFragmentByTag(DialogRequestReadPhone::class.java.simpleName)

        existingFragment?.let {
            if (it.isAdded && !it.isStateSaved) {
                (it as DialogRequestReadPhone).dismissAllowingStateLoss()
            }
        }
        if (!supportFragmentManager.isStateSaved) {
            dialog.show(supportFragmentManager, DialogRequestReadPhone::class.java.simpleName)
        }
    }

    fun setVisibilityProgressAds(visibilityProgressAds: Int) {
        binding.bgLoadingAds.visibility = visibilityProgressAds
    }

    fun intentPermissionActivity() {
        val dialogRequestPermission = RequestPermissionBottomSheet()
        val existingFragment =
            supportFragmentManager.findFragmentByTag(RequestPermissionBottomSheet::class.java.simpleName)
        existingFragment?.let {
            if (it.isAdded && !it.isStateSaved) {
                (it as RequestPermissionBottomSheet).dismissAllowingStateLoss()
            }
        }
        if (!supportFragmentManager.isStateSaved) {
            dialogRequestPermission.show(
                supportFragmentManager,
                RequestPermissionBottomSheet::class.java.simpleName
            )
        }
    }

    companion object {
        @JvmField
        var isDispatchTouchEvent = true
        private const val REQUEST_CODE_NOTIFICATION = 2

        @JvmStatic
        fun isDispatchTouchEvent() {
            isDispatchTouchEvent = false
            Handler(Looper.getMainLooper()).postDelayed(
                { isDispatchTouchEvent = true },
                Constant.TIME_DELAYED_DISPATCH_TOUCH_EVENT.toLong()
            )
        }

        @JvmStatic
        fun isDispatchTouchEvent(time: Long) {
            isDispatchTouchEvent = false
            Handler(Looper.getMainLooper()).postDelayed({ isDispatchTouchEvent = true }, time)
        }

        @JvmStatic
        val isDispatchLongTouchEvent: Unit
            get() {
                isDispatchTouchEvent = false
                Handler(Looper.getMainLooper()).postDelayed(
                    { isDispatchTouchEvent = true },
                    Constant.TIME_DELAYED_DISPATCH_LONG_TOUCH_EVENT.toLong()
                )
            }
    }
}

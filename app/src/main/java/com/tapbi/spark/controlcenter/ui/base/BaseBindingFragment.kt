package com.tapbi.spark.controlcenter.ui.base

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.ads.nativetemplates.OnDecorationAds
import com.google.android.ads.nativetemplates.TemplateViewMultiAds
import com.ironman.trueads.common.Common
import com.ironman.trueads.multiads.InterstitialAdsLiteListener
import com.ironman.trueads.multiads.MultiAdsControl
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.data.model.ThemeControl
import com.tapbi.spark.controlcenter.data.repository.ThemeHelper
import com.tapbi.spark.controlcenter.data.repository.ThemesRepository
import com.tapbi.spark.controlcenter.data.repository.ThemesRepository.onApplyTheme
import com.tapbi.spark.controlcenter.eventbus.EventSelectThemes
import com.tapbi.spark.controlcenter.ui.dialog.DialogPreviewControl
import com.tapbi.spark.controlcenter.ui.dialog.requestpermission.RequestPermissionBottomSheet
import com.tapbi.spark.controlcenter.ui.main.MainViewModel
import com.tapbi.spark.controlcenter.utils.AccessUtils.isAccessibilityServiceEnabled
import com.tapbi.spark.controlcenter.utils.MethodUtils
import com.tapbi.spark.controlcenter.utils.PermissionUtils.checkPermissionReadPhoneState
import com.tapbi.spark.controlcenter.utils.SettingUtils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

abstract class BaseBindingFragment<B : ViewDataBinding, T : BaseViewModel> : BaseFragment() {
    lateinit var binding: B
    lateinit var viewModel: T
    lateinit var mainViewModel: MainViewModel
    private var toast: Toast? = null
    private var dialogPreviewControl: DialogPreviewControl? = null
    protected abstract fun getViewModel(): Class<T>
    abstract val layoutId: Int
    protected abstract fun onCreatedView(view: View?, savedInstanceState: Bundle?)
    protected abstract fun onPermissionGranted()
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
        viewModel = ViewModelProvider(this)[getViewModel()]
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        if (savedInstanceState != null) {
            (childFragmentManager.findFragmentByTag(RequestPermissionBottomSheet::class.java.simpleName) as RequestPermissionBottomSheet?)?.dismissAllowingStateLoss()
        }
        onCreatedView(view, savedInstanceState)
    }

    protected fun toastText(idText: Int) {
        if (context != null) {
            if (toast != null) {
                toast!!.cancel()
            }
            toast = Toast.makeText(context, getText(idText), Toast.LENGTH_SHORT)
            toast?.show()
        }
    }

    protected fun toastText(content: String?) {
        if (context != null) {
            if (toast != null) {
                toast!!.cancel()
            }
            toast = Toast.makeText(context, content, Toast.LENGTH_SHORT)
            toast?.show()
        }
    }

    protected fun displayToastAboveButton(v: View, messageId: Int) {
        var xOffset = 0
        var yOffset = 0
        val gvr = Rect()
        val parent = v.parent as View
        val parentHeight = parent.height
        if (v.getGlobalVisibleRect(gvr)) {
            val root = v.rootView
            val halfWidth = root.right / 2
            val halfHeight = root.bottom / 2
            val parentCenterX = (gvr.right - gvr.left) / 2 + gvr.left
            val parentCenterY = (gvr.bottom - gvr.top) / 2 + gvr.top
            yOffset = if (parentCenterY <= halfHeight) {
                -(halfHeight - parentCenterY) - parentHeight
            } else {
                parentCenterY - halfHeight - parentHeight
            }
            if (parentCenterX < halfWidth) {
                xOffset = -(halfWidth - parentCenterX)
            }
            if (parentCenterX >= halfWidth) {
                xOffset = parentCenterX - halfWidth
            }
        }
        val toast = Toast.makeText(activity, messageId, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, xOffset, yOffset)
        toast.show()
    }

    protected fun setUpPaddingStatusBar(layoutParent: ConstraintLayout) {
        if (context == null) {
            return
        }
        MethodUtils.hideKeyboard(requireActivity())
        layoutParent.setPadding(0, App.statusBarHeight, 0, 0)
    }

    protected fun addFragment(fragment: Fragment, withShowAds: Boolean) {
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        if (withShowAds) {
            fragmentTransaction.setCustomAnimations(
                R.anim.frag_show,
                R.anim.frag_hide,
                R.anim.frag_show,
                R.anim.frag_hide
            )
        } else {
            fragmentTransaction.setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
        }
        fragmentTransaction.add(R.id.flMain, fragment, fragment.javaClass.toString())
        fragmentTransaction.addToBackStack(fragment.javaClass.toString())
        fragmentTransaction.commit()
    }

    val activeFragment: Fragment?
        get() {
            if (parentFragmentManager.backStackEntryCount == 0) {
                return null
            }
            val tag =
                parentFragmentManager.getBackStackEntryAt(parentFragmentManager.backStackEntryCount - 1).name
            return parentFragmentManager.findFragmentByTag(tag)
        }

    @SuppressLint("ClickableViewAccessibility")
    protected fun hideKeyBoardScrollRV(recyclerView: RecyclerView) {
        recyclerView.setOnTouchListener { _: View?, _: MotionEvent? ->
            MethodUtils.hideSoftKeyboard(requireActivity())
            false
        }
    }

    //    public void requestPermissionPhone(ActivityResultLauncher<String[]> requestMultiplePermissions) {
    //        List<String> requiredPermissions = new ArrayList<>();
    //        requiredPermissions.add(Manifest.permission.CALL_PHONE);
    //        requiredPermissions.add(Manifest.permission.READ_PHONE_STATE);
    //        requiredPermissions.add(Manifest.permission.READ_CALL_LOG);
    //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
    //            requiredPermissions.add(Manifest.permission.ANSWER_PHONE_CALLS);
    //        }
    //        List<String> missingPermissions = new ArrayList<>();
    //        for (String permission : requiredPermissions) {
    //            if (ContextCompat.checkSelfPermission(requireContext(), permission)
    //                    != PackageManager.PERMISSION_GRANTED
    //            ) {
    //                missingPermissions.add(permission);
    //            }
    //        }
    //        if (!missingPermissions.isEmpty()) {
    //            requestMultiplePermissions.launch(missingPermissions.toArray(new String[missingPermissions.size()]));
    //        }
    //    }
    protected open fun showAdsFull(positionAds: String) {
        MultiAdsControl.showInterstitialLite(
            requireActivity(),
            positionAds,
            false,
            object : InterstitialAdsLiteListener {


                override fun onInterstitialAdsNextScreen(adsType: Int) {
                    nextAfterFullScreen()
                }

                override fun onInterstitialAdsShowFully(adsType: Int) {

                }

                override fun onPrepareShowInterstitialAds(adsType: Int) {

                }

            })
    }


    protected open fun nextAfterFullScreen() {
    }

    protected fun navigate(idFragmentCurrent: Int, action: Int) {
        if (currentDestination != null && currentDestination!!.id == idFragmentCurrent) {
            findNavController().navigate(action, null)
        }
    }

    protected fun navigate(idFragmentCurrent: Int, action: Int, bundle: Bundle?) {
        if (currentDestination != null && currentDestination!!.id == idFragmentCurrent) {
            findNavController().navigate(action, bundle)
        }
    }


    fun showDialogPreviewControl(
        context: Context,
        themeControl: ThemeControl,
        isMyCustomizationControl: Boolean = false,
        onThemeApplied: (() -> Unit)? = null
    ) {
        dialogPreviewControl = DialogPreviewControl.newInstance(themeControl,
            object : DialogPreviewControl.ClickListener {
                override fun onClickApply(theme: ThemeControl, isMyCustomizationControl: Boolean) {
                    CoroutineScope(Dispatchers.IO + CoroutineExceptionHandler(fun(
                        _: CoroutineContext,
                        throwable: Throwable
                    ) {
                        run {
                            Timber.e(throwable)
                        }
                    })).launch {
                        onApplyTheme(theme,isMyCustomizationControl)
                    }
                    showClickInstructions(onThemeApplied)
                }

                override fun onClickEdit(theme: ThemeControl?, isMyCustomizationControl: Boolean) {
                    theme?.let {
                        val bundle = Bundle()
                        bundle.putBoolean(Constant.KEY_EDIT_THEME, true)
                        bundle.putLong(Constant.ID_THEME_CONTROL,it.id)
                        bundle.putInt(Constant.ID_CATEGORY,it.idCategory)
                        navigate(R.id.homeMainFragment, R.id.customizeControlFragment, bundle)
                    }

                }

                override fun showToast(message: String) {
                    toastText(message)
                    showClickInstructions(onThemeApplied)
                }
            }, { _, templateView ->
                loadAdsNative(
                    templateView, Common.getMapIdAdmobApplovin(
                        requireActivity(),
                        R.array.admob_native_id_preview_my_theme,
                        R.array.applovin_id_native_preview_my_theme
                    )
                )
            }, isMyCustomizationControl
        )

        dialogPreviewControl?.let {
            if (!childFragmentManager.isStateSaved) {
                it.showDialog(
                    childFragmentManager,
                    "dialogPreviewControl"
                )
            }

        }
    }

    private fun showClickInstructions(onThemeApplied: (() -> Unit)?) {
        val isChecked: Boolean = if (checkPermissionService()) {
            App.tinyDB.getInt(
                Constant.IS_ENABLE,
                Constant.IS_DISABLE
            ) == Constant.DEFAULT_IS_ENABLE
        } else {
            false
        }
        if (!isChecked) {
            onThemeApplied?.invoke()
        }
    }

    fun dismissDialogPreviewControl() {
        if (!childFragmentManager.isStateSaved && dialogPreviewControl != null && dialogPreviewControl!!.isAdded) {
            dialogPreviewControl!!.dismissAllowingStateLoss()
            dialogPreviewControl = null
        }
    }

    private fun findNavController(): NavController {
        return NavHostFragment.findNavController(this)
    }

    private val currentDestination: NavDestination?
        get() = findNavController().currentDestination

    protected fun loadAdsNative(
        templateView: TemplateViewMultiAds,
        mapIds: HashMap<String, String>,
        colorTvHeadline: Int = Color.WHITE,
        colorTvBody: Int = Color.WHITE
    ) {
        MultiAdsControl.showNativeAd(
            requireActivity() as AppCompatActivity,
            mapIds,
            templateView,
            true,
            null, null, object : OnDecorationAds {
                override fun onDecoration(network: String?) {
                    templateView.getNativeAdView(network)?.apply {
                        setBackgroundColor(
                            ContextCompat.getColor(
                                requireActivity(),
                                R.color.color_F3F3F3
                            )
                        )
                    }
//                    templateView.getTvHeadline(network)?.apply {
//                        (this as TextView).setTextColor(colorTvHeadline)
//                    }
//                    templateView.getTvBody(network)?.apply {
//                        (this as TextView).setTextColor(colorTvBody)
//                    }
                }
            })
    }

    protected open fun loadAdsBanner(
        bannerContainer: FrameLayout, unitId: HashMap<String, String>, nativeId: HashMap<String, String>, isCollapsible: Boolean
    ) {
        MultiAdsControl.setupAdsBanner(
            requireActivity(), bannerContainer,
            unitId, nativeId,true, isCollapsible, null
        )
    }

    protected fun checkPermissionService(): Boolean {
        return if (context == null) {
            false
        } else SettingUtils.checkPermissionNotificationListener(context) && SettingUtils.checkPermissionOverlay(
            context
        ) && isAccessibilityServiceEnabled(requireContext()) && checkPermissionReadPhoneState(
            requireContext()
        )
    }

    protected fun checkPermission(): Boolean {
        return if (context == null) {
            false
        } else SettingUtils.checkPermissionNotificationListener(context) && SettingUtils.checkPermissionOverlay(
            context
        ) && checkPermissionReadPhoneState(requireContext())
    }
}
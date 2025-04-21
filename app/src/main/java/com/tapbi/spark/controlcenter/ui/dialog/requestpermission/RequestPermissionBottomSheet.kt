package com.tapbi.spark.controlcenter.ui.dialog.requestpermission

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ironman.trueads.common.Common
import com.tapbi.spark.controlcenter.App.Companion.tinyDB
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.common.Constant.ACCESSIBILITY_PERMISSION
import com.tapbi.spark.controlcenter.common.Constant.FINISH
import com.tapbi.spark.controlcenter.common.Constant.NOTIFICATION_PERMISSION
import com.tapbi.spark.controlcenter.common.Constant.OVERDRAW_PERMISSION
import com.tapbi.spark.controlcenter.common.Constant.REQUEST_CODE_READ_PHONE_STATE
import com.tapbi.spark.controlcenter.common.Constant.TYPE_ADS_1
import com.tapbi.spark.controlcenter.common.Constant.TYPE_ADS_2
import com.tapbi.spark.controlcenter.data.repository.ThemeHelper
import com.tapbi.spark.controlcenter.eventbus.EventCheckControlState
import com.tapbi.spark.controlcenter.ui.dialog.BottomSheetAccessibility
import com.tapbi.spark.controlcenter.utils.AccessUtils.isAccessibilityServiceEnabled
import com.tapbi.spark.controlcenter.utils.PermissionUtils
import com.tapbi.spark.controlcenter.utils.PermissionUtils.checkPermissionService
import com.tapbi.spark.controlcenter.utils.RemoteConfigHelper
import com.tapbi.spark.controlcenter.utils.SettingUtils.checkPermissionNotificationListener
import com.tapbi.spark.controlcenter.utils.SettingUtils.checkPermissionOverlay
import com.tapbi.spark.controlcenter.utils.Utils.initTextContentPermission
import com.tapbi.spark.controlcenter.utils.Utils.setBackgroundTintSelect
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

class RequestPermissionBottomSheet : BaseRequestPermissionBottomSheet() {
    private var currentType = ""
    private var bottomSheetAccessibility: BottomSheetAccessibility? = null




    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        initView()
        choosePermission(ACCESSIBILITY_PERMISSION)
        initListener()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            ViewHelper.setUpWrapHeight(it as BottomSheetDialog)
        }
        return dialog
    }


    private fun initView() {
        tvTitle.text = getString(
            R.string.activate_s,
            getString(R.string.app_name)
        )
        loadAdsNative(flAds, Common.getMapIdAdmobApplovin(
            requireContext(),
            R.array.admob_native_id_request_permission,
            R.array.applovin_id_native_request_permission
        ) )
    }

    override fun onResume() {
        super.onResume()
        initCheckPermission(tvAccessibility, tvOverDraw, tvNotification)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.NewCustomBottomSheetDialogTheme)
    }
    private fun initListener() {
        tvAccessibility.setOnClickListener {
            ViewHelper.preventTwoClick(it, 500)
            currentType = ACCESSIBILITY_PERMISSION
            requestPerMission()
        }
        tvOverDraw.setOnClickListener {
            ViewHelper.preventTwoClick(it, 500)
            currentType = OVERDRAW_PERMISSION
            requestPerMission()

        }
        tvNotification.setOnClickListener {
            ViewHelper.preventTwoClick(it, 500)
            currentType = NOTIFICATION_PERMISSION
            requestPerMission()
        }
    }
    private fun choosePermission(type: String) {
        currentType = type
        when (currentType) {
            ACCESSIBILITY_PERMISSION -> {
                setBackgroundTintSelect(requireContext(), tvAccessibility, true)
                setBackgroundTintSelect(requireContext(), tvOverDraw, false)
                setBackgroundTintSelect(requireContext(), tvNotification, false)
                initTextContentPermission(
                    requireContext(),
                    tvContent,
                    R.string.enable_in_your_installed_apps_in_accessibility_settings
                )
            }

            OVERDRAW_PERMISSION -> {
                setBackgroundTintSelect(requireContext(), tvAccessibility, false)
                setBackgroundTintSelect(requireContext(), tvOverDraw, true)
                setBackgroundTintSelect(requireContext(), tvNotification, false)
                initTextContentPermission(
                    requireContext(), tvContent, R.string.enable_s_in_appear_on_top_settings
                )
            }

            NOTIFICATION_PERMISSION -> {
                setBackgroundTintSelect(requireContext(), tvAccessibility, false)
                setBackgroundTintSelect(requireContext(), tvOverDraw, false)
                setBackgroundTintSelect(requireContext(), tvNotification, true)
                initTextContentPermission(
                    requireContext(), tvContent, R.string.enable_in_notification_access
                )
            }
        }
    }
    @SuppressLint("CommitTransaction")
    private fun requestPerMission() {
        Timber.e("NVQ requestPerMission++++++++++")
        when (currentType) {
            ACCESSIBILITY_PERMISSION -> {
                if (!isAccessibilityServiceEnabled(requireContext())) {
                    if (!childFragmentManager.isStateSaved) {
                        if (bottomSheetAccessibility == null) {
                            bottomSheetAccessibility = BottomSheetAccessibility()
                        }
                        bottomSheetAccessibility?.let {
                            if (!it.isAdded && !it.isVisible) {
                                it.show(
                                    childFragmentManager.beginTransaction().remove(it),
                                    BottomSheetAccessibility::class.simpleName
                                )
                            }
                        }
                    }
                } else {
                    initCheckPermission(tvAccessibility, tvOverDraw, tvNotification)
                }
            }

            OVERDRAW_PERMISSION -> {
                if (!checkPermissionOverlay(requireContext())) {
                    PermissionUtils.requestPermissionOverlay(requireContext())
                } else {
                    initCheckPermission(tvAccessibility, tvOverDraw, tvNotification)
                }
            }

            NOTIFICATION_PERMISSION -> {
                if (!checkPermissionNotificationListener(requireContext())) {
                    PermissionUtils.requestPermissionNotifyListener(requireContext())
                } else {
                    initCheckPermission(tvAccessibility, tvOverDraw, tvNotification)
                }
            }
        }
    }
    private fun initCheckPermission(vararg textViews: TextView) {
        context?.let { context ->

            currentType = when {
                !isAccessibilityServiceEnabled(context) -> ACCESSIBILITY_PERMISSION
                !checkPermissionOverlay(context) -> OVERDRAW_PERMISSION
                !checkPermissionNotificationListener(context) -> NOTIFICATION_PERMISSION
                else -> FINISH
            }
            Timber.e("NVQ initCheckPermission $currentType")
            when (currentType) {
                ACCESSIBILITY_PERMISSION -> {
                    choosePermission(ACCESSIBILITY_PERMISSION)
                }

                OVERDRAW_PERMISSION -> {
                    choosePermission(OVERDRAW_PERMISSION)
                }

                NOTIFICATION_PERMISSION -> {
                    choosePermission(NOTIFICATION_PERMISSION)
                }

                else -> {
                    startControl()
                }

            }
        }
    }
    private fun startControl() {
        try {
            dismissAllowingStateLoss()
            if (checkPermissionService()){
                tinyDB.putInt(Constant.IS_ENABLE, Constant.DEFAULT_IS_ENABLE)
                ThemeHelper.enableWindow()
                EventBus.getDefault().post(EventCheckControlState(RequestPermissionBottomSheet::class.simpleName.toString()))
            } else {
                mainActivity?.let {
                    ActivityCompat.requestPermissions(
                        it,
                        arrayOf(Manifest.permission.READ_PHONE_STATE),
                        REQUEST_CODE_READ_PHONE_STATE
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
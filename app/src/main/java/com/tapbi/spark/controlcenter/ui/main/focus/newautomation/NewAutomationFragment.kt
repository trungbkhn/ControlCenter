package com.tapbi.spark.controlcenter.ui.main.focus.newautomation

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.OnBackPressedCallback
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.data.model.FocusIOS
import com.tapbi.spark.controlcenter.databinding.FragmentNewAutomationBinding
import com.tapbi.spark.controlcenter.ui.base.BaseBindingFragment
import com.tapbi.spark.controlcenter.ui.dialog.DialogPermissionUsage
import com.tapbi.spark.controlcenter.ui.main.MainActivity
import com.tapbi.spark.controlcenter.utils.PermissionUtils.isAccessGranted
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper

class NewAutomationFragment :
    BaseBindingFragment<FragmentNewAutomationBinding, NewAutomationViewModel>() {
    private var ios: FocusIOS? = null
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                isEnabled = false
                //            requireActivity().onBackPressed();
                mainViewModel.itemFocusDetail.postValue(ios)
                (requireActivity() as MainActivity).navControllerMain.popBackStack(
                    R.id.newAutomationFragment,
                    true
                )
            }
        }

    //    private AlertDialog dialogPermissionAlertDialog;
    //    private final ActivityResultLauncher<String> requestPermissionLauncherLocation = registerForActivityResult(
    //            new ActivityResultContracts.RequestPermission(), result -> {
    //                if (result) {
    ////                    addFragment(new LocationFragment(), false);
    //                    mainViewModel.itemFocusNewAutomationLocation.postValue(ios);
    //                    ((MainActivity) requireActivity()).navControllerMain.navigate(R.id.action_newAutomationFragment_to_locationFragment);
    //                } else {
    //                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
    //                        boolean b = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION);
    //                        if (getContext() != null && !b) {
    //                            if (dialogPermissionAlertDialog == null) {
    //                                dialogPermissionAlertDialog = MethodUtils.showDialogPermission(getContext(), true, false);
    //                            }
    //                            if (!dialogPermissionAlertDialog.isShowing()) {
    //                                dialogPermissionAlertDialog.show();
    //                            }
    //                        } else {
    //                            toastText(R.string.text_detail_when_permission_location);
    //                        }
    //                    } else {
    //                        toastText(R.string.text_detail_when_permission_location);
    //                    }
    //                }
    //            });
    override fun getViewModel(): Class<NewAutomationViewModel> {
        return NewAutomationViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_new_automation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
//        setUpPaddingStatusBar(binding.layoutNewAutomation)
        (requireActivity() as MainActivity).setColorNavigation(R.color.color_F2F2F6)
//        observerData()
//        initListener()
//        if (savedInstanceState != null) {
//            ios = Gson().fromJson(
//                savedInstanceState.getString(Constant.ITEM_NEW_AUTOMATION),
//                object : TypeToken<FocusIOS?>() {}.type
//            )
//            mainViewModel.itemFocusNewAutomation.postValue(ios)
//        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
//        outState.putString(Constant.ITEM_NEW_AUTOMATION, Gson().toJson(ios))
    }

    private fun initListener() {
        binding.viewClock.setOnClickListener { v: View? ->
            ViewHelper.preventTwoClick(v)
            mainViewModel.itemFocusNewAutomationTime.postValue(ios)
            //            addFragment(new TimeFragment(), false);
            (requireActivity() as MainActivity).navigate(
                R.id.action_newAutomationFragment_to_timeFragment,
                R.id.newAutomationFragment
            )
        }
        //        binding.viewLocation.setOnClickListener(v -> {
//            ViewHelper.preventTwoClick(v);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
//                    != PackageManager.PERMISSION_GRANTED
//                    && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
//                    != PackageManager.PERMISSION_GRANTED) {
//                requestPermissionLauncherLocation.launch(Manifest.permission.ACCESS_FINE_LOCATION);
//            } else {
////                addFragment(new LocationFragment(), false);
//                mainViewModel.itemFocusNewAutomationLocation.postValue(ios);
//                ((MainActivity) requireActivity()).navControllerMain.navigate(R.id.action_newAutomationFragment_to_locationFragment);
//            }
//
//
//        });
        binding.viewApp.setOnClickListener { v: View? ->
            ViewHelper.preventTwoClick(v)
            mainViewModel.itemFocusNewAutomationApps.postValue(ios)
            if (!isAccessGranted(requireContext())) {
                val dialogPermissionUsage = DialogPermissionUsage()
                dialogPermissionUsage.navigateFragment = Constant.NEW_AUTO_APP
                dialogPermissionUsage.show(childFragmentManager, null)
            } else {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (isAdded) {
                        (requireActivity() as MainActivity).navigate(
                            R.id.action_newAutomationFragment_to_appsFragment,
                            R.id.newAutomationFragment
                        )
                    }
                }, 100)
            }
        }
        backPress()
    }

    private fun backPress() {
        binding.imBack.setOnClickListener { v: View? ->
            mainViewModel.itemFocusDetail.postValue(ios)
            requireActivity().onBackPressed()
        }
    }

    private fun observerData() {
        mainViewModel.itemFocusNewAutomation.observe(viewLifecycleOwner) { focusIOS: FocusIOS? ->
            if (focusIOS != null) {
                ios = focusIOS
                if (focusIOS.name == Constant.GAMING) {
                    binding.tvApp.text = getString(R.string.game)
                }
            }
        }
        mainViewModel.openNewAutomationApp.observe(viewLifecycleOwner) { aBoolean: Boolean ->
            if (aBoolean) {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (isAdded) {
                        (requireActivity() as MainActivity).navigate(
                            R.id.action_newAutomationFragment_to_appsFragment,
                            R.id.newAutomationFragment
                        )
                        mainViewModel.openNewAutomationApp.postValue(false)
                    }
                }, 100)
            }
        }
    }

    override fun onPermissionGranted() {}
    override fun onDestroy() {
        super.onDestroy()
        onBackPressedCallback.remove()
    }
}
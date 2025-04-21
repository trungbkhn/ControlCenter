package com.tapbi.spark.controlcenter.ui.main.focus

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.adapter.FocusAdapter
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.common.models.MessageEvent
import com.tapbi.spark.controlcenter.data.model.FocusIOS
import com.tapbi.spark.controlcenter.databinding.FragmentFocusBinding
import com.tapbi.spark.controlcenter.ui.base.BaseBindingFragment
import com.tapbi.spark.controlcenter.ui.main.MainActivity
import com.tapbi.spark.controlcenter.ui.main.focus.add.AddFocusLayout
import com.tapbi.spark.controlcenter.utils.MethodUtils
import com.tapbi.spark.controlcenter.utils.StatusBarUtils
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.math.sqrt

class FocusFragment : BaseBindingFragment<FragmentFocusBinding, FocusViewModel>() {
    //    public FocusIOS focusIOSOpen = null;
    //    private AlertDialog dialogPermissionAlertDialog;
    //    private final ActivityResultLauncher<String[]> requestMultiplePermissions = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
    //        if (result.containsValue(true)) {
    //
    //            if (((MainActivity) requireActivity()).getActionIntent() != null){
    //                if (((MainActivity) requireActivity()).getActionIntent().equals(Constant.SETTING_FOCUS)){
    //                    viewModel.getFocusById(((MainActivity) requireActivity()).getIdFocusSetting());
    //                } else if (((MainActivity) requireActivity()).getActionIntent().equals(Constant.REQUEST_PERMISSION_PHONE)){
    //
    //                } else if (focusIOSOpen != null){
    //                    openFocus(focusIOSOpen);
    //                    focusIOSOpen = null;
    //                } else {
    ////                    if (MethodUtils.checkPermissionCallListener(requireContext())) {
    ////                        ((MainActivity) requireActivity()).navControllerMain.navigate(R.id.action_focusFragment_to_focusDetailFragment);
    ////                    }
    //                    ((MainActivity) requireActivity()).navControllerMain.navigate(R.id.action_focusFragment_to_focusDetailFragment);
    //                }
    //
    //                ((MainActivity) requireActivity()).setActionIntent("");
    //                ((MainActivity) requireActivity()).setIdFocusSetting(-1);
    //            } else {
    ////                if (MethodUtils.checkPermissionCallListener(requireContext())) {
    ////                    ((MainActivity) requireActivity()).navControllerMain.navigate(R.id.action_focusFragment_to_focusDetailFragment);
    ////                }
    //                ((MainActivity) requireActivity()).navControllerMain.navigate(R.id.action_focusFragment_to_focusDetailFragment);
    //            }
    //        } else {
    //            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
    //                boolean b = MethodUtils.checkPermissionCallListener(getContext());
    //                if (getContext() != null && !b && isAdded() && isVisible()) {
    //                    if (dialogPermissionAlertDialog == null) {
    //                        dialogPermissionAlertDialog = MethodUtils.showDialogPermission(getContext(), true, false);
    //                    }
    //                    if (!dialogPermissionAlertDialog.isShowing()) {
    //                        dialogPermissionAlertDialog.show();
    //                    }
    //                }
    //            }
    //        }
    //
    //    });
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.vAddFocus.visibility == View.VISIBLE) {
                    hideLayoutAdd(false, null)
                } else {
                    isEnabled = false
                    //                requireActivity().onBackPressed();
                    (requireActivity() as MainActivity).navControllerMain.popBackStack(
                        R.id.focusFragment,
                        true
                    )
                }
            }
        }
    private var focusAdapter: FocusAdapter? = null
    override fun getViewModel(): Class<FocusViewModel> {
        return FocusViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_focus

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
        when ((requireActivity() as MainActivity).actionIntent) {
            Constant.ADD_FOCUS -> {
                binding.cvContent.radius = MethodUtils.dpToPx(requireContext(), 10f).toFloat()
                binding.layoutFocus.setBackgroundColor(Color.BLACK)
                binding.vAddFocus.scrollToTop()
                binding.vAddFocus.visibility = View.VISIBLE
                val animation = AnimationUtils.loadAnimation(context, R.anim.translate_up)
                binding.vAddFocus.startAnimation(animation)
                val animation1 = AnimationUtils.loadAnimation(context, R.anim.scale_down)
                binding.cvContent.startAnimation(animation1)
                makeStatusBarLight(requireActivity())
                (requireActivity() as MainActivity).actionIntent = ""
            }

            Constant.SETTING_FOCUS -> {
                //                    if (MethodUtils.checkPermissionCallListener(requireContext())) {
//                        viewModel.getFocusById(((MainActivity) requireActivity()).getIdFocusSetting());
//                        ((MainActivity) requireActivity()).setActionIntent("");
//                    } else {
//                        requestPermissionPhone(requestMultiplePermissions);
//                    }
                viewModel.getFocusById((requireActivity() as MainActivity).idFocusSetting)
                (requireActivity() as MainActivity).actionIntent = ""
            }
        }
        initView()
        observerData()
        setUpPaddingStatusBar(binding.clContent)
        (requireActivity() as MainActivity).setColorNavigation(R.color.text_detail_splash)
    }

    private fun observerData() {
        mainViewModel.listFocusAdded.postValue(App.presetFocusList)
        mainViewModel.listFocusAdded.observe(viewLifecycleOwner) { focusIOS: List<FocusIOS?>? ->
            if (focusIOS != null) {
                focusAdapter!!.setData(focusIOS)
                mainViewModel.getListFocusAdd(focusIOS)
            }
        }
        mainViewModel.listAddFocusMutableLiveData.observe(viewLifecycleOwner) { list: List<FocusIOS?>? ->
            if (list != null) {
                binding.vAddFocus.post { if (isAdded) binding.vAddFocus.setListFocus(list) }
            }
        }
        viewModel.focusSetting.observe(viewLifecycleOwner) { focusIOS: FocusIOS? ->
            if (focusIOS != null) {
                mainViewModel.itemFocusDetail.postValue(focusIOS)
                //                openFocusDetail();
//                ((MainActivity) requireActivity()).navControllerMain.navigate(R.id.action_focusFragment_to_focusDetailFragment);
                (requireActivity() as MainActivity).navigate(
                    R.id.action_focusFragment_to_focusDetailFragment,
                    R.id.focusFragment
                )
                viewModel.focusSetting.postValue(null)
            }
        }
        viewModel.nextAfterFullScreenLiveData.observe(viewLifecycleOwner) { aBoolean: Boolean ->
            if (aBoolean) {
                navigate(R.id.focusFragment, R.id.action_focusFragment_to_focusDetailFragment)
                viewModel.nextAfterFullScreenLiveData.value = false
            }
        }
    }

    private fun initView() {
        binding.layoutFocus.post {
            if (isAdded) {
                val paddingTop =
                    (binding.layoutFocus.height - sqrt(0.9) * binding.layoutFocus.height).toInt()
                binding.vAddFocus.setPadding(
                    0,
                    paddingTop + MethodUtils.dpToPx(requireContext(), 10f),
                    0,
                    0
                )
            }
        }
        initAdapter()
        initListener()
        MethodUtils.hideKeyboard(requireActivity())
    }

    private fun initListener() {
        binding.viewCustom.setOnClickListener { view: View? ->
            ViewHelper.preventTwoClick(view)
            binding.cvContent.radius = MethodUtils.dpToPx(requireContext(), 10f).toFloat()
            binding.layoutFocus.setBackgroundColor(Color.BLACK)
            binding.vAddFocus.scrollToTop()
            binding.vAddFocus.visibility = View.VISIBLE
            val animation = AnimationUtils.loadAnimation(context, R.anim.translate_up)
            binding.vAddFocus.startAnimation(animation)
            val animation1 = AnimationUtils.loadAnimation(context, R.anim.scale_down)
            binding.cvContent.startAnimation(animation1)
            makeStatusBarLight(requireActivity())
            (requireActivity() as MainActivity).setColorNavigation(R.color.text_detail_splash)
        }
        binding.vAddFocus.setClickListener(object : AddFocusLayout.ClickListener {
            override fun onCancel() {
                hideLayoutAdd(false, null)

            }

            override fun onFocusClick(focusIOS: FocusIOS?) {
//                if (MethodUtils.checkPermissionCallListener(requireContext())) {
//                    openFocus(focusIOS);
//                } else {
//                    focusIOSOpen = focusIOS;
//                    requestPermissionPhone(requestMultiplePermissions);
//                }
                openFocus(focusIOS)
            }
        })
        binding.icBack.setOnClickListener { v: View? ->
            ViewHelper.preventTwoClick(v)
            requireActivity().onBackPressed()
        }
    }

    private fun openFocus(focusIOS: FocusIOS?) {
        if (focusIOS!!.name != Constant.CUSTOM) {
            hideLayoutAdd(true, focusIOS)
            mainViewModel.insertFocus(focusIOS)
        } else {
            //((MainActivity) requireActivity()).navControllerMain.navigate(R.id.action_focusFragment_to_createFocusFragment);
            navigate(R.id.focusFragment, R.id.action_focusFragment_to_createFocusFragment)
        }
    }

    private fun hideLayoutAdd(isShowDetail: Boolean, focusIOS: FocusIOS?) {
        val animation = AnimationUtils.loadAnimation(context, R.anim.translate_down)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                binding.cvContent.radius = MethodUtils.dpToPx(requireContext(), 0f).toFloat()
                binding.vAddFocus.visibility = View.GONE
                binding.layoutFocus.setBackgroundColor(Color.TRANSPARENT)
                StatusBarUtils.setColorTextStatusBarBlack(requireActivity())
                if (isShowDetail) {
//                    openFocusDetail();
//                    ((MainActivity) requireActivity()).navControllerMain.navigate(R.id.action_focusFragment_to_focusDetailFragment);
                    navigate(R.id.focusFragment, R.id.action_focusFragment_to_focusDetailFragment)
                    mainViewModel.itemFocusDetail.postValue(focusIOS)
                }
                (requireActivity() as MainActivity).setColorNavigation(R.color.color_F2F2F6)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        binding.vAddFocus.startAnimation(animation)
        val animation1 = AnimationUtils.loadAnimation(context, R.anim.scale_up)
        binding.cvContent.startAnimation(animation1)

    }

    private fun initAdapter() {
        focusAdapter = FocusAdapter()
        binding.rvFocus.adapter = focusAdapter
        focusAdapter!!.setClickItemFocus { focusIOS: FocusIOS? ->
//            openFocusDetail();
//            ((MainActivity) requireActivity()).navControllerMain.navigate(R.id.action_focusFragment_to_focusDetailFragment);
            mainViewModel.itemFocusDetail.value = focusIOS
            showAdsFull("item_focus")
        }
    }

    //    private void openFocusDetail() {
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
    //        } else {
    ////            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
    ////                requestRole();
    ////            } else {
    //            ((MainActivity) requireActivity()).navControllerMain.navigate(R.id.action_focusFragment_to_focusDetailFragment);
    ////            }
    //        }
    //    }
    //    public void requestRole() {
    //        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q && getContext() != null) {
    //            RoleManager roleManager = (RoleManager) getContext().getSystemService(Context.ROLE_SERVICE);
    //            Intent intent = roleManager.createRequestRoleIntent("android.app.role.CALL_SCREENING");
    //            someActivityResultLauncher.launch(intent);
    //        }
    //    }
    //
    //    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    //    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
    //            new ActivityResultContracts.StartActivityForResult(),
    //            result -> {
    //                if (result.getResultCode() == Activity.RESULT_OK) {
    //                    // There are no request codes
    //                    if (isAdded() && isVisible()) {
    //                        ((MainActivity) requireActivity()).navControllerMain.navigate(R.id.action_focusFragment_to_focusDetailFragment);
    //                    }
    //                }
    //            });
    override fun onPermissionGranted() {}
    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(messageEvent: MessageEvent) {
        when (messageEvent.typeEvent) {
            Constant.UPDATE_TIME_CHANGE, Constant.UPDATE_APP_CHANGE, Constant.UPDATE_VIEW_FROM_CONTROL, Constant.PACKAGE_APP_REMOVE -> if (focusAdapter != null) {
                focusAdapter!!.setData(App.presetFocusList as List<FocusIOS?>)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        StatusBarUtils.setColorTextStatusBarBlack(requireActivity())
        (requireActivity() as MainActivity).setColorNavigation(R.color.text_detail_splash)
        onBackPressedCallback.remove()
    }

    override fun nextAfterFullScreen() {
        super.nextAfterFullScreen()
        viewModel.nextAfterFullScreenLiveData.postValue(true)
    } //    @Override

    //    public void onDestroy() {
    //        super.onDestroy();
    //        onBackPressedCallback.setEnabled(false);
    //    }
    companion object {
        fun makeStatusBarLight(activity: Activity) {
            val window = activity.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = Color.TRANSPARENT
            activity.window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
    }
}
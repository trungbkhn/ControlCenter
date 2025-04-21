package com.tapbi.spark.controlcenter.ui.main.homemain.home

import android.Manifest
import android.Manifest.permission.READ_PHONE_STATE
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tapbi.spark.controlcenter.App.Companion.tinyDB
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.adapter.ThemeHomeAdapter
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.common.Constant.DIALOG_CHOOSE_STYLE
import com.tapbi.spark.controlcenter.common.Constant.REQUEST_CODE_READ_PHONE_STATE
import com.tapbi.spark.controlcenter.data.model.ThemeControl
import com.tapbi.spark.controlcenter.data.repository.ThemeHelper
import com.tapbi.spark.controlcenter.databinding.FragmentHomeBinding
import com.tapbi.spark.controlcenter.eventbus.EventCheckControlState
import com.tapbi.spark.controlcenter.eventbus.EventSelectThemes
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614
import com.tapbi.spark.controlcenter.ui.base.BaseBindingFragment
import com.tapbi.spark.controlcenter.ui.dialog.DialogChooseStyle
import com.tapbi.spark.controlcenter.ui.dialog.DialogLottieClick
import com.tapbi.spark.controlcenter.ui.dialog.DialogUserManual
import com.tapbi.spark.controlcenter.ui.dialog.dialogchoosecontrol.BottomSheetChooseControl
import com.tapbi.spark.controlcenter.ui.dialog.requestpermission.RequestPermissionBottomSheet
import com.tapbi.spark.controlcenter.ui.main.MainActivity
import com.tapbi.spark.controlcenter.utils.MethodUtils
import com.tapbi.spark.controlcenter.utils.PermissionUtils.checkAllPermissionWithOutReadPhoneState
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper
import com.tapbi.spark.controlcenter.utils.safeDelay
import com.tapbi.spark.controlcenter.views.helper.BottomMarginItemDecoration
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber


class HomeFragment : BaseBindingFragment<FragmentHomeBinding, HomeViewModel>() {

    private var themeHomeAdapter: ThemeHomeAdapter? = null
    private var hasDown = false
    private var isFirstCheckService = false
    private var themePreview: ThemeControl? = null


    private var dialogChooseStyle: DialogChooseStyle? = null
    override fun getViewModel(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }


    override val layoutId: Int
        get() = R.layout.fragment_home

    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {

        safeDelay(10) {
            initView()
            initListener()
            observerData()
        }
    }

    private var listThemes: MutableList<ThemeControl> = mutableListOf()
        set(value) {
            field = value
            themeHomeAdapter?.setData(value)
            field.indexOfFirst { item ->
                item.id == tinyDB.getLong(
                    Constant.KEY_ID_CURRENT_APPLY_THEME,
                    Constant.KEY_ID_CURRENT_APPLY_THEME_DEFAULT
                )
            }.let { index ->
                themeHomeAdapter?.setIdThemes(if (index != -1) index + 1 else index)  // +1 vi tri dau tien la title
            }
        }


    private fun observerData() {
        mainViewModel.listThemes.observe(viewLifecycleOwner) {
            if (it != null) {
                listThemes = it

            }
        }

    }


    private fun initListener() {
        binding.tvActivate.setOnClickListener {
            ViewHelper.preventTwoClick(it)
            if (checkPermissionService()) {
                binding.toggleUse.isChecked = true
            } else {
                (activity as MainActivity).intentPermissionActivity()
            }
        }
        binding.fab.root.setOnClickListener { view ->
            ViewHelper.preventTwoClick(view)
            MainActivity.isDispatchTouchEvent()
            if (dialogChooseStyle == null) {
                dialogChooseStyle = DialogChooseStyle()
            }
            dialogChooseStyle?.icLickDialogChooseStyle =
                object : DialogChooseStyle.ICLickDialogChooseStyle {
                    override fun onClick(type: Int) {
                        val bundle = Bundle()
                        bundle.putInt(Constant.KEY_ID_CATEGORY, type)
                        navigate(R.id.homeMainFragment, R.id.customizeControlFragment, bundle)
                    }

                }
            dialogChooseStyle?.let {
                if (isAdded && !childFragmentManager.isStateSaved) {
                    it.showDialog(childFragmentManager, DIALOG_CHOOSE_STYLE)
                }
            }
//            val bt = BottomSheetChooseControl()
//            if (isAdded && !childFragmentManager.isStateSaved) {
//                bt.show(childFragmentManager, "BottomSheetChooseControl")
//            }
        }


        binding.toggleUse.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            ViewHelper.preventTwoClick(buttonView, 1000)
            if (activity is MainActivity) {
                if (isChecked) {
                    if (checkPermissionService()) {
                        tinyDB.putInt(Constant.IS_ENABLE, Constant.DEFAULT_IS_ENABLE)
                        ThemeHelper.enableWindow()
                        setStateRunning(true)
                    } else {
                        tinyDB.putInt(Constant.IS_ENABLE, Constant.IS_DISABLE)
                        disableService()
                        setStateRunning(false)
                        if (checkAllPermissionWithOutReadPhoneState()) {
                            val b = ActivityCompat.shouldShowRequestPermissionRationale(
                                (activity as MainActivity),
                                READ_PHONE_STATE
                            )
                            if (!b) {
                                (activity as MainActivity).showDialogPermissionOpenSettings()
                            } else {
                                ActivityCompat.requestPermissions(
                                    (activity as MainActivity),
                                    arrayOf(READ_PHONE_STATE),
                                    REQUEST_CODE_READ_PHONE_STATE
                                )
                            }
                        } else {
                            (activity as MainActivity).intentPermissionActivity()
                        }

                    }
                } else {
                    tinyDB.putInt(Constant.IS_ENABLE, Constant.IS_DISABLE)
                    disableService()
                    setStateRunning(false)
                }
            }

        }

    }

    @Subscribe
    fun onEventCheckControlState(eventCheckControlState: EventCheckControlState) {
        if (eventCheckControlState.tag == RequestPermissionBottomSheet::class.simpleName.toString()) {
            context?.let {
                val isChecked: Boolean = if (checkPermissionService()) {
                    tinyDB.getInt(
                        Constant.IS_ENABLE,
                        Constant.IS_DISABLE
                    ) == Constant.DEFAULT_IS_ENABLE
                } else {
                    false
                }
                setStateRunning(isChecked)

            }
        } else {
            if (checkPermissionService()) {
                tinyDB.putInt(Constant.IS_ENABLE, Constant.DEFAULT_IS_ENABLE)
                ThemeHelper.enableWindow()
                setStateRunning(true)
            } else {
                tinyDB.putInt(Constant.IS_ENABLE, Constant.IS_DISABLE)
                disableService()
                setStateRunning(false)
            }
        }
        MethodUtils.intentToCheckPermission(context)
    }


    private fun disableService() {
        if (NotyControlCenterServicev614.getInstance() != null) {
            NotyControlCenterServicev614.getInstance().disableWindow()
        }
    }


    private fun initView() {
        initShowDialogUserManual()
        checkServiceRunning()
        themeHomeAdapter = ThemeHomeAdapter()
        binding.ryAppear.adapter = themeHomeAdapter
        themeHomeAdapter?.clickListener = object : ThemeHomeAdapter.ClickListener {
            override fun onClick(themeControl: ThemeControl) {
                if (isAdded) {
                    themePreview = themeControl
                    showAdsFull(requireContext().getString(R.string.tag_inter_home))
                }
            }
        }
        binding.ryAppear.adapter = themeHomeAdapter
        binding.tvActivate.text = String.format(
            getString(R.string.text_activate_home),
            getString(R.string.app_name),
        )
        binding.tvAppName.text = getString(R.string.app_name).substringBefore("-")
        binding.ryAppear.addItemDecoration(BottomMarginItemDecoration(100f))
        val gridLayoutManager = GridLayoutManager(context, 2)

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (themeHomeAdapter?.getItemViewType(position) === themeHomeAdapter?.ITEM_TITLE_APPEARANCE) {
                    return 2
                }
                return 1
            }
        }
        binding.ryAppear.setLayoutManager(gridLayoutManager)
        binding.ryAppear.setAdapter(themeHomeAdapter)

        binding.ryAppear.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && !hasDown) {
                    binding.fab.clFab.collapse()
                    hasDown = true
                } else if (dy < 0 && hasDown) {
                    binding.fab.clFab.expand()

                    hasDown = false

                }
            }
        })
        binding.fab.clFab.setListenerExpand { isExpanded ->
            if (isExpanded) {
                binding.fab.imAdd.animate()
                    .rotation(90f) // Rotate ImageView to 90 degrees
                    .setDuration(300)
                    .start()
            } else {
                binding.fab.imAdd.animate()
                    .rotation(0f) // Rotate ImageView to 90 degrees
                    .setDuration(300)
                    .start()
            }
        }
    }


    override fun onResume() {
        super.onResume()
        checkServiceRunning()
    }

    override fun nextAfterFullScreen() {
        super.nextAfterFullScreen()
        themePreview?.let {
            showDialogPreviewControl(requireContext(), themePreview!!) {
                val dialog = DialogLottieClick()
                if (!childFragmentManager.isStateSaved && isAdded) {
                    dialog.showDialog(childFragmentManager, "dialogLottieClick")
                }
            }
        }
    }


    private fun checkServiceRunning() {
        if (!isFirstCheckService) {
            context?.let {
                val isChecked: Boolean = if (checkPermissionService()) {
                    tinyDB.getInt(
                        Constant.IS_ENABLE,
                        Constant.IS_DISABLE
                    ) == Constant.DEFAULT_IS_ENABLE
                } else {
                    false
                }
                setStateRunning(isChecked)
                isFirstCheckService = isChecked
            }

        }


    }

    override fun onPause() {
        super.onPause()
        dismissDialogPreviewControl()
        isFirstCheckService = false
        dismissDialogChooseStyle()
    }

    private fun dismissDialogChooseStyle() {
        if (dialogChooseStyle?.isAdded == true && dialogChooseStyle?.isStateSaved == false) {
            dialogChooseStyle?.dismissAllowingStateLoss()
        }
    }

    private fun setStateRunning(isRunning: Boolean) {
        binding.toggleUse.isChecked = isRunning
        binding.tvActivate.visibility =
            if (isRunning && checkPermission()) View.GONE else View.VISIBLE
        binding.tvRunning.setText(if (isRunning) R.string.running else R.string.text_not_running)
    }


    override fun onPermissionGranted() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventBusSelectThemes(event: EventSelectThemes) {
        listThemes.indexOfFirst { it.id == event.id }.let {
            themeHomeAdapter?.setIdThemes(if (it != -1) it + 1 else it)
        }
    }

    private fun initShowDialogUserManual() {
        if (!tinyDB.getBoolean(Constant.IS_SHOW_DIALOG_USER_MANUAL, false)) {
            val dialogUserManual = DialogUserManual()
            if (!childFragmentManager.isStateSaved && isAdded) {
                dialogUserManual.showDialog(childFragmentManager, "dialogUserManual")
            }
        }


    }
}
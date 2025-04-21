package com.tapbi.spark.controlcenter.ui.main.focus.focusdetail

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.App.Companion.setFocusStart
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.adapter.AllowedAppsAdapter
import com.tapbi.spark.controlcenter.adapter.AllowedPeopleAdapter
import com.tapbi.spark.controlcenter.adapter.AutomationAdapter
import com.tapbi.spark.controlcenter.adapter.AutomationAdapter.IListenerAutomation
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.common.models.MessageEvent
import com.tapbi.spark.controlcenter.data.model.FocusIOS
import com.tapbi.spark.controlcenter.data.model.ItemApp
import com.tapbi.spark.controlcenter.data.model.ItemPeople
import com.tapbi.spark.controlcenter.data.model.ItemTurnOn
import com.tapbi.spark.controlcenter.databinding.FragmentFocusIosBinding
import com.tapbi.spark.controlcenter.ui.base.BaseBindingFragment
import com.tapbi.spark.controlcenter.ui.dialog.DialogDelete
import com.tapbi.spark.controlcenter.ui.dialog.DialogPermissionUsage
import com.tapbi.spark.controlcenter.ui.dialog.DialogRequestPermissionWriteSetting
import com.tapbi.spark.controlcenter.ui.main.MainActivity
import com.tapbi.spark.controlcenter.utils.AppUtils
import com.tapbi.spark.controlcenter.utils.DensityUtils
import com.tapbi.spark.controlcenter.utils.MethodUtils
import com.tapbi.spark.controlcenter.utils.PermissionUtils
import com.tapbi.spark.controlcenter.utils.PermissionUtils.isAccessGranted
import com.tapbi.spark.controlcenter.utils.StringUtils.getIconDefaultApp
import com.tapbi.spark.controlcenter.utils.TimeUtils
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

class FocusDetailFragment : BaseBindingFragment<FragmentFocusIosBinding, FocusDetailViewModel>() {
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                isEnabled = false
                (requireActivity() as MainActivity).navControllerMain.popBackStack(
                    R.id.focusDetailFragment,
                    true
                )
            }
        }

    //    private final String[] PERMISSIONS = {
    //            Manifest.permission.READ_CONTACTS,
    //            Manifest.permission.RECEIVE_SMS,
    //    };
    private var numberPeopleShow = -1
    private val listAllowedPeople: MutableList<ItemPeople> = ArrayList()
    private var dialogPermissionAlertDialog: DialogRequestPermissionWriteSetting? = null

    //    private final ActivityResultLauncher<String> requestPermissionLauncherLocation = registerForActivityResult(
    //            new ActivityResultContracts.RequestPermission(), result -> {
    //                if (result) {
    //                    if (!((MainActivity) requireActivity()).isRequestPermissionLocation()) {
    //                        ((MainActivity) requireActivity()).navControllerMain.navigate(R.id.action_focusDetailFragment_to_editlocationFragment);
    //                    }
    //                } else {
    //                    ((MainActivity) requireActivity()).setRequestPermissionLocation(false);
    //                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
    private val requestPermissionLauncherReadContact =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result: Boolean ->
            if (result) {
//                    addFragment(new AllowPeopleFragment(), false);
                navigationPeopleFocus()
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val b = shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)
                    if (context != null && !b) {
                        if (dialogPermissionAlertDialog == null) {
                            dialogPermissionAlertDialog =
                                MethodUtils.showDialogPermission(context, true, "",false, null)
                            Timber.e("NVQ DialogRequestPermissionWriteSetting :$this")
                        }
                        if (dialogPermissionAlertDialog?.dialog?.isShowing != true) {
                            dialogPermissionAlertDialog?.show(
                                childFragmentManager,
                                Constant.DIALOG_REQUEST_PERMISSION_WRITE_SETTING
                            )
                        }
                    } else {
                        toastText(R.string.text_detail_when_permission_read_contact)
                    }
                } else {
                    toastText(R.string.text_detail_when_permission_read_contact)
                }
            }
        }
    private var listAutomation: MutableList<ItemTurnOn> = ArrayList()
    private var listAllowedApps: MutableList<ItemApp> = ArrayList()
    private var allowedPeopleAdapter: AllowedPeopleAdapter? = null
    private var allowedAppsAdapter: AllowedAppsAdapter? = null
    private var automationAdapter: AutomationAdapter? = null
    private var focusIOSs: FocusIOS? = null
    private var isPresetGamingOrWork = false
    private var checkViewCreated = false
    override fun getViewModel(): Class<FocusDetailViewModel> {
        return FocusDetailViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_focus_ios

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        initView()
        initAdapter()
        observerData()
        initListener()
        setUpPaddingStatusBar(binding.layoutFocusDetail)
        (requireActivity() as MainActivity).setColorNavigation(R.color.color_F2F2F6)
        if (savedInstanceState?.getString(Constant.ITEM_FOCUS_DETAIL) != null) {
            focusIOSs = Gson().fromJson(
                savedInstanceState.getString(Constant.ITEM_FOCUS_DETAIL),
                object : TypeToken<FocusIOS?>() {}.type
            )
            mainViewModel.itemFocusDetail.postValue(focusIOSs)
        }

    }

    private fun checkPermissionContact() {
        App.ins.focusUtils?.sendActionFocus(Constant.ACTION_CHECK_PERMISSION_CONTACT, "")
    }

    private fun initView() {
        binding.viewAllowed.post {
            var widthPeopleAllow = -1
            if (isAdded) {
                val imRightAppLayoutParams = binding.imRightApp.layoutParams as MarginLayoutParams
                widthPeopleAllow = if (binding.tvApps.width > binding.tvPeople.width) {
                    val tvAppsLayoutParams = binding.tvApps.layoutParams as MarginLayoutParams
                    DensityUtils.getScreenWidth() - tvAppsLayoutParams.leftMargin - tvAppsLayoutParams.rightMargin - binding.tvApps.width - binding.imRightApp.width - imRightAppLayoutParams.leftMargin - imRightAppLayoutParams.rightMargin
                } else {
                    val tvPeopleLayoutParams = binding.tvPeople.layoutParams as MarginLayoutParams
                    DensityUtils.getScreenWidth() - tvPeopleLayoutParams.leftMargin - tvPeopleLayoutParams.rightMargin - binding.tvPeople.width - binding.imRightApp.width - imRightAppLayoutParams.leftMargin - imRightAppLayoutParams.rightMargin
                }
                numberPeopleShow = widthPeopleAllow / MethodUtils.dpToPx(requireContext(), 45f) - 1
                if (checkViewCreated) {
                    setUIListAllowedPeople(listAllowedPeople)
                }
            }
        }
        initViewFromFocus()
    }

    private fun initViewFromFocus() {
        if (focusIOSs != null) {
            if (focusIOSs?.startCurrent == true || focusIOSs?.startAutoLocation == true || focusIOSs?.startAutoAppOpen == true || focusIOSs?.startAutoTime == true) {
                binding.switchFocus.setImageResource(R.drawable.ic_switch_on)
            } else {
//                initTextViewWarring();
                isCheckAppGaming
                binding.switchFocus.setImageResource(R.drawable.ic_switch_off)
            }
            App.colorFocus = focusIOSs?.colorFocus ?: ""
            binding.tvTitle.text = getIconDefaultApp(
                focusIOSs?.name ?: "", requireContext()
            )
            binding.tvAddSchedule.setTextColor(Color.parseColor(focusIOSs?.colorFocus))
            binding.tvName.text = getIconDefaultApp(
                focusIOSs?.name ?: "", requireContext()
            )
            binding.imScheduleOrAuto.setColorFilter(Color.parseColor(focusIOSs?.colorFocus))
            checkFocus()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (focusIOSs != null) {
            outState.putString(Constant.ITEM_FOCUS_DETAIL, Gson().toJson(focusIOSs))
        }
    }

    private fun observerData() {
        mainViewModel.itemFocusDetail.observe(viewLifecycleOwner) { focusIOS: FocusIOS? ->
            if (focusIOS != null) {
                focusIOSs = focusIOS
                mainViewModel.getAllowedPeopleByName(focusIOSs?.name)
                mainViewModel.getAllowedAppsByName(focusIOSs?.name)
                mainViewModel.getListAutomationByFocus(focusIOSs?.name)
                initViewFromFocus()
                mainViewModel.itemFocusDetail.postValue(null)
            }
        }
        mainViewModel.listItemAllowedPeople.observe(viewLifecycleOwner) { itemPeople: List<ItemPeople>? ->
            if (itemPeople != null) {
                listAllowedPeople.clear()
                listAllowedPeople.addAll(itemPeople)
                setUIListAllowedPeople(listAllowedPeople)
                mainViewModel.listItemAllowedPeople.postValue(null)
            }
        }
        mainViewModel.listItemAllowedApps.observe(viewLifecycleOwner) {
            if (it != null) {
                listAllowedApps.clear()
                listAllowedApps.addAll(it)
                setUIListAllowedApps(listAllowedApps)
                mainViewModel.listItemAllowedApps.postValue(null)
            }
        }
        mainViewModel.listAutomationMutableLiveData.observe(viewLifecycleOwner) { turnOnList: List<ItemTurnOn>? ->
            if (turnOnList != null) {
                listAutomation.clear()
                listAutomation.addAll(turnOnList)
                automationAdapter?.setData(turnOnList, App.colorFocus)
                isCheckAppGaming
            }
        }
        mainViewModel.openEditAutomationApp.observe(viewLifecycleOwner) { aBoolean: Boolean ->
            if (aBoolean) {
                Handler().postDelayed({
                    if (isAdded) {
                        (requireActivity() as MainActivity).navigate(
                            R.id.action_focusDetailFragment_to_editAppFragment,
                            R.id.focusDetailFragment
                        )
                        mainViewModel.openEditAutomationApp.postValue(false)
                    }
                }, 100)
            }
        }
        mainViewModel.deleteFocusMutableLiveData.observe(viewLifecycleOwner) { aBoolean: Boolean ->
            if (aBoolean) {
                (requireActivity() as MainActivity).navigate(
                    R.id.action_focusDetailFragment_to_focusFragment,
                    R.id.focusDetailFragment
                )
                mainViewModel.deleteFocusMutableLiveData.postValue(false)
            }
        }
    }

    private fun setUIListAllowedApps(listApp: List<ItemApp>) {
        if (listApp.isNotEmpty()) {
            val listCurrentShow: MutableList<ItemApp> = ArrayList()
            if (numberPeopleShow < listApp.size) {
                for (i in 0 until numberPeopleShow) {
                    listCurrentShow.add(listApp[i])
                }
            } else {
                listCurrentShow.addAll(listApp)
            }
            if (listCurrentShow.size > 0) {
                val size = listApp.size - numberPeopleShow + 1
                allowedAppsAdapter?.setData(listCurrentShow, size)
                binding.tvNoApp.visibility = View.INVISIBLE
                binding.rcApp.visibility = View.VISIBLE
            } else {
                binding.tvNoApp.visibility = View.VISIBLE
                binding.rcApp.visibility = View.GONE
            }
        } else {
            binding.tvNoApp.visibility = View.VISIBLE
            binding.rcApp.visibility = View.GONE
        }
    }

    private fun setUIListAllowedPeople(listPeople: List<ItemPeople>) {
        checkViewCreated = true
        if (listPeople.isNotEmpty()) {
            val listCurrentShow: MutableList<ItemPeople> = ArrayList()
            if (numberPeopleShow < listPeople.size) {
                for (i in 0 until numberPeopleShow) {
                    listCurrentShow.add(listPeople[i])
                }
            } else {
                listCurrentShow.addAll(listPeople)
            }
            if (listCurrentShow.size > 0) {
                val size = listPeople.size - numberPeopleShow + 1
                allowedPeopleAdapter?.setData(listCurrentShow, size)
                binding.rcPeople.visibility = View.VISIBLE
                binding.tvNoOne.visibility = View.INVISIBLE
            } else {
                binding.rcPeople.visibility = View.GONE
                binding.tvNoOne.visibility = View.VISIBLE
            }
        } else {
            binding.rcPeople.visibility = View.GONE
            binding.tvNoOne.visibility = View.VISIBLE
        }
    }

    private fun initListener() {
        backPress()
        binding.viewAllowedPeople.setOnClickListener { v: View? ->
            ViewHelper.preventTwoClick(v)
            mainViewModel.itemFocusCurrentPeople.postValue(focusIOSs)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_CONTACTS
                ) != PackageManager.PERMISSION_GRANTED /*!PermissionUtils.INSTANCE.checkAndRequestPermissions(requireActivity(), Manifest.permission.READ_CONTACTS, Manifest.permission.RECEIVE_SMS)*/) {
                requestPermissionLauncherReadContact.launch(Manifest.permission.READ_CONTACTS)
            } else {
                navigationPeopleFocus()
            }
        }
        binding.viewAllowedApp.setOnClickListener { v: View? ->
            ViewHelper.preventTwoClick(v)
            (requireActivity() as MainActivity).navigate(
                R.id.action_focusDetailFragment_to_allowAppFragment,
                R.id.focusDetailFragment
            )
            mainViewModel.itemFocusCurrentApp.postValue(focusIOSs)
        }
        binding.llView.setOnClickListener { v: View? ->
            ViewHelper.preventTwoClick(v)
            mainViewModel.itemFocusNewAutomation.postValue(focusIOSs)
            //            addFragment(new NewAutomationFragment(), false);
            (requireActivity() as MainActivity).navigate(
                R.id.action_focusDetailFragment_to_newAutomationFragment,
                R.id.focusDetailFragment
            )
        }
        binding.imEdit.setOnClickListener { v: View? ->
            ViewHelper.preventTwoClick(v)
            mainViewModel.itemEditFocusCurrent.postValue(focusIOSs)
            //            addFragment(new EditFocusFragment(), false);
            (requireActivity() as MainActivity).navigate(
                R.id.action_focusDetailFragment_to_editFocusFragment,
                R.id.focusDetailFragment
            )
        }
        binding.tvDelete.setOnClickListener { v: View? ->
            ViewHelper.preventTwoClick(v)
            if (focusIOSs == null) {
                return@setOnClickListener
            }
            val deleteFocus = DialogDelete(object : DialogDelete.ClickListener {
                override fun onClickDelete() {

                }

            });
            deleteFocus.setFocusIOS(focusIOSs)
            deleteFocus.show(childFragmentManager, null)
        }
        binding.switchFocus.setOnClickListener { v: View? ->
            ViewHelper.preventTwoClick(v)
            if (focusIOSs == null) {
                return@setOnClickListener
            }
            if (focusIOSs?.startAutoLocation == true || focusIOSs?.startAutoTime == true || focusIOSs?.startAutoAppOpen == true || focusIOSs?.startCurrent == true) {
                viewModel.updatePresetFocus()
                binding.switchFocus.setImageResource(R.drawable.ic_switch_off)
                focusIOSs?.startAutoAppOpen = false
                focusIOSs?.startAutoTime = false
                focusIOSs?.startAutoLocation = false
                focusIOSs?.startCurrent = false
                App.tinyDB.putString(Constant.FOCUS_START_OLD, "")
                Timber.e("hachung :" + "put")
                for (fois in App.presetFocusList) {
                    fois.startAutoAppOpen = false
                    fois.startAutoTime = false
                    fois.startAutoLocation = false
                    fois.startCurrent = false
                    viewModel.updateStartItemFocusIos(
                        isStartAutoAppOpen = false,
                        isStartCurrent = false,
                        isStartAutoLocation = false,
                        isStartAutoTime = false,
                        name = fois.name
                    )
                }
                isCheckAppGaming
                setFocusStart(null)
            } else {
                if (focusIOSs?.name == Constant.GAMING) {
                    isCheckAppGaming
                }
                //                else if (focusIOSs.getName().equals(Constant.WORK)) {
//                    isCheckCountLocation();
//                }
                if (!isPresetGamingOrWork) {
                    viewModel.updatePresetFocus()
                    binding.switchFocus.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_switch_on
                        )
                    )
                    focusIOSs?.startAutoAppOpen = false
                    focusIOSs?.startAutoTime = false
                    focusIOSs?.startAutoLocation = false
                    focusIOSs?.startCurrent = true
                    App.tinyDB.putString(Constant.FOCUS_START_OLD, focusIOSs?.name)
                    viewModel.updatePresetHand(focusIOSs?.name)
                    setFocusStart(focusIOSs)
                }
            }
            viewModel.deleteItemTurnOnByControl()
        }
    }

    private fun backPress() {
        binding.imBack.setOnClickListener { v: View? ->
            ViewHelper.preventTwoClick(v)
            requireActivity().onBackPressed()
        }
    }

    private val isCheckAppGaming: Unit
        get() {
            if (focusIOSs != null && focusIOSs?.name != null) {
                if (focusIOSs?.name == Constant.GAMING) {
                    var isExitApp = false
                    var isExitTime = false
                    for (itemApp in listAutomation) {
                        if (itemApp.typeEvent == Constant.APPS && itemApp.start) {
                            if (MethodUtils.packageIsGame(requireContext(), itemApp.packageName)) {
                                isExitApp = true
                            }
                        } else if (itemApp.typeEvent == Constant.TIME && itemApp.start) {
                            isExitTime = true
                        }
                    }
                    if (isExitApp && isExitTime) {
                        isPresetGamingOrWork = false
                        binding.tvWaning.visibility = View.GONE
                    } else {
                        isPresetGamingOrWork = true
                        binding.tvWaning.text =
                            getString(R.string.you_need_to_choose_the_game_Application)
                        binding.tvWaning.visibility = View.VISIBLE
                    }
                }
            }
        }
    private val isUpdateCheckAppGaming: Unit
        get() {
            var isExitApp = false
            var isExitTime = false
            for (itemApp in listAutomation) {
                if (itemApp.typeEvent == Constant.APPS && itemApp.start) {
                    if (MethodUtils.packageIsGame(requireContext(), itemApp.packageName)) {
                        isExitApp = true
                    }
                } else if (itemApp.typeEvent == Constant.TIME && itemApp.start) {
                    isExitTime = true
                }
            }
            if (!isExitApp || !isExitTime) {
                isPresetGamingOrWork = true
                binding.tvWaning.text =
                    getString(R.string.you_need_to_choose_the_game_Application)
                binding.tvWaning.visibility = View.VISIBLE
            } else {
                isPresetGamingOrWork = false
            }
            if (isPresetGamingOrWork) {
                binding.switchFocus.setImageResource(R.drawable.ic_switch_off)
                //            //update preset current focus
                if (focusIOSs?.startCurrent == true) {
                    App.tinyDB.putString(Constant.FOCUS_START_OLD, "")
                    setFocusStart(null)
                }
                focusIOSs?.startAutoAppOpen = false
                focusIOSs?.startCurrent = false
                focusIOSs?.startAutoTime = false
                focusIOSs?.startAutoLocation = false
                viewModel.updateStartItemFocusIos(
                    focusIOSs?.startAutoAppOpen,
                    focusIOSs?.startCurrent,
                    focusIOSs?.startAutoTime,
                    focusIOSs?.startAutoLocation,
                    focusIOSs?.name
                )
            }
            viewModel.startHandFocus(requireContext(), focusIOSs?.name)
        }
    private val isCheckCountLocation: Unit
        get() {
            var isExitLocation = false
            var isExitTime = false
            for (itemWork in listAutomation) {
                if (itemWork.typeEvent == Constant.LOCATION && itemWork.start) {
                    isExitLocation = true
                } else if (itemWork.typeEvent == Constant.TIME && itemWork.start) {
                    isExitTime = true
                }
            }
            if (isExitLocation && isExitTime) {
                isPresetGamingOrWork = false
                binding.tvWaning.visibility = View.GONE
            } else {
                isPresetGamingOrWork = true
                binding.tvWaning.text = getString(R.string.you_need_to_choose_the_location)
                binding.tvWaning.visibility = View.VISIBLE
            }
        }

    override fun onResume() {
        super.onResume()
        if (PermissionUtils.checkPermissionReadContact()) {
            dialogPermissionAlertDialog?.dismiss()
        }
    }

    private fun initAdapter() {
        if (allowedPeopleAdapter == null) {
            allowedPeopleAdapter = AllowedPeopleAdapter()
        }
        if (allowedAppsAdapter == null) {
            allowedAppsAdapter = AllowedAppsAdapter()
        }
        if (automationAdapter == null) {
            automationAdapter = AutomationAdapter()
        }
        automationAdapter?.setListAutomation(object : IListenerAutomation {
            override fun delete(position: Int) {
                if (focusIOSs == null || position == RecyclerView.NO_POSITION || position >= listAutomation.size) {
                    return
                }
                when (listAutomation[position].typeEvent) {
                    Constant.APPS -> viewModel.deleteItemAppAutomation(
                        listAutomation[position].packageName, focusIOSs?.name
                    )

                    Constant.TIME -> viewModel.deleteItemTimeAutomation(
                        listAutomation[position].lastModify, focusIOSs?.name
                    )
                }
//                if (listAutomation[position].typeEvent == Constant.TIME) {
//                    turnOffTime(listAutomation[position])
//                }
                listAutomation.removeAt(position)
                automationAdapter?.setData(listAutomation, focusIOSs?.colorFocus)
                updateTurnOff()
            }

            override fun start(position: Int) {
                if (focusIOSs == null || position == RecyclerView.NO_POSITION || position >= listAutomation.size) {
                    return
                }
                listAutomation[position].start = true
                automationAdapter?.notifyItemChanged(position, listAutomation)
                updateItemAutomation(true, position)
                updateTurnOff()
            }

            override fun finish(position: Int) {
                if (focusIOSs == null || position == RecyclerView.NO_POSITION || position >= listAutomation.size) {
                    return
                }
                listAutomation[position].start = false
                automationAdapter?.notifyItemChanged(position, listAutomation)
                updateItemAutomation(false, position)
                updateTurnOff()
            }

            override fun edit(position: Int, name: String) {
                if (position == RecyclerView.NO_POSITION || position >= listAutomation.size) {
                    return
                }
                when (name) {
                    Constant.TIME -> (requireActivity() as MainActivity).navigate(
                        R.id.action_focusDetailFragment_to_editTimeFocusFragment,
                        R.id.focusDetailFragment
                    )

                    Constant.APPS -> if (!isAccessGranted(requireContext())) {
                        val dialogPermissionUsage = DialogPermissionUsage()
                        dialogPermissionUsage.navigateFragment = Constant.EDIT_AUTO_APP
                        dialogPermissionUsage.show(childFragmentManager, null)
                    } else {
                        Handler().postDelayed({
                            if (isAdded) {
                                (requireActivity() as MainActivity).navigate(
                                    R.id.action_focusDetailFragment_to_editAppFragment,
                                    R.id.focusDetailFragment
                                )
                            }
                        }, 100)
                    }
                }
                mainViewModel.editItemAutomationFocus.postValue(focusIOSs)
                mainViewModel.itemAutomationFocus.postValue(listAutomation[position])
            }
        })
        binding.rcPeople.itemAnimator = null
        binding.rcApp.itemAnimator = null
        binding.rcTimeAuto.itemAnimator = null
        binding.rcPeople.adapter = allowedPeopleAdapter
        binding.rcApp.adapter = allowedAppsAdapter
        binding.rcTimeAuto.adapter = automationAdapter
    }

    private fun updateTurnOff() {
        when (focusIOSs?.name) {
            Constant.GAMING -> isUpdateCheckAppGaming
        }
        //        App.ins.setIsResetLocation(true);
        App.itemNextTimeAuto = null
        //        App.itemNextLocationAuto = null;
        App.ins.focusUtils?.sendActionFocus(Constant.TIME_CHANGE, "")
    }

    private fun updateItemAutomation(isStart: Boolean, position: Int) {
        when (listAutomation[position].typeEvent) {
            Constant.TIME -> {
                val currentTime = System.currentTimeMillis()
                val itemTurnOn = listAutomation[position]
                viewModel.updateStartAutomationTimeFocus(
                    isStart,
                    itemTurnOn.nameFocus,
                    currentTime,
                    itemTurnOn.lastModify
                )
                if (currentTime > listAutomation[position].timeEnd) {
                    if (TimeUtils.checkDayOfWeek(
                            itemTurnOn.monDay,
                            itemTurnOn.tueDay,
                            itemTurnOn.wedDay,
                            itemTurnOn.thuDay,
                            itemTurnOn.friDay,
                            itemTurnOn.satDay,
                            itemTurnOn.sunDay
                        )
                    ) {
                        viewModel.updateTimeRepeat(itemTurnOn)
                    }
                }
//                turnOffTime(listAutomation[position])
                itemTurnOn.lastModify = currentTime
            }

            Constant.APPS -> viewModel.updateStartAutomationAppFocus(
                isStart,
                listAutomation[position].nameFocus,
                listAutomation[position].packageName,
                System.currentTimeMillis()
            )
        }
    }

    //    private void turnOffItemAutomationLocation(ItemTurnOn item) {
    //        Location locationCurrent = new Location("");
    //        locationCurrent.setLatitude(item.getLatitude());
    //        locationCurrent.setLongitude(item.getLongitude());
    //        viewModel.updateStartItemFocusIos(
    //                focusIOSs.getStartAutoAppOpen(), focusIOSs.getStartCurrent(), false,
    //                focusIOSs.getStartAutoTime(), focusIOSs.getName());
    //        if (App.locationCurrent != null) {
    //            if (locationCurrent.distanceTo(App.locationCurrent) < 1000) {
    //                focusIOSs.setStartAutoLocation(false);
    //                for (FocusIOS it : App.presetFocusList
    //                ) {
    //                    if (it.getName().equals(focusIOSs.getName())) {
    //                        if (App.tinyDB.getString(Constant.FOCUS_START_OLD).equals(it.getName())) {
    //                            binding.switchFocus.setImageResource(R.drawable.ic_switch_on);
    //                        }
    //                        it.setStartAutoLocation(false);
    //                        break;
    //                    }
    //                }
    //
    //            }
    //
    //        }
    //    }
    private fun turnOffTime(item: ItemTurnOn) {
        val currentTime = System.currentTimeMillis()
        val timeEnd = item.timeEnd
        val timeStart = item.timeStart
        var itTimeStart: ItemTurnOn? = null
        var isTurnOff = false
        val listTimeStart: MutableList<ItemTurnOn> = ArrayList()
//        App.timeAutoList = App.ins.timeRepository.listTimeFocus

        for (itemTime in App.timeAutoList) {
            if (currentTime >= itemTime.timeStart && currentTime < itemTime.timeEnd) {
                listTimeStart.add(itemTime)
            }
        }
        if (listTimeStart.size > 0) {
            val lastModify = listTimeStart[0].lastModify
            itTimeStart = listTimeStart[0]
            for (it in listTimeStart) {
                if (lastModify < it.lastModify) {
                    itTimeStart = it
                    break
                }
            }
        }
        if (itTimeStart != null) {
            if (itTimeStart.nameFocus == focusIOSs?.name) {
                isTurnOff = true
            }
        }
        viewModel.updateStartItemFocusIos(
            focusIOSs?.startAutoAppOpen,
            focusIOSs?.startCurrent,
            focusIOSs?.startAutoLocation,
            false,
            focusIOSs?.name
        )
        if (currentTime in timeStart until timeEnd) {
            if (!isTurnOff) {
                focusIOSs?.startAutoTime = false
                for (it in App.presetFocusList) {
                    if (it.name == focusIOSs?.name) {
                        if (App.tinyDB.getString(Constant.FOCUS_START_OLD) == it.name) {
                            binding.switchFocus.setImageResource(R.drawable.ic_switch_on)
                        } else {
                            binding.switchFocus.setImageResource(R.drawable.ic_switch_off)
                        }
                        it.startAutoTime = false
                        break
                    }
                }
            }
        }
    }

    override fun onPermissionGranted() {}
    private fun checkFocus() {
        if (focusIOSs?.name == Constant.DO_NOT_DISTURB) {
            binding.tvDelete.visibility = View.GONE
        } else {
            binding.tvDelete.visibility = View.VISIBLE
        }
        if (focusIOSs?.default == true) {
            binding.imEdit.visibility = View.INVISIBLE
        } else {
            binding.imEdit.visibility = View.VISIBLE
            binding.imEdit.setColorFilter(Color.parseColor(focusIOSs?.colorFocus))
        }
    }

    //    private void initTextViewWarring() {
    //        if (focusIOSs.getName().equals(Constant.GAMING)) {
    //            binding.tvWaning.setText(getString(R.string.you_need_to_choose_the_game_Application));
    //            binding.tvWaning.setVisibility(View.VISIBLE);
    ////        } else if (focusIOSs.getName().equals(Constant.WORK)) {
    ////            binding.tvWaning.setText(getString(R.string.you_need_to_choose_the_location));
    ////            binding.tvWaning.setVisibility(View.VISIBLE);
    //        } else {
    //            binding.tvWaning.setVisibility(View.GONE);
    //        }
    //    }
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
            Constant.UPDATE_TIME_CHANGE, Constant.UPDATE_APP_CHANGE, Constant.UPDATE_VIEW_FROM_CONTROL -> try {
                updateFocusOnOff()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            Constant.CONTACT_CHANGE -> {
                for (itemPeople in listAllowedPeople) {
                    if (itemPeople.contactId == messageEvent.stringValue) {
                        itemPeople.name = messageEvent.name
                        itemPeople.phone = messageEvent.phone
                        itemPeople.image = messageEvent.image
                        break
                    }
                }
                setUIListAllowedPeople(listAllowedPeople)
            }

            Constant.CONTACT_DELETE -> {
                for (itemPeople in listAllowedPeople) {
                    if (itemPeople.contactId == messageEvent.stringValue) {
                        listAllowedPeople.remove(itemPeople)
                        break
                    }
                }
                setUIListAllowedPeople(listAllowedPeople)
            }

            Constant.PACKAGE_APP_REMOVE -> {
                listAllowedApps = AppUtils.updatePackageRemoveAllowedApp(
                    messageEvent.stringValue,
                    listAllowedApps
                )
                setUIListAllowedApps(listAllowedApps)
                listAutomation = AppUtils.updatePackageRemoveAllowedAppAuto(
                    messageEvent.stringValue,
                    listAutomation
                )
                automationAdapter?.setData(listAutomation, focusIOSs?.colorFocus)
                when (focusIOSs?.name) {
                    Constant.GAMING -> updateFocusOnOff()
                }
                App.itemNextTimeAuto = null
            }
        }
    }

    private fun updateFocusOnOff() {
        for (fios in App.presetFocusList) {
            if (fios.name == focusIOSs?.name) {
                focusIOSs?.startCurrent = fios.startCurrent
                focusIOSs?.startAutoAppOpen = fios.startAutoAppOpen
                focusIOSs?.startAutoLocation = fios.startAutoLocation
                focusIOSs?.startAutoTime = fios.startAutoTime
                break
            }
        }
        binding.switchFocus.post {
            if (isAdded) {
                if (focusIOSs?.startCurrent == true || focusIOSs?.startAutoAppOpen == true || focusIOSs?.startAutoLocation == true || focusIOSs?.startAutoTime == true) {
                    binding.switchFocus.setImageResource(R.drawable.ic_switch_on)
                    binding.tvWaning.visibility = View.GONE
                } else {
                    binding.switchFocus.setImageResource(R.drawable.ic_switch_off)
                    //                    initTextViewWarring();
                    isCheckAppGaming
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        onBackPressedCallback.remove()
    }

    private fun navigationPeopleFocus() {
        if (!App.isRegisterServiceContact) {
            checkPermissionContact()
        }
        (requireActivity() as MainActivity).navigate(
            R.id.action_focusDetailFragment_to_allowPeopleFragment,
            R.id.focusDetailFragment
        )
    }

    private fun hideDialog() {
        if (dialogPermissionAlertDialog != null && dialogPermissionAlertDialog?.dialog?.isShowing == true) {
            dialogPermissionAlertDialog?.dismissAllowingStateLoss()
        }
    }
}
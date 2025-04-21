package com.tapbi.spark.controlcenter.ui.main.focus.createfocus.allowedapps

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.adapter.AppAdapter
import com.tapbi.spark.controlcenter.adapter.AppSearchAdapter
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.common.models.MessageEvent
import com.tapbi.spark.controlcenter.data.model.FocusIOS
import com.tapbi.spark.controlcenter.data.model.ItemApp
import com.tapbi.spark.controlcenter.data.model.ItemPeople
import com.tapbi.spark.controlcenter.databinding.FragmentAllowedAppBinding
import com.tapbi.spark.controlcenter.ui.base.BaseBindingFragment
import com.tapbi.spark.controlcenter.ui.main.MainActivity
import com.tapbi.spark.controlcenter.utils.AppUtils
import com.tapbi.spark.controlcenter.utils.MethodUtils
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class CustomNewAllowedAppFragment :
    BaseBindingFragment<FragmentAllowedAppBinding, CustomNewAllowedAppViewModel>() {
    private val listApp: MutableList<ItemApp?> = ArrayList()
    private val listAppSearch: MutableList<ItemApp?> = ArrayList()
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                MethodUtils.hideKeyboard(requireActivity())
                (requireActivity() as MainActivity).navControllerMain.popBackStack(
                    R.id.customNewAllowedAppFragment, true
                )
            }
        }
    private var listAppInsert: MutableList<ItemApp> = mutableListOf()
    private var listAppStart: MutableList<String> = mutableListOf()
    private var listPeopleIStart: MutableList<ItemPeople> = mutableListOf()
    private var appAdapter: AppAdapter? = null
    private var appSearchAdapter: AppSearchAdapter? = null
    private var textQuery = ""
    private var focusios: FocusIOS? = null

    //    private AlertDialog dialogPermissionAlertDialog;
    //    private final ActivityResultLauncher<String[]> requestMultiplePermissions = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
    //        if (result.containsValue(true)) {
    //            mainViewModel.insertFocus(focusios);
    //            mainViewModel.itemFocusDetail.postValue(focusios);
    //            ((MainActivity) requireActivity()).navControllerMain.navigate(R.id.action_customNewAllowedAppFragment_to_focusDetailFragment);
    //        } else {
    //            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
    //                boolean b = MethodUtils.checkPermissionCallListener(getContext());
    //                if (getContext() != null && !b) {
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
    override fun getViewModel(): Class<CustomNewAllowedAppViewModel> {
        return CustomNewAllowedAppViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_allowed_app

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (activity != null) {
            requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        }
    }

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        initView()
        observerData()
        if (savedInstanceState != null) {
            focusios = Gson().fromJson(
                savedInstanceState.getString(Constant.ITEM_CUSTOM_ALLOWED_APP),
                object : TypeToken<FocusIOS?>() {}.type
            )
            listPeopleIStart = Gson().fromJson(
                savedInstanceState.getString(Constant.ITEM_CUSTOM_ALLOWED_PEOPLE_INSERT),
                object : TypeToken<List<ItemPeople?>?>() {}.type
            )
            listAppStart = Gson().fromJson(
                savedInstanceState.getString(Constant.ITEM_CUSTOM_ALLOWED_APP_START),
                object : TypeToken<List<String?>?>() {}.type
            )
            textQuery = savedInstanceState.getString(Constant.TEXT_QUERY_CUSTOM_ALLOWED_PEOPLE, "")
            mainViewModel.listItemPeopleStart.postValue(listPeopleIStart)
            mainViewModel.itemCreateAppFocusCurrent.postValue(focusios)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(Constant.ITEM_CUSTOM_ALLOWED_APP, Gson().toJson(focusios))
        outState.putString(
            Constant.ITEM_CUSTOM_ALLOWED_PEOPLE_INSERT, Gson().toJson(listPeopleIStart)
        )
        outState.putString(Constant.ITEM_CUSTOM_ALLOWED_APP_START, Gson().toJson(listAppStart))
        if (textQuery.isNotEmpty()) {
            outState.putString(Constant.TEXT_QUERY_CUSTOM_ALLOWED_APP, textQuery)
        }
    }


    private fun observerData() {
        mainViewModel.itemCreateAppFocusCurrent.observe(viewLifecycleOwner) { focusIOS: FocusIOS? ->
            if (focusIOS != null) {
                focusios = focusIOS
                binding.bgLoading.visibility = View.VISIBLE
                viewModel.getAllApp(requireContext())
                mainViewModel.itemCreateAppFocusCurrent.postValue(null)
            }
        }
        viewModel.listApp.observe(viewLifecycleOwner) { itemAppList: List<ItemApp?>? ->
            if (itemAppList != null) {
                listApp.clear()
                listApp.addAll(itemAppList)
                if (listAppStart.size > 0) {
                    for (itStart in listAppStart) {
                        for (itApp in listApp) {
                            if (itStart == itApp?.packageName) {
                                itApp.isStart = true
                                break
                            }
                        }
                    }
                    binding.viewClickAllow.tvAllowGone.visibility = View.INVISIBLE
                    binding.viewClickAllow.tvAllow.visibility = View.VISIBLE
                } else {
                    binding.viewClickAllow.tvAllowGone.visibility = View.VISIBLE
                    binding.viewClickAllow.tvAllow.visibility = View.INVISIBLE
                }
                appAdapter?.setData(listApp)
                countStartRemove()
                if (textQuery.isNotEmpty()) {
                    binding.searchView.setQuery(textQuery, false)
                }
                binding.bgLoading.visibility = View.GONE

            }
        }
        mainViewModel.listItemPeopleStart.observe(viewLifecycleOwner) { list ->
            if (list != null) {
                listPeopleIStart = list
                mainViewModel.listItemPeopleStart.postValue(null)
            }
        }
        mainViewModel.listItemAppStart.observe(viewLifecycleOwner) { strings ->
            if (strings != null) {
                listAppStart = strings.toMutableList()
                if (listAppStart.size > 0) {
                    binding.viewClickAllow.tvAllowGone.visibility = View.INVISIBLE
                    binding.viewClickAllow.tvAllow.visibility = View.VISIBLE
                } else {
                    binding.viewClickAllow.tvAllowGone.visibility = View.VISIBLE
                    binding.viewClickAllow.tvAllow.visibility = View.INVISIBLE
                }
                mainViewModel.listItemAppStart.postValue(null)
            }
        }
        viewModel.insertAllowPeople.observe(viewLifecycleOwner) { aBoolean: Boolean? ->
            if (aBoolean == true) {
                if (listAppStart.size > 0) {
                    for (itemStart in listAppStart) {
                        for (itemApp in listApp) {
                            if (itemStart == itemApp?.packageName) {
                                listAppInsert.add(itemApp)
                            }
                        }
                    }
                    viewModel.insertItemAllowedApp(listAppInsert, focusios?.name)
                } else {
                    navigateIOS()
                }
            }
        }
        viewModel.deleteAllowedAppsLiveData.observe(viewLifecycleOwner) { aBoolean: Boolean? ->
            if (aBoolean == true) {
                if (listAppStart.size > 0) {
                    for (itemStart in listAppStart) {
                        for (itemApp in listApp) {
                            if (itemStart == itemApp?.packageName) {
                                listAppInsert.add(itemApp)
                            }
                        }
                    }
                    mainViewModel.listItemAllowedApps.postValue(listAppInsert)
                }
            }
        }
        viewModel.insertAllowApp.observe(viewLifecycleOwner) { aBoolean: Boolean? ->
            if (aBoolean == true) {
                navigateIOS()
            }
        }
    }

    private fun navigateIOS() {
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
//                requestRole();
//            } else {
//        if (!PermissionUtils.INSTANCE.checkPermissionCallListener(requireContext())) {
//            requestPermissionPhone(requestMultiplePermissions);
//        } else {
        mainViewModel.insertFocus(focusios)
        mainViewModel.itemFocusDetail.postValue(focusios)
        (requireActivity() as MainActivity).navigate(
            R.id.action_customNewAllowedAppFragment_to_focusDetailFragment,
            R.id.customNewAllowedAppFragment
        )
        //        }
//            }
    }

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    //    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
    //            new ActivityResultContracts.StartActivityForResult(),
    //            result -> {
    //                if (result.getResultCode() == Activity.RESULT_OK) {
    //                    // There are no request codes
    //                    mainViewModel.insertFocus(focusios);
    //                    mainViewModel.itemFocusDetail.postValue(focusios);
    //                    ((MainActivity) requireActivity()).navControllerMain.navigate(R.id.action_customNewAllowedAppFragment_to_focusDetailFragment);
    //                }
    //            });
    //    public void requestRole() {
    //        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q && getContext() != null) {
    //            RoleManager roleManager = (RoleManager) getContext().getSystemService(Context.ROLE_SERVICE);
    //            Intent intent = roleManager.createRequestRoleIntent("android.app.role.CALL_SCREENING");
    //            someActivityResultLauncher.launch(intent);
    //        }
    //    }
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
    private fun initView() {
        setUpPaddingStatusBar(binding.layoutAllowApp)
        (requireActivity() as MainActivity).setColorNavigation(R.color.color_F2F2F6)
        binding.viewClickAllow.root.visibility = View.VISIBLE
        initAdapter()
        initListener()
    }

    private fun initListener() {
        backPress()
        initSearchView()
        hideKeyBoardScrollRV(binding.rvApp)
        hideKeyBoardScrollRV(binding.rvAppSearch)
        binding.viewClickAllow.tvAllow.setOnClickListener { v: View? ->
            ViewHelper.preventTwoClick(v)
            viewModel.insertItemAllowedPeople(listPeopleIStart, focusios?.name)
        }
        binding.viewClickAllow.tvAllowNone.setOnClickListener { v: View? ->
            ViewHelper.preventTwoClick(v)
            listAppStart.clear()
            viewModel.insertItemAllowedPeople(listPeopleIStart, focusios?.name)
            countStartRemove()
        }
        binding.tvRemove.setOnClickListener { v: View? ->
            ViewHelper.preventTwoClick(v)
            for (itemApp in listApp) {
                itemApp?.isStart = false
            }
            appAdapter?.setData(listApp)
            for (itemSearch in listAppSearch) {
                itemSearch?.isStart = false
            }
            appSearchAdapter?.setData(listAppSearch)
            listAppStart.clear()
            countStartRemove()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun countStartRemove() {
        var count = 0
        for (itApp in listApp) {
            if (itApp?.isStart ==true) {
                count++
            }
        }
        binding.tvRemove.text = getString(R.string.remove_allow) + " " + count + " )"
    }

    private fun initSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                textQuery = newText
                if (newText.isEmpty()) {
                    appAdapter?.setData(listApp)
                    showViewApp(View.GONE, View.VISIBLE, View.GONE)
                } else {
                    listAppSearch.clear()
                    for (itemA in listApp) {
                        if (itemA?.name?.uppercase()?.contains(newText.uppercase()) == true) {
                            listAppSearch.add(itemA)
                        }
                    }
                    if (listAppSearch.size > 0) {
                        appSearchAdapter?.setData(listAppSearch)
                        showViewApp(View.VISIBLE, View.GONE, View.GONE)
                    } else {
                        showViewApp(View.GONE, View.GONE, View.VISIBLE)
                    }
                }
                return true
            }
        })
    }

    private fun showViewApp(v1: Int, v2: Int, v3: Int) {
        binding.rvAppSearch.visibility = v1
        binding.rvApp.visibility = v2
        binding.tvNoData.visibility = v3
    }

    private fun backPress() {
        binding.imBack.setOnClickListener { requireActivity().onBackPressed() }
    }

    private fun initAdapter() {
        appAdapter = AppAdapter()
        binding.rvApp.adapter = appAdapter
        appAdapter?.setListener { position: Int, isStart: Boolean ->
            onClickSwitch(listApp, position, isStart)
            appAdapter?.notifyItemChanged(position, listApp)
        }
        appSearchAdapter = AppSearchAdapter()
        binding.rvAppSearch.adapter = appSearchAdapter
        appSearchAdapter?.setListener { position: Int, isStart: Boolean ->
            onClickSwitch(listAppSearch, position, isStart)
            appSearchAdapter?.notifyItemChanged(position, listAppSearch)
        }
    }

    private fun onClickSwitch(list: List<ItemApp?>, position: Int, isStart: Boolean) {
        list[position]?.isStart = !isStart
        if (list[position]?.isStart == true) {
            list[position]?.packageName?.let { listAppStart.add(it) }
        } else {
            listAppStart.remove(list[position]?.packageName)
        }
        countStartRemove()
        mainViewModel.listItemAppStart.postValue(listAppStart)
    }

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
            Constant.PACKAGE_APP_REMOVE -> {
                listApp.clear()
                listApp.addAll(
                    AppUtils.updatePackageRemoveAllowedApp(
                        messageEvent.stringValue, listApp
                    )
                )
                listAppSearch.addAll(
                    AppUtils.updatePackageRemoveAllowedApp(
                        messageEvent.stringValue, listAppSearch
                    )
                )
                appAdapter?.setData(listApp)
                appSearchAdapter?.setData(listAppSearch)
                listAppInsert =
                    AppUtils.updatePackageRemoveAllowedApp(messageEvent.stringValue, listAppInsert)
                viewModel.deleteAllowedApps(focusios?.name)
            }

            Constant.PACKAGE_APP_ADD -> {
                val nameApp = AppUtils.getNameAppFromPackage(messageEvent.stringValue)
                if (nameApp != "(unknown)") {
                    listApp.add(
                        ItemApp(
                            AppUtils.getNameAppFromPackage(messageEvent.stringValue),
                            messageEvent.stringValue,
                            "",
                            false
                        )
                    )
                    appAdapter?.setData(listApp)
                    listAppInsert = AppUtils.updatePackageRemoveAllowedApp(
                        messageEvent.stringValue, listAppInsert
                    )
                    viewModel.deleteAllowedApps(focusios?.name)
                }
            }
        }
    }

    override fun onPermissionGranted() {}
    override fun onDestroyView() {
        super.onDestroyView()
        binding.bgLoading.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        onBackPressedCallback.remove()
    }
}
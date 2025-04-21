package com.tapbi.spark.controlcenter.ui.main.focus.allowapp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.adapter.AppAdapter
import com.tapbi.spark.controlcenter.adapter.AppSearchAdapter
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.common.models.MessageEvent
import com.tapbi.spark.controlcenter.data.model.FocusIOS
import com.tapbi.spark.controlcenter.data.model.ItemApp
import com.tapbi.spark.controlcenter.databinding.FragmentAllowedAppBinding
import com.tapbi.spark.controlcenter.ui.base.BaseBindingFragment
import com.tapbi.spark.controlcenter.ui.main.MainActivity
import com.tapbi.spark.controlcenter.utils.AppUtils
import com.tapbi.spark.controlcenter.utils.MethodUtils
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AllowAppFragment : BaseBindingFragment<FragmentAllowedAppBinding, AllowAppViewModel>() {
    private val listAppSearch: MutableList<ItemApp> = ArrayList()
    private var listApps: MutableList<ItemApp> = ArrayList()
    private var listAllowedApps: MutableList<ItemApp> = ArrayList()
    private var appAdapter: AppAdapter? = null
    private var appSearchAdapter: AppSearchAdapter? = null
    private var focusiOS: FocusIOS? = null
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                mainViewModel.itemFocusDetail.postValue(focusiOS)
                (requireActivity() as MainActivity).navControllerMain.popBackStack(
                    R.id.allowAppFragment,
                    true
                )
            }
        }
    private var textQuery: String? = ""
    override fun getViewModel(): Class<AllowAppViewModel> {
        return AllowAppViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_allowed_app

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        setUpPaddingStatusBar(binding.layoutAllowApp)
        (requireActivity() as MainActivity).setColorNavigation(R.color.color_F2F2F6)
        if (savedInstanceState?.getString(Constant.ITEM_FOCUS_APP) != null) {
            focusiOS = Gson().fromJson(
                savedInstanceState.getString(Constant.ITEM_FOCUS_APP),
                object : TypeToken<FocusIOS?>() {}.type
            )
            textQuery = savedInstanceState.getString(Constant.TEXT_QUERY_ALLOWED_APP, "")
            mainViewModel.itemFocusCurrentApp.postValue(focusiOS)
        }
        initView()
        observerData()
    }

    private fun initView() {
        initAdapter()
        initListener()
        initSearchView()
    }

    private fun initSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                textQuery = newText
                if (newText.isEmpty()) {
                    appAdapter?.setData(listApps)
                    showViewApp(View.GONE, View.VISIBLE, View.GONE)
                } else {
                    listAppSearch.clear()
                    for (itemA in listApps) {
                        if (itemA.name.uppercase().contains(newText.uppercase())) {
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

    private fun initListener() {
        hideKeyBoardScrollRV(binding.rvApp)
        hideKeyBoardScrollRV(binding.rvAppSearch)
        backPress()
        binding.tvRemove.setOnClickListener { v: View? ->
            ViewHelper.preventTwoClick(v)
            focusiOS?.let {
                for (itApp in listApps) {
                    itApp.isStart = false
                }
                appAdapter?.setData(listApps)
                if (listAppSearch.size > 0) {
                    for (itemSearch in listAppSearch) {
                        itemSearch.isStart = false
                    }
                    appSearchAdapter?.setData(listAppSearch)
                }
                listAllowedApps.clear()
                countStartRemove()
                viewModel.deleteAllowedApps(it.name)
            }

        }
    }

    private fun backPress() {
        binding.imBack.setOnClickListener { v: View? ->
            ViewHelper.preventTwoClick(v)
            mainViewModel.itemFocusDetail.postValue(focusiOS)
            (requireActivity() as MainActivity).navControllerMain.popBackStack(
                R.id.allowAppFragment,
                true
            )
        }
    }

    private fun initAdapter() {
        appAdapter = AppAdapter()
        binding.rvApp.adapter = appAdapter
        appSearchAdapter = AppSearchAdapter()
        binding.rvAppSearch.adapter = appSearchAdapter
        appAdapter?.setListener { position: Int, isStart: Boolean ->
            onCLickSwitchApp(listApps, position, isStart)
            appAdapter?.notifyItemChanged(position, listApps[position])
        }
        appSearchAdapter?.setListener { position: Int, isStart: Boolean ->
            onCLickSwitchApp(listAppSearch, position, isStart)
            appSearchAdapter?.notifyItemChanged(position, listAppSearch[position])
        }
    }

    private fun onCLickSwitchApp(itemAppList: List<ItemApp>, position: Int, isStart: Boolean) {
        MethodUtils.hideSoftKeyboard(requireActivity())
        itemAppList[position].isStart = !isStart
        //        appAdapter.notifyItemChanged(position, listApps);
        if (itemAppList[position].isStart) {
            itemAppList[position].nameFocus = focusiOS?.name
            listAllowedApps.add(itemAppList[position])
        } else {
            for (itAllowedApp in listAllowedApps) {
                if (itAllowedApp.packageName == itemAppList[position].packageName) {
                    listAllowedApps.remove(itAllowedApp)
                    break
                }
            }
        }
        countStartRemove()
        viewModel.deleteAllowedApps(focusiOS?.name)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (focusiOS != null) {
            outState.putString(Constant.ITEM_FOCUS_APP, Gson().toJson(focusiOS))
        }
        if (!textQuery.isNullOrEmpty()) {
            outState.putString(Constant.TEXT_QUERY_ALLOWED_APP, textQuery)
        }
    }

    private fun observerData() {
        mainViewModel.itemFocusCurrentApp.observe(viewLifecycleOwner) { focusIOS: FocusIOS? ->
            if (focusIOS != null) {
                focusiOS = focusIOS
                if (App.listAppDevice.size == 0) {
                    binding.bgLoading.visibility = View.VISIBLE
                } else {
                    for (itemApp in App.listAppDevice) {
                        itemApp.isStart = false
                    }
                }
                mainViewModel.getAllowedAppsByName(focusIOS.name)
                mainViewModel.itemFocusCurrentApp.postValue(null)
            }
        }
        viewModel.listAppLiveData.observe(viewLifecycleOwner) { itemApps ->
            if (itemApps != null) {
                listApps = itemApps.toMutableList()
                for (itAllowedApps in listAllowedApps) {
                    for (itApp in listApps) {
                        if (itApp.packageName == itAllowedApps.packageName) {
                            itApp.isStart = true
                        }
                    }
                }
                appAdapter?.setData(itemApps)
                countStartRemove()
                binding.bgLoading.visibility = View.GONE
                if (textQuery != null && !textQuery.isNullOrEmpty()) {
                    binding.searchView.setQuery(textQuery, true)
                }
            }
        }
        mainViewModel.listItemAllowedApps.observe(viewLifecycleOwner) {
            if (it != null) {
                listAllowedApps = it.toMutableList()
                if (App.listAppDevice.size > 0) {
                    viewModel.listAppLiveData.postValue(App.listAppDevice)
                } else {
                    viewModel.getAllApp(requireContext())
                }
                mainViewModel.listItemAllowedApps.postValue(null)
            }
        }


        viewModel.deleteAllowedAppsLiveData.observe(viewLifecycleOwner) { aBoolean: Boolean ->
            if (aBoolean) {
                if (listAllowedApps.size > 0) {
                    for (it in listAllowedApps) {
                        if (it.nameFocus == focusiOS?.name) {
                            viewModel.insertItemApps(it)
                        }
                    }
                    //                    mainViewModel.listItemAllowedApps.postValue(listAllowedApps);
                }
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun countStartRemove() {
        var count = 0
        for (itApp in listApps) {
            if (itApp.isStart) {
                count++
            }
        }
        binding.tvRemove.text = getString(R.string.remove_allow) + " " + count + " )"
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
                listApps =
                    AppUtils.updatePackageRemoveAllowedApp(messageEvent.stringValue, listApps)
                listAppSearch.addAll(
                    AppUtils.updatePackageRemoveAllowedApp(
                        messageEvent.stringValue,
                        listAppSearch
                    )
                )
                appAdapter?.setData(listApps)
                appSearchAdapter?.setData(listAppSearch)
                listAllowedApps = AppUtils.updatePackageRemoveAllowedApp(
                    messageEvent.stringValue,
                    listAllowedApps
                )
                viewModel.deleteAllowedApps(focusiOS?.name)
                countStartRemove()
            }

            Constant.PACKAGE_APP_ADD -> {
                val nameApp = AppUtils.getNameAppFromPackage(messageEvent.stringValue)
                if (nameApp != "(unknown)") {
                    listApps.add(ItemApp(nameApp, messageEvent.stringValue, "", false))
                    appAdapter?.setData(listApps)
                    listAllowedApps = AppUtils.updatePackageRemoveAllowedApp(
                        messageEvent.stringValue,
                        listAllowedApps
                    )
                    viewModel.deleteAllowedApps(focusiOS?.name)
                    countStartRemove()
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
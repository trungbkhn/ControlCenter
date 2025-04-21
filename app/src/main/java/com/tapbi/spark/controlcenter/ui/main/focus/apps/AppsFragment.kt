package com.tapbi.spark.controlcenter.ui.main.focus.apps

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
import com.tapbi.spark.controlcenter.data.model.ItemTurnOn
import com.tapbi.spark.controlcenter.databinding.FragmentAppsBinding
import com.tapbi.spark.controlcenter.ui.base.BaseBindingFragment
import com.tapbi.spark.controlcenter.ui.main.MainActivity
import com.tapbi.spark.controlcenter.utils.AppUtils
import com.tapbi.spark.controlcenter.utils.MethodUtils
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AppsFragment : BaseBindingFragment<FragmentAppsBinding, AppsViewModel>() {
    private val listAutomation: MutableList<ItemTurnOn> = ArrayList()
    private val listAppIns: MutableList<ItemApp> = ArrayList()
    private val listAppSearch: MutableList<ItemApp> = ArrayList()
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                (requireActivity() as MainActivity).navControllerMain.popBackStack(
                    R.id.appsFragment,
                    true
                )
            }
        }
    private var textQuery = ""
    private var appAdapter: AppAdapter? = null
    private var appSearchAdapter: AppSearchAdapter? = null
    private var focusIOS: FocusIOS? = null
    private var listAppStart: MutableList<String> = ArrayList()
    override fun getViewModel(): Class<AppsViewModel> {
        return AppsViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_apps

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (activity != null) {
            requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        }
    }

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        if (savedInstanceState?.getString(Constant.ITEM_FOCUS_NEW_AUTOMATION_APP) != null) {
            focusIOS = Gson().fromJson(
                savedInstanceState.getString(Constant.ITEM_FOCUS_NEW_AUTOMATION_APP),
                object : TypeToken<FocusIOS?>() {}.type
            )
            listAppStart = Gson().fromJson(
                savedInstanceState.getString(Constant.ITEM_APP_START),
                object : TypeToken<List<String?>?>() {}.type
            )
            mainViewModel.itemFocusNewAutomationApps.postValue(focusIOS)
            if (listAppStart.size > 0) {
                binding.tvDone.visibility = View.VISIBLE
            }
        }
        initView()
        observerData()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (focusIOS != null) {
            outState.putString(Constant.ITEM_FOCUS_NEW_AUTOMATION_APP, Gson().toJson(focusIOS))
            outState.putString(Constant.ITEM_APP_START, Gson().toJson(listAppStart))
        }
    }

    private fun observerData() {
        mainViewModel.itemFocusNewAutomationApps.observe(viewLifecycleOwner) { focusIOS: FocusIOS? ->
            if (focusIOS != null) {
                this.focusIOS = focusIOS
                binding.bgLoading.visibility = View.VISIBLE
                mainViewModel.getListAutomationByFocus(focusIOS.name)
                if (focusIOS.name == Constant.GAMING) {
                    binding.tvTitle.text = getString(R.string.game)
                }
                mainViewModel.itemFocusNewAutomationApps.postValue(null)
            }
        }
        mainViewModel.listAutomationMutableLiveData.observe(viewLifecycleOwner) { turnOnList: List<ItemTurnOn>? ->
            if (turnOnList != null) {
                listAutomation.clear()
                listAutomation.addAll(turnOnList)
                viewModel.getAllApp(requireContext())
                mainViewModel.listAutomationMutableLiveData.postValue(null)
            }
        }
        viewModel.listAppLiveData.observe(viewLifecycleOwner) { itemApps ->
            if (itemApps != null && focusIOS != null) {
                listAppIns.clear()
                listAppIns.addAll(itemApps)

                // Create a set for listAutomation package names for fast lookup
                val automationPackages = listAutomation.map { it.packageName }.toSet()
                listAppIns.removeAll { it.packageName in automationPackages }

                // If listAppStart is not empty, process it
                if (listAppStart.isNotEmpty()) {
                    val startPackages = listAppStart.toSet()
                    listAppIns.forEach { itAppIns ->
                        if (itAppIns.packageName in startPackages) {
                            itAppIns.isStart = true
                            listAutomation.add(
                                ItemTurnOn(
                                    focusIOS?.name,
                                    true,
                                    false,
                                    -1,
                                    -1,
                                    false,
                                    false,
                                    false,
                                    false,
                                    false,
                                    false,
                                    false,
                                    "",
                                    0.0,
                                    0.0,
                                    itAppIns.packageName,
                                    itAppIns.name,
                                    Constant.APPS,
                                    System.currentTimeMillis()
                                )
                            )
                        }
                    }
                }

                // Filter games if focusIOS is Constant.GAMING
                if (focusIOS?.name == Constant.GAMING) {
                    listAppIns.retainAll {
                        MethodUtils.packageIsGame(requireContext(), it.packageName)
                    }
                }

                // Update the UI based on the listAppIns size
                if (listAppIns.isNotEmpty()) {
                    appAdapter?.setData(listAppIns)
                    binding.tvNoData.visibility = View.GONE
                } else {
                    binding.tvNoData.visibility = View.VISIBLE
                }
            } else {
                binding.tvNoData.visibility = View.VISIBLE
            }

            binding.bgLoading.visibility = View.GONE
        }
        viewModel.deleteAutomationFocus.observe(viewLifecycleOwner) { aBoolean: Boolean? ->
            if (aBoolean ==true && focusIOS != null) {
                for (it in listAutomation) {
                    if (it.nameFocus == focusIOS?.name) {
                        viewModel.insertAppAutomation(it)
                    }
                }
                mainViewModel.itemFocusDetail.postValue(focusIOS)
                //                ((MainActivity) requireActivity()).navControllerMain.navigate(R.id.action_appsFragment_to_focusDetailFragment);
                (requireActivity() as MainActivity).navigate(
                    R.id.action_appsFragment_to_focusDetailFragment,
                    R.id.appsFragment
                )
            }
        }
    }

    private fun initView() {
        setUpPaddingStatusBar(binding.layoutApps)
        (requireActivity() as MainActivity).setColorNavigation(R.color.color_F2F2F6)
        initAdapter()
        initListener()
    }

    private fun initListener() {
        hideKeyBoardScrollRV(binding.rvAppSearch)
        hideKeyBoardScrollRV(binding.rvApp)
        backPress()
        binding.tvDone.setOnClickListener { v: View? ->
            ViewHelper.preventTwoClick(v)
            focusIOS?.let {
                viewModel.deleteAllItemAutomation(it.name)
            }
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                textQuery = newText
                if (newText.isEmpty()) {
                    appAdapter?.setData(listAppIns)
                    showViewApp(View.GONE, View.VISIBLE, View.GONE)
                } else {
                    listAppSearch.clear()
                    for (itemA in listAppIns) {
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

    private fun backPress() {
        binding.imBack.setOnClickListener { v: View? ->
            ViewHelper.preventTwoClick(v)
            requireActivity().onBackPressed()
        }
    }

    private fun showViewApp(v1: Int, v2: Int, v3: Int) {
        binding.rvAppSearch.visibility = v1
        binding.rvApp.visibility = v2
        binding.tvNoData.visibility = v3
    }

    private fun initAdapter() {
        appAdapter = AppAdapter()
        binding.rvApp.adapter = appAdapter
        appAdapter?.setListener { position: Int, isStart: Boolean ->
            if (focusIOS == null) {
                return@setListener
            }
            listAppIns[position].isStart = !isStart
            appAdapter?.notifyItemChanged(position, listAppIns)
            updateAppStart(listAppIns, position)
        }
        appSearchAdapter = AppSearchAdapter()
        binding.rvAppSearch.adapter = appSearchAdapter
        appSearchAdapter?.setListener { position: Int, isStart: Boolean ->
            if (focusIOS == null) {
                return@setListener
            }
            listAppSearch[position].isStart = !isStart
            appSearchAdapter?.notifyItemChanged(position, listAppSearch)
            updateAppStart(listAppSearch, position)
        }
    }

    override fun onPermissionGranted() {}
    private fun updateAppStart(list: List<ItemApp>, position: Int) {
        if (list[position].isStart) {
            list[position].nameFocus = focusIOS?.name
            listAutomation.add(
                ItemTurnOn(
                    focusIOS?.name,
                    true,
                    false,
                    -1,
                    -1,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    "",
                    0.0,
                    0.0,
                    list[position]
                        .packageName,
                    list[position].name,
                    Constant.APPS,
                    System.currentTimeMillis()
                )
            )
            listAppStart.add(list[position].packageName)
        } else {
            for (itemApp in listAutomation) {
                if (itemApp.packageName == list[position].packageName) {
                    listAutomation.remove(itemApp)
                    listAppStart.remove(list[position].packageName)
                    break
                }
            }
        }
        binding.tvDone.visibility = View.VISIBLE
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: MessageEvent) {
        when (event.typeEvent) {
            Constant.PACKAGE_APP_REMOVE -> {
                val listAppTemp =
                    AppUtils.updatePackageRemoveAllowedApp(event.stringValue, listAppIns)
                val listAppSearchTemp =
                    AppUtils.updatePackageRemoveAllowedApp(event.stringValue, listAppSearch)
                val listAutomationTemp =
                    AppUtils.updatePackageRemoveAllowedAppAuto(event.stringValue, listAutomation)
                listAutomation.clear()
                listAutomation.addAll(listAutomationTemp)
                listAppIns.clear()
                listAppSearch.clear()
                listAppIns.addAll(listAppTemp)
                listAppSearch.addAll(listAppSearchTemp)
                if (textQuery.isEmpty()) {
                    if (listAppIns.size > 0) {
                        appAdapter?.setData(listAppIns)
                        binding.tvNoData.visibility = View.GONE
                    } else {
                        binding.tvNoData.visibility = View.VISIBLE
                    }
                } else {
                    if (listAppSearch.size > 0) {
                        appSearchAdapter?.setData(listAppSearch)
                        binding.tvNoData.visibility = View.GONE
                    } else {
                        binding.tvNoData.visibility = View.VISIBLE
                    }
                }
            }

            Constant.PACKAGE_APP_ADD -> {
                val itemApp = ItemApp(
                    AppUtils.getNameAppFromPackage(event.stringValue),
                    event.stringValue,
                    "",
                    false
                )
                itemApp.iconApp =
                    MethodUtils.getIconFromPackageName(requireContext(), event.stringValue)
                listAppIns.add(itemApp)
                appAdapter?.setData(listAppIns)
                //                    listApp.add(ItemApp(AppUtils.getNameAppFromPackage(it), it, false, ""))
                if (listAppIns.size > 0) {
                    binding.tvNoData.visibility = View.GONE
                } else {
                    binding.tvNoData.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        onBackPressedCallback.remove()
    }

    override fun onDestroyView() {
        super.onDestroyView()

    }
}
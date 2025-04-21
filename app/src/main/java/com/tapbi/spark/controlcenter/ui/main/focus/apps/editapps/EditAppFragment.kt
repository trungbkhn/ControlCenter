package com.tapbi.spark.controlcenter.ui.main.focus.apps.editapps

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.adapter.EditAppAdapter
import com.tapbi.spark.controlcenter.adapter.EditSearchAppAdapter
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.data.model.FocusIOS
import com.tapbi.spark.controlcenter.data.model.ItemApp
import com.tapbi.spark.controlcenter.data.model.ItemTurnOn
import com.tapbi.spark.controlcenter.databinding.FragmentAppsBinding
import com.tapbi.spark.controlcenter.ui.base.BaseBindingFragment
import com.tapbi.spark.controlcenter.ui.main.MainActivity
import com.tapbi.spark.controlcenter.utils.MethodUtils
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper

class EditAppFragment : BaseBindingFragment<FragmentAppsBinding, EditAppsViewModel>() {
    private var focus: FocusIOS? = null
    private val listApp: MutableList<ItemApp?> = ArrayList()
    private val listAppSearch: MutableList<ItemApp?> = ArrayList()
    private val listAutomation: MutableList<ItemTurnOn> = ArrayList()
    private var itemAppAdapter: EditAppAdapter? = null
    private var itemSearchAppAdapter: EditSearchAppAdapter? = null
    private var positionApp = -1
    private var positionAppSearch = -1
    private var nameApp = ""
    private var nameAppOld = ""
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                mainViewModel.itemFocusDetail.postValue(focus)
                (requireActivity() as MainActivity).navControllerMain.popBackStack(
                    R.id.editAppFragment,
                    true
                )
            }
        }

    override fun getViewModel(): Class<EditAppsViewModel> {
        return EditAppsViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_apps

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        initView()
        observerData()
    }

    private fun observerData() {
        mainViewModel.itemAutomationFocus.observe(viewLifecycleOwner) { itemTurnOn: ItemTurnOn? ->
            if (itemTurnOn != null) {
                nameApp = itemTurnOn.packageName
                nameAppOld = itemTurnOn.packageName
            }
        }
        mainViewModel.editItemAutomationFocus.observe(viewLifecycleOwner) { focusIOS: FocusIOS? ->
            if (focusIOS != null) {
                focus = focusIOS
                if (focusIOS.name == Constant.GAMING) {
                    binding.tvTitle.text = requireContext().getString(R.string.game)
                }
                viewModel.getAllApp(requireContext())
                mainViewModel.getListAutomationByFocus(focusIOS.name)
            }
        }
        viewModel.listAppLiveData.observe(viewLifecycleOwner) { itemApps  ->
            if (itemApps != null) {
                listApp.clear()
                listApp.addAll(itemApps)
                listAppSearch.clear()
                listAppSearch.addAll(itemApps)
                if (focus?.name == Constant.GAMING) {
                    val listGame: MutableList<ItemApp?> = ArrayList()
                    for (item in listApp) {
                        if (MethodUtils.packageIsGame(requireContext(), item?.packageName)) {
                            listGame.add(item)
                        }
                    }
                    listApp.clear()
                    listApp.addAll(listGame)
                }
                itemAppAdapter?.setData(listApp)
                itemSearchAppAdapter?.setData(listAppSearch)
                for (i in listApp.indices) {
                    if (listApp[i]?.packageName == nameApp) {
                        positionApp = i
                        itemAppAdapter?.setIdApp(i)
                        break
                    }
                }
                if (listApp.size > 0) {
                    itemAppAdapter?.setData(listApp)
                    binding.tvNoData.visibility = View.GONE
                } else {
                    binding.tvNoData.visibility = View.VISIBLE
                }
            }
        }
        mainViewModel.listAutomationMutableLiveData.observe(viewLifecycleOwner) { turnOnList: List<ItemTurnOn>? ->
            if (turnOnList != null) {
                listAutomation.clear()
                listAutomation.addAll(turnOnList)
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
            var isExit = false
            for (item in listAutomation) {
                if (item.packageName == listApp[positionApp]?.packageName) {
                    isExit = true
                    break
                }
            }
            if (isExit) {
                toastText(R.string.app_already_available)
            } else {
                viewModel.updateAppAutomation(
                    focus?.name, listApp[positionApp]?.packageName, listApp[positionApp]?.name, System.currentTimeMillis(), nameAppOld
                )
                mainViewModel.itemFocusDetail.postValue(focus)
                (requireActivity() as MainActivity).navControllerMain.popBackStack(
                    R.id.editAppFragment,
                    true
                )
            }
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isEmpty()) {
                    itemAppAdapter?.setData(listApp)
                    showViewApp(View.GONE, View.VISIBLE, View.GONE)
                } else {
                    listAppSearch.clear()
                    for (itemA in listApp) {
                        if (itemA?.name?.uppercase()?.contains(newText.uppercase()) ==true) {
                            listAppSearch.add(itemA)
                        }
                    }
                    if (listAppSearch.size > 0) {
                        itemSearchAppAdapter?.setData(listAppSearch)
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
            mainViewModel.itemFocusDetail.postValue(focus)
            (requireActivity() as MainActivity).navControllerMain.popBackStack(
                R.id.editAppFragment,
                true
            )
        }
    }

    private fun showViewApp(v1: Int, v2: Int, v3: Int) {
        binding.rvAppSearch.visibility = v1
        binding.rvApp.visibility = v2
        binding.tvNoData.visibility = v3
    }

    private fun initAdapter() {
        itemAppAdapter = EditAppAdapter()
        itemAppAdapter?.setListener { position: Int, isStart: Boolean ->
            positionApp = position
            listApp[position]?.isStart = !isStart
            nameApp = listApp[position]?.packageName ?: ""
            binding.tvDone.visibility = View.VISIBLE
        }
        binding.rvApp.adapter = itemAppAdapter
        itemSearchAppAdapter = EditSearchAppAdapter()
        itemSearchAppAdapter?.setListener { position: Int, isStart: Boolean ->
            positionAppSearch = position
            nameApp = listAppSearch[position]?.packageName ?: ""
            listAppSearch[position]?.isStart = !isStart
            MethodUtils.hideKeyboard(requireActivity())
            binding.tvDone.visibility = View.VISIBLE
            for (j in listApp.indices) {
                if (listAppSearch[positionAppSearch]?.packageName == listApp[j]?.packageName) {
                    positionApp = j
                    break
                }
            }
            itemAppAdapter?.setIdApp(positionApp)
        }
        binding.rvAppSearch.adapter = itemSearchAppAdapter
    }

    override fun onPermissionGranted() {}
    override fun onDestroy() {
        super.onDestroy()
        onBackPressedCallback.remove()
    }
}
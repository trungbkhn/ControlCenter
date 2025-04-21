package com.tapbi.spark.controlcenter.ui.main.focus.editfocus

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.adapter.ColorAdapter
import com.tapbi.spark.controlcenter.adapter.IconAdapter
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.data.model.FocusIOS
import com.tapbi.spark.controlcenter.databinding.FragmentEditFocusBinding
import com.tapbi.spark.controlcenter.eventbus.EventUpdateFocus
import com.tapbi.spark.controlcenter.ui.base.BaseBindingFragment
import com.tapbi.spark.controlcenter.ui.main.MainActivity
import com.tapbi.spark.controlcenter.utils.MethodUtils
import com.tapbi.spark.controlcenter.utils.StringUtils.isEmptyString
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

class EditFocusFragment : BaseBindingFragment<FragmentEditFocusBinding, EditFocusViewModel>() {
    private val listColor: MutableList<String> = ArrayList()
    private val listIcon: MutableList<String> = ArrayList()
    private var iconAdapter: IconAdapter? = null
    private var colorAdapter: ColorAdapter? = null
    private var focusIOSList: MutableList<FocusIOS> = ArrayList()
    private var colorFocus: String? = null
    private var iconFocus: String? = null
    private var nameOldFocus = ""
    private var focusiOS: FocusIOS? = null
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                MethodUtils.hideKeyboard(requireActivity())
                mainViewModel.itemFocusDetail.postValue(focusiOS)
                (requireActivity() as MainActivity).navControllerMain.popBackStack(
                    R.id.editFocusFragment,
                    true
                )
            }
        }

    override fun getViewModel(): Class<EditFocusViewModel> {
        return EditFocusViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_edit_focus

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        initView()
        observerData()
        if (savedInstanceState != null) {
            focusiOS = Gson().fromJson(
                savedInstanceState.getString(Constant.ITEM_EDIT_FOCUS),
                object : TypeToken<FocusIOS?>() {}.type
            )
            mainViewModel.itemEditFocusCurrent.postValue(focusiOS)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(Constant.ITEM_EDIT_FOCUS, Gson().toJson(focusiOS))
    }

    private fun observerData() {
        mainViewModel.itemEditFocusCurrent.observe(viewLifecycleOwner) { focusIOS: FocusIOS? ->
            if (focusIOS != null) {
                focusiOS = focusIOS
                nameOldFocus = focusIOS.name
                mainViewModel.listColor
                mainViewModel.getListIcon(requireContext())
                mainViewModel.getListFocusAdd(App.presetFocusList as List<FocusIOS?>)
                binding.editName.setText(focusIOS.name)
                binding.imFocus.setColorFilter(Color.parseColor(focusIOS.colorFocus))
                Glide.with(binding.imFocus)
                    .load(focusIOS.imageLink)
                    .into(binding.imFocus)
                binding.editName.setTextColor(Color.parseColor(focusIOS.colorFocus))
                mainViewModel.itemEditFocusCurrent.postValue(null)
            }
        }
        mainViewModel.listColorMutableLiveData.observe(viewLifecycleOwner) { strings: List<String>? ->
            if (strings != null) {
                listColor.clear()
                listColor.addAll(strings)
                colorAdapter?.setData(strings)
                for (i in strings.indices) {
                    if (focusiOS?.colorFocus == strings[i]) {
                        colorFocus = strings[i]
                        colorAdapter?.setIDColor(i)
                        break
                    }
                }
            }
        }
        mainViewModel.listIconMutableLiveData.observe(viewLifecycleOwner) { strings: List<String>? ->
            if (strings != null) {
                listIcon.clear()
                listIcon.addAll(strings)
                iconAdapter?.setData(strings)
                for (i in strings.indices) {
                    if (focusiOS?.imageLink == strings[i]) {
                        iconFocus = strings[i]
                        iconAdapter?.setIdIcon(i)
                        break
                    }
                }
            }
        }

        mainViewModel.listAddFocusMutableLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                focusIOSList = it.toMutableList()
                focusIOSList.addAll(App.presetFocusList)
            }
        }
        viewModel.editFocusMutableLiveData.observe(viewLifecycleOwner) { aBoolean: Boolean? ->
            if (aBoolean!!) {
                mainViewModel.itemFocusDetail.postValue(focusiOS)
                (requireActivity() as MainActivity).navigate(
                    R.id.action_editFocusFragment_to_focusDetailFragment,
                    R.id.editFocusFragment
                )
                EventBus.getDefault().post(EventUpdateFocus())
            }
        }
    }

    private fun initView() {
        setUpPaddingStatusBar(binding.layoutEditFocus)
        (requireActivity() as MainActivity).setColorNavigation(R.color.white)
        initAdapter()
        initListener()
    }

    private fun initListener() {
        backPres()
        binding.tvDone.setOnClickListener { view: View? ->
            ViewHelper.preventTwoClick(view)
            Timber.e("tvDone.setOnClickListener1")
            if (isEmptyString(binding.editName.text.toString())) {
                binding.tvError.text = getString(R.string.name_empty)
                binding.tvError.visibility = View.VISIBLE
            } else {
                var isExitName = false
                for (item in focusIOSList) {
                    if (item.name.uppercase() == focusiOS?.name?.uppercase()) {
                        continue
                    }
                    if (item.name.uppercase() == binding.editName.text.toString().uppercase()) {
                        isExitName = true
                        break
                    }
                }
                if (isExitName) {
                    binding.tvError.text =
                        getString(R.string.a_focus_with_this_name_already_exists)
                    binding.tvError.visibility = View.VISIBLE
                } else {
                    for (item in App.presetFocusList) {
                        if (item.name.uppercase() == focusiOS?.name?.uppercase()) {
                            item.name = binding.editName.text.toString()
                            item.colorFocus = colorFocus
                            item.imageLink = iconFocus
                            focusiOS = item
                            viewModel.editFocus(
                                item.name,
                                item.imageLink,
                                item.colorFocus,
                                nameOldFocus
                            )
                            break
                        }
                    }
                }
                Timber.e("tvDone.setOnClickListener2")
            }
        }
    }

    private fun backPres() {
        binding.imBack.setOnClickListener { v: View? ->
            ViewHelper.preventTwoClick(v)
            MethodUtils.hideKeyboard(requireActivity())
            mainViewModel.itemFocusDetail.postValue(focusiOS)
            (requireActivity() as MainActivity).navControllerMain.popBackStack(
                R.id.editFocusFragment,
                true
            )
        }
    }

    private fun initAdapter() {
        colorAdapter = ColorAdapter()
        colorAdapter?.setListener { _: Int, color: String? ->
            colorFocus = color
            MethodUtils.hideSoftKeyboard(requireActivity())
            binding.editName.setTextColor(Color.parseColor(color))
            binding.imFocus.setColorFilter(Color.parseColor(color))
        }
        iconAdapter = IconAdapter()
        iconAdapter?.setListener { _: Int, icon: String? ->
            iconFocus = icon
            MethodUtils.hideSoftKeyboard(requireActivity())
            Glide.with(binding.imFocus)
                .load(icon)
                .into(binding.imFocus)
        }
        binding.rvColorFocus.adapter = colorAdapter
        binding.rvIconFocus.adapter = iconAdapter
    }

    override fun onPermissionGranted() {}
    override fun onDestroy() {
        super.onDestroy()
        onBackPressedCallback.remove()
    }
}
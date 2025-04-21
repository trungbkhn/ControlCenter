package com.tapbi.spark.controlcenter.ui.main.focus.createfocus

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.adapter.ColorAdapter
import com.tapbi.spark.controlcenter.adapter.IconAdapter
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.data.model.FocusIOS
import com.tapbi.spark.controlcenter.databinding.FragmentCreateFocusBinding
import com.tapbi.spark.controlcenter.ui.base.BaseBindingFragment
import com.tapbi.spark.controlcenter.ui.dialog.DialogRequestPermissionWriteSetting
import com.tapbi.spark.controlcenter.ui.main.MainActivity
import com.tapbi.spark.controlcenter.utils.MethodUtils
import com.tapbi.spark.controlcenter.utils.StringUtils.isEmptyString
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper
import timber.log.Timber

class CreateFocusFragment :
    BaseBindingFragment<FragmentCreateFocusBinding, CreateFocusViewModel>() {
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                MethodUtils.hideKeyboard(requireActivity())
                (requireActivity() as MainActivity).navControllerMain.popBackStack(
                    R.id.createFocusFragment,
                    true
                )
            }
        }
    private var iconAdapter: IconAdapter? = null
    private var colorAdapter: ColorAdapter? = null
    private val listColor: MutableList<String> = mutableListOf()
    private val listIcon: MutableList<String> = mutableListOf()
    private var focusIOSArrayList: MutableList<FocusIOS> = mutableListOf()
    private var posColor = 4
    private var posIcon = 0
    private var dialogPermissionAlertDialog: DialogRequestPermissionWriteSetting? = null
    private val requestPermissionLauncherReadContact = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { result: Boolean ->
        if (result) {
//                    addFragment(new CustomAllowedPeopleFragment(), false);
            (requireActivity() as MainActivity).navigate(
                R.id.action_createFocusFragment_to_customNewAllowPeopleFragment,
                R.id.createFocusFragment
            )
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val b = shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)
                if (context != null && !b) {
                    if (dialogPermissionAlertDialog == null) {
                        dialogPermissionAlertDialog =
                            MethodUtils.showDialogPermission(context, true,"", false, null)
                        Timber.e("NVQ DialogRequestPermissionWriteSetting :$this")
                    }
                    if (dialogPermissionAlertDialog?.dialog?.isShowing != true) {
                        dialogPermissionAlertDialog?.show(childFragmentManager,Constant.DIALOG_REQUEST_PERMISSION_WRITE_SETTING)
                    }
                } else {
                    toastText(R.string.text_detail_when_permission_read_contact)
                }
            } else {
                toastText(R.string.text_detail_when_permission_read_contact)
            }
        }
    }

    override fun getViewModel(): Class<CreateFocusViewModel> {
        return CreateFocusViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_create_focus

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        initView()
        mainViewModel.listColor
        mainViewModel.getListIcon(requireContext())
        observerData()
    }

    private fun observerData() {
        mainViewModel.listColorMutableLiveData.observe(viewLifecycleOwner) { strings ->
            if (strings != null) {
                listColor.clear()
                listColor.addAll(strings)
                colorAdapter?.setData(strings)
                colorAdapter?.setIDColor(posColor)
                val intColor = Color.parseColor(strings[posColor].trim { it <= ' ' })
                binding.imFocus.setColorFilter(intColor)
                binding.editName.setTextColor(intColor)
            }
        }
        mainViewModel.listIconMutableLiveData.observe(viewLifecycleOwner) { strings ->
            if (strings != null) {
                listIcon.clear()
                listIcon.addAll(strings)
                iconAdapter?.setData(strings)
                iconAdapter?.setIdIcon(posIcon)
                Glide.with(binding.root)
                    .load(listIcon[posIcon])
                    .into(binding.imFocus)
            }
        }
        mainViewModel.listAddFocusMutableLiveData.observe(viewLifecycleOwner) { list ->
            list?.let {
                focusIOSArrayList = it.toMutableList()
                focusIOSArrayList.addAll(App.presetFocusList)
            }
        }

    }

    private fun initView() {
        setUpPaddingStatusBar(binding.layoutCreateFocus)
        (requireActivity() as MainActivity).setColorNavigation(R.color.white)
        initAdapter()
        initListener()
    }

    private fun initListener() {
        backPress()
        binding.editName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (isEmptyString(s.toString())) {
                    binding.tvNext.visibility = View.INVISIBLE
                    binding.tvNextOne.visibility = View.VISIBLE
                } else {
                    binding.tvNext.visibility = View.VISIBLE
                    binding.tvNextOne.visibility = View.INVISIBLE
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binding.tvNext.setOnClickListener { v: View? ->
            ViewHelper.preventTwoClick(v)
            var isExit = false
            for (focusIOS in focusIOSArrayList) {
                if (focusIOS.name.uppercase() == binding.editName.text.toString().uppercase()) {
                    isExit = true
                    break
                }
            }
            if (isExit) {
                binding.tvError.visibility = View.VISIBLE
            } else {
                binding.tvError.visibility = View.GONE
                val focusIOS = FocusIOS(
                    binding.editName.text.toString(),
                    listIcon[posIcon],
                    listColor[posColor],
                    Constant.NO_ONE,
                    false,
                    false,
                    false,
                    false,
                    false
                )
                mainViewModel.itemCreateFocusCurrent.postValue(focusIOS)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_CONTACTS
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissionLauncherReadContact.launch(Manifest.permission.READ_CONTACTS)
                } else {
//                    addFragment(new CustomAllowedPeopleFragment(), false);
                    (requireActivity() as MainActivity).navigate(
                        R.id.action_createFocusFragment_to_customNewAllowPeopleFragment,
                        R.id.createFocusFragment
                    )
                }
            }
        }
    }

    private fun backPress() {
        binding.imBack.setOnClickListener { v: View? ->
            ViewHelper.preventTwoClick(v)
            requireActivity().onBackPressed()
        }
    }

    private fun initAdapter() {
        colorAdapter = ColorAdapter()
        colorAdapter?.setListener { position: Int, _: String? ->
            MethodUtils.hideSoftKeyboard(requireActivity())
            posColor = position
            binding.editName.setTextColor(Color.parseColor(listColor[posColor]))
            binding.imFocus.setColorFilter(Color.parseColor(listColor[posColor]))
        }
        iconAdapter = IconAdapter()
        iconAdapter?.setListener { position: Int, icon: String? ->
            MethodUtils.hideSoftKeyboard(requireActivity())
            posIcon = position
            Glide.with(binding.imFocus)
                .load(icon)
                .into(binding.imFocus)
        }
        binding.rvColorFocus.adapter = colorAdapter
        binding.rvIconFocus.adapter = iconAdapter
    }

    override fun onPermissionGranted() {}
    override fun onDestroyView() {
        super.onDestroyView()
        hideDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        onBackPressedCallback.remove()
    }

    private fun hideDialog() {
        if (dialogPermissionAlertDialog != null && dialogPermissionAlertDialog?.dialog?.isShowing == true) {
            dialogPermissionAlertDialog?.dismissAllowingStateLoss()
        }
    }
}
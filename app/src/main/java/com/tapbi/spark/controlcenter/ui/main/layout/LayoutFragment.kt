package com.tapbi.spark.controlcenter.ui.main.layout

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import com.tapbi.spark.controlcenter.App.Companion.tinyDB
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.common.models.IconNotyEvent
import com.tapbi.spark.controlcenter.common.models.TitleMiControlChange
import com.tapbi.spark.controlcenter.databinding.ActivityLayoutBinding
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614
import com.tapbi.spark.controlcenter.ui.base.BaseBindingFragment
import com.tapbi.spark.controlcenter.ui.main.MainActivity
import com.tapbi.spark.controlcenter.ui.main.MainActivity.Companion.isDispatchLongTouchEvent
import com.tapbi.spark.controlcenter.ui.main.MainActivity.Companion.isDispatchTouchEvent
import com.tapbi.spark.controlcenter.ui.main.layout.icon.IconBottomSheetFragment
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper
import com.tapbi.spark.controlcenter.views.SwitchButtonIos

class LayoutFragment : BaseBindingFragment<ActivityLayoutBinding, LayoutViewModel>(),
    View.OnClickListener {
    private var iconFragment: IconBottomSheetFragment? = null
    private var listenerBackPress: OnBackPressedCallback? = null
    override fun getViewModel(): Class<LayoutViewModel> {
        return LayoutViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.activity_layout

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        setUpPaddingStatusBar(binding.layoutParent)
        (requireActivity() as MainActivity).setColorNavigation(R.color.colorPrimary)
        findView()
        addListenerBackPress()
        addListenerChangeIcon()
        addListenerChangeTitleMiControl()
    }

    private fun addListenerChangeTitleMiControl() {
        mainViewModel.titleMiControlChangeLiveEvent.observe(viewLifecycleOwner) { titleMiControl: Any? ->
            if (titleMiControl is TitleMiControlChange) {
                setTextTitleMiControl(titleMiControl.title)
            }
        }
    }

    private fun addListenerChangeIcon() {
        mainViewModel.iconNotyLiveEvent.observe(viewLifecycleOwner) { iconChange: Any? ->
            if (iconChange is IconNotyEvent && iconChange.isChange) {
                updateIcon()
            }
        }
    }

    override fun onPermissionGranted() {}

    @SuppressLint("ClickableViewAccessibility")
    private fun findView() {
        setViewIcon()
        if (tinyDB.getInt(
                Constant.TYPE_NOTY,
                Constant.VALUE_CONTROL_CENTER_OS
            ) == Constant.VALUE_SHADE
        ) {
            binding.groupShowDateTime.visibility = View.GONE
            binding.v3.visibility = View.GONE
        } else {
            setUpViewShowDateTime()
            listenerSwShowDateTimeChange()
        }
        binding.enableNotificationSection.visibility = View.VISIBLE
        binding.viewNone.visibility = View.VISIBLE
        binding.viewHeader.tvTitle.setText(R.string.menu_layout)
        setClick()


//        PackageManager packageManager = getPackageManager();
//        packageManager.queryBroadcastReceivers()
    }

    private fun listenerSwShowDateTimeChange() {
        binding.swShowDateTime.onCheckedChangeListener =
            SwitchButtonIos.OnCheckedChangeListener { isChecked: Boolean ->
                binding.tvClickTextShow.visibility = if (isChecked) View.GONE else View.VISIBLE
                binding.tvTextShow.visibility = if (isChecked) View.GONE else View.VISIBLE
                binding.v4.visibility = if (isChecked) View.GONE else View.VISIBLE
                tinyDB.putBoolean(Constant.SHOW_DATE_TIME, isChecked)
                if (NotyControlCenterServicev614.getInstance() != null) {
                    NotyControlCenterServicev614.getInstance().updateTextTitle()
                }
            }
    }

    private fun setUpViewShowDateTime() {
        setTextTitleMiControl(tinyDB.getString(Constant.TEXT_SHOW))
        if (tinyDB.getBoolean(Constant.SHOW_DATE_TIME, false)) {
            binding.swShowDateTime.setChecked(true)
            binding.tvClickTextShow.visibility = View.GONE
            binding.tvTextShow.visibility = View.GONE
            binding.v4.visibility = View.GONE
        } else {
            binding.swShowDateTime.setChecked(false)
            binding.tvClickTextShow.visibility = View.VISIBLE
            binding.tvTextShow.visibility = View.VISIBLE
            binding.v4.visibility = View.VISIBLE
        }
    }

    private fun setTextTitleMiControl(title: String) {
        binding.tvTextShow.text = title
    }

    private fun updateIcon() {
        setViewIcon()
        if (NotyControlCenterServicev614.getInstance() != null) {
            NotyControlCenterServicev614.getInstance().updateIcon()
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun setViewIcon() {
        val ress: Int =
            if (tinyDB.getString(Constant.ICON_ACTION_SELECT) != null && tinyDB.getString(
                    Constant.ICON_ACTION_SELECT
                ).isNotEmpty()
            ) {
                resources.getIdentifier(
                    tinyDB.getString(Constant.ICON_ACTION_SELECT),
                    "drawable",
                    requireContext().packageName
                )
            } else {
                resources.getIdentifier(
                    Constant.ICON_ACTION_DEFAULT,
                    "drawable",
                    requireContext().packageName
                )
            }
        try {
            binding.imgPreview.setImageResource(ress)
        } catch (_: Exception) { }
    }

    private fun setClick() {
        binding.tvClickTextShow.setOnClickListener(this)
        binding.textSelectIcon.setOnClickListener(this)
        //    swShowDateTime.setOnClickListener(this);
        binding.viewHeader.imBack.setOnClickListener {
            isDispatchTouchEvent()
            //            onBackPressed();
            (requireActivity() as MainActivity).navControllerMain.popBackStack(
                R.id.layoutFragment,
                true
            )
        }
    }

    @SuppressLint("NonConstantResourceId")
    override fun onClick(v: View) {
        when (v.id) {
            R.id.textSelectIcon -> {
                isDispatchLongTouchEvent
                ViewHelper.preventTwoClick(v, 1000)
                showBottomSheetIcon()
            }

            R.id.tvClickTextShow -> {
                //                isDispatchLongTouchEvent();
                ViewHelper.preventTwoClick(v, 500)
                intentChangeText()
            }
        }
    }

    private fun showBottomSheetIcon() {
        if (iconFragment == null) {
            iconFragment =
                if (childFragmentManager.findFragmentByTag(IconBottomSheetFragment::class.java.name) is IconBottomSheetFragment) {
                    childFragmentManager.findFragmentByTag(IconBottomSheetFragment::class.java.name) as IconBottomSheetFragment?
                } else {
                    IconBottomSheetFragment()
                }
        }
        if (iconFragment != null && !iconFragment!!.isAdded) {
            iconFragment!!.show(childFragmentManager, IconBottomSheetFragment::class.java.name)
        }
    }

    override fun onPause() {
        super.onPause()
        if (iconFragment != null) {
            iconFragment!!.dismissAllowingStateLoss()
        }
    }

    private fun addListenerBackPress() {
        // This callback will only be called when MyFragment is at least Started.
        listenerBackPress = object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
                // Handle the back button event
                listenerBackPress!!.isEnabled = false
                //                onBackPressed();
                (requireActivity() as MainActivity).navControllerMain.popBackStack(
                    R.id.layoutFragment,
                    true
                )
            }
        }
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            listenerBackPress as OnBackPressedCallback
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (listenerBackPress != null) {
            listenerBackPress!!.remove()
        }
    }

    private fun intentChangeText() {
//        addFragment(new CustomizeTextFragment(), false);
        (requireActivity() as MainActivity).navigate(
            R.id.action_layoutFragment_to_customizeTextFragment,
            R.id.layoutFragment
        )
        //navigate(R.id.layoutFragment, R.id.action_layoutFragment_to_customizeTextFragment);
    }
}

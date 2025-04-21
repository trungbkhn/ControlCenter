package com.tapbi.spark.controlcenter.ui.main.customcontrol.gallery

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.SimpleItemAnimator
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.App.Companion.tinyDB
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.adapter.AllControlsAdapter
import com.tapbi.spark.controlcenter.adapter.IncludedControlsAdapter
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.common.models.CustomizeControlApp
import com.tapbi.spark.controlcenter.common.models.MessageEvent
import com.tapbi.spark.controlcenter.databinding.FragmentCustomControlBinding
import com.tapbi.spark.controlcenter.eventbus.EventCustomControls
import com.tapbi.spark.controlcenter.feature.controlios14.helper.SimpleItemTouchHelperCallback
import com.tapbi.spark.controlcenter.feature.controlios14.model.ControlCustomize
import com.tapbi.spark.controlcenter.interfaces.OnStartDragListener
import com.tapbi.spark.controlcenter.ui.base.BaseBindingFragment
import com.tapbi.spark.controlcenter.ui.main.MainActivity
import com.tapbi.spark.controlcenter.utils.Analytics
import com.tapbi.spark.controlcenter.utils.AppUtils
import com.tapbi.spark.controlcenter.utils.ControlCustomizeManager
import com.tapbi.spark.controlcenter.utils.DensityUtils
import com.tapbi.spark.controlcenter.utils.MethodUtils
import com.tapbi.spark.controlcenter.utils.safeDelay
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.util.Objects

class GalleryFragment
    : BaseBindingFragment<FragmentCustomControlBinding, GalleryViewModel>(),
    AllControlsAdapter.ICustomizeControlClick {
    private val listCustomizeCurrentApp = ArrayList<ControlCustomize>()
    private val listExceptCurrentApp = ArrayList<ControlCustomize>()
    private val MAX_ITEM_ADD_16_9 = 12
    private val MAX_ITEM_ADD_NORMAL = 11
    private var isLoading = false
    private lateinit var customControl: Array<String?>
    private var styleSelected = Constant.STYLE_CONTROL_TOP
    private var mItemTouchHelper: ItemTouchHelper? = null
    private var isSwitchButtonIosCheck: Boolean? = null
    private val onStartDragListener =
        OnStartDragListener { viewHolder -> mItemTouchHelper?.startDrag(viewHolder) }
    private var allControlsAdapter: AllControlsAdapter? = null
    private var includedControlsAdapter: IncludedControlsAdapter? = null
    private var callbackDrag: SimpleItemTouchHelperCallback? = null
    private var countItemAddMax = MAX_ITEM_ADD_NORMAL
    private var listenerBackPress: OnBackPressedCallback? = null
    override fun getViewModel(): Class<GalleryViewModel> {
        return GalleryViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_custom_control

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addListenerBackPress()
        EventBus.getDefault().register(this)
    }

    @SuppressLint("StringFormatInvalid")
    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        safeDelay(100) {
            customControl = ControlCustomizeManager.getInstance(context).listControlsSave
            evenClick()
            initRecyclerview()
            styleSelected = tinyDB.getInt(Constant.STYLE_CONTROL, Constant.STYLE_CONTROL_TOP)
            initData()
            bindStyle()
        }
        binding.itemTitleMoreAppCustomizeControl.detailInclude.text = requireContext().getString(
            R.string.text_add_organize_control,
            getString(R.string.app_name)
        )
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun initRecyclerview() {
        binding.scroll.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            if (scrollY == (v.getChildAt(0).measuredHeight - v.measuredHeight)) {
                val count = allControlsAdapter?.itemCount ?: 0
                safeDelay(100) {
                    if (!isLoading) {
                        if (count < App.all.size) {
                            allControlsAdapter?.loadData()
                        } else {
                            onLoading(false)
                        }

                    }
                }
            }
        })

        if (includedControlsAdapter == null) {
            includedControlsAdapter = IncludedControlsAdapter()
            includedControlsAdapter?.setParameters(
                requireContext(),
                binding.rcvIncluded,
                onStartDragListener,
                this
            )
        }

        if (allControlsAdapter == null) {
            allControlsAdapter = AllControlsAdapter()
            allControlsAdapter?.setParameters(requireContext(), binding.rcvMore, this)
        }

//        includedControlsAdapter?.changeList(listCustomizeCurrentApp)

        //allControlsAdapter?.setData(listExceptCurrentApp)

        binding.rcvMore.adapter = allControlsAdapter
        binding.rcvIncluded.adapter = includedControlsAdapter

        callbackDrag = SimpleItemTouchHelperCallback(includedControlsAdapter)
        setPosCallBackDrag()
        mItemTouchHelper = ItemTouchHelper(callbackDrag!!)
        mItemTouchHelper?.attachToRecyclerView(binding.rcvIncluded)
        (Objects.requireNonNull(
            binding.rcvIncluded.itemAnimator
        ) as SimpleItemAnimator).supportsChangeAnimations = false
    }

    private fun evenClick() {
//        binding.imBack.setOnClickListener {
//            ViewHelper.preventTwoClick(it, 200)
//            (activity as MainActivity).navControllerMain.popBackStack()
//        }
//
//        binding.tvDone.setOnClickListener {
//            ViewHelper.preventTwoClick(it, 200)
//            updateControlSave()
//            (activity as MainActivity).navControllerMain.popBackStack(
//                R.id.customizeControlFragment,
//                true
//            )
//        }
    }

    private fun initData() {
//        mainViewModel.customizeControlAppLiveData.value?.let {
//            if (it.listExceptCurrentApp.isNullOrEmpty())
//                mainViewModel.getListAppForCustomize(
//                    context,
//                    customControl
//                )
//        } ?: mainViewModel.getListAppForCustomize(
//            context,
//            customControl
//        )
        mainViewModel.customizeControlAppLiveData.observe(viewLifecycleOwner) { listApp: CustomizeControlApp ->
            listCustomizeCurrentApp.clear()
            listExceptCurrentApp.clear()
            listCustomizeCurrentApp.addAll(listApp.listCustomizeCurrentApp)
            listExceptCurrentApp.addAll(listApp.listExceptCurrentApp)
            setValueListAppToAdapter()
        }
    }

    private fun bindStyle() {
        setUpValueCheckBox()
//        binding.itemStyleCustomizeControl.viewClick1.setOnClickListener {
//            styleSelected = Constant.STYLE_CONTROL_TOP
//            setUpValueCheckBox()
//        }
//        binding.itemStyleCustomizeControl.viewClick2.setOnClickListener {
//            styleSelected = Constant.STYLE_CONTROL_BOTTOM
//            setUpValueCheckBox()
//        }
//        binding.itemVibrateControl.swVibrate.setChecked(
//            tinyDB.getBoolean(
//                Constant.VIBRATOR_CONTROL_LONG_CLICK,
//                Constant.VALUE_DEFAULT_VIBRATOR
//            )
//        )
//
//        binding.itemVibrateControl.swVibrate.onCheckedChangeListener =
//            SwitchButtonIos.OnCheckedChangeListener { isChecked ->
//                isSwitchButtonIosCheck = isChecked
//            }
    }

    private fun setUpValueCheckBox() {
//        if (styleSelected == Constant.STYLE_CONTROL_TOP) {
//            binding.itemStyleCustomizeControl.cbStyle1.isChecked = true
//            binding.itemStyleCustomizeControl.cbStyle2.isChecked = false
//        } else {
//            binding.itemStyleCustomizeControl.cbStyle1.isChecked = false
//            binding.itemStyleCustomizeControl.cbStyle2.isChecked = true
//        }
    }

    private fun setValueListAppToAdapter() {
        setPosCallBackDrag()
        includedControlsAdapter?.changeList(listCustomizeCurrentApp)
        styleSelected = tinyDB.getInt(Constant.STYLE_CONTROL, Constant.STYLE_CONTROL_TOP)
    }

    private fun setCountMaxItemAdd() {
        val heightScreen = DensityUtils.getScreenHeight()
        val widthScreen = DensityUtils.getScreenWidth()
        countItemAddMax = if (heightScreen / widthScreen >= 16 / 9) {
            MAX_ITEM_ADD_16_9
        } else {
            MAX_ITEM_ADD_NORMAL
        }
    }

    override fun onResume() {
        super.onResume()
        Analytics.getInstance().setCurrentScreen(activity, javaClass.simpleName)
    }


    override fun styleClick(style: Int) {
        styleSelected = style
    }

    override fun onDelete(position: Int) {
        if (listCustomizeCurrentApp.size > 0 && position < listCustomizeCurrentApp.size) {
            listExceptCurrentApp.add(0, listCustomizeCurrentApp[position])
            allControlsAdapter?.addItem(0, listCustomizeCurrentApp[position])
            listCustomizeCurrentApp.remove(listCustomizeCurrentApp[position])
            changeListAdapter()
            binding.rcvMore.scrollToPosition(0)
        }
    }

    override fun onAdd(position: Int) {
        if (listCustomizeCurrentApp.size < countItemAddMax) {
            if (position >= 0 && position < listExceptCurrentApp.size) {
                listCustomizeCurrentApp.add(listExceptCurrentApp[position])
                listExceptCurrentApp.remove(listExceptCurrentApp[position])
                allControlsAdapter?.removeItem(position)
                changeListAdapter()

            }
        } else {
            toastText(R.string.maximum_11_control)
        }
    }

    override fun onLoading(it: Boolean) {
        if (it) {
            isLoading = true
            binding.viewLoading.visibility = View.VISIBLE
        } else {
            isLoading = false
            binding.viewLoading.visibility = View.GONE
        }

    }

    override fun onLoadingIncludeDone() {
        allControlsAdapter?.setData(listExceptCurrentApp)
        allControlsAdapter?.loadIconAfterChange()
        binding.bgLoading.visibility = View.GONE
    }


    private fun changeListAdapter() {
        setPosCallBackDrag()

        allControlsAdapter?.let {
            it.setData(listExceptCurrentApp)
            it.loadIconAfterChange()
        }
        includedControlsAdapter?.changeList(listCustomizeCurrentApp)


        EventBus.getDefault()
            .post(EventCustomControls(Constant.EVENT_CHANGE_GALLERY, listCustomizeCurrentApp))
    }

    private fun setPosCallBackDrag() {
        callbackDrag?.setPositionStartAndEnd(
            0,
            listCustomizeCurrentApp.size - 1
        )
    }

    private fun updateControlSave() {
//        val action = if (tinyDB.getInt(
//                Constant.STYLE_CONTROL,
//                Constant.STYLE_CONTROL_TOP
//            ) == styleSelected
//        ) Constant.ACTION_CHANGE_ITEM_CONTROL else Constant.ACTION_CHANGE_LAYOUT_CONTROL
//        tinyDB.putInt(Constant.STYLE_CONTROL, styleSelected)
//        customControl = arrayOfNulls(listCustomizeCurrentApp.size)
//        for (i in listCustomizeCurrentApp.indices) {
//            customControl[i] = listCustomizeCurrentApp[i].packageName
//        }
//        isSwitchButtonIosCheck?.let { isChecked ->
//            tinyDB.putBoolean(Constant.VIBRATOR_CONTROL_LONG_CLICK, isChecked)
//            if (isChecked) {
//                VibratorUtils.getInstance(binding.itemVibrateControl.swVibrate.context)
//                    .vibrator(VibratorUtils.TIME_DEFAULT)
//            }
//        }
//        ControlCustomizeManager.getInstance(activity).saveCustomControl(customControl)
//        EventBus.getDefault().post(EventSaveControl(action))
//        mainViewModel.getListAppForCustomize(context, customControl)
//        toastText(R.string.save)


    }

    override fun onPermissionGranted() {}


    override fun onDestroyView() {
        super.onDestroyView()
        binding.bgLoading.visibility = View.GONE
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        listenerBackPress?.remove()
    }

    private fun addListenerBackPress() {
        // This callback will only be called when MyFragment is at least Started.
        listenerBackPress = object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
                // Handle the back button event
                (activity as MainActivity).navControllerMain.popBackStack()
            }
        }
        activity?.onBackPressedDispatcher?.addCallback(listenerBackPress as OnBackPressedCallback)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(messageEvent: MessageEvent) {
        when (messageEvent.typeEvent) {
            Constant.PACKAGE_APP_REMOVE -> {
                val listCustomizeCurrentAppTemp: List<ControlCustomize> =
                    AppUtils.updatePackageRemoveControlCustomize(
                        messageEvent.stringValue,
                        listCustomizeCurrentApp
                    )
                val listExceptCurrentAppTemp: List<ControlCustomize> =
                    AppUtils.updatePackageRemoveControlCustomize(
                        messageEvent.stringValue,
                        listExceptCurrentApp
                    )
                listCustomizeCurrentApp.clear()
                listCustomizeCurrentApp.addAll(listCustomizeCurrentAppTemp)
                listExceptCurrentApp.clear()
                listExceptCurrentApp.addAll(listExceptCurrentAppTemp)
                includedControlsAdapter?.setNewData(listCustomizeCurrentApp)
                allControlsAdapter?.setNewData(listExceptCurrentApp)
            }

            Constant.PACKAGE_APP_ADD -> if (messageEvent.stringValue.isNotEmpty()) {
                listExceptCurrentApp.add(
                    0,
                    ControlCustomize(
                        0,
                        MethodUtils.getAppNameFromPackageName(
                            requireContext(),
                            messageEvent.stringValue
                        ),
                        null,
                        messageEvent.stringValue
                    )
                )
                allControlsAdapter?.setNewData(listExceptCurrentApp)
            }
        }
    }
}

package com.tapbi.spark.controlcenter.ui.main.customcontrol.controls

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.adapter.ColorControlsAdapter
import com.tapbi.spark.controlcenter.adapter.ControlsAdapter
import com.tapbi.spark.controlcenter.adapter.GroupColorControlsAdapter
import com.tapbi.spark.controlcenter.adapter.IconShapeAdapter
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.common.Constant.EVENT_CHANGE_CONNER
import com.tapbi.spark.controlcenter.common.Constant.EVENT_CHANGE_STATE_SEEK_BAR
import com.tapbi.spark.controlcenter.data.model.GroupColor
import com.tapbi.spark.controlcenter.databinding.FragmentControlsBinding
import com.tapbi.spark.controlcenter.eventbus.EventCustomControls
import com.tapbi.spark.controlcenter.ui.base.BaseBindingFragment
import com.tapbi.spark.controlcenter.ui.custom.CustomLayoutManager
import com.tapbi.spark.controlcenter.ui.main.customcontrol.CustomizeControlFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber


class ControlsFragment : BaseBindingFragment<FragmentControlsBinding, ControlsViewModel>() {

    private var customLayoutManager: CustomLayoutManager? = null
    private var controlsAdapter: ControlsAdapter? = null
    private var type = Constant.VALUE_CONTROL_CENTER
    private var shapeAdapter: IconShapeAdapter? = null
    private var colorControlsAdapter: ColorControlsAdapter? = null
    private var groupColorAdapter: GroupColorControlsAdapter? = null

    private var listGroupColor = mutableListOf<GroupColor>()
        set(value) {
            field = value
            groupColorAdapter?.setData(value)
        }

    private var listColor = mutableListOf<Int>()
        set(value) {
            field = value
            colorControlsAdapter?.setData(value)
            (parentFragment as? CustomizeControlFragment)?.theme?.let { theme ->
                val selectedColor = if (type == Constant.VALUE_SHADE) {
                    theme.miShade?.backgroundColorSelectControl
                } else {
                    theme.controlCenterOS?.listControlCenterStyleVerticalTop
                        ?.firstOrNull { it.controlSettingIosModel?.backgroundColorSelectViewItem?.isNotEmpty() == true }
                        ?.controlSettingIosModel?.backgroundColorSelectViewItem
                }
                selectedColor?.let { selected ->
                    listColor.indexOfFirst { Color.parseColor(selected) == it }.let { index ->
                        colorControlsAdapter?.setSelect(index, Color.parseColor(selectedColor))
                    }
                }
            }
        }

    private var listIconShape = mutableListOf<String>()
        set(value) {
            field = value
            shapeAdapter?.setData(value)
            (parentFragment as? CustomizeControlFragment)?.theme?.let { theme ->
                val selectedIcon = if (type == Constant.VALUE_SHADE) {
                    theme.miShade?.iconControl
                } else {
                    theme.controlCenterOS?.listControlCenterStyleVerticalTop
                        ?.firstOrNull { it.controlSettingIosModel?.iconControl != null }
                        ?.controlSettingIosModel?.iconControl
                }
                selectedIcon?.let { icon ->
                    listIconShape.indexOfFirst { icon == it }
                        .takeIf { it != -1 }
                        ?.let { shapeAdapter?.setSelect(it) }
                }
            }
        }
    private fun setSelectGroupColor(){
        (parentFragment as? CustomizeControlFragment)?.theme?.let { theme ->
            try {
                val groupColor = GroupColor(Color.parseColor(theme.controlCenter?.backgroundColorSelectControl1),
                    Color.parseColor(theme.controlCenter?.backgroundColorSelectControl2),
                    Color.parseColor(theme.controlCenter?.backgroundColorSelectControl3))

                listGroupColor.indexOfFirst {g -> (g.color1==groupColor.color1 && g.color2==groupColor.color2 && g.color3==groupColor.color3) }.let { index ->
                    groupColorAdapter?.setSelect(index)
                }
            } catch (e:Exception){ }

        }
    }
    private fun setSelect(){
        (parentFragment as? CustomizeControlFragment)?.theme?.let { theme ->
            val selectedIcon = when (type) {
                Constant.VALUE_CONTROL_CENTER -> {
                    theme.controlCenter?.iconControl
                }
                Constant.VALUE_SHADE -> {
                    theme.miShade?.iconControl
                }
                else -> {
                    theme.controlCenterOS?.listControlCenterStyleVerticalTop?.get(1)?.controlSettingIosModel?.backgroundImageViewItem
                }
            }
            if (selectedIcon != "icon_shade_1.webp") {
                EventBus.getDefault().post(EventCustomControls(EVENT_CHANGE_STATE_SEEK_BAR, false))
            }
            selectedIcon?.let { icon ->
                listIconShape.indexOfFirst { icon == it }.takeIf { it != -1 }
                    ?.let { shapeAdapter?.setSelect(it) }
            }
        }
    }

    override fun getViewModel() = ControlsViewModel::class.java

    override val layoutId: Int
        get() = R.layout.fragment_controls

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        initView()
        observeData()
    }

    private fun observeData() {
        viewModel.listLiveDataIconShape.observe(viewLifecycleOwner) { listIconShape = it }
        viewModel.listLiveDataColor.observe(viewLifecycleOwner) { listColor = it }
        viewModel.listLiveDataGroupColor.observe(viewLifecycleOwner) { listGroupColor = it }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    private fun initAdapter() {
        customLayoutManager = CustomLayoutManager(App.ins, true).apply {
            binding.rcvControls.layoutManager = this
        }
        shapeAdapter = IconShapeAdapter()

        if (type != Constant.VALUE_CONTROL_CENTER) {
            colorControlsAdapter = ColorControlsAdapter()
        } else {
            groupColorAdapter = GroupColorControlsAdapter()
        }

        val progressBar = (parentFragment as? CustomizeControlFragment)?.theme?.let {
            when (type) {
                Constant.VALUE_CONTROL_CENTER -> it.controlCenter?.cornerBackgroundControl
                Constant.VALUE_SHADE -> it.miShade?.cornerBackgroundControl
                else -> it.controlCenterOS?.listControlCenterStyleVerticalTop
                    ?.firstOrNull { style -> style.controlSettingIosModel?.backgroundColorSelectViewItem?.isNotEmpty() == true }
                    ?.controlSettingIosModel?.cornerBackgroundViewItem
            }
        } ?: 50f
        controlsAdapter = ControlsAdapter(
            shapeAdapter!!,
            colorControlsAdapter,
            groupColorAdapter,
            (progressBar * 100).toInt()
        ).apply {
            setListener(object : ControlsAdapter.ClickListener {
                override fun onChangeConner(progress: Int) {
                    EventBus.getDefault().post(EventCustomControls(EVENT_CHANGE_CONNER, progress))
                }

                override fun onStartTrackingTouch() {
                    customLayoutManager?.setScrollEnabled(false)
                }

                override fun onStopTrackingTouch() {
                    customLayoutManager?.setScrollEnabled(true)
                }
            })
        }

        binding.rcvControls.itemAnimator = null
        binding.rcvControls.adapter = controlsAdapter
    }

    private fun initView() {
        type = arguments?.getInt(KEY_TYPE_CUSTOMIZE_CONTROL) ?: Constant.VALUE_CONTROL_CENTER
        initAdapter()
        viewModel.getListIconShapeControl()
        if (type == Constant.VALUE_CONTROL_CENTER) {
            viewModel.getListGroupColor()
        } else {
            viewModel.getListColor()
        }
        mainViewModel.eventSetSelect.observe(viewLifecycleOwner){
            if (it){
                lifecycleScope.launch(Dispatchers.Main){
                    while (listIconShape.isEmpty()){
                        delay(100)
                    }
                    setSelect()
                    if (type == Constant.VALUE_CONTROL_CENTER){
                        while (listGroupColor.isEmpty()){
                            delay(100)
                        }
                        setSelectGroupColor()
                    }
                }
                mainViewModel.eventSetSelect.postValue(false)
            }
        }
    }

    override fun onPermissionGranted() {
        // Handle permission granted logic here
    }

    @Subscribe
    fun onEventCustomControls(event: EventCustomControls) {
        when (event.event) {
            EVENT_CHANGE_STATE_SEEK_BAR -> {
                val isSeekBarEnabled = event.data as Boolean
                controlsAdapter?.setStateSeekBar(isSeekBarEnabled)
            }
        }
    }

    companion object {
        const val KEY_TYPE_CUSTOMIZE_CONTROL = "KEY_TYPE_CUSTOMIZE_CONTROL"

        fun newInstance(type: Int) = ControlsFragment().apply {
            arguments = Bundle().apply {
                putInt(KEY_TYPE_CUSTOMIZE_CONTROL, type)
            }
        }
    }
}


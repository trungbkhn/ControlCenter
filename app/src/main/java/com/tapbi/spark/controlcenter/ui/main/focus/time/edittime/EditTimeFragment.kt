package com.tapbi.spark.controlcenter.ui.main.focus.time.edittime

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.adapter.TimeRepeatAdapter
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.data.model.FocusIOS
import com.tapbi.spark.controlcenter.data.model.ItemTimeRepeat
import com.tapbi.spark.controlcenter.data.model.ItemTurnOn
import com.tapbi.spark.controlcenter.databinding.CustomWheelPickerItemBinding
import com.tapbi.spark.controlcenter.databinding.FragmentFocusTimeBinding
import com.tapbi.spark.controlcenter.ui.base.BaseBindingFragment
import com.tapbi.spark.controlcenter.ui.main.MainActivity
import com.tapbi.spark.controlcenter.utils.TimeUtils
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper
import sh.tyy.wheelpicker.CustomTimeToPicker

class EditTimeFragment : BaseBindingFragment<FragmentFocusTimeBinding, EditTimeViewModel>() {
    private val listItemRepeat: MutableList<ItemTimeRepeat> = ArrayList()
    private var textAMPM = ""
    private var hourSelectTo = 0
    private var minSelectTo = 0
    private var hourSelectFrom = 0
    private var minSelectFrom = 0
    private var timeRepeatAdapter: TimeRepeatAdapter? = null
    private var isFirstLoadTimeTo = false
    private var isFirstLoadTimeFrom = false
    private var minSelect = ""
    private var focus: FocusIOS? = null
    private var itemAutomationFocus: ItemTurnOn? = null
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                binding.tvTimeTo.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
                binding.tvTimeFrom.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
                if (binding.dlTimeTo.root.visibility == View.VISIBLE) binding.dlTimeTo.root.visibility =
                    View.GONE
                if (binding.dlTimeFrom.root.visibility == View.VISIBLE) binding.dlTimeFrom.root.visibility =
                    View.GONE
                mainViewModel.itemFocusDetail.postValue(focus)
                (requireActivity() as MainActivity).navControllerMain.popBackStack(
                    R.id.edittimeFragment,
                    true
                )
            }
        }

    override fun getViewModel(): Class<EditTimeViewModel> {
        return EditTimeViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_focus_time

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        initView()
        observerData()
        if (savedInstanceState?.getString(Constant.ITEM_FOCUS_EDIT_TIME) != null && savedInstanceState.getString(
                Constant.ITEM_EDIT_TIME
            ) != null
        ) {
            focus = Gson().fromJson(
                savedInstanceState.getString(Constant.ITEM_FOCUS_EDIT_TIME),
                object : TypeToken<FocusIOS?>() {}.type
            )
            itemAutomationFocus = Gson().fromJson(
                savedInstanceState.getString(Constant.ITEM_EDIT_TIME),
                object : TypeToken<ItemTurnOn?>() {}.type
            )
            mainViewModel.itemAutomationFocus.postValue(itemAutomationFocus)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (focus != null) {
            outState.putString(Constant.ITEM_FOCUS_EDIT_TIME, Gson().toJson(focus))
        }
        if (itemAutomationFocus != null) {
            outState.putString(Constant.ITEM_EDIT_TIME, Gson().toJson(itemAutomationFocus))
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun observerData() {
        mainViewModel.editItemAutomationFocus.observe(viewLifecycleOwner) { focusIOS: FocusIOS? ->
            if (focusIOS != null) {
                focus = focusIOS
            }
        }
        mainViewModel.itemAutomationFocus.observe(viewLifecycleOwner) { itemTurnOn: ItemTurnOn? ->
            if (itemTurnOn != null && focus != null) {
                itemAutomationFocus = itemTurnOn
                binding.tvDone.visibility = View.VISIBLE
                binding.tvTimeFrom.text =
                    TimeUtils.getTimeAutoWithCurrentMini(requireContext(), itemTurnOn.timeStart)
                binding.tvTimeTo.text =
                    TimeUtils.getTimeAutoWithCurrentMini(requireContext(), itemTurnOn.timeEnd)
                focus?.colorFocus?.let {
                    setDataRecyclerRepeat(
                        it,
                        itemTurnOn.monDay,
                        itemTurnOn.tueDay,
                        itemTurnOn.wedDay,
                        itemTurnOn.thuDay,
                        itemTurnOn.friDay,
                        itemTurnOn.satDay,
                        itemTurnOn.sunDay
                    )
                }
                hourSelectFrom = TimeUtils.getHourWithTimeMini(itemTurnOn.timeStart)
                hourSelectTo = TimeUtils.getHourWithTimeMini(itemTurnOn.timeEnd)
                minSelectTo = TimeUtils.getMinuteWithTimeMini(itemTurnOn.timeEnd)
                minSelectFrom = TimeUtils.getMinuteWithTimeMini(itemTurnOn.timeStart)
                mainViewModel.itemAutomationFocus.postValue(null)
            }
        }
    }

    private fun initView() {
        (activity as MainActivity).setColorNavigation(R.color.color_F2F2F6)
        setUpPaddingStatusBar(binding.layoutFocusTime)
        binding.tvDone.visibility = View.GONE
        initAdapter()
        initListener()
    }

    private fun setDataRecyclerRepeat(
        color: String,
        isMon: Boolean,
        isTu: Boolean,
        isWe: Boolean,
        isTH: Boolean,
        isFr: Boolean,
        isSa: Boolean,
        isSu: Boolean
    ) {
        listItemRepeat.clear()
        listItemRepeat.add(ItemTimeRepeat(getString(R.string.month), color, isMon))
        listItemRepeat.add(ItemTimeRepeat(getString(R.string.tu), color, isTu))
        listItemRepeat.add(ItemTimeRepeat(getString(R.string.we), color, isWe))
        listItemRepeat.add(ItemTimeRepeat(getString(R.string.th), color, isTH))
        listItemRepeat.add(ItemTimeRepeat(getString(R.string.fr), color, isFr))
        listItemRepeat.add(ItemTimeRepeat(getString(R.string.sa), color, isSa))
        listItemRepeat.add(ItemTimeRepeat(getString(R.string.su), color, isSu))
        showTextDay()
        timeRepeatAdapter?.setData(listItemRepeat)
    }

    private fun backPress() {
        binding.imBack.setOnClickListener { v: View? ->
            ViewHelper.preventTwoClick(v)
            requireActivity().onBackPressed()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initListener() {
        backPress()
        binding.tvTimeFrom.setOnClickListener {
            binding.tvTimeFrom.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_007AFF
                )
            )
            binding.dlTimeFrom.root.visibility = View.VISIBLE
            if (binding.dlTimeTo.root.visibility == View.VISIBLE) {
                binding.dlTimeTo.root.visibility = View.GONE
                binding.tvTimeTo.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
            }
            showDialogTime(binding.dlTimeFrom, hourSelectFrom, minSelectFrom)
        }
        binding.tvTimeTo.setOnClickListener {
            binding.tvTimeTo.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_007AFF
                )
            )
            binding.dlTimeTo.root.visibility = View.VISIBLE
            if (binding.dlTimeFrom.root.visibility == View.VISIBLE) {
                binding.dlTimeFrom.root.visibility = View.GONE
                binding.tvTimeFrom.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
            }
            showDialogTime(binding.dlTimeTo, hourSelectTo, minSelectTo)
        }
        binding.root.setOnClickListener { v: View? ->
            ViewHelper.preventTwoClick(v)
            binding.tvTimeTo.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.tvTimeFrom.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.black
                )
            )
            if (binding.dlTimeTo.root.visibility == View.VISIBLE) binding.dlTimeTo.root.visibility =
                View.GONE
            if (binding.dlTimeFrom.root.visibility == View.VISIBLE) binding.dlTimeFrom.root.visibility =
                View.GONE
        }
        binding.dlTimeFrom.customTimePicker.setWheelListener(object : CustomTimeToPicker.Listener {
            override fun didSelectData(hour: Int, min: Int, amPM: Int) {
                if (TimeUtils.getTimeAutoWithCurrentMini(
                        requireContext(),
                        hourSelectFrom,
                        minSelectFrom
                    ) == TimeUtils.getTimeAutoCurrentMini(requireContext(), hour, min, amPM)
                ) {
                    isFirstLoadTimeFrom = true
                }
                if (isFirstLoadTimeFrom) {
                    minSelect = if (min < 10) {
                        "0$min"
                    } else {
                        "" + min
                    }
                    if (amPM == 0) {
                        textAMPM = requireContext().getString(R.string.am)
                        hourSelectFrom = if (hour == 12) {
                            0
                        } else {
                            hour
                        }
                    }
                    if (amPM == 1) {
                        textAMPM = requireContext().getString(R.string.pm)
                        hourSelectFrom = if (hour == 12) {
                            hour
                        } else {
                            hour + 12
                        }
                    }
                    minSelectFrom = min
                    binding.tvTimeFrom.text = "$hour:$minSelect $textAMPM"
                }
            }
        })

        binding.dlTimeTo.customTimePicker.setWheelListener(object : CustomTimeToPicker.Listener {
            override fun didSelectData(hour: Int, min: Int, amPM: Int) {
                if (TimeUtils.getTimeAutoWithCurrentMini(
                        requireContext(),
                        hourSelectTo,
                        minSelectTo
                    ) == TimeUtils.getTimeAutoCurrentMini(requireContext(), hour, min, amPM)
                ) {
                    isFirstLoadTimeTo = true
                }
                if (isFirstLoadTimeTo) {
                    minSelect = if (min < 10) {
                        "0$min"
                    } else {
                        "" + min
                    }
                    if (amPM == 0) {
                        textAMPM = requireContext().getString(R.string.am)
                        hourSelectTo = if (hour == 12) {
                            0
                        } else {
                            hour
                        }
                    }
                    if (amPM == 1) {
                        textAMPM = requireContext().getString(R.string.pm)
                        hourSelectTo = if (hour == 12) {
                            hour
                        } else {
                            hour + 12
                        }
                    }
                    minSelectTo = min
                    binding.tvTimeTo.text = "$hour:$minSelect $textAMPM"
                }
            }
        })
        binding.tvDone.setOnClickListener { v: View? ->
            ViewHelper.preventTwoClick(v)
            if (hourSelectFrom == hourSelectTo && minSelectTo == minSelectFrom) {
                toastText(R.string.time_error)
            } else {
                itemAutomationFocus?.let {
                    viewModel.updateTimeFocus(
                        it.nameFocus,
                        TimeUtils.getTimeWithHourStartRepeat(
                            hourSelectFrom,
                            minSelectFrom,
                            listItemRepeat[0].isSelect,
                            listItemRepeat[1].isSelect,
                            listItemRepeat[2].isSelect,
                            listItemRepeat[3].isSelect,
                            listItemRepeat[4].isSelect,
                            listItemRepeat[5].isSelect,
                            listItemRepeat[6].isSelect
                        ),
                        TimeUtils.getTimeWithHourEndRepeat(
                            hourSelectTo,
                            minSelectTo,
                            TimeUtils.getTimeWithHourStartRepeat(
                                hourSelectFrom,
                                minSelectFrom,
                                listItemRepeat[0].isSelect,
                                listItemRepeat[1].isSelect,
                                listItemRepeat[2].isSelect,
                                listItemRepeat[3].isSelect,
                                listItemRepeat[4].isSelect,
                                listItemRepeat[5].isSelect,
                                listItemRepeat[6].isSelect
                            ),
                            listItemRepeat[0].isSelect,
                            listItemRepeat[1].isSelect,
                            listItemRepeat[2].isSelect,
                            listItemRepeat[3].isSelect,
                            listItemRepeat[4].isSelect,
                            listItemRepeat[5].isSelect,
                            listItemRepeat[6].isSelect
                        ),
                        listItemRepeat[0].isSelect,
                        listItemRepeat[1].isSelect,
                        listItemRepeat[2].isSelect,
                        listItemRepeat[3].isSelect,
                        listItemRepeat[4].isSelect,
                        listItemRepeat[5].isSelect,
                        listItemRepeat[6].isSelect,
                        System.currentTimeMillis(),
                        it.lastModify
                    )
                }

                //                if (focus.getName().equals(Constant.WORK)) {
////                    App.itemNextLocationAuto = null;
//                    App.ins.setIsResetLocation(true);
//                }
//                if (focus.getName().equals(Constant.WORK)){
//                    App.PauseLocation = false;
//                }
                App.ins.focusUtils?.sendActionFocus(Constant.TIME_CHANGE, "")
                requireActivity().onBackPressed()
            }
        }
    }

    private fun initAdapter() {
        timeRepeatAdapter = TimeRepeatAdapter()
        timeRepeatAdapter?.setListenClickDay { position: Int, isSelect: Boolean ->
            listItemRepeat[position].isSelect = !isSelect
            timeRepeatAdapter?.notifyItemChanged(position, listItemRepeat[position])
            showTextDay()
        }
        binding.rvDay.adapter = timeRepeatAdapter
    }

    @SuppressLint("SetTextI18n")
    private fun showTextDay() {
        var isExit = false
        for (itemTimeRepeat in listItemRepeat) {
            if (!itemTimeRepeat.isSelect) {
                isExit = true
                break
            }
        }
        if (isExit) {
            binding.tvSumDay.text =
                requireContext().getString(R.string.every) + " " + TimeUtils.getDay(
                    listItemRepeat[0].isSelect,
                    listItemRepeat[1].isSelect,
                    listItemRepeat[2].isSelect,
                    listItemRepeat[3].isSelect,
                    listItemRepeat[4].isSelect,
                    listItemRepeat[5].isSelect,
                    listItemRepeat[6].isSelect,
                    requireContext()
                )
        } else {
            binding.tvSumDay.text = requireContext().getString(R.string.every_day)
        }
        if (!listItemRepeat[0].isSelect && !listItemRepeat[1].isSelect && !listItemRepeat[2].isSelect && !listItemRepeat[3].isSelect && !listItemRepeat[4].isSelect && !listItemRepeat[5].isSelect && !listItemRepeat[6].isSelect) {
            binding.tvSumDay.text = requireContext().getString(R.string.no_repeact)
        }
    }

    private fun showDialogTime(
        customWheelPickerItemBinding: CustomWheelPickerItemBinding,
        hour: Int,
        min: Int
    ) {
        if (hour == 0) {
            customWheelPickerItemBinding.customTimePicker.hour = hour + 12
            customWheelPickerItemBinding.customTimePicker.amPM = 0
            textAMPM = requireContext().getString(R.string.am)
        } else if (hour == 12) {
            customWheelPickerItemBinding.customTimePicker.hour = hour
            customWheelPickerItemBinding.customTimePicker.amPM = 1
            textAMPM = requireContext().getString(R.string.pm)
        } else if (hour > 12) {
            customWheelPickerItemBinding.customTimePicker.hour = hour - 12
            customWheelPickerItemBinding.customTimePicker.amPM = 1
            textAMPM = requireContext().getString(R.string.pm)
        } else {
            customWheelPickerItemBinding.customTimePicker.hour = hour
            customWheelPickerItemBinding.customTimePicker.amPM = 0
            textAMPM = requireContext().getString(R.string.am)
        }
        customWheelPickerItemBinding.customTimePicker.minute = min
    }

    override fun onPermissionGranted() {}
    override fun onDestroy() {
        super.onDestroy()
        onBackPressedCallback.remove()
    }
}
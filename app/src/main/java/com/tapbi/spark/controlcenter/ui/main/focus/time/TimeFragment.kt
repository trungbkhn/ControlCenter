package com.tapbi.spark.controlcenter.ui.main.focus.time

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

class TimeFragment : BaseBindingFragment<FragmentFocusTimeBinding, TimeViewModel>() {
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if ( binding.dlTimeTo.root.visibility == View.VISIBLE)  binding.dlTimeTo.root.visibility =
                    View.GONE
                if ( binding.dlTimeFrom.root.visibility == View.VISIBLE)  binding.dlTimeFrom.root.visibility =
                    View.GONE
                (requireActivity() as MainActivity).navControllerMain.popBackStack(
                    R.id.timeFragment,
                    true
                )
            }
        }
    private var listItemRepeat: MutableList<ItemTimeRepeat> = ArrayList()
    private var timeTo = ""
    private var timeFrom = ""
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
    override fun getViewModel(): Class<TimeViewModel> {
        return TimeViewModel::class.java
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
        if (savedInstanceState != null) {
            focus = Gson().fromJson(
                savedInstanceState.getString(Constant.ITEM_FOCUS_TIME),
                object : TypeToken<FocusIOS?>() {}.type
            )
            listItemRepeat = Gson().fromJson(
                savedInstanceState.getString(Constant.LIST_TIME_REPEAT),
                object : TypeToken<List<ItemTimeRepeat?>?>() {}.type
            )
            showTextDay()
            timeRepeatAdapter?.setData(listItemRepeat)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(Constant.ITEM_FOCUS_TIME, Gson().toJson(focus))
        outState.putString(Constant.LIST_TIME_REPEAT, Gson().toJson(listItemRepeat))
    }

    private fun observerData() {
        mainViewModel.itemFocusNewAutomationTime.observe(viewLifecycleOwner) { focusIOS: FocusIOS? ->
            if (focusIOS != null) {
                focus = focusIOS
                setDataRecyclerRepeat(focusIOS.colorFocus)
            }
        }
    }

    private fun initView() {
        (requireActivity() as MainActivity).setColorNavigation(R.color.color_F2F2F6)
        setUpPaddingStatusBar( binding.layoutFocusTime)
        timeTo = TimeUtils.getTimeAutoWithCurrentMini(
            requireContext(), TimeUtils.getHourWithTimeMini(
                System.currentTimeMillis()
            ), TimeUtils.getMinuteWithTimeMini(System.currentTimeMillis())
        )
        timeFrom = TimeUtils.getTimeAutoWithCurrentMini(
            requireContext(), TimeUtils.getHourWithTimeMini(
                System.currentTimeMillis()
            ), TimeUtils.getMinuteWithTimeMini(System.currentTimeMillis())
        )
        hourSelectTo = TimeUtils.getHourWithTimeMini(System.currentTimeMillis())
        minSelectTo = TimeUtils.getMinuteWithTimeMini(System.currentTimeMillis())
        hourSelectFrom = TimeUtils.getHourWithTimeMini(System.currentTimeMillis())
        minSelectFrom = TimeUtils.getMinuteWithTimeMini(System.currentTimeMillis())
         binding.tvTimeFrom.text = timeFrom
         binding.tvTimeTo.text = timeTo
        initTimeRepeatAdapter()
        initListener()
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
            if ( binding.dlTimeTo.root.visibility == View.VISIBLE) {
                 binding.dlTimeTo.root.visibility = View.GONE
                 binding.tvTimeTo.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
            }
            showDialogTime( binding.dlTimeFrom, hourSelectFrom, minSelectFrom)
        }
         binding.tvTimeTo.setOnClickListener {
             binding.tvTimeTo.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_007AFF
                )
            )
             binding.dlTimeTo.root.visibility = View.VISIBLE
            if ( binding.dlTimeFrom.root.visibility == View.VISIBLE) {
                 binding.dlTimeFrom.root.visibility = View.GONE
                 binding.tvTimeFrom.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
            }
            showDialogTime( binding.dlTimeTo, hourSelectTo, minSelectTo)
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
            if ( binding.dlTimeTo.root.visibility == View.VISIBLE)  binding.dlTimeTo.root.visibility =
                View.GONE
            if ( binding.dlTimeFrom.root.visibility == View.VISIBLE)  binding.dlTimeFrom.root.visibility =
                View.GONE
        }
         binding.dlTimeTo.customTimePicker.setWheelListener(object :CustomTimeToPicker.Listener{
            override fun didSelectData(hour: Int, min: Int, amPM: Int) {
                if (timeTo == TimeUtils.getTimeAutoCurrentMini(requireContext(), hour, min, amPM)) {
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
         binding.dlTimeFrom.customTimePicker.setWheelListener(object :CustomTimeToPicker.Listener {
            override fun didSelectData(hour: Int, min: Int, amPM: Int) {
                if (timeFrom == TimeUtils.getTimeAutoCurrentMini(requireContext(), hour, min, amPM)) {
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
         binding.tvDone.setOnClickListener { v: View? ->
            if (focus == null) {
                toastText(R.string.text_error_try_again)
                return@setOnClickListener
            }
            if ( binding.dlTimeTo.root.visibility == View.VISIBLE)  binding.dlTimeTo.root.visibility =
                View.GONE
            if ( binding.dlTimeFrom.root.visibility == View.VISIBLE)  binding.dlTimeFrom.root.visibility =
                View.GONE
            if (hourSelectFrom == hourSelectTo && minSelectFrom == minSelectTo) {
                toastText(R.string.time_error)
            } else {
                ViewHelper.preventTwoClick(v)
                val itemTurnOn = ItemTurnOn(
                    focus?.name,
                    true,
                    false,
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
                    "",
                    0.0,
                    0.0,
                    "",
                    "",
                    Constant.TIME,
                    System.currentTimeMillis()
                )
                viewModel.insertAutomationFocus(itemTurnOn, requireContext())
                //                if (focus.getName().equals(Constant.WORK)){
//                    App.PauseLocation = false;
//                }
                mainViewModel.itemFocusDetail.postValue(focus)
                App.ins.setIsResetLocation(true)
                (requireActivity() as MainActivity).navigate(
                    R.id.action_timeFragment_to_focusDetailFragment,
                    R.id.timeFragment
                )
            }
        }
    }

    private fun initTimeRepeatAdapter() {
        timeRepeatAdapter = TimeRepeatAdapter()
        timeRepeatAdapter?.setListenClickDay { position: Int, isSelect: Boolean ->
            listItemRepeat[position].isSelect = !isSelect
            timeRepeatAdapter?.notifyItemChanged(position, listItemRepeat[position])
            showTextDay()
        }
         binding.rvDay.adapter = timeRepeatAdapter
    }

    private fun setDataRecyclerRepeat(color: String) {
        listItemRepeat.clear()
        listItemRepeat.add(ItemTimeRepeat(getString(R.string.month), color, true))
        listItemRepeat.add(ItemTimeRepeat(getString(R.string.tu), color, true))
        listItemRepeat.add(ItemTimeRepeat(getString(R.string.we), color, true))
        listItemRepeat.add(ItemTimeRepeat(getString(R.string.th), color, true))
        listItemRepeat.add(ItemTimeRepeat(getString(R.string.fr), color, true))
        listItemRepeat.add(ItemTimeRepeat(getString(R.string.sa), color, true))
        listItemRepeat.add(ItemTimeRepeat(getString(R.string.su), color, true))
        showTextDay()
        timeRepeatAdapter?.setData(listItemRepeat)
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
    override fun onDestroyView() {
        super.onDestroyView()
        onBackPressedCallback.remove()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
package sh.tyy.wheelpicker

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import sh.tyy.wheelpicker.core.BaseWheelPickerView
import sh.tyy.wheelpicker.core.TextWheelAdapter
import sh.tyy.wheelpicker.core.TextWheelPickerView
import sh.tyy.wheelpicker.databinding.TriplePickerViewBinding

class CustomTimeToPicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), BaseWheelPickerView.WheelPickerViewListener {
    interface Listener {
        fun didSelectData(hour: Int, min: Int, amPM: Int)
    }

    private val amPMAdapter = TextWheelAdapter()
    private val hourAdapter = TextWheelAdapter()
    private val minuteAdapter = TextWheelAdapter()
    private var listener: Listener? = null

    var hour: Int
        set(value) {
            Log.d(TAG, "value: " + value)
            binding.leftPicker.setSelectedIndex(" binding.leftPicker", value - 1, true, null)
//            hourAdapter.notifyDataSetChanged()
        }
        get() = binding.leftPicker.selectedIndex + 1

    var minute: Int
        set(value) {
            binding.midPicker.setSelectedIndex(" binding.leftPicker", value, true, null)
        }
        get() = binding.midPicker.selectedIndex

    var amPM: Int
        set(value) {
            binding.rightPicker.setSelectedIndex(" binding.rightPicker", value, true, null)
        }
        get() = binding.rightPicker.selectedIndex
    private val highlightView: View = run {
        val view = View(context)
        view.background =
            ContextCompat.getDrawable(context, R.drawable.text_wheel_highlight_bg)
        view
    }
    private val binding: TriplePickerViewBinding =
        TriplePickerViewBinding.inflate(LayoutInflater.from(context), this)

    init {


        binding.leftPicker.setAdapter(hourAdapter)
        hourAdapter.values = (1 until 13).map { TextWheelPickerView.Item("$it", "$it") }
        binding.midPicker.setAdapter(minuteAdapter)
        minuteAdapter.values = (0 until 60).map {
            TextWheelPickerView.Item(
                "$it",
                context.getString(R.string.day_time_picker_format_day, it)
            )
        }
        binding.rightPicker.setAdapter(amPMAdapter)
        amPMAdapter.values = (arrayOf(
            context.getString(R.string.Am),
            context.getString(R.string.Pm)
        )).map { TextWheelPickerView.Item(it, it) }



        addView(highlightView)
        (highlightView.layoutParams as? LayoutParams)?.apply {
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height =
                context.resources.getDimensionPixelSize(R.dimen.text_wheel_picker_item_height)
            gravity = Gravity.CENTER_VERTICAL
        }
        binding.leftPicker.isCircular = false
        binding.midPicker.isCircular = false
        binding.rightPicker.isCircular = false

        binding.leftPicker.isHapticFeedbackEnabled = false
        binding.midPicker.isHapticFeedbackEnabled = false
        binding.rightPicker.isHapticFeedbackEnabled = false
//        binding.leftPicker.setWheelListener(object : BaseWheelPickerView.WheelPickerViewListener {
//            override fun didSelectItem(picker: BaseWheelPickerView, index: Int) {
//                listener?.didSelectDataHour(index + 1)
//            }
//
//        })
//        binding.midPicker.setWheelListener(object : BaseWheelPickerView.WheelPickerViewListener {
//            override fun didSelectItem(picker: BaseWheelPickerView, index: Int) {
//                listener?.didSelectDataMin(index)
//            }
//
//        })
//        binding.rightPicker.setWheelListener(object : BaseWheelPickerView.WheelPickerViewListener {
//            override fun didSelectItem(picker: BaseWheelPickerView, index: Int) {
//                listener?.didSelectDataAMPM(index)
//            }
//        })
        binding.leftPicker.setWheelListener(this)
        binding.midPicker.setWheelListener(this)
        binding.rightPicker.setWheelListener(this)
    }

    fun setWheelListener(listener: Listener) {
        this.listener = listener
    }

    private val TAG = "customTimePicker"
    override fun didSelectItem(picker: BaseWheelPickerView, index: Int) {
        listener?.didSelectData(hour, minute, amPM)
    }


}
package com.tapbi.spark.controlcenter.ui.main.edgetriggers

import android.app.Activity
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AlertDialog
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.ironman.trueads.common.Common
import com.tapbi.spark.controlcenter.App.Companion.tinyDB
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.common.models.EventCustomEdge
import com.tapbi.spark.controlcenter.data.repository.ThemeHelper
import com.tapbi.spark.controlcenter.databinding.ActivitySettingTouchBinding
import com.tapbi.spark.controlcenter.ui.base.BaseBindingFragment
import com.tapbi.spark.controlcenter.ui.main.MainActivity
import com.tapbi.spark.controlcenter.ui.main.MainActivity.Companion.isDispatchLongTouchEvent
import com.tapbi.spark.controlcenter.utils.Analytics
import com.tapbi.spark.controlcenter.utils.MethodUtils
import com.tapbi.spark.controlcenter.utils.VibratorUtils
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper
import com.tapbi.spark.controlcenter.views.SwitchButtonIos
import com.tapbi.spark.controlcenter.views.TabLayoutEdge.OnTabListener
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

class SettingTouchFragment :
    BaseBindingFragment<ActivitySettingTouchBinding, SettingTouchViewModel>() {
    enum class TabEdge {
        TOP,
        LEFT,
        RIGHT,
        BOTTOM
    }

    private var tabEdgeCurrent = TabEdge.TOP
    private var colorSelectedCurrent = 0
    private var isTypeNotyShade = false
    private var dialogColor: AlertDialog? = null
    override fun getViewModel(): Class<SettingTouchViewModel> {
        return SettingTouchViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.activity_setting_touch

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        setUpPaddingStatusBar(binding.layoutParent)
        (requireActivity() as MainActivity).setColorNavigation(R.color.colorPrimary)
//        isTypeNotyShade = tinyDB.getInt(
//            Constant.TYPE_NOTY,
//            Constant.VALUE_CONTROL_CENTER_OS
//        ) == Constant.VALUE_SHADE
        isTypeNotyShade = ThemeHelper.itemControl.idCategory == Constant.VALUE_SHADE
        initView()
        listener()
        loadAds()
    }

    private fun initView() {
        setUpTextViewActivate()
        setUpViewTop()

    }

    private fun setUpTextViewActivate() {
        //setup text view activate
        //check permission
        if (checkPermissionService()) {
            if (tinyDB.getInt(
                    Constant.IS_ENABLE,
                    Constant.IS_DISABLE
                ) == Constant.DEFAULT_IS_ENABLE
            ) {
                binding.tvActivate.visibility = View.GONE
            } else {
                binding.tvActivate.text = String.format(
                    getString(R.string.text_activate_home),
                    getString(R.string.app_name)
                )
                binding.tvActivate.isClickable = false
            }
        } else {
            binding.tvActivate.setText(R.string.text_allow_permisson_to_use_functions)
            binding.tvActivate.setOnClickListener {
                if (binding.tvActivate.text == getString(R.string.text_allow_permisson_to_use_functions)) {
                    (activity as MainActivity).intentPermissionActivity()
                }
            }
            binding.tvActivate.isClickable = true
        }
    }

    private fun listener() {
        binding.tlEdge.setOnTabListener(object : OnTabListener {
            override fun onTop() {
                setUpViewTop()
            }

            override fun onLeft() {
                setUpViewLeft()
            }

            override fun onRight() {
                setUpViewRight()
            }

            override fun onBottom() {
                setUpViewBottom()
            }
        })
        binding.tvColorNoty.setOnClickListener {
            isDispatchLongTouchEvent
            showDialogColor(false)
        }
        binding.tvColorControl.setOnClickListener {
            isDispatchLongTouchEvent
            showDialogColor(true)
        }
        binding.icBack.setOnClickListener {
            ViewHelper.preventTwoClick(it)
            (activity as MainActivity).navControllerMain.popBackStack()
        }
        binding.seekBarLength.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    changeLength(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        binding.seekBarSize.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    changeSize(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        binding.seekBarPosition.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    changePosition(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        binding.swVibrate.onCheckedChangeListener =
            SwitchButtonIos.OnCheckedChangeListener { isChecked: Boolean ->
                changeVibrate(isChecked)
                if (isChecked) {
                    VibratorUtils.getInstance(binding.swVibrate.context)
                        .vibrator(VibratorUtils.TIME_DEFAULT)
                }
            }
        binding.swEnabled.onCheckedChangeListener =
            SwitchButtonIos.OnCheckedChangeListener { enabled: Boolean -> changeEnabled(enabled) }
    }

    override fun onPermissionGranted() {}
    private fun showDialogColor(isTypeControl: Boolean) {
        var colorOld: Int
        val textShow: String
        if (isTypeControl) {
            textShow = getString(R.string.menu_color_control)
            colorOld = when (tabEdgeCurrent) {
                TabEdge.LEFT -> tinyDB.getInt(Constant.COLOR_EDGE_CONTROL_LEFT, Color.TRANSPARENT)
                TabEdge.RIGHT -> tinyDB.getInt(Constant.COLOR_EDGE_CONTROL_RIGHT, Color.TRANSPARENT)
                TabEdge.BOTTOM -> tinyDB.getInt(
                    Constant.COLOR_EDGE_CONTROL_BOTTOM,
                    Color.TRANSPARENT
                )

                else -> tinyDB.getInt(Constant.COLOR_EDGE_CONTROL_TOP, Color.TRANSPARENT)
            }
        } else {
            textShow = getString(R.string.menu_color_noty)
            colorOld = when (tabEdgeCurrent) {
                TabEdge.LEFT -> tinyDB.getInt(Constant.COLOR_EDGE_NOTY_LEFT, Color.TRANSPARENT)
                TabEdge.RIGHT -> tinyDB.getInt(Constant.COLOR_EDGE_NOTY_RIGHT, Color.TRANSPARENT)
                TabEdge.BOTTOM -> tinyDB.getInt(Constant.COLOR_EDGE_NOTY_BOTTOM, Color.TRANSPARENT)
                else -> tinyDB.getInt(Constant.COLOR_EDGE_NOTY_TOP, Color.TRANSPARENT)
            }

        }
        if (!isAdded || context == null || (context as? Activity)?.isFinishing == true) {
            return
        }
        Timber.e("hachung colorOld : $colorOld /check ${(colorOld == Color.TRANSPARENT)}")
        if (colorOld >= Color.TRANSPARENT) {
            colorOld = Color.WHITE
        }

        if (dialogColor?.isShowing == true) {
            dialogColor?.dismiss()
        }
        colorSelectedCurrent = colorOld
        dialogColor =
            ColorPickerDialogBuilder.with(context).setTitle(textShow).initialColor(colorOld)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER).density(12)
                .setOnColorChangedListener { selectedColor: Int ->
                    colorSelectedCurrent = selectedColor
                }
                .setOnColorSelectedListener { selectedColor: Int ->
                    colorSelectedCurrent = selectedColor
                }
                .setPositiveButton(getString(R.string.ok)) { _: DialogInterface?, _: Int, _: Array<Int?>? ->
                    if (isTypeControl) {
                        changeColorControl(colorSelectedCurrent)
                    } else {
                        changeColorNoty(colorSelectedCurrent)
                    }
                }
                .setNegativeButton(getString(R.string.cancel)) { _: DialogInterface?, _: Int -> }
                .build()
        if (dialogColor?.isShowing != true) {
            dialogColor?.show()
        }
    }

    override fun onResume() {
        super.onResume()
        isSetting = true
        if (activity == null) {
            return
        }
        Analytics.getInstance().setCurrentScreen(activity, javaClass.simpleName)
        sentToService(Constant.CHANGE_STATUS_EDIT_EDGE, true)
        setUpTextViewActivate()
    }

    override fun onPause() {
        super.onPause()
        sentToService(Constant.CHANGE_STATUS_EDIT_EDGE, false)
    }

    override fun onStop() {
        super.onStop()
        isSetting = false
        Timber.e("NVQ onStop")
        //        saveMargin();
    }

    override fun onDestroy() {
        super.onDestroy()
        dialogColor?.dismiss()
    }

    private fun changeEnabled(enabled: Boolean) {
        var key: String? = null
        when (tabEdgeCurrent) {
            TabEdge.LEFT -> key = Constant.KEY_ENABLED_EDGE_LEFT
            TabEdge.RIGHT -> key = Constant.KEY_ENABLED_EDGE_RIGHT
            TabEdge.BOTTOM -> key = Constant.KEY_ENABLED_EDGE_BOTTOM
            else -> {}
        }
        if (key != null) {
            tinyDB.putBoolean(key, enabled)
            sentToService(Constant.CHANGE_ENABLED_EDGE, enabled)
            if (enabled && MethodUtils.isEdgeToEdgeEnabled(context)) {
                toastText(R.string.conflict_navigation_mode_swipe_gestures_system)
            }
        }
    }

    private fun changePosition(percent: Int) {
        when (tabEdgeCurrent) {
            TabEdge.LEFT -> tinyDB.putInt(Constant.POSITION_TOUCH_EDGE_LEFT_PERCENT, percent)
            TabEdge.RIGHT -> tinyDB.putInt(Constant.POSITION_TOUCH_EDGE_RIGHT_PERCENT, percent)
            TabEdge.BOTTOM -> tinyDB.putInt(Constant.POSITION_TOUCH_EDGE_BOTTOM_PERCENT, percent)
            else -> {}
        }
        sentToService(Constant.CHANGE_POSITION_EDGE, percent)
    }

    private fun changeLength(percent: Int) {
        when (tabEdgeCurrent) {
            TabEdge.LEFT -> tinyDB.putInt(Constant.LENGTH_TOUCH_EDGE_LEFT_PERCENT, percent)
            TabEdge.RIGHT -> tinyDB.putInt(Constant.LENGTH_TOUCH_EDGE_RIGHT_PERCENT, percent)
            TabEdge.BOTTOM -> tinyDB.putInt(Constant.LENGTH_TOUCH_EDGE_BOTTOM_PERCENT, percent)
            else -> {}
        }
        sentToService(Constant.CHANGE_LENGTH_EDGE, percent)
    }

    /**
     * @param percent is percent thickness, default is 50% corresponding to height status bar device
     */
    private fun changeSize(percent: Int) {
        val key: String = when (tabEdgeCurrent) {
            TabEdge.TOP -> Constant.SIZE_TOUCH_EDGE_TOP_PERCENT
            TabEdge.LEFT -> Constant.SIZE_TOUCH_EDGE_LEFT_PERCENT
            TabEdge.RIGHT -> Constant.SIZE_TOUCH_EDGE_RIGHT_PERCENT
            TabEdge.BOTTOM -> Constant.SIZE_TOUCH_EDGE_BOTTOM_PERCENT
        }
        tinyDB.putInt(key, percent)
        sentToService(Constant.CHANGE_SIZE_EDGE, percent)
    }

    private fun sentToService(action: String, value: Int) {
        val eventCustomEdge = EventCustomEdge(action, tabEdgeCurrent, value)
        EventBus.getDefault().post(eventCustomEdge)
    }

    private fun sentToService(action: String, value: Boolean) {
        val eventCustomEdge = EventCustomEdge(action, tabEdgeCurrent, value)
        EventBus.getDefault().post(eventCustomEdge)
    }

    private fun changeColorControl(color: Int) {
        val key: String = when (tabEdgeCurrent) {
            TabEdge.LEFT -> Constant.COLOR_EDGE_CONTROL_LEFT
            TabEdge.RIGHT -> Constant.COLOR_EDGE_CONTROL_RIGHT
            TabEdge.BOTTOM -> Constant.COLOR_EDGE_CONTROL_BOTTOM
            TabEdge.TOP -> Constant.COLOR_EDGE_CONTROL_TOP
        }
        tinyDB.putInt(key, color)
        sentToService(Constant.CHANGE_COLOR_CONTROL, color)
    }

    private fun changeColorNoty(color: Int) {
        val key: String = when (tabEdgeCurrent) {
            TabEdge.LEFT -> Constant.COLOR_EDGE_NOTY_LEFT
            TabEdge.RIGHT -> Constant.COLOR_EDGE_NOTY_RIGHT
            TabEdge.BOTTOM -> Constant.COLOR_EDGE_NOTY_BOTTOM
            TabEdge.TOP -> Constant.COLOR_EDGE_NOTY_TOP
        }
        tinyDB.putInt(key, color)
        sentToService(Constant.CHANGE_COLOR_NOTY, color)
    }

    private fun showCustomLayout(isTypeTop: Boolean) {
        binding.lnEnabled.visibility = if (isTypeTop) View.GONE else View.VISIBLE
        binding.lnLength.visibility =
            if (isTypeTop) View.GONE else View.VISIBLE
        binding.lnPosition.visibility = if (isTypeTop) View.GONE else View.VISIBLE
        binding.tvColorNoty.visibility =
            if (isTypeNotyShade) View.GONE else View.VISIBLE
    }

    private fun changeVibrate(isVibrate: Boolean) {
        val key: String = when (tabEdgeCurrent) {
            TabEdge.TOP -> Constant.VIBRATOR_EDGE_TOP
            TabEdge.LEFT -> Constant.VIBRATOR_EDGE_LEFT
            TabEdge.RIGHT -> Constant.VIBRATOR_EDGE_RIGHT
            TabEdge.BOTTOM -> Constant.VIBRATOR_EDGE_BOTTOM
        }
        tinyDB.putBoolean(key, isVibrate)
    }

    private fun setUpViewTop() {
        tabEdgeCurrent = TabEdge.TOP
        binding.swVibrate.setChecked(
            tinyDB.getBoolean(
                Constant.VIBRATOR_EDGE_TOP,
                Constant.VALUE_DEFAULT_VIBRATOR
            )
        )
        binding.seekBarSize.progress = tinyDB.getInt(
            Constant.SIZE_TOUCH_EDGE_TOP_PERCENT,
            Constant.SIZE_TOUCH_EDGE_PERCENT_DEFAULT
        )
        showCustomLayout(true)
    }

    private fun setUpViewLeft() {
        tabEdgeCurrent = TabEdge.LEFT
        binding.swEnabled.setChecked(
            tinyDB.getBoolean(
                Constant.KEY_ENABLED_EDGE_LEFT,
                Constant.DEFAULT_ENABLED_EDGE_LEFT_RIGHT_BOTTOM
            )
        )
        binding.swVibrate.setChecked(
            tinyDB.getBoolean(
                Constant.VIBRATOR_EDGE_LEFT,
                Constant.VALUE_DEFAULT_VIBRATOR
            )
        )
        binding.seekBarLength.progress = tinyDB.getInt(
            Constant.LENGTH_TOUCH_EDGE_LEFT_PERCENT,
            Constant.LENGTH_TOUCH_EDGE_PERCENT_DEFAULT
        )
        binding.seekBarSize.progress = tinyDB.getInt(
            Constant.SIZE_TOUCH_EDGE_LEFT_PERCENT,
            Constant.SIZE_TOUCH_EDGE_PERCENT_DEFAULT
        )
        binding.seekBarPosition.progress = tinyDB.getInt(
            Constant.POSITION_TOUCH_EDGE_LEFT_PERCENT,
            Constant.POSITION_TOUCH_EDGE_PERCENT_DEFAULT
        )
        showCustomLayout(false)
    }

    private fun setUpViewRight() {
        tabEdgeCurrent = TabEdge.RIGHT
        binding.swEnabled.setChecked(
            tinyDB.getBoolean(
                Constant.KEY_ENABLED_EDGE_RIGHT,
                Constant.DEFAULT_ENABLED_EDGE_LEFT_RIGHT_BOTTOM
            )
        )
        binding.swVibrate.setChecked(
            tinyDB.getBoolean(
                Constant.VIBRATOR_EDGE_RIGHT,
                Constant.VALUE_DEFAULT_VIBRATOR
            )
        )
        binding.seekBarLength.progress = tinyDB.getInt(
            Constant.LENGTH_TOUCH_EDGE_RIGHT_PERCENT,
            Constant.LENGTH_TOUCH_EDGE_PERCENT_DEFAULT
        )
        binding.seekBarSize.progress = tinyDB.getInt(
            Constant.SIZE_TOUCH_EDGE_RIGHT_PERCENT,
            Constant.SIZE_TOUCH_EDGE_PERCENT_DEFAULT
        )
        binding.seekBarPosition.progress = tinyDB.getInt(
            Constant.POSITION_TOUCH_EDGE_RIGHT_PERCENT,
            Constant.POSITION_TOUCH_EDGE_PERCENT_DEFAULT
        )
        showCustomLayout(false)
    }

    private fun setUpViewBottom() {
        tabEdgeCurrent = TabEdge.BOTTOM
        binding.swEnabled.setChecked(
            tinyDB.getBoolean(
                Constant.KEY_ENABLED_EDGE_BOTTOM,
                Constant.DEFAULT_ENABLED_EDGE_LEFT_RIGHT_BOTTOM
            )
        )
        binding.swVibrate.setChecked(
            tinyDB.getBoolean(
                Constant.VIBRATOR_EDGE_BOTTOM,
                Constant.VALUE_DEFAULT_VIBRATOR
            )
        )
        binding.seekBarLength.progress = tinyDB.getInt(
            Constant.LENGTH_TOUCH_EDGE_BOTTOM_PERCENT,
            Constant.LENGTH_TOUCH_EDGE_PERCENT_DEFAULT
        )
        binding.seekBarSize.progress = tinyDB.getInt(
            Constant.SIZE_TOUCH_EDGE_BOTTOM_PERCENT,
            Constant.SIZE_TOUCH_EDGE_PERCENT_DEFAULT
        )
        binding.seekBarPosition.progress = tinyDB.getInt(
            Constant.POSITION_TOUCH_EDGE_BOTTOM_PERCENT,
            Constant.POSITION_TOUCH_EDGE_PERCENT_DEFAULT
        )
        showCustomLayout(false)
    }

    private fun loadAds() {
        loadAdsNative(
            binding.flAds,
            Common.getMapIdAdmobApplovin(
                requireActivity(),
                R.array.admob_native_id_setting_touch,
                R.array.applovin_id_native_setting_touch
            )
        )

    }

    companion object {
        var isSetting = false
    }
}

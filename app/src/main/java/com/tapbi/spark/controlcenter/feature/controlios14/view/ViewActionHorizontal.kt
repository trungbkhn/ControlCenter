package com.tapbi.spark.controlcenter.feature.controlios14.view

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.core.app.ActivityCompat
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.data.repository.ThemesRepository.isControlEditing
import com.tapbi.spark.controlcenter.databinding.LayoutActionHorizontalBinding
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ConstraintLayoutBase
import com.tapbi.spark.controlcenter.interfaces.IListenActionClick
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614
import com.tapbi.spark.controlcenter.utils.SettingUtils
import com.tapbi.spark.controlcenter.utils.VibratorUtils
import com.tapbi.spark.controlcenter.views.ViewDialogContent
import timber.log.Timber

class ViewActionHorizontal :ConstraintLayoutBase {
    var mode :String = ""
    var binding :LayoutActionHorizontalBinding? = null
    var colorUnselect: Int = Color.parseColor("#26FFFFFF")
    var colorSelect: Int = Color.parseColor("#007AFF")
    private var fade_in: ScaleAnimation? = null
    private var isLongClick = false
    private val runnable = Runnable {
        isLongClick = true
        this.longClick()
    }
    constructor(context: Context?) : super(context){
        initView()
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        initView(attrs)
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){initView(attrs)}

    fun initView(attrs: AttributeSet? = null){
        binding = LayoutActionHorizontalBinding.inflate(LayoutInflater.from(App.ins), this, true)
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.ViewActionHorizontal)
            colorSelect = typedArray.getColor(
                R.styleable.ViewActionHorizontal_colorSelect,
                Color.parseColor("#007AFF")
            )
            typedArray.recycle()
        }
        mode = tag.toString()
        initName()
    }
    private fun initName(){
        when(mode){
            Constant.STRING_ACTION_AIRPLANE_MODE ->{
                binding?.tvName?.text = context.getString(R.string.airplane_mode);
                binding?.iconControl?.setImageResource(R.drawable.ic_airplane_ios);
            }
            Constant.STRING_ACTION_WIFI ->{
                binding?.tvName?.text = context.getString(R.string.wifi);
                binding?.iconControl?.setImageResource(R.drawable.ic_wifi_ios);
            }
            Constant.STRING_ACTION_DATA_MOBILE ->{
                binding?.tvName?.text = context.getString(R.string.data_mobile);
                binding?.iconControl?.setImageResource(R.drawable.ic_data_ios);
            }
            Constant.STRING_ACTION_BLUETOOTH -> {
                binding?.tvName?.text = context.getString(R.string.bluetooth);
                binding?.iconControl?.setImageResource(R.drawable.ic_bluetoolh_ios);
            }
            Constant.STRING_ACTION_SILENT -> {
                binding?.tvName?.text = context.getString(R.string.do_not_disturb);
                binding?.iconControl?.setImageResource(R.drawable.ic_silent_ios);
            }
            Constant.STRING_ACTION_LOCATION -> {
                binding?.tvName?.text = context.getString(R.string.location);
                binding?.iconControl?.setImageResource(R.drawable.ic_location_ios);
            }
            Constant.STRING_ACTION_SYNC -> {
                binding?.tvName?.text = context.getString(R.string.sync);
                binding?.iconControl?.setImageResource(R.drawable.ic_sync_ios);
            }
            else ->{}
        }
    }
    fun setBackgroundState(isSelect:Boolean){
        binding?.iconControl?.setBackgroundColor(if (isSelect) colorSelect else colorUnselect)
        initState(isSelect)
    }
    private fun initState(isSelect: Boolean) {
        if (isSelect){
            binding?.tvState?.text = context.getString(R.string.text_on)
        }else{
            binding?.tvState?.text = context.getString(R.string.text_off)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    animationDown()
                    if (!isControlEditing) handler.postDelayed(
                        runnable,
                        ViewConfiguration.getLongPressTimeout().toLong()
                    )
                }

                MotionEvent.ACTION_CANCEL -> {
                    handler.removeCallbacks(runnable)
                }

                MotionEvent.ACTION_UP -> {
                    animationUp()
                    handler.removeCallbacks(runnable)
                    if (checkClick(
                            event.x,
                            event.y
                        ) && !isLongClick  &&  !isControlEditing
                    ) {
                        click()
                    }
                    isLongClick = false
                }
            }

            return (!isControlEditing)
    }
    private fun click(){
        if (mode == Constant.STRING_ACTION_AIRPLANE_MODE  || mode == Constant.STRING_ACTION_DATA_MOBILE  || mode == Constant.STRING_ACTION_WIFI  || mode == Constant.STRING_ACTION_LOCATION ) {
            if (!NotyControlCenterServicev614.getInstance().allowClickAction()) {
                if (NotyControlCenterServicev614.getInstance() != null) {
                    NotyControlCenterServicev614.getInstance().showToast(context.getString(R.string.wait_until_job_done))
                }
                return
            }
        }
        when(mode){
            Constant.STRING_ACTION_AIRPLANE_MODE ->{
                statAniZoom(binding?.iconControl)
                NotyControlCenterServicev614.getInstance()
                    .setHandingAction(object : IListenActionClick {
                        override fun noFindAction() {
                            stopAniZoom(binding?.iconControl)
                        }

                        override fun actionClicked() {
                            stopAniZoom(binding?.iconControl)
                        }
                    }, Constant.STRING_ACTION_AIRPLANE_MODE)
            }
            Constant.STRING_ACTION_WIFI ->{
                NotyControlCenterServicev614.getInstance().setWifiNoty()
            }
            Constant.STRING_ACTION_DATA_MOBILE ->{
                if (!NotyControlCenterServicev614.getInstance().isAirPlaneModeEnabled) { statAniZoom(binding?.iconControl) }
                NotyControlCenterServicev614.getInstance()
                    .setHandingAction(object : IListenActionClick {
                        override fun noFindAction() {
                            stopAniZoom(binding?.iconControl)
                        }

                        override fun actionClicked() {
                            stopAniZoom(binding?.iconControl)
                        }
                    }, Constant.STRING_ACTION_DATA_MOBILE)

            }
            Constant.STRING_ACTION_BLUETOOTH -> {
                statAniZoom(binding?.iconControl)
                SettingUtils.setOnOffBluetooth(context)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    NotyControlCenterServicev614.getInstance().closeCenterWhenClick3G()
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    NotyControlCenterServicev614.getInstance().closeCenterWhenClick3G()
                }
            }
            Constant.STRING_ACTION_SILENT -> {
                val notificationManager =
                    context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    try {
                        if (Settings.Global.getInt(context.contentResolver, "zen_mode") == 0) {
                            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
                        } else {
                            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
                        }
                    } catch (e: Exception) {
                        Timber.d(e)
                    }
                }
            }
            Constant.STRING_ACTION_LOCATION -> {
                statAniZoom(binding?.iconControl)
                NotyControlCenterServicev614.getInstance()
                    .setHandingAction(object : IListenActionClick {
                        override fun noFindAction() {
                            stopAniZoom(binding?.iconControl)
                        }

                        override fun actionClicked() {
                            stopAniZoom(binding?.iconControl)
                        }
                    }, Constant.STRING_ACTION_LOCATION)
            }
            Constant.STRING_ACTION_SYNC -> {
                val b= SettingUtils.isSyncAutomaticallyEnable()
                SettingUtils.setSyncAutomatically()
                setBackgroundState(!b)
            }
            else ->{}
        }

    }
    private fun longClick(){
        when(mode){
            Constant.STRING_ACTION_AIRPLANE_MODE ->{
                if (App.tinyDB.getBoolean(
                        Constant.VIBRATOR_CONTROL_LONG_CLICK,
                        Constant.VALUE_DEFAULT_VIBRATOR
                    )
                ) {
                    VibratorUtils.getInstance(context).vibrator(VibratorUtils.TIME_DEFAULT)
                }
                SettingUtils.intentChangeAirPlane(context)
                NotyControlCenterServicev614.getInstance().closeCenterWhenClick3G()
            }
            Constant.STRING_ACTION_WIFI ->{
                if (App.tinyDB.getBoolean(
                        Constant.VIBRATOR_CONTROL_LONG_CLICK,
                        Constant.VALUE_DEFAULT_VIBRATOR
                    )
                ) {
                    VibratorUtils.getInstance(context).vibrator(VibratorUtils.TIME_DEFAULT)
                }
                SettingUtils.intentChangeWifi(context)
                Timber.e("NVQ onHideControl1")
                NotyControlCenterServicev614.getInstance().closeCenterWhenClick3G()
            }
            Constant.STRING_ACTION_DATA_MOBILE ->{

                if (App.tinyDB.getBoolean(
                        Constant.VIBRATOR_CONTROL_LONG_CLICK,
                        Constant.VALUE_DEFAULT_VIBRATOR
                    )
                ) {
                    VibratorUtils.getInstance(context).vibrator(VibratorUtils.TIME_DEFAULT)
                }
                if (!SettingUtils.hasSimCard(context)) {
                    NotyControlCenterServicev614.getInstance()
                        .showDialogContent(object :ViewDialogContent.Listener {
                            override fun onClose() {
                                NotyControlCenterServicev614.getInstance().closeCenterWhenClick3G()
                            }
                        })
                    return
                }
                SettingUtils.intentChangeDataMobile(context)
                NotyControlCenterServicev614.getInstance().closeCenterWhenClick3G()
            }
            Constant.STRING_ACTION_BLUETOOTH -> {
                if (App.tinyDB.getBoolean(
                        Constant.VIBRATOR_CONTROL_LONG_CLICK,
                        Constant.VALUE_DEFAULT_VIBRATOR
                    )
                ) {
                    VibratorUtils.getInstance(context).vibrator(VibratorUtils.TIME_DEFAULT)
                }
                SettingUtils.intentChangeBlueTooth(context)
                NotyControlCenterServicev614.getInstance().closeCenterWhenClick3G()
            }
            Constant.STRING_ACTION_SILENT -> {}
            Constant.STRING_ACTION_LOCATION -> {
                if (App.tinyDB.getBoolean(
                        Constant.VIBRATOR_CONTROL_LONG_CLICK,
                        Constant.VALUE_DEFAULT_VIBRATOR
                    )
                ) {
                    VibratorUtils.getInstance(context).vibrator(VibratorUtils.TIME_DEFAULT)
                }
                SettingUtils.intentChangeLocation(context)
                NotyControlCenterServicev614.getInstance().closeCenterWhenClick3G()
            }
            Constant.STRING_ACTION_SYNC -> {
                if (App.tinyDB.getBoolean(
                        Constant.VIBRATOR_CONTROL_LONG_CLICK,
                        Constant.VALUE_DEFAULT_VIBRATOR
                    )
                ) {
                    VibratorUtils.getInstance(context).vibrator(VibratorUtils.TIME_DEFAULT)
                }
                SettingUtils.intentChangeSync(context)
                NotyControlCenterServicev614.getInstance().closeCenterWhenClick3G()
            }
            else ->{}
        }
    }

    fun statAniZoom(view: View?) {
        if (view != null) {
            view.clearAnimation()
            if (fade_in == null) {
                fade_in = ScaleAnimation(
                    0.8f,
                    1.1f,
                    0.8f,
                    1.1f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f
                )
                fade_in?.duration = 1000 // animation duration in milliseconds
                fade_in?.setFillAfter(true) // If fillAfter is true, the transformation that this animation performed will persist when it is finished.
                fade_in?.setRepeatMode(Animation.REVERSE)
                fade_in?.setRepeatCount(Animation.INFINITE)
            }
            view.startAnimation(fade_in)
        }
    }

    fun stopAniZoom(view: View?) {
        view?.clearAnimation()
    }
}
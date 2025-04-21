package com.tapbi.spark.controlcenter.feature.mishade.view

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.location.LocationManager
import android.media.AudioManager
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.data.model.ItemControlCenter
import com.tapbi.spark.controlcenter.data.model.ItemMiShade
import com.tapbi.spark.controlcenter.data.repository.ThemeHelper
import com.tapbi.spark.controlcenter.databinding.ViewItemCollapseShadeBinding
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CallBackUpdateUi
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CloseMiControlView
import com.tapbi.spark.controlcenter.feature.controlcenter.utils.DataMobileUtils
import com.tapbi.spark.controlcenter.feature.controlcenter.utils.HostPostUtils
import com.tapbi.spark.controlcenter.feature.controlcenter.view.control.micenter.view.BaseItemRecyclerView
import com.tapbi.spark.controlcenter.feature.controlios14.model.InfoSystem
import com.tapbi.spark.controlcenter.receiver.SyncStatusOb
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614
import com.tapbi.spark.controlcenter.utils.MethodUtils
import com.tapbi.spark.controlcenter.utils.SettingUtils

class ItemCollapseRecyclerviewShade : BaseItemRecyclerView, SyncStatusOb.Callback {
    var first: Boolean = true
    private var firstLoad: Boolean = true
    private var infoSystem: InfoSystem? = null

    private var itemMiShade: ItemMiShade? = null
    private var itemControlCenter: ItemControlCenter? = null

    private val callBackUpdateUi =
        CallBackUpdateUi { valueRegister, b, _ ->
            if (valueRegister == infoSystem!!.name) {
                if (infoSystem!!.name == Constant.STRING_ACTION_DATA_MOBILE) {
                    val enabled =
                        SettingUtils.hasSimCard(context) && !SettingUtils.isAirplaneModeOn(context) && DataMobileUtils(
                            context
                        ).isDataEnable
                    setBg(enabled)
                } else {
                    setBg(b)
                }
            }
        }
    private var pos = 0
    private var closeMiControlView: CloseMiControlView? = null
    private var action = ""

    var binding = ViewItemCollapseShadeBinding.inflate(LayoutInflater.from(context), this, true)

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }


    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    fun setStageAction(valueRegister: String, b: Boolean) {
        if (valueRegister == infoSystem!!.name) {
            if (infoSystem!!.name == Constant.STRING_ACTION_DATA_MOBILE) {
                val enabled =
                    SettingUtils.hasSimCard(context) && !SettingUtils.isAirplaneModeOn(getContext()) && DataMobileUtils(
                        context
                    ).isDataEnable
                setBg(enabled)
            } else {
                setBg(b)
            }
        }
    }

    private fun init() {
        setLayerType(LAYER_TYPE_NONE, null)
        when (ThemeHelper.itemControl.idCategory) {
            Constant.VALUE_CONTROL_CENTER -> {
                itemControlCenter = ThemeHelper.itemControl.controlCenter
            }

            else -> {
                itemMiShade = ThemeHelper.itemControl.miShade
            }
        }

        binding.imgIcon.setOnClickListener(this)
        setTextStyle(itemMiShade, itemControlCenter)
        binding.imgIcon.setOnLongClickListener { onLongClickFromChild() }
    }

    private fun setTextStyle(itemShade: ItemMiShade?, itemCenter: ItemControlCenter?) {
        val textColor = itemShade?.colorTextControl ?: itemCenter?.textColorDefaultControl ?: return
        binding.tvNameAction.setTextColor(Color.parseColor(textColor))
        binding.tvNameAction.typeface =
            Typeface.createFromAsset(
                context.assets,
                Constant.FOLDER_FONT_CONTROL_ASSETS + ThemeHelper.itemControl.font
            )
    }

    override fun onClick(v: View?) {
        when (action) {
            Constant.STRING_ACTION_DATA_MOBILE, Constant.STRING_ACTION_WIFI, Constant.STRING_ACTION_BLUETOOTH, Constant.STRING_ACTION_AIRPLANE_MODE, Constant.STRING_ACTION_LOCATION, Constant.STRING_ACTION_HOST_POST, Constant.STRING_ACTION_BATTERY, Constant.STRING_ACTION_NIGHT_LIGHT, Constant.DARK_MODE -> statAniZoom(
                binding.imgIcon
            )
        }
        super.onClick(v)
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        //enableListener(infoSystem.getUri(), nameAction.getText().toString(), callBackUpdateSound, callBackUpdateUi, closeMiControlView);
    }

    fun data(infoSystem: InfoSystem, pos: Int, closeMiControlView: CloseMiControlView?) {
        this.pos = pos
        this.infoSystem = infoSystem
        this.closeMiControlView = closeMiControlView
        if (firstLoad) {
            setBg(false)
            binding.imgIcon.setIcon(infoSystem.icon)
            itemMiShade?.let {
                if (it.iconControl != Constant.STRING_ICON_SHADE_1) {
                    binding.imgIcon.setBackground(it.iconControl)
                } else {
                    binding.imgIcon.setRatioRadius(it.cornerBackgroundControl)
                }

            }

            itemControlCenter?.let {
                if (it.iconControl != Constant.STRING_ICON_SHADE_1) {
                    binding.imgIcon.setBackground(it.iconControl)
                } else {
                    binding.imgIcon.setRatioRadius(it.cornerBackgroundControl)
                }

            }
            binding.tvNameAction.text =
                MethodUtils.getNameActionShowTextView(context, infoSystem.name)
            setUpData()
            updateStatusActionSync()
            firstLoad = false
        }
    }


    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (getVisibility() == VISIBLE) {
            updateStatusActionSync()
        }
    }

    private fun updateStatusActionSync() {
        if (action == Constant.STRING_ACTION_SYNC) {
            setBg(SettingUtils.isSyncAutomaticallyEnable())
        }
    }


    private fun setUpData() {
        action = MethodUtils.getAction(context, binding.tvNameAction.text.toString())
        enableListener(infoSystem!!.uri, action, pos, { valueRegister: String, value: Int ->
            if (valueRegister == Constant.STRING_ACTION_SOUND || valueRegister == Constant.STRING_ACTION_VIBRATE || valueRegister == Constant.STRING_ACTION_SILENT) {
                changeTypeSound(value)
            }
        }, callBackUpdateUi, closeMiControlView)
        when (action) {
            Constant.STRING_ACTION_AUTO_ROTATE -> setBg(
                Settings.System.getInt(
                    context.contentResolver,
                    Settings.System.ACCELEROMETER_ROTATION,
                    0
                ) == 1
            )

            Constant.STRING_ACTION_SOUND -> {

                audioManager?.let {
                    changeTypeSound(it.ringerMode)
                }
            }

            Constant.STRING_ACTION_AIRPLANE_MODE -> setBg(
                Settings.System.getInt(
                    context.contentResolver,
                    Settings.Global.AIRPLANE_MODE_ON,
                    0
                ) == 1
            )

            Constant.STRING_ACTION_DO_NOT_DISTURB -> try {
                val value = Settings.Global.getInt(context.contentResolver, "zen_mode") != 0
                setBg(value)
            } catch (e: SettingNotFoundException) {
                e.printStackTrace()
            }

            Constant.STRING_ACTION_LOCATION -> setBg(lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            Constant.STRING_ACTION_HOST_POST -> setBg(HostPostUtils(context).stateWifi)

            Constant.STRING_ACTION_KEYBOARD_PICKER, Constant.STRING_ACTION_SCREEN_CAST, Constant.STRING_ACTION_OPEN_SYSTEM, Constant.STRING_ACTION_SCREEN_SHOT, Constant.STRING_ACTION_SCREEN_LOCK, Constant.STRING_ACTION_CLOCK, Constant.STRING_ACTION_CAMERA -> setBg(
                false
            )

            Constant.STRING_ACTION_BATTERY -> setBg(SettingUtils.isPowerSaveMode(context))
            Constant.STRING_ACTION_DATA_MOBILE ->
                dataMobileUtils?.let {
                    callBackUpdateUi.stage(action, it.isDataEnable, pos)
                }

            Constant.STRING_ACTION_BLUETOOTH -> callBackUpdateUi.stage(
                action,
                mBluetoothAdapter != null && mBluetoothAdapter.isEnabled, pos
            )

            Constant.DARK_MODE -> {
                if (NotyControlCenterServicev614.getInstance() != null) {
                    setBg(NotyControlCenterServicev614.getInstance().isDarkModeOn)
                }
            }

            Constant.STRING_ACTION_WIFI -> {
                callBackUpdateUi.stage(action, SettingUtils.isEnableWifi(context), pos)
            }

            Constant.STRING_ACTION_FLASH_LIGHT -> {
                if (NotyControlCenterServicev614.getInstance() != null) {
                    setBg(NotyControlCenterServicev614.getInstance().isFlashOn)
                }
            }
        }
    }

    private fun changeTypeSound(value: Int) {
        setIconText(value)
        when (value) {
            AudioManager.RINGER_MODE_VIBRATE, AudioManager.RINGER_MODE_NORMAL -> setBg(true)
            AudioManager.RINGER_MODE_SILENT -> setBg(false)
        }
    }

    private fun setIconText(value: Int) {
        var resource = R.drawable.ic_mi_sounds
        var text = getContext().getString(R.string.text_sound)
        when (value) {
            AudioManager.RINGER_MODE_VIBRATE -> {
                resource = R.drawable.ic_mi_vibrate
                text = getContext().getString(R.string.text_vibrate)
            }

            AudioManager.RINGER_MODE_SILENT -> {
                resource = R.drawable.ic_mi_sound_silent
                text = getContext().getString(R.string.text_silent)
            }
        }
        binding.tvNameAction.text = text
        binding.imgIcon.setIcon(resource)
    }


    private fun setBg(b: Boolean) {
        stopAniZoom()
        if (itemMiShade?.backgroundColorSelectControl != null) {
            itemMiShade?.let {
                setIconBackground(
                    colorFilter = if (b) it.iconColorSelectControl else it.iconColorDefaultControl,
                    backgroundColor = if (b) it.backgroundColorSelectControl else it.backgroundColorDefaultControl
                )
            }
        }
        if (itemControlCenter?.backgroundColorDefaultControl != null) {
            itemControlCenter?.let {
                setIconBackground(
                    colorFilter = if (b) it.iconColorSelectControl else it.iconColorDefaultControl,
                    backgroundColor = if (b) it.backgroundColorSelectControl2 else it.backgroundColorDefaultControl,
                )
            }
        }


    }

    private fun setIconBackground(
        colorFilter: String?,
        backgroundColor: String?
    ) {
        binding.imgIcon.setIconColor(Color.parseColor(colorFilter))
        binding.imgIcon.setBackgroundC(Color.parseColor(backgroundColor))

    }

    override fun onSyncsStarted() {
    }

    override fun onSyncsFinished() {
    }
}

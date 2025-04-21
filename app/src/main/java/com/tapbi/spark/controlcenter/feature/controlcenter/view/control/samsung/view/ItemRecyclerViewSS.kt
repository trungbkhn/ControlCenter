package com.tapbi.spark.controlcenter.feature.controlcenter.view.control.samsung.view

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.location.LocationManager
import android.media.AudioManager
import android.provider.Settings
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.data.model.ItemControlSamSung
import com.tapbi.spark.controlcenter.data.repository.ThemeHelper
import com.tapbi.spark.controlcenter.databinding.ViewItemCollapseSsBinding
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CallBackUpdateUi
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CloseMiControlView
import com.tapbi.spark.controlcenter.feature.controlcenter.utils.DataMobileUtils
import com.tapbi.spark.controlcenter.feature.controlcenter.view.control.micenter.view.BaseItemRecyclerView
import com.tapbi.spark.controlcenter.feature.controlios14.model.InfoSystem
import com.tapbi.spark.controlcenter.utils.MethodUtils
import com.tapbi.spark.controlcenter.utils.SettingUtils
import com.tapbi.spark.controlcenter.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class ItemRecyclerViewSS : BaseItemRecyclerView {
    var first: Boolean = true
    var binding = ViewItemCollapseSsBinding.inflate(LayoutInflater.from(context), this, true)
    private var firstLoad = true
    private var infoSystem: InfoSystem? = null
    private var pos = 0
    private var closeMiControlView: CloseMiControlView? = null
    private var action = ""
    private var itemSamSung: ItemControlSamSung? = ThemeHelper.itemControl.samsung

    private val callBackUpdateUi = CallBackUpdateUi { valueRegister, b, _ ->
        if (valueRegister == infoSystem?.name) {
            val isEnabled = when (infoSystem?.name) {
                Constant.STRING_ACTION_DATA_MOBILE -> isDataMobileEnabled()
                else -> b
            }
            setBg(isEnabled)
        }
    }

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

    private fun init() {
        setLayerType(LAYER_TYPE_NONE, null)
        setupDefaultUi()
        binding.mcvIcon.setOnClickListener(this)
        binding.mcvIcon.setOnLongClickListener { onLongClickFromChild() }
    }

    private fun setupDefaultUi() {
        itemSamSung?.let {
            with(binding) {
                tvNameAction.setTextColor(Color.parseColor(it.colorIconControl))
                var typeface: Typeface? = null
                try {
                    typeface = Typeface.createFromAsset(
                        context.assets,
                        Constant.FOLDER_FONT_CONTROL_ASSETS + ThemeHelper.itemControl.font
                    )
                } catch (e: Exception) {

                }
                if (typeface != null) {
                    tvNameAction.typeface = typeface
                }
                imgIcon.setColorFilter(Color.parseColor(it.colorIconControl))
                mcvIcon.apply {
                    setBackgroundColorView(Color.parseColor(it.backgroundColorDefaultControl))
                    setRadius(it.cornerControl)
                    setStrokeColor(Color.parseColor(it.colorStrokeBackgroundControl))
                }
            }
        }
    }

    fun data(infoSystem: InfoSystem, pos: Int, closeMiControlView: CloseMiControlView?) {
        this.infoSystem = infoSystem
        this.pos = pos
        this.closeMiControlView = closeMiControlView

        if (firstLoad) {
            setBg(false)
            setIconSystem(infoSystem.name)
            binding.tvNameAction.text =
                MethodUtils.getNameActionShowTextView(context, infoSystem.name)
            setUpData()
            updateStatusActionSync()
            firstLoad = false
        }
    }

    private fun setIconSystem(nameAction: String) {
        val assetPath =
            "themes/${ThemeHelper.itemControl.idCategory}/${ThemeHelper.itemControl.id}/iconsystemcontrol/${
                Utils.getStringNameIconAssets(nameAction)
            }"
        App.myScope.launch(Dispatchers.IO) {
            try {
                context.assets.open(assetPath).use { inputStream ->
                    val drawable = Drawable.createFromStream(inputStream, null)
                    withContext(Dispatchers.Main) {
                        binding.imgIcon.setImageDrawable(drawable)
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) { setIconDefault() }
            }
        }
    }

    private fun setIconDefault() {
        infoSystem?.icon?.let { binding.imgIcon.setImageResource(it) }
    }


    private fun setUpData() {
        action = MethodUtils.getAction(context, binding.tvNameAction.text.toString())
        enableListener(
            infoSystem!!.uri,
            action,
            pos,
            ::onSoundChange,
            callBackUpdateUi,
            closeMiControlView
        )

        when (action) {
            Constant.STRING_ACTION_AUTO_ROTATE -> setBg(
                Settings.System.getInt(
                    context.contentResolver,
                    Settings.System.ACCELEROMETER_ROTATION,
                    0
                ) == 1
            )

            Constant.STRING_ACTION_SOUND -> audioManager?.ringerMode?.let { changeTypeSound(it) }
            Constant.STRING_ACTION_AIRPLANE_MODE -> setBg(
                Settings.System.getInt(
                    context.contentResolver,
                    Settings.Global.AIRPLANE_MODE_ON,
                    0
                ) == 1
            )

            Constant.STRING_ACTION_DO_NOT_DISTURB -> setBg(getZenMode() != 0)
            Constant.STRING_ACTION_LOCATION -> setBg(lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            Constant.STRING_ACTION_BATTERY -> setBg(SettingUtils.isPowerSaveMode(context))
            Constant.STRING_ACTION_DATA_MOBILE -> dataMobileUtils?.let {
                callBackUpdateUi.stage(
                    action,
                    it.isDataEnable,
                    pos
                )
            }

            Constant.STRING_ACTION_WIFI -> callBackUpdateUi.stage(
                action,
                SettingUtils.isEnableWifi(context),
                pos
            )

            Constant.STRING_ACTION_BLUETOOTH -> callBackUpdateUi.stage(
                action,
                mBluetoothAdapter?.isEnabled == true,
                pos
            )

            Constant.DARK_MODE -> setBg(isDarkModeEnabled())
        }
    }

    private fun isDataMobileEnabled() =
        SettingUtils.hasSimCard(context) && !SettingUtils.isAirplaneModeOn(context) && DataMobileUtils(
            context
        ).isDataEnable

    private fun getZenMode() = try {
        Settings.Global.getInt(context.contentResolver, "zen_mode")
    } catch (e: Settings.SettingNotFoundException) {
        0
    }

    private fun isDarkModeEnabled(): Boolean {
        val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        return when (uiModeManager.nightMode) {
            UiModeManager.MODE_NIGHT_YES -> true
            UiModeManager.MODE_NIGHT_NO -> false
            else -> (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        }
    }

    private fun changeTypeSound(value: Int) {
        setIconText(value)
        setBg(value != AudioManager.RINGER_MODE_SILENT)
    }

    private fun setIconText(value: Int) {
        val (nameAction, text) = when (value) {
            AudioManager.RINGER_MODE_VIBRATE -> Constant.STRING_ACTION_VIBRATE to context.getString(
                R.string.text_vibrate
            )

            AudioManager.RINGER_MODE_SILENT -> Constant.STRING_ACTION_SILENT to context.getString(R.string.text_silent)
            else -> Constant.STRING_ACTION_SOUND to context.getString(R.string.text_sound)
        }
        binding.tvNameAction.text = text
        setIconSystem(nameAction)
    }

    private fun setBg(enabled: Boolean) {
        itemSamSung?.let {
            stopAniZoom()
            val color =
                if (enabled) it.backgroundColorSelectControl else it.backgroundColorDefaultControl
            binding.mcvIcon.setBackgroundColorView(Color.parseColor(color))
        }
    }

    override fun onClick(v: View?) {
        when (action) {
            Constant.STRING_ACTION_DATA_MOBILE, Constant.STRING_ACTION_WIFI, Constant.STRING_ACTION_BLUETOOTH, Constant.STRING_ACTION_AIRPLANE_MODE, Constant.STRING_ACTION_LOCATION, Constant.STRING_ACTION_HOST_POST, Constant.STRING_ACTION_BATTERY, Constant.STRING_ACTION_NIGHT_LIGHT, Constant.DARK_MODE -> statAniZoom(
                binding.imgIcon
            )
        }
        super.onClick(v)
    }

    private fun onSoundChange(valueRegister: String, value: Int) {
        if (valueRegister in listOf(
                Constant.STRING_ACTION_SOUND,
                Constant.STRING_ACTION_VIBRATE,
                Constant.STRING_ACTION_SILENT
            )
        ) {
            changeTypeSound(value)
        }
    }

    private fun updateStatusActionSync() {
        if (action == Constant.STRING_ACTION_SYNC) {
            setBg(SettingUtils.isSyncAutomaticallyEnable())
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (getVisibility() == VISIBLE) {
            updateStatusActionSync()
        }
    }

    fun setStageAction(valueRegister: String, b: Boolean) {
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


}

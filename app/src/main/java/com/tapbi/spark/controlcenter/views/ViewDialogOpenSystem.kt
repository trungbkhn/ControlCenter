package com.tapbi.spark.controlcenter.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614
import com.tapbi.spark.controlcenter.utils.MethodUtils
import com.tapbi.spark.controlcenter.utils.SettingUtils
import com.tapbi.spark.controlcenter.utils.StringAction
import com.tapbi.spark.controlcenter.utils.TinyDB

class ViewDialogOpenSystem : LinearLayout {
    private var context: Context? = null
    private var rootView: LinearLayout? = null
    private var viewContent: ConstraintLayout? = null
    private var tvTypeAction: TextView? = null
    private var tvDes: TextView? = null
    private var tvOpenSystem: TextView? = null
    private var imgIcTypeAction: ImageView? = null
    private var des1 = ""
    private var des2 = ""
    private var tinyDB: TinyDB? = null
    private var cbAutoOpenSystem: CheckBox? = null
    private var action: String? = ""

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        this.context = context
        tinyDB = App.tinyDB
        LayoutInflater.from(context).inflate(R.layout.layout_dialog_does_find_action, this, true)
        rootView = findViewById(R.id.rootDialog)
        viewContent = findViewById(R.id.viewContent)
        tvTypeAction = findViewById(R.id.tvTypeAction)
        tvDes = findViewById(R.id.tvDes)
        tvOpenSystem = findViewById(R.id.tvOpenSystem)
        imgIcTypeAction = findViewById(R.id.imgIcTypeAction)
        cbAutoOpenSystem = findViewById(R.id.cbAuto)
        des1 = context.getString(R.string.dialog_setup_noty_des_1)
        des2 = context.getString(R.string.dialog_setup_noty_des_2)
        evenClick()
    }

    //    @Override
    //    protected void onConfigurationChanged(Configuration newConfig) {
    //        super.onConfigurationChanged(newConfig);
    //        setVisible(false);
    //    }
    private fun evenClick() {
        rootView!!.setOnClickListener { _: View? -> setVisible(false) }
        viewContent!!.setOnClickListener { _: View? -> }
        tvOpenSystem!!.setOnClickListener { _: View? ->
            setVisible(false)
            openNotySystem()
        }
        cbAutoOpenSystem!!.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            tinyDB!!.putBoolean(
                Constant.AUTO_OPEN_NOTY_SYSTEM, isChecked
            )
        }
    }

    @SuppressLint("SetTextI18n")
    fun setContent(action: String?) {
        this.action = action
        val nameActionShow = MethodUtils.getNameActionShowTextView(context, action)
        tvTypeAction!!.text = nameActionShow
        imgIcTypeAction!!.setImageResource(StringAction().getIconAction(action))
        tvDes!!.text = "$des1 $nameActionShow $des2"
    }

    fun setVisible(show: Boolean) {
        if (show && tinyDB!!.getBoolean(Constant.AUTO_OPEN_NOTY_SYSTEM, false)) {
            visibility = GONE
            openNotySystem()
        } else {
            visibility = if (show) VISIBLE else GONE
        }
    }

    private fun openNotySystem() {
        val notyService = NotyControlCenterServicev614.getInstance()

        if (action == null || notyService == null) {
            return
        }

        notyService.dismissNotyShade()
        notyService.closeNotyCenter()

        val context = getContext() ?: return

        when (action) {
            Constant.STRING_ACTION_AIRPLANE_MODE -> SettingUtils.intentChangeAirPlane(context)
            Constant.DARK_MODE -> SettingUtils.intentChangeDisplay(context)
            Constant.STRING_ACTION_BATTERY -> SettingUtils.intentChangeBatterySaver(context)
            Constant.STRING_ACTION_DATA_MOBILE -> SettingUtils.intentChangeDataMobile(context)
            Constant.STRING_ACTION_LOCATION -> SettingUtils.intentChangeLocation(context)
            Constant.STRING_ACTION_HOST_POST -> SettingUtils.intentChangeHostPost(context)
        }
    }

}
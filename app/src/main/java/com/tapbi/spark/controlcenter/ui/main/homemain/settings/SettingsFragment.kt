package com.tapbi.spark.controlcenter.ui.main.homemain.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import com.orhanobut.hawk.Hawk
import com.tapbi.spark.controlcenter.App.Companion.tinyDB
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.databinding.FragmentSettingsBinding
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614
import com.tapbi.spark.controlcenter.ui.base.BaseBindingFragment
import com.tapbi.spark.controlcenter.ui.choosemusic.ChooseMusicPlayerActivity
import com.tapbi.spark.controlcenter.ui.language.LanguageActivity
import com.tapbi.spark.controlcenter.utils.Analytics
import com.tapbi.spark.controlcenter.utils.LocaleUtils
import com.tapbi.spark.controlcenter.utils.MethodUtils
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper
import com.tapbi.spark.controlcenter.utils.setState
import company.librate.RateDialog
import timber.log.Timber

class SettingsFragment : BaseBindingFragment<FragmentSettingsBinding, SettingsViewModel>() {
    private var enableNoty = false
    private var enableControl = false
    private var rateDialog: RateDialog? = null
    private var typeOpen : TYPE_OPEN = TYPE_OPEN.EDGE_TRIGGERS
    override fun getViewModel(): Class<SettingsViewModel> {
        return SettingsViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_settings

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        showViewRate()
        initListener()
    }

    private fun initListener() {
        binding.tvCurrentLanguage.text = LocaleUtils.getCurrentLanguageName(context)
        binding.tvMenuEdge.setOnClickListener {
            ViewHelper.preventTwoClick(it)
            typeOpen = TYPE_OPEN.EDGE_TRIGGERS
            showAdsFull(requireContext().getString(R.string.tag_inter_edge_triggers))
        }
//        binding.tvMenuFocus.setOnClickListener {
//            ViewHelper.preventTwoClick(it)
//            navigate(R.id.homeMainFragment, R.id.focusFragment)
//        }
        binding.tvMenuFeedback.setOnClickListener {
            ViewHelper.preventTwoClick(it)
            MethodUtils().sendCommentEmail(context)
            Analytics.getInstance().logEvent("feed_back", null)

        }
        binding.tvMenuRate.setOnClickListener {
            ViewHelper.preventTwoClick(it)
            showRate()

        }
        binding.tvMenuShareApp.setOnClickListener {
            ViewHelper.preventTwoClick(it)
            MethodUtils().shareApp(context)
            Analytics.getInstance().logEvent("share_app", null)
        }
        binding.tvMenuPrivacyPolicy.setOnClickListener {
            ViewHelper.preventTwoClick(it)
            MethodUtils().openWebApp(context)
            Analytics.getInstance().logEvent("policy", null)
        }
//        binding.tvMenuChoseMusic.setOnClickListener {
//            ViewHelper.preventTwoClick(it)
//            val intent = Intent(context, ChooseMusicPlayerActivity::class.java)
//            startActivity(intent)
//        }
        binding.cbEnableNotification.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            setChangeEnableNoty(
                isChecked,
            )
//            setStateRunning(
//                tinyDB.getInt(
//                    Constant.IS_ENABLE,
//                    Constant.DEFAULT_IS_ENABLE,
//                ) == Constant.DEFAULT_IS_ENABLE
//            )
        }
        binding.cbEnableControls.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            setChangeEnableControl(
                isChecked,
            )
        }
        binding.tvLanguage.setOnClickListener { v ->
            ViewHelper.preventTwoClick(v)
            typeOpen = TYPE_OPEN.LANGUAGE
            showAdsFull(requireContext().getString(R.string.tag_inter_language))
        }

    }

    override fun nextAfterFullScreen() {
        super.nextAfterFullScreen()
        if (typeOpen == TYPE_OPEN.EDGE_TRIGGERS){
            navigate(R.id.homeMainFragment, R.id.settingTouchFragment)
        }else if (typeOpen == TYPE_OPEN.LANGUAGE){
            activity?.apply {
                val intent =
                    Intent(this, LanguageActivity::class.java)
                startActivity(intent)
                activity?.overridePendingTransition(
                    R.anim.from_right,
                    R.anim.to_left
                )
            }
        }
    }

    private fun setChangeEnableNoty(isEnable: Boolean) {
        tinyDB.putBoolean(Constant.ENABLE_NOTY, isEnable)
        enableNoty = isEnable
        if (NotyControlCenterServicev614.getInstance() != null) {
            NotyControlCenterServicev614.getInstance().updateEnabledTouchEdge()
        }
    }
    private fun setChangeEnableControl(isEnable: Boolean) {
        tinyDB.putBoolean(Constant.ENABLE_CONTROL, isEnable)
        enableControl = isEnable
        if (NotyControlCenterServicev614.getInstance() != null) {
            NotyControlCenterServicev614.getInstance().updateEnabledTouchEdge()
        }
    }

    private fun showRate() {
        if (rateDialog == null) {
            rateDialog = RateDialog(
                activity,
                getString(R.string.app_name),
                object : RateDialog.IResultClickDialog {
                    override fun onCancel() {

                    }

                    override fun later() {

                    }

                    override fun rateNow() {
                        Hawk.put(Constant.KEY_IS_RATE, true)
                        showViewRate()
                    }
                },
            )
        }
        rateDialog?.show()

    }

    override fun onResume() {
        super.onResume()
        reloadCheckBox()
    }
    private fun reloadCheckBox(){
//        binding.cbEnableNotification.isChecked = tinyDB.getBoolean(Constant.ENABLE_NOTY, false)
//        binding.cbEnableControls.isChecked = tinyDB.getBoolean(Constant.ENABLE_CONTROL, false)
//        val typeNoti  = tinyDB.getInt(Constant.TYPE_NOTY, Constant.VALUE_CONTROL_CENTER)
//        val isEnable = (typeNoti == Constant.VALUE_MI_NOTI || typeNoti == Constant.VALUE_CONTROL_CENTER)
//        Timber.e("NVQ reloadCheckBox ++++++++++++$isEnable")
//        binding.groupCheckEnable.visibility = if (isEnable) View.VISIBLE else View.INVISIBLE
    }

    private fun showViewRate() {
        val show = Hawk.get(Constant.KEY_IS_RATE, false)
        if (show) {
            binding.tvMenuRate.visibility = View.GONE
            binding.vLineMenuShareApp.visibility = View.GONE
        }
    }

    override fun onPermissionGranted() {

    }

    enum class TYPE_OPEN {
        EDGE_TRIGGERS, LANGUAGE
    }
}
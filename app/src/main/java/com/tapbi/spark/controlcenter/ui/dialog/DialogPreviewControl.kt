package com.tapbi.spark.controlcenter.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.applovin.impl.bi
import com.bumptech.glide.Glide
import com.google.android.ads.nativetemplates.TemplateViewMultiAds
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ironman.trueads.common.Common
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.data.model.ThemeControl
import com.tapbi.spark.controlcenter.databinding.DialogPreviewControl2Binding
import com.tapbi.spark.controlcenter.databinding.DialogPreviewControlBinding
import com.tapbi.spark.controlcenter.ui.base.BaseBindingDialogFragment
import com.tapbi.spark.controlcenter.ui.base.BaseDialogFragment
import com.tapbi.spark.controlcenter.ui.main.MainViewModel
import com.tapbi.spark.controlcenter.utils.RemoteConfigHelper
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper

open class DialogPreviewControl(
    private val iLoadAdsNative: ILoadAdsNative,
    private val clickListener: ClickListener,
) :  BaseBindingDialogFragment<DialogPreviewControlBinding>() {
    private var themeControl: ThemeControl? = null

    private var isMyCustomizationControl = false
    private var optionClick: OPTION_CLICK = OPTION_CLICK.APPLY

    override val layoutId: Int
        get() = R.layout.dialog_preview_control

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        arguments?.let {
            themeControl = Gson().fromJson(
                it.getString(KEY_THEME_CONTROL),
                object : TypeToken<ThemeControl>() {}.type
            )
            isMyCustomizationControl = it.getBoolean(KEY_IS_MY_CUSTOMIZATION_CONTROL)
        }
        if (isMyCustomizationControl) {
            binding.tvDelete.visibility = View.VISIBLE
            binding.imgDone.visibility = View.VISIBLE
            binding.tvApply.visibility = View.INVISIBLE
        } else {
           binding. tvDelete.visibility = View.INVISIBLE
          binding.tvApply.visibility = View.VISIBLE
           binding. imgDone.visibility = View.INVISIBLE
        }

        themeControl?.let {
            if (isAdded) {
                val pathStart =
                    if (isMyCustomizationControl) requireContext().filesDir else "file:///android_asset"
                Glide.with(binding.imPreview)
                    .load("$pathStart/${Constant.FOLDER_THEMES_ASSETS}/${it.idCategory}/${it.id}/${it.preview}")
                    .placeholder(R.drawable.ic_loading_theme).into(binding.imPreview)
                if (App.tinyDB.getLong(
                        Constant.KEY_ID_CURRENT_APPLY_THEME,
                        Constant.KEY_ID_CURRENT_APPLY_THEME_DEFAULT
                    ) == it.id
                ) {
                    binding.tvApply.text = getString(R.string.using)
                } else {
                    binding.tvApply.text = getString(R.string.text_apply)
                }
            }

        }



        initListener()
        iLoadAdsNative.onLoadAdsNative(binding.cvAdsWrap, binding.flAds)
    }

   


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.navigationBarColor = ContextCompat.getColor(App.ins, R.color.transparent)
        dialog.setOnKeyListener { dialogInterface, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                if (isAdded && !isStateSaved) {
                    dismissAllowingStateLoss()
                }
                true
            } else {
                false
            }
        }
        return dialog

    }




    private fun applyTheme(view: View) {
        ViewHelper.preventTwoClick(view, 500)
        if (isMyCustomizationControl) {
            optionClick = OPTION_CLICK.APPLY
            showAdsFull(requireContext().getString(R.string.tag_inter_preview_theme))
        } else {
            pressApply()
        }
    }

    private fun initListener() {
        binding.tvApply.setOnClickListener { view ->
            applyTheme(view)
        }

        binding.imgDone.setOnClickListener { view ->
            applyTheme(view)
        }

        binding.layoutDismiss.setOnClickListener {
            ViewHelper.preventTwoClick(it, 500)
            dismiss()
        }
        binding.imgEdit.setOnClickListener {
            ViewHelper.preventTwoClick(it, 500)
            clickListener.onClickEdit(themeControl, isMyCustomizationControl)
            dismiss()
        }
        binding. tvDelete.setOnClickListener { v ->
            ViewHelper.preventTwoClick(v, 500)
            val dialogDelete = DialogDelete.newInstance(Constant.KEY_IS_DELETE_THEME_CONTROL,
                object : DialogDelete.ClickListener {
                    override fun onClickDelete() {
                        if (isMyCustomizationControl) {
                            optionClick = OPTION_CLICK.DELETE
                            showAdsFull(requireContext().getString(R.string.tag_inter_preview_theme))
                        } else {
                            pressDelete()
                        }
                    }

                })
            if (isAdded && !childFragmentManager.isStateSaved) {
                dialogDelete.show(
                    childFragmentManager.beginTransaction().remove(dialogDelete),
                    dialogDelete.tag
                )
            }
        }

        if (Common.checkAdsIsDisable(
                requireContext().getString(R.string.tag_native_preview_my_theme),
                Common.TYPE_ADS_NATIVE
            )
        ) {
            binding.cvAdsWrap.visibility = View.GONE
        } else {
            binding. cvAdsWrap.visibility = View.VISIBLE
        }
    }

    override fun nextAfterFullScreen() {
        super.nextAfterFullScreen()
        if (optionClick == OPTION_CLICK.APPLY) {
            pressApply()
        } else if (optionClick == OPTION_CLICK.DELETE) {
            pressDelete()
        }
    }

    fun pressDelete() {
        themeControl?.let {
            if (it.id == App.tinyDB.getLong(
                    Constant.KEY_ID_CURRENT_APPLY_THEME,
                    Constant.KEY_ID_CURRENT_APPLY_THEME_DEFAULT
                )
            ) {
                clickListener.showToast(getString(R.string.this_control_interface_is_in_use_and_cannot_be_deleted))
            } else {
                mMainViewModel.deleteThemeControl(requireContext(), it)
            }

        }
        if (isAdded && !isStateSaved) {
            dismissAllowingStateLoss()
        }
    }

    fun pressApply() {
        themeControl?.let {
            if (it.id == App.tinyDB.getLong(
                    Constant.KEY_ID_CURRENT_APPLY_THEME,
                    Constant.KEY_ID_CURRENT_APPLY_THEME_DEFAULT
                )
            ) {
                clickListener.showToast(getString(R.string.this_control_interface_is_in_use))
            } else {
                clickListener.onClickApply(it, isMyCustomizationControl)
            }
        }
        if (isAdded && !isStateSaved) {
            dismissAllowingStateLoss()
        }
    }


    interface ClickListener {
        fun onClickApply(theme: ThemeControl, isMyCustomizationControl: Boolean)

        fun onClickEdit(theme: ThemeControl?, isMyCustomizationControl: Boolean)

        fun showToast(message: String)
    }

    fun interface ILoadAdsNative {
        fun onLoadAdsNative(frameLayout: FrameLayout, templateView: TemplateViewMultiAds)

    }

    companion object {

        private const val KEY_THEME_CONTROL = "KEY_THEME_CONTROL"
        private const val KEY_IS_MY_CUSTOMIZATION_CONTROL = "KEY_IS_MY_CUSTOMIZATION_CONTROL"

        fun newInstance(
            theme: ThemeControl,
            clickListener: ClickListener,
            iLoadAdsNative: ILoadAdsNative,
            isMyCustomizationControl: Boolean
        ): DialogPreviewControl {
            val dialog = DialogPreviewControl(iLoadAdsNative, clickListener)
            val bundle = Bundle()
            bundle.putString(KEY_THEME_CONTROL, Gson().toJson(theme))
            bundle.putBoolean(KEY_IS_MY_CUSTOMIZATION_CONTROL, isMyCustomizationControl)
            dialog.arguments = bundle
            return dialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ThemeFullScreen)
    }

    enum class OPTION_CLICK {
        APPLY, DELETE
    }

}


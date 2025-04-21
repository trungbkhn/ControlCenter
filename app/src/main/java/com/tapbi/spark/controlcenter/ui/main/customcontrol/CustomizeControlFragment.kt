package com.tapbi.spark.controlcenter.ui.main.customcontrol

import android.graphics.Color
import android.os.Bundle
import android.view.InflateException
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.ironman.trueads.common.Common
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.adapter.CustomizeControlViewPage
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.common.Constant.EVENT_CHANGE_BACKGROUND
import com.tapbi.spark.controlcenter.common.Constant.EVENT_CHANGE_COLOR
import com.tapbi.spark.controlcenter.common.Constant.EVENT_CHANGE_CONNER
import com.tapbi.spark.controlcenter.common.Constant.EVENT_CHANGE_FONT
import com.tapbi.spark.controlcenter.common.Constant.EVENT_CHANGE_GALLERY
import com.tapbi.spark.controlcenter.common.Constant.EVENT_CHANGE_GROUP_COLOR
import com.tapbi.spark.controlcenter.common.Constant.EVENT_CHANGE_ICON_SHADE
import com.tapbi.spark.controlcenter.common.Constant.EVENT_CHANGE_STATE_SEEK_BAR
import com.tapbi.spark.controlcenter.common.Constant.EVENT_SET_SELECT
import com.tapbi.spark.controlcenter.common.Constant.KEY_ID_CATEGORY
import com.tapbi.spark.controlcenter.common.Constant.VALUE_CONTROL_CENTER
import com.tapbi.spark.controlcenter.common.Constant.VALUE_CONTROL_CENTER_OS
import com.tapbi.spark.controlcenter.common.Constant.VALUE_SHADE
import com.tapbi.spark.controlcenter.data.model.GroupColor
import com.tapbi.spark.controlcenter.data.model.ItemControl
import com.tapbi.spark.controlcenter.data.model.ThemeControl
import com.tapbi.spark.controlcenter.databinding.FragmentCustomizeControlBinding
import com.tapbi.spark.controlcenter.databinding.LayoutCustomTypeControlOsBinding
import com.tapbi.spark.controlcenter.databinding.LayoutCustomsTypeMiControlsBinding
import com.tapbi.spark.controlcenter.databinding.LayoutCustomsTypeMiShadeBinding
import com.tapbi.spark.controlcenter.eventbus.EventCustomControls
import com.tapbi.spark.controlcenter.eventbus.EventTickStoreWallpaper
import com.tapbi.spark.controlcenter.ui.base.BaseBindingFragment
import com.tapbi.spark.controlcenter.ui.main.MainActivity
import com.tapbi.spark.controlcenter.ui.main.customcontrol.preview.PreviewControlCenter
import com.tapbi.spark.controlcenter.ui.main.customcontrol.preview.PreviewControlMiShade
import com.tapbi.spark.controlcenter.ui.main.customcontrol.preview.PreviewControlOS
import com.tapbi.spark.controlcenter.utils.MethodUtils
import com.tapbi.spark.controlcenter.utils.MethodUtils.getStatusBarHeight
import com.tapbi.spark.controlcenter.utils.Utils.readItemControlFromJson2
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper
import com.tapbi.spark.controlcenter.utils.hide
import com.tapbi.spark.controlcenter.utils.safeDelay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber

class CustomizeControlFragment :
    BaseBindingFragment<FragmentCustomizeControlBinding, CustomControlViewModel>() {
    var isEdit = false
    private var idEdit = 0L
    private var idCategoryEdit = 0
    var idBackgroundStore = -1
    private var typeMiShadeBinding: LayoutCustomsTypeMiShadeBinding? = null
    private var typeMiControlBinding: LayoutCustomsTypeMiControlsBinding? = null
    private var typeControlOSBinding: LayoutCustomTypeControlOsBinding? = null
    private var customizeCusViewPager: CustomizeControlViewPage? = null
    var theme = ItemControl()
    var type = VALUE_CONTROL_CENTER
    override fun getViewModel(): Class<CustomControlViewModel> {
        return CustomControlViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_customize_control

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        arguments?.let {
            type = it.getInt(KEY_ID_CATEGORY)
            isEdit = it.getBoolean(Constant.KEY_EDIT_THEME,false)

            idEdit = it.getLong(Constant.ID_THEME_CONTROL)
            idCategoryEdit = it.getInt(Constant.ID_CATEGORY)
            if (idCategoryEdit >= 1000) type = idCategoryEdit
        }
        if (isEdit) {
            viewModel.getThemeEdit(requireContext(), ThemeControl(idEdit, idCategoryEdit,"thumb.webp"))
        } else {
            viewModel.getControlThemeDefaultById(requireContext(), type)
        }
        binding.clMain.setPadding(0, getStatusBarHeight(requireContext()), 0, 0)
        initViewHeader()
        initListener()
        onObserve()
        safeDelay(100) {
            initTabLayout()
            initViewPager()
        }

        loadAdsBanner(
            binding.bannerContainer,
            Common.getMapIdAdmobApplovin(requireContext(),R.string.admob_banner_id_customize_theme_collapsible,R.string.applovin_banner_id_customize_theme),
            Common.getMapIdAdmobApplovin(
                requireActivity(),
                R.array.admob_native_id_customize_theme,
                R.array.applovin_id_native_customize_theme
            ),
            Common.enableAdsBannerCollapsible()
        )
    }
    private fun loadThemeEdit(){
        loadBackgroundEdit()
        changeFontPreview(getCurrentParent(), theme.font)
        var conner = 0f
        when(idCategoryEdit){
            VALUE_CONTROL_CENTER_OS ->  {
                changeIconShape(getCurrentParent(), theme.controlCenterOS?.listControlCenterStyleVerticalTop?.get(1)?.controlSettingIosModel?.backgroundImageViewItem)
                conner = theme.controlCenterOS?.listControlCenterStyleVerticalTop?.get(1)?.controlSettingIosModel?.cornerBackgroundViewItem ?: 0f
            }
            VALUE_CONTROL_CENTER -> {
                changeIconShape(getCurrentParent(), theme.controlCenter?.iconControl)
                conner = theme.controlCenter?.cornerBackgroundControl ?: 0f
            }
            VALUE_SHADE -> {
                changeIconShape(getCurrentParent(), theme.miShade?.iconControl)
                conner = theme.miShade?.cornerBackgroundControl ?: 0f
            }
        }
        changeConner(getCurrentParent(), (conner*100).toInt())
        binding.vpCustomizeControl.post {
            mainViewModel.eventSetSelect.postValue(true)
        }
    }

    private fun onObserve() {
        mainViewModel.chooseBackGroundInStoreLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                idBackgroundStore = it
                changeBackGround(Constant.STORE_WALLPAPER)
                if (it != -1) (EventBus.getDefault().post(EventTickStoreWallpaper(true)))
                mainViewModel.chooseBackGroundInStoreLiveData.postValue(null)
            }
        }
        viewModel.itemControlTheme.observe(viewLifecycleOwner) {
            if (it != null) {
                theme = it
                initPreview()
                viewModel.itemControlTheme.postValue(null)
            }

        }
        mainViewModel.insertThemeControlDone.observe(viewLifecycleOwner) {
            if (it) {
                mainViewModel.insertThemeControlDone.postValue(false)
                showAdsFull(requireContext().getString(R.string.tag_inter_customize_theme))
            }
        }
    }

    private fun initPreview() {
        when (type) {
            VALUE_SHADE -> {
                initStubMiShade()
            }

            VALUE_CONTROL_CENTER -> {
                initStubMiControl()
            }

            else -> {
                initStubControlOS()
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    private fun initViewHeader() {
        binding.viewHeader.tvTitle.text = getString(R.string.customize)
        binding.viewHeader.tvDone.visibility = View.VISIBLE
        binding.viewHeader.root.setBackgroundColor(Color.parseColor("#EFEFF4"))
        binding.clMain.setBackgroundColor(Color.parseColor("#EFEFF4"))
    }

    private fun initTabLayout() {
        val tabs = listOfNotNull(
            Pair(getString(R.string.controls), R.drawable.ic_tab_control),
            if (type == VALUE_CONTROL_CENTER_OS) Pair(
                getString(R.string.gallery),
                R.drawable.ic_tab_gallery
            ) else null,
            Pair(getString(R.string.fonts), R.drawable.ic_tab_font),
            Pair(getString(R.string.background), R.drawable.ic_tab_background)
        )

        tabs.forEach { (title, icon) ->
            binding.tabLayout.addTab(
                binding.tabLayout.newTab().setText(title).setIcon(icon)
            )
        }
        binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    binding.vpCustomizeControl.setCurrentItem(it.position, false)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        val tabStrip = binding.tabLayout.getChildAt(0) as LinearLayout
        for (i in 0 until tabStrip.childCount) {
            tabStrip.getChildAt(i).setOnLongClickListener { true }
        }
    }

    private fun initListener() {
        binding.viewHeader.imBack.setOnClickListener {
            ViewHelper.preventTwoClick(it, 800)
            findNavController().popBackStack()
        }
        binding.viewHeader.tvDone.setOnClickListener {
            ViewHelper.preventTwoClick(it, 800)
            if (isAdded) {
                (activity as MainActivity).binding.loading.visibility = View.VISIBLE
                val layout = getCurrentParent()
                layout?.let { preview ->
                    mainViewModel.insertThemeControl(
                        requireContext(),
                        theme, preview,isEdit
                    )
                }
            }
        }

    }

    @Subscribe
    fun onEventCustomControls(event: EventCustomControls) {
        when (event.event) {
            EVENT_CHANGE_CONNER -> {
                val conner = event.data as Int
                changeConner(getCurrentParent(), conner)
            }

            EVENT_CHANGE_COLOR -> {
                val color = event.data as Int
                changeBackgroundColor(color)
            }

            EVENT_CHANGE_GROUP_COLOR -> {
                val groupColor = event.data as GroupColor
                typeMiControlBinding?.previewControl?.setColorSelectControl(groupColor)
            }

            EVENT_CHANGE_ICON_SHADE -> {
                val iconName = event.data as String
                changeIconShape(getCurrentParent(), iconName)
            }

            EVENT_CHANGE_FONT -> {
                val fontName = event.data as String
                changeFontPreview(getCurrentParent(), fontName)
            }

            EVENT_CHANGE_BACKGROUND -> {
                val type = event.data as String
                if (type != Constant.STORE_WALLPAPER) {
                    theme.typeBackground = type
                    changeBackGround(type)
                }

            }

            EVENT_CHANGE_GALLERY -> {
                val list = event.data as ArrayList<*>
                Timber.e("NVQ EVENT_CHANGE_GALLERY+++++ ${theme?.controlCenterOS?.listControlCenterStyleVerticalTop?.size} // ${list.size}")
                typeControlOSBinding?.previewControl?.setListControlSettings(list)
                Timber.e("NVQ EVENT_CHANGE_GALLERY ${theme?.controlCenterOS?.listControlCenterStyleVerticalTop?.size} // ${list.size}")
            }

            else -> {}
        }
    }
    private fun loadBackgroundEdit(){
        Timber.e("NVQ loadBackgroundEdit ${theme.typeBackground} ")
        typeControlOSBinding?.previewControl?.binding?.imBg?.let {
            setBackgroundEdit(
                theme,
                it
            )
        }
        typeMiControlBinding?.previewControl?.binding?.imBackground?.let {
            setBackgroundEdit(
                theme,
                it
            )
        }
        typeMiShadeBinding?.previewControl?.binding?.imBackground?.let {
            setBackgroundEdit(
                theme,
                it
            )
        }
    }
    private fun setBackgroundEdit(themeControl: ItemControl, imgBg: ImageView){
        Timber.e("NVQ loadBackgroundEdit 1 ${themeControl.typeBackground}")
        imgBg.clearColorFilter()
        when (themeControl.typeBackground) {
            Constant.DEFAULT -> {
                idBackgroundStore = -1
                Glide.with(requireContext())
                    .load( if (isEdit) {"${requireActivity().filesDir}/themes/${theme.idCategory}/${theme.id}/${theme.background}"}
                    else {"file:///android_asset/themes/" + theme.idCategory + "/" + theme.id + "/" + theme.background})
                    .into(imgBg)
            }

            Constant.STORE_WALLPAPER -> {
                theme.background = ""
                if (theme.idStoreWallpaper != -1) {
                    Glide.with(requireContext())
                        .load(mainViewModel.getPathBackground(theme.idStoreWallpaper))
                        .into(imgBg)
                }
            }

            Constant.TRANSPARENT -> {
                idBackgroundStore = -1
                theme.background = ""
                imgBg.setImageResource(R.drawable.ic_bg_trans)
                imgBg.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_bg_card1_dark
                    )
                )
            }

            Constant.CURRENT_BACKGROUND -> {
                idBackgroundStore = -1
                theme.background = ""
                App.myScope.launch(Dispatchers.IO) {
                    val bitmap = MethodUtils.getWallPaper(context)
                    withContext(Dispatchers.Main) {
                        imgBg.setImageBitmap(bitmap)
                    }
                }

            }

            else -> {
                theme.background = ""
                idBackgroundStore = -1
                imgBg.setImageResource(R.drawable.bg_screen_blur)
                imgBg.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_background_real_time
                    )
                )
            }
        }
    }


    private fun changeBackGround(value: String) {
        typeControlOSBinding?.previewControl?.binding?.imBg?.let {
            setBackground(
                value,
                it
            )
        }
        typeMiControlBinding?.previewControl?.binding?.imBackground?.let {
            setBackground(
                value,
                it
            )
        }
        typeMiShadeBinding?.previewControl?.binding?.imBackground?.let {
            setBackground(
                value,
                it
            )
        }
    }

    fun setBackground(typeBackground: String, imgBg: ImageView) {
        theme.typeBackground = typeBackground
        imgBg.clearColorFilter()
        when (typeBackground) {
            Constant.DEFAULT -> {
                idBackgroundStore = -1
                theme.background = "background.webp"
                val link = if (isEdit) {"file:///android_asset/themes/${theme.idCategory}/${when (theme.idCategory ) {
                    VALUE_CONTROL_CENTER_OS -> 6004
                    VALUE_SHADE -> 1004
                    else -> 2001
                }}/background.webp"}
                else {"file:///android_asset/themes/" + theme.idCategory + "/" + theme.id + "/" + theme.background}
                Glide.with(requireContext())
                    .load(link)
                    .into(imgBg)
            }

            Constant.STORE_WALLPAPER -> {
                theme.background = ""
                if (idBackgroundStore != -1) {
                    theme.idStoreWallpaper = idBackgroundStore
                    Glide.with(requireContext())
                        .load(mainViewModel.getPathBackground(idBackgroundStore))
                        .into(imgBg)
                }
            }

            Constant.TRANSPARENT -> {
                idBackgroundStore = -1
                theme.background = ""
                imgBg.setImageResource(R.drawable.ic_bg_trans)
                imgBg.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_bg_card1_dark
                    )
                )
            }

            Constant.CURRENT_BACKGROUND -> {
                idBackgroundStore = -1
                theme.background = ""
                App.myScope.launch(Dispatchers.IO) {
                    val bitmap = MethodUtils.getWallPaper(context)
                    withContext(Dispatchers.Main) {
                        imgBg.setImageBitmap(bitmap)
                    }
                }

            }

            else -> {
                theme.background = ""
                idBackgroundStore = -1
                imgBg.setImageResource(R.drawable.bg_screen_blur)
                imgBg.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_background_real_time
                    )
                )
            }
        }


    }

    private fun changeFontPreview(parent: ConstraintLayout?, fontName: String) {
        when (parent) {
            is PreviewControlOS -> {
                parent.setFontControl(fontName)
            }

            is PreviewControlMiShade -> {
                parent.setFontControl(fontName)
            }

            is PreviewControlCenter -> {
                parent.setFontControl(fontName)
            }
        }
    }

    private fun changeConner(parent: ConstraintLayout?, conner: Int) {
        Timber.e("NVQ changeConner+++ $conner")
        when (parent) {
            is PreviewControlOS -> {
                parent.setConnerBackgroundControl(conner.toFloat())
            }

            is PreviewControlMiShade -> {
                parent.setConnerBackgroundControl(conner.toFloat())
            }

            is PreviewControlCenter -> {
                parent.setConnerBackgroundControl(conner.toFloat())
            }
        }

    }

    private fun changeBackgroundColor(color: Int) {
        val stringsColor = String.format(
            "#%06X",
            (0xFFFFFF and color)
        )
        typeControlOSBinding?.previewControl?.setColorSelectControl(
            stringsColor
        )
        typeMiShadeBinding?.previewControl?.setColorSelectControl(stringsColor)
    }

    private fun changeIconShape(parent: ConstraintLayout?, iconName: String?) {
        iconName?.let {
            when (parent) {
                is PreviewControlOS -> {
                    parent.setIconSettingsControl(iconName)

                }

                is PreviewControlMiShade -> {
                    parent.setIconSettingsControl(iconName)
                }

                is PreviewControlCenter -> {
                    parent.setIconSettingsControl(iconName)
                }
            }
        }
    }

    private fun initStubMiShade() {
        try {
            binding.viewStubMiShade.setOnInflateListener { _, inflated ->
                typeMiShadeBinding = DataBindingUtil.bind(inflated)
                typeMiShadeBinding?.let {
                    it.previewControl.itemControl = theme
                }
                if (isEdit) loadThemeEdit()
            }
            binding.viewStubMiShade.viewStub?.inflate()
        } catch (e: Exception) {
        }

    }

    private fun initStubMiControl() {
        try {
            binding.viewStubMiControl.setOnInflateListener { _, inflated ->
                typeMiControlBinding = DataBindingUtil.bind(inflated)
                typeMiControlBinding?.previewControl?.itemControl = theme
                if (isEdit) loadThemeEdit()
            }
            binding.viewStubMiControl.viewStub?.inflate()
        } catch (e: Exception) {
        }

    }

    override fun nextAfterFullScreen() {
        super.nextAfterFullScreen()
        findNavController().popBackStack()
        mainViewModel.liveDataSetCurrentViewPager.postValue(true)
    }


    private fun initStubControlOS() {
        try {
            binding.viewStubControlOS.viewStub?.let { stub ->
                stub.setOnInflateListener { _, inflated ->
                    typeControlOSBinding = DataBindingUtil.bind(inflated)
                    typeControlOSBinding?.let {
                        it.previewControl.itemControl = theme
                    }
                    if (isEdit) loadThemeEdit()
                }
                stub.inflate()
            }
        } catch (e: InflateException) {
            Timber.e("hachung InflateException: $e")
        } catch (e: Exception) {
            Timber.e("hachung Exception: $e")
        }

        val listControlSettings = theme.controlCenterOS?.listControlCenterStyleVerticalTop
            ?.mapNotNull {
                if ((it.ratioWidght == 4 && it.ratioHeight == 4) || it.keyControl == Constant.KEY_CONTROL_SCREEN_TIME_OUT) {
                    it.keyControl
                } else null
            } ?: emptyList()
        mainViewModel.getListAppForCustomize(
            context,
            listControlSettings.toTypedArray()
        )
    }


    private fun initViewPager() {
        customizeCusViewPager = CustomizeControlViewPage(childFragmentManager, lifecycle, type)
        binding.vpCustomizeControl.offscreenPageLimit = customizeCusViewPager?.itemCount ?: ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
        binding.vpCustomizeControl.isSaveEnabled = false
        binding.vpCustomizeControl.isUserInputEnabled = false
        binding.vpCustomizeControl.adapter = customizeCusViewPager
        binding.vpCustomizeControl.post {
            binding.progressBar.hide()
        }
    }

    private fun getCurrentParent(): ConstraintLayout? {
        return when (type) {
            VALUE_SHADE -> {
                typeMiShadeBinding?.previewControl
            }

            VALUE_CONTROL_CENTER -> {
                typeMiControlBinding?.previewControl
            }

            else -> {
                typeControlOSBinding?.previewControl
            }
        }
    }

    override fun onPermissionGranted() {

    }
}
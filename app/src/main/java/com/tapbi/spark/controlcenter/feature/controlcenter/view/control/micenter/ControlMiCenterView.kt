package com.tapbi.spark.controlcenter.feature.controlcenter.view.control.micenter

import android.Manifest
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.text.format.DateUtils
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.DecelerateInterpolator
import android.view.animation.TranslateAnimation
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.data.model.ItemControlCenter
import com.tapbi.spark.controlcenter.data.repository.ThemeHelper
import com.tapbi.spark.controlcenter.databinding.LayoutStubChangeActionControlcenterBinding
import com.tapbi.spark.controlcenter.feature.controlcenter.adapter.AdapterAddAction
import com.tapbi.spark.controlcenter.feature.controlcenter.adapter.AdapterSettingExpandMiControl
import com.tapbi.spark.controlcenter.feature.controlcenter.adapter.AdapterSettingMiControl
import com.tapbi.spark.controlcenter.feature.controlcenter.adapter.AdapterSettingMiControl.TouchItemView
import com.tapbi.spark.controlcenter.feature.controlcenter.adapter.AdapterSettingMiControl.ValueAuto
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.ClickAddOrRemoveAction
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CloseMiControlView
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.ShowHideViewMusicMi
import com.tapbi.spark.controlcenter.feature.controlcenter.view.control.micenter.view.BrightnessMi
import com.tapbi.spark.controlcenter.feature.controlcenter.view.control.micenter.view.CommonMiCenterBinding
import com.tapbi.spark.controlcenter.feature.controlcenter.view.control.micenter.view.MiCenterLandscapeBinding
import com.tapbi.spark.controlcenter.feature.controlcenter.view.control.micenter.view.MiCenterPortraitBinding
import com.tapbi.spark.controlcenter.feature.controlios14.helper.BlurBackground
import com.tapbi.spark.controlcenter.feature.controlios14.model.InfoSystem
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.ControlCenterIOSView.OnControlCenterListener
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614
import com.tapbi.spark.controlcenter.ui.base.BaseConstraintLayout
import com.tapbi.spark.controlcenter.ui.base.BaseConstraintLayout.CallBackIntent
import com.tapbi.spark.controlcenter.ui.main.MainActivity
import com.tapbi.spark.controlcenter.utils.DensityUtils
import com.tapbi.spark.controlcenter.utils.MethodUtils
import com.tapbi.spark.controlcenter.utils.SettingUtils
import com.tapbi.spark.controlcenter.utils.StringAction
import com.tapbi.spark.controlcenter.utils.StringUtils.uppercaseFirstCharacters
import com.tapbi.spark.controlcenter.utils.TinyDB
import com.tapbi.spark.controlcenter.utils.Utils
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper
import com.tapbi.spark.controlcenter.utils.helper.rcvhepler.NpaGridLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.everything.android.ui.overscroll.IOverScrollDecor
import me.everything.android.ui.overscroll.IOverScrollState
import me.everything.android.ui.overscroll.IOverScrollStateListener
import me.everything.android.ui.overscroll.IOverScrollUpdateListener
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import timber.log.Timber
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.min

class ControlMiCenterView : BaseConstraintLayout, CallBackIntent, BrightnessMi.CallBackUpdateBg,
    TouchItemView, ValueAuto, OnControlCenterListener, CloseMiControlView {
    val oldPos: IntArray = IntArray(1)
    val newPos: IntArray = IntArray(1)
    private var binding: CommonMiCenterBinding? = null
    private var bindingViewStubActionChange: LayoutStubChangeActionControlcenterBinding? = null
    private val listType: Type = object : TypeToken<List<InfoSystem>>() {
    }.type
    private val showHideViewMusicMi = ShowHideViewMusicMi { b: Boolean ->

        binding?.apply {
            val params = gdCenter.layoutParams as LayoutParams
            if (b) {
                params.guidePercent = 0.45f
            } else {
                params.guidePercent = 0.37f
            }
            gdCenter.layoutParams = params
        }

    }

    @JvmField
    var adapterSettingMiControl: AdapterSettingMiControl? = null
    private var onControlCenterListener: OnControlCenterListener? = null
    private var maxBrightness = 255
    private var mode = 0
    private var infoSystems: MutableList<InfoSystem> = mutableListOf()
    private var adapterSettingExpandMiControl: AdapterSettingExpandMiControl? = null
    private var valueAnimateImageScroll = 0f
    private var downYImageScroll = 0f
    private var dY = 0f
    private var h = 0f
    private var w = 0f
    private var widthItem = 0f
    private var marginItem = 0f
    private var readyClose = true
    private var close = false
    private var iOverScrollDecor: IOverScrollDecor? = null
    private var readyCloseAnimate = false
    private val iOverScrollStateListener: IOverScrollStateListener =
        object : IOverScrollStateListener {
            override fun onOverScrollStateChange(
                decor: IOverScrollDecor,
                oldState: Int,
                newState: Int
            ) {
                if (newState == IOverScrollState.STATE_IDLE && readyCloseAnimate) {
                    binding?.layoutParent?.alpha = 1f
                    readyCloseAnimate = false
                    onControlCenterListener?.onClose()
                    iOverScrollDecor?.detach()
                }
            }

            override fun onScrollStateCancel() {
                binding?.layoutParent?.alpha = 1f
            }
        }
    private var spaceSwipeHideVertical = App.widthHeightScreenCurrent.h / 20f
    private var spaceSwipeHideHorizontal = App.widthHeightScreenCurrent.w / 20f
    private var mContext: Context? = null
    private var adapterAddAction: AdapterAddAction? = null
    private var adapterAllAction: AdapterAddAction? = null
    private var actionSave: MutableList<InfoSystem> = mutableListOf()
    private val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
        0
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            oldPos[0] = viewHolder.absoluteAdapterPosition
            newPos[0] = target.absoluteAdapterPosition
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            if (oldPos[0] != RecyclerView.NO_POSITION && newPos[0] != RecyclerView.NO_POSITION) {
                moveItem(oldPos[0], newPos[0])
            }
        }
    })
    private var allAction: MutableList<InfoSystem> = mutableListOf()
    private val clickAddOrRemoveAction: ClickAddOrRemoveAction = object : ClickAddOrRemoveAction {
        override fun clickAdd(infoSystem: InfoSystem, pos: Int) {
            if (actionSave.size > 15) {
                NotyControlCenterServicev614.getInstance()
                    .showToast(context.getString(R.string.the_maximum_controls, 16))
                return
            }
            actionSave.add(infoSystem)
            allAction.remove(infoSystem)
            adapterAllAction?.actionItemChange()
            adapterAddAction?.actionItemChange()
        }

        override fun clickRemove(infoSystem: InfoSystem, pos: Int) {
            if (actionSave.size < 14) {
                NotyControlCenterServicev614.getInstance()
                    .showToast(context.getString(R.string.the_minimum_controls, 13))
                return
            }
            actionSave.remove(infoSystem)
            allAction.add(infoSystem)
            adapterAllAction?.actionItemChange()
            adapterAddAction?.actionItemChange()
        }
    }
    private var orientation = 0
    private val iOverScrollUpdateListener = IOverScrollUpdateListener { decor, state, offset ->
        if (!readyCloseAnimate) {
            val distanceDownY = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                offset / -spaceSwipeHideVertical * 100
            } else {
                offset / -spaceSwipeHideHorizontal * 100
            }
            val percentOfNumberOne = distanceDownY / 100
            binding?.layoutParent?.alpha = 1f - percentOfNumberOne
        }
        if (if (orientation == Configuration.ORIENTATION_PORTRAIT) offset < -spaceSwipeHideVertical else offset < -spaceSwipeHideHorizontal) {
            readyCloseAnimate = true
        }
    }



    private var wRcc = 0f
    private var marginRccCollapse = 0f
    private var marginView = 0f
    private var isFirstShow = true
    private var isExpandViewAddAction = true
    private var downImageBrightness = false
    private var valueF = 0f
    private var touchHanding = false
    private var maxHeightTouch = 0f
    private var minHeightTouch = 0f

    private var itemControlCenter: ItemControlCenter? = null


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

    fun updateActionView(action: String, b: Boolean) {
        adapterSettingMiControl?.updateActionView(action, b)
    }

    fun updateActionDataView(b: Boolean) {
        adapterSettingExpandMiControl?.updateActionViewExpand(
            Constant.STRING_ACTION_DATA_MOBILE,
            b
        )
    }

    fun updateActionViewExpand(action: String, b: Boolean) {
        adapterSettingExpandMiControl?.updateActionViewExpand(action, b)
    }

    private fun init(context: Context) {
        if (ThemeHelper.itemControl.controlCenter?.backgroundColorDefaultControl != null) {
            itemControlCenter = ThemeHelper.itemControl.controlCenter
        }
        mContext = context
        setUpWidthHeight()
        setCallBackIntent(this)
        orientation = DensityUtils.getOrientationWindowManager(getContext())
        if (Utils.isTablet(context)){
            spaceSwipeHideVertical = App.widthHeightScreenCurrent.h / 35f
            spaceSwipeHideHorizontal = App.widthHeightScreenCurrent.w / 35f
        }
        binding = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            MiCenterPortraitBinding(
                DataBindingUtil.inflate(
                    LayoutInflater.from(context),
                    R.layout.layout_mi_control_center,
                    this,
                    true
                )
            )
        } else {
            MiCenterLandscapeBinding(
                DataBindingUtil.inflate(
                    LayoutInflater.from(context),
                    R.layout.layout_mi_control_center_land,
                    this,
                    true
                )
            )
        }
        //orientation = getResources().getConfiguration().orientation;
        calculator()
        findViews()
        setUpWightHeightCallBakImageProcessBrightness()
        calculatorWidthHeightImgAuto()
        setCallBackTouchImgProcess()
        setUpViewAddRemoveAction()
        setUpBg()
        setTextTitle()
        setClickTouch()
        setUpAdapter()
        setUpRecyclerView()
    }

    private fun calculator() {
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            wRcc = 0.84f * w
            marginRccCollapse = 0.01f * h
        } else {
            wRcc = (0.9f * w) / 2
            marginView = DensityUtils.pxFromDp(context, 8f).toInt().toFloat()
            marginRccCollapse = ((0.7f * h) - ((wRcc / 5) * 3) + (wRcc / 5) * 0.2f) / 2
        }
    }


    private fun setUpWidthHeight() {
        val widthHeightScreen = App.widthHeightScreenCurrent
        h = widthHeightScreen.h.toFloat()
        w = widthHeightScreen.w.toFloat()
        widthItem = ((0.84f * w) / 4.4).toInt().toFloat()
        marginItem = (0.01f * h)

    }

    fun reloadRcc() {
        adapterSettingMiControl?.isUnregister = true
        val infoCollapse: MutableList<InfoSystem> = ArrayList()
        for (i in 4 until infoSystems.size) {
            infoCollapse.add(infoSystems[i])
        }
        binding?.apply {
            adapterSettingMiControl = AdapterSettingMiControl(
                rccCollapse,
                valueF,
                infoCollapse,
                this@ControlMiCenterView,
                wRcc,
                marginRccCollapse,
                this@ControlMiCenterView,
                orientation
            )
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                adapterSettingMiControl?.passValue(1f)
                adapterSettingMiControl?.notyItem()
            }
            rccCollapse.adapter = adapterSettingMiControl
            setBgIconBrightNess()
        }

    }

    private fun setUpAdapter() {
        infoSystems = Gson().fromJson(
            TinyDB(
                context
            ).getString(Constant.ACTION_Mi_SELECT), listType
        )

        val infoExpand: MutableList<InfoSystem> = ArrayList()
        for (i in 0 until min(4.0, infoSystems.size.toDouble()).toInt()) {
            infoExpand.add(infoSystems[i])
        }

        val infoCollapse: MutableList<InfoSystem> = ArrayList()
        for (i in 4 until infoSystems.size) {
            infoCollapse.add(infoSystems[i])
        }

        adapterSettingMiControl = AdapterSettingMiControl(
            binding?.rccCollapse,
            valueF,
            infoCollapse,
            this,
            wRcc,
            marginRccCollapse,
            this,
            orientation
        )

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            adapterSettingMiControl?.passValue(1f)
        }
        adapterSettingMiControl?.endValue()
        adapterSettingExpandMiControl = AdapterSettingExpandMiControl(
            infoExpand,
            this,
            if (orientation == Configuration.ORIENTATION_PORTRAIT) 0.19f * h else (h * 0.4).toFloat(),
            this
        )
    }

    private val yTouchDown: Unit
        get() {
            binding?.let {
                downYImageScroll = it.imgScroll.y
            }
        }

    private fun setUpRecyclerView() {
        binding?.apply {
            rccExpand.height
            rccExpand.layoutManager = NpaGridLayoutManager(context, 2)
            rccExpand.adapter = adapterSettingExpandMiControl
            rccExpand.animation = null
            rccExpand.itemAnimator = null

            rccCollapse.layoutManager = NpaGridLayoutManager(context, 4)
            rccCollapse.adapter = adapterSettingMiControl
            rccCollapse.animation = null
            rccCollapse.itemAnimator = null
        }

    }

    private fun calculatorWidthHeightImgAuto() {
        binding?.apply {
            val imgAutoLayoutParams = imgAutoBrightness.layoutParams as LayoutParams
            imgAutoLayoutParams.width = (wRcc / 5f * 0.8f).toInt()
            imgAutoLayoutParams.height = (wRcc / 5f * 0.8f).toInt()
            imgAutoLayoutParams.topMargin = (((0.84f * w) / 2) + marginRccCollapse).toInt()
            imgAutoBrightness.layoutParams = imgAutoLayoutParams
        }

    }

    private fun setUpWightHeightCallBakImageProcessBrightness() {
        binding?.apply {
            val newLayoutParamsProcessBrightness = flProcessBrightness.layoutParams as LayoutParams
            val totalWidthRcc = wRcc
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                newLayoutParamsProcessBrightness.height = (wRcc / 5 * 2 + marginRccCollapse).toInt()
            }
            newLayoutParamsProcessBrightness.width =
                if (orientation == Configuration.ORIENTATION_PORTRAIT) (totalWidthRcc * 3 / 4).toInt() else (wRcc / 5 * 0.8f).toInt()
            newLayoutParamsProcessBrightness.leftMargin =
                if (orientation == Configuration.ORIENTATION_PORTRAIT) (totalWidthRcc / 4 * 0.6f).toInt() else (marginView + marginView * 0.5).toInt()
            flProcessBrightness.layoutParams = newLayoutParamsProcessBrightness
        }

    }

    private fun setCallBackTouchImgProcess() {
        binding?.let {
            it.viewProcessBrightness.setCallBackUpdateBg(this)
            it.viewProcessBrightness.setOnControlCenterListener(this)
        }

    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        adapterSettingMiControl?.unregister()
        adapterSettingExpandMiControl?.unregister()
    }

    private fun findViews() {
        initViewSettings()
        initMusicView()
        binding?.apply {
            imgScroll.setPadding(0, marginItem.toInt() * 3, 0, 0)
            initVerticalScrollView(layoutParent)

        }

        setBgIconBrightNess()
        showCollapse()
        maxBrightness = SettingUtils.getMaxBrightness(context)
        initBrightnessView()
    }

    private fun initBrightnessView() {
        binding?.apply {
            viewProcessBrightness.setValueBrightnessMax(maxBrightness)
            itemControlCenter?.let {
                imgProcessBrightness.setColorFilter(Color.parseColor(it.iconColorBrightness))
                viewProcessBrightness.setColorProgress(Color.parseColor(it.colorProgressBrightness))
                viewProcessBrightness.setColorBackground(Color.parseColor(it.backgroundColorBrightness))
            }

        }
    }

    private fun setBgIconBrightNess() {
        try {
            binding?.apply {
                imgAutoBrightness.setIcon(R.drawable.ic_menu_autu_brightness)
                itemControlCenter?.let {
                    imgAutoBrightness.setBackground(it.iconControl)
                }

            }
        } catch (e: Exception) {
            Timber.e(e)
        }
        updateBgBrightness(mode)
    }

    private fun viewStubChangeAction() {
        binding?.apply {
            if (viewStubChangeAction.viewStub?.parent != null) {
                viewStubChangeAction.viewStub?.setOnInflateListener { stub, inflated ->
                    bindingViewStubActionChange = DataBindingUtil.bind(inflated)
                    Timber.e("hachung bindingViewStubActionChange: $bindingViewStubActionChange")
                    bindingViewStubActionChange?.let {
                        it.tvDone.setOnClickListener {
                            Timber.e("hachung tvDone: ")
                            showLayoutView()
                        }
                        it.layoutChangeAction.setOnTouchListener { v: View?, event: MotionEvent? -> true }
                        it.imgStateView.setOnClickListener { view ->
                            if (isExpandViewAddAction) {
                                isExpandViewAddAction = false
                                it.imgStateView.setImageResource(R.drawable.ic_collapse_noty)
                            } else {
                                isExpandViewAddAction = true
                                it.imgStateView.setImageResource(R.drawable.ic_expaned_noty)
                            }
                            showCollapse()
                        }

                        actionSave = Gson().fromJson(
                            TinyDB(
                                context
                            ).getString(Constant.ACTION_Mi_SELECT), listType
                        )
                        adapterAddAction =
                            AdapterAddAction(
                                actionSave,
                                Constant.TYPE_REMOVE,
                                wRcc,
                                clickAddOrRemoveAction
                            )
                        it.rccActionSave.layoutManager = NpaGridLayoutManager(
                            context,
                            if (orientation == Configuration.ORIENTATION_PORTRAIT) 4 else 6
                        )
                        it.rccActionSave.adapter = adapterAddAction
                        it.rccActionSave.animation = null
                        it.rccActionSave.itemAnimator = null
                        itemTouchHelper.attachToRecyclerView(it.rccActionSave)
                        allAction = StringAction().addString(context.resources)

                        for (infoSystem in actionSave) {
                            for (system in allAction) {
                                if (infoSystem.name != null && infoSystem.name == system.name) {
                                    allAction.remove(system)
                                    break
                                }
                            }
                        }

                        adapterAllAction =
                            AdapterAddAction(
                                allAction,
                                Constant.TYPE_ADD,
                                wRcc,
                                clickAddOrRemoveAction
                            )
                        it.rccAllAction.layoutManager = NpaGridLayoutManager(
                            context,
                            if (orientation == Configuration.ORIENTATION_PORTRAIT) 4 else 6
                        )
                        it.rccAllAction.adapter = adapterAllAction
                    }


                }
                viewStubChangeAction.viewStub?.inflate()
            } else {
                viewStubChangeAction.viewStub?.visibility = VISIBLE
            }
        }
    }

    private fun initMusicView() {
        binding?.apply {
            imgMusic.setShowHideViewMusicMi(showHideViewMusicMi, this@ControlMiCenterView)
            if (orientation == Configuration.ORIENTATION_PORTRAIT) return
            val layoutParamsImgMusic = imgMusic.layoutParams as LayoutParams
            layoutParamsImgMusic.setMargins(0, (marginView + marginView * 0.5).toInt(), 0, 0)
            imgMusic.layoutParams = layoutParamsImgMusic
            imgMusic.setMiMusicListener {
                SettingUtils.intentPermissionNotificationListener(App.mContext)
                animationHideMain()
            }
        }

    }

    //    private final ContentObBrightness.CallBackUpdateUiBrightness callBackUpdateUiBrightness = this::updateProcessBrightness;
    @SuppressLint("ClickableViewAccessibility")
    private fun setClickTouch() {
        binding?.apply {
            imgSetting.setOnClickListener {
                ViewHelper.preventTwoClick(it,800)
                intentAction(Settings.ACTION_SETTINGS)
            }
            imgEdit.setOnClickListener {
                ViewHelper.preventTwoClick(it,800)
//                showLayoutView()
                openSplashApp()
            }

            imgAutoBrightness.setOnClickListener { checkAndSetValueImageAutoBrightness() }
            viewProcessBrightness.setOnClickListener { }
            layoutParent.setOnClickListener { }

        }

    }

    fun setUpBg() {
        binding?.apply {
            val typeBg = ThemeHelper.itemControl.typeBackground
            imgBg.clearColorFilter()
            when (typeBg) {
                Constant.TRANSPARENT -> imgBg.setImageDrawable(null)

                Constant.CURRENT_BACKGROUND -> loadCurrentBackground()

                Constant.REAL_TIME -> {
                    imgBg.setColorFilter(
                        ContextCompat.getColor(
                            context,
                            R.color.color_background_real_time
                        )
                    )
                    imgBg.setImageBitmap(BlurBackground.getInstance().bitmapBgBlur)
                }

                else -> setBgDefault()
            }
        }
    }

    private fun loadCurrentBackground() {
        App.myScope.launch(Dispatchers.IO) {
            val bitmap = MethodUtils.getWallPaper(context)
            launch(Dispatchers.Main) {
                binding?.imgBg?.setImageBitmap(bitmap)
            }
        }
    }


    @SuppressLint("ResourceType")
    private fun setBgDefault() {
        binding?.apply {
            imgBg.setImageBitmap(BlurBackground.getInstance().bitmapBgNotBlur)
        }

    }

    private fun initVerticalScrollView(scrollView: ConstraintLayout) {
        iOverScrollDecor = OverScrollDecoratorHelper.setUpStaticOverScroll(
            scrollView,
            OverScrollDecoratorHelper.ORIENTATION_VERTICAL
        )
        iOverScrollDecor?.setOverScrollStateListener(iOverScrollStateListener)
        iOverScrollDecor?.setOverScrollUpdateListener(iOverScrollUpdateListener)
    }

    private fun moveItem(oldPos: Int, newPos: Int) {
        bindingViewStubActionChange?.let {
            it.rccActionSave.post {
                actionSave.let { list ->
                    if (list.size > oldPos && list.size > newPos) {
                        val temp = list[oldPos]
                        if (!it.rccActionSave.isComputingLayout) {
                            list[oldPos] = list[newPos]
                            list[newPos] = temp
                            adapterAddAction?.notifyItemChanged(oldPos)
                            adapterAddAction?.notifyItemChanged(newPos)
                        }
                    }
                }
            }
        }

    }

    private fun setUpViewAddRemoveAction() {

    }

    fun setTextTitle() {
        if (App.tinyDB.getBoolean(Constant.SHOW_DATE_TIME, false)) {
            setTextDateTime()
        } else {
            binding?.apply {
                tvDate.visibility = GONE
                tvTime.visibility = GONE
                if (ThemeHelper.itemControl.idCategory == Constant.VALUE_CONTROL_CENTER) {
                    if (ThemeHelper.itemControl.id != Constant.KEY_ID_2002) {
//                        titleScreen.text =
//                            App.tinyDB.getString(Constant.TEXT_SHOW)
                        titleScreen.visibility = VISIBLE
                    } else {
                        titleScreen.visibility = GONE
                    }
                }
            }

        }
    }

    private fun setTextDateTime() {
        val date = SimpleDateFormat(
            Constant.FORMAT_SIMPLE_DATE,
            Locale.getDefault()
        ).format(System.currentTimeMillis())
        val dates = date.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        binding?.apply {
            tvTime.text = dates[1]
            tvDate.text = uppercaseFirstCharacters(
                DateUtils.formatDateTime(
                    context,
                    System.currentTimeMillis(),
                    18
                )
            )
            tvDate.visibility = VISIBLE
            tvTime.visibility = VISIBLE
            titleScreen.visibility = GONE
        }

    }

    fun setOnControlCenterListener(onControlCenterListener: OnControlCenterListener?) {
        this.onControlCenterListener = onControlCenterListener
    }

    fun show() {
        iOverScrollDecor?.attachView()
        binding?.layoutParent?.apply {
            alpha = 0.1f
            animate()
                .alpha(1.0f)
                .setInterpolator(DecelerateInterpolator())
                .setDuration(300)
                .start()
        }

        if (isFirstShow) {
            isFirstShow = false
            post { autoPositionTouchUp(false) }
        }
    }


    private fun showCollapse() {
        bindingViewStubActionChange?.let {

            val params = it.lineCenter.layoutParams as LayoutParams
            var value = 50
            if (!isExpandViewAddAction) {
                try {
                    value =
                        (100 - Math.round((wRcc / 5 + it.textAdd.height + it.imgStateView.height + it.imgStateView.paddingTop + it.imgStateView.paddingBottom + marginItem) / h * 100f))
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }
            val anim = ValueAnimator.ofInt((params.guidePercent * 100).toInt(), value)
            anim.addUpdateListener { valueAnimator: ValueAnimator ->
                val `val` = valueAnimator.animatedValue as Int
                params.guidePercent = `val` / 100f
                it.lineCenter.layoutParams = params
            }

            anim.setDuration(200)
            anim.start()
        }

    }

    private fun saveAction() {
        val jsonAction = Gson().toJson(actionSave)
        App.tinyDB.putString(Constant.ACTION_Mi_SELECT, jsonAction)
        if (infoSystems === actionSave) return
        infoSystems = actionSave
        setUpAdapter()
        binding?.apply {
            rccCollapse.adapter = adapterSettingMiControl
            rccExpand.adapter = adapterSettingExpandMiControl
        }

    }

    private fun showLayoutView() {
        binding?.let {
            if (it.layoutControl.visibility == VISIBLE) {
                it.layoutControl.visibility = INVISIBLE
                viewStubChangeAction()
            } else {
                it.layoutControl.visibility = VISIBLE
                bindingViewStubActionChange?.let { viewstub ->
                    viewstub.layoutChangeAction.visibility = GONE
                }
                saveAction()
            }
        }


    }

    private fun checkAndSetValueImageAutoBrightness() {
        try {
            mode = SettingUtils.getModeBrightness(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.System.canWrite(context)) {
                    SettingUtils.intentActivityRequestPermission(
                        context,
                        arrayOf(Manifest.permission.WRITE_SETTINGS)
                    )
                    onControlCenterListener?.onExit()
                    return
                }
            }
            Timber.e("hachung mode: $mode")
            updateBgBrightness(checkValueBrightness(mode))
        } catch (e: SettingNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun updateBgBrightness(isValue: Int) {
        binding?.apply {
            itemControlCenter?.let {
                if (isValue == Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL) {
                    imgAutoBrightness.setIconColor(Color.WHITE)
                    imgAutoBrightness.setBackgroundC(
                        Color.parseColor(it.backgroundColorDefaultControl)
                    )
                } else {
                    imgAutoBrightness.setIconColor(Color.parseColor(it.iconColorSelectControl))
                    imgAutoBrightness.setBackgroundC(
                        Color.parseColor(it.backgroundColorSelectControl2)
                    )
                }
            }


        }
    }

    fun updateProcessBrightness() {
        binding?.apply {
            val valueX = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                Timber.e("hachung getValueBrightness: ${SettingUtils.getValueBrightness(context)} / ${maxBrightness.toFloat()}")
                (SettingUtils.getValueBrightness(context) / maxBrightness.toFloat()) * 100f / 100f * viewProcessBrightness.width
            } else {
                (SettingUtils.getValueBrightness(context) / maxBrightness.toFloat()) * 100f / 100f * viewProcessBrightness.height
            }
            viewProcessBrightness.setValueProcess(valueX)
        }

    }

    override fun success() {
        animationHideMain()
    }

    override fun onChange() {
        try {
            mode = SettingUtils.getModeBrightness(context)
            updateBgBrightness(mode)
        } catch (e: SettingNotFoundException) {
            e.printStackTrace()
        }
    }

    override fun onBrightnessDown() {
        downImageBrightness = true
    }

    override fun onBrightnessUp() {
        downImageBrightness = false
    }

    override fun down(value: Float) {
    }

    override fun up() {
    }

    override fun value(value: Boolean) {
        autoPositionTouchUp(value)
    }

    private fun autoPositionTouchUp(value: Boolean) {
        binding?.apply {
            valueAnimateImageScroll = if (value) {
                rccCollapse.top + widthItem * 4 + marginItem * 4
            } else {
                rccCollapse.top + widthItem * 3 + marginItem * 3
            }
            animationView()
        }

    }

    private fun animationView() {
        binding?.apply {
            val total = rccCollapse.top + widthItem * 4 + marginItem * 4
            val limit = rccCollapse.top + widthItem * 3 + marginItem * 3
            if (valueAnimateImageScroll < limit) {
                readyClose = true
            } else if (valueAnimateImageScroll > total) {
                readyClose = false
            }

            if (orientation == Configuration.ORIENTATION_LANDSCAPE) return
            binding?.apply {
                imgScroll.animate().y(valueAnimateImageScroll).setDuration(0).start()

                flProcessBrightness.animate()
                    .y(valueAnimateImageScroll - flProcessBrightness.height)
                    .setDuration(0).start()

                imgAutoBrightness.animate().y(valueAnimateImageScroll - flProcessBrightness.height)
                    .setDuration(0).start()
            }
        }


    }

    private fun checkWithClose() {
        if (binding?.layoutParent?.visibility == INVISIBLE) {
            showLayoutView()
        }
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (bindingViewStubActionChange?.layoutChangeAction?.visibility == VISIBLE || downImageBrightness) {
            return super.onInterceptTouchEvent(event)
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                yTouchDown
                dY = event.rawY
                touchHanding = dY > minHeightTouch
                binding?.let {
                    maxHeightTouch = it.rccCollapse.top + widthItem * 4 + marginItem * 4
                    minHeightTouch = it.rccCollapse.top + widthItem * 3 + marginItem * 3
                }

            }

            MotionEvent.ACTION_MOVE -> {
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    iOverScrollDecor?.attachView()
                    return super.onInterceptTouchEvent(event) // Skip the rest of the logic in landscape mode
                }


                if (!touchHanding) {
                    return super.onInterceptTouchEvent(event) // Skip the touch handling if not handling touch
                }

                valueAnimateImageScroll = event.rawY + (downYImageScroll - dY)
                if (valueAnimateImageScroll + widthItem * 1.5f + marginItem < minHeightTouch && readyClose) {
                    adapterSettingMiControl?.passValue(0.0f)
                    valueF = 0.0f
                    close = true
                    checkWithClose()
                    Timber.e("hachung onClose: $onControlCenterListener")
                    onControlCenterListener?.onClose()
                    return false
                } else {
                    if (valueAnimateImageScroll < minHeightTouch) {
                        valueF = 0f
                        val range = widthItem * 1.5f + marginItem * 3
                        val minusDistance = minHeightTouch - valueAnimateImageScroll
                        valueAnimateImageScroll = minHeightTouch
                        val percent = minusDistance / range * 100f
                        val percentOfNumberOne = percent / 100f
                        var valueAnimateClose = 1f - percentOfNumberOne
                        if (valueAnimateClose < 0.1) {
                            valueAnimateClose = 0f
                        }
                        binding?.layoutParent?.alpha = valueAnimateClose
                        readyClose = true
                        iOverScrollDecor?.attachView()
                    } else if (valueAnimateImageScroll > maxHeightTouch) {
                        valueAnimateImageScroll = maxHeightTouch
                        readyClose = false
                        iOverScrollDecor?.attachView()
                    } else {
                        iOverScrollDecor?.detach()
                    }
                    animationView()
                    val range = valueAnimateImageScroll - minHeightTouch
                    val minusDistance = maxHeightTouch - minHeightTouch
                    val percent = range / minusDistance * 100f
                    val percentOfNumberOne = percent / 100f
                    valueF = percentOfNumberOne
                    adapterSettingMiControl?.passValue(percentOfNumberOne)
                    adapterSettingMiControl?.notifyItemWhenTouchParent()
                }
            }

            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                iOverScrollDecor?.detach()
                binding?.layoutParent?.translationY = 0f
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    return super.onInterceptTouchEvent(event) // Skip the rest of the logic in landscape mode
                }

                if (!touchHanding) {
                    return super.onInterceptTouchEvent(event) // Skip the touch handling if not handling touch
                }

                binding?.layoutParent?.animate()?.alpha(1f)?.setDuration(200)?.start()
                animate().alpha(1f).setDuration(200).start()
                if (close) {
                    close = false
                } else {
                    adapterSettingMiControl?.endValue()
                }
            }
        }
        return super.onInterceptTouchEvent(event)
    }


    override fun onExit() {
        onControlCenterListener?.onExit()
    }

    override fun onClose() {
        Timber.e("hachung onClose: ")
        onControlCenterListener?.onClose()
    }

    override fun close() {
        onControlCenterListener?.onExit()
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            if (bindingViewStubActionChange?.layoutChangeAction?.isShown == true) {
                showLayoutView()
            } else {
                animationHideMain()
            }
        }
        return super.dispatchKeyEvent(event)
    }

    private fun animationHideMain() {
        binding?.apply {
            animate().cancel()
            animate().alpha(0f).setDuration(300).withEndAction {
                Timber.e("hachung onClose: ")
                onControlCenterListener?.onClose()
                alpha = 1f
            }.start()
        }

    }

    private fun initViewSettings() {
        binding?.apply {
            itemControlCenter?.let {
                val colorIcon =
                    Color.parseColor(it.colorIconSettings)
                imgSetting.setColorFilter(colorIcon)
                imgEdit.setColorFilter(colorIcon)
            }

        }

    }


}

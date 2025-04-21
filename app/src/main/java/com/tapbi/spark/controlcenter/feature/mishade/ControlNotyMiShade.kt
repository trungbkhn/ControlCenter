package com.tapbi.spark.controlcenter.feature.mishade

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PointF
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.text.format.DateUtils
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orhanobut.hawk.Hawk
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.common.Constant.CURRENT_BACKGROUND
import com.tapbi.spark.controlcenter.common.Constant.REAL_TIME
import com.tapbi.spark.controlcenter.common.Constant.TRANSPARENT
import com.tapbi.spark.controlcenter.common.models.ItemAddedNoty
import com.tapbi.spark.controlcenter.data.model.ItemMiShade
import com.tapbi.spark.controlcenter.data.repository.ThemeHelper
import com.tapbi.spark.controlcenter.eventbus.EventOpen
import com.tapbi.spark.controlcenter.feature.NotyManager
import com.tapbi.spark.controlcenter.feature.NotyManager.listNotyGroup
import com.tapbi.spark.controlcenter.feature.controlcenter.adapter.AdapterNotyMi
import com.tapbi.spark.controlcenter.feature.controlcenter.adapter.AdapterSettingMiControl.TouchItemView
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CloseMiControlView
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.ITouchItemView
import com.tapbi.spark.controlcenter.feature.controlcenter.view.control.micenter.view.BrightnessMi
import com.tapbi.spark.controlcenter.feature.controlios14.helper.BlurBackground
import com.tapbi.spark.controlcenter.feature.controlios14.model.InfoSystem
import com.tapbi.spark.controlcenter.feature.controlios14.view.PermissionNotificationView
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.ControlCenterIOSView.OnControlCenterListener
import com.tapbi.spark.controlcenter.feature.controlios14.view.noty.NotyCenterViewOS.OnNotyCenterCloseListener
import com.tapbi.spark.controlcenter.feature.controlios14.view.status.BatteryView
import com.tapbi.spark.controlcenter.feature.controlios14.view.status.WifiView
import com.tapbi.spark.controlcenter.feature.mishade.adapter.ActionMiShadeAdapter
import com.tapbi.spark.controlcenter.feature.mishade.adapter.ViewPagerAdapterNotification
import com.tapbi.spark.controlcenter.feature.mishade.interfaces.DataAction
import com.tapbi.spark.controlcenter.feature.mishade.view.ItemTouchCall
import com.tapbi.spark.controlcenter.feature.mishade.view.NoScrollViewPager
import com.tapbi.spark.controlcenter.feature.mishade.view.WaveViewMiShade
import com.tapbi.spark.controlcenter.feature.mishade.view.customdot.WormDotsIndicator
import com.tapbi.spark.controlcenter.interfaces.ListenerAnim
import com.tapbi.spark.controlcenter.service.NotificationListener
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614
import com.tapbi.spark.controlcenter.ui.base.BaseConstraintLayout
import com.tapbi.spark.controlcenter.ui.base.BaseConstraintLayout.CallBackIntent
import com.tapbi.spark.controlcenter.ui.splash.SplashActivity
import com.tapbi.spark.controlcenter.utils.DensityUtils
import com.tapbi.spark.controlcenter.utils.MethodUtils
import com.tapbi.spark.controlcenter.utils.SettingUtils
import com.tapbi.spark.controlcenter.utils.StringAction
import com.tapbi.spark.controlcenter.utils.StringUtils.uppercaseFirstCharacters
import com.tapbi.spark.controlcenter.utils.TinyDB
import com.tapbi.spark.controlcenter.utils.Utils
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper
import com.tapbi.spark.controlcenter.utils.helper.rcvhepler.NpaLinearLayoutManager
import com.tapbi.spark.controlcenter.views.helper.BottomMarginItemDecoration
import me.everything.android.ui.overscroll.IOverScrollDecor
import me.everything.android.ui.overscroll.IOverScrollState
import me.everything.android.ui.overscroll.IOverScrollStateListener
import me.everything.android.ui.overscroll.IOverScrollUpdateListener
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator
import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator.ValueTouch
import me.everything.android.ui.overscroll.adapters.RecyclerViewOverScrollDecorAdapter
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.abs

class ControlNotyMiShade : BaseConstraintLayout, CloseMiControlView, TouchItemView, ITouchItemView,
    BrightnessMi.CallBackUpdateBg, OnControlCenterListener, CallBackIntent {
    private val listType: Type = object : TypeToken<List<InfoSystem?>?>() {
    }.type

    @SuppressLint("ClickableViewAccessibility")
    private val touchParent = OnTouchListener { _: View?, _: MotionEvent? -> true }
    private val valueTouch = ValueTouch { false }

    @SuppressLint("ClickableViewAccessibility")
    private val touchVieNone = OnTouchListener { _: View?, _: MotionEvent? -> true }
    private val handler = Handler(Looper.getMainLooper())
    private var spaceSwipeHideVertical = App.widthHeightScreenCurrent.h / 20f
    private var spaceSwipeHideHorizontal = App.widthHeightScreenCurrent.w / 20f
    var viewPagerAdapterNotification: ViewPagerAdapterNotification? = null
    private var onControlCenterListener: OnControlCenterListener? = null
    private val onNotyCenterCloseListener =
        OnNotyCenterCloseListener { onControlCenterListener?.onClose() }
    private var itemTouchListener: ItemTouchCall? = null

    //    private var viewBackgroundEdit: View? = null
//    private var cvHeaderEdit: CardView? = null
//    private var backgroundAction: View? = null
//    private var cardViewReset: CardView? = null
    private var layoutParent: ConstraintLayout? = null
    private var layoutChild: ConstraintLayout? = null

    //    private var cvConstraintEdit: ConstraintLayout? = null
    private var sdf: SimpleDateFormat? = null
    private var tvTime: TextView? = null
    private var tvDate: TextView? = null
    private var w = 0f
    private var wItem = 0f
    private var viewPager: NoScrollViewPager? = null
    private var cardViewPage: CardView? = null
    private var imViewPage: ImageView? = null
    private var wormDotsIndicator: WormDotsIndicator? = null
    private var rccNoty: RecyclerView? = null
    private var adapterNotyMi: AdapterNotyMi? = null
    private var actionMiShadeAdapter: ActionMiShadeAdapter? = null
    private var linearLayoutManager: NpaLinearLayoutManager? = null
    private var viewProcessBrightness: BrightnessMi? = null
    private var imgProcessBrightness: ImageView? = null
    private var maxBrightness = 255
    private var flProcessBrightness: CardView? = null
    private var bg: ImageView? = null
    private var imgEditControl: ImageView? = null
    private var tinyDB: TinyDB? = null
    private var btnClearNoty: CardView? = null
    private var infoSystems: MutableList<InfoSystem> = mutableListOf()
    private var oldDownY = 0f
    private var oldDownX = 0f
    private var totalDownY = 0f
    private var decorLayoutParent: IOverScrollDecor? = null
    private var readyClose = false
    private var isShowEditControl = false
    private var verticalOverScrollBounceEffectDecorator: VerticalOverScrollBounceEffectDecorator? =
        null
    private val iOverScrollStateListener: IOverScrollStateListener =
        object : IOverScrollStateListener {
            override fun onOverScrollStateChange(
                decor: IOverScrollDecor,
                oldState: Int,
                newState: Int
            ) {
                if (newState == IOverScrollState.STATE_IDLE && readyClose) {
                    readyClose = false
                    onNotyCenterCloseListener.closeEnd()
                    layoutParent?.alpha = 1f
                    if (verticalOverScrollBounceEffectDecorator != null) {
                        verticalOverScrollBounceEffectDecorator!!.detach()
                    }
                    verticalOverScrollBounceEffectDecorator?.detach()
                    rccNoty?.scrollToPosition(0)
                }
            }

            override fun onScrollStateCancel() {
                layoutParent?.alpha = 1f
            }
        }
    private var orientation = 0
    private val iOverScrollUpdateListener = IOverScrollUpdateListener { decor, state, offset ->
        if (!readyClose) {
            val distanceDownY = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                offset / -spaceSwipeHideVertical * 100
            } else {
                offset / -spaceSwipeHideHorizontal * 100
            }
            val percentOfNumberOne = distanceDownY / 100
            layoutParent!!.alpha = 1f - percentOfNumberOne
        }
        if (if (orientation == Configuration.ORIENTATION_PORTRAIT) offset < -spaceSwipeHideVertical else offset < -spaceSwipeHideHorizontal) {
            readyClose = true
        }
    }
    private val dataAction: DataAction = object : DataAction {
        override fun dataRcc1(): List<InfoSystem> {
            val k = if (orientation == Configuration.ORIENTATION_PORTRAIT) 5 else 3
            val data1: MutableList<InfoSystem> = ArrayList()
            for (i in 0 until k) {
                if (infoSystems.size <= i) return data1
                data1.add(infoSystems[i])
            }
            return data1
        }

        override fun dataRcc2(): List<InfoSystem> {
            val k = if (orientation == Configuration.ORIENTATION_PORTRAIT) 8 else 6
            val data2: MutableList<InfoSystem> = ArrayList()
            for (i in (if (orientation == Configuration.ORIENTATION_PORTRAIT) 4 else 3) until k) {
                if (infoSystems.size <= i) return data2
                data2.add(infoSystems[i])
            }
            return data2
        }

        override fun dataRcc3(): List<InfoSystem> {
            val k = if (orientation == Configuration.ORIENTATION_PORTRAIT) 12 else 9
            val data3: MutableList<InfoSystem> = ArrayList()
            for (i in (if (orientation == Configuration.ORIENTATION_PORTRAIT) 8 else 6) until k) {
                if (infoSystems.size <= i) return data3
                data3.add(infoSystems[i])
            }
            return data3
        }

        override fun dataRcc4(): List<InfoSystem> {
            val k = if (orientation == Configuration.ORIENTATION_PORTRAIT) 16 else 12
            val data4: MutableList<InfoSystem> = ArrayList()
            for (i in (if (orientation == Configuration.ORIENTATION_PORTRAIT) 12 else 9) until k) {
                if (infoSystems.size <= i) return data4
                data4.add(infoSystems[i])
            }
            return data4
        }

        override fun dataRcc5(): List<InfoSystem> {
            val k = if (orientation == Configuration.ORIENTATION_PORTRAIT) 20 else 15
            val data5: MutableList<InfoSystem> = ArrayList()
            for (i in (if (orientation == Configuration.ORIENTATION_PORTRAIT) 16 else 12) until k) {
                if (infoSystems.size <= i) return data5
                data5.add(infoSystems[i])
            }
            return data5
        }

        override fun dataRcc6(): List<InfoSystem> {
            val k = if (orientation == Configuration.ORIENTATION_PORTRAIT) 24 else 18
            val data6: MutableList<InfoSystem> = ArrayList()
            for (i in (if (orientation == Configuration.ORIENTATION_PORTRAIT) 20 else 15) until k) {
                if (infoSystems.size <= i) return data6
                data6.add(infoSystems[i])
            }
            return data6
        }
    }
    private var currentY = -1
    private val runnable: Runnable = object : Runnable {
        override fun run() {
            setHeightViewBackground()
            handler.post(this)
        }
    }
    private var size12 = 0f
    private var pastVisibleItems = 0
    private var countNoty = 0
    private var allAction: MutableList<InfoSystem> = mutableListOf()
    private var rvAction: RecyclerView? = null
    private var batteryView: BatteryView? = null

    //    private boolean isTouchingViewPager = false;
    private var allowOnMove = true
    private var wifiView: WifiView? = null
    private var waveView: WaveViewMiShade? = null
    private var animChangeHeight: ValueAnimator? = null
    private val mFirstP = PointF()
    private var xDown = 0
    private var yDown = 0
    private var layoutParamsViewPager: LayoutParams? = null
    private val updateHeight: ViewPagerAdapterNotification.UpdateHeight = object :
        ViewPagerAdapterNotification.UpdateHeight {
        override fun updateNewHeight(height: Int, percent: Float, enableSwipeVpg: Boolean) {
            updateHeightVpg(height, percent)
            viewPager?.setPagingEnabled(percent == 1f)
        }

        override fun animationView(b: Boolean) {
            animChangeHeight?.removeAllUpdateListeners()
            animChangeHeight?.removeAllListeners()

            val valueNewHeight =
                (if (b) (wItem * 3 + wItem * 0.2f * 5f) else wItem + wItem * 0.2f).toInt()
            animChangeHeight =
                viewPager?.measuredHeight?.let { ValueAnimator.ofInt(it, valueNewHeight) }
            animChangeHeight?.addUpdateListener { valueAnimator: ValueAnimator ->
                val `val` = valueAnimator.animatedValue as Int
                if (layoutParamsViewPager == null) {
                    layoutParamsViewPager = cardViewPage?.layoutParams as LayoutParams
                }
                layoutParamsViewPager?.height = `val`
                cardViewPage?.layoutParams = layoutParamsViewPager
                val percent = (`val` / ((wItem * 3 + wItem * 0.2f * 5f)) * 100)
                viewPagerAdapterNotification?.let {
                    if (it.notificationOneFragment == null || it.notificationOneFragment.adapterRccTopShade == null) {
                        return@addUpdateListener
                    }
                    it.notificationOneFragment.adapterRccTopShade!!.valueF =
                        (if (b) Math.round(percent) else Math.round(percent - 29)).toFloat()
                } ?: return@addUpdateListener

                wormDotsIndicator?.alpha = if (b) percent else 29 - percent
            }
            animChangeHeight?.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                }

                override fun onAnimationEnd(animation: Animator) {
                    if (!b) {
                        viewPagerAdapterNotification?.let {
                            it.notificationOneFragment.adapterRccTopShade?.valueF =
                                0f
                        }

                    }
                }

                override fun onAnimationCancel(animation: Animator) {
                }

                override fun onAnimationRepeat(animation: Animator) {
                }
            })
            animChangeHeight?.setDuration(200)
            animChangeHeight?.start()
        }
    }
    private var permissionNotificationView: PermissionNotificationView? = null

    private var itemMiShade: ItemMiShade? = null

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

    fun updateActionView(action: String?, b: Boolean) {
        viewPagerAdapterNotification?.updateActionView(action, b)
    }

    fun setOnControlCenterListener(onControlCenterListener: OnControlCenterListener?) {
        this.onControlCenterListener = onControlCenterListener
        setUpAdapter()
        setUpAction()
        setUpRcc()
        setUpVpg()
        setUpOverScroll()
        setUpOverScrollRcc()
    }

    fun show(listenerAnim: ListenerAnim?) {
        verticalOverScrollBounceEffectDecorator?.attachView()
        decorLayoutParent?.attachView()

        animate().cancel()
        alpha = 0f
        animate().alpha(1f).setDuration(300).withEndAction {
            listenerAnim?.onAnimEnd()
        }.start()
        val scaleAnimation = scaleAnimation
        layoutChild!!.startAnimation(scaleAnimation)
    }

    private val scaleAnimation: ScaleAnimation
        get() {
            val scaleAnimation = ScaleAnimation(
                0.9f,
                1f,
                0.9f,
                1f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
            scaleAnimation.duration = 300
            scaleAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    setUpVpg()
                }

                override fun onAnimationEnd(animation: Animation) {
                }

                override fun onAnimationRepeat(animation: Animation) {
                }
            })
            return scaleAnimation
        }


    fun updateColor() {
        setUpAdapter()
        setUpRcc()
    }

    fun updateBgIcon(b: Boolean) {
        if (!b) {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                updateHeightVpg((wItem + (wItem * 0.2f)).toInt(), 0f)
            }
        }

        viewPagerAdapterNotification =
            ViewPagerAdapterNotification(updateHeight, onControlCenterListener, dataAction)


        viewPager?.let {
            it.adapter = viewPagerAdapterNotification
            wormDotsIndicator?.setViewPager(it)
            if (infoSystems.size > 12) {
                it.setHaveTwoPage(true)
                wormDotsIndicator?.visibility = VISIBLE
            } else {
                it.setHaveTwoPage(false)
                wormDotsIndicator?.visibility = INVISIBLE
            }
        }
//        setUpMoreAction()
    }


    private fun setUpNewNoty() {
        if (NotificationListener.getInstance() != null) {
            if (NotificationListener.getInstance().isFirstLoad) {
                NotificationListener.getInstance().loadInFirstUse()
            }
        }
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setUpNewNoty()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewPagerAdapterNotification?.unregisterItemBaseRcv()
    }


    fun init() {
        if (ThemeHelper.itemControl.miShade?.backgroundColorSelectControl != null) {
            itemMiShade = ThemeHelper.itemControl.miShade
        }
        size12 = DensityUtils.pxFromDp(context, 12f)
        sdf = SimpleDateFormat(Constant.FORMAT_SIMPLE_DATE, Locale.getDefault())
        tinyDB = TinyDB(context)
        infoSystems = Gson().fromJson(
            tinyDB!!.getString(Constant.ACTION_SHADE_SELECT), listType
        )
        widthHeight
        orientation = DensityUtils.getOrientationWindowManager(context)
        if (Utils.isTablet(context)){
            spaceSwipeHideVertical = App.widthHeightScreenCurrent.h / 35f
            spaceSwipeHideHorizontal = App.widthHeightScreenCurrent.w / 35f
        }
        if (orientation == Constant.PORTRAIT) {
            LayoutInflater.from(context).inflate(R.layout.layout_shade, this, true)
        } else {
            LayoutInflater.from(context).inflate(R.layout.layout_shade_land, this, true)
        }
        findView()
        setUpBg()
        setDataDateTime()
        setCallBackIntent(this)
        resetAction()
        updateStatusNotificationAccess()

//        setUpMoreAction()
    }

//    private fun setUpMoreAction() {
//        val bgActionLayout = findViewById<ImageView>(R.id.bgActionLayout)
//        val bgActionColor = findViewById<ImageView>(R.id.bgActionColor)
//        val bgActionEdge = findViewById<ImageView>(R.id.bgActionEdge)
//        val bgActionMore = findViewById<ImageView>(R.id.bgActionMore)
//
//        if (bgActionLayout == null || bgActionColor == null || bgActionEdge == null || bgActionMore == null) {
//            return
//        }
//        val iconAction =
//            if (tinyDB!!.getString(Constant.ICON_ACTION_SELECT) != null && !tinyDB!!.getString(
//                    Constant.ICON_ACTION_SELECT
//                ).isEmpty()
//            ) {
//                tinyDB!!.getString(Constant.ICON_ACTION_SELECT)
//            } else {
//                "ic_ellipse"
//            }
//        val resourceBg =
//            resources.getIdentifier(iconAction + "_shade", "drawable", context.packageName)
//        try {
//            bgActionLayout.setImageResource(resourceBg)
//            bgActionColor.setImageResource(resourceBg)
//            bgActionEdge.setImageResource(resourceBg)
//            bgActionMore.setImageResource(resourceBg)
//        } catch (e: Exception) {
//        }
//
//        bgActionLayout.setOnClickListener { openActionSettings(Constant.ACTION_LAYOUT) }
//        bgActionColor.setOnClickListener { v: View? -> openActionSettings(Constant.ACTION_COLOR) }
//        bgActionEdge.setOnClickListener { v: View? -> openActionSettings(Constant.ACTION_EDGE_TRIGGERS) }
//        bgActionMore.setOnClickListener { v: View? -> openActionSettings(Constant.ACTION_HOME) }
//    }

    private fun openActionSettings(action: String) {
        if (App.isStartActivity) {
            EventBus.getDefault().post(EventOpen(action))
        } else {
            val intent = Intent(context, SplashActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setAction(action)
            context.startActivity(intent)
        }
        postDelayed({ this.animationHideMain() }, 300)
    }

    private fun resetAction() {
        allAction = StringAction().addString(context.resources)
        for (infoSystem in infoSystems) {
            for (system in allAction) {
                if (infoSystem.name != null && infoSystem.name == system.name) {
                    allAction.remove(system)
                    break
                }
            }
        }
    }

    private fun setUpVpg() {
        setWWithVpg()
        updateBgIcon(true)

    }

    private val widthHeight: Unit
        get() {
            val widthHeightScreen = App.widthHeightScreenCurrent
            w = widthHeightScreen.w.toFloat()
            wItem = w * 0.94f / 5
        }


    private fun setUpOverScrollRcc() {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) return
        verticalOverScrollBounceEffectDecorator = VerticalOverScrollBounceEffectDecorator(
            RecyclerViewOverScrollDecorAdapter(rccNoty),
            valueTouch
        )
        verticalOverScrollBounceEffectDecorator?.setOverScrollStateListener(
            iOverScrollStateListener
        )
        verticalOverScrollBounceEffectDecorator?.setOverScrollUpdateListener(
            iOverScrollUpdateListener
        )
    }

    private fun setUpOverScroll() {
        decorLayoutParent = OverScrollDecoratorHelper.setUpStaticOverScroll(
            layoutChild,
            OverScrollDecoratorHelper.ORIENTATION_VERTICAL
        )
        decorLayoutParent?.setOverScrollStateListener(iOverScrollStateListener)
        decorLayoutParent?.setOverScrollUpdateListener(iOverScrollUpdateListener)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) return super.dispatchTouchEvent(ev)

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                allowOnMove = true
                pastVisibleItems =
                    linearLayoutManager?.findFirstCompletelyVisibleItemPosition() ?: 0
                oldDownY = ev.rawY
                oldDownX = ev.rawX  // Lưu vị trí X khi bắt đầu
                totalDownY = (wItem * 2) + wItem * 0.2f * 5f
                countNoty = adapterNotyMi?.itemCount ?: 0
                mFirstP[ev.rawX] = ev.rawY
                xDown = x.toInt()
                yDown = y.toInt()
            }

            MotionEvent.ACTION_MOVE -> {
                if (isShowEditControl || !allowOnMove || viewProcessBrightness?.isTouching == true) {
                    return super.dispatchTouchEvent(ev)
                }

                if (countNoty > 0 && ev.rawY < viewPager?.top!! && viewPager?.currentItem == 0) {
                    return super.dispatchTouchEvent(ev)
                }

                val deltaX = ev.rawX - oldDownX  // Tính khoảng cách di chuyển X
                val deltaY = ev.rawY - oldDownY  // Tính khoảng cách di chuyển Y

                if (abs(deltaX) > abs(deltaY)) {
                    // Vuốt theo hướng ngang (trái/phải), không xử lý logic dọc
                    return super.dispatchTouchEvent(ev)
                }

                // Logic xử lý vuốt dọc (vertical swipe)
                val newDownY = ev.rawY
                if (pastVisibleItems == 0 || countNoty == 0) {
                    val lineDistance = newDownY - oldDownY
                    var percent = (lineDistance / totalDownY * 100f).let {
                        if (viewPagerAdapterNotification?.notificationOneFragment?.isExpand == true) {
                            100 + it
                        } else it
                    }

                    percent = percent.coerceIn(0f, 100f)
                    viewPagerAdapterNotification?.notificationOneFragment?.apply {
                        adapterRccTopShade?.valueF = percent
                        adapterRccBotShade?.valueF = percent
                        adapterRccCenterShade?.valueF = percent
                    }
                    if (percent >= 100) {
                        viewPagerAdapterNotification?.notificationOneFragment?.isExpand = true
                    } else if (percent <= 0) {
                        viewPagerAdapterNotification?.notificationOneFragment?.isExpand = false
                    }

                    val newHeight = (wItem + percent / 100 * totalDownY).toInt()
                        .coerceAtLeast((wItem + wItem * 0.2f).toInt())
                    updateHeightVpg(newHeight, percent / 100f)
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                viewPagerAdapterNotification?.notificationOneFragment?.let { fragment ->
                    if (fragment.adapterRccTopShade?.valueF!! > 50) {
                        if (fragment.adapterRccTopShade?.valueF != 100f) {
                            fragment.isExpand = true
                            viewPager?.setPagingEnabled(true)
                            updateHeight.animationView(true)
                        }
                    } else {
                        if (fragment.adapterRccTopShade?.valueF != 0f) {
                            fragment.isExpand = false
                            viewPager?.setPagingEnabled(false)
                            updateHeight.animationView(false)
                        }
                    }
                }
            }
        }

        return super.dispatchTouchEvent(ev)
    }


    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return super.onInterceptTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun findView() {
        textViewTypeFace()
        viewClearNoty()
        updateViewBrightness()
        viewWormDotsIndicator()
        setBgViewControl()
        rccNoty = findViewById(R.id.rccNoty)


        batteryView = findViewById(R.id.batteryView)
        batteryView?.setTextShowHide(GONE)
        bg = findViewById(R.id.bg)
        val imgGoToNotificationAccess = findViewById<ImageView>(R.id.imgGoToNotificationAccess)
        imgEditControl = findViewById(R.id.imgEditControl)
        layoutParent = findViewById(R.id.layoutParent)
        layoutChild = findViewById(R.id.layoutChild)

        layoutParent?.setOnTouchListener(touchParent)
        layoutParent?.setOnClickListener({
//            cardViewReset?.visibility = INVISIBLE
        })
        val viewNone = findViewById<View>(R.id.viewNone)
        val viewNone1 = findViewById<View>(R.id.viewNone1)
        wifiView = findViewById(R.id.wifiView)
        permissionNotificationView = findViewById(R.id.permissionNotiView)
        viewNone.setOnTouchListener(touchVieNone)
        viewNone1.setOnTouchListener(touchVieNone)

        imgGoToNotificationAccess.setOnClickListener {
            ViewHelper.preventTwoClick(it, 800)
            showHideViewEditAction(false)
            btnClearNoty?.setVisibility(VISIBLE)
            intentAction(Settings.ACTION_SETTINGS)
        }
        imgEditControl?.setOnClickListener {
//            showHideViewEditAction(true)
//            btnClearNoty?.setVisibility(GONE)
//            if (viewBackgroundEdit != null && cvHeaderEdit != null) {
//                viewBackgroundEdit!!.post {
//                    val layoutParams = viewBackgroundEdit!!.layoutParams as LayoutParams
//                    layoutParams.topToBottom = cvHeaderEdit!!.id
//                    layoutParams.height = 0
//                    viewBackgroundEdit!!.layoutParams = layoutParams
//                }
//            }
            ViewHelper.preventTwoClick(it, 800)
            openSplashApp()

        }



        viewPager?.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
            }

            override fun onPageScrollStateChanged(state: Int) {
//                setTouchingViewPager(state == ViewPager.SCROLL_STATE_DRAGGING);
            }
        })

        rvAction?.setOnTouchListener { _: View?, _: MotionEvent? ->
//            cardViewReset?.visibility = INVISIBLE
            false
        }
        if (NotyControlCenterServicev614.getInstance() != null) {
            val autoHidePanel = App.tinyDB.getBoolean(
                Constant.KEY_AUTO_HIDE_NOTI_PANEL_SYSTEM_SHADE,
                Constant.VALUE_DEFAULT_AUTO_HIDE_NOTI_PANEL_SYSTEM_SHADE
            )
            NotyControlCenterServicev614.getInstance()
                .setAutoHideNotyPanelSystemStyleShade(autoHidePanel)
        }
        permissionNotificationView?.setClickListener { this.animationHideMain() }


//        viewBackgroundEdit = findViewById(R.id.viewBackgroundEdit)
//        backgroundAction = findViewById(R.id.background_action)
//        rvAction = findViewById(R.id.rv_action)
//        cvHeaderEdit = findViewById(R.id.cvHeaderEdit)
//        cvConstraintEdit = findViewById(R.id.constraint_edit)
//        cardViewReset = findViewById(R.id.cv_reset)
//        val imgEditReset = findViewById<ImageView>(R.id.img_reset)
//        val imgBackEdit = findViewById<ImageView>(R.id.imBackEdit)

        //        imgBackEdit?.setOnClickListener { backPressedFromEditAction() }
//        imgEditReset?.setOnClickListener {
//            cardViewReset?.visibility = VISIBLE
//        }

//        cvConstraintEdit?.setOnClickListener {
//            cardViewReset?.visibility = INVISIBLE
//        }

//        cardViewReset?.setOnClickListener {
//            val stringAction = StringAction()
//            itemTouchListener?.listSystem?.clear()
//            infoSystems.clear()
//            val infoSystemDefaults = stringAction.addString(resources)
//            for (i in 0..16) {
//                infoSystems.add(infoSystemDefaults[i])
//            }
//            resetAction()
//            actionMiShadeAdapter?.setData(infoSystems, allAction)
//            setHeightViewBackground()
//            cardViewReset?.visibility = INVISIBLE
//        }
        //        if (rvAction != null && cardViewReset != null) {
//
//        }
    }

    fun setNotySnoozed(key: String?) {
        if (adapterNotyMi != null) {
            return
        }
        val notySnoozed = DensityUtils().deserializeFromJson(Hawk.get(key))
        Hawk.delete(key)
        if (notySnoozed == null || notySnoozed.notyModels.isEmpty()) {
            return
        }
        for (notyModel in notySnoozed.notyModels) {
            notyModel.time = System.currentTimeMillis()
        }
        val notyGroups = ArrayList(listNotyGroup)
        notyGroups.add(0, notySnoozed)
        adapterNotyMi!!.setData(notyGroups)
    }

    private fun setWithProcessBrightness() {
        if (orientation == Configuration.ORIENTATION_PORTRAIT) return
        val layoutParams = flProcessBrightness!!.layoutParams as LayoutParams
        layoutParams.width = (((w * 0.35f) / 4) * 0.7f).toInt()
        flProcessBrightness?.layoutParams = layoutParams


        val layoutParams2 = imgProcessBrightness!!.layoutParams as FrameLayout.LayoutParams
        layoutParams2.width = (((w * 0.35f) / 4) * 0.7f).toInt()

        imgProcessBrightness?.layoutParams = layoutParams2
    }

    fun changeBattery(isChange: Boolean, level: Int) {
        batteryView?.setLevelBattery(level)
        batteryView?.changeImageBattery(isChange)
    }

    fun setUpBg() {
        if (bg != null && ThemeHelper.itemControl != null) {
            bg!!.clearColorFilter()
            val typeBg = ThemeHelper.itemControl.typeBackground
            if (typeBg == TRANSPARENT) {
                bg!!.setImageDrawable(null)
            }else if (typeBg == REAL_TIME && NotyControlCenterServicev614.getInstance() != null && NotyControlCenterServicev614.getInstance().resultDataMediaProjection != null) {
                bg!!.setColorFilter(
                    ContextCompat.getColor(
                        context,
                        R.color.color_background_real_time
                    )
                )
                bg!!.setImageBitmap(BlurBackground.getInstance().bitmapBgBlur)
            } else {
                val backgroundColor = ThemeHelper.itemControl.backgroundColor
                if (backgroundColor.isNotEmpty()) {
                    bg!!.setBackgroundColor(Color.parseColor(backgroundColor))
                } else {
                    bg!!.setImageBitmap(BlurBackground.getInstance().bitmapBgNotBlur)
                }
            }
        }
    }

    fun updateProcessBrightness() {
        try {
            viewProcessBrightness?.let {
                val valueX = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    (Settings.System.getInt(
                        context.contentResolver, Settings.System.SCREEN_BRIGHTNESS
                    ) / maxBrightness.toFloat()) * 100f / 100f * it.width
                } else {
                    (Settings.System.getInt(
                        context.contentResolver, Settings.System.SCREEN_BRIGHTNESS
                    ) / maxBrightness.toFloat()) * 100f / 100f * it.height
                }
                viewProcessBrightness?.setValueProcess(valueX)
            }

        } catch (e: SettingNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun setUpAdapter() {
        adapterNotyMi = AdapterNotyMi(rccNoty, onNotyCenterCloseListener, this)
        if (NotificationListener.getInstance() != null) {
            adapterNotyMi!!.setData(listNotyGroup)
        }
        //adapterNotyMi.setHasStableIds(true);
    }

    private fun setUpAction() {
        if (rvAction != null) {
            actionMiShadeAdapter = ActionMiShadeAdapter()
            actionMiShadeAdapter?.setData(infoSystems, allAction)
            val gridLayoutManager = GridLayoutManager(App.mContext, 4)

            gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    if (actionMiShadeAdapter!!.getItemViewType(position) == Constant.TYPE_VIEW_HEADER) {
                        return 4
                    }
                    return 1
                }
            }
            rvAction?.layoutManager = gridLayoutManager
            rvAction?.adapter = actionMiShadeAdapter
            itemTouchListener = ItemTouchCall(actionMiShadeAdapter, infoSystems)
            val itemTouchHelper = ItemTouchHelper(
                itemTouchListener!!
            )
            itemTouchHelper.attachToRecyclerView(rvAction)

            actionMiShadeAdapter!!.setOnMoveItemListener { this.setHeightViewBackground() }

            itemTouchListener!!.setOnSelectedItem(object : ItemTouchCall.OnSelectedItem {
                override fun onSelectedChange(moving: Boolean) {
                    actionMiShadeAdapter!!.setChanging()
                    if (moving) {
                        handler.post(runnable)
                    } else {
                        Handler(Looper.getMainLooper()).postDelayed({
                            handler.removeCallbacks(
                                runnable
                            )
                        }, 200)
                    }
                }

                override fun onMoved() {
                }
            })

            rvAction?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    setHeightViewBackground()
                }
            })
        }
    }

    private fun setHeightViewBackground() {
//        actionMiShadeAdapter?.let {
//            if (it.constraintLayout != null) {
//                backgroundAction?.visibility = VISIBLE
//                val location = IntArray(2)
//                it.constraintLayout.getLocationOnScreen(location)
//                val y = location[1]
//                if (y > 0) {
//                    currentY = y
//                }
//                backgroundAction?.y =
//                    (currentY - actionMiShadeAdapter!!.constraintLayout.height).toFloat()
//            } else {
//                backgroundAction?.visibility = INVISIBLE
//            }
//        }
    }

    private fun setUpRcc() {
        linearLayoutManager = NpaLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rccNoty?.layoutManager = linearLayoutManager
        rccNoty?.addItemDecoration(BottomMarginItemDecoration(32f))
        rccNoty?.setHasFixedSize(false)
        rccNoty?.adapter = adapterNotyMi
    }

    private fun setWWithVpg() {
        viewPager?.setPagingEnabled(orientation != Configuration.ORIENTATION_PORTRAIT)
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            updateHeightVpg((wItem + (wItem * 0.2f)).toInt(), 0f)
        }
    }

    fun updateHeightVpg(value: Int, percent: Float) {
        if (layoutParamsViewPager == null) {
            layoutParamsViewPager = cardViewPage?.layoutParams as LayoutParams
        }
        layoutParamsViewPager?.height = value
        cardViewPage?.layoutParams = layoutParamsViewPager
        wormDotsIndicator?.alpha = percent
    }

    fun setDataDateTime() {
        val date = sdf!!.format(System.currentTimeMillis())
        val dates = date.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        tvTime!!.text = dates[1]
        tvDate!!.text = uppercaseFirstCharacters(
            DateUtils.formatDateTime(
                context,
                System.currentTimeMillis(),
                18
            )
        )
    }

    private fun showHideViewEditAction(show: Boolean) {
//        if (cvConstraintEdit != null && imgEditControl != null) {
//            cvConstraintEdit!!.visibility = if (show) VISIBLE else GONE
//            isShowEditControl = show
//            if (!isShowEditControl) {
//                imgEditControl!!.visibility = GONE
//            }
//        }
    }

    override fun close() {
        onControlCenterListener!!.onExit()
    }

    override fun down(value: Float) {
    }

    override fun up() {
    }

    override fun onChange() {
    }

    override fun onBrightnessDown() {
    }

    override fun onBrightnessUp() {
    }

    override fun onExit() {
        onControlCenterListener!!.onExit()
    }

    override fun onClose() {
        onControlCenterListener!!.onClose()
    }

    override fun success() {
        onControlCenterListener!!.onExit()
    }


    private fun backPressedFromEditAction() {
//        if (btnClearNoty != null && cardViewReset != null) {
//            showHideViewEditAction(false)
//            btnClearNoty!!.visibility = VISIBLE
//            cardViewReset!!.visibility = INVISIBLE
//            if (itemTouchListener!!.listSystem.isNotEmpty()) {
//                infoSystems = itemTouchListener!!.listSystem
//            }
//            resetAction()
//            val jsonShade = Gson().toJson(infoSystems)
//            tinyDB!!.putString(Constant.ACTION_SHADE_SELECT, jsonShade)
//            setUpVpg()
//        }
    }

    fun updateStateSim() {
        waveView?.updateStateSim()
    }

    fun updateViewAirplane(enabled: Boolean) {
        waveView?.visibility = if (enabled) GONE else VISIBLE
    }

    fun notyRemoved(positionGroup: Int, posChild: Int, isRemovedGroup: Boolean, idNoty: String?) {
        post {
//            adapterNotyMi?.let {
//                if (isRemovedGroup) {
//                    it.removeGroup(positionGroup)
//                } else {
//                    if (listNotyGroup.size > positionGroup) {
//                        it.removedItemInGroup(
//                            positionGroup,
//                            listNotyGroup[positionGroup],
//                            posChild
//                        )
//                    }
//                }
//            }
            adapterNotyMi?.setData(NotyManager.listNotyGroup)
        }
    }

    fun notyAdded(itemAddedNoty: ItemAddedNoty?) {
        adapterNotyMi?.setAddedItem(itemAddedNoty)
    }

    fun reloadNoty() {
        adapterNotyMi?.reloadDataAdapter()
    }



    fun updateWifi(enabled: Boolean) {
        wifiView?.updateWifiMode(enabled)
    }

    fun updateDataMobile() {
        wifiView?.updateTvDataMobile()
    }

    override fun onHorizontalScroll() {
        allowOnMove = false
    }

    fun updateStatusNotificationAccess() {
        if (NotificationListener.getInstance() != null) {
            permissionNotificationView?.visibility = GONE
            btnClearNoty?.visibility = VISIBLE
        } else {
            adapterNotyMi?.setData(ArrayList())
            permissionNotificationView!!.visibility = VISIBLE
            btnClearNoty?.visibility = GONE
        }
    }

    private fun animationHideMain() {
        layoutParent?.let {
            it.animate().cancel()
            it.animate().alpha(0f).setDuration(300).withEndAction {
                onNotyCenterCloseListener.closeEnd()
                it.alpha = 1f
            }.start()
        }

    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
//            if (cvConstraintEdit != null && cvConstraintEdit!!.isShown) {
//                backPressedFromEditAction()
//            } else {
//                animationHideMain()
//            }
            animationHideMain()
        }
        return super.dispatchKeyEvent(event)
    }

    fun setonSignalsChange(lever: Int) {
        if (waveView != null) {
            waveView!!.onSignalsChange(lever)
        }
    }


    /*
    update Themes Phase1
     */

    private fun setBgViewControl() {
        viewPager = findViewById(R.id.vpgs)
        cardViewPage = findViewById(R.id.cvViewPage)
        imViewPage = findViewById(R.id.imViewPage)
        itemMiShade?.let {
            if (it.backgroundColorViewPagerControl.isNotEmpty()) {
                imViewPage?.setBackgroundColor(Color.parseColor(it.backgroundColorViewPagerControl))
            } else {
                imViewPage?.post {
                    imViewPage?.setImageBitmap(
                        BlurBackground.getInstance()
                            .getBitmap("${Constant.FOLDER_THEMES_ASSETS}/${ThemeHelper.itemControl.idCategory}/${ThemeHelper.itemControl.id}/${it.backgroundImageViewPagerControl}")
                    )
                }
            }
        }
    }

    private fun textViewTypeFace() {
        tvTime = findViewById(R.id.tvTime)
        tvDate = findViewById(R.id.tvDate)
        waveView = findViewById(R.id.waveView)
        itemMiShade?.let {
            tvTime?.setTextColor(Color.parseColor(it.colorTextTime))
            tvTime?.typeface = Typeface.createFromAsset(
                context.assets,
                Constant.FOLDER_FONT_CONTROL_ASSETS + it.fontTextTime
            )
            tvDate?.typeface = Typeface.createFromAsset(
                context.assets,
                Constant.FOLDER_FONT_CONTROL_ASSETS + ThemeHelper.itemControl.font
            )

        }
        waveView?.setTextTypeFace(ThemeHelper.itemControl.font)
    }

    private fun viewClearNoty() {
        btnClearNoty = findViewById(R.id.btnClearNoty)
        itemMiShade?.let {
            btnClearNoty?.setCardBackgroundColor(Color.parseColor(it.backgroundColorClearNoty))
        }
        btnClearNoty?.setOnClickListener {
            if (NotificationListener.getInstance() != null) {
                NotificationListener.getInstance().deleteAllNoty()
            }
            animationHideMain()
        }
    }

    private fun updateViewBrightness() {
        imgProcessBrightness = findViewById(R.id.imgBrightness)
        flProcessBrightness = findViewById(R.id.flBrightness)
        viewProcessBrightness = findViewById(R.id.viewBrightness)
        maxBrightness = SettingUtils.getMaxBrightness(context)
        viewProcessBrightness?.setValueBrightnessMax(maxBrightness)
        setWithProcessBrightness()
        itemMiShade?.let {
            imgProcessBrightness?.setColorFilter(Color.parseColor(it.colorIconBrightness))
            viewProcessBrightness?.setColorProgress(Color.parseColor(it.colorProgressBrightness))
            viewProcessBrightness?.setColorBackground(Color.parseColor(it.backgroundColorBrightness))
        }
        viewProcessBrightness?.setOnControlCenterListener(this)
    }

    private fun viewWormDotsIndicator() {
        wormDotsIndicator = findViewById(R.id.worm_dots_indicator)
        itemMiShade?.let {
            wormDotsIndicator?.setDotIndicatorColor(Color.parseColor(it.backgroundColorSelectControl))
            wormDotsIndicator?.setStrokeDotsIndicatorColor(Color.parseColor(it.colorWormDots))
        }


    }


}

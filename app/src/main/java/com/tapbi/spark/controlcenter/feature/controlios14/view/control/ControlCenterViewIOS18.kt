package com.tapbi.spark.controlcenter.feature.controlios14.view.control

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.adapter.ChooseControlAdapter.IControlClick
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.common.Constant.NO_POSITION
import com.tapbi.spark.controlcenter.data.model.ItemControl
import com.tapbi.spark.controlcenter.data.repository.ThemeHelper
import com.tapbi.spark.controlcenter.data.repository.ThemesRepository.getItemControl
import com.tapbi.spark.controlcenter.data.repository.ThemesRepository.isControlEditing
import com.tapbi.spark.controlcenter.data.repository.ThemesRepository.saveThemeIOSEdit
import com.tapbi.spark.controlcenter.databinding.LayoutControlCenterIos18Binding
import com.tapbi.spark.controlcenter.databinding.LayoutControlIosPage1Binding
import com.tapbi.spark.controlcenter.databinding.LayoutControlIosPage3Binding
import com.tapbi.spark.controlcenter.eventbus.EventSaveControl
import com.tapbi.spark.controlcenter.feature.SpaceItemDecorator
import com.tapbi.spark.controlcenter.feature.controlios14.adapter.PagerControlIosAdapter
import com.tapbi.spark.controlcenter.feature.controlios14.helper.BlurBackground
import com.tapbi.spark.controlcenter.feature.controlios14.model.ControlCustomize
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlCenterIosModel
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel
import com.tapbi.spark.controlcenter.feature.controlios14.view.ControlCenterIOS18Page3
import com.tapbi.spark.controlcenter.feature.controlios14.view.ControlMusicViewIos18Page2
import com.tapbi.spark.controlcenter.feature.controlios14.view.SpanSize
import com.tapbi.spark.controlcenter.feature.controlios14.view.SpannedGridLayoutManager
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.ControlCenterIOSView.initSizeRecyclerView
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.BaseControlCenterIos
import com.tapbi.spark.controlcenter.receiver.HHeadsetReceiver.Companion.instance
import com.tapbi.spark.controlcenter.receiver.HHeadsetReceiver.HeadsetReceiverCallback
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614
import com.tapbi.spark.controlcenter.utils.DensityUtils
import com.tapbi.spark.controlcenter.utils.MediaUtils
import com.tapbi.spark.controlcenter.utils.MethodUtils
import com.tapbi.spark.controlcenter.utils.Utils.getLastModel
import com.tapbi.spark.controlcenter.utils.Utils.removeLastModel
import com.tapbi.spark.controlcenter.utils.Utils.replaceItemAt
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper
import com.tapbi.spark.controlcenter.utils.safeDelay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber
import kotlin.jvm.internal.Intrinsics.Kotlin
import kotlin.math.abs

class ControlCenterViewIOS18 : BaseControlCenterIos, HeadsetReceiverCallback {
    private lateinit var itemControlCenterListener: ItemControlCenterListener
    private lateinit var binding: LayoutControlCenterIos18Binding
    private var fakeBottomSheetView: ChooseControlView? = null
    private var pagerControlIosAdapter: PagerControlIosAdapter? = null
    protected var dataSetupViewControlModel : DataSetupViewControlModel ?= null
    lateinit var bindingPage1: LayoutControlIosPage1Binding
    lateinit var page2: ControlMusicViewIos18Page2
    lateinit var page3: ControlCenterIOS18Page3
    private var controlMusicUtils: MediaUtils? = null
    private var iMediaListener: MediaUtils.IMediaListener? = null

    private var currentChangePos: Int = NO_POSITION

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun init(context: Context) {
        Timber.e("NVQ onViewInit+++")
        try {
            val layoutInflater = LayoutInflater.from(App.ins)
            orientation = DensityUtils.getOrientationWindowManager(getContext())
            binding = LayoutControlCenterIos18Binding.inflate(layoutInflater, this, true)
            itemControl = ThemeHelper.itemControl
            try {
                typeface =
                    if ((itemControl?.font != null && !itemControl!!.font.isEmpty() && itemControl!!.font != "font_default")) Typeface.createFromAsset(
                        context.assets,
                        Constant.PATH_FOLDER_FONT + itemControl?.font
                    ) else null
            } catch (e: Exception) {
                Timber.e("NVQ Exception $e")
            }
            dataSetupViewControlModel = DataSetupViewControlModel(itemControl!!.id, itemControl!!.idCategory, typeface)
            iMediaListener = object  : MediaUtils.IMediaListener{
                override fun stateChange(state: Int) {
                    if (::page2.isInitialized){
                        page2.stateChange(state)
                    }
                    createItemViewControlCenterIOS?.controlMusicIOS18?.stateChange(state) ?: run {
                        setStateMusicIOS18WithRetry(state)
                    }
                }

                override fun contentChange(
                    artist: String?,
                    track: String?,
                    thumb: Bitmap?,
                    packageName: String?
                ) {
                    if (::page2.isInitialized){
                        page2.contentChange(artist, track, thumb, packageName);
                    }
                    createItemViewControlCenterIOS?.controlMusicIOS18?.contentChange(artist, track, thumb, packageName) ?: run {
                        contentChangeIOS18WithRetry(artist, track, thumb, packageName)
                    }
                }

                override fun checkPermissionNotificationListener(isCheck: Boolean) {
                    if (::page2.isInitialized){
                        page2.checkPermissionNotificationListener(isCheck)
                    }
                    createItemViewControlCenterIOS?.controlMusicIOS18?.checkPermissionNotificationListener(isCheck) ?: run {
                        checkPermissionNotificationListenerIOS18WithRetry(isCheck)
                    }
                }

                override fun timeMediaChange(state: Int) {
                    if (::page2.isInitialized){
                        page2.timeMediaChange(state)
                    }
                }
            }
            controlMusicUtils = MediaUtils(context, iMediaListener)
            bindingPage1 = LayoutControlIosPage1Binding.inflate(layoutInflater)
            page2 = ControlMusicViewIos18Page2(App.ins,controlMusicUtils)
            page3 = ControlCenterIOS18Page3(App.ins)
            binding.settingExpand.changeFont(typeface, "")
            binding.volumeExpand.changeFont(typeface)
            binding.musicExpand.changeFont(typeface)
            binding.screenTimeoutLayout.changeFont(typeface)
            if (itemControl?.isThemeIos18 == true && orientation == Configuration.ORIENTATION_PORTRAIT) {
                binding.actionPower.visibility = VISIBLE
                binding.actionPlus.visibility = VISIBLE
            } else {
                binding.actionPower.visibility = GONE
                binding.actionPlus.visibility = GONE
            }
            itemControlCenterListener = object : ItemControlCenterListener {
                override fun onClickRemove(pos: Int) {
                    Timber.e("NVQ itemControlCenterListener remove $pos")
                    removeItem(pos)
                }

                override fun onClickChange(pos: Int) {
                    currentChangePos = pos
                    Timber.e("NVQ itemControlCenterListener add ")
                    fakeBottomSheetView?.setListControl(listControl1)
                    fakeBottomSheetView?.showView()
                }

                override fun onClickAdd() {
                    currentChangePos = NO_POSITION
                    Timber.e("NVQ itemControlCenterListener add ")
                    fakeBottomSheetView?.setListControl(listControl1)
                    fakeBottomSheetView?.showView()
                }
            }
            initViewControl()
        } catch (e: Exception) {
            Timber.e("hachung Exception:$e")
        }
    }

    override fun removeItem(pos: Int) {
        orientation = DensityUtils.getOrientationWindowManager(getContext())
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (controlCenterIOSAdapter1 != null) {
                listControl1.removeAt(pos)
                controlCenterIOSAdapter1?.changeListControl(listControl1)
                initSizeRecyclerView(
                    bindingPage1.rclControl1,
                    controlCenterIOSAdapter1,
                    DensityUtils.getScreenWidth(),
                    listControl1,
                    orientation
                )
                loadScrollAfterChangeList()
            }
        }
    }

    override fun changeData(itemControl: ItemControl) {
        this.itemControl = itemControl
        initViewControl()
    }

    override fun reloadTheme(itemControl: ItemControl) {
        this.itemControl = itemControl
    }

    override fun updateVolume(volume: Int) {
        super.updateVolume(volume)
        page2?.updateVolume()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initViewControl() {
        initViewAddControl()
        pagerControlIosAdapter = PagerControlIosAdapter(bindingPage1.root, page2, page3)
        if (pagerControlIosAdapter != null) {
            binding.viewPager.adapter = pagerControlIosAdapter
            binding.viewPager.offscreenPageLimit = 4
            binding.viewPager.isUserInputEnabled = false
        }
        if (itemControl != null && itemControl!!.controlCenterOS != null) {
            orientation = DensityUtils.getOrientationWindowManager(getContext())
            binding.statusView.changeColorStatus(Color.parseColor(itemControl!!.colorStatus))
            createItemViewControlCenterIOS = CreateItemViewControlCenterIOS(
                context,
                binding.root,
                bindingPage1.clControl,
                binding.settingExpand,
                binding.screenTimeoutLayout,
                binding.viewFocus,
                binding.brightnessExpand,
                binding.volumeExpand,
                binding.statusView,
                binding.imgViewTouch,
                binding.imgTouchCloseControl,
                binding.musicExpand,
                fakeBottomSheetView,
                controlMusicUtils,
                object : ShowPage3Listener{
                    override fun onShowPage3() {
                        binding.viewPager.setCurrentItem(2, false)
                    }

                    override fun onShowPage2() {
                        binding.viewPager.setCurrentItem(1, false)
                    }
                },
                dataSetupViewControlModel
            )
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                listControl1 = itemControl?.controlCenterOS!!.listControlCenterStyleVerticalTop as java.util.ArrayList<ControlCenterIosModel>
                bindingPage1.nest2.visibility = GONE
                bindingPage1.viewMargin.visibility = VISIBLE
                controlCenterIOSAdapter1 = ControlCenterIOSAdapter(context, listControl1, createItemViewControlCenterIOS)
                controlCenterIOSAdapter1?.setListenerItem(itemControlCenterListener)
                initAdapterControl(bindingPage1.rclControl1, controlCenterIOSAdapter1, listControl1)
            } else {
                listControl1 = itemControl?.controlCenterOS?.controlCenterStyleHorizontal!!.listControlLeft as java.util.ArrayList<ControlCenterIosModel>
                listControl2 = itemControl?.controlCenterOS?.controlCenterStyleHorizontal!!.listControlRight as java.util.ArrayList<ControlCenterIosModel>
                bindingPage1.nest2.visibility = VISIBLE
                bindingPage1.viewMargin.visibility = VISIBLE
                controlCenterIOSAdapter1 = ControlCenterIOSAdapter(context, listControl1, createItemViewControlCenterIOS)
                controlCenterIOSAdapter2 = ControlCenterIOSAdapter(context, listControl2, createItemViewControlCenterIOS)
                controlCenterIOSAdapter1?.setListenerItem(itemControlCenterListener)
                controlCenterIOSAdapter2?.setListenerItem(itemControlCenterListener)
                initAdapterControl(bindingPage1.rclControl1, controlCenterIOSAdapter1, listControl1)
                initAdapterControl(bindingPage1.rclControl2, controlCenterIOSAdapter2, listControl2)
                bindingPage1.viewBottom2.layoutParams.apply {
                    height = MethodUtils.dpToPx(200f)
                }
                bindingPage1.viewBottom1.layoutParams.apply {
                    height = MethodUtils.dpToPx(4f)
                }
            }
        }
        setBgNew()
        initViewScroll()
        initListener()
    }

    private fun initViewAddControl() {
        if (fakeBottomSheetView == null) {
            fakeBottomSheetView = ChooseControlView(NotyControlCenterServicev614.getInstance())
            fakeBottomSheetView?.setListener(object : IControlClick {
                override fun onClick(position: Int, controlCustomize: ControlCustomize) {
                    if (currentChangePos == NO_POSITION) {
                        var insertIndex = listControl1.size - 1
                        if (insertIndex < 0) insertIndex = 0
                        listControl1.add(insertIndex, getItemControl(controlCustomize, listControl1))
                    } else {
                        replaceItemAt(listControl1, currentChangePos, getItemControl(controlCustomize, listControl1))
                        currentChangePos = NO_POSITION
                    }
                    controlCenterIOSAdapter1!!.changeListControl(listControl1)
                    initSizeRecyclerView(
                        bindingPage1.rclControl1,
                        controlCenterIOSAdapter1,
                        DensityUtils.getScreenWidth(),
                        listControl1,
                        orientation
                    )
                    loadScrollAfterChangeList()
                    fakeBottomSheetView?.removeClickView(position)
                    fakeBottomSheetView?.hideView()
                }
            })
        }
    }

    private fun initListener() {
        binding.actionPlus.setOnClickListener { v ->
            ViewHelper.preventTwoClick(v)
            setEditControl(true)
        }
        binding.actionPower.setOnLongClickListener { v ->
            ViewHelper.preventTwoClick(v)
            Timber.e("NVQ actionPower long click")
            false
        }
        binding.icBack.setOnClickListener { v ->
            ViewHelper.preventTwoClick(v)
            setEditControl(false)
            if (itemControl != null && itemControl!!.controlCenterOS != null) {
                itemControl?.controlCenterOS!!.listControlCenterStyleVerticalTop = listControl1
                saveThemeIOSEdit(itemControl!!, this)
            }
        }
        bindingPage1.rootPage1.setOnClickListener {
            setHideViewExpand()
        }
        page3?.binding?.page3?.setOnClickListener {
            setHideViewExpand()
        }
        page2?.binding?.page2?.setOnClickListener {
            Timber.e("NVQ imgBackground click")
            setHideViewExpand()
        }
        if (createItemViewControlCenterIOS != null){
            page2.onClickSettingListener = createItemViewControlCenterIOS?.onClickSettingListener
            page2.onMusicViewListener = createItemViewControlCenterIOS?.onMusicViewListener
        }
    }

    private var isEdit = false
    private fun setEditControl(b: Boolean) {
        Timber.e("NVQ setEditControl $isEdit // $b")
        if (isEdit != b) {
            isEdit = b
            loadItemAdd(isEdit, listControl1)
            isControlEditing = isEdit
            Timber.e("NVQ setEditControl $isEdit // $b")
            orientation = DensityUtils.getOrientationWindowManager(getContext())
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                if (controlCenterIOSAdapter1 != null) {
                    val childCount = bindingPage1.rclControl1.childCount
                    for (i in 0 until childCount) {
                        val child = bindingPage1.rclControl1.getChildAt(i)
                        val viewHolder = bindingPage1.rclControl1.getChildViewHolder(child)

                        if (viewHolder is ControlCenterIOSAdapter.ViewHolder) {
                            viewHolder.loadViewRemove(isEdit)
                        }
                    }
                }
            }
            if (isEdit) {
                binding.actionPlus.visibility = GONE
                binding.actionPower.visibility = GONE
                binding.icBack.visibility = VISIBLE
                binding.tvCus.visibility = VISIBLE
            } else {
                binding.actionPlus.visibility = VISIBLE
                binding.actionPower.visibility = VISIBLE
                binding.icBack.visibility = GONE
                binding.tvCus.visibility = GONE
            }
            loadScrollAfterChangeList()
        }
    }
    private fun loadScrollAfterChangeList(){
        safeDelay(200){
            if (::binding.isInitialized){
                if (binding.viewPager.currentItem == 0){
                    loadScrollPage1()
                } else {
                    binding.viewPager.isUserInputEnabled = true
                }
            }
        }
    }
    private fun loadItemAdd(isAdd: Boolean, list: java.util.ArrayList<ControlCenterIosModel>) {
        if (isAdd) {
            val add = ControlCenterIosModel(getLastModel(listControl1))
            add.keyControl = Constant.KEY_CONTROL_ADD
            add.ratioHeight = 4
            add.ratioWidght = 4
            list.add(add)
        } else {
            removeLastModel(list)
        }
        controlCenterIOSAdapter1!!.changeListControl(list)
        initSizeRecyclerView(
            bindingPage1.rclControl1,
            controlCenterIOSAdapter1,
            DensityUtils.getScreenWidth(),
            listControl1,
            orientation
        )
        loadScrollAfterChangeList()
    }

    private fun initViewScroll() {
        bindingPage1.nest1.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            onScrollNet(
                v,
                scrollX,
                scrollY,
                oldScrollX,
                oldScrollY
            )
        })
        bindingPage1.nest2.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            onScrollNet(
                v,
                scrollX,
                scrollY,
                oldScrollX,
                oldScrollY
            )
        })
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Timber.e("NVQ Đã chọn trang: $position")
                if (position == 0 && bindingPage1 != null) {
                    bindingPage1.nest1.fullScroll(FOCUS_UP)
                    bindingPage1.nest2.fullScroll(FOCUS_UP)
                    updateSync()
                }
                if (position == 0) {
                    loadScrollPage1()
                } else {
                    binding.viewPager.isUserInputEnabled = true
                }
//                page2.isVisibleToUser = position == 1
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })
    }

    private fun loadScrollPage1() {
        if (::bindingPage1.isInitialized){
            val b= bindingPage1.nest1.canScrollVertically(1);
            Timber.e("NVQ Đã chọn trang: " + 0 + " // " + b);
            binding.viewPager.setUserInputEnabled(!b);
            bindingPage1.nest1.isScrollEnabled = b;
        }
    }

    private fun onScrollNet(
        v: NestedScrollView,
        scrollX: Int,
        scrollY: Int,
        oldScrollX: Int,
        oldScrollY: Int
    ) {
        val dy = scrollY - oldScrollY // khoảng cách cuộn
        val isScrollingUp = dy < 0
        val isScrollingDown = dy > 0

        if (isScrollingUp) {
            Log.e("NVQ", "Đang vuốt lên: " + abs(dy.toDouble()) + "px")
        } else if (isScrollingDown) {
            Log.e("NVQ", "Đang vuốt xuống: " + dy + "px")
        }

        // Kiểm tra xem đã cuộn tới đầu chưa
        if (scrollY == 0) {
            Log.e("NVQ", "Đã cuộn tới đầu")
        }

        // Kiểm tra xem đã cuộn tới cuối chưa
        val viewHeight = v.getChildAt(0).measuredHeight
        val containerHeight = v.height
        if ((scrollY + containerHeight) >= viewHeight && isScrollingDown) {
            Log.e("NVQ", "Đã cuộn tới cuối")
            binding.viewPager.currentItem = 1
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (listControl1.isEmpty() && itemControl != null && itemControl!!.controlCenterOS != null) listControl1 =
                itemControl!!.controlCenterOS!!.listControlCenterStyleVerticalTop as java.util.ArrayList<ControlCenterIosModel>
            if (binding != null) initSizeRecyclerView(
                bindingPage1.rclControl1,
                controlCenterIOSAdapter1,
                w,
                listControl1,
                orientation
            )
        } else {
            if (binding != null) {
                initSizeRecyclerView(
                    bindingPage1.rclControl1,
                    controlCenterIOSAdapter1,
                    w,
                    listControl1,
                    orientation
                )
                initSizeRecyclerView(
                    bindingPage1.rclControl2,
                    controlCenterIOSAdapter2,
                    w,
                    listControl2,
                    orientation
                )
            }
        }
    }


    override fun initAdapterControl(
        recyclerView: RecyclerView,
        controlCenterIOSAdapter: ControlCenterIOSAdapter?,
        list: java.util.ArrayList<ControlCenterIosModel>
    ) {
        controlCenterIOSAdapter?.let {
            initSizeRecyclerView(
                recyclerView,
                controlCenterIOSAdapter,
                DensityUtils.getScreenWidth(),
                list,
                orientation
            )
            val spannedGridLayoutManager =
                SpannedGridLayoutManager(SpannedGridLayoutManager.Orientation.VERTICAL, 16)
            recyclerView.layoutManager = spannedGridLayoutManager
            recyclerView.adapter = controlCenterIOSAdapter
            val finalControlCenterIOSAdapter = controlCenterIOSAdapter
            spannedGridLayoutManager.spanSizeLookup =
                SpannedGridLayoutManager.SpanSizeLookup { integer: Int? ->
                    val model = finalControlCenterIOSAdapter!!.listControl[integer!!]
                    SpanSize(model.ratioWidght, model.ratioHeight)
                }

            recyclerView.addItemDecoration(SpaceItemDecorator(10, 10, 10, 10))

            val gestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    val child = recyclerView.findChildViewUnder(e.x, e.y)
                    if (child == null) {
                        if (NotyControlCenterServicev614.getInstance() != null) {
                            NotyControlCenterServicev614.getInstance().closeNotyCenter()
                        }
                    }
                    return true
                }

                override fun onScroll(
                    e1: MotionEvent,
                    e2: MotionEvent,
                    distanceX: Float,
                    distanceY: Float
                ): Boolean {
                    // Xử lý khi vuốt (có thể bỏ qua nếu không cần)
                    return false
                }
            })

            recyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    gestureDetector.onTouchEvent(e)
                    return false
                }

                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                    // Không cần xử lý
                }

                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
                    // Không cần xử lý
                }
            })
        }

    }

    override fun setBgNew() {
        if (itemControl != null) {
            if (itemControl!!.typeBackground == Constant.TRANSPARENT) {
                binding.imgBackground.setImageDrawable(null)
            } else if (itemControl!!.typeBackground == Constant.REAL_TIME) {
                binding.imgBackground.setColorFilter(
                    ContextCompat.getColor(
                        context!!, R.color.color_background_real_time
                    )
                )
                binding.imgBackground.setImageBitmap(BlurBackground.getInstance().bitmapBgBlur)
            } else {
                binding.imgBackground.setImageBitmap(BlurBackground.getInstance().bitmapBgNotBlur)
            }
        }
    }


    override fun show() {
        Timber.e("NVQ showok")
        if (::binding.isInitialized){
            binding.viewPager.currentItem = 0
            loadScrollAfterChangeList()
        }
    }

    override fun hide() {
        if (::binding.isInitialized){
            setEditControl(false)
            Timber.e("NVQ hideOk")
        }
    }

    override fun updateSync() {
        super.updateSync()
        if (::page3.isInitialized){
            page3.updateSync()
        }
    }

    override fun updateViewAirplane(state: Boolean) {
        super.updateViewAirplane(state)
        if (::page3.isInitialized){
            page3.updateViewAirplane(state)
        }

    }
    override fun updateViewWifi(state: Boolean) {
        super.updateViewWifi(state)
        if (::page3.isInitialized){
            page3.updateViewWifi(state)
        }
    }

    override fun updateViewDataMobile(state: Boolean) {
        super.updateViewDataMobile(state)
        if (::page3.isInitialized){
            page3.updateViewDataMobile(state)
        }
    }

    override fun updateViewBluetooth(state: Boolean) {
        super.updateViewBluetooth(state)
        if (::page3.isInitialized){
            page3.updateViewBluetooth(state)
        }
    }

    override fun updateDoNotDisturb(b: Boolean) {
        super.updateDoNotDisturb(b)
        if (::page3.isInitialized){
            page3.updateDoNotDisturb(b)
        }
    }

    override fun updateViewLocation(state: Boolean) {
        super.updateViewLocation(state)
        if (::page3.isInitialized){
            page3.updateViewLocation(state)
        }
    }


    @Subscribe
    fun onEventUpdateControl(eventSaveControl: EventSaveControl) {
        eventUpdate(eventSaveControl)
        if (eventSaveControl.action == Constant.EVENT_UPDATE_STATE_VIEW_CONTROL) {
            if (bindingPage1.nest2 != null) {
                bindingPage1.nest2.fullScroll(FOCUS_UP)
            }
            if (bindingPage1.nest1 != null) {
                Log.d("duongcvc", "onEventUpdateControl: 1")
                bindingPage1.nest1.fullScroll(FOCUS_UP)
            }
            Log.d("duongcvc", "onEventUpdateControl: ")
            createItemViewControlCenterIOS?.updateState()
        }
    }


    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            setHideViewExpand()
        }
        return super.dispatchKeyEvent(event)
    }

    interface ItemControlCenterListener {
        fun onClickRemove(pos: Int)
        fun onClickChange(pos: Int)
        fun onClickAdd()
    }
    interface ShowPage3Listener{
        fun onShowPage3()
        fun onShowPage2()
    }
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        listenerHeadset()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        instance.unregister(context)
    }

    private fun listenerHeadset() {
        try {
            instance.register(context)
            instance.callback = this
        } catch (E: Exception){}
    }

    fun setStateMusicIOS18WithRetry(state : Int){
        CoroutineScope(Dispatchers.IO).launch {
            while (createItemViewControlCenterIOS?.controlMusicIOS18 == null){
                delay(100)
            }
            withContext(Dispatchers.Main){
                createItemViewControlCenterIOS?.controlMusicIOS18?.stateChange(state)
            }
        }
    }
    fun checkPermissionNotificationListenerIOS18WithRetry(isCheck : Boolean){
        CoroutineScope(Dispatchers.IO).launch {
            while (createItemViewControlCenterIOS?.controlMusicIOS18 == null){
                delay(100)
            }
            withContext(Dispatchers.Main){
                createItemViewControlCenterIOS?.controlMusicIOS18?.checkPermissionNotificationListener(isCheck)
            }
        }
    }
    fun contentChangeIOS18WithRetry(artist: String?, track: String?, thumb: Bitmap?, packageName: String?){
        CoroutineScope(Dispatchers.IO).launch {
            while (createItemViewControlCenterIOS?.controlMusicIOS18 == null){
                delay(100)
            }
            withContext(Dispatchers.Main){
                createItemViewControlCenterIOS?.controlMusicIOS18?.contentChange(artist, track, thumb, packageName);
            }
        }
    }


    override fun onHeadsetConnected() {
        if (::page2.isInitialized){
            page2.onHeadsetConnected()
        }
        createItemViewControlCenterIOS?.controlMusicIOS18?.onHeadsetConnected()
    }

    override fun onHeadsetDisconnected() {
        if (::page2.isInitialized){
            page2.onHeadsetDisconnected()
        }
        createItemViewControlCenterIOS?.controlMusicIOS18?.onHeadsetDisconnected()
    }
}
package com.tapbi.spark.controlcenter.feature.controlcenter.view.control.pixel


import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.provider.Settings
import android.text.format.DateUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.data.model.ItemControlPixel
import com.tapbi.spark.controlcenter.data.repository.ThemeHelper
import com.tapbi.spark.controlcenter.feature.controlcenter.adapter.AdapterControlPixel
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CloseMiControlView
import com.tapbi.spark.controlcenter.feature.controlcenter.view.control.pixel.view.BrightnessPixel
import com.tapbi.spark.controlcenter.feature.controlcenter.view.control.pixel.view.CommonPixelBinding
import com.tapbi.spark.controlcenter.feature.controlcenter.view.control.pixel.view.PixelLandscapeBinding
import com.tapbi.spark.controlcenter.feature.controlcenter.view.control.pixel.view.PixelPortraitBinding
import com.tapbi.spark.controlcenter.feature.controlcenter.view.control.pixel.view.ViewPagePixel
import com.tapbi.spark.controlcenter.feature.controlios14.helper.BlurBackground
import com.tapbi.spark.controlcenter.feature.controlios14.model.InfoSystem
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.ControlCenterIOSView
import com.tapbi.spark.controlcenter.ui.base.BaseConstraintLayout
import com.tapbi.spark.controlcenter.utils.DensityUtils
import com.tapbi.spark.controlcenter.utils.MethodUtils
import com.tapbi.spark.controlcenter.utils.SettingUtils
import com.tapbi.spark.controlcenter.utils.StringUtils
import com.tapbi.spark.controlcenter.utils.TinyDB
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Locale


class ControlPixel : BaseConstraintLayout, BaseConstraintLayout.CallBackIntent, CloseMiControlView {

    private var binding: CommonPixelBinding? = null
    private var orientation = 0
    private var infoSystems: MutableList<InfoSystem> = mutableListOf()

    private var adapterControl1: AdapterControlPixel? = null
    private var adapterControl2: AdapterControlPixel? = null

    private val listType: Type = object : TypeToken<List<InfoSystem>>() {
    }.type
    private var maxBrightness = 255

    private var limitedDistance = 0f
    private var initialY = 0f
    private val maxDistance = 500f // Khoảng cách tối đa để làm mờ
    private var isDragging = false // Để kiểm tra xem người dùng có đang kéo không


    private var onControlCenterListener: ControlCenterIOSView.OnControlCenterListener? = null


    private var viewPager: ViewPagePixel? = null

    private var itemPixel: ItemControlPixel? = null


    fun setOnControlCenterListener(onControlCenterListener: ControlCenterIOSView.OnControlCenterListener) {
        this.onControlCenterListener = onControlCenterListener
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            viewPager?.setOnControlCenterListener(onControlCenterListener)
        } else {
            adapterControl2?.setListener(this)
            adapterControl1?.setListener(this)
        }

    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        if (ThemeHelper.itemControl.pixel?.backgroundSelectControl != null) {
            itemPixel = ThemeHelper.itemControl.pixel
        }
        infoSystems = Gson().fromJson(
            TinyDB(
                context
            ).getString(Constant.ACTION_Mi_SELECT), listType
        )
        maxBrightness = SettingUtils.getMaxBrightness(context)
        orientation = DensityUtils.getOrientationWindowManager(context)
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding = PixelPortraitBinding(
                DataBindingUtil.inflate(
                    LayoutInflater.from(context),
                    R.layout.layout_control_pixel,
                    this,
                    true
                )
            )
            initViewPage()
            viewWormDotsIndicator()
        } else {
            binding = PixelLandscapeBinding(
                DataBindingUtil.inflate(
                    LayoutInflater.from(context),
                    R.layout.layout_control_pixel_land,
                    this,
                    true
                )
            )
            initRVControl()
        }
        setCallBackIntent(this)
        initBrightnessView()
        setTextDateTime()
        setUpBg()
        updateProcessBrightness()
        initListenerAndTouch()

    }

    private fun initRVControl() {
        val list1: MutableList<InfoSystem> = mutableListOf()
        val list2: MutableList<InfoSystem> = mutableListOf()
        for (i in infoSystems.indices) {
            if (i <= 3) {
                list1.add(infoSystems[i])
            } else {
                list2.add(infoSystems[i])
            }
        }
        adapterControl1 = AdapterControlPixel()
        adapterControl1?.setData(list1, orientation)
        adapterControl2 = AdapterControlPixel()
        adapterControl2?.setData(list2, orientation)
        (binding as PixelLandscapeBinding).apply {
            rvControl1.adapter = adapterControl1
            rvControl2.adapter = adapterControl2
        }
    }

    private fun initViewPage() {
        val (list1, list2) = infoSystems.partition { infoSystems.indexOf(it) <= 7 }
        if (viewPager == null) {
            viewPager = ViewPagePixel()
        }
        viewPager?.setListInfoSystem(list1, list2)

        (binding as? PixelPortraitBinding)?.viewPager?.adapter = viewPager
    }

    private fun setTextDateTime() {
        val date = SimpleDateFormat(
            Constant.FORMAT_SIMPLE_DATE,
            Locale.getDefault()
        ).format(System.currentTimeMillis())
        val dates = date.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        binding?.apply {
            tvTime.text = dates[1]
            tvDate.text = StringUtils.uppercaseFirstCharacters(
                DateUtils.formatDateTime(
                    context,
                    System.currentTimeMillis(),
                    18
                )
            )
            itemPixel?.let {
                tvTime.typeface = Typeface.createFromAsset(
                    context.assets, Constant.FOLDER_FONT_CONTROL_ASSETS +
                            it.fontTextTime
                )
                tvDate.typeface = Typeface.createFromAsset(
                    context.assets, Constant.FOLDER_FONT_CONTROL_ASSETS +
                            it.fontTextDate
                )
            }
            tvDate.setTextColor(Color.parseColor(itemPixel?.colorTextTime))
            tvTime.setTextColor(Color.parseColor(itemPixel?.colorTextTime))

        }
    }

    fun setUpBg() {
        binding?.apply {
            val typeBg = ThemeHelper.itemControl.typeBackground
            imgBg.clearColorFilter()
            when (typeBg) {
                Constant.TRANSPARENT -> imgBg.setImageDrawable(null)

                Constant.CURRENT_BACKGROUND -> App.myScope.launch(Dispatchers.IO) {
                    val bitmap = MethodUtils.getWallPaper(context)
                    launch(Dispatchers.Main) {
                        binding?.imgBg?.setImageBitmap(bitmap)
                    }
                }

                Constant.REAL_TIME -> {
                    imgBg.setColorFilter(
                        ContextCompat.getColor(
                            context,
                            R.color.color_background_real_time
                        )
                    )
                    imgBg.setImageBitmap(BlurBackground.getInstance().bitmapBgBlur)
                }

                else -> {
                    setColorBackgroundDefault(
                        imgBg,
                        BlurBackground.getInstance().bitmapBgNotBlur,
                        ThemeHelper.itemControl.backgroundColor
                    )
                }
            }
        }
    }

    private fun setColorBackgroundDefault(image: ImageView, bitmap: Bitmap?, pathBg: String) {
        if (pathBg.startsWith("#")) {
            image.setBackgroundColor(Color.parseColor(pathBg))
        } else {
            image.setImageBitmap(bitmap)
        }
    }


    private fun initBrightnessView() {
        binding?.apply {

            viewProcessBrightness.setValueBrightnessMax(maxBrightness)
            itemPixel?.let {
                viewProcessBrightness.setColorProgress(Color.parseColor(it.backgroundSelectControl))
                viewProcessBrightness.setBackgroundRadius(
                    it.connerViewBrightness,
                    Color.parseColor(it.backgroundViewBrightness)
                )
            }
            viewProcessBrightness.setCallBackUpdateBg(object : BrightnessPixel.CallBackUpdateBg {
                override fun onChange() {

                }

                override fun onBrightnessDown() {

                }

                override fun onBrightnessUp() {
                }


            })
            viewProcessBrightness.setOnControlCenterListener(object :
                ControlCenterIOSView.OnControlCenterListener {
                override fun onExit() {
                    onControlCenterListener?.onExit()
                }

                override fun onClose() {
                    onControlCenterListener?.onClose()
                }

            })
        }

    }

    fun updateProcessBrightness() {
        binding?.apply {
            if (!viewProcessBrightness.isTouching()) {
//                val valueX = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
//                    (SettingUtils.getValueBrightness(context) / maxBrightness.toFloat()) * 100f / 100f * viewProcessBrightness.width
//                } else {
//                    (SettingUtils.getValueBrightness(context) / maxBrightness.toFloat()) * 100f / 100f * viewProcessBrightness.height
//                }
                val valueX =
                    (SettingUtils.getValueBrightness(context) / maxBrightness.toFloat()) * 100f / 100f * viewProcessBrightness.width
                viewProcessBrightness.setValueProcess(valueX)
            }

        }

    }

    private fun viewWormDotsIndicator() {
        (binding as PixelPortraitBinding).apply {
            itemPixel?.let {
                wormDotsIndicator.setDotIndicatorColor(Color.parseColor(it.colorDotIndicator))
                wormDotsIndicator.setStrokeDotsIndicatorColor(Color.parseColor(it.colorStrokeDotsIndicator))
            }

            wormDotsIndicator.setViewPager(viewPager)
        }

    }

    fun updateActionView(action: String, b: Boolean) {
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            viewPager?.updateActionView(action, b)
        } else {
            adapterControl1?.updateActionView(action, b)
            adapterControl2?.updateActionView(action, b)
        }

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewPager?.clearViewList()
    }

    override fun close() {
        onControlCenterListener?.onClose()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListenerAndTouch() {
        binding?.apply {
            layoutControl.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // Lưu tọa độ ban đầu khi bắt đầu vuốt
                        initialY = event.rawY
                        limitedDistance = 0f
                        isDragging = true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        if (isDragging) {
                            val distance = initialY - event.rawY
                            limitedDistance =
                                0f.coerceAtLeast(distance.coerceAtMost(maxDistance))
                            // Tính toán alpha dựa trên khoảng cách (từ 1.0 đến 0.0)
                            val alpha = 1.0f - (limitedDistance / maxDistance)
                            layoutControl.alpha = alpha
                            if (limitedDistance >= maxDistance) {
                                animationHideMain()
                            }
                        }
                    }

                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {

                        if (limitedDistance > 0 || isDragging) {
                            resetDragState()
                            animationHideMain()
                        }
                    }
                }
                true
            }
            imgEdit.setOnClickListener {
                ViewHelper.preventTwoClick(it, 800)
                openSplashApp()
            }
            imgSetting.setOnClickListener {
                ViewHelper.preventTwoClick(it, 800)
                intentAction(Settings.ACTION_SETTINGS)
            }

        }
    }


    private fun animationHideMain() {
        onControlCenterListener?.onClose()
        binding?.layoutControl?.alpha = 1f
        if (binding is PixelPortraitBinding) {
            (binding as PixelPortraitBinding).viewPager.setCurrentItem(0, false)
        }

    }

    private fun resetDragState() {
        isDragging = false
        limitedDistance = 0f
    }


    override fun success() {
        animationHideMain()
    }

}


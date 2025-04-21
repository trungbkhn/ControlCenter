package com.tapbi.spark.controlcenter.feature.controlcenter.view.control.samsung

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.media.AudioManager
import android.os.Build
import android.provider.Settings
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.data.model.ItemControlSamSung
import com.tapbi.spark.controlcenter.data.repository.ThemeHelper
import com.tapbi.spark.controlcenter.feature.controlcenter.adapter.AdapterSettingsSamSungControl
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CloseMiControlView
import com.tapbi.spark.controlcenter.feature.controlcenter.view.control.micenter.view.BaseItemRecyclerView
import com.tapbi.spark.controlcenter.feature.controlcenter.view.control.pixel.view.ViewPagePixel
import com.tapbi.spark.controlcenter.feature.controlcenter.view.control.samsung.view.CommonSamSungBinding
import com.tapbi.spark.controlcenter.feature.controlcenter.view.control.samsung.view.SamSungLandscapeBinding
import com.tapbi.spark.controlcenter.feature.controlcenter.view.control.samsung.view.SamSungPortraitBinding
import com.tapbi.spark.controlcenter.feature.controlios14.helper.BlurBackground
import com.tapbi.spark.controlcenter.feature.controlios14.manager.AudioManagerUtils
import com.tapbi.spark.controlcenter.feature.controlios14.model.InfoSystem
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.ControlCenterIOSView
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614
import com.tapbi.spark.controlcenter.ui.base.BaseConstraintLayout
import com.tapbi.spark.controlcenter.utils.DensityUtils
import com.tapbi.spark.controlcenter.utils.MethodUtils
import com.tapbi.spark.controlcenter.utils.SettingUtils
import com.tapbi.spark.controlcenter.utils.TinyDB
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper
import timber.log.Timber
import java.lang.reflect.Type


class ControlSamSung : BaseConstraintLayout, BaseConstraintLayout.CallBackIntent,
    CloseMiControlView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var limitedDistance = 0f
    private var initialY = 0f
    private val maxDistance = 500f // Khoảng cách tối đa để làm mờ
    private var isDragging = false // Để kiểm tra xem người dùng có đang kéo không
    var binding: CommonSamSungBinding? = null
    private var onControlCenterListener: ControlCenterIOSView.OnControlCenterListener? = null

    fun setOnControlCenterListener(onControlCenterListener: ControlCenterIOSView.OnControlCenterListener) {
        this.onControlCenterListener = onControlCenterListener
        viewPager?.setOnControlCenterListener(onControlCenterListener)
        adapterControl1?.setListener { onControlCenterListener.onClose() }
        adapterControl2?.setListener { onControlCenterListener.onClose() }
    }

    private var adapterControl1: AdapterSettingsSamSungControl? = null
    private var adapterControl2: AdapterSettingsSamSungControl? = null
    private var viewPager: ViewPagePixel? = null

    private val listType: Type = object : TypeToken<List<InfoSystem>>() {}.type

    private var infoSystems: MutableList<InfoSystem> = mutableListOf()

    private var maxBrightness = 255

    private var maxVolume = 0

    private var orientation = 0

    private var progressChanging = false

    private var itemSamSung: ItemControlSamSung? = null


    init {
        if (ThemeHelper.itemControl.samsung?.backgroundColorSelectControl != null) {
            itemSamSung = ThemeHelper.itemControl.samsung
        }
        infoSystems = Gson().fromJson(
            TinyDB(
                context
            ).getString(Constant.ACTION_Mi_SELECT), listType
        )
        orientation = DensityUtils.getOrientationWindowManager(context)
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding = SamSungPortraitBinding(
                DataBindingUtil.inflate(
                    LayoutInflater.from(context),
                    R.layout.layout_control_samsung,
                    this,
                    true
                )
            )
            initViewPage()
            initBattery()
            viewWormDotsIndicator()
        } else {
            binding = SamSungLandscapeBinding(
                DataBindingUtil.inflate(
                    LayoutInflater.from(context),
                    R.layout.layout_control_samsung_land,
                    this,
                    true
                )
            )
            (binding as SamSungLandscapeBinding).apply {
                itemSamSung?.let {
                    binding.cardView2.radius =
                        MethodUtils.dpToPx(it.cornerBackgroundControl.toFloat())
                            .toFloat()
                }

            }
            initRVControl()
        }
        setCallBackIntent(this)
        setUpBg()
        initSeekBar()
        initCardView()
        initListenerAndTouch()

    }


    private fun viewWormDotsIndicator() {
        (binding as SamSungPortraitBinding).apply {
            itemSamSung?.let {
                binding.wormDotsIndicator.setDotIndicatorColor(Color.parseColor(it.colorDotIndicator))
                binding.wormDotsIndicator.setStrokeDotsIndicatorColor(Color.parseColor(it.colorStrokeDotsIndicator))
            }

            binding.wormDotsIndicator.setViewPager(binding.viewPagerControl)
        }

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
            if (this is SamSungPortraitBinding) {
                binding.imgEdit.setOnClickListener {
                    ViewHelper.preventTwoClick(it, 800)
                    openSplashApp()
                }
                binding.imgSettings.setOnClickListener {
                    ViewHelper.preventTwoClick(it, 800)
                    intentAction(Settings.ACTION_SETTINGS)
                }
            }

        }
    }

    private fun animationHideMain() {
        onControlCenterListener?.onClose()
        binding?.layoutControl?.alpha = 1f
        if (binding is SamSungPortraitBinding) {
            (binding as SamSungPortraitBinding).binding.viewPagerControl.setCurrentItem(0, false)
        }

    }

    private fun resetDragState() {
        isDragging = false
        limitedDistance = 0f
    }

    override fun success() {
        animationHideMain()
    }


    private fun initCardView() {
        binding?.apply {
            itemSamSung?.let {
                cardView.radius =
                    MethodUtils.dpToPx(it.cornerBackgroundControl)
                        .toFloat()
            }

        }
    }


    private fun initRVControl() {
        val (list2, list1) = infoSystems.partition { infoSystems.indexOf(it) <= 5 }

        adapterControl1 = AdapterSettingsSamSungControl().apply {
            setData(list1.toMutableList())
        }
        adapterControl2 = AdapterSettingsSamSungControl().apply {
            setData(list2.toMutableList())
        }

        (binding as? SamSungLandscapeBinding)?.apply {
            binding.receyViewControl.adapter = adapterControl1
            binding.receyViewControl2.adapter = adapterControl2
        }

    }

    private fun initViewPage() {
        val (list1, list2) = infoSystems.partition { infoSystem ->
            infoSystems.indexOf(infoSystem) <= 11
        }
        if (viewPager == null) {
            viewPager = ViewPagePixel()
        }
        viewPager?.setListInfoSystem(list1, list2)
        (binding as SamSungPortraitBinding).binding.viewPagerControl.adapter = viewPager
    }

    fun setUpBg() {
        val blurBackground = BlurBackground.getInstance() ?: return
        val nonBlurBitmap = blurBackground.bitmapBgNotBlur
        val blurBitmap = blurBackground.bitmapBgBlur

        binding?.apply {
            nonBlurBitmap?.let { imgBg.setImageBitmap(it) }
        }

        if (blurBitmap != null) {
            when (binding) {
                is SamSungPortraitBinding -> {
                    setBitmapBlur((binding as SamSungPortraitBinding).binding.bgBlur, blurBitmap)
                }

                is SamSungLandscapeBinding -> {
                    (binding as SamSungLandscapeBinding).apply {
                        post {
                            val screenWidth = App.widthHeightScreenCurrent.w
                            val screenHeight = App.widthHeightScreenCurrent.h
                            val bitmapWidth = blurBitmap.width
                            val bitmapHeight = blurBitmap.height

                            val leftHalfBitmap = Bitmap.createBitmap(
                                blurBitmap,
                                0,
                                0,
                                minOf(screenWidth / 2, bitmapWidth / 2),
                                minOf(screenHeight, bitmapHeight)
                            )

                            val rightHalfBitmap = Bitmap.createBitmap(
                                blurBitmap,
                                minOf(screenWidth / 2, bitmapWidth / 2),
                                0,
                                minOf(screenWidth / 2, bitmapWidth / 2),
                                minOf(screenHeight, bitmapHeight)
                            )

                            setBitmapBlur(binding.bgBlur, leftHalfBitmap)
                            setBitmapBlur(binding.bgBlur2, rightHalfBitmap)
                        }
                    }
                }
            }
        }
    }

    private fun setBitmapBlur(im: ImageView, bitmap: Bitmap) {
        im.setImageBitmap(bitmap)
        im.setColorFilter(Color.parseColor("#96FFFFFF"))
    }

    @SuppressLint("SetTextI18n")
    private fun initBattery() {
        if (binding is SamSungPortraitBinding) {
            (binding as SamSungPortraitBinding).apply {
                val battery = NotyControlCenterServicev614.getInstance().battery
                if (battery != null) {
                    setLevelBattery(battery.level)
                    changeImageBattery(battery.isChange)
                    itemSamSung?.let {
                        binding.tvBattery.typeface = Typeface.createFromAsset(
                            context.assets, Constant.FOLDER_FONT_CONTROL_ASSETS +
                                    ThemeHelper.itemControl.font
                        )
                    }

                    binding.tvBattery.text = "${battery.pct.toInt()}%"
                }
            }

        }


    }

    private fun initSeekBar() {
        maxBrightness = SettingUtils.getMaxBrightness(context)
        maxVolume = AudioManagerUtils.getInstance(context).maxVolume
        Timber.e("hachung maxBrightness: $maxBrightness ")
        binding?.apply {
            val colorSeekbar = Color.parseColor(itemSamSung?.colorSeekbar)
            setProgressColor(seekBarVolume, colorSeekbar)
            seekBarVolume.max = maxVolume
            seekBarBrightness.max = maxBrightness
            setProgressVolume(AudioManagerUtils.getInstance(context).volume)
            setProgressBrightness()
            setProgressColor(seekBarBrightness, colorSeekbar)

            seekBarBrightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.System.canWrite(
                            context
                        )
                    ) {
                        if (fromUser) {
                            SettingUtils.setValueBrightness(context, progress)
                        }
                    } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        if (fromUser) {
                            SettingUtils.setValueBrightness(context, progress)
                        }
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    onStartTouch()
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {

                }

            })

            seekBarVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        AudioManagerUtils.getInstance(context).volume = progress
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    progressChanging = true
                    onStartTouch()
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    progressChanging = false
                    if (!BaseItemRecyclerView.isPressDarkmode) {
                        AudioManagerUtils.getInstance(context)
                            .changeVolumeInForeground(
                                context,
                                AudioManager.STREAM_MUSIC,
                                seekBar.progress
                            )
                    }
                }

            })

        }

    }


    private fun setProgressColor(seekBar: SeekBar, progressColor: Int) {
        val progressDrawable = seekBar.progressDrawable as LayerDrawable
        val progressLayer = progressDrawable.findDrawableByLayerId(android.R.id.progress)

        if (progressLayer is GradientDrawable) {
            progressLayer.setColor(progressColor)
        } else if (progressLayer is ClipDrawable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val innerDrawable = progressLayer.drawable
                if (innerDrawable is GradientDrawable) {
                    innerDrawable.setColor(progressColor)
                }
            }

        }
        val thumbDrawable = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setSize(MethodUtils.dpToPx(20f), MethodUtils.dpToPx(20f))
            setColor(progressColor) // Màu background của thumb
            setStroke(MethodUtils.dpToPx(4f), Color.WHITE) // Màu và kích thước của stroke
        }
        seekBar.thumb = thumbDrawable
    }

    fun setProgressVolume(progress: Int) {
        if (progressChanging) {
            return;
        }
        binding?.apply {
            seekBarVolume.progress = progress
        }

    }


    fun setProgressBrightness() {
        binding?.apply {
            val value = SettingUtils.getValueBrightness(context)
            Timber.e("hachung value: $value")
            seekBarBrightness.progress = value
        }

    }


    @SuppressLint("SetTextI18n")
    fun setBattery(isCharging: Boolean, level: Int, pct: Float) {
        setLevelBattery(level)
        changeImageBattery(isCharging)
        if (binding is SamSungPortraitBinding) {
            (binding as SamSungPortraitBinding).binding.tvBattery.text = "${pct.toInt()}%"
        }

    }

    private fun changeImageBattery(isCharging: Boolean) {
        if (binding is SamSungPortraitBinding) {
            (binding as SamSungPortraitBinding).apply {
                if (isCharging) {
                    binding.imBattery.clearColorFilter()
                    binding.imBattery.setImageResource(R.drawable.ic_battery_green)
                } else if (SettingUtils.isPowerSaveMode(context)) {
                    binding.imBattery.clearColorFilter()
                    binding.imBattery.setImageResource(R.drawable.ic_battery_yellow)
                } else {
                    binding.imBattery.setColorFilter(Color.parseColor(itemSamSung?.colorIconControl))
                    binding.imBattery.setImageResource(R.drawable.ic_battery)
                }
            }
        }

    }

    fun show() {
        binding?.layoutControl?.apply {
            alpha = 0.1f
            animate()
                .alpha(1.0f)
                .setInterpolator(DecelerateInterpolator())
                .setDuration(200)
                .start()
        }
    }

    private fun setLevelBattery(level: Int) {
        if (binding is SamSungPortraitBinding) {
            (binding as SamSungPortraitBinding).apply {
                binding.imBattery.setImageLevel(level)
            }
        }
    }


    fun updateActionView(action: String, b: Boolean) {
        viewPager?.updateActionView(action, b)
        adapterControl1?.updateActionView(action, b)
        adapterControl2?.updateActionView(action, b)
    }

    fun onStartTouch() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(context)) {
                onControlCenterListener?.onClose()
                SettingUtils.intentActivityRequestPermission(
                    context,
                    arrayOf(Manifest.permission.WRITE_SETTINGS)
                )
            }
        }
    }

    override fun close() {
        onControlCenterListener?.onClose()

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewPager?.clearViewList()
    }
}

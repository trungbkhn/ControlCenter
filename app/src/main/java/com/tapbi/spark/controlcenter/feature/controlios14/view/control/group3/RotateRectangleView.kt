package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group3

import android.Manifest
import android.content.Context
import android.content.res.Configuration
import android.database.ContentObserver
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.databinding.LayoutRotateRectangleViewBinding
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlSettingIosModel
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ConstraintLayoutBase
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.callback.OnClickSettingListener
import com.tapbi.spark.controlcenter.utils.SettingUtils
import com.tapbi.spark.controlcenter.utils.VibratorUtils
import java.io.File

class RotateRectangleView : ConstraintLayoutBase {
    private var onClickSettingListener: OnClickSettingListener? = null
    private var onRotateChangeListener: OnRotateChangeListener? = null
    private var autoRotate = 0
    private var context : Context? = null
    private var controlSettingIosModel : ControlSettingIosModel? = null
    private var isSelect : Boolean = false

    private var binding : LayoutRotateRectangleViewBinding? = null

    fun setOnRotateChangeListener(onRotateChangeListener: OnRotateChangeListener?) {
        this.onRotateChangeListener = onRotateChangeListener
    }

    fun setOnClickSettingListener(onClickSettingListener: OnClickSettingListener?) {
        this.onClickSettingListener = onClickSettingListener
    }

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, controlSettingIosModel: ControlSettingIosModel, dataSetupViewControlModel : DataSetupViewControlModel) : super(context){
        this.controlSettingIosModel = controlSettingIosModel
        this.dataSetupViewControlModel = dataSetupViewControlModel
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context)
    }


    private fun initView(context: Context){
        this.context = context
        binding = LayoutRotateRectangleViewBinding.inflate(LayoutInflater.from(context), this, true)
        if (controlSettingIosModel != null){
            changeColorBackground(controlSettingIosModel?.backgroundDefaultColorViewParent, controlSettingIosModel?.backgroundSelectColorViewParent, controlSettingIosModel?.cornerBackgroundViewParent)
            if (controlSettingIosModel!!.iconControl != null && controlSettingIosModel!!.iconControl != Constant.ICON_DEFAULT) {
                var pathIcon =
                    Constant.PATH_ASSET_THEME + dataSetupViewControlModel.idCategory.toString() + "/" + dataSetupViewControlModel.id + "/" + controlSettingIosModel!!.iconControl;
                if (dataSetupViewControlModel.id > 10000) {
                    val file = File(
                        context.filesDir, Constant.FOLDER_THEMES_ASSETS + "/" + dataSetupViewControlModel.idCategory + "/" + dataSetupViewControlModel.id + "/" + controlSettingIosModel!!.iconControl
                    )
                    pathIcon = file.absolutePath
                }
//                Glide.with(context).load(pathIcon).into(binding!!.imgLock)
                loadImage(context, pathIcon, pathIcon.contains(Constant.PATH_ASSET_THEME), R.drawable.ic_rotate_ios, binding!!.imgLock)
            }
        }
        updateRotateState(false)
        binding?.tvRotate?.typeface = dataSetupViewControlModel.typefaceText
        binding?.imgLock?.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.System.canWrite(context)) {
                    onClickSettingListener?.onClick()
                    SettingUtils.intentActivityRequestPermission(
                        context,
                        arrayOf(Manifest.permission.WRITE_SETTINGS)
                    )

                }else{
                    onClickRotate(context)
                }
            }else{
                onClickRotate(context)
            }

        }

    }

    private fun onClickRotate(context: Context) {
        autoRotate = if (autoRotate == 0) {
            1
        } else {
            0
        }
        animationRotation()
        SettingUtils.settingRotate(context)
        onRotateChangeListener?.onRotateChange()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
//        if (isTablet(context)) {
//            val padding = App.ins.dpToPx((width * 0.12).toInt())
//            Timber.e("hachung padding: $padding")
//            binding.rotateLock.setPadding(padding, padding, padding, padding)
//            binding.rotateArrow.setPadding(padding, padding, padding, padding)
//        }


    }

    override fun onTouchDown() {
        super.onTouchDown()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(context)) {
                onClickSettingListener?.onClick()
                SettingUtils.intentActivityRequestPermission(
                    context,
                    arrayOf(Manifest.permission.WRITE_SETTINGS)
                )
                return
            }
        }
        context?.let { onClickRotate(it) }
    }


    override fun setOnLongClickListener(l: OnLongClickListener?) {
        super.setOnLongClickListener(l)
        if (App.tinyDB.getBoolean(
                Constant.VIBRATOR_CONTROL_LONG_CLICK,
                Constant.VALUE_DEFAULT_VIBRATOR
            )
        ) {
            VibratorUtils.getInstance(context).vibrator(VibratorUtils.TIME_DEFAULT)
        }
        SettingUtils.intentChangeDisplay(context)
        onClickSettingListener?.onClick()
    }


    fun destroy() {
        context?.contentResolver?.unregisterContentObserver(contentObserver)
    }

    fun updateRotateState(anim: Boolean) {
        autoRotate = try {
            Settings.System.getInt(
                context!!.contentResolver,
                Settings.System.ACCELEROMETER_ROTATION
            )
        } catch (e: SettingNotFoundException) {
            e.printStackTrace()
            0
        }
//        if (autoRotate == 0) {
//            setBackgroundResource(R.drawable.background_boder_radius_white)
//            binding.rotateLock.setColorFilter(Color.parseColor("#007AFF"));
//            binding.rotateArrow.setColorFilter(Color.parseColor("#007AFF"))
//        } else if (autoRotate == 1) {
//            setBackgroundResource(R.drawable.background_boder_radius_gray)
//            binding.rotateLock.clearColorFilter()
//            binding.rotateArrow.clearColorFilter()
//        }
        isSelect = autoRotate == 0
        changeIsSelect(isSelect)
        initColorIcon()
    }

    fun initColorIcon() {
        if (controlSettingIosModel != null) {
            if (isSelect) {
                controlSettingIosModel?.let {
                    binding?.imgLock?.setColorFilter(Color.parseColor(it.colorSelectIcon))
                    binding?.tvRotate?.setTextColor(Color.parseColor(it.colorTextTitleSelect))
                }?: run {
                    binding?.imgLock?.setColorFilter(null)
                }

            } else {
                controlSettingIosModel?.let {
                    binding?.imgLock?.setColorFilter(Color.parseColor(it.colorDefaultIcon))
                    binding?.tvRotate?.setTextColor(Color.parseColor(it.colorTextTitle))
                }?: run {
                    binding?.imgLock?.setColorFilter(null)
                }
            }
        }
    }

    private fun animationRotation() {
        changeIsSelect(autoRotate == 0)
        initColorIcon()
        if (autoRotate == 0) {
//            setBackgroundResource(R.drawable.background_boder_radius_white)
//            binding.rotateLock.setColorFilter(Color.parseColor("#007AFF"));
//            binding.rotateArrow.setColorFilter(Color.parseColor("#007AFF"))
//            binding.rotateLock.startAnimation(
//                AnimationUtils.loadAnimation(
//                    context,
//                    R.anim.anim_rotate_lock_on
//                )
//            )
//            binding.rotateArrow.startAnimation(
//                AnimationUtils.loadAnimation(
//                    context,
//                    R.anim.anim_rotate_lock_on
//                )
//            )
        } else if (autoRotate == 1) {
//            setBackgroundResource(R.drawable.background_boder_radius_gray)
//            binding.rotateLock.clearColorFilter()
//            binding.rotateArrow.clearColorFilter()
//            binding.rotateLock.startAnimation(
//                AnimationUtils.loadAnimation(
//                    context,
//                    R.anim.anim_rotate_lock_off
//                )
//            )
//            binding.rotateArrow.startAnimation(
//                AnimationUtils.loadAnimation(
//                    context,
//                    R.anim.anim_rotate_arrow_off
//                )
//            )
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        context!!.contentResolver.registerContentObserver(
            Settings.System.getUriFor(Settings.System.ACCELEROMETER_ROTATION),
            true, contentObserver
        )
    }



    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        destroy()
    }

    interface OnRotateChangeListener {
        fun onRotateChange()
    }



    private val contentObserver: ContentObserver =
        object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                updateRotateState(true)
                onRotateChangeListener?.onRotateChange()
            }
        }
}
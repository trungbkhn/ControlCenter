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
import android.view.LayoutInflater
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.databinding.LayoutRotateBinding
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlSettingIosModel
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.RelativeLayoutBase
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.callback.OnClickSettingListener
import com.tapbi.spark.controlcenter.utils.SettingUtils
import com.tapbi.spark.controlcenter.utils.VibratorUtils
import java.io.File

class RotateView : RelativeLayoutBase {
    private var onClickSettingListener: OnClickSettingListener? = null
    private var onRotateChangeListener: OnRotateChangeListener? = null
    private var autoRotate = 0


    private var binding = LayoutRotateBinding.inflate(LayoutInflater.from(context), this, true)
    fun setOnRotateChangeListener(onRotateChangeListener: OnRotateChangeListener?) {
        this.onRotateChangeListener = onRotateChangeListener
    }

    fun setOnClickSettingListener(onClickSettingListener: OnClickSettingListener?) {
        this.onClickSettingListener = onClickSettingListener
    }

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, controlSettingIosModel: ControlSettingIosModel, dataSetupViewControlModel: DataSetupViewControlModel) : super(context){
        this.controlSettingIosModel = controlSettingIosModel
        this.dataSetupViewControlModel = dataSetupViewControlModel
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {

    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    private fun initView(){
        updateRotateState(false)
        if (controlSettingIosModel != null){
            if (controlSettingIosModel.iconControl != null && controlSettingIosModel.iconControl != Constant.ICON_DEFAULT) {
                var pathIcon = Constant.PATH_ASSET_THEME + dataSetupViewControlModel.idCategory.toString() + "/" + dataSetupViewControlModel.id + "/" + controlSettingIosModel.iconControl;
                if (dataSetupViewControlModel.id > 10000) {
                    val file = File(
                        context.filesDir, Constant.FOLDER_THEMES_ASSETS + "/" + dataSetupViewControlModel.idCategory + "/" + dataSetupViewControlModel.id + "/" + controlSettingIosModel.iconControl
                    )
                    pathIcon = file.absolutePath
                }
                Glide.with(context).load(pathIcon).placeholder(R.drawable.ic_rotate_ios).into(binding.rotateLock)
            }
        }
        initColorIcon()
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val paddingIcon: Int = (width * 0.267).toInt()
        binding.rotateLock.setPadding(paddingIcon,paddingIcon,paddingIcon,paddingIcon)
        binding.rotateLock.requestLayout()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
//        Timber.e("hachung onLayout w: $width h: $height")
//        if (isTablet(context)) {
//            val padding = App.ins.dpToPx((width * 0.12).toInt())
//            Timber.e("hachung padding: $padding")
//            binding.rotateLock.setPadding(padding, padding, padding, padding)
////            binding.rotateArrow.setPadding(padding, padding, padding, padding)
//        }else {
//            val paddingIcon: Int = (width * 0.267).toInt()
//            setPadding(paddingIcon, paddingIcon, paddingIcon, paddingIcon)
//        binding.rotateLock.setPadding(paddingIcon,paddingIcon,paddingIcon,paddingIcon)
//        binding.rotateLock.requestLayout()
//        }

    }

    public fun getImageIcon() : ImageView {
        return binding.rotateLock
    }

    override fun click() {
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
        autoRotate = if (autoRotate == 0) {
            1
        } else {
            0
        }
        animationRotation()
        SettingUtils.settingRotate(context)
        onRotateChangeListener?.onRotateChange()
    }

    override fun onLongClick() {
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

    override fun onDown() {
        animationDown()
    }

    override fun onUp() {
        animationUp()
    }


    fun destroy() {
        context?.contentResolver?.unregisterContentObserver(contentObserver)
    }

    override fun changeData(controlSettingIosModel: ControlSettingIosModel?) {
        super.changeData(controlSettingIosModel)
        updateRotateState(false)
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
        changeIsSelect(autoRotate == 0)
        initColorIcon()
    }

    override fun initColorIcon() {
        super.initColorIcon()
        if (controlSettingIosModel != null) {

            if (isSelect) {
                if (controlSettingIosModel.colorSelectIcon != null) {
                    binding.rotateLock.setColorFilter(Color.parseColor(controlSettingIosModel.colorSelectIcon))
//                    binding.rotateArrow.setColorFilter(Color.parseColor(controlSettingIosModel.colorSelectIcon))
                } else {
                    binding.rotateLock.setColorFilter(null)
//                    binding.rotateArrow.setColorFilter(null)
                }
            } else {
                if (controlSettingIosModel.colorDefaultIcon != null) {
                    binding.rotateLock.setColorFilter(Color.parseColor(controlSettingIosModel.colorDefaultIcon))
//                    binding.rotateArrow.setColorFilter(Color.parseColor(controlSettingIosModel.colorDefaultIcon))
                } else {
                    binding.rotateLock.setColorFilter(null)
//                    binding.rotateArrow.setColorFilter(null)
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

    fun isTablet(context: Context): Boolean {
        val configuration = context.resources.configuration
        return if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            configuration.screenWidthDp > 840
        } else {
            configuration.screenWidthDp > 600
        }
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

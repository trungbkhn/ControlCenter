package com.tapbi.spark.controlcenter.ui.main.customcontrol.color

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.tapbi.spark.controlcenter.App.Companion.tinyDB
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.common.Constant.EVENT_CHANGE_BACKGROUND
import com.tapbi.spark.controlcenter.common.Constant.ID_STORE_WALLPAPER_SELECTED_CUSTOM_DEFAULT
import com.tapbi.spark.controlcenter.databinding.ActivityColorBinding
import com.tapbi.spark.controlcenter.eventbus.EventCustomControls
import com.tapbi.spark.controlcenter.eventbus.EventTickStoreWallpaper
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614
import com.tapbi.spark.controlcenter.ui.base.BaseBindingFragment
import com.tapbi.spark.controlcenter.ui.dialog.DialogRequestPermissionWriteSetting
import com.tapbi.spark.controlcenter.ui.main.customcontrol.CustomizeControlFragment
import com.tapbi.spark.controlcenter.ui.main.customcontrol.color.storewallpaper.DialogWallpaperFragment
import com.tapbi.spark.controlcenter.utils.MethodUtils
import com.tapbi.spark.controlcenter.utils.PermissionUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber

class ColorFragment : BaseBindingFragment<ActivityColorBinding, ColorViewModel>(),
    View.OnClickListener {
    private var resultRealTimeLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            setDrawableClick(Constant.REAL_TIME)
            val i = Intent(context, NotyControlCenterServicev614::class.java).putExtra(
                NotyControlCenterServicev614.EXTRA_ACTION,
                NotyControlCenterServicev614.ACTION_CAPTURE
            ).putExtra(NotyControlCenterServicev614.EXTRA_RESULT_CODE, result.resultCode)
                .putExtra(NotyControlCenterServicev614.EXTRA_RESULT_INTENT, result.data)
            context?.startService(i)
        }
    }
    private var dialogPermissionAlertDialog: DialogRequestPermissionWriteSetting? = null


    private var dialogWallpaperFragment: DialogWallpaperFragment? = null

    //Instead of onActivityResult() method use this one
    private var mPermissionResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result: Boolean ->
            if (result) {
                setBackGroundCurrent()
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val b =
                        shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    if (context != null && !b) {
                        if (dialogPermissionAlertDialog == null) {
                            dialogPermissionAlertDialog =
                                MethodUtils.showDialogPermission(context, true, "", false, null)
                            Timber.e("NVQ DialogRequestPermissionWriteSetting :$this")
                        }
                        if (dialogPermissionAlertDialog?.dialog?.isShowing != true) {
                            dialogPermissionAlertDialog?.show(
                                childFragmentManager,
                                Constant.DIALOG_REQUEST_PERMISSION_WRITE_SETTING
                            )
                        }
                    } else {
                        toastText(R.string.text_detail_when_permission_writer_denied)
                    }
                } else {
                    toastText(R.string.text_detail_when_permission_writer_denied)
                }
            }
        }

    override fun getViewModel(): Class<ColorViewModel> {
        return ColorViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.activity_color

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        initView()
        onObserve()
    }

    private fun onObserve() {

    }

    private fun initView() {
        (parentFragment as? CustomizeControlFragment)?.let {
            setDrawableClick(it.theme.typeBackground,it.isEdit)
        }


        setClick()
    }

    private fun setClick() {
        binding.bgDefault.setOnClickListener(this)
        binding.bgTransparent.setOnClickListener(this)
        binding.bgCurrent.setOnClickListener(this)
        binding.bgScreenBlur.setOnClickListener(this)
        binding.tvStoreWallpaper.setOnClickListener(this)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setDrawableClick(value: String,isEdit:Boolean=false) {
        binding.imgCheckDefault.visibility = View.INVISIBLE
        binding.imgCheckTransparent.visibility = View.INVISIBLE
        binding.imgCheckBgCurrent.visibility = View.INVISIBLE
        binding.imgCheckScreenBlur.visibility = View.INVISIBLE
        binding.imgCheckStoreWallpaper.visibility = View.INVISIBLE
        when (value) {
            Constant.DEFAULT -> binding.imgCheckDefault.visibility = View.VISIBLE

            Constant.TRANSPARENT -> binding.imgCheckTransparent.visibility = View.VISIBLE

            Constant.CURRENT_BACKGROUND -> binding.imgCheckBgCurrent.visibility = View.VISIBLE

            Constant.REAL_TIME -> binding.imgCheckScreenBlur.visibility = View.VISIBLE

            Constant.STORE_WALLPAPER -> binding.imgCheckStoreWallpaper.visibility = View.VISIBLE

        }
        if (!isEdit){
            EventBus.getDefault().post(EventCustomControls(EVENT_CHANGE_BACKGROUND, value))
        }

    }

    private fun setTickDrawables(view: TextView?) {
        view?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checkbox_check_background, 0)
    }

    private fun updateBg() {
        // background chỉ sử dụng trong màn customs nên cm lại
//        if (NotyControlCenterServicev614.getInstance() != null) {
//            NotyControlCenterServicev614.getInstance().updateBg()
//        }

        //
    }

    private fun changeStyleDarkLight() {
//        if (NotyControlCenterServicev614.getInstance() != null) {
//            NotyControlCenterServicev614.getInstance().invalidateControl()
//            if (tinyDB.getInt(
//                    Constant.BACKGROUND_SELECTED_CUSTOM,
//                    Constant.DEFAULT
//                ) == Constant.STORE_WALLPAPER
//            ) {
//                updateBg()
//            }
//        }
    }

    @Subscribe
    fun onEventTickStoreWallpaper(event: EventTickStoreWallpaper) {
        if (event.isTick) {
            setDrawableClick(Constant.STORE_WALLPAPER)
            event.isTick = false
        }
    }

    override fun onResume() {
        super.onResume()
        if (PermissionUtils.checkPermissionWriteExternalStorage()) {
            hideDialog()
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

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onEvent(event: MessageEvent?) {
//        if (event != null && event.typeEvent == Constant.CHANGE_DARK_MODE) {
//            setUpValueCheckBox(
//                tinyDB.getInt(
//                    Constant.STYLE_SELECTED,
//                    Constant.LIGHT
//                ) == Constant.LIGHT
//            )
//        }
//    }


    override fun onDestroyView() {
        super.onDestroyView()
        hideDialog()
    }

    override fun onPause() {
        super.onPause()
        dismissDialogWallpaper()
    }

    private fun dismissDialogWallpaper() {
        if (dialogWallpaperFragment?.isAdded == true && dialogWallpaperFragment?.isStateSaved == false) {
            dialogWallpaperFragment?.dismiss()
        }
    }

    @SuppressLint("NonConstantResourceId")
    override fun onClick(v: View) {
        v.clearFocus()
        when (v.id) {
            R.id.bgDefault -> {
                setDrawableClick(Constant.DEFAULT)
            }

            R.id.bgTransparent -> {
                tinyDB.putInt(
                    Constant.ID_STORE_WALLPAPER_CUSTOM_SELECTED,
                    ID_STORE_WALLPAPER_SELECTED_CUSTOM_DEFAULT
                )
                setDrawableClick(Constant.TRANSPARENT)
            }

            R.id.bgCurrent -> {
                if (activity == null) {
                    return
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && ActivityCompat.checkSelfPermission(
                        requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    mPermissionResult.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                } else {
                    setBackGroundCurrent()
                }
            }

            R.id.bgScreenBlur -> {
                setDrawableClick(Constant.REAL_TIME)
            }

            R.id.tvStoreWallpaper -> {
                dialogWallpaperFragment =
                    DialogWallpaperFragment(object :
                        DialogWallpaperFragment.IListenerWallpaper {
                        override fun onLoadAdsNative(frameLayout: FrameLayout) {

                        }

                        override fun onClickWallpaper(path: String) {

                        }

                    }, (parentFragment as CustomizeControlFragment).idBackgroundStore)
                dialogWallpaperFragment?.let {
                    if (!it.isAdded && !it.isVisible) {
                        it.showDialog(
                            childFragmentManager,
                            "dialogWallpaperFragment"
                        )
                    }
                }
            }
        }
    }


    private fun setBackGroundCurrent() {
//        tinyDB.putInt(
//            Constant.ID_STORE_WALLPAPER_CUSTOM_SELECTED,
//            ID_STORE_WALLPAPER_SELECTED_CUSTOM_DEFAULT
//        )
        setDrawableClick(Constant.CURRENT_BACKGROUND)
        updateBg()
    }

//    private fun setUpValueCheckBox(valueCheckBox: Boolean) {
//        if (valueCheckBox) {
//            binding.cbLight.isChecked = true
//            binding.cbDark.isChecked = false
//        } else {
//            binding.cbLight.isChecked = false
//            binding.cbDark.isChecked = true
//        }
//    }


    override fun onPermissionGranted() {}
    private fun hideDialog() {
        if (dialogPermissionAlertDialog != null && dialogPermissionAlertDialog?.dialog?.isShowing == true) {
            dialogPermissionAlertDialog?.dismissAllowingStateLoss()
        }
    }


}

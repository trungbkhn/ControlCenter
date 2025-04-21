package com.tapbi.spark.controlcenter.ui.dialog.dialogchoosecontrol

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.adapter.ChooseControlAdapter
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.common.models.CustomizeControlApp
import com.tapbi.spark.controlcenter.data.repository.ThemeHelper.Companion.itemControl
import com.tapbi.spark.controlcenter.databinding.LayoutChooseControlBinding
import com.tapbi.spark.controlcenter.feature.controlios14.model.ControlCustomize
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlCenterIosModel
import com.tapbi.spark.controlcenter.ui.base.BaseBottomSheetDialogFragment
import com.tapbi.spark.controlcenter.utils.safeDelay
import timber.log.Timber

class BottomSheetChooseControl : BaseBottomSheetDialogFragment(){
    lateinit var binding: LayoutChooseControlBinding
    lateinit var viewModel: ChooseControlViewModel
    private val listCustomizeCurrentApp = ArrayList<ControlCustomize>()
    private val listExceptCurrentApp = ArrayList<ControlCustomize>()
    private var controlAdapter : ChooseControlAdapter? = null
    private var listItem : ArrayList<ControlCenterIosModel> = ArrayList()
    private var typeface: Typeface? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutChooseControlBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ChooseControlViewModel::class.java]
        initView()
    }

    private fun initView() {
        try {
            typeface = if ((itemControl.font != null && !itemControl.font.isEmpty() && itemControl.font != "font_default")) Typeface.createFromAsset(
                    App.ins.assets, Constant.PATH_FOLDER_FONT + itemControl.font
                ) else null
        } catch (e: Exception) {
        }
        initListControl()
        initAdapter()
        onObserve()
    }

    private fun initAdapter() {
        if (controlAdapter == null){
            controlAdapter = ChooseControlAdapter()
            val layoutManager = GridLayoutManager(context,4)
            binding.rclControl1.layoutManager = layoutManager
            binding.rclControl1.adapter = controlAdapter
        }
    }

    private fun onObserve() {
         viewModel.customizeControlAppLiveData.observe(viewLifecycleOwner) { listApp: CustomizeControlApp ->
             listCustomizeCurrentApp.clear()
             listExceptCurrentApp.clear()
             listExceptCurrentApp.addAll(listApp.listCustomizeCurrentApp)
             listExceptCurrentApp.addAll(listApp.listExceptCurrentApp)
             Timber.e("NVQ listCustomizeCurrentApp ${listExceptCurrentApp.size}")
             controlAdapter?.setData(listExceptCurrentApp)
             safeDelay(200){
                 binding.rclControl1.post {
                     binding.layoutProgress.visibility = View.GONE
                 }
             }

         }
    }

    private fun initListControl(){
//        val listControlSettings = listControl1
//            ?.mapNotNull {
//                if ((it.ratioWidght == 4 && it.ratioHeight == 4) || it.keyControl == Constant.KEY_CONTROL_SCREEN_TIME_OUT) {
//                    it.keyControl
//                } else null
//            } ?: emptyList()
//        viewModel.getListAppForCustomize(
//            context,
//            listControlSettings.toTypedArray()
//        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }
}
package com.tapbi.spark.controlcenter.feature.mishade.view

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RelativeLayout
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.databinding.LayoutWaveMiShadeBinding
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614
import com.tapbi.spark.controlcenter.utils.SettingUtils

class WaveViewMiShade : RelativeLayout {


    private var listSim: MutableList<String> = ArrayList()
    var binding = LayoutWaveMiShadeBinding.inflate(LayoutInflater.from(context), this, true)

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

    private fun init(ctx: Context) {

        setData()
    }


    private fun setData() {
        updateSim()
        if (NotyControlCenterServicev614.getInstance() != null) {
            val lever = NotyControlCenterServicev614.getInstance().leverSim
            setViewSignalSim(lever, binding.imgWave1, binding.imgWave2)
        }
    }

    private fun updateSim() {
        listSim = ArrayList()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            listSim = SettingUtils.getNetworkOperator(context)
        } else {
            listSim.add(SettingUtils.getGSM(context))
        }

        val hasSim = listSim.any { it.isNotEmpty() }
        binding.tvNoSim.visibility = if (hasSim) GONE else VISIBLE
        if (hasSim) {
            binding.imgWave1.visibility = VISIBLE
            binding.imgWave2.visibility = if (listSim.size == 2) VISIBLE else GONE
        } else {
            binding.imgWave1.visibility = GONE
            binding.imgWave2.visibility = GONE
        }

    }

    fun setTextTypeFace(typeFace: String) {
        binding.tvNoSim.typeface =
            Typeface.createFromAsset(context.assets, Constant.FOLDER_FONT_CONTROL_ASSETS + typeFace)
    }

    fun onSignalsChange(lever: Int) {
        setViewSignalSim(lever, binding.imgWave1, binding.imgWave2)
    }

    fun updateStateSim() {
        updateSim()
    }


    private fun setViewSignalSim(level: Int, vararg imageViews: ImageView) {
        val waveImages = arrayOf(
            R.drawable.wave_0,
            R.drawable.wave_1,
            R.drawable.wave_2,
            R.drawable.wave_3,
            R.drawable.wave_4
        )
        val imageRes = waveImages.getOrNull(level) ?: R.drawable.wave_0
        imageViews.forEach {
            it.setImageResource(imageRes)
        }
    }


}
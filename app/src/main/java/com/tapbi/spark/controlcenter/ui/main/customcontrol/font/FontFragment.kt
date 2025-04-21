package com.tapbi.spark.controlcenter.ui.main.customcontrol.font

import android.os.Bundle
import android.view.View
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.adapter.FontControlsAdapter
import com.tapbi.spark.controlcenter.common.Constant.EVENT_CHANGE_FONT
import com.tapbi.spark.controlcenter.databinding.FragmentFontBinding
import com.tapbi.spark.controlcenter.eventbus.EventCustomControls
import com.tapbi.spark.controlcenter.ui.base.BaseBindingFragment
import com.tapbi.spark.controlcenter.ui.main.customcontrol.CustomizeControlFragment
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

class FontFragment : BaseBindingFragment<FragmentFontBinding, FontViewModel>() {
    private var fontControlsAdapter: FontControlsAdapter? = null
    override fun getViewModel(): Class<FontViewModel> {
        return FontViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_font

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        initAdapter()
        observerData()
    }

    private var listFont = mutableListOf<String>()
        set(value) {
            field = value
            fontControlsAdapter?.setListFont(value)
            (parentFragment as CustomizeControlFragment).theme.let { theme ->
                listFont.indexOfFirst { it == theme.font }.let { index ->
                    fontControlsAdapter?.setSelect(index, theme.font)
                }
            }
        }

    private fun observerData() {
        viewModel.getListFontAssets()
        viewModel.liveDataListFont.observe(viewLifecycleOwner) {
            listFont = it
        }
    }

    private fun initAdapter() {
        fontControlsAdapter = FontControlsAdapter()
        fontControlsAdapter?.setClickListener(object : FontControlsAdapter.ClickListener {
            override fun onChooseFont(fontName: String) {
                EventBus.getDefault().post(EventCustomControls(EVENT_CHANGE_FONT, fontName))
            }
        })
        binding.rcvFont.animation = null
        binding.rcvFont.itemAnimator = null
        binding.rcvFont.adapter = fontControlsAdapter
    }

    override fun onPermissionGranted() {

    }

}
package com.tapbi.spark.controlcenter.ui.choosemusic

import android.os.Bundle
import android.view.View
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.adapter.MusicPlayerAdapter
import com.tapbi.spark.controlcenter.databinding.ActivityChooseMusicPlayerBinding
import com.tapbi.spark.controlcenter.feature.controlios14.model.MusicPlayer
import com.tapbi.spark.controlcenter.ui.base.BaseBindingActivity
import com.tapbi.spark.controlcenter.utils.Analytics
import com.tapbi.spark.controlcenter.utils.helper.rcvhepler.NpaLinearLayoutManager

class ChooseMusicPlayerActivity :
    BaseBindingActivity<ActivityChooseMusicPlayerBinding, ChooseMusicPlayerViewModel>() {
    private var adapter: MusicPlayerAdapter? = null
    private var musicPlayers = ArrayList<MusicPlayer>()
    override val layoutId: Int
        get() = R.layout.activity_choose_music_player

    override fun getViewModel(): Class<ChooseMusicPlayerViewModel> {
        return ChooseMusicPlayerViewModel::class.java
    }

    override fun setupView(savedInstanceState: Bundle?) {
        setUpPaddingStatusBar()
        setColorNavigation(R.color.text_detail_splash)
        initAdapter()
        evenClick()

    }

    private fun evenClick() {
        binding.viewHeader.imBack.setOnClickListener { _: View? -> onBackPressed() }
    }

    private fun initAdapter() {
        adapter = MusicPlayerAdapter(this, musicPlayers)
        binding.listMusicPlayer.adapter = adapter
    }


    override fun setupData() {
        binding.bgLoading.visibility = View.VISIBLE
        viewModel.getListAppMusicPlayer(this)
        viewModel.listAppMusicPlayerLiveData.observe(this) {
            if (it != null) {
                this.musicPlayers = it
                adapter?.setData(musicPlayers)
                binding.bgLoading.visibility = View.GONE
            }

        }
    }

    private fun setUpPaddingStatusBar() {
        binding.layoutParent.setPadding(0, App.statusBarHeight, 0, 0)
    }

    override fun onResume() {
        super.onResume()
        if (Analytics.getInstance() == null) {
            return
        }
        Analytics.getInstance().setCurrentScreen(this, javaClass.simpleName)
    }
}

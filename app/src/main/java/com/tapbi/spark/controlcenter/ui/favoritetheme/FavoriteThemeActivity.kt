package com.tapbi.spark.controlcenter.ui.favoritetheme

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.ironman.trueads.common.Common
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.adapter.FavoriteAdapter
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.data.local.SharedPreferenceHelper
import com.tapbi.spark.controlcenter.data.model.ThemeControlFavorite
import com.tapbi.spark.controlcenter.databinding.ActivityFavoriteThemeBinding
import com.tapbi.spark.controlcenter.ui.base.BaseBindingActivity
import com.tapbi.spark.controlcenter.ui.main.MainActivity
import com.tapbi.spark.controlcenter.utils.AppUtils
import com.tapbi.spark.controlcenter.utils.setState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoriteThemeActivity : BaseBindingActivity<ActivityFavoriteThemeBinding, FavoriteViewModel>(){
    private var favoriteAdapter: FavoriteAdapter? = null
    private var isToastShowing = false
    override val layoutId: Int
        get() = R.layout.activity_favorite_theme

    override fun getViewModel(): Class<FavoriteViewModel> {
        return FavoriteViewModel::class.java
    }

    override fun setupView(savedInstanceState: Bundle?) {
        observer()
        initAdapter()
        initView()
    }
    private fun initAdapter() {
        favoriteAdapter = FavoriteAdapter()
        favoriteAdapter?.clickListener = object : FavoriteAdapter.ClickListener {
            override fun onClickItem(item: ThemeControlFavorite, position: Int) {
                try {
                    val holder = binding.rcvFavorite.findViewHolderForAdapterPosition(position)
                    if (holder is FavoriteAdapter.FavoriteViewHolder) {
                        holder.setStateSelect(item.isSelect)
                    }
                } catch (ignore: Exception) {
                }
            }

            override fun setStateBtnNext(isEnable: Boolean) {
                if (isEnable){
                    binding.icDone.setColorFilter(ContextCompat.getColor(this@FavoriteThemeActivity, R.color.color_undo))
                } else {
                    binding.icDone.setColorFilter(ContextCompat.getColor(this@FavoriteThemeActivity, R.color._9D9D9D))
                }
                binding.icDone.setState(isEnable)
            }

            override fun onFullSelect() {
                if (isToastShowing) {
                    return
                }
                val toast = Toast.makeText(
                    App.ins,
                    App.ins.getString(R.string.you_can_only_select_a_maximum_of_3_themes),
                    Toast.LENGTH_SHORT
                )
                toast.show()
                isToastShowing = true
                AppUtils.safeDelay(if (toast.duration == Toast.LENGTH_SHORT) 2000L else 3500L) {
                    isToastShowing = false
                }
            }
        }
        val layoutManager = GridLayoutManager(this, 3)
        binding.rcvFavorite.layoutManager = layoutManager
        binding.rcvFavorite.adapter = favoriteAdapter
    }
    private fun observer() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finish()
                }
            })
        viewModel.listThemesFavorite.observe(this) {
            if (it != null) {
                favoriteAdapter?.setListFavorites(it)
                viewModel.listThemesFavorite.postValue(null)
            }
        }
    }
    private fun logSelectedThemesEvent() {
        favoriteAdapter?.let {
            val selectedThemes = it.getSelectedThemes()
            val bundle = Bundle().apply {
                putString(Constant.FAVORITE_THEMES, selectedThemes.joinToString(","))
            }
            Firebase.analytics.logEvent(Constant.FAVORITE_THEMES, bundle)
        }
    }
    private fun initView() {
        viewModel.getListThemesFavorite(this)
        binding.icDone.setOnClickListener { v ->
            CoroutineScope(Dispatchers.IO).launch {
                logSelectedThemesEvent()
                SharedPreferenceHelper.storeBoolean(Constant.IS_FAVORITE_SELECTED, true)
            }
            SharedPreferenceHelper.storeBoolean(Constant.IS_FAVORITE_SELECTED,true)
            val intent = Intent(this, MainActivity::class.java);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
        loadAdsNative(binding.flAds, Common.getMapIdAdmobApplovin(
            this,
            R.array.admob_native_id_favorite_theme,
            R.array.applovin_id_native_favorite_theme
        ) )
    }

    override fun setupData() {

    }
}
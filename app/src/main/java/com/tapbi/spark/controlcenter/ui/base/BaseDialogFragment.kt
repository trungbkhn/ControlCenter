package com.tapbi.spark.controlcenter.ui.base

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.ironman.trueads.multiads.InterstitialAdsLiteListener
import com.ironman.trueads.multiads.MultiAdsControl
import com.tapbi.spark.controlcenter.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(R.color.transparent)
        return dialog
    }

    protected open fun showAdsFull(positionAds: String) {
        MultiAdsControl.showInterstitialLite(
            requireActivity(),
            positionAds,
            false,
            object : InterstitialAdsLiteListener {


                override fun onInterstitialAdsNextScreen(adsType: Int) {
                    nextAfterFullScreen()
                }

                override fun onInterstitialAdsShowFully(adsType: Int) {

                }

                override fun onPrepareShowInterstitialAds(adsType: Int) {

                }

            })
    }


    protected open fun nextAfterFullScreen() {
    }


}
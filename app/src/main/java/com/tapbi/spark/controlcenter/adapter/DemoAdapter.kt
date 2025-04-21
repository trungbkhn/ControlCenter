package com.tapbi.spark.controlcenter.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.databinding.ItemDemoBinding
import com.tapbi.spark.controlcenter.feature.base.CustomImageView
import com.tapbi.spark.controlcenter.feature.controlcenter.view.control.oppo.view.SettingsViewOppo
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group2.ControlMusicIosView
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group2.MusicExpandView
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group3.RotateView
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group3.ScreenTimeoutView
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group3.SilentView
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group4.SettingBrightnessView
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group4.SettingVolumeTextView
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group4.SettingVolumeView

class DemoAdapter : RecyclerView.Adapter<DemoAdapter.DemoViewHolder>() {

    inner class DemoViewHolder(val binding: ItemDemoBinding) :
        RecyclerView.ViewHolder(binding.root)

    var list = mutableListOf(
        "Settings",
        "Rotate",
        "Silent",
        "Screen Time Out",
        "",
        "",
        "Volume",
        "",
        "",
        "Brightness",
        "",
        "",
        "",
        "",
        "Music",
        "MusicExpand",
        "MusicControl"
    )

    var listener: IClickItem? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DemoViewHolder {
        return DemoViewHolder(
            ItemDemoBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        var type = 0
        if (list[position] == "Settings" || list[position] == "MusicExpand" || list[position] == "MusicControl") {
            type = 1
        } else if (list[position] == "Brightness" || list[position] == "Volume" || list[position] == "Screen Time Out") {
            type = 2
        } else if (list[position] == "Music") {
            type = 3
        }
        return type
    }

    override fun onBindViewHolder(holder: DemoViewHolder, position: Int) {
        val view = getView(list[position], holder.binding.fl.context)
        view.setBackgroundResource(R.drawable.background_boder_radius_gray)
        holder.binding.fl.addView(view)


    }

    private fun getView(text: String, context: Context): View {
        when (text) {
            "Settings" -> {
                val settingView = SettingsViewOppo(context)
                return settingView
            }

            "Rotate" -> {
                return RotateView(context)
            }

            "Silent" -> {
                return SilentView(context)
            }

            "Screen Time Out" -> {
                return ScreenTimeoutView(context)
            }

            "Volume" -> {
                return SettingVolumeView(context)
            }

            "Brightness" -> {
                return SettingBrightnessView(context)
            }

            "Music" -> {
//                return MusicView(context)
                return SettingVolumeTextView(context)
            }

            "MusicExpand" -> {
                return MusicExpandView(context)
            }

            "MusicControl" -> {
                return ControlMusicIosView(context)
            }

            else -> {
                return CustomImageView(context)
            }


        }
    }

    interface IClickItem {
        fun onLongClick()
    }


}
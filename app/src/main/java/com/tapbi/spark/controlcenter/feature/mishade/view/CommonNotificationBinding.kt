package com.tapbi.spark.controlcenter.feature.mishade.view

import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.tapbi.spark.controlcenter.databinding.FragmentNotificationOneBinding
import com.tapbi.spark.controlcenter.databinding.FragmentNotificationOneLandBinding

abstract class CommonNotificationBinding {
    abstract val rccTop: RecyclerView
    abstract val rccBot: RecyclerView
    abstract val rccCenter: RecyclerView
}

class NotificationPortraitBinding(
    private val binding: FragmentNotificationOneBinding
) : CommonNotificationBinding() {

    override val rccTop: RecyclerView get() = binding.rccTop
    override val rccBot: RecyclerView get() = binding.rccBot
    override val rccCenter: RecyclerView get() = binding.rccCenter
}

class NotificationLandscapeBinding(
    private val binding: FragmentNotificationOneLandBinding
) : CommonNotificationBinding() {
    override val rccTop: RecyclerView get() = binding.rccTop
    override val rccBot: RecyclerView get() = binding.rccBot
    override val rccCenter: RecyclerView get() = binding.rccCenter
}

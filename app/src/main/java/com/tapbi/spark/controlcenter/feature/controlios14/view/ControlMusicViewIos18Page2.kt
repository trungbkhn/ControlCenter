package com.tapbi.spark.controlcenter.feature.controlios14.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.AudioManager
import android.media.session.PlaybackState
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.databinding.LayoutControlIosPage2Binding
import com.tapbi.spark.controlcenter.feature.controlios14.manager.AudioManagerUtils
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ImageBase.OnAnimationListener
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.callback.OnClickSettingListener
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group2.MusicView.OnMusicViewListener
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group4.CustomSeekbarHorizontalView
import com.tapbi.spark.controlcenter.receiver.HHeadsetReceiver
import com.tapbi.spark.controlcenter.utils.DensityUtils
import com.tapbi.spark.controlcenter.utils.MediaUtils
import com.tapbi.spark.controlcenter.utils.MethodUtils
import com.tapbi.spark.controlcenter.utils.Utils.milliSecondsToTimer
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.CompletableObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber

class ControlMusicViewIos18Page2 :  ConstraintLayout {
    var binding : LayoutControlIosPage2Binding? = null

    var onClickSettingListener: OnClickSettingListener? = null
    var onMusicViewListener: OnMusicViewListener? = null
    private var controlMusicUtils: MediaUtils? = null


    private var downTimer: CountDownTimer? = null
    private var durationMusic: Long = 1
    private var currentTimeMusic: Long = 1


    private val handler = Handler(Looper.getMainLooper())

    private var isCheck = false

    private val runnablePlayPause = Runnable { isCheck = false }
    private var maxVolume = 0
    private var currentVolume = 0f

    constructor(context: Context,controlMusicUtils: MediaUtils?) : super(context){
        this.controlMusicUtils = controlMusicUtils
        initView()
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){initView()}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){initView()}

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes){initView()}


    @SuppressLint("ClickableViewAccessibility")
    private fun initView(){
        try {
            val orientation2 = DensityUtils.getOrientationWindowManager(getContext())
            context.resources.configuration.orientation = orientation2
            val configuration = context.resources.configuration
            configuration.orientation = orientation2
            val themedContext = context.createConfigurationContext(configuration)

            binding = LayoutControlIosPage2Binding.inflate(LayoutInflater.from(themedContext),this,true);
        } catch (e :Exception){
            Timber.e("NVQ 123456")
        }
        maxVolume = AudioManagerUtils.getInstance(context).maxVolume
        descendantFocusability = FOCUS_BLOCK_DESCENDANTS

        binding?.controlTimeMusic?.changeIsPermission(true)
        binding?.controlTimeMusic?.changeIsTouch(false)
        binding?.controlTimeMusic?.changeColor("#26FFFFFF", "#A6FFFFFF", "#00000000", 0.5f)
        binding?.controlVolume?.changeIsPermission(true)
        binding?.controlVolume?.changeIsTouch(true)
        binding?.controlVolume?.changeColor("#26FFFFFF", "#A6FFFFFF", "#00000000", 0.5f)
        binding?.controlVolume?.setOnCustomSeekbarHorizontalListener(object : CustomSeekbarHorizontalView.OnCustomSeekbarHorizontalListener{
            override fun onStartTrackingTouch(horizontalSeekBar: CustomSeekbarHorizontalView?) {
                parent.requestDisallowInterceptTouchEvent(true)
            }

            override fun onProgressChanged(
                horizontalSeekBar: CustomSeekbarHorizontalView?,
                i: Float
            ) {
                currentVolume = i
                val volume = (i * maxVolume).toInt()
                AudioManagerUtils.getInstance(context).volume = volume
            }

            override fun onStopTrackingTouch(horizontalSeekBar: CustomSeekbarHorizontalView?) {
                val volume = Math.round((currentVolume * maxVolume) as Float)
                AudioManagerUtils.getInstance(context)
                    .changeVolumeInForeground(context, AudioManager.STREAM_MUSIC, volume)
                parent.requestDisallowInterceptTouchEvent(false)
            }

            override fun onLongPress(horizontalSeekBar: CustomSeekbarHorizontalView?) {}
        })
        binding?.previousAction?.setOnAnimationListener(object : OnAnimationListener {
            override fun onDown() {
                onMusicViewListener?.onDown()
            }

            override fun onUp() {
                onMusicViewListener?.onUp()
            }

            override fun onClick() {
                if (controlMusicUtils != null) {
                    controlMusicUtils!!.controlMusic(AudioManagerUtils.PREVIOUS)
                }
            }

            override fun onLongClick() {
                if (onMusicViewListener != null) {
                    onMusicViewListener?.onLongClick()
                }
            }

            override fun onClose() {
            }
        })
        binding?.playPauseAction?.setOnAnimationListener(object : OnAnimationListener {
            override fun onDown() {
                onMusicViewListener?.onDown()
            }

            override fun onUp() {
                onMusicViewListener?.onUp()
            }

            override fun onClick() {
                if (!isCheck) {
                    isCheck = true
                    if (controlMusicUtils != null) {
                        controlMusicUtils!!.controlMusic(AudioManagerUtils.PLAYPAUSE)
                    }
                    handler.postDelayed(runnablePlayPause, 1000)
                }
            }

            override fun onLongClick() {
                if (onMusicViewListener != null) {
                    onMusicViewListener?.onLongClick()
                }
            }

            override fun onClose() {
            }
        })
        binding?.nextAction?.setOnAnimationListener(object : OnAnimationListener {
            override fun onDown() {
                onMusicViewListener?.onDown()
            }

            override fun onUp() {
                onMusicViewListener?.onUp()
            }

            override fun onClick() {
                if (controlMusicUtils != null) {
                    controlMusicUtils!!.controlMusic(AudioManagerUtils.NEXT)
                }
            }

            override fun onLongClick() {
                if (onMusicViewListener != null) {
                    onMusicViewListener?.onLongClick()
                }
            }

            override fun onClose() {
            }
        })
        binding?.cvPlayerIcon?.setOnClickListener {
            ViewHelper.preventTwoClick(it)
            if (controlMusicUtils != null) {
                val p = controlMusicUtils!!.openAppMusic()
                if (p) {
                    onClickSettingListener?.onClick()
                }
            }
        }
        binding?.cvPlayerIcon?.setOnLongClickListener {
            if (onMusicViewListener != null) {
                onMusicViewListener?.onLongClick()
            }
            false
        }
        binding?.tvHeadphone?.text = App.ins.getString(R.string.speakerphone)
        binding?.tvHeadphone?.setCompoundDrawablesRelativeWithIntrinsicBounds(
            ContextCompat.getDrawable(context, R.drawable.phone_small),
            null,
            null,
            null
        )
        binding?.tvTimeCurrent?.text = "0:00"
        updateVolume()
        binding?.ctlPage2?.setOnClickListener {
            Timber.e("NVQ ctlClick")
        }
    }
    fun updateVolume(){
        val progress: Float = AudioManagerUtils.getInstance(context).volume.toFloat() / maxVolume.toFloat()
        binding?.controlVolume?.setCurrentProgress(progress)
    }


     fun stateChange(state: Int) {
        Timber.e("NVQ stateChange+++++++ $state")
        if (state == PlaybackState.STATE_PLAYING) {
            binding?.playPauseAction?.setImageResource(R.drawable.pause)
        } else {
            binding?.playPauseAction?.setImageResource(R.drawable.play)
        }
        updateDuration(state)
    }
    private fun updateDuration(state: Int) {
        if (controlMusicUtils != null) {
            val infoTimeMedia = controlMusicUtils!!.infoTimeMedia
            if (controlMusicUtils!!.mediaController != null) {
                val playbackState = controlMusicUtils!!.mediaController.playbackState
                if (playbackState != null) {
                    infoTimeMedia.currentPosition = playbackState.position
                }
            }
            if (downTimer != null) {
                downTimer?.cancel()
            }
            val millisInFuture = infoTimeMedia.duration - infoTimeMedia.currentPosition
            if (state == PlaybackState.STATE_PLAYING) {
                downTimer = object : CountDownTimer(millisInFuture, 1000) {
                    override fun onTick(l: Long) {
                        val currentTime = millisInFuture - l + infoTimeMedia.currentPosition
                        updateViewSeekbarPosition(currentTime)
                    }

                    override fun onFinish() {
                        updateViewSeekbarPosition(infoTimeMedia.duration)
                    }
                }.start()
            }
            val duration = milliSecondsToTimer(infoTimeMedia.duration)
            Timber.e("NVQ milliSecondsToTimer $duration")
            binding?.tvDuration?.text = duration
            setViewSeekbarPosition(infoTimeMedia.currentPosition, infoTimeMedia.duration)
        }
    }
    private fun setViewSeekbarPosition(position: Long, duration: Long) {
        durationMusic = (duration / 1000)
        updateViewSeekbarPosition(position)
    }
    private fun updateViewSeekbarPosition(position: Long) {
        binding?.let {
            if (binding?.tvDuration?.text != "0:00") {
                val duration = milliSecondsToTimer(position)
                Timber.e("NVQ milliSecondsToTimer $duration")
                binding?.tvTimeCurrent!!.text = duration
                val oldCurrent = currentTimeMusic
                currentTimeMusic = position / 1000
                binding?.controlTimeMusic!!.setCurrentProgress(currentTimeMusic.toFloat() / durationMusic.toFloat())
            } else {
                binding?.tvTimeCurrent!!.text = "0:00"
            }
        }
    }

     fun contentChange(
        artist: String?,
        track: String?,
        thumb: Bitmap?,
        packageName: String?
    ) {
        binding?.tvName?.text = track
        binding?.tvArtist?.text = artist
        if (packageName == null || packageName.isEmpty()) {
            setDrawableDefault()
            binding?.tvTimeCurrent!!.text = "0:00"
            binding?.tvDuration!!.text = "0:00"
            durationMusic = 1
            currentTimeMusic = 0
            binding?.controlTimeMusic!!.setCurrentProgress(0f)
        } else {
            if (thumb != null && !thumb.isRecycled) {
                binding?.cvPlayerIcon?.setImageBitmap(thumb)
            } else {
                Completable.fromRunnable {
                    val drawable = MethodUtils.getIconFromPackageNameMusic(context, packageName)
                    binding?.cvPlayerIcon?.setImageBitmap(drawable.bitmap)
                }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : CompletableObserver {
                        override fun onSubscribe(d: Disposable) {}

                        override fun onComplete() {
                        }

                        override fun onError(e: Throwable) {
                        }
                    })
            }
        }
        binding?.tvName?.isSelected = true
        binding?.tvArtist?.isSelected = true
    }
    private fun setDrawableDefault() {
        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_music)
        if (drawable != null) {
            binding?.cvPlayerIcon?.setImageBitmap((drawable as BitmapDrawable).bitmap)
        }
    }
     fun checkPermissionNotificationListener(isCheck: Boolean) {}

     fun timeMediaChange(state: Int) {
        updateDuration(state)
    }

    fun onHeadsetConnected() {
        binding?.tvHeadphone?.text = App.ins.getString(R.string.headphones)
        binding?.tvHeadphone?.setCompoundDrawablesRelativeWithIntrinsicBounds(
            ContextCompat.getDrawable(context, R.drawable.headphones_small),
            null,
            null,
            null
        )

    }

    fun onHeadsetDisconnected() {
        binding?.tvHeadphone?.text = App.ins.getString(R.string.speakerphone)
        binding?.tvHeadphone?.setCompoundDrawablesRelativeWithIntrinsicBounds(
            ContextCompat.getDrawable(context, R.drawable.phone_small),
            null,
            null,
            null
        )
    }
//    private var isVisibleToUser = false
//
//    fun setVisibleToUser(visible: Boolean) {
//        isVisibleToUser = visible
//        if (!visible) {
//            descendantFocusability = FOCUS_BLOCK_DESCENDANTS
//        } else {
//            descendantFocusability = FOCUS_AFTER_DESCENDANTS
//        }
//    }
//
//    override fun requestLayout() {
//        if (isVisibleToUser) {
//            super.requestLayout()
//        }
//    }

}
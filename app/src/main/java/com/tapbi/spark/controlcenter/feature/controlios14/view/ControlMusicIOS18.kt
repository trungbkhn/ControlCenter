package com.tapbi.spark.controlcenter.feature.controlios14.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.media.session.PlaybackState
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.databinding.LayoutControlMusicIos18Binding
import com.tapbi.spark.controlcenter.feature.controlios14.manager.AudioManagerUtils
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlMusicIosModel
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ConstraintLayoutBase
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ImageBase.OnAnimationListener
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.callback.OnClickSettingListener
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group2.MusicView.OnMusicViewListener
import com.tapbi.spark.controlcenter.utils.MediaUtils
import com.tapbi.spark.controlcenter.utils.MethodUtils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.CompletableObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber

class ControlMusicIOS18 : ConstraintLayoutBase {
    var binding : LayoutControlMusicIos18Binding? = null
    private var isCheckNoty = false
    private var controlMusicIOS: ControlMusicIosModel? = null

    private var onClickSettingListener: OnClickSettingListener? = null
    private var onMusicViewListener: OnMusicViewListener? = null
    private var controlMusicUtils: MediaUtils? = null


    private val handler = Handler(Looper.getMainLooper())

    private var isCheck = false

    private val runnablePlayPause = Runnable { isCheck = false }

    constructor(context: Context, ControlMusicIosModel: ControlMusicIosModel?, dataSetupViewControlModel: DataSetupViewControlModel?
                ,controlMusicUtils :MediaUtils) : super(context){
        this.controlMusicIOS = ControlMusicIosModel
        this.dataSetupViewControlModel = dataSetupViewControlModel
        this.controlMusicUtils = controlMusicUtils
        init(context)
    }
    constructor(context: Context?) : super(context){
        context?.let { init(it) }
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        context?.let { init(it) }
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        context?.let { init(it) }
    }

    fun changeControlMusicIOS(
        controlMusicIOS: ControlMusicIosModel?,
        dataSetupViewControlModel: DataSetupViewControlModel?
    ) {
        this.controlMusicIOS = controlMusicIOS
        this.dataSetupViewControlModel = dataSetupViewControlModel
        initView()
    }

    private fun init(ctx: Context) {
        val layoutInflater = LayoutInflater.from(context)
        binding = LayoutControlMusicIos18Binding.inflate(layoutInflater,this,true)
        setDrawableDefault()
        initView()
        binding?.tvName?.isSelected = true
        binding?.tvArtist?.isSelected = true

        binding?.background?.setOnAnimationListener(object : OnAnimationListener {
            override fun onDown() {
                onMusicViewListener?.onDown()
                animationDown()
            }

            override fun onUp() {
                onMusicViewListener?.onUp()
                animationUp()
            }

            override fun onClick() {
            }

            override fun onLongClick() {
                if (onMusicViewListener != null && isCheckNoty) {
                    onMusicViewListener?.onLongClick(this@ControlMusicIOS18)
                }
            }

            override fun onClose() {
            }
        })
        binding?.previousAction?.setOnAnimationListener(object : OnAnimationListener {
            override fun onDown() {
                onMusicViewListener?.onDown()
//                animationDown()
            }

            override fun onUp() {
                onMusicViewListener?.onUp()
//                animationUp()
            }

            override fun onClick() {
                controlMusicUtils?.controlMusic(AudioManagerUtils.PREVIOUS)
            }

            override fun onLongClick() {
                if (onMusicViewListener != null && isCheckNoty) {
                    onMusicViewListener?.onLongClick()
                }
            }

            override fun onClose() {
            }
        })

        binding?.playPauseAction?.setOnAnimationListener(object : OnAnimationListener {
            override fun onDown() {
                onMusicViewListener?.onDown()
//                animationDown()
            }

            override fun onUp() {
                onMusicViewListener?.onUp()
//                animationUp()
            }

            override fun onClick() {
                if (!isCheck) {
                    isCheck = true
                    controlMusicUtils?.controlMusic(AudioManagerUtils.PLAYPAUSE)
                    handler.postDelayed(runnablePlayPause, 1000)
                }
            }

            override fun onLongClick() {
                if (onMusicViewListener != null && isCheckNoty) {
                    onMusicViewListener?.onLongClick()
                }
            }

            override fun onClose() {
            }
        })

        binding?.nextAction?.setOnAnimationListener(object : OnAnimationListener {
            override fun onDown() {
                onMusicViewListener?.onDown()
//                animationDown()
            }

            override fun onUp() {
                onMusicViewListener?.onUp()
//                animationUp()
            }

            override fun onClick() {
                controlMusicUtils?.controlMusic(AudioManagerUtils.NEXT)
            }

            override fun onLongClick() {
                if (onMusicViewListener != null && isCheckNoty) {
                    onMusicViewListener?.onLongClick()
                }
            }

            override fun onClose() {
            }
        })

        binding?.cvPlayerIcon?.setOnAnimationListener(object : OnAnimationListener {
            override fun onDown() {
                onMusicViewListener?.onDown()
                animationDown()
            }

            override fun onUp() {
                onMusicViewListener?.onUp()
                animationUp()
            }

            override fun onClick() {
                if (controlMusicUtils != null) {
                    val p = controlMusicUtils?.openAppMusic()
                    if (p == true) {
                        onClickSettingListener?.onClick()
                    }
                }
            }

            override fun onLongClick() {
                if (onMusicViewListener != null && isCheckNoty) {
                    onMusicViewListener?.onLongClick()
                }
            }

            override fun onClose() {
            }
        })
    }
    private fun setDrawableDefault() {
        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_music)
        if (drawable != null) {
            binding?.cvPlayerIcon?.setSrc((drawable as BitmapDrawable).bitmap)
        }
    }
    fun setOnMusicViewListener(onMusicViewListener: OnMusicViewListener?) {
        this.onMusicViewListener = onMusicViewListener
    }
    private fun initView() {
        Timber.e("NVQ 11111122 ${controlMusicIOS == null} // ${dataSetupViewControlModel == null}")
        if (controlMusicIOS != null) {
            changeColorBackground(
                controlMusicIOS?.backgroundDefaultColorViewParent,
                controlMusicIOS?.backgroundSelectColorViewParent,
                controlMusicIOS?.cornerBackgroundViewParent
            )
            binding?.cvPlayerIcon?.setCornerBackground(controlMusicIOS?.cornerImageAvatarMusic)
            binding?.tvName?.setTextColor(Color.parseColor(controlMusicIOS?.colorTextName))
            binding?.tvName?.typeface = dataSetupViewControlModel.typefaceText
            binding?.tvArtist?.setTextColor(Color.parseColor(controlMusicIOS?.colorTextArtists))
            binding?.tvArtist?.typeface = dataSetupViewControlModel.typefaceText
            binding?.nextAction?.setColorFilter(Color.parseColor(controlMusicIOS?.colorIcon))
            binding?.playPauseAction?.setColorFilter(Color.parseColor(controlMusicIOS?.colorIcon))
            binding?.previousAction?.setColorFilter(Color.parseColor(controlMusicIOS?.colorIcon))
            binding?.imgHeadphone?.setImageResource(R.drawable.ic_phone)
        }
    }
    fun setOnClickSettingListener(onClickSettingListener: OnClickSettingListener?) {
        this.onClickSettingListener = onClickSettingListener
    }

     fun stateChange(state: Int) {
        Timber.e("NVQ stateChange+++++++ $state")
        if (state == PlaybackState.STATE_PLAYING) {
            binding?.playPauseAction?.setImageResource(R.drawable.pause)
        } else {
            binding?.playPauseAction?.setImageResource(R.drawable.play)
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
        } else {
            if (thumb != null && !thumb.isRecycled) {
                binding?.cvPlayerIcon?.setSrc(thumb)
            } else {
                Completable.fromRunnable {
                    val drawable = MethodUtils.getIconFromPackageNameMusic(context, packageName)
                    binding?.cvPlayerIcon?.setSrc(drawable.bitmap)
                }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : CompletableObserver {
                        override fun onSubscribe(d: Disposable) {
                            compositeDisposable.add(d)
                        }

                        override fun onComplete() {
                        }

                        override fun onError(e: Throwable) {
                        }
                    })
            }
        }
    }
     fun checkPermissionNotificationListener(isCheck: Boolean) {
        checkNotyPermission(isCheck)
    }

     fun timeMediaChange(state: Int) {

    }

    fun checkNotyPermission(isCheck: Boolean) {
        isCheckNoty = isCheck
    }

     fun onHeadsetConnected() {
        Timber.e("hachung onHeadsetConnected:")
        binding?.imgHeadphone?.setImageResource(R.drawable.ic_headphone)
    }

     fun onHeadsetDisconnected() {
        Timber.e("hachung onHeadsetDisconnected:")
        binding?.imgHeadphone?.setImageResource(R.drawable.ic_phone)
    }

}
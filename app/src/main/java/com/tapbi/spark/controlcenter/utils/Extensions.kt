package com.tapbi.spark.controlcenter.utils

import android.graphics.Matrix
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import timber.log.Timber


fun WindowManager.updateLayout(view: View, params : WindowManager.LayoutParams){
    try {
        updateViewLayout(view,params)
    }catch (_:Exception){
        Timber.e("NVQ view not attached to window manager")
    }
}
fun WindowManager.addLayout(view: View, params : WindowManager.LayoutParams){
    try {
        addView(view,params)
    }catch (_:Exception){
        Timber.e("NVQ view attached to window manager")
    }
}
fun WindowManager.removeLayout(view: View){
    try {
        removeView(view)
    }catch (_:Exception){
        Timber.e("NVQ view not attached to window manager")
    }
}
fun safeDelay(delayMillis: Long = 0, action: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed({
        try {
            action()
        } catch (e: java.lang.Exception) {
            Timber.e("safeDelay: $e")
        }
    }, delayMillis)
}
fun View.show(){
    visibility = View.VISIBLE
}
fun View.hide(){
    visibility = View.GONE
}
fun View.invisible(){
    visibility = View.INVISIBLE
}
fun View.setState(state:Boolean){
    if (state){
        isClickable = true
        isEnabled = true
        alpha = 1f
    }else{
        isClickable = false
        isEnabled = false
        alpha = 0.7f
    }
}
fun View.isShow() = visibility == View.VISIBLE

fun View.isGone() = visibility == View.GONE

fun View.isInvisible() = visibility == View.INVISIBLE


fun View.gone() {
    visibility = View.GONE
}

fun View.inv() {
    visibility = View.INVISIBLE
}

fun View.show(isShow: Boolean, visibilityState: Int = View.GONE) {
    visibility = if (isShow) View.VISIBLE else visibilityState
}

fun View.disable() {
    isEnabled = false
    isClickable = false
    alpha = 0.5f
}

fun View.enable() {
    isEnabled = true
    isClickable = true
    alpha = 1f
}
fun AppCompatImageView.setCenterBottomCrop() {
    val drawable = drawable ?: return
    val matrix = Matrix()

    val viewWidth = width.toFloat()
    val viewHeight = height.toFloat()
    val drawableWidth = drawable.intrinsicWidth.toFloat()
    val drawableHeight = drawable.intrinsicHeight.toFloat()

    val scale = maxOf(viewWidth / drawableWidth, viewHeight / drawableHeight)
    val dx = (viewWidth - drawableWidth * scale) * 0.5f
    val dy = viewHeight - drawableHeight * scale

    matrix.setScale(scale, scale)
    matrix.postTranslate(dx, dy)

    this.imageMatrix = matrix
}
fun ConstraintLayout.LayoutParams.clearAllConstraints() {
    topToTop = ConstraintLayout.LayoutParams.UNSET
    topToBottom = ConstraintLayout.LayoutParams.UNSET
    bottomToTop = ConstraintLayout.LayoutParams.UNSET
    bottomToBottom = ConstraintLayout.LayoutParams.UNSET
    startToStart = ConstraintLayout.LayoutParams.UNSET
    startToEnd = ConstraintLayout.LayoutParams.UNSET
    endToStart = ConstraintLayout.LayoutParams.UNSET
    endToEnd = ConstraintLayout.LayoutParams.UNSET
}


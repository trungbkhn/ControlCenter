package com.tapbi.spark.controlcenter.utils

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import com.google.gson.Gson
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.common.Constant.FONT_FOLDER
import com.tapbi.spark.controlcenter.common.Constant.FONT_ROBOTO_REGULAR
import com.tapbi.spark.controlcenter.common.Constant.ICON_SHADE_FOLDER
import com.tapbi.spark.controlcenter.common.Constant.KEY_CONTROL_ADD
import com.tapbi.spark.controlcenter.data.model.GroupColor
import com.tapbi.spark.controlcenter.data.model.ItemControl
import com.tapbi.spark.controlcenter.data.model.ItemOnboarding
import com.tapbi.spark.controlcenter.data.model.ThemeControl
import com.tapbi.spark.controlcenter.data.repository.ThemeHelper
import com.tapbi.spark.controlcenter.feature.controlios14.model.ControlCustomize
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlCenterIosModel
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


object Utils {
    fun setBackgroundIcon(context: Context?, img: ImageView, control: ControlCustomize) {
        val action = control.name
        if (control.isDefault != 0) {
            if (action == context?.getString(R.string.flash_light)) {
                img.setBackgroundResource(R.drawable.background_icon_cusomize_control_flash)
            } else if (action == context?.getString(R.string.clock)) {
                img.setBackgroundResource(R.drawable.background_icon_cusomize_control_clock)
            } else if (action == context?.getString(R.string.calculator)) {
                img.setBackgroundResource(R.drawable.background_icon_cusomize_control_calculator)
            } else if (action == context?.getString(R.string.screen_recoding)) {
                img.setBackgroundResource(R.drawable.background_icon_cusomize_control_recoder)
            } else if (action == context?.getString(R.string.dark_mode)) {
                img.setBackgroundResource(R.drawable.background_icon_cusomize_control_dark_mode)
            } else if (action == context?.getString(R.string.low_power_mode)) {
                img.setBackgroundResource(R.drawable.background_icon_cusomize_control_low_power_mode)
            } else if (action == context?.getString(R.string.notes)) {
                img.setBackgroundResource(R.drawable.background_icon_cusomize_control_notes)
            } else {
                img.setBackgroundResource(R.drawable.background_icon_cusomize_control)
            }
        } else {
            img.setBackgroundResource(R.drawable.background_icon_cusomize_control)
        }
    }
    @JvmStatic
    fun isViewVisibleInScrollView(nestedScrollView: NestedScrollView, childView: View): Boolean {
        val scrollBounds = Rect()
        nestedScrollView.getDrawingRect(scrollBounds) // Lấy vùng hiển thị của NestedScrollView

        val childRect = Rect()
        childView.getDrawingRect(childRect)
        nestedScrollView.offsetDescendantRectToMyCoords(childView, childRect) // Chuyển tọa độ về NestedScrollView

        return scrollBounds.intersect(childRect) // Kiểm tra nếu có phần nào của View con nằm trong vùng hiển thị
    }
    @JvmStatic
    fun getLastModel( listControl1: ArrayList<ControlCenterIosModel>) : ControlCenterIosModel {
        return ControlCenterIosModel(listControl1.last())
    }
    @JvmStatic
    fun removeLastModel(listControl1: ArrayList<ControlCenterIosModel>) {
        try {
            if (listControl1.last().keyControl == KEY_CONTROL_ADD){
                listControl1.removeAt(listControl1.lastIndex)
            }
        } catch (e:Exception){ }
    }

    fun setBackgroundTintSelect(context: Context, view: TextView, isSelect: Boolean) {
        if (isSelect) {
            val color = ContextCompat.getColor(context, R.color.color_246BFD)
            view.setTextColor(ContextCompat.getColor(context, R.color.white))
            ViewCompat.setBackgroundTintList(view, ColorStateList.valueOf(color))
            view.setState(true)
        } else {
            val color = ContextCompat.getColor(context, R.color.color_F3F3F7)
            view.setTextColor(ContextCompat.getColor(context, R.color.black50))
            ViewCompat.setBackgroundTintList(view, ColorStateList.valueOf(color))
            view.setState(false)
        }
    }
    @JvmStatic
    fun getIconShow(nameDefault: String?,isIos18 : Boolean = false): Drawable {
        return when (nameDefault) {
            Constant.KEY_CONTROL_FLASH -> ControlCustomizeManager.getIcon(R.drawable.flashlight_off)
            Constant.KEY_CONTROL_ALARM -> ControlCustomizeManager.getIcon(R.drawable.ic_alarm_id_6004)
            Constant.KEY_CONTROL_CALCULATOR -> ControlCustomizeManager.getIcon(R.drawable.ic_calculator_ios)
            Constant.KEY_CONTROL_CAMERA -> ControlCustomizeManager.getIcon(R.drawable.camera)
            Constant.KEY_CONTROL_RECORD -> ControlCustomizeManager.getIcon(R.drawable.record_icon)
            Constant.KEY_CONTROL_DARKMODE -> ControlCustomizeManager.getIcon(R.drawable.ic_darkmode_id_6004)
            Constant.KEY_CONTROL_PIN -> ControlCustomizeManager.getIcon(R.drawable.ic_low_power_mode)
            Constant.KEY_CONTROL_NOTE -> ControlCustomizeManager.getIcon(R.drawable.ic_note)
            Constant.KEY_CONTROL_ROTATE -> ControlCustomizeManager.getIcon(R.drawable.ic_rotate_ios)
            Constant.KEY_CONTROL_SCREEN_TIME_OUT -> ControlCustomizeManager.getIcon(R.drawable.ic_screen_time_out)
            else -> {
                if (isIos18) {
                    ControlCustomizeManager.getIcon(R.drawable.ic_silent_ios)
                } else ControlCustomizeManager.getIcon(R.drawable.ic_silent_id_6004)
            }
        }
    }

    fun isTablet(context: Context): Boolean {
        val configuration = context.resources.configuration
        return if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            configuration.screenWidthDp > 840
        } else {
            configuration.screenWidthDp > 600
        }
    }

    fun initTextContentPermission(context: Context, textView: TextView, idString: Int) {
        val appName = context.getString(R.string.app_name)
        val text = context.getString(idString, appName)
        val spannableString = SpannableString(text)

        val start = text.indexOf(appName)
        val end = start + appName.length

        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(Color.BLACK),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        textView.text = spannableString
    }
@JvmStatic
    fun milliSecondsToTimer(milliseconds: Long): String {
        var finalTimerString = ""
        var secondsString = ""

        val hours = (milliseconds / (1000 * 60 * 60)).toInt()
        val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
        val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()
        // Add hours if there
        if (hours > 0) {
            finalTimerString = "$hours:"
        }

        secondsString = if (seconds < 10) {
            "0$seconds"
        } else {
            "" + seconds
        }
        finalTimerString = "$finalTimerString$minutes:$secondsString"

// return timer string
        return finalTimerString
    }

//    fun getListIconShape() :ArrayList<Int>{
//        val listIconShape = ArrayList<Int>()
//        listIconShape.add(R.drawable.icon_shade_1)
//        listIconShape.add(R.drawable.icon_shade_2)
//        listIconShape.add(R.drawable.icon_shade_3)
//        listIconShape.add(R.drawable.icon_shade_4)
//        listIconShape.add(R.drawable.icon_shade_5)
//        listIconShape.add(R.drawable.icon_shade_6)
//        listIconShape.add(R.drawable.icon_shade_7)
//        listIconShape.add(R.drawable.icon_shade_8)
//        listIconShape.add(R.drawable.icon_shade_9)
//        listIconShape.add(R.drawable.icon_shade_10)
//        listIconShape.add(R.drawable.icon_shade_11)
//        listIconShape.add(R.drawable.icon_shade_12)
//        listIconShape.add(R.drawable.icon_shade_13)
//        listIconShape.add(R.drawable.icon_shade_14)
//        return listIconShape
//    }

    fun getListIconShape(): List<String> {
        val assetManager = App.ins.assets
        return assetManager.list(ICON_SHADE_FOLDER)?.toList() ?: emptyList()
    }

    fun loadImageFromAssetsDrawable(fileName: String): Drawable? {
        try {
            val assetManager = App.ins.assets
            val inputStream = assetManager.open("${ICON_SHADE_FOLDER}/$fileName")
            return Drawable.createFromStream(inputStream, null)
        } catch (e: Exception) {
            return null
        }
    }


    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 1
        val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 1

        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
            val canvas = Canvas(this)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
        }
    }

    @JvmStatic
    fun getScaledTextSize(context: Context, defaultSpSize: Float): Float {
        // Lấy scaledDensity từ display metrics
        val scaledDensity: Float = context.resources.displayMetrics.scaledDensity
        // Tính toán textSize dựa trên scaledDensity
        return defaultSpSize * scaledDensity
    }


    fun loadImageFromAssetsByPath(imagePath: String): Drawable? {
        try {
            val assetManager = App.ins.assets
            val inputStream = assetManager.open(imagePath)
            return Drawable.createFromStream(inputStream, null)
        } catch (e: Exception) {
            return null
        }
    }


    fun getListColorControls(): ArrayList<Int> {
        val colorStrings = listOf(
            "#000000", "#B970E9", "#7860F6", "#5352E4", "#233693", "#5068B7",
            "#458EF7", "#1D1D1D", "#585F6D", "#AAA9AC", "#E0E4E9", "#F6F0DC", "#9C877B",
            "#965635", "#39261F", "#CB4D6E", "#EA3891", "#F69ABA", "#BFD6E8", "#9BE9EB",
            "#80E3C3", "#5CAAA8", "#CFD7C2", "#768F7A", "#7AAE59", "#92DD96", "#6AE58B",
            "#FDF250", "#F6CC6B", "#CEB04E", "#F2A93B", "#E8A989", "#EF865B", "#EB732E",
            "#C53F39", "#ED7470", "#EB565C", "#FF3B30"
        )

        return ArrayList(colorStrings.map { Color.parseColor(it) })
    }

    fun getListGroupColor(): MutableList<GroupColor> {
        val colors = listOf(
            listOf("#03924D", "#2073F8", "#EF8023"),
            listOf("#FDC5F5", "#F7AEF8", "#B388EB"),
            listOf("#FB97AF", "#FF7093", "#34D0D9"),
            listOf("#FDDA4E", "#FECEAB", "#FF857D"),
            listOf("#51A2ED", "#71DE8A", "#00BCAA"),
            listOf("#FFC89D", "#FFA159", "#0062BA"),
            listOf("#CB4D6E", "#EA3891", "#F69ABA"),
            listOf("#F6F0DC", "#9C877B", "#965635")
        )
        return colors.map { colorList ->
            GroupColor(
                Color.parseColor(colorList[0]),
                Color.parseColor(colorList[1]),
                Color.parseColor(colorList[2])
            )
        }.toMutableList()
    }

    fun getColorFromRes(res: Int): Int {
        return ContextCompat.getColor(App.ins, res)
    }

    fun getListFont(): MutableList<String> {
        return App.ins.assets.list(FONT_FOLDER)?.toMutableList()?.apply {
            if (remove(FONT_ROBOTO_REGULAR)) {
                add(0, FONT_ROBOTO_REGULAR)
            }
        } ?: mutableListOf()
    }

    fun getLayoutBitmap(view: ConstraintLayout): Bitmap? {

        // Measure the view to obtain its width and height
        // Measure the view to obtain its width and height
        val width = view.measuredWidth
        val height = view.measuredHeight

        // Create a Bitmap with the measured dimensions

        // Create a Bitmap with the measured dimensions
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // Create a Canvas to draw the view

        // Create a Canvas to draw the view
        val canvas = Canvas(bitmap)

        // Clear the Canvas with a transparent color

        // Clear the Canvas with a transparent color
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        // Draw the view onto the Canvas

        // Draw the view onto the Canvas
        view.draw(canvas)

        return bitmap
    }

    @JvmStatic
    fun <T> replaceItemAt(list: ArrayList<T>, position: Int, newItem: T) {
        if (position in 0 until list.size) {
            list[position] = newItem
        }
    }
    fun saveViewJpegToFile(view: ConstraintLayout): String {
        val storageDir = File(view.context.filesDir, Constant.FOLDER_THUMB_CONTROL)
        var success = true
        if (!storageDir.exists()) {
            success = storageDir.mkdirs()
        }
        return if (success) {
            val imageFile = File(storageDir, "" + System.currentTimeMillis() + ".png")
            if (saveBitmapToFile(
                    getLayoutBitmap(view) ?: return "",
                    imageFile,
                    Bitmap.CompressFormat.PNG
                )
            ) {
                imageFile.path
            } else {
                ""
            }
        } else {
            ""
        }
    }

    fun saveBitmapToFile(bitmap: Bitmap, file: File, format: Bitmap.CompressFormat): Boolean {
        var fOut: OutputStream? = null
        try {
            fOut = FileOutputStream(file)
            bitmap.compress(format, 100, fOut)
            fOut.close()
            return true
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            try {
                fOut?.close()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        return false
    }

    fun getFontByFontName(fontName: String): Typeface? {
        val assetManager = App.ins.assets
        return Typeface.createFromAsset(assetManager, fontName)
    }

    fun setFontForTextView(textView: TextView, fontName: String, context: Context) {
        try {
            val assetManager = context.assets
            val typeface =
                Typeface.createFromAsset(assetManager, "${Constant.FONT_FOLDER}/$fontName")
            textView.typeface = typeface
        } catch (e: Exception) {
            Timber.e("NVQ setFontForTextView $e")
        }
    }

    fun setBackgroundTint(view: View, color: Int) {
        ViewCompat.setBackgroundTintList(view, ColorStateList.valueOf(color))
    }

    fun getAppNameFromPackage(context: Context, packageName: String): String? {
        return try {
            val packageManager = context.packageManager
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(applicationInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    fun setViewMaxHeight(view: View, context: Context) {
        val heightScreen = DensityUtils.getScreenHeight()
        val itemHeight = MethodUtils.dpToPx(context, 40f)
        val maxHeight = (heightScreen / itemHeight).toInt() * itemHeight
        val layoutParams = view.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.matchConstraintMaxHeight = maxHeight
        view.layoutParams = layoutParams

        view.layoutParams = layoutParams

    }

    fun getListItemOnboard(
        context: Context,
        isShowFullAds: Boolean
    ): List<ItemOnboarding> {
        val list = mutableListOf<ItemOnboarding>()
        list.add(
            ItemOnboarding(
                context.getString(R.string.titile_onboard_1),
                context.getString(R.string.content_onboard_1),
                R.drawable.item_onboard_1
            )
        )
        list.add(
            ItemOnboarding(
                context.getString(R.string.titile_onboard_2),
                context.getString(R.string.content_onboard_2), R.drawable.item_onboard_2
            )
        )
        list.add(
            ItemOnboarding(
                context.getString(R.string.titile_onboard_3),
                context.getString(R.string.content_onboard_3),
                R.drawable.item_onboard_3
            )
        )
        if (isShowFullAds) {
            list.add(ItemOnboarding("", "", R.drawable.item_onboard_4, true))
        }
        list.add(
            ItemOnboarding(
                context.getString(R.string.titile_onboard_4),
                context.getString(R.string.content_onboard_4),
                R.drawable.item_onboard_4
            )
        )

        return list
    }
    fun readItemControlFromJson2(context: Context, theme: ThemeControl): ItemControl? {
        val jsonFile = File(context.filesDir, "themes/${theme.idCategory}/${theme.id}/theme.json")
        return if (jsonFile.exists()) {
            jsonFile.readText().let { jsonString ->
                Gson().fromJson(jsonString, ItemControl::class.java)
            }
        } else {
            null
        }
    }


    fun getStringNameIconAssets(nameAction: String) = when (nameAction) {
        Constant.STRING_ACTION_DATA_MOBILE -> "ic_data.png"
        Constant.STRING_ACTION_WIFI -> "ic_wifi.png"
        Constant.STRING_ACTION_BLUETOOTH -> "ic_bluetooth.png"
        Constant.STRING_ACTION_FLASH_LIGHT -> "ic_flash.png"
        Constant.STRING_ACTION_SOUND -> "ic_sound.png"
        Constant.STRING_ACTION_SILENT -> "ic_silent.png"
        Constant.STRING_ACTION_AIRPLANE_MODE -> "ic_air_mode.png"
        Constant.STRING_ACTION_DO_NOT_DISTURB -> "ic_do_not_disturb.png"
        Constant.STRING_ACTION_LOCATION -> "ic_location.png"
        Constant.STRING_ACTION_SCREEN_CAST -> "ic_screen_cast.png"
        Constant.STRING_ACTION_AUTO_ROTATE -> "ic_auto_rotate.png"
        Constant.STRING_ACTION_OPEN_SYSTEM -> "ic_open_system.png"
        Constant.STRING_ACTION_CLOCK -> "ic_alarm.png"
        Constant.STRING_ACTION_SYNC -> "ic_data_saver.png"
        Constant.STRING_ACTION_BATTERY -> "ic_pow_lower.png"
        Constant.STRING_ACTION_VIBRATE -> "ic_vibrate.png"
        Constant.STRING_ACTION_HOST_POST -> "ic_host_post.png"
        else -> "ic_night_light.png"
    }
}
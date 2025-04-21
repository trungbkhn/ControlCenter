package com.tapbi.spark.controlcenter.data.repository

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.gson.Gson
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.common.Constant.LAST_TIME_EDIT_THEME
import com.tapbi.spark.controlcenter.data.db.room.ControlCenterDataBase
import com.tapbi.spark.controlcenter.data.local.SharedPreferenceHelper
import com.tapbi.spark.controlcenter.data.model.ItemControl
import com.tapbi.spark.controlcenter.data.model.ThemeControl
import com.tapbi.spark.controlcenter.data.model.ThemeControlFavorite
import com.tapbi.spark.controlcenter.eventbus.EventSelectThemes
import com.tapbi.spark.controlcenter.feature.controlios14.model.ControlCustomize
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlCenterIosModel
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlSettingIosModel
import com.tapbi.spark.controlcenter.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import javax.inject.Inject

object ThemesRepository{
    @JvmStatic
    var isControlEditing = false // chặn click khi bật trạng thái chỉnh sửa
    //
    fun getSingleTheme(context: Context, theme: ThemeControl): ItemControl {
        val res = loadDataJsonAssets(
            context,
            "${Constant.FOLDER_THEMES_ASSETS}/${theme.idCategory}/${theme.id}/theme.json"
        )
        return Gson().fromJson(res, ItemControl::class.java)
    }


    fun getThemeControlDefault(context: Context, idCategory: Int): ItemControl {
        val id = when (idCategory) {
            Constant.VALUE_CONTROL_CENTER_OS -> 6004
            Constant.VALUE_SHADE -> 1004
            else -> 2001
        }
        val res = loadDataJsonAssets(
            context,
            "${Constant.FOLDER_THEMES_ASSETS}/$idCategory/$id/theme.json"
        )

        return Gson().fromJson(res, ItemControl::class.java)
    }


    fun loadDataJsonAssets(context: Context, inFile: String): String {
        return try {
            val stream = context.assets.open(inFile)
            val size = stream.available()
            val buffer = ByteArray(size)
            stream.read(buffer)
            stream.close()
            String(buffer)
        } catch (e: Exception) {
            ""
        }
    }


    var listAllThemes: MutableList<ThemeControl> = mutableListOf()


    fun getAllListThemes(context: Context): MutableList<ThemeControl> {
        if (listAllThemes.isEmpty()) {
            val inputStream =
                context.assets.open("${Constant.FOLDER_THEMES_ASSETS}/${Constant.FILE_NAME_THEME_ASSETS}")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            val json = String(buffer, StandardCharsets.UTF_8)
            val gson = Gson()
            val objectList = gson.fromJson(json, Array<ThemeControl>::class.java).asList()
            listAllThemes.addAll(objectList)
        }

        return listAllThemes
    }


    fun getAllListThemesFavorite(context: Context): MutableList<ThemeControlFavorite> {
        val inputStream =
            context.assets.open("${Constant.FOLDER_THEMES_ASSETS}/${Constant.FILE_NAME_THEME_ASSETS_FAVORITE}")
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        val json = String(buffer, StandardCharsets.UTF_8)
        val gson = Gson()
        return gson.fromJson(json, Array<ThemeControlFavorite>::class.java).toMutableList()
    }

    fun insertThemeControl(itemTheme: ThemeControl): Long {
        return App.ins.controlCenterDataBase.themeControlDao().insertThemeControl(itemTheme)
    }


    fun getAllThemeControlFlow(): Flow<List<ThemeControl>> {
        return App.ins.controlCenterDataBase.themeControlDao().getAllThemeControlFlow()
    }


    fun deleteThemeControlById(id: Long) {
        App.ins.controlCenterDataBase.themeControlDao().deleteThemeControlById(id)
    }

    fun getItemThemeControlById(id: Long): ThemeControl? {
        return App.ins.controlCenterDataBase.themeControlDao().getItemThemeControlById(id)
    }

    suspend fun deleteThemeControl(itemThemeControl: ThemeControl) {
        App.ins.controlCenterDataBase.themeControlDao().deleteItemThemeControl(itemThemeControl)
    }


    fun deleteThemeData(context: Context, idCategory: Int, id: Long) {
        try {
            val dir = File(context.filesDir, "themes/$idCategory/$id")
            val jsonFile = File(dir, "theme.json")
            if (jsonFile.exists()) {
                jsonFile.delete()
            }

            // Xóa các ảnh
            val imageFile1 = File(dir, "thumb.webp")
            if (imageFile1.exists()) {
                imageFile1.delete()

            }

            val imageFile2 = File(dir, "background.webp")
            if (imageFile2.exists()) {
                imageFile2.delete()
            }
            if (idCategory == Constant.VALUE_CONTROL_CENTER_OS) {
                val imageFile3 = File(dir, "ic_alarm.png")
                val imageFile4 = File(dir, "ic_darkmode.png")
                val imageFile5 = File(dir, "ic_silent.png")
                if (imageFile3.exists()) {
                    imageFile3.delete()
                }
                if (imageFile4.exists()) {
                    imageFile4.delete()
                }
                if (imageFile5.exists()) {
                    imageFile5.delete()
                }
            }
            if (dir.listFiles()?.isEmpty() == true) {
                dir.delete()
                Timber.d("Đã xóa thư mục: ${dir.absolutePath}")
            }
        } catch (i: IOException) {
            Timber.e("hachung i: $i")
        }

    }

    fun readItemControlFromJson(context: Context, theme: ThemeControl): ItemControl? {
        val jsonFile = File(context.filesDir, "themes/${theme.idCategory}/${theme.id}/theme.json")
        return if (jsonFile.exists()) {
            jsonFile.readText().let { jsonString ->
                Gson().fromJson(jsonString, ItemControl::class.java)
            }
        } else {
            null
        }
    }


    suspend fun saveTheme(
        context: Context,
        theme: ItemControl,
        preview: ConstraintLayout,
        isEdit: Boolean = false
    ): Long {
        val idOld = if (!isEdit) theme.id else {
            when (theme.idCategory) {
                Constant.VALUE_CONTROL_CENTER_OS -> 6004
                Constant.VALUE_SHADE -> 1004
                else -> 2001
            }
        }
        if (!isEdit) theme.id = System.currentTimeMillis()
        val dir = File(
            context.filesDir,
            Constant.FOLDER_THEMES_ASSETS + "/${theme.idCategory}/${theme.id}"
        )

        if (dir.exists()) {
            if (isEdit) dir.listFiles()?.forEach { it.deleteRecursively() }
        } else {
            dir.mkdirs()
        }

        // Sử dụng coroutine để chạy song song các tác vụ I/O
        withContext(Dispatchers.IO) {
            // Các task để lưu background, thumb và theme JSON

            val backgroundTask = async {
                val background = File(dir, "background.webp")
                if (theme.typeBackground == Constant.DEFAULT) {
                    saveImageFromAssetsToFile(
                        context.assets,
                        Constant.FOLDER_THEMES_ASSETS + "/" + theme.idCategory + "/" + idOld + "/" + theme.background,
                        background, Bitmap.CompressFormat.WEBP
                    )
                }
            }

            when (theme.idCategory) {
                Constant.VALUE_CONTROL_CENTER_OS -> {
                    saveImageFromAssetsToFile(
                        context.assets,
                        Constant.FOLDER_THEMES_ASSETS + "/" + theme.idCategory + "/" + idOld + "/ic_alarm.png",
                        File(dir, "ic_alarm.png"), Bitmap.CompressFormat.PNG
                    )
                    saveImageFromAssetsToFile(
                        context.assets,
                        Constant.FOLDER_THEMES_ASSETS + "/" + theme.idCategory + "/" + idOld + "/ic_darkmode.png",
                        File(dir, "ic_darkmode.png"), Bitmap.CompressFormat.PNG
                    )
                    saveImageFromAssetsToFile(
                        context.assets,
                        Constant.FOLDER_THEMES_ASSETS + "/" + theme.idCategory + "/" + idOld + "/ic_silent.png",
                        File(dir, "ic_silent.png"), Bitmap.CompressFormat.PNG
                    )
                }

            }
            val thumbTask = async {
                val thumb = File(dir, "thumb.webp")
                Utils.getLayoutBitmap(preview)?.let {
                    Utils.saveBitmapToFile(it, thumb, Bitmap.CompressFormat.WEBP)
                }
            }

            val jsonTask = async {
                // Lưu theme dưới dạng JSON
                val jsonFile = File(dir, "theme.json")
                val gson = Gson()
                val jsonString = gson.toJson(theme)
                jsonFile.writeText(jsonString)
            }

            // Đợi tất cả các tác vụ hoàn thành
            awaitAll(backgroundTask, thumbTask, jsonTask)
        }

        return theme.id
    }


    private fun saveImageFromAssetsToFile(
        assetManager: AssetManager,
        assetPath: String,
        file: File, format: Bitmap.CompressFormat
    ) {
        try {
            assetManager.open(assetPath).use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
                    .compress(format, 100, FileOutputStream(file))
            }
        } catch (e: Exception) {
        }
    }


    fun updateThemesWithCategory6000(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            if (!App.tinyDB.getBoolean(Constant.UPDATE_THEME_6000_VER_1_2, false)) {
                App.tinyDB.putBoolean(Constant.UPDATE_THEME_6000_VER_1_2, true)
                val baseDir = File(context.filesDir, Constant.FOLDER_THEMES_ASSETS)
                if (!baseDir.exists() || !baseDir.isDirectory) return@launch

                val gson = Gson()

                baseDir.listFiles()?.forEach { categoryDir ->
                    categoryDir.listFiles()?.forEach { themeDir ->
                        val jsonFile = File(themeDir, "theme.json")
                        if (!jsonFile.exists()) return@forEach

                        val jsonString = jsonFile.readText()
                        val updatedTheme =
                            gson.fromJson(jsonString, ItemControl::class.java) ?: return@forEach
                        Timber.e("hachung updatedTheme: $updatedTheme")
                        if (updatedTheme.idCategory != 6000) return@forEach

                        updatedTheme.controlCenterOS?.let { controlCenterOS ->
                            Timber.e("hachung controlCenterOS: $controlCenterOS")
                            val listTop =
                                controlCenterOS.listControlCenterStyleVerticalTop.orEmpty()
                            val controlCenterIosModel =
                                listTop.find { it.ratioWidght == 4 && it.ratioHeight == 4 }
                                    ?: return@let

                            // Cập nhật dữ liệu
                            Timber.e("hachung controlCenterIosModel: ${controlCenterIosModel.controlSettingIosModel}")
                            controlCenterOS.controlCenterStyleHorizontal?.apply {
                                updateControlSettingIosModel(listControlLeft, controlCenterIosModel)
                                updateControlSettingIosModel(
                                    listControlRight,
                                    controlCenterIosModel
                                )
                            }

                            // Lọc danh sách App mới
                            val listAppNew = listTop.filter {
                                (it.ratioHeight == 4 && it.ratioWidght == 4) || it.keyControl == Constant.KEY_CONTROL_SCREEN_TIME_OUT
                            }


                            // Cập nhật listControlRight (tránh lỗi ConcurrentModificationException)
                            controlCenterOS.controlCenterStyleHorizontal?.let { controlCenterStyleHorizontal ->
                                val newList =
                                    controlCenterStyleHorizontal.listControlRight?.toMutableList()
                                        ?: mutableListOf()
                                newList.clear() // Xóa toàn bộ phần tử cũ
                                newList.addAll(listAppNew) // Thêm danh sách mới
                                controlCenterStyleHorizontal.listControlRight =
                                    newList // Cập nhật lại danh sách
                            }


                            // Ghi lại file JSON sau khi cập nhật
                            jsonFile.writeText(gson.toJson(updatedTheme))

                            if (App.tinyDB.getLong(
                                    Constant.KEY_ID_CURRENT_APPLY_THEME,
                                    Constant.KEY_ID_CURRENT_APPLY_THEME_DEFAULT
                                ) == updatedTheme.id
                            ) {
                                ThemeHelper.setItemThemeCurrent(updatedTheme)

                            }

                        }
                    }
                }
            }

        }


    }

    private fun updateControlSettingIosModel(
        list: List<ControlCenterIosModel>?,
        controlCenterIosModel: ControlCenterIosModel
    ) {
        if (controlCenterIosModel.controlSettingIosModel != null) {
            list?.forEach {
                it.controlSettingIosModel?.apply {
                    if (backgroundImageViewItem != null) {
                        backgroundImageViewItem =
                            controlCenterIosModel.controlSettingIosModel?.backgroundImageViewItem.toString()
                        cornerBackgroundViewItem =
                            controlCenterIosModel.controlSettingIosModel?.cornerBackgroundViewItem ?: 0f
                        backgroundColorSelectViewItem =
                            controlCenterIosModel.controlSettingIosModel?.backgroundColorSelectViewItem.toString()
                    }
                }
            }
        }

    }
    @JvmStatic
    fun saveThemeIOSEdit(
        control: ItemControl,
        preview: ConstraintLayout,
    ){
        CoroutineScope(Dispatchers.IO).launch {
            if (control.idCategory == Constant.VALUE_CONTROL_CENTER_OS) {
                SharedPreferenceHelper.storeLong(LAST_TIME_EDIT_THEME, System.currentTimeMillis())
                val id = if (control.id in 6000..6012){
                     saveThemeIOSFromAssets(App.ins,control, preview)
                } else {
                     saveThemeIOSFromCache(App.ins,control, preview)
                }
                val theme = ThemeControl(id, control.idCategory, "thumb.webp")
                insertThemeControl(theme)
                Timber.e("NVQ saveThemeIOSEdit ++++++")
                onApplyTheme(theme, isMyCustomizationControl = true, isEdit = true)
                }
            }
    }
    private suspend fun saveThemeIOSFromAssets(context: Context,theme: ItemControl, preview: ConstraintLayout) :Long{
        val idOld = theme.id
        theme.id = System.currentTimeMillis()
        val dir = File(
            context.filesDir,
            Constant.FOLDER_THEMES_ASSETS + "/${theme.idCategory}/${theme.id}"
        )

        if (!dir.exists()) {
            dir.mkdirs()
        }

        // Sử dụng coroutine để chạy song song các tác vụ I/O
        withContext(Dispatchers.IO) {
            // Các task để lưu background, thumb và theme JSON

            val backgroundTask = async {
                val background = File(dir, "background.webp")
                if (theme.typeBackground == Constant.DEFAULT) {
                    saveImageFromAssetsToFile(
                        context.assets,
                        Constant.FOLDER_THEMES_ASSETS + "/" + theme.idCategory + "/" + idOld + "/" + theme.background,
                        background, Bitmap.CompressFormat.WEBP
                    )
                }
            }

            when (theme.idCategory) {
                Constant.VALUE_CONTROL_CENTER_OS -> {
                    saveImageFromAssetsToFile(
                        context.assets,
                        Constant.FOLDER_THEMES_ASSETS + "/" + theme.idCategory + "/" + idOld + "/ic_alarm.png",
                        File(dir, "ic_alarm.png"), Bitmap.CompressFormat.PNG
                    )
                    saveImageFromAssetsToFile(
                        context.assets,
                        Constant.FOLDER_THEMES_ASSETS + "/" + theme.idCategory + "/" + idOld + "/ic_darkmode.png",
                        File(dir, "ic_darkmode.png"), Bitmap.CompressFormat.PNG
                    )
                    saveImageFromAssetsToFile(
                        context.assets,
                        Constant.FOLDER_THEMES_ASSETS + "/" + theme.idCategory + "/" + idOld + "/ic_silent.png",
                        File(dir, "ic_silent.png"), Bitmap.CompressFormat.PNG
                    )
                }

            }
            val thumbTask = async {
                val thumb = File(dir, "thumb.webp")
                Utils.getLayoutBitmap(preview)?.let {
                    Utils.saveBitmapToFile(it, thumb, Bitmap.CompressFormat.WEBP)
                }
            }

            val jsonTask = async {
                // Lưu theme dưới dạng JSON
                val jsonFile = File(dir, "theme.json")
                val gson = Gson()
                val jsonString = gson.toJson(theme)
                jsonFile.writeText(jsonString)
            }

            // Đợi tất cả các tác vụ hoàn thành
            awaitAll(backgroundTask, thumbTask, jsonTask)
        }

        return theme.id
    }
    suspend fun onApplyTheme(theme: ThemeControl, isMyCustomizationControl: Boolean,isEdit: Boolean = false){
        EventBus.getDefault().post(EventSelectThemes(theme.id))
        val itemControlTheme = if (isMyCustomizationControl) {
            readItemControlFromJson(App.ins, theme)
        } else {
            getSingleTheme(App.ins, theme)
        }
        withContext(Dispatchers.Main) {
            itemControlTheme?.let {
                ThemeHelper.setItemThemeCurrent(it,isEdit)
            }

        }
    }

    private suspend fun saveThemeIOSFromCache(context: Context,theme: ItemControl, preview: ConstraintLayout) :Long{
        val dir = File(
            context.filesDir,
            Constant.FOLDER_THEMES_ASSETS + "/${theme.idCategory}/${theme.id}"
        )

        if (!dir.exists()) {
            dir.mkdirs()
        }

        // Sử dụng coroutine để chạy song song các tác vụ I/O
        withContext(Dispatchers.IO) {
            // Các task để lưu background, thumb và theme JSON
            val thumbTask = async {
                val thumb = File(dir, "thumb.webp")
                Utils.getLayoutBitmap(preview)?.let {
                    Utils.saveBitmapToFile(it, thumb, Bitmap.CompressFormat.WEBP)
                }
            }

            val jsonTask = async {
                // Lưu theme dưới dạng JSON
                val jsonFile = File(dir, "theme.json")
                val gson = Gson()
                val jsonString = gson.toJson(theme)
                jsonFile.writeText(jsonString)
            }

            // Đợi tất cả các tác vụ hoàn thành
            awaitAll(thumbTask, jsonTask)
        }

        return theme.id
    }


    fun updateListControlVsApp(
        rawList: List<ControlCenterIosModel>,
        listAppControl: ArrayList<*>
    ): ArrayList<ControlCenterIosModel> {
        val list = rawList as ArrayList<ControlCenterIosModel>
        val indexesToRemove = mutableListOf<Int>()
        var controlSettingsModelTemplate: ControlSettingIosModel? = null

        // Tìm kiếm mẫu controlSettingsModel
        list.forEachIndexed { index, item ->
            if ((item.ratioWidght == 4 && item.ratioHeight == 4) || item.keyControl == Constant.KEY_CONTROL_SCREEN_TIME_OUT) {
                indexesToRemove.add(index)
            }

            if (controlSettingsModelTemplate == null && item.controlSettingIosModel?.backgroundColorSelectViewItem != null) {
                controlSettingsModelTemplate = item.controlSettingIosModel
            }
        }

        indexesToRemove.reversed().forEach { index ->
            list.removeAt(index)
        }

        listAppControl.forEach { includeApp ->
            val controlSettingsModel = controlSettingsModelTemplate?.let {
                ControlSettingIosModel(
                    backgroundColorDefaultViewItem = it.backgroundColorDefaultViewItem,
                    backgroundColorSelectViewItem = it.backgroundColorSelectViewItem,
                    backgroundImageViewItem = it.backgroundImageViewItem,
                    isFilterBackgroundViewItem = it.isFilterBackgroundViewItem,
                    cornerBackgroundViewItem = it.cornerBackgroundViewItem,
                    colorDefaultIcon = it.colorDefaultIcon,
                    colorSelectIcon = it.colorSelectIcon,
                    colorTextTitle = it.colorTextTitle,
                    colorTextTitleSelect = it.colorTextTitleSelect,
                    colorTextDescription = it.colorTextDescription,
                    colorTextDescriptionSelect = it.colorTextDescriptionSelect,
                    iconControl = it.iconControl,
                    backgroundDefaultColorViewParent = it.backgroundDefaultColorViewParent,
                    backgroundSelectColorViewParent = it.backgroundSelectColorViewParent,
                    backgroundImageViewParent = it.backgroundImageViewParent,
                    cornerBackgroundViewParent = it.cornerBackgroundViewParent
                )
            }

            val controlCenterIosModel = ControlCenterIosModel().apply {
                this.controlSettingIosModel = controlSettingsModel

                if (includeApp is ControlCustomize) {
                    when {
                        includeApp.isDefault == 1 -> {
                            keyControl = includeApp.packageName
                            if (keyControl == Constant.KEY_CONTROL_SCREEN_TIME_OUT) {
                                ratioWidght = 8
                                ratioHeight = 4
                            } else {
                                ratioWidght = 4
                                ratioHeight = 4
                            }
                            controlSettingsModel?.iconControl = when (keyControl) {
                                Constant.KEY_CONTROL_ALARM -> "ic_alarm.png"
                                Constant.KEY_CONTROL_DARKMODE -> "ic_darkmode.png"
                                //                                    Constant.KEY_CONTROL_ROTATE -> "ic_rotate.png"
                                Constant.KEY_CONTROL_SILENT -> "ic_silent.png"
                                else -> "ICON_DEFAULT"
                            }

                        }

                        else -> {
                            keyControl = Constant.KEY_CONTROL_OPEN_APP
                            ratioWidght = 4
                            ratioHeight = 4
                            controlSettingsModel?.colorDefaultIcon = "#00000000"
                            controlSettingsModel?.colorSelectIcon = "#00000000"
                            controlSettingsModel?.iconControl = includeApp.packageName
                        }
                    }
                }
            }
            list.add(controlCenterIosModel)
        }
        return list
    }
    @JvmStatic
    fun getItemControl(controlCustomize : ControlCustomize, listControl1 :ArrayList<ControlCenterIosModel>) :ControlCenterIosModel{
        val control = ControlCenterIosModel();
        val controlTemplate = listControl1.lastOrNull()
        with(control){
            Timber.e("NVQ getItemControl controlTemplate: $controlTemplate")
            controlSettingIosModel = controlTemplate?.controlSettingIosModel?.clone()
            controlMusicIosModel = controlTemplate?.controlMusicIosModel?.clone()
            controlBrightnessVolumeIosModel = controlTemplate?.controlBrightnessVolumeIosModel?.clone()
            when {
                controlCustomize.isDefault == 1 -> {
                    keyControl = controlCustomize.packageName
                    if (keyControl == Constant.KEY_CONTROL_SCREEN_TIME_OUT) {
                        ratioWidght = 8
                        ratioHeight = 4
                    } else {
                        ratioWidght = 4
                        ratioHeight = 4
                    }
                    controlSettingIosModel?.iconControl = when (keyControl) {
                        Constant.KEY_CONTROL_ALARM -> "ic_alarm.png"
                        Constant.KEY_CONTROL_DARKMODE -> "ic_darkmode.png"
                        Constant.KEY_CONTROL_SILENT -> "ic_silent.png"
                        else -> "ICON_DEFAULT"
                    }

                }

                else -> {
                    keyControl = Constant.KEY_CONTROL_OPEN_APP
                    ratioWidght = 4
                    ratioHeight = 4
                    controlSettingIosModel?.iconControl = controlCustomize.packageName
                }
            }
        }
        return control
    }
}


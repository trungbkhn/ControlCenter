package com.tapbi.spark.controlcenter.data.repository

import android.content.Context
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.data.model.ItemControl
import com.tapbi.spark.controlcenter.eventbus.EventSelectThemes
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class ThemeHelper @Inject constructor() {


    companion object {
        lateinit var itemControl: ItemControl

        fun setItemThemeCurrent(theme: ItemControl,isEditTheme:Boolean = false) {
            ThemesRepository.isControlEditing = false
            itemControl = theme
            App.tinyDB.putLong(Constant.KEY_ID_CURRENT_APPLY_THEME, theme.id)
            if (isEditTheme){
                NotyControlCenterServicev614.getInstance().loadThemeEdit(theme)
            } else {
                enableWindow()
            }
        }

        fun enableWindow() {
            if (NotyControlCenterServicev614.getInstance() != null) {
                NotyControlCenterServicev614.getInstance()
                    .setValueChange(itemControl.idCategory)
                NotyControlCenterServicev614.getInstance().updateBg()
                NotyControlCenterServicev614.getInstance().disableWindow()
                if (App.tinyDB.getInt(
                        Constant.IS_ENABLE,
                        Constant.IS_DISABLE,
                    ) == Constant.DEFAULT_IS_ENABLE
                ) {
                    NotyControlCenterServicev614.getInstance().enableWindow()
                }
            }
        }
        fun getThemeCurrentApply(context: Context) {
            CoroutineScope(Dispatchers.IO + CoroutineExceptionHandler(fun(
                _: CoroutineContext, throwable: Throwable
            ) {
                run {
                    Timber.e(throwable)
                }
            })).launch {
                val id = App.tinyDB.getLong(
                    Constant.KEY_ID_CURRENT_APPLY_THEME,
                    Constant.KEY_ID_CURRENT_APPLY_THEME_DEFAULT
                )
                if (ThemesRepository.listAllThemes.isEmpty()) {
                    ThemesRepository.getAllListThemes(context)
                }
                val control = ThemesRepository.listAllThemes.find { it.id == id }?.let {
                    ThemesRepository.getSingleTheme(context, it)
                } ?: kotlin.run {
                    ThemesRepository.getItemThemeControlById(id)
                        ?.let { ThemesRepository.readItemControlFromJson(context, it) }
                }
                control?.let {
                    setItemThemeCurrent(it)
                }


            }
        }

    }




}
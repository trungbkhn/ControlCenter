package com.tapbi.spark.controlcenter.data.repository

import android.content.Context
import android.content.res.Resources
import android.os.Build
import com.google.gson.Gson
import com.tapbi.spark.controlcenter.data.model.ActionClick
import com.tapbi.spark.controlcenter.data.model.Language
import com.tapbi.spark.controlcenter.data.model.ListLanguage
import com.tapbi.spark.controlcenter.utils.LocaleUtils
import java.nio.charset.StandardCharsets
import javax.inject.Inject

class LanguageRepository @Inject constructor() {
    fun getListLanguage(context: Context): ListLanguage {
        val list: MutableList<Language> = LocaleUtils.getLanguages(context)
        var posSelected = -1
        for (i in list.indices) {
            if (list[i].codeLocale == LocaleUtils.codeLanguageCurrent) {
                posSelected = i
                break
            }
        }
        return ListLanguage(list, posSelected)
    }


    fun getListAction(context: Context): ActionClick? {
        val inputStream = context.assets.open("text_action.json")
        // Đọc file JSON
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        val json = String(buffer, StandardCharsets.UTF_8)

        // Parse JSON và tìm action tương ứng
        val gson = Gson()
        val objectList = gson.fromJson(json, Array<ActionClick>::class.java).asList()
        return objectList.find { it.languageCode == getLocaleStringResource() }
    }


    private fun getLocaleStringResource(): String {
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Resources.getSystem().configuration.locales[0]
        } else {
            Resources.getSystem().configuration.locale
        }
        return locale.language;

    }
}
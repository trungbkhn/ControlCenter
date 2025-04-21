package com.tapbi.spark.controlcenter.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.preference.PreferenceManager
import androidx.core.app.ActivityCompat
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.data.local.SharedPreferenceHelper
import com.tapbi.spark.controlcenter.data.model.Language
import com.tapbi.spark.controlcenter.data.repository.ThemeHelper
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614
import com.tapbi.spark.controlcenter.ui.main.MainActivity
import com.tapbi.spark.controlcenter.ui.onboard.OnBoardActivity
import com.tapbi.spark.controlcenter.ui.splash.SplashActivity
import com.tapbi.spark.controlcenter.utils.DensityUtils.isAtLeastSdkVersion
import timber.log.Timber
import java.util.*

object LocaleUtils {
    var codeLanguageCurrent: String = Locale.getDefault().language
    fun applyLocale(context: Context?) {
        if (context == null)
            return
        codeLanguageCurrent =
            SharedPreferenceHelper.getString(Constant.PREF_SETTING_LANGUAGE).toString()
        if (codeLanguageCurrent == "null" || codeLanguageCurrent.isEmpty()) {
            codeLanguageCurrent = Locale.getDefault().language
        }
        val mLanguageCode = if(codeLanguageCurrent.contains("-")) codeLanguageCurrent.split("-")[0] else codeLanguageCurrent
        val mCountry = if(codeLanguageCurrent.contains("-")) codeLanguageCurrent.split("-")[1] else ""
        val newLocale = if(mCountry.isEmpty()) Locale(mLanguageCode) else Locale(mLanguageCode, mCountry)
        updateResource(context, newLocale)
        NotyControlCenterServicev614.getInstance()?.let{ins -> updateResource(ins,newLocale) }
        if (context != context.applicationContext) {
            updateResource(context.applicationContext, newLocale)
        }
    }
    fun setCurrentResources(context: Context?,activity: Activity?=null){
        if (context == null) return
        try {
            codeLanguageCurrent =
                SharedPreferenceHelper.getString(Constant.PREF_SETTING_LANGUAGE).toString()
        }catch (_ : RuntimeException){}
        if (codeLanguageCurrent == "null" || codeLanguageCurrent.isEmpty()) {
            codeLanguageCurrent = Locale.getDefault().language
        }
        val mLanguageCode = if(codeLanguageCurrent.contains("-")) codeLanguageCurrent.split("-")[0] else codeLanguageCurrent
        val mCountry = if(codeLanguageCurrent.contains("-")) codeLanguageCurrent.split("-")[1] else ""
        val newLocale = if(mCountry.isEmpty()) Locale(mLanguageCode) else Locale(mLanguageCode, mCountry)
        updateResource(context, newLocale)
        if (activity is SplashActivity || activity is MainActivity) {
            NotyControlCenterServicev614.getInstance()?.let{ins -> updateResource(ins,newLocale) }
            updateResource(context.applicationContext, newLocale)
        }
    }

    fun getCurrentLanguageName(context: Context?): String {
        context?.let {
            codeLanguageCurrent =
                SharedPreferenceHelper.getString(Constant.PREF_SETTING_LANGUAGE).toString()
            if (codeLanguageCurrent == "null" || codeLanguageCurrent.isEmpty()) {
                codeLanguageCurrent = Locale.getDefault().language
            }
            val languageList = getLanguages(context)
            for (item in languageList) {
                if (item.codeLocale == codeLanguageCurrent) {
                    return item.nameLanguage
                }
            }
        }
        return ""
    }

    fun updateResource(context: Context, locale: Locale) {
        Locale.setDefault(locale)
        val resources = context.resources
        val current = getLocaleCompat(resources)
        if (current === locale) {
            return
        }
        val configuration = Configuration(resources.configuration)
        if (isAtLeastSdkVersion(Build.VERSION_CODES.N)) {
            configuration.setLocale(locale)
        } else if (isAtLeastSdkVersion(Build.VERSION_CODES.JELLY_BEAN_MR1)) {
            configuration.setLocale(locale)
        } else {
            configuration.locale = locale
        }
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    @JvmStatic
    fun getLocaleCompat(resources: Resources): Locale {
        val configuration = resources.configuration
        return if (isAtLeastSdkVersion(Build.VERSION_CODES.N)) configuration.locales[0] else configuration.locale
    }

    fun applyLocaleAndRestart(
        activity: Activity?,
        localeString: String,
        destination: Class<*> = SplashActivity::class.java,
        action: String? = null
    ) {
        activity?.let {
            SharedPreferenceHelper.storeString(Constant.PREF_SETTING_LANGUAGE, localeString)
            applyLocale(activity)
            val intent = Intent(activity, destination)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            action?.let {
                intent.action = it
            }
            activity.startActivity(intent)
            ActivityCompat.finishAffinity(activity)
        }
    }

    fun getLanguages(context: Context): MutableList<Language> {
        val list: MutableList<Language> = ArrayList<Language>()
        list.add(Language(Constant.ENGLISH_LANGUAGE_CODE, context.resources.getString(R.string.english)))
        list.add(Language(Constant.ARABIC_LANGUAGE_CODE, context.resources.getString(R.string.arabic)))
        list.add(Language(Constant.GERMAN_LANGUAGE_CODE, context.resources.getString(R.string.german)))
        list.add(Language(Constant.SPANISH_LANGUAGE_CODE, context.resources.getString(R.string.spain)))
        list.add(Language(Constant.PHILIPPINE_LANGUAGE_CODE, context.resources.getString(R.string.philippine)))
        list.add(Language(Constant.FRENCH_LANGUAGE_CODE, context.resources.getString(R.string.france)))

        list.add(Language(Constant.HINDI_LANGUAGE_CODE, context.resources.getString(R.string.hindi)))
        list.add(Language(Constant.INDONESIA_LANGUAGE_CODE, context.resources.getString(R.string.indonesia)))
        list.add(Language(Constant.JAPAN_LANGUAGE_CODE, context.resources.getString(R.string.japanese)))
        list.add(Language(Constant.KOREA_LANGUAGE_CODE, context.resources.getString(R.string.korean)))
        list.add(Language(Constant.PORTUGAL_LANGUAGE_CODE, context.resources.getString(R.string.portuguese)))
        list.add(Language(Constant.RUSSIAN_LANGUAGE_CODE, context.resources.getString(R.string.russia)))
        list.add(Language(Constant.TURKEY_LANGUAGE_CODE, context.resources.getString(R.string.turkish)))
        list.add(Language(Constant.VIETNAM_LANGUAGE_CODE, context.resources.getString(R.string.vietnamese)))
        return list
    }
    fun applyLocaleAndRestartFirstTime(activity: Activity, localeString: String) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
        preferences.edit().putString(Constant.PREF_SETTING_LANGUAGE, localeString).apply()
        applyLocale(activity)
        val intent = Intent(activity, OnBoardActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        activity.startActivity(intent)
        ActivityCompat.finishAffinity(activity)
    }


    fun setLocal(local: String, context: Context): Context {
        var activity = context
        val locale = Locale(local)
        Locale.setDefault(locale)
        val res = activity.resources
        val config = Configuration(res.configuration)
        config.setLocale(locale)
        activity = activity.createConfigurationContext(config)
        return activity
    }
}
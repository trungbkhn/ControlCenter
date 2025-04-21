package com.tapbi.spark.controlcenter.utils

import android.annotation.SuppressLint
import android.content.Context
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import java.util.*


object StringUtils {

    fun getIconDefaultApp(name: String, context: Context): String {
        when (name) {
            Constant.SLEEP -> {
                return context.resources.getString(R.string.Sleep)
            }
            Constant.DO_NOT_DISTURB -> {
                return context.resources.getString(R.string.do_not_disturb)
            }
            Constant.READING -> {
                return context.resources.getString(R.string.reading)
            }
            Constant.MINDFULNESS -> {
                return context.resources.getString(R.string.Mindfulness)
            }
            Constant.WORK -> {
                return context.resources.getString(R.string.work)
            }
            Constant.GAMING -> {
                return context.resources.getString(R.string.gaming)
            }
            Constant.DRIVING -> {
                return context.resources.getString(R.string.driving)
            }
            Constant.PERSONAL -> {
                return context.getString(R.string.personal)
            }
            Constant.CUSTOM -> {
                return context.getString(R.string.custom)
            }
        }
        return name
    }

    fun uppercaseFirstCharacters(s: String): String {
        return s.substring(0, 1).uppercase(Locale.getDefault()) + s.substring(1)
    }

    fun isEmptyString(text: String?): Boolean {
        return text == null || text.trim { it <= ' ' } == "null" || text.trim { it <= ' ' }
            .isEmpty()
    }

    @SuppressLint("Override")
    fun formatNumberToE164(phoneNumber: String, defaultCountryIso: String?): String {
        val util = PhoneNumberUtil.getInstance()
        var result: String = phoneNumber
        try {
            val pn = util.parse(phoneNumber, defaultCountryIso)
            if (util.isValidNumber(pn)) {
                result = util.format(pn, PhoneNumberUtil.PhoneNumberFormat.E164)
            }
        } catch (_: NumberParseException) {

        }
        return result
    }


}
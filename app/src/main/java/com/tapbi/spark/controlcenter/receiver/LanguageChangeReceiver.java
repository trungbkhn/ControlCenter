package com.tapbi.spark.controlcenter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.tapbi.spark.controlcenter.utils.LocaleUtils;

import java.util.Locale;

import timber.log.Timber;

public class LanguageChangeReceiver extends BroadcastReceiver {

    private ILanguageChange iLanguageChange;

    public LanguageChangeReceiver(ILanguageChange iLanguageChange) {
        this.iLanguageChange = iLanguageChange;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        Timber.e("NVQ onLanguageChange");
//        Locale locale = new Locale(Locale.getDefault().getLanguage());
//        Locale.setDefault(locale);
//        Resources resources = context.getResources();
//        Configuration config = resources.getConfiguration();
//        config.setLocale(locale);
//        resources.updateConfiguration(config, resources.getDisplayMetrics());
//
//        if (iLanguageChange != null) {
//            iLanguageChange.languageChange();
//        }
    }

    public interface ILanguageChange {
        void languageChange();
    }
}
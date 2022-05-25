package com.bulat.soshicon2.BottomNavigation.account;

import static com.bulat.soshicon2.constants.constants.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class LanguageManager {
    private final Context context;

    private final SharedPreferences sharedPreferences;

    public LanguageManager(Context cont) {
        context = cont;

        sharedPreferences = context.getSharedPreferences(DATABASE, 0);
    }

    public void updateResource(String code) {
        Locale locale = new Locale(code);
        Locale.setDefault(locale);

        Resources resource = context.getResources();

        Configuration configuration = resource.getConfiguration();
        configuration.locale = locale;

        resource.updateConfiguration(configuration, resource.getDisplayMetrics());

        setLang(code);
    }

    public String getLang() {
        return sharedPreferences.getString(LANG, "ru");
    }

    public void setLang(String code) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LANG, code);
        editor.apply();
    }
}

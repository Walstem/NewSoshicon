package com.bulat.soshicon2.BottomNavigation.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class LanguageManager {
    private final Context context;

    private SharedPreferences sharedPreferences;

    public LanguageManager(Context cont) {
        context = cont;

        sharedPreferences = context.getSharedPreferences("LANG", Context.MODE_PRIVATE);
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
        return sharedPreferences.getString("lang", "en");
    }

    public void setLang(String code) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lang", code);
        editor.commit();
    }
}

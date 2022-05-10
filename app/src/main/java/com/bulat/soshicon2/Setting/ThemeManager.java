package com.bulat.soshicon2.Setting;

import static com.bulat.soshicon2.constants.constants.*;

import static com.bulat.soshicon2.constants.constants.DATABASE;
import static com.bulat.soshicon2.constants.constants.LANG;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeManager {
    private final Context context;

    private final SharedPreferences sharedPreferences;

    public ThemeManager(Context cont) {
        context = cont;
        sharedPreferences = context.getSharedPreferences(DATABASE, Context.MODE_PRIVATE);
    }

    public void updateResource(String checked) {
        if (checked.equals("true")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        setTheme(checked);
    }

    public String getTheme() {
        return sharedPreferences.getString(THEME, "");
    }

    public void setTheme(String checked) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(THEME, checked);
        editor.apply();
    }
}

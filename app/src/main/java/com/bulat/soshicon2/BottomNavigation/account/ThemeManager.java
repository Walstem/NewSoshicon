package com.bulat.soshicon2.BottomNavigation.account;

import static com.bulat.soshicon2.constants.constants.*;

import static com.bulat.soshicon2.constants.constants.DATABASE;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeManager {

    private final SharedPreferences sharedPreferences;

    public ThemeManager(Context cont) {
        sharedPreferences = cont.getSharedPreferences(DATABASE, 0);
    }

    public void updateResource(boolean checked) {
        if(checked) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setTheme(checked);
    }

    public boolean getTheme() {
        return sharedPreferences.getBoolean(THEME, false);
    }

    public void setTheme(boolean checked) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(THEME, checked);
        editor.apply();
    }
}

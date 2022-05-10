package com.bulat.soshicon2.BottomNavigation.account;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.appcompat.app.AppCompatDelegate;

import java.util.Locale;

public class ThemeManager {
    private final Context context;

    public ThemeManager(Context cont) {
        context = cont;
    }

    public void updateResource() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }
}

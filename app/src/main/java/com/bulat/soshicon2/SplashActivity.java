package com.bulat.soshicon2;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bulat.soshicon2.BottomNavigation.account.LanguageManager;
import com.bulat.soshicon2.BottomNavigation.account.ThemeManager;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LanguageManager languageManager = new LanguageManager(this);
        languageManager.updateResource(languageManager.getLang());

        ThemeManager themeManager = new ThemeManager(this);
        themeManager.updateResource(themeManager.getTheme());

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        this.finish();
    }
}

package com.bulat.soshicon2;

import static com.bulat.soshicon2.constants.constants.*;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bulat.soshicon2.BottomNavigation.account.Account;
import com.bulat.soshicon2.BottomNavigation.Chat;
import com.bulat.soshicon2.BottomNavigation.account.ThemeManager;
import com.bulat.soshicon2.BottomNavigation.event.Event;
import com.bulat.soshicon2.BottomNavigation.Response;
import com.bulat.soshicon2.Registration.Authorization;

import com.bulat.soshicon2.BottomNavigation.account.LanguageManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        LanguageManager languageManager = new LanguageManager(this);
        languageManager.updateResource(languageManager.getLang());

        ThemeManager themeManager = new ThemeManager(this);
        themeManager.updateResource(themeManager.getTheme());

        SharedPreferences sp = getSharedPreferences(DATABASE, MODE_PRIVATE);
        String id = sp.getString(ID, "");

        if (id.equals("0")) {
            replaceFragment(new Authorization());
        }
        else {
            replaceFragment(new Event());
        }

        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnItemSelectedListener(item -> {

            int idMenu = item.getItemId();

            if (idMenu == R.id.nav_event)
                replaceFragment(new Event());

            else if (idMenu == R.id.nav_response)
                replaceFragment(new Response());

            else if (idMenu == R.id.nav_chat)
                replaceFragment(new Chat());

            else if (idMenu == R.id.nav_account)
                replaceFragment(new Account());

            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}

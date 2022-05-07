package com.bulat.soshicon2.Setting;

import static com.bulat.soshicon2.constants.constants.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bulat.soshicon2.BottomNavigation.Account;
import com.bulat.soshicon2.R;
import com.bulat.soshicon2.Registration.Authorization;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Setting extends Fragment {

    BottomNavigationView navBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View MainView = inflater.inflate(R.layout.account_setting, container, false);

        navBar = getActivity().findViewById(R.id.bottom_navigation);
        ConstraintLayout log_out = MainView.findViewById(R.id.setting_log_out);
        LanguageManager lang = new LanguageManager(getContext());
        ImageView faq = MainView.findViewById(R.id.aboutUs_image);
        ImageView tex = MainView.findViewById(R.id.text_size_image);
        ImageView cancel = MainView.findViewById(R.id.cancel);

        //Выключение bottom navigation
        navBar.setVisibility(View.GONE);

        //Смена темы приложения
        SwitchCompat lightMode = MainView.findViewById(R.id.lightModeSwitch);
        lightMode.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                lightMode.setChecked(true);
            }
            else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                lightMode.setChecked(false);
            }
        });

        //Кнопка выхода из настроек
        cancel.setOnClickListener(view -> {
            replaceFragmentParent(new Account());
            BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation);
            navBar.setVisibility(View.VISIBLE);
        });

        //Смена языка приложения
        tex.setOnClickListener(view -> {
            lang.updateResource("ru");
            replaceFragmentParent(new Setting());
        });

        faq.setOnClickListener(view -> {
            lang.updateResource("en");
            replaceFragmentParent(new Setting());
        });

        //Выход из аккаунта
        log_out.setOnClickListener(v -> {
            SharedPreferences sp = MainView.getContext().getSharedPreferences(DATABASE, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(ID, "");
            editor.putString(U_NICKNAME, "");
            editor.putString(EMAIL, "");
            editor.putString(PASSWORD, "");
            editor.apply();

            replaceFragmentParent(new Authorization());

            BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation);
            navBar.setVisibility(View.GONE);
        });

        return MainView;
    }

    //Функция обновление родительского фрагмента
    public void replaceFragmentParent(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, fragment);
        fragmentTransaction.commit();
    }
}

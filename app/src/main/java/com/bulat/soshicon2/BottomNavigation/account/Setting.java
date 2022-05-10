package com.bulat.soshicon2.BottomNavigation.account;

import static com.bulat.soshicon2.constants.constants.AVATAR;
import static com.bulat.soshicon2.constants.constants.DATABASE;
import static com.bulat.soshicon2.constants.constants.EMAIL;
import static com.bulat.soshicon2.constants.constants.ID;
import static com.bulat.soshicon2.constants.constants.PASSWORD;
import static com.bulat.soshicon2.constants.constants.SMALL_AVATAR;
import static com.bulat.soshicon2.constants.constants.THEME;
import static com.bulat.soshicon2.constants.constants.U_NICKNAME;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
;
import com.bulat.soshicon2.R;
import com.bulat.soshicon2.Registration.Authorization;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import de.hdodenhof.circleimageview.CircleImageView;

public class Setting extends Fragment {

    BottomNavigationView navBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View MainView = inflater.inflate(R.layout.setting, container, false);
        SharedPreferences sp = getContext().getSharedPreferences(DATABASE, 0);

        navBar = getActivity().findViewById(R.id.bottom_navigation);
        ConstraintLayout log_out = MainView.findViewById(R.id.setting_log_out);
        ConstraintLayout language = MainView.findViewById(R.id.languages);
        ImageView cancel = MainView.findViewById(R.id.cancel);
        SwitchCompat lightMode = MainView.findViewById(R.id.lightModeSwitch);
        ThemeManager themeManager = new ThemeManager(getContext());

        CircleImageView avatar = MainView.findViewById(R.id.avatar);
        TextView username = MainView.findViewById(R.id.username_bottom_avatar);
        username.setText(sp.getString(U_NICKNAME, ""));
        File file = new File(sp.getString(AVATAR, ""));

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            avatar.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //Выключение bottom navigation
        navBar.setVisibility(View.GONE);

        //Переход на фрагмент смены языка
        language.setOnClickListener(view -> { replaceFragmentParent(new Language()); });

        //Смена темы приложения
        SharedPreferences spp = getActivity().getSharedPreferences("night", 0);
        boolean booleanValue = spp.getBoolean(THEME, false);
        if (booleanValue) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            lightMode.setChecked(true);
        }

        lightMode.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                themeManager.updateResource(true);
                lightMode.setChecked(true);
                SharedPreferences.Editor editor = spp.edit();
                editor.putBoolean(THEME, true);
                editor.apply();
            }
            else {
                themeManager.updateResource(false);
                lightMode.setChecked(false);
                SharedPreferences.Editor editor = spp.edit();
                editor.putBoolean(THEME, false);
                editor.apply();
            }
        });

        //Кнопка выхода из настроек
        cancel.setOnClickListener(view -> {
            replaceFragmentParent(new Account());
            BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation);
            navBar.setVisibility(View.VISIBLE);
        });

        //Выход из аккаунта
        log_out.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(ID, "");
            editor.putString(U_NICKNAME, "");
            editor.putString(EMAIL, "");
            editor.putString(PASSWORD, "");
            editor.putString(AVATAR, "");
            editor.putString(SMALL_AVATAR, "");
            editor.apply();

            getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
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

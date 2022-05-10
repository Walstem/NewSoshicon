package com.bulat.soshicon2.BottomNavigation.account;

import static com.bulat.soshicon2.constants.constants.AVATAR;
import static com.bulat.soshicon2.constants.constants.DATABASE;
import static com.bulat.soshicon2.constants.constants.EMAIL;
import static com.bulat.soshicon2.constants.constants.ID;
import static com.bulat.soshicon2.constants.constants.PASSWORD;
import static com.bulat.soshicon2.constants.constants.SMALL_AVATAR;
import static com.bulat.soshicon2.constants.constants.U_NICKNAME;

import android.content.Context;
import android.content.SharedPreferences;
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
        View View = inflater.inflate(R.layout.setting, container, false);
        SharedPreferences sp = getContext().getSharedPreferences(DATABASE, 0);

        navBar = getActivity().findViewById(R.id.bottom_navigation);
        ConstraintLayout log_out = View.findViewById(R.id.setting_log_out);
        LanguageManager lang = new LanguageManager(getContext());
        ImageView faq = View.findViewById(R.id.aboutUs_image);
        ImageView tex = View.findViewById(R.id.text_size_image);
        ImageView cancel = View.findViewById(R.id.cancel);
        CircleImageView avatar = View.findViewById(R.id.avatar);
        TextView username = View.findViewById(R.id.username_bottom_avatar);
        username .setText(sp.getString(U_NICKNAME, ""));


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

        //Смена темы приложения
        SwitchCompat lightMode = View.findViewById(R.id.lightModeSwitch);
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
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(ID, "");
            editor.putString(U_NICKNAME, "");
            editor.putString(EMAIL, "");
            editor.putString(PASSWORD, "");
            editor.putString(AVATAR, "");
            editor.putString(SMALL_AVATAR, "");
            editor.apply();

            replaceFragmentParent(new Authorization());

            BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation);
            navBar.setVisibility(View.GONE);
        });

        return View;
    }

    //Функция обновление родительского фрагмента
    public void replaceFragmentParent(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, fragment);
        fragmentTransaction.commit();
    }
}

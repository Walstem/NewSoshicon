package com.bulat.soshicon2.BottomNavigation.account;

import static com.bulat.soshicon2.constants.constants.*;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
;
import com.bulat.soshicon2.BottomNavigation.event.receivingEvent;
import com.bulat.soshicon2.R;
import com.bulat.soshicon2.Registration.Authorization;
import com.bulat.soshicon2.checks.FragmentReplace;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

public class Setting extends Fragment {

    BottomNavigationView navBar;
    public static final String GET_AVATAR_PHP = "get_avatar.php";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View MainView = inflater.inflate(R.layout.setting, container, false);

        SharedPreferences sp = requireContext().getSharedPreferences(DATABASE, 0);

        navBar = requireActivity().findViewById(R.id.bottom_navigation);

        ConstraintLayout log_out = MainView.findViewById(R.id.setting_log_out);
        ConstraintLayout language = MainView.findViewById(R.id.languages);
        ImageView cancel = MainView.findViewById(R.id.cancel);
        SwitchCompat lightMode = MainView.findViewById(R.id.lightModeSwitch);
        ThemeManager themeManager = new ThemeManager(requireContext());

        CircleImageView avatar = MainView.findViewById(R.id.avatar);
        TextView username = MainView.findViewById(R.id.username_bottom_avatar);
        username.setText(sp.getString(U_NICKNAME, ""));

        String[] KeyArgs = {"id"};
        String[] Args = {sp.getString(ID, "")};

        receivingEvent Query = new receivingEvent(GET_AVATAR_PHP, KeyArgs, Args);
        Query.execute();
        JSONArray Event_json = null;
        try {
            Event_json = new JSONArray(Query.get());
        } catch (JSONException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        //добавляем данные из json в массив фотографий и в переменную аватар

        //парсим данные

        try
        {
            JSONObject jo = new JSONObject((String) Event_json.get(0));
            String Avatar = (String) jo.get("avatar");

            if (Avatar != null){
                //byte [] encodeByte = Base64.decode(GalleryPhotos.get(i),Base64.DEFAULT);
                Uri bitmap =  Uri.parse ("http://j911147y.beget.tech/"+Avatar);

                Glide.with(getContext())
                        .load(bitmap)
                        .into(avatar);

            }
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }

        //Выключение bottom navigation
        navBar.setVisibility(View.GONE);

        //Переход в смену языка
        language.setOnClickListener(view -> FragmentReplace.replaceFragmentParent(new Language(), getActivity()));

        //Смена темы приложения
        SharedPreferences spp = requireContext().getSharedPreferences("night", 0);
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
            BottomNavigationView navBar = requireActivity().findViewById(R.id.bottom_navigation);
            navBar.setVisibility(View.VISIBLE);
        });

        //Выход из аккаунта
        log_out.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(ID, "0");
            editor.putString(U_NICKNAME, "");
            editor.putString(EMAIL, "");
            editor.putString(PASSWORD, "");
            editor.putString(AVATAR, "");
            editor.putString(SMALL_AVATAR, "");
            editor.putString("compress_gallery_photo_0", "");
            editor.putString("compress_gallery_photo_1", "");
            editor.putString("compress_gallery_photo_2", "");
            editor.putString("compress_gallery_photo_3", "");
            editor.putString("compress_gallery_photo_4", "");
            editor.putString("compress_gallery_photo_5", "");
            editor.putString("compress_gallery_photo_6", "");
            editor.putString("compress_gallery_photo_7", "");
            editor.putString("compress_gallery_photo_8", "");
            editor.apply();

            requireActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            replaceFragmentParent(new Authorization());

            BottomNavigationView navBar = requireActivity().findViewById(R.id.bottom_navigation);
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

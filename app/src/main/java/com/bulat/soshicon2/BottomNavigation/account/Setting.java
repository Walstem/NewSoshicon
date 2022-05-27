package com.bulat.soshicon2.BottomNavigation.account;

import static com.bulat.soshicon2.constants.constants.*;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.bulat.soshicon2.R;
import com.bulat.soshicon2.Registration.Authorization;
import com.bulat.soshicon2.checks.FragmentReplace;
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

        SharedPreferences sp = requireContext().getSharedPreferences(DATABASE, 0);

        navBar = requireActivity().findViewById(R.id.bottom_navigation);

        ConstraintLayout log_out = MainView.findViewById(R.id.setting_log_out);
        ConstraintLayout language = MainView.findViewById(R.id.languages);
        ImageView cancel = MainView.findViewById(R.id.cancel);
        SwitchCompat lightMode = MainView.findViewById(R.id.lightModeSwitch);
        ThemeManager themeManager = new ThemeManager(requireContext());
        Button editBtn = MainView.findViewById(R.id.setting_editBtn);

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

        //���������� bottom navigation
        navBar.setVisibility(View.GONE);

        //������� � �������������� ������
        editBtn.setOnClickListener(view -> FragmentReplace.replaceFragmentParent(new Redactor(), requireActivity()));

        //������� � ����� �����
        language.setOnClickListener(view -> FragmentReplace.replaceFragmentParent(new Language(), requireActivity()));

        //����� ���� ����������
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

        //������ ������ �� ��������
        cancel.setOnClickListener(view -> {
            replaceFragmentParent(new Account());
            BottomNavigationView navBar = requireActivity().findViewById(R.id.bottom_navigation);
            navBar.setVisibility(View.VISIBLE);
        });

        //����� �� ��������
        log_out.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(ID, "0");
            editor.putString(U_NICKNAME, "");
            editor.putString(EMAIL, "");
            editor.putString(PASSWORD, "");
            editor.putString(AVATAR, "");
            editor.putString(SMALL_AVATAR, "");
            editor.apply();

            requireActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            replaceFragmentParent(new Authorization());

            BottomNavigationView navBar = requireActivity().findViewById(R.id.bottom_navigation);
            navBar.setVisibility(View.GONE);
        });

        return MainView;
    }

    //������� ���������� ������������� ���������
    public void replaceFragmentParent(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, fragment);
        fragmentTransaction.commit();
    }
}

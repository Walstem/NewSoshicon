package com.bulat.soshicon2.Setting;

import static com.bulat.soshicon2.constants.constants.*;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bulat.soshicon2.R;
import com.bulat.soshicon2.Registration.Authorization;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Setting extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.account_setting, container, false);

        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation);
        navBar.setVisibility(View.GONE);

        ConstraintLayout log_out = view.findViewById(R.id.setting_log_out);

        LanguageManager lang = new LanguageManager(getContext());

        ImageView faq = view.findViewById(R.id.aboutUs_image);
        ImageView tex = view.findViewById(R.id.text_size_image);
        ImageView cancel = view.findViewById(R.id.cancel);

        SwitchCompat lightMode = view.findViewById(R.id.lightModeSwitch);

        lightMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    lightMode.setChecked(true);
                    BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation);
                    navBar.setVisibility(View.VISIBLE);
                }
                else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    lightMode.setChecked(false);
                    BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation);
                    navBar.setVisibility(View.VISIBLE);
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().remove(Setting.this).commit();
                BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation);
                navBar.setVisibility(View.VISIBLE);
            }
        });

        tex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lang.updateResource("ru");
                Setting f2 = new Setting();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment_activity_main, f2);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lang.updateResource("en");
                Setting f2 = new Setting();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment_activity_main, f2);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                SharedPreferences sp = view.getContext().getSharedPreferences(DATABASE, getContext().MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(ID, "");
                editor.putString(U_NICKNAME, "");
                editor.putString(EMAIL, "");
                editor.putString(PASSWORD, "");

                Authorization authorization = new Authorization();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(R.id.nav_host_fragment_activity_main, authorization);
                transaction.addToBackStack(null);
                transaction.commit();

                BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation);
                navBar.setVisibility(View.GONE);
            }
        });

        return view;
    }
}

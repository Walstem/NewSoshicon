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
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bulat.soshicon2.R;
import com.bulat.soshicon2.Registration.Authorization;

public class Setting extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.account_setting, container, false);

        ConstraintLayout log_out = view.findViewById(R.id.setting_log_out);

        LanguageManager lang = new LanguageManager(getContext());

        ImageView faq = view.findViewById(R.id.aboutUs_image);
        ImageView tex = view.findViewById(R.id.text_size_image);

        tex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lang.updateResource("ru");
                Setting set = new Setting();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(R.id.nav_host_fragment_activity_main, set);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lang.updateResource("en");
                Setting set = new Setting();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(R.id.nav_host_fragment_activity_main, set);
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
            }
        });

        return view;
    }
}

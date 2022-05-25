package com.bulat.soshicon2.BottomNavigation.account;

import static com.bulat.soshicon2.constants.constants.*;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bulat.soshicon2.R;

public class Language extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View MainView = inflater.inflate(R.layout.setting_language, container, false);

        RadioButton russian = MainView.findViewById(R.id.russian_radio);
        RadioButton english = MainView.findViewById(R.id.english_radio);
        ImageView cancel = MainView.findViewById(R.id.cancel);
        LanguageManager lang = new LanguageManager(getContext());

        RadioGroup radioGroup = MainView.findViewById(R.id.languages_group);
        SharedPreferences sp = getContext().getSharedPreferences(DATABASE, 0);
        if (sp.getString("lang", "").equals("en")){
            english.setChecked(true);
        }
        else if (sp.getString("lang", "").equals("ru")){
            russian.setChecked(true);
        }
        else{
            russian.setChecked(true);
        }
        radioGroup.setOnCheckedChangeListener((radioGroup1, i) -> {

            int RadioId = radioGroup1.getCheckedRadioButtonId();

            if (RadioId == R.id.english_radio) {
                english.setChecked(true);
                lang.updateResource("en");
                replaceFragmentParent(new Language());
            }
            else if (RadioId == R.id.russian_radio) {
                russian.setChecked(true);
                lang.updateResource("ru");
                replaceFragmentParent(new Language());
            }
            else {
                russian.setChecked(true);
                lang.updateResource("ru");
            }
        });

        //Кнопка выхода из настроек языка
        cancel.setOnClickListener(view -> { replaceFragmentParent(new Setting()); });

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

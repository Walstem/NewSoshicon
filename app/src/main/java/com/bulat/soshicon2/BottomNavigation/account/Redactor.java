package com.bulat.soshicon2.BottomNavigation.account;

import static com.bulat.soshicon2.constants.constants.DATABASE;
import static com.bulat.soshicon2.constants.constants.ID;
import static com.bulat.soshicon2.constants.constants.U_STATUS;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.bulat.soshicon2.R;
import com.bulat.soshicon2.asynctasks.SendQuery;
import com.bulat.soshicon2.checks.FragmentReplace;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;

public class Redactor extends Fragment {

    private static final String UPLOAD_STATUS = "uploadStatus.php";

    SharedPreferences sp;
    TextInputLayout usernameField,
            passwordField,
            emailField,
            statusField;
    Button saveChangeBtn;
    ImageView cancelEditProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View MainView = inflater.inflate(R.layout.setting_edit, container, false);
        BottomNavigationView navBar = requireActivity().findViewById(R.id.bottom_navigation);
        navBar.setVisibility(View.GONE);
        sp = requireContext().getSharedPreferences(DATABASE, 0);

        usernameField = MainView.findViewById(R.id.usernameField);
        passwordField = MainView.findViewById(R.id.passwordField);
        emailField = MainView.findViewById(R.id.emailField);
        statusField = MainView.findViewById(R.id.statusField);
        saveChangeBtn = MainView.findViewById(R.id.saveChangeBtn);
        cancelEditProfile = MainView.findViewById(R.id.cancelEditProfile);

        //сохранение измененных данных
        saveChangeBtn.setOnClickListener(view -> {
            String getStatusField = statusField.getEditText().getText().toString();
            SendQuery request = new SendQuery(UPLOAD_STATUS);
            request.execute("?status=" + getStatusField + "&id=" + sp.getString(ID, ""));

            //добавляем статус пользователя в файл-настройки
            SharedPreferences.Editor ed = sp.edit();
            ed.putString(U_STATUS, "");
            ed.apply();
        });

        cancelEditProfile.setOnClickListener(view -> FragmentReplace.replaceFragmentParent(new Account(), requireActivity()));

        return MainView;
    }
}

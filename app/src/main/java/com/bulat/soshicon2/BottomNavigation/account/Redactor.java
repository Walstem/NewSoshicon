package com.bulat.soshicon2.BottomNavigation.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bulat.soshicon2.R;

public class Redactor extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View MainView = inflater.inflate(R.layout.setting_edit, container, false);
        return MainView;
    }
}

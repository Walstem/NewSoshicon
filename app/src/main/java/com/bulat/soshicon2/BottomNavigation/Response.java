package com.bulat.soshicon2.BottomNavigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.bulat.soshicon2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Response extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation);
        navBar.setVisibility(View.VISIBLE);

        return inflater.inflate(R.layout.fragment_response, container, false);
    }
}

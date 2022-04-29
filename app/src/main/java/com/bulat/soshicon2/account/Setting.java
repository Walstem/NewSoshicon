package com.bulat.soshicon2.account;

import static com.bulat.soshicon2.constants.constants.*;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;

import com.bulat.soshicon2.R;
import com.bulat.soshicon2.Registration.Authorization;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class
Setting extends BottomSheetDialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        return dialog;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ConstraintLayout log_out = view.findViewById(R.id.setting_log_out);
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

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
                closeBottomSheet(bottomSheetBehavior);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.account_setting, container, false);

        return view;
    }
    private void closeBottomSheet(BottomSheetBehavior bottomSheetBehavior) {
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }
}

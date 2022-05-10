package com.bulat.soshicon2.BottomNavigation.event;

import static com.bulat.soshicon2.constants.constants.*;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.bulat.soshicon2.R;
import com.bulat.soshicon2.SQLUtils.SQLUtils;
import com.bulat.soshicon2.asynctasks.SendQuery;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class AddEvent extends BottomSheetDialogFragment {
    int selected_id = 0;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottomshit_add_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View MainView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(MainView, savedInstanceState);

        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from((View) MainView.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        CoordinatorLayout layout = getDialog().findViewById(R.id.bottom_sheet_layout);
        assert layout != null;
        layout.setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels);

        ImageView cancelEvent = MainView.findViewById(R.id.cancel);
        ImageView add = MainView.findViewById(R.id.add);
        EditText editText = (EditText) MainView.findViewById(R.id.ed_add);

        SharedPreferences sp = getActivity().getSharedPreferences(DATABASE, Context.MODE_PRIVATE);
        TextView name = MainView.findViewById(R.id.username);
        name.setText(sp.getString(U_NICKNAME, ""));

        add.setOnClickListener(view -> {
            String content = editText.getText().toString();

            if(!content.equals("")){
                String pattern = "yyyy-MM-dd-HH-mm";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

                SharedPreferences sp1 = getContext().getSharedPreferences(DATABASE, 0);
                String user_id  = sp1.getString(ID, "");
                String nickname = sp1.getString(U_NICKNAME, "");
                String Message = editText.getText().toString();
                String time = simpleDateFormat.format(new Date());

                String urlArgs = new SQLUtils(user_id, Message, nickname, time).input_event();

                SendQuery sendQuery = new SendQuery("input_event.php");
                sendQuery.execute(urlArgs);
                try {
                    String answer = sendQuery.get();
                    if (answer.equals("true")) {
                        //������ ����� ���������� ��������� �� �������� �������� �������
                        closeBottomSheet(bottomSheetBehavior);
                    }
                    else {
                        //������ ����� ���������� ��������� �� ������
                    }
                } catch (ExecutionException | InterruptedException e) {
                    //������ ����� ���������� ��������� �� ������
                }
            }
        });
        cancelEvent.setOnClickListener(v -> closeBottomSheet(bottomSheetBehavior));

    }
    private void closeBottomSheet(BottomSheetBehavior bottomSheetBehavior) {
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }
}
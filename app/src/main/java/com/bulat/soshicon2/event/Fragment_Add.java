package com.bulat.soshicon2.event;

import static com.bulat.soshicon2.constants.constants.*;

import android.app.Dialog;
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

import java.util.concurrent.ExecutionException;

public class Fragment_Add extends BottomSheetDialogFragment {
    int selected_id = 0;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.bottomshit_add_event, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        CoordinatorLayout layout = getDialog().findViewById(R.id.bottom_sheet_layout);
        assert layout != null;
        layout.setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels);

        ImageView cancelEvent = view.findViewById(R.id.cancel);
        ImageView add = view.findViewById(R.id.add);
        EditText editText = (EditText) view.findViewById(R.id.ed_add);

        SharedPreferences sp = getActivity().getSharedPreferences(DATABASE, getContext().MODE_PRIVATE);
        TextView name = view.findViewById(R.id.username);
        name.setText(sp.getString(U_NICKNAME, ""));

        add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String content = editText.getText().toString();

                if(!content.equals("")){
                    SharedPreferences sp = getContext().getSharedPreferences(DATABASE, 0);
                    String user_id = sp.getString(ID, "");
                    String nickname = sp.getString(U_NICKNAME, "");
                    String Message = editText.getText().toString();
                    String urlArgs = new SQLUtils(user_id, Message, nickname).input_distribution();

                    SendQuery sendQuery = new SendQuery("input_distribution_soshicon.php");
                    sendQuery.execute(urlArgs);
                    try {
                        String answer = sendQuery.get();
                        if (answer.equals("true")){
                            //здесть будет выводиться сообщение об успешном создании события
                            closeBottomSheet(bottomSheetBehavior);
                        }
                        else{
                            //здесть будет выводиться сообщение об ошибке
                        }
                    } catch (ExecutionException | InterruptedException e) {
                        //здесть будет выводиться сообщение об ошибке
                    }
                }
            }
        });
        cancelEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeBottomSheet(bottomSheetBehavior);
            }
        });

    }
    private void closeBottomSheet(BottomSheetBehavior bottomSheetBehavior) {
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }
}
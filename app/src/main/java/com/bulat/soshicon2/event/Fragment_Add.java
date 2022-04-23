package com.bulat.soshicon2.event;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.PorterDuff;
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
import androidx.fragment.app.Fragment;

import com.bulat.soshicon2.R;
import com.bulat.soshicon2.SQLUtils.SQLUtils;
import com.bulat.soshicon2.asynctasks.SendQuery;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Add#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Add extends BottomSheetDialogFragment {
    int selected_id = 0;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Add() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Add newInstance(String param1, String param2) {
        Fragment_Add fragment = new Fragment_Add();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.bottomshit_add_event, container, false);
        EditText editText = (EditText) view.findViewById(R.id.ed_add);
        String[] recipient_arr = getResources().getStringArray(R.array.recipient);

        SharedPreferences sp = getActivity().getSharedPreferences("UserData", getContext().MODE_PRIVATE);
        TextView name = view.findViewById(R.id.username);
        name.setText(sp.getString("U_NICKNAME", ""));
        ImageView add = view.findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String content = editText.getText().toString();

                if(!content.equals("")){
                    SharedPreferences sp = getContext().getSharedPreferences("UserData", 0);
                    String user_id = sp.getString("ID", "");
                    String nickname = sp.getString("U_NICKNAME", "");
                    String Message = editText.getText().toString();
                    String urlArgs = new SQLUtils(user_id, Message, nickname).input_distribution();

                    SendQuery sendQuery = new SendQuery("input_distribution_soshicon.php");
                    sendQuery.execute(urlArgs);
                    try {
                        String answer = sendQuery.get();
                        if (answer.equals("true")){
                            //здесть будет выводиться сообщение об успешном создании события
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

    }
}
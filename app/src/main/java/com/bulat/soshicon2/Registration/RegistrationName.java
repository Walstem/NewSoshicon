package com.bulat.soshicon2.Registration;

import static com.bulat.soshicon2.constants.constants.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bulat.soshicon2.R;
import com.bulat.soshicon2.checks.FragmentReplace;
import com.bulat.soshicon2.checks.NetCheck;
import com.bulat.soshicon2.checks.UniqueNameCheck;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class RegistrationName extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.registration_name, container, false);

        Button onwards = (Button) root.findViewById(R.id.login_btn);

        onwards.setOnClickListener(view -> {
            try {
                onwards(root);
            } catch (IOException | ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        return root;
    }

    //when you click on the onwards button, we start the registrationActivityPassword activity
    public void onwards(View view) throws IOException, ExecutionException, InterruptedException {
        if (NetCheck.StatusConnection(getContext())) {
            ViewToastMessage(view);
        }
        else {
            TextInputLayout username = view.findViewById(R.id.username);
            String u_nickname = username.getEditText().getText().toString();
            TextView MessageError = view.findViewById(R.id.error_text);

            //запрос в бд на существование имени
            UniqueNameCheck unique = new UniqueNameCheck();
            String answerUnique = unique.uniqueness(u_nickname);

            //Если пользователь введет слишком короткое имя
            if(u_nickname.length() < 3) {
                String ErrorMess = getResources().getString(R.string.min_size_name_error);
                System.out.println(ErrorMess);
                alertError(username, MessageError, ErrorMess);
            }
            //Если пользователь введет уже существующие имя
            else if(answerUnique.equals(u_nickname)) {
                String ErrorMess = getResources().getString(R.string.name_taken_1) + u_nickname + getResources().getString(R.string.name_taken_2);;
                alertError(username, MessageError, ErrorMess);
            }
            //Если все будет верно

            else {
                //Переход на фрагмент создания пароля
                SharedPreferences sp = getContext().getSharedPreferences(DATABASE, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();

                editor.putString(U_NICKNAME, u_nickname);
                editor.apply();

                FragmentReplace.replaceFragmentParent(new RegistrationPassword(), getActivity());
            }
        }
    }

    //анимация edittext, если пользователь ошибется
    public void alertError(TextInputLayout filed, TextView MessageError ,String message) {
        final Animation shakeAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.error_shake);

        //анимация
        filed.startAnimation(shakeAnimation);
        filed.setBackground(getResources().getDrawable(R.drawable.anim_et_changecolor));

        //сообщение о ошибке
        MessageError.setText(message);
        MessageError.setVisibility(View.VISIBLE);
    }

    public void ViewToastMessage(View view) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_internet_message,(ViewGroup) view.findViewById(R.id.toast_layout_root));
        Toast toast = new Toast(getContext().getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 50);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}

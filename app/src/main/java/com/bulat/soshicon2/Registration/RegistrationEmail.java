package com.bulat.soshicon2.Registration;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
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
import androidx.fragment.app.FragmentTransaction;

import com.bulat.soshicon2.R;
import com.bulat.soshicon2.checks.NetCheck;

import java.io.IOException;

public class RegistrationEmail extends Fragment {

    private final String EMAIL = "email";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.registration_email, container, false);

        Button onwards = (Button) root.findViewById(R.id.email_btn);

        onwards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    onwards(root);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return root;
    }

    //Проверка ввода электронной почты
    public static boolean isValidEmail(CharSequence mail) {
        if (TextUtils.isEmpty(mail)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches();
        }
    }

    //when you click on the onwards button, we start the registration_finish activity
    public void onwards(View view) throws IOException {
        if (NetCheck.StatusConnection(getContext())) {
            ViewToastMessage(view);
        }
        else{
            EditText email = (EditText) view.findViewById(R.id.email);
            TextView MessageEror = (TextView) view.findViewById(R.id.error_text);

            //User input validation
            CharSequence mail = email.getText().toString();
            if (!isValidEmail(mail)) {
                String message = getResources().getString(R.string.email_error);
                alertEror(email, MessageEror, message);
            }
            else {
                //Переход на фрагмент окончания регистрации
                SharedPreferences sp = getContext().getSharedPreferences("user_data", getContext().MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();

                editor.putString(EMAIL, email.getText().toString());
                editor.apply();

                RegistrationFinish rf = new RegistrationFinish();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(this.getId(), rf);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        }
    }
    //анимация edittext, если пользователь ошибется
    public void alertEror(EditText filed, TextView MessageEror ,String message){
        final Animation shakeAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.error_shake);

        //анимация
        filed.startAnimation(shakeAnimation);
        filed.setBackground(getResources().getDrawable(R.drawable.anim_et_changecolor));

        //сообщение о ошибке
        MessageEror.setText(message);
        MessageEror.setVisibility(View.VISIBLE);
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

package com.bulat.soshicon2.Registration;

import static com.bulat.soshicon2.constants.constants.*;

import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.bulat.soshicon2.R;
import com.bulat.soshicon2.checks.FragmentReplace;
import com.bulat.soshicon2.checks.NetCheck;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;

public class RegistrationEmail extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View MainView = inflater.inflate(R.layout.registration_email, container, false);

        Button onwards = (Button) MainView.findViewById(R.id.email_btn);

        onwards.setOnClickListener(view -> {
            try {
                onwards(MainView);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return MainView;
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
        if (NetCheck.StatusConnection(requireContext())) {
            ViewToastMessage(view);
        }
        else {
            TextInputLayout email = view.findViewById(R.id.email);
            TextView MessageError = (TextView) view.findViewById(R.id.error_text);

            //User input validation
            CharSequence mail = email.getEditText().getText().toString();
            if (!isValidEmail(mail)) {
                String message = getResources().getString(R.string.email_error);
                alertError(email, MessageError, message);
            }
            else {
                //Переход на фрагмент окончания регистрации
                SharedPreferences sp = requireContext().getSharedPreferences(DATABASE, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();

                editor.putString(EMAIL, email.getEditText().getText().toString());
                editor.apply();

                FragmentReplace.replaceFragmentParent(new RegistrationFinish(), getActivity());
            }
        }
    }

    //анимация edittext, если пользователь ошибется
    public void alertError(TextInputLayout filed, TextView MessageError ,String message){
        final Animation shakeAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.error_shake);

        //анимация
        filed.startAnimation(shakeAnimation);
        filed.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.anim_et_changecolor, null));

        //сообщение о ошибке
        MessageError.setText(message);
        MessageError.setVisibility(View.VISIBLE);
    }

    public void ViewToastMessage(View view) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_internet_message,(ViewGroup) view.findViewById(R.id.toast_layout_root));
        Toast toast = new Toast(requireActivity().getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 50);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}

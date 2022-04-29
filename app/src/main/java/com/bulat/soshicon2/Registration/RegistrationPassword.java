package com.bulat.soshicon2.Registration;

import static com.bulat.soshicon2.constants.constants.*;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
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
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegistrationPassword extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.registration_password, container, false);

        EditText password = (EditText) root.findViewById(R.id.password);
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());

        Button onwards = (Button) root.findViewById(R.id.password_btn);

        onwards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    onwards(root);
                } catch (IOException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        });

        return root;
    }

    //when you click on the onwards button, we start the registrationActivityEmail activity
    public void onwards(View view) throws IOException, NoSuchAlgorithmException {
        if (NetCheck.StatusConnection(getContext())) {
            ViewToastMessage(view);
        }
        else{
            EditText password = (EditText) view.findViewById(R.id.password);
            TextView filedError = (TextView) view.findViewById(R.id.error_text);

            //Если пароль меньше восьми символов
            if(password.getText().toString().length()<8) {
                String message = getResources().getString(R.string.min_size_password);
                alertEror(password,filedError, message);
            }
            else {
                //Переход на фрагмент создания электронной почты
                SharedPreferences sp = getContext().getSharedPreferences(DATABASE, getContext().MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                String hexPassword = toHexString(getSHA(password.getText().toString()));
                editor.putString(PASSWORD, hexPassword);
                editor.apply();

                RegistrationEmail re = new RegistrationEmail();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(this.getId(), re);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        }
    }
    //анимация edittext, если пользователь ошибется
    public void alertEror(EditText filed, TextView filedEror ,String message){
        final Animation shakeAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.error_shake);

        //анимация
        filed.startAnimation(shakeAnimation);
        filed.setBackground(getResources().getDrawable(R.drawable.anim_et_changecolor));

        //сообщение о ошибке
        filedEror.setText(message);
        filedEror.setVisibility(View.VISIBLE);
    }

    private static byte[] getSHA(String input) throws NoSuchAlgorithmException {
        // Static getInstance method is called with hashing SHA
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // digest() method called
        // to calculate message digest of an input
        // and return array of byte
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    private static String toHexString(byte[] hash)
    {
        // Convert byte array into signup representation
        BigInteger number = new BigInteger(1, hash);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 32)
        {
            hexString.insert(0, '0');
        }

        return hexString.toString();

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
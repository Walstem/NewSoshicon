package com.bulat.soshicon2.Registration;

import static com.bulat.soshicon2.constants.constants.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bulat.soshicon2.BottomNavigation.event.Event;
import com.bulat.soshicon2.R;
import com.bulat.soshicon2.SQLUtils.SQLUtils;
import com.bulat.soshicon2.asynctasks.SendQuery;
import com.bulat.soshicon2.checks.NetCheck;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Authorization extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View MainView = inflater.inflate(R.layout.authorization, container, false);

        //Выключение bottom navigation
        BottomNavigationView navigationView = getActivity().findViewById(R.id.bottom_navigation);
        navigationView.setVisibility(View.GONE);

        //Кнопка регистрации
        TextView tv_registration = (TextView) MainView.findViewById(R.id.tv_registration);
        tv_registration.setOnClickListener(v -> registration(MainView));

        //Кнопка авторизации
        Button authorization_enter = MainView.findViewById(R.id.authorization_enter);
        authorization_enter.setOnClickListener(v -> authorization(MainView));

        return MainView;
    }

    //Авторизация
    public void authorization(View view){
        if (NetCheck.StatusConnection(getContext())) {
            ViewToastMessage(view);
        }
        else {

            try {
                //получаем данные пользователя
                EditText ed_login = (EditText) view.findViewById(R.id.login);
                EditText ed_password = (EditText) view.findViewById(R.id.password);
                String login = ed_login.getText().toString();
                //преобразуем пароль в хэш
                String password =  toHexString(getSHA(ed_password.getText().toString()));

                //выполняем запрос в базу данных
                String UserData = new SQLUtils(login, password).Authorization();

                SendQuery request = new SendQuery("authorization.php");
                request.execute(UserData);
                String getData = request.get();
                //если данные верны, переводим на главную страницу
                if (getData.equals("true")){
                    //получаем id пользователя
                    SendQuery request_id = new SendQuery("get_id.php");
                    request_id.execute("?name="+login);
                    String id =  request_id.get();

                    // добавляем id пользователя в сохраненные настрйоки
                    SharedPreferences sPref = getContext().getSharedPreferences(DATABASE, Context.MODE_PRIVATE);
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putString(ID, id);
                    ed.putString(U_NICKNAME, login);
                    ed.apply();
                    String data_id = sPref.getString(ID, "");
                    System.out.println(data_id);

                    replaceFragmentParent(new Event());
                }
                // если сообщения ложные выводим сообщение об ошибке
                else {
                    LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.error_message_out);
                    linearLayout.setVisibility(View.VISIBLE);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //Проверка на наличие интернета, если он есть, начинается регистрация
    public void registration(View view) {
        if (NetCheck.StatusConnection(getContext())) {
            ViewToastMessage(view);
        }
        else {
            replaceFragmentParent(new RegistrationName());
        }
    }

    //Функция обновление родительского фрагмента
    public void replaceFragmentParent(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, fragment);
        fragmentTransaction.commit();
    }

    //Сообщение об отсутствии интернета
    public void ViewToastMessage(View view) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_internet_message,(ViewGroup) view.findViewById(R.id.toast_layout_root));
        Toast toast = new Toast(getContext().getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 50);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    private static byte[] getSHA(String input) throws NoSuchAlgorithmException {
        // Static getInstance method is called with hashing SHA
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // digest() method called
        // to calculate message digest of an input
        // and return array of byte
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    private static String toHexString(byte[] hash) {
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
}

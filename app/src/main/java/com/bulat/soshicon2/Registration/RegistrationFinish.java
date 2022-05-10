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
import java.io.IOException;

public class RegistrationFinish extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View MainView = inflater.inflate(R.layout.registration_finish, container, false);

        Button finish = (Button) MainView.findViewById(R.id.finish);
        try {
            Welcome(MainView);
        } catch (IOException e) {
            e.printStackTrace();
        }

        finish.setOnClickListener(view -> {
            try {
                finish(MainView);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return MainView;
    }

    //we read the data written to the file and display the greeting line of the new user
    public void Welcome(View view) throws IOException {
        //излекаем данные регистрации из файла
        TextView welcome = (TextView) view.findViewById(R.id.welcome);
        SharedPreferences sp = getContext().getSharedPreferences(DATABASE, Context.MODE_PRIVATE);

        String welcomeText = sp.getString(U_NICKNAME, "") + ", добро \n пожаловать в Soshicon!";
        welcome.setText(welcomeText);
    }

    //when you click on the finish button, we start the main_activity
    public void finish(View view) throws IOException {
        if (NetCheck.StatusConnection(getContext())) {
            ViewToastMessage(view);
        } else {
            //получаем данные введенные в процессе регистрации
            SharedPreferences sp = getContext().getSharedPreferences(DATABASE, getContext().MODE_PRIVATE);
            String name = sp.getString(U_NICKNAME, "");
            String password = sp.getString(PASSWORD, "");
            String email = sp.getString(EMAIL, "");

            //добавляем данные в бд
            String UserData = new SQLUtils(name, password, email).InsertRegData();
            System.out.println(UserData);
            new SendQuery("input_request_handler_soshicon.php").execute(UserData);

            try {
                //получаем id пользователя
                SendQuery request = new SendQuery("get_id.php");
                request.execute("?name=" + name);
                String id = request.get();

                //добавляем id пользователя в сохраненные настрйоки
                SharedPreferences.Editor ed = sp.edit();
                ed.putString(ID, id);
                ed.apply();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Окончание регистрации
            replaceFragmentParent(new Event());
        }
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

    //Функция обновление родительского фрагмента
    public void replaceFragmentParent(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, fragment);
        fragmentTransaction.commit();
    }
}
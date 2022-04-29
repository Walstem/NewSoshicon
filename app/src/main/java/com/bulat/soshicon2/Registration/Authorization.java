package com.bulat.soshicon2.Registration;

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
import androidx.fragment.app.FragmentTransaction;

import com.bulat.soshicon2.BottomNavigation.Event;
import com.bulat.soshicon2.R;
import com.bulat.soshicon2.SQLUtils.SQLUtils;
import com.bulat.soshicon2.asynctasks.SendQuery;
import com.bulat.soshicon2.checks.NetCheck;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Authorization extends Fragment {
    final String ID = "ID";
    private final String U_NICKNAME = "U_NICKNAME";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.authorization, container, false);

        TextView tv_registration = (TextView) view.findViewById(R.id.tv_registration);
        tv_registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registration(view);
            }
        });
        Button authorization_enter = view.findViewById(R.id.authorization_enter);
        authorization_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Authorization(view);
            }
        });

        return view;
    }

    public void Authorization(View view){
        if (NetCheck.StatusConnection(getContext())) {
            ViewToastMessage(view);
        }
        else {

            try {
                //�������� ������ ������������
                EditText ed_login = (EditText) view.findViewById(R.id.login);
                EditText ed_password = (EditText) view.findViewById(R.id.password);
                String login = ed_login.getText().toString();
                //����������� ������ � ���
                String password =  toHexString(getSHA(ed_password.getText().toString()));

                //��������� ������ � ���� ������
                String UserData = new SQLUtils(login, password).Authorization();

                SendQuery request = new SendQuery("authorization.php");
                request.execute(UserData);
                String getData = request.get();
                //���� ������ �����, ��������� �� ������� ��������
                if (getData.equals("true")){
                    //�������� id ������������
                    SendQuery request_id = new SendQuery("get_id.php");
                    request_id.execute("?name="+login);
                    String id =  request_id.get();

                    // ��������� id ������������ � ����������� ���������
                    SharedPreferences sPref = getContext().getSharedPreferences("user_data", getContext().MODE_PRIVATE);
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putString(ID, id);
                    ed.putString(U_NICKNAME, login);
                    ed.apply();
                    String data_id = sPref.getString(ID, "");
                    System.out.println(data_id);

                    Event event = new Event();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.add(this.getId(), event);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
                // ���� ��������� ������ ������� ��������� �� ������
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

    //�������� �� ������� ���������, ���� �� ����, ���������� �����������
    public void registration(View view) {
        if (NetCheck.StatusConnection(getContext())) {
            ViewToastMessage(view);
        }
        else {
            RegistrationName rn = new RegistrationName();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(this.getId(), rn);
            transaction.addToBackStack(null);
            transaction.commit();
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

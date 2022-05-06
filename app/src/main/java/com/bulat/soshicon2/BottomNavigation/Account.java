package com.bulat.soshicon2.BottomNavigation;

import static com.bulat.soshicon2.constants.constants.*;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bulat.soshicon2.R;
import com.bulat.soshicon2.Setting.Setting;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Account extends Fragment {

    CircleImageView profile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View MainView = inflater.inflate(R.layout.account, container, false);

        SharedPreferences sp = getActivity().getSharedPreferences(DATABASE, Context.MODE_PRIVATE);
        TextView name = MainView.findViewById(R.id.username_bottom_avatar);
        ImageView account_setting = MainView.findViewById(R.id.account_edit);
        profile = (CircleImageView) MainView.findViewById(R.id.profile_avatar);

        name.setText(sp.getString(U_NICKNAME, ""));

        //Включение bottom navigation
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation);
        navBar.setVisibility(View.VISIBLE);

        //Переход в настройки
        account_setting.setOnClickListener(v -> replaceFragmentParent(new Setting()));

        //Выбор аватара пользователя
        profile.setOnClickListener(view ->
                ImagePicker.with(Account.this)
                .crop()	    			                //Обрезать изображение (необязательно)
                .compress(1024)			                //Окончательный размер изображения будет меньше 1 МБ (необязательно)
                .maxResultSize(600, 600)	//Окончательное разрешение изображения будет меньше 1080 x 1080 (необязательно)
                .start());

        return MainView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            Uri uri = data.getData();
            profile.setImageURI(uri);

            try {
                byte[] img = ReadFile(uri.getPath());

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        catch (Exception e){

        }

    }
    public byte[] ReadFile(String filename) throws FileNotFoundException {

        File file = new File(filename);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        byte[] byteArray = stream.toByteArray();
        bitmap.recycle();
        return byteArray;
    }
    public void UplPhoto(String strURL) throws IOException {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("http://www.a-domain.com/foo/");

        // Request parameters and other properties.
        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        params.add(new BasicNameValuePair("param-1", "12345"));
        params.add(new BasicNameValuePair("param-2", "Hello!"));
        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        //Execute and get the response.
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            try (InputStream instream = entity.getContent()) {
                // do something useful
            }
        }

        httpclient.getConnectionManager().shutdown();
    }

    //Функция обновление родительского фрагмента
    public void replaceFragmentParent(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
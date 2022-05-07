package com.bulat.soshicon2.BottomNavigation;

import static com.bulat.soshicon2.constants.constants.*;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Account extends Fragment {

    public static final String UPLOAD_AVATAR_PHP = "upload_avatar.php";
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
                        .crop()                                 //Обрезать изображение (необязательно)
                        .compress(1024)                         //Окончательный размер изображения будет меньше 1 МБ (необязательно)
                        .maxResultSize(600, 600)    //Окончательное разрешение изображения будет меньше 1080 x 1080 (необязательно)
                        .start());

        return MainView;
    }

    //Функция обновление родительского фрагмента
    public void replaceFragmentParent(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            Uri uri = data.getData();
            profile.setImageURI(uri);

            try {
                byte[] img = ReadFile(uri.getPath(), 100);
                byte[] compress_img = ReadFile(uri.getPath(), 20);

                UploadAvatar UploadPhoto = new UploadAvatar(img,compress_img, UPLOAD_AVATAR_PHP);
                UploadPhoto.execute();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {

        }

    }
<<<<<<< HEAD
    public byte[] ReadFile(String filename, int compress) throws FileNotFoundException {
=======

    public byte[] ReadFile(String filename) throws FileNotFoundException {
>>>>>>> bff05e154e5a95ade09357438f07f80846ae8a98

        File file = new File(filename);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
        bitmap.compress(Bitmap.CompressFormat.JPEG, compress, stream);

        byte[] byteArray = stream.toByteArray();
        bitmap.recycle();
        return byteArray;
    }

    class UploadAvatar extends AsyncTask<String, String, String> {
        String filename;
        byte[] imgArray;
<<<<<<< HEAD
        byte[] CompressImgArray;
        UploadAvatar(byte[] imgArray, byte[] CompressImgArray,String filename){
=======

        UploadAvatar(byte[] imgArray, String filename) {
>>>>>>> bff05e154e5a95ade09357438f07f80846ae8a98
            this.imgArray = imgArray;
            this.filename = filename;
            this.CompressImgArray = CompressImgArray;
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://j911147y.beget.tech/" + filename);
            SharedPreferences sp = getContext().getSharedPreferences(DATABASE, getContext().MODE_PRIVATE);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            final String ConvertImage = Base64.encodeToString(imgArray, Base64.DEFAULT);
            final String ConvertCompressImage = Base64.encodeToString(CompressImgArray, Base64.DEFAULT);

            nameValuePairs.add(new BasicNameValuePair("avatar_img", ConvertImage));
            nameValuePairs.add(new BasicNameValuePair("compress_img", ConvertCompressImage));
            nameValuePairs.add(new BasicNameValuePair("id", sp.getString(ID, "")));
            try {
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            //Execute and get the response.
            HttpResponse response = null;
            try {
                response = httpclient.execute(httppost);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                assert response != null;
                HttpEntity entity = response.getEntity();

                int n = 0;
                char[] buffer = new char[1024 * 4];
                InputStreamReader reader = new InputStreamReader(entity.getContent(), "UTF8");
                StringWriter writer = new StringWriter();
                while (-1 != (n = reader.read(buffer))) writer.write(buffer, 0, n);

            } catch (IOException e) {
                e.printStackTrace();
            }


            httpclient.getConnectionManager().shutdown();
            return null;
        }
    }
}
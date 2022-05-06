package com.bulat.soshicon2.BottomNavigation;

import static com.bulat.soshicon2.constants.constants.*;

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
import androidx.fragment.app.FragmentTransaction;

import com.bulat.soshicon2.R;;
import com.bulat.soshicon2.Setting.Setting;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
        View view = inflater.inflate(R.layout.account, container, false);

        SharedPreferences sp = getActivity().getSharedPreferences(DATABASE, getContext().MODE_PRIVATE);
        TextView name = view.findViewById(R.id.username_bottom_avatar);
        ImageView account_setting = view.findViewById(R.id.account_edit);
        profile = (CircleImageView) view.findViewById(R.id.profile_avatar);

        name.setText(sp.getString(U_NICKNAME, ""));

        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation);
        navBar.setVisibility(view.VISIBLE);

        account_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Setting set = new Setting();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(R.id.nav_host_fragment_activity_main, set);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(Account.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(600, 600)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            Uri uri = data.getData();
            profile.setImageURI(uri);

            try {
                byte[] img = ReadFile(uri.getPath());

                UploadAvatar UploadPhoto = new UploadAvatar(img, UPLOAD_AVATAR_PHP);
                UploadPhoto.execute();

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
    class UploadAvatar extends AsyncTask<String, String, String>{
        String filename;
        byte[] imgArray;
        UploadAvatar(byte[] imgArray, String filename){
            this.imgArray = imgArray;
            this.filename = filename;
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://j911147y.beget.tech/" + filename);
            SharedPreferences sp = getContext().getSharedPreferences(DATABASE, getContext().MODE_PRIVATE);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            final String ConvertImage = Base64.encodeToString(imgArray, Base64.DEFAULT);
            nameValuePairs.add(new BasicNameValuePair("img", ConvertImage));
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
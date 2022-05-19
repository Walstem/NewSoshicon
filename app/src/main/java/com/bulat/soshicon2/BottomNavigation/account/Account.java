package com.bulat.soshicon2.BottomNavigation.account;

import static com.bulat.soshicon2.constants.constants.*;

<<<<<<< HEAD
import com.bulat.soshicon2.BottomNavigation.account.*;

import android.Manifest;
=======
>>>>>>> 4981b6c41691e2ab4808409fc7a1eaaac73fa859
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bulat.soshicon2.R;;
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

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class Account extends Fragment {

    public static final String UPLOAD_AVATAR_PHP = "upload_avatar.php";
    private static final int READ_PERMISSION = 101;
    CircleImageView profile;

    RecyclerView recyclerView;
    ImageView add_photo;

    ArrayList<Uri> uri = new ArrayList<>();
    RecyclerAdapter adapter;

    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation);
        navBar.setVisibility(View.VISIBLE);

        View view = inflater.inflate(R.layout.account, container, false);

        add_photo = view.findViewById(R.id.add_photo);
        recyclerView = view.findViewById(R.id.gallery_images);

        adapter = new RecyclerAdapter(uri);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        recyclerView.setAdapter(adapter);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == getActivity().RESULT_OK && null != result.getData()) {
                        if (result.getData().getClipData() != null) {
                            int countOfImages = result.getData().getClipData().getItemCount();
                            for (int i = 0; i < countOfImages; i++) {
                                if (uri.size() < 11) {
                                    Uri imagesuri = result.getData().getClipData().getItemAt(i).getUri();
                                    uri.add(imagesuri);
                                } else {
                                    Toast.makeText(getContext(), "Hehey", Toast.LENGTH_SHORT).show();
                                }
                            }
                            adapter.notifyDataSetChanged();

                        } else {
                            if (uri.size() < 11) {
                                Uri imageuri = result.getData().getData();
                                uri.add(imageuri);
                            } else {
                                Toast.makeText(getContext(), "Hehey", Toast.LENGTH_SHORT).show();
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Hehey", Toast.LENGTH_SHORT).show();
                    }
                });

        add_photo.setOnClickListener(view13 -> {
            if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION);
            }

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            activityResultLauncher.launch(intent);
        });

        SharedPreferences sp = getActivity().getSharedPreferences(DATABASE, 0);
        TextView name = view.findViewById(R.id.username_bottom_avatar);
        ImageView account_setting = view.findViewById(R.id.account_edit);
        profile = (CircleImageView) view.findViewById(R.id.profile_avatar);

        //Отоброжение фотографии в профиле
        File file = new File(sp.getString(AVATAR, ""));
        System.out.println(sp.getString(AVATAR, ""));

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            profile.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //чтение

        name.setText(sp.getString(U_NICKNAME, ""));

        account_setting.setOnClickListener(view1 -> {
            Setting set = new Setting();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.nav_host_fragment_activity_main, set);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        profile.setOnClickListener(view12 -> ImagePicker.with(Account.this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(600, 600)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start());

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            Uri uri = data.getData();
            profile.setImageURI(uri);

            try {
                byte[] img = ReadFileOrSaveInDevice(uri.getPath(), 100);
                byte[] compress_img = ReadFileOrSaveInDevice(uri.getPath(), 10);

                UploadAvatar UploadPhoto = new UploadAvatar(img,compress_img, UPLOAD_AVATAR_PHP);
                UploadPhoto.execute();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        catch (Exception e){

        }

    }




//    public class CustomAdapter extends BaseAdapter {
//        private int[] imagePhoto;
//        private Context context;
//        private LayoutInflater inflater;
//
//        public CustomAdapter(int[] imagePhoto, Context context) {
//            this.imagePhoto = imagePhoto;
//            this.context = context;
//            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        }
//
//
//        @Override
//        public int getCount() {
//            return imagePhoto.length;
//        }
//
//        @Override
//        public Object getItem(int i) {
//            return null;
//        }
//
//        @Override
//        public long getItemId(int i) {
//            return 0;
//        }
//
//        @Override
//        public View getView(int i, View view, ViewGroup viewGroup) {
//
//            if(view == null) {
//                view = inflater.inflate(R.layout.row_item_profile, viewGroup, false);
//            }
//
//            ImageView imageView = view.findViewById(R.id.im);
//
//            imageView.setImageResource(imagePhoto[i]);
//
//            return view;
//        }
//    }

    public byte[] ReadFileOrSaveInDevice(String filename, int compress) throws IOException {
        boolean compressFlag;
        compressFlag = compress != 100;
        String compressPath = getContext().getFilesDir() + "avatar_compress_" + compressFlag + ".jpg";


        //Создание объекта записи сжатой фографии на устройсвто
        FileOutputStream out = new FileOutputStream(compressPath);
        //Создание объекта чтения фото с устройства
        File file = new File(filename);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
        //чтение
        bitmap.compress(Bitmap.CompressFormat.JPEG, compress, stream);
        //запись
        bitmap.compress(Bitmap.CompressFormat.JPEG, compress, out);
        out.close();
        //запись пути к сжатой фотографии
        SharedPreferences sp = getContext().getSharedPreferences(DATABASE, 0);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("compress_avatar_" + compressFlag, compressPath);
        ed.commit();
        System.out.println("1234" + compressPath);

        //сохранение пути к картинке

        byte[] byteArray = stream.toByteArray();
        bitmap.recycle();
        return byteArray;
    }
    class UploadAvatar extends AsyncTask<String, String, String>{
        String filename;
        byte[] imgArray;
        byte[] CompressImgArray;
        UploadAvatar(byte[] imgArray, byte[] CompressImgArray,String filename){
            this.imgArray = imgArray;
            this.filename = filename;
            this.CompressImgArray = CompressImgArray;
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://j911147y.beget.tech/" + filename);
            SharedPreferences sp = getContext().getSharedPreferences(DATABASE, 0);

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
    public void WriteImages(String filename, String content, Context context) throws IOException {

        File file = new File(context.getFilesDir(), filename);

        //We write the data entered by the user into a file
        FileWriter filewriter = new FileWriter(file);
        System.out.println(file.getName()+ file.exists());
        BufferedWriter out = new BufferedWriter(filewriter);
        out.write(content);

        out.close();
    }
}
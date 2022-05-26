package com.bulat.soshicon2.BottomNavigation.account;

import static com.bulat.soshicon2.constants.constants.*;

import android.Manifest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Account extends Fragment {

    public static final String UPLOAD_AVATAR_PHP = "upload_avatar.php";
    public static final String UPLOAD_GALLERY_PHP = "upload_gallery_image.php";
    private static final int READ_PERMISSION = 101;
    CircleImageView profile;
    int numPhotoGal;

    RecyclerView recyclerView;
    ImageView add_photo, delete_photo;

    ArrayList<Uri> uri = new ArrayList<>();
    RecyclerAdapter adapter;

    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation);
        navBar.setVisibility(View.VISIBLE);

        View MainView = inflater.inflate(R.layout.account, container, false);

        add_photo = MainView.findViewById(R.id.add_photo);
        delete_photo = MainView.findViewById(R.id.delete_photo);

        recyclerView = MainView.findViewById(R.id.gallery_images);

        adapter = new RecyclerAdapter(uri);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(adapter);

        //���������� ��� ������ ����������� � �������
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == getActivity().RESULT_OK && null != result.getData()) {
                if (result.getData().getClipData() != null) {
                    int countOfImages = result.getData().getClipData().getItemCount();
                    for (int i = 0; i < countOfImages; i++) {
                        if (uri.size() < 9) {
                            Uri imageuri = result.getData().getClipData().getItemAt(i).getUri();
                            try {
                                String getUri = getRealPathFromUri(getContext(), imageuri);
                                byte[] img = ReadFileOrSaveInDeviceGallery(getUri, uri.size());
                                numPhotoGal = uri.size();
                                UploadGallery UploadPhotoGallery = new UploadGallery(img, UPLOAD_GALLERY_PHP, numPhotoGal);
                                UploadPhotoGallery.execute();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            uri.add(imageuri);
                        } else {
                            Toast.makeText(getContext(), "Hehey", Toast.LENGTH_SHORT).show();
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    if (uri.size() < 9) {
                        Uri imageuri = result.getData().getData();
                        System.out.println(imageuri);
                        try {
                            String getUri = getRealPathFromUri(getContext(), imageuri);
                            byte[] img = ReadFileOrSaveInDeviceGallery(getUri, uri.size());
                            numPhotoGal = uri.size();
                            UploadGallery UploadPhotoGallery = new UploadGallery(img, UPLOAD_GALLERY_PHP, numPhotoGal);
                            UploadPhotoGallery.execute();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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

        //������ ���������� ����������� � �������
        add_photo.setOnClickListener(view -> {
            if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION);
            }

            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            }
            activityResultLauncher.launch(intent);
        });

        //������ �������� ����������� �� �������
        delete_photo.setOnClickListener(view -> {
            if(uri.size() == 0) {
                Toast.makeText(getContext(), "� ������� ��� �����������", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "���� ��������", Toast.LENGTH_SHORT).show();
            }
        });


        SharedPreferences sp = getActivity().getSharedPreferences(DATABASE, 0);
        TextView name = MainView.findViewById(R.id.username_bottom_avatar);
        ImageView account_setting = MainView.findViewById(R.id.account_edit);
        profile = (CircleImageView) MainView.findViewById(R.id.profile_avatar);

        //����������� ���������� � �������
        File file = new File(sp.getString(AVATAR, ""));
        System.out.println(sp.getString(AVATAR, ""));

        try {
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            profile.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //������
        name.setText(sp.getString(U_NICKNAME, ""));

        account_setting.setOnClickListener(view -> {
            replaceFragmentParent(new Setting());
        });

        profile.setOnClickListener(view -> ImagePicker.with(Account.this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(600, 600)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start());

        return MainView;
    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
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

                UploadAvatar UploadPhoto = new UploadAvatar(img, compress_img, UPLOAD_AVATAR_PHP);
                UploadPhoto.execute();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        catch (Exception e){

        }

    }

    public byte[] ReadFileOrSaveInDevice(String filename, int compress) throws IOException {
        boolean compressFlag;
        compressFlag = compress != 100;
        String compressPath = getContext().getFilesDir() + "avatar_compress_" + compressFlag + ".jpg";


        //�������� ������� ������ ������ �������� �� ����������
        FileOutputStream out = new FileOutputStream(compressPath);
        //�������� ������� ������ ���� � ����������
        File file = new File(filename);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
        //������
        bitmap.compress(Bitmap.CompressFormat.JPEG, compress, stream);
        //������
        bitmap.compress(Bitmap.CompressFormat.JPEG, compress, out);
        out.close();
        //������ ���� � ������ ����������
        SharedPreferences sp = getContext().getSharedPreferences(DATABASE, 0);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("compress_avatar_" + compressFlag, compressPath);
        ed.apply();
        System.out.println("1234" + compressPath);

        //���������� ���� � ��������

        byte[] byteArray = stream.toByteArray();
        bitmap.recycle();
        return byteArray;
    }

    class UploadAvatar extends AsyncTask<String, String, String> {
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

    class UploadGallery extends AsyncTask<String, String, String> {
        String filename;
        byte[] imgArray;
        int numberPhotoGallery;
        UploadGallery(byte[] imgArray,String filename, int numberPhotoGallery){
            this.imgArray = imgArray;
            this.filename = filename;
            this.numberPhotoGallery = numberPhotoGallery;
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://j911147y.beget.tech/" + filename);
            SharedPreferences sp = getContext().getSharedPreferences(DATABASE, 0);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            final String ConvertImage = Base64.encodeToString(imgArray, Base64.DEFAULT);

            nameValuePairs.add(new BasicNameValuePair("gallery_image", ConvertImage));
            nameValuePairs.add(new BasicNameValuePair("number_photo_gallery", Integer.toString(numberPhotoGallery)));
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

    public byte[] ReadFileOrSaveInDeviceGallery(String filename, int numberPhoto) throws IOException {
        String compressPath = getContext().getFilesDir() + "/gallery_photo_compress_" + numberPhoto + ".jpg";
        System.out.println(compressPath);

        //�������� ������� ������ ������ �������� �� ����������
        FileOutputStream out = new FileOutputStream(compressPath);
        //�������� ������� ������ ���� � ����������
        File file = new File(filename);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
        //������
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        //������
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        out.close();
        //������ ���� � ������ ����������
        SharedPreferences sp = getContext().getSharedPreferences(DATABASE, 0);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("compress_gallery_photo_" + numberPhoto, compressPath);
        ed.apply();
        System.out.println("1234" + compressPath);

        //���������� ���� � ��������

        byte[] byteArray = stream.toByteArray();
        bitmap.recycle();
        return byteArray;
    }

    //������� ���������� ������������� ���������
    public void replaceFragmentParent(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, fragment);
        fragmentTransaction.commit();
    }
}
package com.bulat.soshicon2.BottomNavigation.account;

import static com.bulat.soshicon2.constants.constants.*;

import android.Manifest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bulat.soshicon2.R;
import com.bulat.soshicon2.asynctasks.SendQuery;
import com.bulat.soshicon2.checks.FragmentReplace;
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

public class Account extends Fragment {

    public static final String UPLOAD_AVATAR_PHP = "upload_avatar.php";
    public static final String UPLOAD_GALLERY_PHP = "upload_gallery_image.php";
    private static final int READ_PERMISSION = 101;
    CircleImageView profile;
    int numPhotoGal;
    int countImages;

    RecyclerView recyclerView;
    ImageView add_photo, delete_photo;
    SharedPreferences sp;

    ArrayList<Uri> uriArr = new ArrayList<>();
    RecyclerAdapter adapter;

    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BottomNavigationView navBar = requireActivity().findViewById(R.id.bottom_navigation);
        navBar.setVisibility(View.VISIBLE);

        View MainView = inflater.inflate(R.layout.account, container, false);
        sp = requireContext().getSharedPreferences(DATABASE, 0);

        add_photo = MainView.findViewById(R.id.add_photo);

        recyclerView = MainView.findViewById(R.id.gallery_images);

        //Обновление галереи
        SendQuery query = new SendQuery("getCountImages.php");
        query.execute("?user_id=" + sp.getString(ID, ""));
        System.out.println("SP: " + sp.getString(ID, ""));


        try {
            countImages = Integer.parseInt(query.get());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        //UploadGallerydwd uploadGallery = new UploadGallerydwd(countImages);
        //uploadGallery.execute();
        adapter = new RecyclerAdapter(uriArr,getContext());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(adapter);

        //Активность для выбора изображения в галерею
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == Activity.RESULT_OK && null != result.getData()) {
                if (result.getData().getClipData() != null) {
                    int countOfImages = result.getData().getClipData().getItemCount();
                    for (int i = 0; i < countOfImages; i++) {
                        if (uriArr.size() < 9) {
                            Uri imageUri = result.getData().getClipData().getItemAt(i).getUri();
                            try {
                                String getUri = getRealPathFromUri(requireContext(), imageUri);
                                byte[] img = ReadFileOrSaveInDeviceGallery(getUri, uriArr.size());
                                numPhotoGal = uriArr.size();
                                UploadGallery UploadPhotoGallery = new UploadGallery(img, UPLOAD_GALLERY_PHP, numPhotoGal);
                                UploadPhotoGallery.execute();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            uriArr.add(imageUri);
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    if (uriArr.size() < 9) {
                        Uri imageUri = result.getData().getData();
                        System.out.println(imageUri);
                        try {
                            String getUri = getRealPathFromUri(requireContext(), imageUri);
                            byte[] img = ReadFileOrSaveInDeviceGallery(getUri, uriArr.size());
                            numPhotoGal = uriArr.size();
                            UploadGallery UploadPhotoGallery = new UploadGallery(img, UPLOAD_GALLERY_PHP, numPhotoGal);
                            UploadPhotoGallery.execute();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        uriArr.add(imageUri);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

        //Кнопка добавления изображения в галерею
        add_photo.setOnClickListener(view -> {
            if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION);
            }
            else{
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                activityResultLauncher.launch(intent);
            }
        });

        TextView name = MainView.findViewById(R.id.username_bottom_avatar);
        ImageView account_setting = MainView.findViewById(R.id.account_edit);
        profile = (CircleImageView) MainView.findViewById(R.id.profile_avatar);

        //Отоброжение фотографии в профиле
        File file = new File(sp.getString(AVATAR, ""));
        System.out.println(sp.getString(AVATAR, ""));

        try {
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            profile.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //чтение
        name.setText(sp.getString(U_NICKNAME, ""));

        account_setting.setOnClickListener(view -> FragmentReplace.replaceFragmentParent(new Setting(), requireActivity()));

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
                byte[] compress_img = ReadFileOrSaveInDevice(uri.getPath(), 40);

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
        String compressPath = requireContext().getFilesDir() + "/avatar_compress_" + compressFlag + ".jpg";


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
        SharedPreferences sp = requireContext().getSharedPreferences(DATABASE, 0);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("compress_avatar_" + compressFlag, compressPath);
        ed.apply();
        System.out.println("1234" + compressPath);

        //сохранение пути к картинке

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
            SharedPreferences sp = requireContext().getSharedPreferences(DATABASE, 0);

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
                InputStreamReader reader = new InputStreamReader(entity.getContent(), StandardCharsets.UTF_8);
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
        UploadGallery(byte[] imgArray, String filename, int numberPhotoGallery){
            this.imgArray = imgArray;
            this.filename = filename;
            this.numberPhotoGallery = numberPhotoGallery;
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://j911147y.beget.tech/" + filename);
            SharedPreferences sp = requireContext().getSharedPreferences(DATABASE, 0);

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
                InputStreamReader reader = new InputStreamReader(entity.getContent(), StandardCharsets.UTF_8);
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
        String compressPath = requireContext().getFilesDir() + "/compress_gallery_photo_" + numberPhoto + ".jpg";
        System.out.println(compressPath);

        //Создание объекта записи сжатой фографии на устройсвто
        FileOutputStream out = new FileOutputStream(compressPath);
        //Создание объекта чтения фото с устройства
        File file = new File(filename);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
        //чтение
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);
        //запись
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, out);
        out.close();
        //запись пути к сжатой фотографии
        SharedPreferences sp = requireContext().getSharedPreferences(DATABASE, 0);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("compress_gallery_photo_" + numberPhoto, compressPath);
        System.out.println("ЭТО ОНО2:" + sp.getString("compress_gallery_photo_" + numberPhoto, ""));
        ed.apply();
        System.out.println("1234" + compressPath);

        //сохранение пути к картинке

        byte[] byteArray = stream.toByteArray();
        bitmap.recycle();
        return byteArray;
    }
    public class UploadGallerydwd extends AsyncTask {
        int countImages;

        public UploadGallerydwd(int countImages){
            this.countImages = countImages;
        }
        @Override
        protected Void doInBackground(Object... objects) {

            for (int i = 0; i < countImages; i++) {
                File fileGallery = new File(sp.getString("compress_gallery_photo_" + i, ""));
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(fileGallery));
                    System.out.println(bitmap.toString());
                    String path = sp.getString("compress_gallery_photo_" + i, "");
                    Uri uri = Uri.parse(path);
                    uriArr.add(uri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            adapter = new RecyclerAdapter(uriArr,getContext());
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
            recyclerView.setAdapter(adapter);

            return null;
        }
    }
}
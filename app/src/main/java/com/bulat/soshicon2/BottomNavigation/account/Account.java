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
import android.widget.Button;
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

import com.bulat.soshicon2.BottomNavigation.event.receivingEvent;
import com.bulat.soshicon2.R;
import com.bulat.soshicon2.asynctasks.SendQuery;
import com.bulat.soshicon2.checks.FragmentReplace;
import com.bumptech.glide.Glide;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public static final String GET_COUNT_IMAGES_PHP = "getCountImages.php";
    public static final String GET_STATUS = "getStatus.php";
    public static final String GET_PHOTOS_GALLERY_PHP = "get_photos_gallery.php";
    public static final String GET_AVATAR_PHP = "get_avatar.php";

    private static final int READ_PERMISSION = 101;

    private ArrayList<String> GalleryPhotos = new ArrayList<>();
    ArrayList<Uri> uris = new ArrayList<>();

    int numPhotoGal;
    String status;
    Uri imageUri;

    CircleImageView avatarProfile;
    TextView usernameProfile, statusProfile;
    RecyclerView recyclerView;
    ImageView addPhotoGalleryBtn, profileSetting;
    Button editProfile;
    SharedPreferences sp;

    RecyclerAdapter adapter;

    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View MainView = inflater.inflate(R.layout.account, container, false);
        sp = requireContext().getSharedPreferences(DATABASE, 0);
        BottomNavigationView navBar = requireActivity().findViewById(R.id.bottom_navigation);
        navBar.setVisibility(View.VISIBLE);

        usernameProfile = MainView.findViewById(R.id.usernameProfile);
        statusProfile = MainView.findViewById(R.id.statusProfile);
        profileSetting = MainView.findViewById(R.id.settingBtnProfile);
        avatarProfile = MainView.findViewById(R.id.avatarProfile);
        addPhotoGalleryBtn = MainView.findViewById(R.id.addPhotoGalleryBtn);
        recyclerView = MainView.findViewById(R.id.galleryProfile);
        editProfile = MainView.findViewById(R.id.editProfileBtn);

        adapter = new RecyclerAdapter(uris,getContext());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(adapter);

        //Отображения имени пользователя
        usernameProfile.setText(sp.getString(U_NICKNAME, ""));

        //Обновление статуса
        SendQuery queryStatus = new SendQuery(GET_STATUS);
        queryStatus.execute("?id=" + sp.getString(ID, ""));
        try {
            status = queryStatus.get();
            System.out.println(queryStatus.get());
            statusProfile.setText(status);
        }
        catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


        //Обновление галереи
        SendQuery query = new SendQuery(GET_COUNT_IMAGES_PHP);
        query.execute("?user_id=" + sp.getString(ID, ""));

        String[] KeyArgs = {"id"};
        String[] Args = {sp.getString(ID, "")};

        receivingEvent QueryGallery = new receivingEvent(GET_PHOTOS_GALLERY_PHP, KeyArgs, Args);
        QueryGallery.execute();

        JSONArray Event_json_gallery = null;
        try {
            Event_json_gallery = new JSONArray(QueryGallery.get());

            for (int i = 0; i < Event_json_gallery.length(); i++) {
                JSONObject jo_Gallery = new JSONObject((String) Event_json_gallery.get(i));
                GalleryPhotos.add(jo_Gallery.get("gallery_image").toString());
                //добавляем данные фото в список
                if (GalleryPhotos.get(i) != null){
                    //byte [] encodeByte = Base64.decode(GalleryPhotos.get(i),Base64.DEFAULT);
                    Uri bitmap =  Uri.parse ("http://j911147y.beget.tech/"+GalleryPhotos.get(i));
                    uris.add(bitmap);
                }
            }

            if (GalleryPhotos.size() != 0){
                //устанавливаем фотографии галлерии
                adapter = new RecyclerAdapter(uris,getContext());
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
                recyclerView.setAdapter(adapter);
            }
        } catch (JSONException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        //загрузка аватара
        receivingEvent Query = new receivingEvent(GET_AVATAR_PHP, KeyArgs, Args);
        Query.execute();
        JSONArray Event_json = null;
        try {
            Event_json = new JSONArray(Query.get());
        } catch (JSONException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        //добавляем данные из json в массив фотографий и в переменную аватар
        //парсим данные
        try
        {
            JSONObject jo = new JSONObject((String) Event_json.get(0));
            String Avatar = (String) jo.get("avatar");

            if (Avatar != null){
                //byte [] encodeByte = Base64.decode(GalleryPhotos.get(i),Base64.DEFAULT);
                Uri bitmap =  Uri.parse ("http://j911147y.beget.tech/"+Avatar);

                Glide.with(getContext())
                        .load(bitmap)
                        .into(avatarProfile);
                
            }
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }

        //Активность для выбора изображения в галерею
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == Activity.RESULT_OK && null != result.getData()) {
                if (result.getData().getClipData() != null) {
                    int countOfImages = result.getData().getClipData().getItemCount();
                    for (int i = 0;  i < countOfImages; i++) {
                        if (uris.size() < 9) {
                            imageUri = result.getData().getClipData().getItemAt(i).getUri();
                            try {
                                String getUri = getRealPathFromUri(requireContext(), imageUri);
                                byte[] img = ReadFileOrSaveInDeviceGallery(getUri, uris.size());
                                numPhotoGal = uris.size();
                                UploadGallery UploadPhotoGallery = new UploadGallery(img, UPLOAD_GALLERY_PHP, numPhotoGal);
                                UploadPhotoGallery.execute();

                                uris.add(imageUri);
                                adapter = new RecyclerAdapter(uris,getContext());
                                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
                                recyclerView.setAdapter(adapter);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    if (uris.size() < 9) {
                        Uri imageUri = result.getData().getData();
                        System.out.println(imageUri);
                        try {
                            String getUri = getRealPathFromUri(requireContext(), imageUri);
                            byte[] img = ReadFileOrSaveInDeviceGallery(getUri, uris.size());
                            numPhotoGal = uris.size();
                            UploadGallery UploadPhotoGallery = new UploadGallery(img, UPLOAD_GALLERY_PHP, numPhotoGal);
                            UploadPhotoGallery.execute();

                            uris.add(imageUri);
                            adapter = new RecyclerAdapter(uris,getContext());
                            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
                            recyclerView.setAdapter(adapter);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        //Кнопка добавления изображения в галерею
        addPhotoGalleryBtn.setOnClickListener(view -> {
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

        //Кнопка обновления аватара
        avatarProfile.setOnClickListener(view -> ImagePicker.with(Account.this)
                .crop()
                .compress(1024)
                .maxResultSize(600, 600)
                .start());

        //Переходы
        profileSetting.setOnClickListener(view -> FragmentReplace.replaceFragmentParent(new Setting(), requireActivity()));
        editProfile.setOnClickListener(view -> FragmentReplace.replaceFragmentParent(new Redactor(), requireActivity()));

        return MainView;
    }

    //Правильный путь Uri
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
            avatarProfile.setImageURI(uri);

            try {
                byte[] img = ReadFileOrSaveInDevice(uri.getPath(), 100);
                byte[] compress_img = ReadFileOrSaveInDevice(uri.getPath(), 40);

                UploadAvatar UploadPhoto = new UploadAvatar(img, compress_img, UPLOAD_AVATAR_PHP);
                UploadPhoto.execute();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    class UploadAvatar extends AsyncTask<String, String, String> {
        String filename;
        byte[] imgArray;
        byte[] CompressImgArray;

        UploadAvatar(byte[] imgArray, byte[] CompressImgArray, String filename) {
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

    public byte[] ReadFileOrSaveInDevice(String filename, int compress) throws IOException {
        boolean compressFlag;
        compressFlag = compress != 100;
        String compressPath = requireContext().getFilesDir() + "/avatar_compress_" + compressFlag + ".jpg";

        //???????? ??????? ?????? ?????? ???????? ?? ??????????
        FileOutputStream out = new FileOutputStream(compressPath);
        //???????? ??????? ?????? ???? ? ??????????
        File file = new File(filename);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
        //??????
        bitmap.compress(Bitmap.CompressFormat.JPEG, compress, stream);
        //??????
        bitmap.compress(Bitmap.CompressFormat.JPEG, compress, out);
        out.close();
        //?????? ???? ? ?????? ??????????
        SharedPreferences sp = requireContext().getSharedPreferences(DATABASE, 0);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("compress_avatar_" + compressFlag, compressPath);
        ed.apply();
        System.out.println("1234" + compressPath);

        //?????????? ???? ? ????????
        byte[] byteArray = stream.toByteArray();
        bitmap.recycle();
        return byteArray;
    }

    class UploadGallery extends AsyncTask<String, String, String> {
        String filename;
        byte[] imgArray;
        int numberPhotoGallery;

        UploadGallery(byte[] imgArray, String filename, int numberPhotoGallery) {
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

        //???????? ??????? ?????? ?????? ???????? ?? ??????????
        FileOutputStream out = new FileOutputStream(compressPath);
        //???????? ??????? ?????? ???? ? ??????????
        File file = new File(filename);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
        //??????
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);
        //??????
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, out);
        out.close();
        //?????? ???? ? ?????? ??????????
        SharedPreferences sp = requireContext().getSharedPreferences(DATABASE, 0);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("compress_gallery_photo_" + numberPhoto, compressPath);

        System.out.println("??? ???2:" + sp.getString("compress_gallery_photo_" + numberPhoto, ""));
        ed.apply();
        System.out.println("1234" + compressPath);

        //?????????? ???? ? ????????
        byte[] byteArray = stream.toByteArray();
        bitmap.recycle();
        return byteArray;
    }
}
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
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    public static final String GET_COUNT_IMAGES_PHP = "getCountImages.php";
    public static final String GET_STATUS = "getStatus.php";
    private static final int READ_PERMISSION = 101;

    int numPhotoGal;
    int countImages;
    String status;

    CircleImageView avatarProfile;
    TextView usernameProfile, statusProfile;
    RecyclerView recyclerView;
    ImageView addPhotoGalleryBtn, profileSetting;
    Button editProfile;
    SharedPreferences sp;

    ArrayList<Uri> uriArr = new ArrayList<>();
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

        adapter = new RecyclerAdapter(uriArr);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(adapter);

        //Отоброжение аватара
        File file = new File(sp.getString(AVATAR, ""));
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            avatarProfile.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

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
        try {
            countImages = Integer.parseInt(query.get());
            for (int i = 0; i < countImages; i++) {
                File fileGallery = new File(sp.getString("compress_gallery_photo_" + i, ""));
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(fileGallery));
                    String path = sp.getString("compress_gallery_photo_" + i, "");
                    Uri uri = Uri.parse(path);
                    uriArr.add(uri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

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
                                byte[] img = UploadGallery.ReadFileOrSaveInDeviceGallery(requireContext(), getUri, uriArr.size());
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
                            byte[] img = UploadGallery.ReadFileOrSaveInDeviceGallery(requireContext(), getUri, uriArr.size());
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

        profileSetting.setOnClickListener(view -> FragmentReplace.replaceFragmentParent(new Setting(), requireActivity()));
        editProfile.setOnClickListener(view -> FragmentReplace.replaceFragmentParent(new Redactor(), requireActivity()));

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
            avatarProfile.setImageURI(uri);

            try {
                byte[] img = UploadAvatar.ReadFileOrSaveInDevice(requireContext(), uri.getPath(), 100);
                byte[] compress_img = UploadAvatar.ReadFileOrSaveInDevice(requireContext(), uri.getPath(), 10);

                UploadAvatar UploadPhoto = new UploadAvatar(requireContext(), img, compress_img, UPLOAD_AVATAR_PHP);
                UploadPhoto.execute();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
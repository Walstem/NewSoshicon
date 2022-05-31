package com.bulat.soshicon2.Registration;

import static com.bulat.soshicon2.constants.constants.*;

import com.bulat.soshicon2.Toasts.Toasts;
import com.bulat.soshicon2.checks.FragmentReplace;
import com.bulat.soshicon2.constants.constants;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bulat.soshicon2.BottomNavigation.event.Event;
import com.bulat.soshicon2.BottomNavigation.event.receivingEvent;
import com.bulat.soshicon2.R;
import com.bulat.soshicon2.SQLUtils.SQLUtils;
import com.bulat.soshicon2.asynctasks.SendQuery;
import com.bulat.soshicon2.checks.NetCheck;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Authorization extends Fragment {
    public static final String GET_PHOTOS_GALLERY_PHP = "get_photos_gallery.php";
    private String Avatar;
    private String CompressAvatar;
    private String UserId;
    private ArrayList<String> GalleryPhotos = new ArrayList<>();
    public static final String GET_AVATAR_PHP = "get_avatar_aut.php";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View MainView = inflater.inflate(R.layout.authorization, container, false);

        //Выключение bottom navigation
        BottomNavigationView navigationView = requireActivity().findViewById(R.id.bottom_navigation);
        navigationView.setVisibility(View.GONE);

        //Кнопка регистрации
        TextView tv_registration = (TextView) MainView.findViewById(R.id.tv_registration);
        tv_registration.setOnClickListener(v -> registration(MainView));

        //Кнопка авторизации
        Button authorization_enter = MainView.findViewById(R.id.authorization_enter);
        authorization_enter.setOnClickListener(v -> authorization(MainView));

        return MainView;
    }

    //Авторизация
    public void authorization(View view){
        if (NetCheck.StatusConnection(requireContext())) {
            LayoutInflater lnInflater = getLayoutInflater();
            View ToastId = view.findViewById(R.id.toast_layout_root);
            Toasts InternetToast  = new Toasts(getContext(), lnInflater, ToastId);
            InternetToast.ViewInterntEror(view);
        }
        else {

            try {
                //получаем данные пользователя
                TextInputLayout ed_login = view.findViewById(R.id.login);
                TextInputLayout ed_password = view.findViewById(R.id.password);
                String login = ed_login.getEditText().getText().toString();
                //преобразуем пароль в хэш
                String password =  toHexString(getSHA(ed_password.getEditText().getText().toString()));

                //выполняем запрос в базу данных
                String UserData = new SQLUtils(login, password).Authorization();

                SendQuery request = new SendQuery("authorization.php");
                request.execute(UserData);
                String getData = request.get();
                //если данные верны, переводим на главную страницу
                if (getData.equals("true")){

                    //получаем id пользователя
                    SendQuery request_id = new SendQuery("get_id.php");
                    request_id.execute("?name="+login);
                    String id =  request_id.get();

                    // добавляем id пользователя в сохраненные настрйоки
                    SharedPreferences sPref = requireActivity().getSharedPreferences(constants.DATABASE, 0);
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putString(constants.ID, id);
                    ed.putString(U_NICKNAME, login);
                    ed.apply();

                    //загружаем аватар
                    UserId = sPref.getString(ID, "");
                    String[] KeyArgs = {"id"};
                    String[] Args = {UserId};

                    receivingEvent Query = new receivingEvent(GET_AVATAR_PHP, KeyArgs, Args);
                    Query.execute();

                    JSONArray Event_json = new JSONArray(Query.get());
                    //уменьшаем количество записей оставшихся в таблице

                    //добавляем данные в массивы
                    for (int i = 0; i < Event_json.length(); i++) {
                        JSONObject jo = new JSONObject((String) Event_json.get(i));
                        CompressAvatar = (String) jo.get("compress_avatar");
                        Avatar = (String) jo.get("avatar");
                    }
                    String compressPathTrue = requireContext().getFilesDir() + "avatar_compress_" + true + ".jpg";
                    String compressPathFalse = requireContext().getFilesDir() + "avatar_compress_" + false + ".jpg";
                    FileOutputStream outTrue = new FileOutputStream(compressPathTrue);
                    FileOutputStream outFalse = new FileOutputStream(compressPathFalse);

                    byte [] encodeByte1 = Base64.decode(CompressAvatar,Base64.DEFAULT);
                    Bitmap bitmap1 = BitmapFactory.decodeByteArray(encodeByte1, 0, encodeByte1.length);
                    byte [] encodeByte2 = Base64.decode(Avatar,Base64.DEFAULT);
                    Bitmap bitmap2 = BitmapFactory.decodeByteArray(encodeByte2, 0, encodeByte2.length);

                    bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, outTrue);
                    bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, outFalse);

                    ed.putString("compress_avatar_" + true, compressPathTrue);
                    ed.putString("compress_avatar_" + false, compressPathFalse);
                    ed.apply();

                    //загружаем фотографии
                    String[] KeyArgsGallery = {"id"};
                    String[] ArgsGallery = {UserId};


                    try {
                        receivingEvent QueryGallery = new receivingEvent(GET_PHOTOS_GALLERY_PHP, KeyArgsGallery, ArgsGallery);
                        QueryGallery.execute();

                        JSONArray EventJsonGallery = new JSONArray(QueryGallery.get());
                        System.out.println(EventJsonGallery);

                        //добавляем данные в массивы
                        for (int i = 0; i < EventJsonGallery.length(); i++) {
                            JSONObject jo = new JSONObject((String) EventJsonGallery.get(i));
                            GalleryPhotos.add((String) jo.get("gallery_image"));
                        }
                        for (int i = 0; i < GalleryPhotos.size(); i++) {
                            String compressPath = requireContext().getFilesDir() + "/gallery_photo_compress_" + i + ".jpg";
                            System.out.println(compressPath);

                            FileOutputStream outGallery = new FileOutputStream(compressPath);

                            byte[] encodeByte = Base64.decode(GalleryPhotos.get(i), Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outGallery);

                            //запись пути к сжатой фотографии
                            ed.putString("compress_gallery_photo_" + i, compressPath);
                            System.out.println(sPref.getString("compress_gallery_photo_" + i, ""));
                            ed.apply();
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }


                    FragmentReplace.replaceFragmentParent(new Event(), getActivity());
                }
                // если сообщения ложные выводим сообщение об ошибке
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

    //Проверка на наличие интернета, если он есть, начинается регистрация
    public void registration(View view) {
        if (NetCheck.StatusConnection(requireContext())) {
            LayoutInflater lnInflater = getLayoutInflater();
            View ToastId = view.findViewById(R.id.toast_layout_root);
            Toasts InternetToast  = new Toasts(getContext(), lnInflater, ToastId);
            InternetToast.ViewInterntEror(view);
        }
        else {
            FragmentReplace.replaceFragmentParent(new RegistrationName(), getActivity());
        }
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

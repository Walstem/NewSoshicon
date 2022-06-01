package com.bulat.soshicon2.BottomNavigation.anotherAccount;

import static com.bulat.soshicon2.constants.constants.*;
import static com.bulat.soshicon2.constants.constants.ID;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bulat.soshicon2.BottomNavigation.account.Account;
import com.bulat.soshicon2.BottomNavigation.account.RecyclerAdapter;
import com.bulat.soshicon2.BottomNavigation.event.Event;
import com.bulat.soshicon2.BottomNavigation.event.receivingEvent;
import com.bulat.soshicon2.R;
import com.bulat.soshicon2.checks.FragmentReplace;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class AnotherAccount extends Fragment {
    View view;
    SharedPreferences sPref;

    private String Avatar;
    private ArrayList<String> GalleryPhotos = new ArrayList<>();
    ArrayList<Uri> uris = new ArrayList<>();
    RecyclerAdapter adapter;
    RecyclerView recyclerView;
    JSONObject jo;

    public static final String GET_AVATAR_PHP = "get_avatar.php";
    public static final String GET_PHOTOS_GALLERY_PHP = "get_photos_gallery.php";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.another_account, container, false);
        Bundle bundle = this.getArguments();
        sPref = getContext().getSharedPreferences(DATABASE, 0);

        TextView UserName = view.findViewById(R.id.username);
        ImageView AnotherAvatar = view.findViewById(R.id.profile_avatar);
        recyclerView = view.findViewById(R.id.gallery_images);
        ImageView back = view.findViewById(R.id.back);

        back.setOnClickListener(v -> {
            FragmentReplace.replaceFragmentParent(new Event(), requireActivity());
        });


        assert bundle != null;
        UserName.setText(bundle.getString("CreatorName"));

        //Совершаем запрос на получение аватара и фотграфий из галлерии
        String[] KeyArgs = {"id"};
        String[] Args = {bundle.getString("CreatorId")};

        receivingEvent Query = new receivingEvent(GET_AVATAR_PHP, KeyArgs, Args);
        Query.execute();
        JSONArray Event_json = null;
        try {
            Event_json = new JSONArray(Query.get());
        } catch (JSONException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        //добавляем данные из json в массив фотографий и в переменную аватар
            try {
                //парсим данные
                jo = new JSONObject((String) Event_json.get(0));
                Avatar = (String) jo.get("avatar");

                if (Avatar != null || !Avatar.equals("null")){
                    //декодируем строку аватара в bitmap, устанавливаем аватар
                    byte [] encodeByte = Base64.decode(Avatar,Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                    AnotherAvatar.setImageBitmap(bitmap);
                }

                //добавляем данные фото в список

            } catch (JSONException e) {
                e.printStackTrace();
            }

        receivingEvent QueryGallery = new receivingEvent(GET_PHOTOS_GALLERY_PHP, KeyArgs, Args);
        QueryGallery.execute();
        try {
            JSONArray Event_json_gallery = new JSONArray(QueryGallery.get());

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
        return view;
    }
}

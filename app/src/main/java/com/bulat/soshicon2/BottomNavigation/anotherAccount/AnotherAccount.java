package com.bulat.soshicon2.BottomNavigation.anotherAccount;

import static com.bulat.soshicon2.constants.constants.DATABASE;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bulat.soshicon2.BottomNavigation.account.RecyclerAdapter;
import com.bulat.soshicon2.BottomNavigation.chat.Chat;
import com.bulat.soshicon2.BottomNavigation.event.Event;
import com.bulat.soshicon2.BottomNavigation.event.receivingEvent;
import com.bulat.soshicon2.BottomNavigation.response.Response;
import com.bulat.soshicon2.R;
import com.bulat.soshicon2.checks.FragmentReplace;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class AnotherAccount extends Fragment {
    View view;
    SharedPreferences sPref;

    private String Avatar;
    private ArrayList<String> GalleryPhotos = new ArrayList<>();
    ArrayList<Uri> uris = new ArrayList<>();
    Uri uriAvatar;
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
            if (bundle.getString("page").equals("Event")){
                FragmentReplace.replaceFragmentParent(new Event(), requireActivity());
            }
            else if (bundle.getString("page").equals("Response")) {
                FragmentReplace.replaceFragmentParent(new Response(), requireActivity());

            }
            else if (bundle.getString("page").equals("chat")) {
                FragmentReplace.replaceFragmentParent(new Chat(), requireActivity());

            }
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

            //парсим данные

            try
            {
                jo = new JSONObject((String) Event_json.get(0));
                Avatar = (String) jo.get("avatar");

                if (Avatar != null){
                    //byte [] encodeByte = Base64.decode(GalleryPhotos.get(i),Base64.DEFAULT);
                    Uri bitmap =  Uri.parse ("http://j911147y.beget.tech/"+Avatar);

                    Glide.with(getContext())
                            .load(bitmap)
                            .into(AnotherAvatar);
                }
            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
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

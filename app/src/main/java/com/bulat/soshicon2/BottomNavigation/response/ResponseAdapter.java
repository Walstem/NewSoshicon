package com.bulat.soshicon2.BottomNavigation.response;

import static com.bulat.soshicon2.constants.constants.DATABASE;
import static com.bulat.soshicon2.constants.constants.ID;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bulat.soshicon2.BottomNavigation.anotherAccount.AnotherAccount;
import com.bulat.soshicon2.R;
import com.bulat.soshicon2.asynctasks.SendQuery;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

class ResponseAdapter extends ArrayAdapter<String> {
    public static final String INPUT_CHAT_PHP = "input_chat.php";
    public static final String DELETE_RESPONSE_PHP = "delete_response.php";
    public static final String CHECK_FOR_DUPLICATES_CHAT_PHP = "check_for_duplicates_chat.php";
    Context context;
    View view;

    private ArrayList<String> CreatorId;
    private ArrayList<String> ResponceId;
    private ArrayList<String> Discription;
    private ArrayList<String> Title;
    private ArrayList<String> Avatar ;
    private ArrayList<String> EventID;
    private ArrayList<String> Time;

    FragmentActivity activity;

    String id;

    public ResponseAdapter(@NonNull Context context,  View view, ArrayList<String> CreatorId, ArrayList<String> ResponceId,  ArrayList<String> Discription, ArrayList<String> Title, ArrayList<String> Avatar, ArrayList<String> EventID, ArrayList<String> Time, FragmentActivity activity) {
        super(context, R.layout.row_card_event, R.id.NameMessage, Title);
        this.context = context;
        this.CreatorId = CreatorId;
        this.ResponceId = ResponceId;
        this.Discription = Discription;
        this.Title = Title;
        this.Avatar = Avatar;
        this.EventID = EventID;
        this.Time = Time;
        this.view = view;

        this.activity = activity;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.row_card_response, parent, false);
        SharedPreferences sp = getContext().getSharedPreferences(DATABASE, 0);


        TextView NameMessage = row.findViewById(R.id.Name);
        TextView Content = row.findViewById(R.id.Content);
        ImageView avatar = row.findViewById(R.id.avatar);
        TextView time = row.findViewById(R.id.Time);
        ImageView cancelBtn = row.findViewById(R.id.cancel);
        ImageView acceptBtn = row.findViewById(R.id.accept);

        id = sp.getString(ID,"");


        if (!Avatar.get(position).equals("null")){

            Glide.with(context)
                    .load(Avatar.get(position))
                    .into(avatar);

        }

        NameMessage.setText(Title.get(position));
        Content.setText(Discription.get(position));
        time.setText(Time.get(position));

        //переход на профиль пользователя
        avatar.setOnClickListener(v -> {
            String EventCreatorId = CreatorId.get(position);
            if (!EventCreatorId.equals(id)){
                Fragment AnotherAccount = new AnotherAccount();
                Bundle bundle = new Bundle();
                bundle.putString("CreatorId", CreatorId.get(position));
                bundle.putString("CreatorName", Title.get(position));
                bundle.putString("page", "Response");
                AnotherAccount.setArguments(bundle);

                FragmentManager fragmentManager =  activity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, AnotherAccount);
                fragmentTransaction.commit();
            }
            else{
                BottomNavigationView navBar = activity.findViewById(R.id.bottom_navigation);
                navBar.setSelectedItemId(R.id.nav_account);
            }

        });

        //прослушиваем нажатие на кнопку принять запрос
        acceptBtn.setOnClickListener(v ->{


            String pattern = "yyyy-MM-dd-HH-mm";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String timeChat = simpleDateFormat.format(new Date());

            SendQuery sendQuery = new SendQuery(INPUT_CHAT_PHP);
            sendQuery.execute("?user_id=" + id + "&another_user_id=" + CreatorId.get(position) + "&res_id=" + ResponceId.get(position) + "&time=" + timeChat);


            CreatorId.remove(position);
            ResponceId.remove(position);
            Discription.remove(position);
            Title.remove(position);
            Avatar.remove(position);
            EventID.remove(position);
            Time.remove(position);

            ListView listView = view.findViewById(R.id.listView);
            ResponseAdapter eventBlock = new ResponseAdapter(getContext(), view, CreatorId, ResponceId, Discription, Title, Avatar,EventID, Time, activity);
            listView.setAdapter(eventBlock);

        });

        //прослушиваем нажатие на кнопку отклонить запрос
        cancelBtn.setOnClickListener(v ->{
            SendQuery sendQuery = new SendQuery(DELETE_RESPONSE_PHP);
            sendQuery.execute("?res_id=" + ResponceId.get(position));

            CreatorId.remove(position);
            ResponceId.remove(position);
            Discription.remove(position);
            Title.remove(position);
            Avatar.remove(position);
            EventID.remove(position);
            Time.remove(position);

            ListView listView = view.findViewById(R.id.listView);
            ResponseAdapter eventBlock = new ResponseAdapter(getContext(), view, CreatorId, ResponceId, Discription, Title, Avatar,EventID, Time, activity);
            listView.setAdapter(eventBlock);
        });
        return row;
    }
}
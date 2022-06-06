package com.bulat.soshicon2.BottomNavigation.chat;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bulat.soshicon2.BottomNavigation.anotherAccount.AnotherAccount;
import com.bulat.soshicon2.R;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

class ChatAdapter extends ArrayAdapter<String> {
    Context context;
    View view;

    private ArrayList<String> ChatsId;
    private ArrayList<String> User_Id;
    private ArrayList<String> AnotherUserId;
    private ArrayList<String> Nickname;
    private ArrayList<String> LastMessage;
    private ArrayList<String> Avatar;
    private ArrayList<String> TimeChat;

    FragmentActivity activity;

    String id;

    public ChatAdapter(@NonNull Context context, View view, ArrayList<String> ChatsId, ArrayList<String> User_Id, ArrayList<String> AnotherUserId, ArrayList<String> Nickname, ArrayList<String> Avatar, ArrayList<String> TimeChat, ArrayList<String> LastMessage, FragmentActivity activity) {
        super(context, R.layout.row_card_event, R.id.NameMessage, Nickname);
        this.context = context;
        this.ChatsId = ChatsId;
        this.User_Id = User_Id;
        this.AnotherUserId = AnotherUserId;
        this.Nickname = Nickname;
        this.Avatar = Avatar;
        this.TimeChat = TimeChat;
        this.LastMessage = LastMessage;
        this.view = view;

        this.activity = activity;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.row_chat, parent, false);
        SharedPreferences sp = getContext().getSharedPreferences(DATABASE, 0);


        TextView NameMessage = row.findViewById(R.id.Name);
        TextView Content = row.findViewById(R.id.Content);
        ImageView avatar = row.findViewById(R.id.avatar);
        TextView time = row.findViewById(R.id.Time);

        id = sp.getString(ID,"");


        if (!Avatar.get(position).equals("null")){

            Glide.with(context)
                    .load(Avatar.get(position))
                    .into(avatar);

        }

        NameMessage.setText(Nickname.get(position));
        Content.setText(LastMessage.get(position));
        time.setText(TimeChat.get(position));

        //переход на профиль пользователя
        avatar.setOnClickListener(v -> {
            String EventCreatorId = AnotherUserId.get(position);
            if (!EventCreatorId.equals(id)){
                Fragment AnotherAccount = new AnotherAccount();
                Bundle bundle = new Bundle();
                bundle.putString("CreatorId", AnotherUserId.get(position));
                bundle.putString("CreatorName", Nickname.get(position));
                bundle.putString("page", "Chat");
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

        return row;
    }
}
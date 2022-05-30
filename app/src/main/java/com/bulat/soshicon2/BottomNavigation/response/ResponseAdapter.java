package com.bulat.soshicon2.BottomNavigation.response;

import static com.bulat.soshicon2.constants.constants.DATABASE;
import static com.bulat.soshicon2.constants.constants.ID;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

class ResponseAdapter extends ArrayAdapter<String> {
    Context context;

    private ArrayList<String> CreatorId;
    private ArrayList<String> ResponceId;
    private ArrayList<String> Discription;
    private ArrayList<String> Title;
    private ArrayList<String> Avatar ;
    private ArrayList<String> EventID;
    private ArrayList<String> Time;

    FragmentActivity activity;

    public ResponseAdapter(@NonNull Context context, ArrayList<String> CreatorId, ArrayList<String> ResponceId,  ArrayList<String> Discription, ArrayList<String> Title, ArrayList<String> Avatar, ArrayList<String> EventID, ArrayList<String> Time, FragmentActivity activity) {
        super(context, R.layout.row_card_event, R.id.NameMessage, Title);
        this.context = context;
        this.CreatorId = CreatorId;
        this.ResponceId = ResponceId;
        this.Discription = Discription;
        this.Title = Title;
        this.Avatar = Avatar;
        this.EventID = EventID;
        this.Time = Time;

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



        if (!Avatar.get(position).equals("null")){
            byte [] encodeByte = Base64.decode(Avatar.get(position),Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            avatar.setImageBitmap(bitmap);
        }

        NameMessage.setText(Title.get(position));
        Content.setText(Discription.get(position));
        time.setText(Time.get(position));

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = sp.getString(ID,"");
                String EventCreatorId = CreatorId.get(position);
                if (!EventCreatorId.equals(id)){
                    Fragment AnotherAccount = new AnotherAccount();
                    Bundle bundle = new Bundle();
                    bundle.putString("CreatorId", CreatorId.get(position));
                    bundle.putString("CreatorName", Title.get(position));
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

            }
        });
        return row;
    }
}
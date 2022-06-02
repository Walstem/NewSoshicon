package com.bulat.soshicon2.BottomNavigation.event;

import static com.bulat.soshicon2.constants.constants.DATABASE;
import static com.bulat.soshicon2.constants.constants.ID;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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

class EventAdapter extends ArrayAdapter<String> {
    public static final String INPUT_LIKED_EVENT_PHP = "input_liked_event.php";
    public static final String INPUT_UNLIKED_EVENT_PHP = "input_unliked_event.php";
    Context context;

    ArrayList<String> CreatorId;
    ArrayList<String> EventId;
    ArrayList<String> Title;
    ArrayList<String> Discription;
    ArrayList<String> Avatar;
    ArrayList<String> Time;
    ArrayList<String> Distance;
    ArrayList<Boolean> IsLiked;
    FragmentActivity activity;

    public EventAdapter(@NonNull Context context, ArrayList<String> Title, ArrayList<String> Discription, ArrayList<String> Avatars, ArrayList<String> Time, ArrayList<String> Distance, ArrayList<String> Id, ArrayList<Boolean> IsLiked,  ArrayList<String> CreatorId, FragmentActivity activity) {
        super(context, R.layout.row_card_event, R.id.NameMessage, Title);
        this.context = context;
        this.Title = Title;
        this.Discription = Discription;
        this.Avatar = Avatars;
        this.Time = Time;
        this.Distance = Distance;
        this.EventId = Id;
        this.IsLiked=IsLiked;
        this.CreatorId = CreatorId;
        this.activity = activity;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.row_card_event, parent, false);
        SharedPreferences sp = getContext().getSharedPreferences(DATABASE, 0);
        TextView NameMessage = row.findViewById(R.id.NameMessage);
        TextView Content = row.findViewById(R.id.ContentMessage);
        ImageView avatar = row.findViewById(R.id.avatar);
        TextView time = row.findViewById(R.id.Time);
        TextView distance = row.findViewById(R.id.distance);
        CheckBox like = row.findViewById(R.id.like);

        like.setChecked(IsLiked.get(position));



        if (!Avatar.get(position).equals("null")){

            Glide.with(context)
                    .load(Avatar.get(position))
                    .into(avatar);

        }
        if (Distance.get(position) != null){
            distance.setText(Distance.get(position));
        }
        NameMessage.setText(Title.get(position));
        Content.setText(Discription.get(position));
        time.setText(Time.get(position));

        ColorStateList colorStateList = new ColorStateList(
                new int[][] {
                        new int[] { -android.R.attr.state_checked }, // unchecked
                        new int[] {  android.R.attr.state_checked }  // checked
                },
                new int[] {
                        getContext().getColor(R.color.text_color_primary),
                        getContext().getColor(R.color.splash_screen_bg)
                }
        );
        //переход на профиль пользователя
        avatar.setOnClickListener(v -> {
            String id = sp.getString(ID,"");
            String EventCreatorId = CreatorId.get(position);
            if (!EventCreatorId.equals(id)){
                Fragment AnotherAccount = new AnotherAccount();
                Bundle bundle = new Bundle();
                bundle.putString("CreatorId", CreatorId.get(position));
                bundle.putString("CreatorName", Title.get(position));
                bundle.putString("page", "Event");
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
        like.setButtonTintList(colorStateList);
        like.setOnClickListener(v -> {



            if(like.isChecked()){
                like.setButtonDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_liked));
                if (!sp.getString(ID, "").equals(EventId.get(position))){

                    String UserID = sp.getString(ID,"");
                    String EventID =  EventAdapter.this.EventId.get(position);

                    String pattern = "yyyy-MM-dd-HH-mm";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    String time1 = simpleDateFormat.format(new Date());

                    SendQuery query = new SendQuery(INPUT_LIKED_EVENT_PHP);
                    query.execute("?user_id="+UserID + "&event_id=" + EventID + "&id_liked_user=" + CreatorId.get(position) + "&time=" + time1);
                }
            }
            else{
                like.setButtonDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite));
                if (!sp.getString(ID, "").equals(EventId.get(position))){
                    SendQuery query = new SendQuery(INPUT_UNLIKED_EVENT_PHP);
                    String UserID = sp.getString(ID,"");
                    query.execute("?user_id="+UserID + "&event_id=" + EventAdapter.this.EventId.get(position));
                }
            }

        });

        return row;
    }
}
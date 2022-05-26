package com.bulat.soshicon2.BottomNavigation.event;

import static com.bulat.soshicon2.constants.constants.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
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

import com.bulat.soshicon2.R;
import com.bulat.soshicon2.asynctasks.SendQuery;

import java.util.ArrayList;

class EventAdapter extends ArrayAdapter<String> {
    Context context;
    ArrayList<String> Title;
    ArrayList<String> Discription;
    ArrayList<String> Avatar;
    ArrayList<String> Time;
    ArrayList<String> Distance;
    ArrayList<String> Id;

    public EventAdapter(@NonNull Context context, ArrayList<String> Title, ArrayList<String> Discription, ArrayList<String> Avatars, ArrayList<String> Time, ArrayList<String> Distance, ArrayList<String> Id) {
        super(context, R.layout.row_card_event, R.id.NameMessage, Title);
        this.context = context;
        this.Title = Title;
        this.Discription = Discription;
        this.Avatar = Avatars;
        this.Time = Time;
        this.Distance = Distance;
        this.Id = Distance;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.row_card_event, parent, false);
        SharedPreferences sp = getContext().getSharedPreferences(DATABASE, 0);
        TextView EventId = row.findViewById(R.id.event_id);
        TextView NameMessage = row.findViewById(R.id.NameMessage);
        TextView Content = row.findViewById(R.id.ContentMessage);
        ImageView avatar = row.findViewById(R.id.avatar);
        TextView time = row.findViewById(R.id.Time);
        TextView distance = row.findViewById(R.id.distance);
        CheckBox like = row.findViewById(R.id.like);
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(like.isChecked()){
                    like.setButtonDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_liked));
                    if (!sp.getString(ID, "").equals(EventId.getText().toString())){
                        SendQuery query = new SendQuery("");
                    }
                }
                else{
                    like.setButtonDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite));
                    if (!sp.getString(ID, "").equals(EventId.getText().toString())){

                    }
                }

            }
        });

        if (!Avatar.get(position).equals("null")){
            byte [] encodeByte = Base64.decode(Avatar.get(position),Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            avatar.setImageBitmap(bitmap);
        }
        if (Distance.get(position) != null){
            distance.setText(Distance.get(position));
        }
        NameMessage.setText(Title.get(position));
        Content.setText(Discription.get(position));
        time.setText(Time.get(position));
        EventId.setText(Id.get(position));
        return row;
    }
}
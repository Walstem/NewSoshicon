package com.bulat.soshicon2.BottomNavigation.event;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bulat.soshicon2.R;

import java.util.ArrayList;

class EventBlock extends ArrayAdapter<String> {
    Context context;
    ArrayList<String> Title;
    ArrayList<String> Discription;
    ArrayList<String> Avatars;

    public EventBlock(@NonNull Context context, ArrayList<String> Title, ArrayList<String> Discription, ArrayList<String> Avatars) {
        super(context, R.layout.row_card_event, R.id.NameMessage, Title);
        this.context = context;
        this.Title = Title;
        this.Discription = Discription;
        this.Avatars = Avatars;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.row_card_event, parent, false);
        TextView NameMessage = row.findViewById(R.id.NameMessage);
        TextView Content = row.findViewById(R.id.ContentMessage);
        ImageView avatar = row.findViewById(R.id.avatar);
        if (Avatars.get(position) != "null"){
            byte [] encodeByte = Base64.decode(Avatars.get(position),Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

            avatar.setImageBitmap(bitmap);
        }

        NameMessage.setText(Title.get(position));
        Content.setText(Discription.get(position));

        return row;
    }
}
package com.bulat.soshicon2.BottomNavigation.account;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bulat.soshicon2.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<Uri> uriArrayList;
    private boolean FlagUri = true;
    ArrayList<Bitmap> bitmaps = new ArrayList<>();
    Context context;

    public RecyclerAdapter(ArrayList<Uri> uriArrayList, Context context) {
        this.uriArrayList = uriArrayList;
        this.context=context;
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.image_single, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, int position) {
        if (FlagUri){
            //holder.imageView.setImageURI(uriArrayList.get(position));
            Uri uri = uriArrayList.get(position);
            Glide.with(context)
                    .load(uri)
                    .into(holder.imageView);
        }
        else{
            holder.imageView.setImageBitmap(bitmaps.get(position));
        }

    }

    @Override
    public int getItemCount() {
        int size;
        if (FlagUri){
            size = uriArrayList.size();
        }
        else{
            size = bitmaps.size();
        }
        return size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView, delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_recycler);
            delete = itemView.findViewById(R.id.delete_image);
        }
    }
}
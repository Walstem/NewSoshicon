package com.bulat.soshicon2.BottomNavigation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bulat.soshicon2.R;
import com.bulat.soshicon2.Registration.Authorization;
import com.bulat.soshicon2.Registration.RegistrationPassword;
import com.bulat.soshicon2.account.Setting;
import com.bulat.soshicon2.event.Fragment_Add;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import de.hdodenhof.circleimageview.CircleImageView;

public class Account extends Fragment {

    CircleImageView profile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.account, container, false);

        SharedPreferences sp = getActivity().getSharedPreferences("user_data", getContext().MODE_PRIVATE);
        TextView name = view.findViewById(R.id.username_bottom_avatar);
        BottomSheetDialogFragment BottomSheet = new Setting();
        ImageView account_setting = view.findViewById(R.id.account_edit);
        profile = (CircleImageView) view.findViewById(R.id.profile_avatar);

        name.setText(sp.getString("U_NICKNAME", ""));

        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation);
        navBar.setVisibility(view.VISIBLE);

        account_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheet.show(getFragmentManager().beginTransaction(), "BottomSheetDialog");
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(Account.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(600, 600)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri uri = data.getData();
        profile.setImageURI(uri);
        System.out.println(uri.getPath());
        try {
            byte[] img = ReadFile(uri.getPath());
            System.out.println(img.length);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public byte[] ReadFile(String filename) throws FileNotFoundException {

        File file = new File(filename);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        byte[] byteArray = stream.toByteArray();
        bitmap.recycle();
        return byteArray;
    }
}
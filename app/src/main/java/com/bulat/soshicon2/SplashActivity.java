package com.bulat.soshicon2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bulat.soshicon2.Registration.Authorization;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            int a = 1;
            SharedPreferences sp = getSharedPreferences("UserData", MODE_PRIVATE);
            String id = sp.getString("ID", "");
            Intent intent;

            if (id.equals("0")){
                intent = new Intent(this, Authorization.class);
            }
            else{
                intent = new Intent(this, MainActivity.class);
            }
            startActivity(intent);
        }
        catch (Exception e){
            Intent intent = new Intent(this, Authorization.class);
            startActivity(intent);
            System.out.println("Error");
        }
        this.finish();
    }
}

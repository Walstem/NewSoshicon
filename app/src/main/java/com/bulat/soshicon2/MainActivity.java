package com.bulat.soshicon2;

import static com.bulat.soshicon2.constants.constants.*;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bulat.soshicon2.BottomNavigation.Account;
import com.bulat.soshicon2.BottomNavigation.Chat;
import com.bulat.soshicon2.BottomNavigation.Event;
import com.bulat.soshicon2.BottomNavigation.Response;
import com.bulat.soshicon2.Registration.Authorization;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        SharedPreferences sp = getSharedPreferences(DATABASE, MODE_PRIVATE);
        String id = sp.getString(ID, "");


        if (id.equals("0")){
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main, new Authorization()).commit();
        }
        else{
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main, new Event()).commit();
        }


        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            //depending on the button pressed, a fragment (page) is selected
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.nav_event:
                        fragment = new Event();
                        break;
                    case R.id.nav_response:
                        fragment = new Response();
                        break;
                    case R.id.nav_chat:
                        fragment = new Chat();
                        break;
                    case R.id.nav_account:
                        fragment = new Account();
                        break;
                }

                //change the current page to the selected one
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main, fragment).commit();
                return true;
            }
        });
    }

    public void setArguments(Bundle bundle) {

    }
}

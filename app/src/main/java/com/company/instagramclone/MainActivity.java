package com.company.instagramclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.company.instagramclone.fragment.HomeFragment;
import com.company.instagramclone.fragment.NotificationFragment;
import com.company.instagramclone.fragment.ProfileFragment;
import com.company.instagramclone.fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Fragment selectorFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_nav_view);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.nav_home)
                    selectorFragment=new HomeFragment();
                if(item.getItemId()==R.id.nav_search)
                    selectorFragment=new SearchFragment();
                if(item.getItemId()==R.id.nav_profile)
                {
                    Bundle intent  = getIntent().getExtras();
                    getSharedPreferences("PROFILE",MODE_PRIVATE).edit().putString("profileId","NONE").apply();
                    selectorFragment=new ProfileFragment();
                }

                if(item.getItemId()==R.id.nav_heart)
                    selectorFragment=new NotificationFragment();
                if(item.getItemId()==R.id.nav_add)
                {
                    selectorFragment=null;
                    startActivity(new Intent(MainActivity.this,PostActivity.class));
                }


                if(selectorFragment!=null)
                {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectorFragment).commit();
                }
                return  true;

            }
        });

        Bundle intent  = getIntent().getExtras();
        if (intent!=null)
        {
            String profileId = intent.getString("publisherId");
            getSharedPreferences("PROFILE",MODE_PRIVATE).edit().putString("profileId",profileId).apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
            bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        }
        else
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
        }




    }
}

/*
 * Created by Sue on 07/10/2019
 * Edited by Sue on 11/10/2019
 * */

package com.example.missyou;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView navigation;
    FrameLayout frameLayout;

    // Fragments
    private HomeFragment homeFragment;
    private LostFragment lostFragment;
    private ReportFragment reportFragment;
    private SettingsFragment settingsFragment;

    private FirebaseAuth mFirebaseAuth;
    private TextView tvSample;
    private Button btnLogout;
    private Button btnnewpost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();

        navigation = findViewById(R.id.bottomNavigationView);
        frameLayout = findViewById(R.id.frameLayout);

        // fragment initialization
        homeFragment = new HomeFragment();
        lostFragment = new LostFragment();
        reportFragment = new ReportFragment();
        settingsFragment = new SettingsFragment();

        btnLogout = findViewById(R.id.btnLogout);
        btnnewpost = findViewById(R.id.btnNewpost);

        InitializeFragments(homeFragment);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                // Switch to select which case is chosen :
                switch (menuItem.getItemId()){

                    // code to be executed when single item is selected
                    case R.id.navigation_home :
                        InitializeFragments(homeFragment);
                        return true;

                    case R.id.navigation_search :
                        InitializeFragments(lostFragment);
                        return true;

                    case R.id.navigation_report:
                        InitializeFragments(reportFragment);
                        return true;

                    case R.id.navigation_settings:
                        InitializeFragments(settingsFragment);
                        return true;
                }
                return false;
            }
        });

        // If the user is not logged in
        if (mFirebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAuth.signOut();
                finish();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
       });

    }

    public void goSettings (View view) {
        // Do something in response to button
        Intent intent = new Intent(this,Settings.class);
        startActivity(intent);
    }


    public void newPost (View view) {
        //Do something in response to button
        Intent intent = new Intent(this, NewPostActivity.class);
        startActivity(intent);
    }

    private void InitializeFragments(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit(); // save changes
    }


}

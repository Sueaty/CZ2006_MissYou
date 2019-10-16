
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView navigation;
    FrameLayout frameLayout;
    private Animation report_open, report_close;

    // Fragments
    private HomeFragment homeFragment;
    private LostFragment lostFragment;
    private SettingsFragment settingsFragment;

    private FirebaseAuth mFirebaseAuth;
    private FloatingActionButton drawer, reportLost, reportFound;
    private Boolean isReportOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        report_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.report_open);
        report_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.report_close);
        mFirebaseAuth = FirebaseAuth.getInstance();

        navigation = findViewById(R.id.bottomNavigationView);
        frameLayout = findViewById(R.id.frameLayout);

        // fragment initialization
        homeFragment = new HomeFragment();
        lostFragment = new LostFragment();
        settingsFragment = new SettingsFragment();

        drawer = findViewById(R.id.floatingActionButton);
        reportLost = findViewById(R.id.lostReportButton);
        reportFound = findViewById(R.id.foundReportButton);

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

                    case R.id.navigation_settings:
                        InitializeFragments(settingsFragment);
                        return true;
                }
                return false;
            }
        });

        drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animation();
            }
        });


        // If the user is not logged in
        if (mFirebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }


    private void InitializeFragments(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit(); // save changes
    }

    private void animation(){
        if (isReportOpen){
            reportFound.startAnimation(report_close);
            reportLost.startAnimation(report_close);
            reportFound.setClickable(false);
            reportLost.setClickable(false);
            isReportOpen = false;
        }
        else{
            reportFound.startAnimation(report_open);
            reportLost.startAnimation(report_open);
            reportFound.setClickable(true);
            reportLost.setClickable(true);
            isReportOpen = true;
        }
    }

}

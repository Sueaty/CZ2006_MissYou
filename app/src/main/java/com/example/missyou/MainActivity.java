
/*
 * Created by Sue on 07/10/2019
 * Edited by Sue on 11/10/2019
 * */

package com.example.missyou;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView navigation;
    FrameLayout frameLayout;
    private Animation report_open, report_close;

    // Fragments
    private HomeFragment homeFragment;
    private LostFragment lostFragment;
    private CenterFragment mapFragment;
    private SettingsFragment settingsFragment;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser currentUser;
    private FloatingActionButton drawer, reportLost, reportFound;
    private TextView tvLost, tvFound;
    private Boolean isReportOpen = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        report_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.report_open);
        report_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.report_close);
        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = mFirebaseAuth.getCurrentUser();

        navigation = findViewById(R.id.bottomNavigationView);
        frameLayout = findViewById(R.id.frameLayout);

        // fragment initialization
        homeFragment = new HomeFragment();
        lostFragment = new LostFragment();
        mapFragment = new CenterFragment();
        settingsFragment = new SettingsFragment();

        drawer = findViewById(R.id.floatingActionButton);
        reportLost = findViewById(R.id.btnLostReport);
        reportFound = findViewById(R.id.btnFoundReport);
        tvLost= findViewById(R.id.tvLostReport);
        tvFound = findViewById(R.id.tvFoundReport);

        final AlertDialog.Builder builder
                = new AlertDialog.Builder(this)
                .setTitle("Action Requires Login")
                .setMessage("Login??")
                .setIcon(R.drawable.ic_found_24px)
                .setNegativeButton("Login", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                })
                .setPositiveButton("No thanks!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
                    }
                });

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

                    case R.id.navigation_map:
                        InitializeFragments(mapFragment);
                        return true;

                    case R.id.navigation_settings:
                        if(currentUser == null){
                            AlertDialog registerAlert = builder.create();
                            registerAlert.show();
                        }
                        else{
                            InitializeFragments(settingsFragment);
                        }
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


        reportLost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUser == null) {

                    AlertDialog registerAlert = builder.create();
                    registerAlert.show();

                }
                else startActivity(new Intent(MainActivity.this, NewPostActivity.class));
            }
        });

        reportFound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUser == null) {
                    //startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    //finish();
                    AlertDialog registerAlert = builder.create();
                    registerAlert.show();
                }
                else startActivity(new Intent(MainActivity.this, NewPostActivity.class));
                //finish();
            }
        });

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
            tvFound.startAnimation(report_close);
            tvLost.startAnimation(report_close);
            reportFound.setClickable(false);
            reportLost.setClickable(false);
            isReportOpen = false;
        }
        else{
            reportFound.startAnimation(report_open);
            reportLost.startAnimation(report_open);
            tvFound.startAnimation(report_open);
            tvLost.startAnimation(report_open );
            reportFound.setClickable(true);
            reportLost.setClickable(true);
            isReportOpen = true;
        }
    }
}

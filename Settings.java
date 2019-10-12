package com.example.missyou;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void goMyProfile(View view) {
        // Do something in response to button
        Intent intent = new Intent(this,MyProfile.class);
        startActivity(intent);
    }

    public void goMyPosts(View view) {
        // Do something in response to button
        Intent intent = new Intent(this,MyPost.class);
        startActivity(intent);
    }
}

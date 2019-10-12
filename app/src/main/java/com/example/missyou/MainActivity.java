
/*
 * Created by Sue on 07/10/2019
 * Edited by Sue on 11/10/2019
 * */

package com.example.missyou;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private TextView tvSample;
    private Button btnLogout;
    private Button btnnewpost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();
        tvSample = findViewById(R.id.tvSample);
        btnLogout = findViewById(R.id.btnLogout);
        btnnewpost = findViewById(R.id.btnNewpost);

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
        // Do something in response to button
        Intent intent = new Intent(this,NewPost.class);
        startActivity(intent);
    }


}

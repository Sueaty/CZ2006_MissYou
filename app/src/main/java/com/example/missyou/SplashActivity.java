package com.example.missyou;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends Activity {

    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);


        try{
            Thread.sleep(1500);

        }catch(InterruptedException e){

            e.printStackTrace();
        }

        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();

    }



}

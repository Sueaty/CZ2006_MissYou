
/*
 * Created by Sue on 07/10/2019
 * */

package com.example.missyou;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class UserRegisterActivity extends AppCompatActivity {

    private EditText inputName, inputEmail, inputPassword;
    private Button btnSignUp;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        mFirebaseAuth = FirebaseAuth.getInstance();
        inputName = findViewById(R.id.inputName);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        btnSignUp = findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = inputName.getText().toString();
                final String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();

                if(name.isEmpty()){
                    inputName.setError("Please enter your name");
                    inputName.requestFocus();
                }
                else if (email.isEmpty()){
                    inputEmail.setError("You must enter your email");
                    inputEmail.requestFocus();
                }
                else if (password.isEmpty()){
                    inputPassword.setError("You must enter your password");
                    inputName.requestFocus();
                }
                // If all fields are filled create account
                else if (!(name.isEmpty() && email.isEmpty() && password.isEmpty())){
                    mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(UserRegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            User user = new User(
                                    name,
                                    email
                            );
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(!task.isSuccessful()){
                                        Toast.makeText(UserRegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        startActivity(new Intent(UserRegisterActivity.this, MainActivity.class));
                                    }
                                }
                            });
                        }
                    });
                }

                // some sort of error occured
                else{
                    Toast.makeText(UserRegisterActivity.this, "Error occured\nPlease try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}



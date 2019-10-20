
/*
 * Created by Sue on 07/10/2019
 * Edited by Sue on 13/10/2019 : Registration fail case added(Attempt to register with existing eamil.)
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
import com.google.firebase.auth.SignInMethodQueryResult;
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
        inputEmail = findViewById(R.id.tvEmail);
        inputPassword = findViewById(R.id.inputPassword);
        btnSignUp = findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = inputName.getText().toString();
                final String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();
                final String phone = "";
                final String havePet = "false";

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

/*
If all fields are filled, check if user's emil already  exists.
   If email exists, make a toast that email exists so try again.
   If email doesn't exist, proceed to making account.
 */
                else if (!(name.isEmpty() && email.isEmpty() && password.isEmpty())){

                    // Check if email already exists
                    mFirebaseAuth.fetchSignInMethodsForEmail(inputEmail.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            boolean check = !task.getResult().getSignInMethods().isEmpty();

                            // If it's new email
                            if(!check){
                                // Create account
                                mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(UserRegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                User user = new User(
                                                        name,
                                                        email,
                                                        phone,
                                                        havePet
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
                            // If email does exist
                            else{
                                inputEmail.setError("Email Already Exists!");
                                inputEmail.requestFocus();
                            }
                        }
                    });
                }

                // some sort of error occurred
                else{
                    Toast.makeText(UserRegisterActivity.this, "Error occurred\nPlease try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}



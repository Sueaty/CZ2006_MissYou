
/*
 * Created by Sue on 07/10/2019
 * Edited by Sue on 13/10/2019 : Registration fail case added(Attempt to register with existing eamil.)
 * */

package com.example.missyou;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class UserRegisterActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private Toolbar toolbar;
    private EditText inputName, inputEmail, inputPassword;
    private Button btnSignUp;

    private FirebaseUser currentUser;
    private String currentUserUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        toolbar = findViewById(R.id.registerToolbar);
        inputName = findViewById(R.id.inputName);
        inputEmail = findViewById(R.id.tvEmail);
        inputPassword = findViewById(R.id.inputPassword);
        btnSignUp = findViewById(R.id.btnSignUp);

        // Set up toolbar for ProfileActivity
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("User Register");

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = inputName.getText().toString();
                final String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

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

                                                HashMap<String, String> userInfo = new HashMap<>();
                                                userInfo.put("name", name);
                                                userInfo.put("phone", "");
                                                userInfo.put("email", email);
                                                userInfo.put("havePet","false");
                                                userInfo.put("image", "");

                                                currentUser = mFirebaseAuth.getCurrentUser();
                                                currentUserUID = currentUser.getUid();
                                                firebaseFirestore.collection("Users").document(currentUserUID).set(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        startActivity(new Intent(UserRegisterActivity.this, MainActivity.class));
                                                        finish();
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



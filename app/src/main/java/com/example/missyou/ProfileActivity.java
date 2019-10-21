package com.example.missyou;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.grpc.Context;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CircleImageView profileImage;
    private Uri mainImageURI = null;

    private RadioGroup radioGroup;
    private RadioButton btnHavePet, btnNoPet;
    private TextView contactInfo, email;
    private EditText inputName, inputPhone;
    private Button btnDone, btnReturn;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mFirebaseRef;
    private FirebaseUser currentUser;
    private String currentUserUID, userName, userEmail, userPhone, userHavePet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = findViewById(R.id.profileToolbar);
        profileImage = findViewById(R.id.profileImage);

        radioGroup = findViewById(R.id.radioGroup);
        btnHavePet = findViewById(R.id.btnYesPet);
        btnNoPet = findViewById(R.id.btnNoPet);
        contactInfo = findViewById(R.id.tvContactInfo);
        inputName = findViewById(R.id.inputName);
        email = findViewById(R.id.tvEmail);
        inputPhone = findViewById(R.id.inputPhone);
        btnDone = findViewById(R.id.btnDone);
        btnReturn = findViewById(R.id.btnReturn);

        storage = FirebaseStorage.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseRef = FirebaseDatabase.getInstance().getReference();
        currentUser = mFirebaseAuth.getCurrentUser();
        currentUserUID = currentUser.getUid();

        // Set up toolbar for ProfileActivity
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("User Profile");

        // Fill up existing information
        mFirebaseRef.child("Users").child(currentUserUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userName = dataSnapshot.child("name").getValue().toString();
                userEmail = dataSnapshot.child("email").getValue().toString();
                userPhone = dataSnapshot.child("phone").getValue().toString();
                userHavePet = dataSnapshot.child("havePet").getValue().toString();

                inputName.setText(userName);
                email.setText(userEmail);
                inputPhone.setText(userPhone);
                if(userHavePet.equals("false")){
                    radioGroup.check(btnNoPet.getId());
                }
                else{
                    radioGroup.check(btnHavePet.getId());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(ContextCompat.checkSelfPermission(ProfileActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(ProfileActivity.this, "Access permission is Denied", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }
                    else{
                        // start picker to get image for cropping and then use the image in cropping activity
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1, 1)
                                .start(ProfileActivity.this);
                    }
                }
            }
        });

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userName = inputName.getText().toString();
                userPhone = inputPhone.getText().toString();
                if(btnHavePet.isChecked()){ userHavePet = "true";}
                else if (btnNoPet.isChecked()){ userHavePet = "false"; }

                HashMap<String, Object> infoMap = new HashMap<>();
                infoMap.put(currentUserUID, new User(userName, userEmail, userPhone, userHavePet));
                mFirebaseRef.child("Users").updateChildren(infoMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            StorageReference image_path = storageReference.child("profile_images").child(currentUserUID + ".jpg");
                            image_path.putFile(mainImageURI);
                            Task<Uri> urlTask = image_path.putFile(mainImageURI).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if(!task.isSuccessful()){
                                        // Unsuccessful case
                                    }

                                    // continue with the task to get the download URL
                                    return storageReference.child("profile_images").child(currentUserUID + ".jpg").getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if(task.isSuccessful()){
                                        Uri downloadUri = task.getResult();
                                        Toast.makeText(ProfileActivity.this, "Successfully Saved", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        // Handle failures
                                        String error = task.getException().getMessage();
                                        Toast.makeText(ProfileActivity.this, "Error : " + error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else{
                            String error = task.getException().getMessage();
                            Toast.makeText(ProfileActivity.this, "Error : " + error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });



            }

        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                profileImage.setImageURI(mainImageURI);

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }
    }
}


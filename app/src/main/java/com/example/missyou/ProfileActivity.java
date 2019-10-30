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
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestOptions;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.net.URI;
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
    private TextView contactInfo, inputEmail;
    private EditText inputName, inputPhone;
    private Button btnDone, btnReturn;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser currentUser;
    private String currentUserUID;


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
        inputEmail = findViewById(R.id.tvEmail);
        inputPhone = findViewById(R.id.inputPhone);
        btnDone = findViewById(R.id.btnDone);
        btnReturn = findViewById(R.id.btnReturn);

        storage = FirebaseStorage.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = mFirebaseAuth.getCurrentUser();
        currentUserUID = currentUser.getUid();

        // Set up toolbar for ProfileActivity
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("User Profile");


        firebaseFirestore.collection("Users").document(currentUserUID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    if (task.getResult().exists()) {

                        String name = task.getResult().getString("name");
                        String email = task.getResult().getString("email");
                        String image = task.getResult().getString("image");
                        String phone = task.getResult().getString("phone");
                        String havePet = task.getResult().getString("havePet");

                        //mainImageURI = Uri.parse(image);
                        inputName.setText(name);
                        inputEmail.setText(email);
                        inputPhone.setText(phone);

                        if (havePet.equals("false")) {
                            radioGroup.check(btnNoPet.getId());
                        } else {
                            radioGroup.check(btnHavePet.getId());
                        }

                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.default_profile_image);

                        Glide.with(ProfileActivity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(profileImage);

                    } else {
                        try {
                            String error = task.getException().getMessage();
                             Toast.makeText(ProfileActivity.this, "(FIRESTORE Retrieve Error) : " + error, Toast.LENGTH_LONG).show();

                        }
                        catch(java.lang.NullPointerException exception) {
                        }
                      //  Toast.makeText(ProfileActivity.this, "(FIRESTORE Retrieve Error) : " + error, Toast.LENGTH_LONG).show();

                    }
                }
            }
        });


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(ProfileActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(ProfileActivity.this, "Access permission is Denied", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        // start picker to get image for cropping and then use the image in cropping activity
                        imageCrop();
                    }
                }
                else{
                    imageCrop();
                }
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String userName = inputName.getText().toString();
                final String userPhone = inputPhone.getText().toString();
                final String userEmail = inputEmail.getText().toString();
                final String userHavePet;

                if (btnHavePet.isChecked()) { userHavePet = "true"; }
                else { userHavePet = "false"; }

                File newImageFile = new File(mainImageURI.getPath());
                UploadTask image_path = storageReference.child("profile_images").child(currentUserUID + ".jpg").putFile(mainImageURI);

                Task<Uri> urlTask = image_path.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return storageReference.child("profile_images").child(currentUserUID + ".jpg").getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri download_uri;
                        if (task.isSuccessful()) {

                            if(task != null){download_uri = task.getResult();}
                            else{download_uri = mainImageURI;}

                            HashMap<String, String> userInfo = new HashMap<>();
                            userInfo.put("name", userName);
                            userInfo.put("phone", userPhone);
                            userInfo.put("email", userEmail);
                            userInfo.put("havePet", userHavePet);
                            userInfo.put("image", download_uri.toString());

                            firebaseFirestore.collection("Users").document(currentUserUID).set(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(ProfileActivity.this, "User Information Updated.", Toast.LENGTH_SHORT).show();

                                    } else {

                                        String error = task.getException().getMessage();
                                        Toast.makeText(ProfileActivity.this, "(FIRESTORE Error) : " + error, Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                        }
                        else {
                            String error = task.getException().getMessage();
                            Toast.makeText(ProfileActivity.this, "(IMAGE Error) : " + error, Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }

        });

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    protected void imageCrop(){
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(ProfileActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                //profileImage.setImageURI(mainImageURI);
                Glide.with(ProfileActivity.this).load(mainImageURI).into(profileImage);
                Toast.makeText(ProfileActivity.this, "Profile Image Changed", Toast.LENGTH_SHORT).show();

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }


}


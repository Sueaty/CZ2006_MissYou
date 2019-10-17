package com.example.missyou;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.net.*;
import android.content.Intent;
import android.content.ContentResolver;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import android.graphics.Bitmap;
import id.zelory.compressor.Compressor;

import java.io.*;
import java.io.File;
import java.util.*;
import java.util.UUID;



public class NewPostActivity extends AppCompatActivity{
    private ImageView newPostImage;
    private EditText yourPhone;
    private EditText yourAddress;
    private EditText yourEmail;
    private EditText yourDescription;
    private Button btnPost;
    private final int PICK_IMAGE_REQUEST = 71;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseFirestore firebaseFirestore;
    private Bitmap compressedImageFile;
    private Uri postImageUri = null;
    //private DataSnapshot dataSnapshot;


    private String current_user_id;


    FirebaseUser user;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);


        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        current_user_id = firebaseAuth.getCurrentUser().getUid();


        //databaseReference = FirebaseDatabase.getInstance().getReference();




        newPostImage = findViewById(R.id.newPostImage);
        yourAddress = findViewById(R.id.yourAddress);
        yourEmail = findViewById(R.id.yourEmail);
        yourPhone = findViewById(R.id.yourPhone);
        yourDescription = findViewById(R.id.descriptions);
        btnPost = findViewById(R.id.btnPost);
        // storageReference post_image_path = storageReference.child("Post_img").child(user_name + ".jpg");


        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //user = FirebaseAuth.getInstance().getCurrentUser();
                // uid = user.getUid();
                 //String user_name = dataSnapshot.child(uid).child("name").getValue(String.class);  // get the user's name from data base
               // Intent intent = new Intent(NewPostActivity.this,MainActivity.class);  // jum to Main Activity
                File newImageFile = new File(postImageUri.getPath());
                final String Postdesc = yourDescription.getText().toString();
                final String postadd = yourAddress.getText().toString();
                final String postemail = yourEmail.getText().toString();
                final String postph = yourPhone.getText().toString();

                final String randomName = UUID.randomUUID().toString();

                File newThumbFile = new File(postImageUri.getPath());
               try {
                    compressedImageFile = new Compressor (NewPostActivity.this)
                            .setMaxHeight(125)
                            .setMaxWidth(125)
                            .setQuality(50)
                            .compressToBitmap(newThumbFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

             /*   ByteArrayOutputStream baos = new ByteArrayOutputStream();
                compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] thumbData = baos.toByteArray(); */




               // UploadTask filePath1 = storageReference.child("post_images").child(randomName + ".jpg");
                UploadTask filePath = storageReference.child("post_images").child(randomName + ".jpg").putFile(postImageUri);
                filePath.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                      //  String downloadthumbUri = taskSnapshot.getDownloadUrl().toString();

                        Map<String, Object> postMap = new HashMap<>();
                        postMap.put("image_url", postImageUri);
                        postMap.put("desc", Postdesc);
                        postMap.put("Address",postadd);
                        postMap.put("Email",postemail);
                        postMap.put("Phone",postph);
                        postMap.put("user_id", current_user_id);
                        postMap.put("timestamp", FieldValue.serverTimestamp());
                        firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {

                                    Toast.makeText(NewPostActivity.this, "Post was added", Toast.LENGTH_LONG).show();
                                    Intent mainIntent = new Intent(NewPostActivity.this, LoginActivity.class);
                                    startActivity(mainIntent);
                                    finish();

                            }
                        });


                    }
                });


            }
        });

        newPostImage.setOnClickListener(new View.OnClickListener() { // Select img from photo
            @Override
            public void onClick(View view) {
                chooseImage();
                final String desc = yourDescription.getText().toString();
                final String randomName = UUID.randomUUID().toString();

            }
        });
    }
        private void chooseImage() {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        }
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                    && data != null && data.getData() != null )
            {
                postImageUri = data.getData();
                newPostImage.setImageURI(postImageUri);
            }
        }
        private String getExtension (Uri uri)
        {
            ContentResolver cr = getContentResolver();
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
        }



    }


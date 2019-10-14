package com.example.missyou;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class NewPostActivity extends AppCompatActivity{
    private ImageView newPostImage;
    private EditText yourPhone;
    private EditText yourAddress;
    private EditText yourEmail;
    private EditText yourDescription;
    private Uri postimageUri = null;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        newPostImage = findViewById(R.id.newPostImage);
        yourAddress = findViewById(R.id.yourAddress);
        yourEmail = findViewById(R.id.yourEmail);
        yourPhone = findViewById(R.id.yourPhone);
        yourDescription = findViewById(R.id.descriptions);

    }

}

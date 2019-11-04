package com.example.missyou;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser currentUser;

    private CircleImageView profileImage;
    private Uri mainImageURI = null;

    private Button logout, btnDone;
    private RadioGroup radioGroup;
    private RadioButton btnHavePet, btnNoPet;
    private TextView contactInfo, inputEmail;
    private EditText inputName, inputPhone;

    private String currentUserUID;

    // Required empty public constructor
    public SettingsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);


        mFirebaseAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        currentUser = mFirebaseAuth.getCurrentUser();
        currentUserUID = currentUser.getUid();

        profileImage = v.findViewById(R.id.profileImage);

        radioGroup = v.findViewById(R.id.radioGroup);
        btnHavePet = v.findViewById(R.id.btnYesPet);
        btnNoPet = v.findViewById(R.id.btnNoPet);
        logout = v.findViewById(R.id.btnLogout);
        contactInfo = v.findViewById(R.id.tvContactInfo);
        inputName = v.findViewById(R.id.inputName);
        inputEmail = v.findViewById(R.id.tvEmail);
        inputPhone = v.findViewById(R.id.inputPhone);
        btnDone = v.findViewById(R.id.btnDone);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAuth.signOut();
                getActivity().finish();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

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

                        Glide.with(SettingsFragment.this).setDefaultRequestOptions(placeholderRequest).load(image).into(profileImage);

                    } else {
                        try {
                            String error = task.getException().getMessage();
                            Toast.makeText(getActivity(), "(FIRESTORE Retrieve Error) : " + error, Toast.LENGTH_SHORT).show();

                        } catch (java.lang.NullPointerException exception) {
                        }


                    }
                }
            }
        });


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getActivity(), "Access Permission Denied", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        // start picker to get image for cropping and then use the image in cropping activity
                        imageCrop();
                    }
                } else {
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

                if (btnHavePet.isChecked()) {
                    userHavePet = "true";
                } else {
                    userHavePet = "false";
                }

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

                            if (task != null) {
                                download_uri = task.getResult();
                            } else {
                                download_uri = mainImageURI;
                            }

                            HashMap<String, String> userInfo = new HashMap<>();
                            userInfo.put("name", userName);
                            userInfo.put("phone", userPhone);
                            userInfo.put("email", userEmail);
                            userInfo.put("havePet", userHavePet);
                            userInfo.put("image", download_uri.toString());

                            firebaseFirestore.collection("Users").document(currentUserUID).set(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), "User Information Updated", Toast.LENGTH_SHORT).show();

                                    } else {

                                        String error = task.getException().getMessage();
                                        Toast.makeText(getActivity(), "(FIRESTORE Retrieve Error) : " + error, Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(getActivity(), "(Image Error) : " + error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

        });

        return v;
    }


    protected void imageCrop(){
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(getActivity());

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                //profileImage.setImageURI(mainImageURI);
                Glide.with(SettingsFragment.this).load(mainImageURI).into(profileImage);
                Toast.makeText(getActivity(), "Profile Image Changed", Toast.LENGTH_SHORT).show();

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }
}
package com.example.missyou;
import com.example.missyou.PetLocationMapsActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.content.Intent;
import android.content.ContentResolver;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.maps.model.LatLng;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import android.graphics.Bitmap;
//import id.zelory.compressor.Compressor;
import com.google.firebase.database.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.UUID;


public class  NewPostActivity extends AppCompatActivity{
    private ImageView newPostImage;
    private EditText yourPhone, yourEmail, yourDescription;
    private TextView yourAddress;
    Map<String, Object> postMap;
    String locationText;
    private Button btnPost;
    private final int PICK_IMAGE_REQUEST = 71;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;
    private FirebaseFirestore firebaseFirestore;
    private Bitmap compressedImageFile;
    private Uri postImageUri = null;
    //private DataSnapshot dataSnapshot;
    private String current_user_id;

    public double longitude = 0.0;
    public double latitude = 0.0;
    LatLng latLng;


    //private DatabaseReference mDatabase;

    private AutoCompleteTextView mSearchText;
    public PlaceAutocomplete placeAutocomplete;



    //  FirebaseUser user;
   // String uid;
  public  double[] geoLocate(){

      mSearchText = (AutoCompleteTextView) findViewById(R.id.input_search);

      String searchString = mSearchText.getText().toString();
     // Log.d(TAG,"geoLocate: geolocating");

      Geocoder geocoder = new Geocoder(NewPostActivity.this);

      List<Address> list = new ArrayList<>();

      try{
          list = geocoder.getFromLocationName(searchString,1);
      }catch( IOException e){
       //   Log.e(TAG,"geolocate: IO Exception: " + e.getMessage());

      }

      // if(list.size()>0) {//if we had result
      final Address address = list.get(0);

   //   Log.d(TAG, "geolocate: found a location: " + address.toString());

    //  moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM,address.getAddressLine(0));

      double locationAry[] = new double[2];
      locationAry[0] = address.getLatitude();
      locationAry[1] = address.getLongitude();

      latitude = address.getLatitude();
      longitude = address.getLongitude();




      return locationAry;

  }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        init();//Location button


        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
      //  mDatabase = FirebaseDatabase.getInstance().getReference();


        currentUser = firebaseAuth.getCurrentUser();
        current_user_id = currentUser.getUid();


        newPostImage = findViewById(R.id.newPostImage);

        //type textview
      //  yourAddress =  findViewById(R.id.yourAddress);

      //yourAddress = yourAddress.setText(postadd);

        //yourEmail = findViewById(R.id.yourEmail);
        yourPhone = findViewById(R.id.yourPhone);
        yourDescription = findViewById(R.id.descriptions);
        btnPost = findViewById(R.id.btnPost);

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Intent intent = new Intent(NewPostActivity.this,MainActivity.class);  // jum to Main Activity
                File newImageFile = new File(postImageUri.getPath());
                final String Postdesc = yourDescription.getText().toString();

            //    final String postadd = yourAddress.getText().toString();

              //   yourAddress.setText(postadd);
            //   yourAddress.setText(getAddress());


                // final String postemail = yourEmail.getText().toString();a
                final String postemail = currentUser.getEmail();
                final String postph = yourPhone.getText().toString();
                final String randomName = UUID.randomUUID().toString();

              /*  File newThumbFile = new File(postImageUri.getPath());
               try {
                    compressedImageFile = new Compressor (NewPostActivity.this)
                            .setMaxHeight(125)
                            .setMaxWidth(125)
                            .setQuality(50)
                            .compressToBitmap(newThumbFile);
                } catch (IOException e) {
                    e.printStackTrace();
                } */

             /*   ByteArrayOutputStream baos = new ByteArrayOutputStream();
                compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] thumbData = baos.toByteArray(); */


                final StorageReference imageRef = storageReference.child("post_images").child(randomName + ".jpg");
                final UploadTask filePath = imageRef.putFile(postImageUri);

                filePath.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //String downloadthumbUri = taskSnapshot.getDownloadUrl().toString();
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                postMap = new HashMap<>();
                            //    PetLocationMapsActivity loc = new PetLocationMapsActivity();


                               latitude = (double) geoLocate()[0];
                               longitude = (double) geoLocate()[1];
                               latLng = new LatLng(latitude,longitude);
                              //   latitude = loc.latitude;
                               // longitude = loc.longitude;


                                Log.e("IMAGE URL", uri.toString());
                                postMap.put("image_url", uri.toString());
                                postMap.put("desc", Postdesc);
                               //  postMap.put("Location",postadd);

                              //  loc.geoLocate();
                                postMap.put("location", latLng);

                                postMap.put("Latitude", latitude);
                                postMap.put("Longitude",longitude );

                                postMap.put("Email",postemail);
                                postMap.put("Phone",postph);
                                postMap.put("user_id", current_user_id);
                                postMap.put("timestamp", FieldValue.serverTimestamp());
                                firebaseFirestore.collection("Posts").add(postMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.e("ADD / SUCCESS", "Document ID is " + documentReference.getId());
                                        Intent intent = new Intent(NewPostActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        finish();
                                        startActivity(intent);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(NewPostActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(NewPostActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
        //button to open PetLocationMapsActivity
private void init() {
        Button toMap = (Button) findViewById(R.id.toMap);

    toMap.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(NewPostActivity.this,  PetLocationMapsActivity.class);
            startActivity(intent);
        }
    });
}

        private String getExtension (Uri uri)
        {
            ContentResolver cr = getContentResolver();
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
        }


    }


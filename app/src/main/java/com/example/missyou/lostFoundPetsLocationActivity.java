package com.example.missyou;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;
import com.google.firebase.firestore.CollectionReference;


public class lostFoundPetsLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private CollectionReference mUsers;
    private ChildEventListener mChildEventListenet;
    private DocumentReference postRef;

    Marker marker;


    private GoogleMap mMap;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionsGranted;
    private final LatLng mDefaultLocation = new LatLng(1.3483, 103.6831);
    private Location mLastKnownLocation;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final String TAG = "lostFoundPetsLocationActivity";
    public double latitude;
    public double longitude;
   public LatLng loc;
//public int position;
    private AutoCompleteTextView mAutocompleteTextView;
    private PlaceAutocomplete placeAutocomplete;


    //widgets
    private EditText mSearchText;
    private ImageView mGps;

    //firebase
    public int position;
    public List<userpost> post_list;

    //public postRecyclerAdapter user_id;
    //public List<userpost>post_list;
/*
    public lostFoundPetsLocationActivity(){

        this.post_list = post_list;

    }

*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_found_pets_location);

        getLocationPermission();

        mSearchText = (EditText) findViewById(R.id.input_search);
        mGps = (ImageView) findViewById(R.id.ic_gps);
        mAutocompleteTextView = (AutoCompleteTextView) findViewById(R.id.places_autocomplete_edit_text);
        //build the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ChildEventListener mChildEvenetListener;
        mUsers = FirebaseFirestore.getInstance().collection("Users");

        // post_list.get(position); error
        //  String user_id = post_list.get(position).getUser_id();
        // postRef = mUsers.document(user_id);
        // mUsers.push().setValue(marker);
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionsGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    private void init(){
        Log.d(TAG,"init: initializing");

        /*   mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == keyEvent.ACTION_DOWN
                        || keyEvent.getAction() == keyEvent.KEYCODE_ENTER){
                    //execute our method for searching

               //     geoLocate();//geolocate the search string you entered to search field

                }
                return false;
            }
        });
        */

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"OnClick: clicked gps icon");
                getDeviceLocation();
            }
        });

    }


    private void getDeviceLocation() {

        Log.d(TAG, "getDeviceLocation: getting the current device location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(this,new OnCompleteListener(){
                    @Override
                    public void onComplete(Task task){
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: Found location");
                            Location mLastKnownLocation = (Location) task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()),
                                    DEFAULT_ZOOM));

                            //moveCamera(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()),DEFAULT_ZOOM);
                        }
                        else {
                            Log.d(TAG, "onComplete: current location is null");
                            Log.e(TAG, "Exception: %s", task.getException());
                            Toast.makeText(lostFoundPetsLocationActivity.this, " unable to get current location", Toast.LENGTH_SHORT).show();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation,DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });

            }
        }catch (SecurityException e){
            Log.e(TAG , "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    public void moveCamera(LatLng latLng , float zoom, String title) {

        Log.d(TAG, "moveCamera: moving the camera to: lat:" + latLng.latitude + ", long" + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        NewPostActivity newP = new NewPostActivity();

        newP.latitude = latLng.latitude;
        newP.longitude = latLng.longitude;


        // to drop down a pin
        final MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(title);
        mMap.addMarker(options);
        // mMap.setOnInfoWindowClickListener(lostFoundPetsLocationActivity.this);

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();

        mMap = googleMap;
        if(mLocationPermissionsGranted){
            updateLocationUI();
            getDeviceLocation();

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false); //gonna block location button with searchbar anyway, make it false

            /*
            // retriving data from firebase
            this.post_list = post_list;

            postRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            GeoPoint geo = document.getGeoPoint("location");

                          //  String name = document.getString("name");
                            double lat = geo.getLatitude();
                            double lng = geo.getLongitude();
                            LatLng latLng = new LatLng(lat, lng);
                            mMap.addMarker(new MarkerOptions().position(latLng).title("last seen location"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                        }
                    }
                    else
                        Toast.makeText(lostFoundPetsLocationActivity.this, "retrieving data error ", Toast.LENGTH_SHORT).show();

                }
            });
            */


        }

 // Add a marker in Sydney and move the camera
        LatLng currentloc = new LatLng(1.34, 103.68);
        mMap.addMarker(new MarkerOptions().position(currentloc).title("last seen location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentloc));

    }


    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionsGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionsGranted = true;
                }
            }
        }
        updateLocationUI();
    }

}

package com.example.missyou;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;

import com.google.android.gms.location.places.ui.PlaceAutocomplete;



import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceDetectionClient;

import com.google.android.libraries.places.api.Places;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;


/*
import com.google.android.libraries.places.compat.Place;
import com.google.android.libraries.places.compat.AutocompletePredictionBufferResponse;
import com.google.android.libraries.places.compat.GeoDataClient;

import com.google.android.libraries.places.compat.AutocompletePrediction;
import com.google.android.libraries.places.compat.Place;
import com.google.android.libraries.places.compat.PlaceBuffer;
*/



import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;

import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.Manifest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class PetLocationMapsActivity extends FragmentActivity implements OnMapReadyCallback {

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
    private static final String TAG = "PetLocationMapsActivity";


    private AutoCompleteTextView mAutocompleteTextView;


    //widgets
    private EditText mSearchText;
    private ImageView mGps;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_location_maps);

        getLocationPermission();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
 //  mGeoDataClient = Places.getGeoDataClient(this, null);
        // Construct a PlaceDetectionClient.
     //  mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
    //    mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mSearchText = (EditText) findViewById(R.id.input_search);
        mGps = (ImageView) findViewById(R.id.ic_gps);

        mAutocompleteTextView = (AutoCompleteTextView) findViewById(R.id.places_autocomplete_edit_text);

//build the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);





    }

    private void init(){
        Log.d(TAG,"init: initializing");

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == keyEvent.ACTION_DOWN
                        || keyEvent.getAction() == keyEvent.KEYCODE_ENTER){
                    //execute our method for searching

                    geoLocate();//geolocate the search string you entered to search field

            }
                return false;
            }
        });

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"OnClick: clicked gps icon");
                getDeviceLocation();
            }
        });

    }

    public String geoLocate(){
        Log.d(TAG,"geoLocate: geolocating");

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(PetLocationMapsActivity.this);

        List<Address> list = new ArrayList<>();

        try{
            list = geocoder.getFromLocationName(searchString,1);
        }catch( IOException e){
            Log.e(TAG,"geolocate: IO Exception: " + e.getMessage());

        }

        if(list.size()>0) {//if we had result
            Address address = list.get(0);

            Log.d(TAG, "geolocate: found a location: " + address.toString());

            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM,address.getAddressLine(0));
        //firebase

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("Locations");
            //Instead of "Locations" you can say something else. Locations will be the name of your path where the location is stored.
            //Create a Hashmap to store the information with a key and a value:
            HashMap<String, String> values = new HashMap<>();
            values.put("Locations", address.toString());
            databaseReference.push().setValue(values);

        }
        return list.get(0).toString();


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
                            Toast.makeText(PetLocationMapsActivity.this, " unable to get current location", Toast.LENGTH_SHORT).show();
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

    public void moveCamera(LatLng latLng , float zoom, String title){

        Log.d(TAG,"moveCamera: moving the camera to: lat:" + latLng.latitude + ", long" + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));


        // to drop down a pin

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(title);
        mMap.addMarker(options);

     //   return title;
        //Toast.makeText(PetLocationMapsActivity.this, title, Toast.LENGTH_SHORT).show();

    }





    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        mMap = googleMap;

        if(mLocationPermissionsGranted){
            updateLocationUI();

            getDeviceLocation();

            mMap.setMyLocationEnabled(true);
            //gonna block location button with searchbar anyway, make it false
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            init();
        }


/*
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
*/
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

/*
    private void getLocationPermission() // get permission that we want
    {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
            } else {

                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);


            }
        }
    }
    */
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
/*
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults ){
 mLocationPermissionsGranted = false;

 switch(requestCode){
     case LOCATION_PERMISSION_REQUEST_CODE:{
// If request is cancelled, the result arrays are empty
         if(grantResults.length > 0 ){
             for(int i = 0;i< grantResults.length;i++){

                 if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                     mLocationPermissionsGranted = false;
                     return;
                 }
             }
        mLocationPermissionsGranted = true;
        //initialize our map;

         }
     }

 }
        updateLocationUI();

    }*/
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

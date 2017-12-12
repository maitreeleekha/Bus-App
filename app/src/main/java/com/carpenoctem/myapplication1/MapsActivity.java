package com.carpenoctem.myapplication1;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    LatLng mylocation;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    Marker marker;
    int index;
    String check;
    private DatabaseReference mDatabase;

    boolean readyMap =false;
    ImageView current;
    EditText current_ed, destination;
    Button write, read;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Intent i =getIntent();
        index = i.getIntExtra("index",0);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        read = (Button) findViewById(R.id.read);
        write = (Button) findViewById(R.id.write);
        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (marker != null) {
                    marker.remove();
                }
                marker = mMap.addMarker(new MarkerOptions().position(new LatLng(-31.90, 115.86)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(-31.90, 115.86)));
            }
        });

        current = (ImageView) findViewById(R.id.current_location);
        current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(readyMap){

                }
                else{
                    Toast.makeText(MapsActivity.this,"Turn On the Location to get Current Location",Toast.LENGTH_LONG).show();
                }
            }
        });

        /*
        for(index=1;index<=2;index++){
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    check = dataSnapshot.child("879").child("bus" + index).child("check").getValue(String.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            if (check.equals("true")) {
                mDatabase.child("879").child("bus" + index).child("check").setValue("false");
                break;
            }
        }
        */

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
        mMap = googleMap;

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        try {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                            }
                        }
                    });

            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Log.i("Lakshay",  "Changed" );
                    for (Location location : locationResult.getLocations()) {
                        Toast.makeText(MapsActivity.this, "Lat:" + location.getLatitude() + "Long: "+location.getLongitude(),Toast.LENGTH_SHORT).show();

                        mylocation = new LatLng(location.getLatitude(), location.getLongitude());

                        if(marker != null){
                            marker.remove();
                        }
                        marker = mMap.addMarker(new MarkerOptions().position(mylocation));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(mylocation));

                        mDatabase.child("879").child("bus"+index).child("lat").setValue(location.getLatitude()+"");
                        mDatabase.child("879").child("bus"+index).child("long").setValue(location.getLongitude()+"");

                        Log.i("Lakshay",  "Lat:" + location.getLatitude() + "Long: "+location.getLongitude() );
                        // Update UI with location data
                        // ...
                    }
                };
            };

            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null /* Looper */);

        }
        catch (SecurityException e){

        }

    }

    @Override
    protected void onStop() {
        mDatabase.child("879").child("bus"+index).child("check").setValue("true");
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v("LAKSHAY", connectionResult.toString());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        readyMap = true;
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if ( googleApiAvailability.isUserResolvableError(resultCode) ) {
                googleApiAvailability.getErrorDialog(this,resultCode, 404).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG).show();
            }
            return false;
        }
        return true;
    }

}

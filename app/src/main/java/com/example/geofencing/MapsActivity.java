package com.example.geofencing;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private static final String TAG = "MapsActivity";


    private GoogleMap mMap;
    private LatLng mLatlng;
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;
    private CircleOptions circleOptions = new CircleOptions();
    private SeekBar sk;
    private String fenceID;
    private float geofenceRadius = 200;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private static final int REQUEST_CODE = 10001;
    private final int FINE_LOCATION_ACCESS_REQUEST_CODE = 10002;
    private final int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);
        fenceID = getIntent().getStringExtra("fenceID");
        Log.e(TAG, "fenceid:" + fenceID);
        getUserLocation();
        seekbarRadius();
        screenElements();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng eiffel = new LatLng(54.3922233,-7.460641);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eiffel, 16));

        enableUserLocation();
        enableTextMessages();

        mMap.setOnMapLongClickListener(this);
    }

    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            //Ask for permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
        }
    }

    private void getUserLocation(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            final LatLng t = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(t, 14));
                        }
                    }
                });
    }


    private void enableTextMessages(){
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) + ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS))
                != PackageManager.PERMISSION_GRANTED) {
            // should get both permission pop ups at the same time now
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_SMS) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_SMS)) {
                Log.e("Permissions", "one");

            } else {
                ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS}, REQUEST_CODE);
                Log.e("Permissions", "two");
            }
        }

        else {
            // Permissions has already been granted
            Log.e("Permissions", "three");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
            } else {
                //We do not have the permission...
                //The app just won't work so make ask the question again? stick this in a while loop?

            }
        }

        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                Toast.makeText(this, "You can add geofences...", Toast.LENGTH_SHORT).show();
            } else {
                //We do not have the permission..
                Toast.makeText(this, "Background location access is neccessary for geofences to trigger...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (Build.VERSION.SDK_INT >= 29) {
            //We need background permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                handleMapLongClick(latLng);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    //We show a dialog and ask for permission
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                }
            }

        } else {
            handleMapLongClick(latLng);
            mLatlng = latLng;
            sk.setVisibility(View.VISIBLE);

        }

    }

    private void seekbarRadius(){
        sk=(SeekBar) findViewById(R.id.seekBar2);
        sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {

                circleOptions.radius(progress);
                mMap.clear();
                mMap.addCircle(circleOptions);

                // Lose the animation and put this in the onStopTrackingTouch? Can look glitchy
                //just to show what the radius is
                //Toast.makeText(getApplicationContext(), String.valueOf(progress),Toast.LENGTH_LONG).show();

            }
        });
        sk.setVisibility(View.INVISIBLE);
    }

    private void handleMapLongClick(LatLng latLng) {
        Log.d("testing", "creating the geofence");
        mMap.clear();
        sk.setProgress(0);
        addMarker(latLng);
        addCircle(latLng, geofenceRadius);
        // do some shit before this is added
        // addGeofence(latLng, GEOFENCE_RADIUS);
    }

    //todo add a save button
    //  this will have the addGeofence method and create and intent to the details page.
    //  need to consider people redoing the goefence by pressing back on the details page
    //  the intent needs to include the id of the geofence.


    private void addGeofence(LatLng latLng, float radius) {

        //todo - need to pick what transition types that I should add here
        //todo - I need to remove the fence ID here, just setting it for testing
        Log.e(TAG, "adding:" + fenceID);
        //fenceID = "newtest";
        Geofence geofence = geofenceHelper.getGeofence(fenceID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        Log.e(TAG, "created fence");
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        Log.e(TAG, "created request ");
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();
        Log.e(TAG, "intent returned ");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Missing some permission that are messing with creating the listener");
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            //return;
        }
        Log.e(TAG, "creating listener ");
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Geofence Added...");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Log.e(TAG, "onFailure: " + errorMessage);
                    }
                });
        //todo - figure out how to use geofencingClient to unreg all of the fences to be deleted.
        Log.e(TAG, "created listener ");
    }

    private void addMarker(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMap.addMarker(markerOptions);
    }


    //okay I can make circle options public and then update the radios from there
    //the seekbar might do this in a smooth fashion but I really don't mind if it doesn't
    private void addCircle(LatLng latLng, float radius) {
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255, 255, 255,255));
        circleOptions.fillColor(Color.argb(64, 0, 0,255));
        circleOptions.strokeWidth(8);
        mMap.addCircle(circleOptions);
    }

    private void save() {
        addGeofence(mLatlng, geofenceRadius);
        Intent intent = new Intent(MapsActivity.this, MainActivity.class);
        //MapsActivity.this.startActivity(intent);
    }

    private void screenElements(){
        findViewById(R.id.button_map_save).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                save();
            }
        });
    }
}

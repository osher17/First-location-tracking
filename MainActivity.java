package com.example.locationtryfirst;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {

    private static final int DEFAULT_INTERVAL = 30000; // default time for an update
    private static final int FASTEST_INTERVAL = 5000; // fastest time for an update
    private static final int PERMISSION_FINE_LOCATION = 22;

    TextView lat_res, long_res, type_txt; // text views which will show the location and the settings (type of location collecting)
    Switch type_sw, prm_sw; // settings switches
    LocationRequest locationRequest; // saves the configurations of the request
    FusedLocationProviderClient fusedLocationClient; // Google's API for location services
    boolean batterySaver; // is on battery saver mode? (doesn't use gps but cellphone tower tracing)
    double latitute, longitude;
    String toastStr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //give a value to each UI variable
        lat_res = (TextView) findViewById(R.id.lat_res);
        long_res = (TextView) findViewById(R.id.long_res);
        type_txt = (TextView)findViewById(R.id.type_txt);
        type_sw = (Switch) findViewById(R.id.type_sw);
        prm_sw = (Switch) findViewById(R.id.prm_sw);


        // create an instance of location request and set it to the wanted settings
        locationRequest = new LocationRequest();
        locationRequest.setInterval(DEFAULT_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // set and manage switch listener
        type_sw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type_sw.isChecked()) {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    type_txt.setText("Using cell towers");
                } else {
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    type_txt.setText("Using GPS");
                }
            }
        });

        //
        //fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Log.i("Main Activity", "On create");
        dealWithLocation();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        switch(requestCode)
        {
            case PERMISSION_FINE_LOCATION:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    Log.i("MainActivity", "Got Location Permission");
                    dealWithLocation();
                }
                else
                {
                    Toast.makeText(this, "didn't get fine location permission", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void dealWithLocation()
    {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        // check if we have permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // if we don't
            // check if build version sufficient 23 or over
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                // request permission
                requestPermissions( new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }
            Log.i("Main Activity", "Asking for permission");
            return;
        }
        else {
            Log.i("MainActivity", "Trying to receive loaction");
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Log.i("Main Activity", "Got location, not null");
                                latitute = location.getLatitude();
                                longitude = location.getLongitude();
                                toastStr = "latitute: " + latitute + " longitude: " + longitude;
                                Toast.makeText(MainActivity.this, toastStr, Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Log.i("Main Activity", "Location is null");

                                Toast.makeText(MainActivity.this, "something went wrong :/ ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
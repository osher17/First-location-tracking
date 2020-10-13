package com.example.findlocationbasic;

import androidx.annotation.NonNull;
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
import android.widget.ToggleButton;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {

    public static final long DEFAULT_UPDATE_INTERVAL = 30000;
    public static final long FASTEST_UPDATE_INTERVAL = 5000;
    public static final int PERMISSION_FINE_LOCATION = 22;

    Switch type_sw; // switch responsible for settings
    TextView type_txt; // presents which type of location tracking is used
    TextView address_txt ; // presents address
    ToggleButton prm_button; // stop and start the tracking
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    Double longitude, latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        type_sw = (Switch)findViewById(R.id.type_sw);
        type_txt = (TextView)findViewById(R.id.type_txt);
        address_txt = (TextView)findViewById(R.id.address_txt);
        prm_button = (ToggleButton)findViewById(R.id.prm_btn);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(DEFAULT_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        type_sw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type_sw.isChecked())
                {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    type_txt.setText("Using towers + wifi");
                }
                else
                {
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    type_txt.setText("Using GPS sensors");

                }
            }
        });

        dealWithLocation();


    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode)
        {
            case PERMISSION_FINE_LOCATION:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    dealWithLocation();
                }
                else
                {
                    Toast.makeText(this,"this app requires location permission", LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void dealWithLocation()
    {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location)
                {
                    if(location != null) {
                        // we got permissions
                        updateUi(location);
                    }
                    else
                    {
                        Log.i("We have permission", "location is null***************************************************");
                    }
                }
            });
        }
        else
        {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }
        }
    }

    private void updateUi(Location location)
    {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        Toast.makeText(this, "Latitude: " + latitude + " Longitude: " + longitude, LENGTH_SHORT).show();

    }
}
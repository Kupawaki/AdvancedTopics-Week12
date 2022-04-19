package com.example.gps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity
{
    //Hooks
    TextView latTV, lonTV, altitudeTV, accuracyTV, speedTV, sensorTV, updatesTV, addressTV;
    Switch locationSW, gpsSW;

    //Instance Variables
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;

    //Constants
    public static final int SLOW_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 5;
    public static final int PERMISSIONS_FINE_LOCATION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Hooks
        latTV      = findViewById(R.id.latTV);
        lonTV      = findViewById(R.id.lonTV);
        altitudeTV = findViewById(R.id.altitudeTV);
        accuracyTV = findViewById(R.id.accuracyTV);
        speedTV    = findViewById(R.id.speedTV);
        sensorTV   = findViewById(R.id.sensorTV);
        updatesTV  = findViewById(R.id.updatesTV);
        addressTV  = findViewById(R.id.addressTV);
        locationSW = findViewById(R.id.locationSW);
        gpsSW      = findViewById(R.id.gpsSW);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000 * SLOW_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        gpsSW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(gpsSW.isChecked())
                {
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    sensorTV.setText("GPS");
                }
                else
                {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    sensorTV.setText("Cell Towers + WIFI");
                }
            }
        });

        updateGPS();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case PERMISSIONS_FINE_LOCATION:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    updateGPS();
                }
                else
                {
                    Toast.makeText(this, "The app requires permissions to br granted", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    public void updateGPS()
    {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location)
                        {
                            updateUI(location);
                        }
                    }
            );
        }
        else
        {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
    }

    public void updateUI(Location location)
    {
        latTV.setText(String.valueOf(location.getLatitude()));
        lonTV.setText(String.valueOf(location.getLongitude()));
        accuracyTV.setText(String.valueOf(location.getAccuracy()));

        if(location.hasAltitude())
        {
            altitudeTV.setText(String.valueOf(location.getAltitude()));
        }

        if(location.hasSpeed())
        {
            speedTV.setText(String.valueOf(location.getSpeed()));
        }
    }
}
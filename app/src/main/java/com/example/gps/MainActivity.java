package com.example.gps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.List;

@SuppressLint({"SetTextI18n", "UseSwitchCompatOrMaterialCode"})
public class MainActivity extends AppCompatActivity {
    //Hooks
    TextView latTV, lonTV, altitudeTV, accuracyTV, speedTV, sensorTV, updatesTV, addressTV;
    Switch locationSW, gpsSW;

    //Instance Variables
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallBack;

    //Constants
    public static final int SLOW_UPDATE_INTERVAL = 5;
    public static final int FAST_UPDATE_INTERVAL = 1;
    public static final int PERMISSIONS_FINE_LOCATION = 99;

    //Log Tags
    public static final String LOCA = "LOCATION";
    public static final String GEO = "GEOCODER";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(LOCA, "Created");

        //Hooks
        latTV = findViewById(R.id.latTV);
        lonTV = findViewById(R.id.lonTV);
        altitudeTV = findViewById(R.id.altitudeTV);
        accuracyTV = findViewById(R.id.accuracyTV);
        speedTV = findViewById(R.id.speedTV);
        sensorTV = findViewById(R.id.sensorTV);
        updatesTV = findViewById(R.id.updatesTV);
        addressTV = findViewById(R.id.addressTV);
        locationSW = findViewById(R.id.locationSW);
        gpsSW = findViewById(R.id.gpsSW);

        //Location config
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000 * SLOW_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        //Triggers when update interval is met
        locationCallBack = new LocationCallback()
        {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult)
            {
                super.onLocationResult(locationResult);
                updateUI(locationResult.getLastLocation());
                Log.d(LOCA, "LocationCallBack - OnLocationResult");
            }

            @Override
            public void onLocationAvailability(@NonNull LocationAvailability locationAvailability)
            {
                super.onLocationAvailability(locationAvailability);
                Log.d(LOCA, "LocationCallBack - OnLocationAvailability");
            }
        };

        gpsSW.setOnClickListener(view ->
        {
            if (gpsSW.isChecked()) {
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                sensorTV.setText("GPS");
            } else {
                locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                sensorTV.setText("Cell Towers + WIFI");
            }
        });

        locationSW.setOnClickListener(view ->
        {
            if (locationSW.isChecked()) {
                startLocationUpdates();
            } else {
                stopLocationUpdates();
            }
        });

        updateGPS();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_FINE_LOCATION)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                updateGPS();
            }
            else
            {
                Toast.makeText(this, "The app requires permissions to br granted", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    public void updateGPS()
    {
        Log.d(LOCA, "UpdateGPS");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            Log.d(LOCA, "UpdateGPS - Getting last location");
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, this::updateUI);
        }
        else
        {
            Log.d(LOCA, "UpdateGPS - Requesting permission");
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
        }
    }

    public void updateUI(Location location)
    {
        //Location can be null for a couple reasons,
        //Location service is off
        //Location has never been discovered before
        if (location == null)
        {
            Log.d(LOCA, "UpdateUI - Location is null");
            return;
        }

        Log.d(LOCA, "UpdateUI - Location is not null");

        latTV.setText(String.valueOf(location.getLatitude()));
        lonTV.setText(String.valueOf(location.getLongitude()));

        if (location.hasAccuracy()) {
            accuracyTV.setText(String.valueOf(location.getAccuracy()));
        }
        if (location.hasAltitude()) {
            altitudeTV.setText(String.valueOf(location.getAltitude()));
        }
        if (location.hasSpeed()) {
            speedTV.setText(String.valueOf(location.getSpeed()));
        }

        Geocoder geocoder = new Geocoder(MainActivity.this);

        try
        {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            addressTV.setText(addresses.get(0).getAddressLine(0));
        }
        catch (Exception e)
        {
            Log.e(GEO, e.getMessage());
        }
    }

    public void startLocationUpdates()
    {
        updatesTV.setText("ON");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
            updateGPS();
        }
    }

    public void stopLocationUpdates()
    {
        updatesTV.setText("OFF");

        latTV.setText("Location is not being tracked");
        lonTV.setText("Location is not being tracked");
        accuracyTV.setText("Location is not being tracked");
        altitudeTV.setText("Location is not being tracked");
        speedTV.setText("Location is not being tracked");
        addressTV.setText("Location is not being tracked");
        sensorTV.setText("Location is not being tracked");

        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
    }
}
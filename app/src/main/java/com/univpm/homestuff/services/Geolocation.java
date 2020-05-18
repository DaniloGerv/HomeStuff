package com.univpm.homestuff.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.univpm.homestuff.callbacks.LocationCallBack;
import com.univpm.homestuff.utilities.Codes;



public class Geolocation extends AppCompatActivity {

    private FusedLocationProviderClient mFusedLocationClient;

    public Geolocation(Activity sender) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(sender);
    }

    private boolean checkPermissions(Activity sender) {
        if (ActivityCompat.checkSelfPermission(sender, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(sender, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions(Activity sender) {
        ActivityCompat.requestPermissions(
                sender,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                Codes.GEO_PERMISSION
        );
    }


    private boolean isLocationEnabled(Activity sender) {
        LocationManager locationManager = (LocationManager) sender.getSystemService(sender.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }


    @SuppressLint("MissingPermission")
    public void getLastLocation(final Activity sender, final LocationCallBack myCallBack) {
        if (checkPermissions(sender)) {
            if (isLocationEnabled(sender)) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData(sender, myCallBack);
                                } else {
                                    myCallBack.onCallbackLocation(location);

                                }
                            }
                        });
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        }
         else {
            requestPermissions(sender);
        }
    }



    //Requesting data when location is null, sometimes could happen
    @SuppressLint("MissingPermission")
    private void requestNewLocationData(final Activity sender, final LocationCallBack myCallBack){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(sender);
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {

                Location location = task.getResult();
                if (location!=null) {
                    myCallBack.onCallbackLocation(location);
                }
                else {
                    Log.d("GEO","repeat");
                    requestNewLocationData(sender, myCallBack);
                }
            }
        });
    }


}

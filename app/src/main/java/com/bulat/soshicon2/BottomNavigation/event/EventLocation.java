package com.bulat.soshicon2.BottomNavigation.event;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class EventLocation implements LocationListener {
    boolean gps_enabled = false;
    Activity activity;
    Context context;
    android.location.Location location = null;
    double logitude;

    public double getLogitude() {
        return logitude;
    }

    public double getLatitude() {
        return latitude;
    }

    double latitude;

    public EventLocation(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        CheckLocation();
    }

    private void CheckLocation() {
        LocationManager locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);

        while (location == null) {


            try {
                gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!gps_enabled) {
                new AlertDialog.Builder(context)
                        .setMessage("GPS Enable")
                        .setPositiveButton("Settings", new
                                DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                        context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                    }
                                })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            }
            else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

                location=locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }

        }



        latitude = location.getLatitude();
        logitude = location.getLongitude();
    }

    @Override
    public void onLocationChanged(@NonNull android.location.Location location) {
        latitude = location.getLatitude();
        logitude = location.getLongitude();
    }
}

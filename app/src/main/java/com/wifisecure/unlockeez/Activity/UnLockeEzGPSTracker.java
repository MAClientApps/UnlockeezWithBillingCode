package com.wifisecure.unlockeez.Activity;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import androidx.core.app.ActivityCompat;

public class UnLockeEzGPSTracker extends Service implements LocationListener {
    private final Context UnLockeEzContextThisGPS;
    boolean unLockeEzGPSEnabled = false;
    boolean UnLockeEzNetworkEnabled = false;
    boolean UnLockeEzLocationBool = false;
    private Location UnLockeEzLocation;
    private double latitude,longitude;
    LocationManager locationWifiUnlockerManager;
    public UnLockeEzGPSTracker(Context UnLockeEzContextThisGPS){
        this.UnLockeEzContextThisGPS = UnLockeEzContextThisGPS;
        getLocation();
    }
    private void getLocation() {
        try {
            locationWifiUnlockerManager = (LocationManager) UnLockeEzContextThisGPS.getSystemService(Context.LOCATION_SERVICE);
            unLockeEzGPSEnabled = locationWifiUnlockerManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            UnLockeEzNetworkEnabled = locationWifiUnlockerManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!unLockeEzGPSEnabled && !UnLockeEzNetworkEnabled){
                try {
                    unLockeEzGPSEnabled = locationWifiUnlockerManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    UnLockeEzNetworkEnabled = locationWifiUnlockerManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                }catch (Exception e){
                    e.printStackTrace();
                }
            } else {
                this.UnLockeEzLocationBool = true;
                if (UnLockeEzNetworkEnabled){
                    if(ActivityCompat.checkSelfPermission(UnLockeEzContextThisGPS, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        locationWifiUnlockerManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 3, this);
                        if (locationWifiUnlockerManager != null){
                            UnLockeEzLocation = locationWifiUnlockerManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (UnLockeEzLocation != null){
                                latitude = UnLockeEzLocation.getLatitude();
                                longitude = UnLockeEzLocation.getLongitude();
                            }
                        }
                    }
                }
                if (unLockeEzGPSEnabled){
                    if (UnLockeEzLocation == null){
                        assert locationWifiUnlockerManager != null;
                        locationWifiUnlockerManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 3, this);
                        if (locationWifiUnlockerManager != null){
                            UnLockeEzLocation = locationWifiUnlockerManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (UnLockeEzLocation != null){
                                latitude = UnLockeEzLocation.getLatitude();
                                longitude = UnLockeEzLocation.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public double getLatitude(){
        if (UnLockeEzLocation != null){
            latitude = UnLockeEzLocation.getLatitude();
        }
        return latitude;
    }
    public double getLongitude(){
        if (UnLockeEzLocation != null){
            longitude = UnLockeEzLocation.getLongitude();
        }
        return longitude;
    }
    @Override
    public void onLocationChanged(Location location) {
        if (location != null){
            this.UnLockeEzLocation = location;
        }
    }
    @Override
    public void onProviderDisabled(String provider) {
    }
    @Override
    public void onProviderEnabled(String provider) {
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

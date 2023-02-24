package fr.java.aoitechnicien.Function;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class LocListener {
    private LocationManager locationManager;
    private LocationListener locationListener;
    Context context;

    public LocListener(Context context) {
        this.context = context;
    }

    private void startLocationUpdates() {
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                // Do something with the location...
                Log.i("DEBUG_LOCATION", String.valueOf(latitude));
                Log.i("DEBUG_LOCATION", String.valueOf(longitude));
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    private void stopLocationUpdates() {
        locationManager.removeUpdates(locationListener);
    }
}

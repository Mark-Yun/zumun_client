package com.mark.zumo.client.core.provider;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.mark.zumo.client.core.util.context.ContextHolder;

import io.reactivex.Observable;

/**
 * Created by mark on 18. 5. 20.
 */
public enum AppLocationProvider {

    INSTANCE;

    private static final String TAG = "AppLocationProvider";
    private LocationManager locationManager;
    private Context context;

    AppLocationProvider() {
        context = ContextHolder.getContext();
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public Observable<Location> currentLocation() {
        return Observable.create(e -> {
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Log.d(TAG, "isGPSEnabled=" + isGPSEnabled);
            Log.d(TAG, "isNetworkEnabled=" + isNetworkEnabled);

            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    e.onNext(location);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            };

            // Register the listener with the Location Manager to receive location updates
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            String locationProvider = LocationManager.GPS_PROVIDER;
            Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
            if (lastKnownLocation != null) {
                e.onNext(lastKnownLocation);
            }

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        });
    }
}

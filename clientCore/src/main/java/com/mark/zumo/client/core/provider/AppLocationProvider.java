/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
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
import io.reactivex.ObservableEmitter;

/**
 * Created by mark on 18. 5. 20.
 */
public enum AppLocationProvider {

    INSTANCE;

    private static final String TAG = "AppLocationProvider";
    private final LocationManager locationManager;
    private final Context context;

    public final Observable<Location> currentLocationObservable;
    private Location location;

    AppLocationProvider() {
        context = ContextHolder.getContext();
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        currentLocationObservable = currentLocation();
    }

    private Observable<Location> currentLocation() {
        return Observable.create((ObservableEmitter<Location> e) -> {
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Log.d(TAG, "isGPSEnabled=" + isGPSEnabled);
            Log.d(TAG, "isNetworkEnabled=" + isNetworkEnabled);

            if (location != null) {
                e.onNext(location);
                e.onComplete();
            }

            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    e.onNext(location);
                    AppLocationProvider.this.location = location;
                    e.onComplete();
                    locationManager.removeUpdates(this);
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

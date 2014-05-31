package com.indyhack.civicpotholes;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;


/** Class which handles returns a user's location when required.
 *  Create it normally.
 *  Calls to getLat() or getLng() should be enclosed in an if(isLocationAvailable()) statement.
 *  Call disconnect() when you're finished with it. */

public class UserLocationManager
        implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

    /** Stores the user's location at all times */
    private LocationClient userLocation;

    /** Whether or not the location is currently good */
    private boolean isAvailable = false;


    MainActivity main;
    /** Constructor. Pass in context of activity. */
    public UserLocationManager(Context c, MainActivity act) {
        main = act;


        userLocation = new LocationClient(c, this, this);
        userLocation.connect();
    }

    /** Returns whether the location is currently available */
    public boolean isLocationAvailable() {
        if (userLocation.getLastLocation() != null) {
            return isAvailable;
        }
        return false;
    }

    /** Call this when you're finished */
    public void disconnect() {
        userLocation.disconnect();
    }

    /** Returns the user's latitude */
    public double getLat() {
        return userLocation.getLastLocation().getLatitude();
    }

    /** Returns the user's longitude */
    public double getLng() {
        return userLocation.getLastLocation().getLongitude();
    }

    @Override
    public void onConnected(Bundle bundle) {
        isAvailable = true;


        Log.v("MainActivity", "Zooming camera!!!");
        if(main.getMap() == null) {
            Log.e("LocationClient","map" + " is null!");
            return;
        }

        CameraPosition.Builder cameraPositionBuilder = new CameraPosition.Builder();
	        /*if(mLocationClient.getLastLocation() == null) {
	            Log.e("LocationClient", "Last location is null!");
	            mLocationClient.disconnect();
	            return;
	        }*/
        if(isLocationAvailable()) {
            cameraPositionBuilder.target(new LatLng(getLat(), getLng()));
            cameraPositionBuilder.zoom((float) 12);
            main.getMap().animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPositionBuilder.build()), new GoogleMap.CancelableCallback() {
                @Override
                public void onCancel() {
                }

                @Override
                public void onFinish() {
                    Toast.makeText(main, "Finished zoom!", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onDisconnected() {
        isAvailable = false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        isAvailable = false;
    }
}
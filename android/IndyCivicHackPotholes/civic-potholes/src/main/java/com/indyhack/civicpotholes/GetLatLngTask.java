package com.indyhack.civicpotholes;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by bwencke on 5/31/14.
 */
public class GetLatLngTask extends AsyncTask<String, Integer, LatLng> {

    Activity activity;
    static BitmapDescriptor icon;

    public GetLatLngTask(Activity a) {
       activity = a;
        if(icon == null) {
            icon = BitmapDescriptorFactory.fromResource(R.drawable.pothole);
        }
    }

    @Override
    protected LatLng doInBackground(String... params) {
        Geocoder geoCoder = new Geocoder(activity, Locale.getDefault());
        try {
            List<Address> address = geoCoder.getFromLocationName(params[0], 1);
            if(address.size() >=1 ) {
                double latitude = address.get(0).getLatitude();
                double longitude = address.get(0).getLongitude();

                return new LatLng(latitude, longitude);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values[0]);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onPostExecute(final LatLng latLng) {
        super.onPostExecute(latLng);
           if(latLng != null) {
               Log.i("LATLNG", "Lat: " + latLng.latitude + ", Lng: " + latLng.longitude);
               activity.runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       GoogleMap map = ((MapActivity) activity).getMap();
                       if(map != null) {
                           map.addMarker(new MarkerOptions().position(latLng).icon(icon));
                       }
                       else
                           Log.e("Marker", "map is null");
                   }
               });
               // add to map
           }
    }
}

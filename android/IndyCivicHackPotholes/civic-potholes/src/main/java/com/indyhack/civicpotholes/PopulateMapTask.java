package com.indyhack.civicpotholes;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by david on 5/31/14 for IndyCivicHackPotholes
 */
public class PopulateMapTask extends AsyncTask<Void, Integer, List<LatLng>> {

    Context context;

    public PopulateMapTask(Context c)
    {
        context = c;
    }

    @Override
    protected List<LatLng> doInBackground(Void... params) {
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    private LatLng addressToLatLng(String name)
    {
        Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> address = geoCoder.getFromLocationName(name, 1);
            double latitude = address.get(0).getLatitude();
            double longitude = address.get(0).getLongitude();

            return new LatLng(latitude, longitude);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

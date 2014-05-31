package com.indyhack.civicpotholes;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by david on 5/31/14 for IndyCivicHackPotholes
 */
public class PopulateMapTask extends AsyncTask<Void, Integer, List<LatLng>> {
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
}

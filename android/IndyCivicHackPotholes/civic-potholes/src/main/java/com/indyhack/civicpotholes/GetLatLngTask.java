package com.indyhack.civicpotholes;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;

/**
 * Created by bwencke on 5/31/14.
 */
public class GetLatLngTask extends AsyncTask<String, Integer, LatLng> {

    Context context;

    public GetLatLngTask(Context c)
    {
        context = c;
    }

    @Override
    protected LatLng doInBackground(String... params) {
        Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
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
    protected void onPostExecute(LatLng latLng) {
        super.onPostExecute(latLng);
           if(latLng != null) {
               Log.i("LATLNG", "Lat: " + latLng.latitude + ", Lng: " + latLng.longitude);
               // add to map
           }
    }
}

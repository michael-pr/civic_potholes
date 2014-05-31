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
        HttpResponse response = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI("http://communities.socrata.com/resource/8f5a-5xkb.json?$select=incident_address&status=Open"));
            response = client.execute(request);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String result = null;
        try {
            result = convertStreamToString(response.getEntity().getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(result != null ) {
            try {
                JSONArray array = new JSONArray(result);
                for(int i = 0; i < array.length(); i++) {
                    JSONObject addr = array.getJSONObject(i);
                    new GetLatLngTask(context).execute(addr.getString("incident_address")+", Indianapolis, IN");
                    //publishProgress((int)(i / (float) array.length() * 100));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
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

    private String convertStreamToString(InputStream inputStream) throws IOException {
        if (inputStream != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"),1024);
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                inputStream.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }
}

package com.indyhack.civicpotholes.task;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

/**
 * Created by mike on 5/31/14.
 */
public class AddPotholeTask extends AsyncTask<Void, Void, Void> {

    private double lat;
    private double lng;

    public AddPotholeTask(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    protected Void doInBackground(Void... params) {

        HttpClient c = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://aqueous-citadel-2041.herokuapp.com/addpothole");

        String contents = "{\"latitude\":"+lat+",\"longitude\":"+lng+"}";
        HttpEntity entity = new ByteArrayEntity(contents.getBytes());
        post.setEntity(entity);

        try {
            c.execute(post);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }

}

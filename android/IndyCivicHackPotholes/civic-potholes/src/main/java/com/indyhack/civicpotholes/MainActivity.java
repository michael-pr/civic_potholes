package com.indyhack.civicpotholes;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.indyhack.civicpotholes.service.PotholeDetectionService;
import com.indyhack.civicpotholes.service.SensorService;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context c = getBaseContext();
        PotholeDetectionService service = new PotholeDetectionService(c, new PotholeDetectionService.OnPotholeDetectedListener() {
            public void onPotholeDetected() {
                Log.d("asdf", "Pothole detected");
            }
        });
        service.start();

//        final Context c = getBaseContext();
//        new Thread(new Runnable() {
//            public void run() {
//                SensorService service = new SensorService(c);
//                while (true) {
//                    Log.d("asdf", "" + service.getLinearZAcceleration());
//                    try { Thread.sleep(10); } catch (InterruptedException e) {}
//                }
//            }
//        }).start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

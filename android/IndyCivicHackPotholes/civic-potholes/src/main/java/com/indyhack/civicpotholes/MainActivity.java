package com.indyhack.civicpotholes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.indyhack.civicpotholes.service.ActivityRecognitionIntentService;
import com.indyhack.civicpotholes.service.PotholeDetectionService;


public class MainActivity extends Activity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        MapActivity {


    // Constants that define the activity detection interval
    public static final int MILLISECONDS_PER_SECOND = 1000;
    public static final int DETECTION_INTERVAL_SECONDS = 20;
    public static final int DETECTION_INTERVAL_MILLISECONDS =
            MILLISECONDS_PER_SECOND * DETECTION_INTERVAL_SECONDS;


    UserLocationManager locManager;

    public enum REQUEST_TYPE {START, STOP}
    private REQUEST_TYPE mRequestType;


    /*
     * Store the PendingIntent used to send activity recognition events
     * back to the app
     */
    private PendingIntent mActivityRecognitionPendingIntent;
    // Store the current activity recognition client
    private ActivityRecognitionClient mActivityRecognitionClient;



    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleMap mMap;
    private LocationClient mLocationClient;
    private PopulateMapTask mPopulaterTask;

    public static SharedPreferences prefs;
    public static final String SHARED_PREFS_NAME = "civic_hack_potholes_prefs";
    public static final String PREF_ENABLE_POTHOLE_DETECTION = "enable_pothole_detection";


    // Flag that indicates if a request is underway.
    private boolean mInProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = getSharedPreferences(SHARED_PREFS_NAME, 0);


        locManager = new UserLocationManager(this, this);


        /*
         * Instantiate a new activity recognition client. Since the
         * parent Activity implements the connection listener and
         * connection failure listener, the constructor uses "this"
         * to specify the values of those parameters.
         */
        mActivityRecognitionClient =
                new ActivityRecognitionClient(this, this, this);
        /*
         * Create the PendingIntent that Location Services uses
         * to send activity recognition updates back to this app.
         */
        Intent intent = new Intent(
                this, ActivityRecognitionIntentService.class);
        /*
         * Return a PendingIntent that starts the IntentService.
         */
        mActivityRecognitionPendingIntent =
                PendingIntent.getService(this, 0, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);



        // Start with the request flag set to false
        mInProgress = false;





        final Context c = getBaseContext();
        PotholeDetectionService service = new PotholeDetectionService(c, new PotholeDetectionService.OnPotholeDetectedListener() {
            public void onPotholeDetected() {
                Toast.makeText(getApplicationContext(), "Pothole detected.", Toast.LENGTH_LONG).show();
                Log.d("civic-pothole-detection", "Pothole detected");

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.pothole)
                                .setContentTitle("Pothole Detected")
                                .setContentText("A pothole hole was detected and has been reported.");
                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(0, mBuilder.build());
            }
        });
        service.start();

        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapView)).getMap();
        mMap.setMyLocationEnabled(true);

        mPopulaterTask = new PopulateMapTask(MainActivity.this);
        mPopulaterTask.execute();

        mLocationClient = new LocationClient(this, locManager, locManager);

    }

    public void startUpdates() {
        // Set the request type to START
        mRequestType = REQUEST_TYPE.START;
        // If a request is not already underway
        if (!mInProgress) {
            // Indicate that a request is in progress
            mInProgress = true;
            // Request a connection to Location Services
            mActivityRecognitionClient.connect();
            //
        } else {
            /*
             * A request is already underway. You can handle
             * this situation by disconnecting the client,
             * re-setting the flag, and then re-trying the
             * request.
             */
        }
    }

    /**
     * Turn off activity recognition updates
     *
     */
    public void stopUpdates() {
        // Set the request type to STOP
        mRequestType = REQUEST_TYPE.STOP;
        // If a request is not already underway
        if (!mInProgress) {
            // Indicate that a request is in progress
            mInProgress = true;
            // Request a connection to Location Services
            mActivityRecognitionClient.connect();
            //
        } else {
            /*
             * A request is already underway. You can handle
             * this situation by disconnecting the client,
             * re-setting the flag, and then re-trying the
             * request.
             */
        }
    }


    public void onConnected(Bundle dataBundle) {
        switch (mRequestType) {
            case START :
                /*
                 * Request activity recognition updates using the
                 * preset detection interval and PendingIntent.
                 * This call is synchronous.
                 */
                mActivityRecognitionClient.requestActivityUpdates(
                        DETECTION_INTERVAL_MILLISECONDS,
                        mActivityRecognitionPendingIntent);
                break;
            case STOP :
                mActivityRecognitionClient.removeActivityUpdates(
                        mActivityRecognitionPendingIntent);
                break;
        }
    }

    /**
     * Called when we fail to connect to the mLocationClient.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.isSuccess() == true)
            return;

        if (connectionResult.hasResolution() == true) {
            try {
                connectionResult.startResolutionForResult(this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (SendIntentException e) {
                e.printStackTrace();
                this.onConnectionFailed(connectionResult); // HACK: Possible stack overflow.
            }
        } else {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setTitle("Error connection to GPS");
            alertBuilder.setMessage("Connection result: " + connectionResult.toString());
            alertBuilder.show();
        }
    }


    @Override
    public void onDisconnected() {
        mLocationClient.connect();

        // Turn off the request flag
        mInProgress = false;
        // Delete the client
        mActivityRecognitionClient = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopUpdates();

        if (mLocationClient != null && mLocationClient.isConnected()) {
            mLocationClient.disconnect();
        }
        if( mPopulaterTask != null && !mPopulaterTask.isCancelled())
        {
            mPopulaterTask.cancel(true);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        startUpdates();

        if(mMap != null)
        {
            mMap.clear();
            mPopulaterTask = new PopulateMapTask(MainActivity.this);
            mPopulaterTask.execute();
        }
        if (mLocationClient != null && (!mLocationClient.isConnected() && !mLocationClient.isConnecting())) {
            mLocationClient.connect();
        }
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
        if (id == R.id.action_settings)
        {
            return true;
        } else if (id == R.id.detect_toggle)
        {
            item.setChecked( !item.isChecked() );
            prefs.edit().putBoolean(PREF_ENABLE_POTHOLE_DETECTION, item.isChecked()).commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public GoogleMap getMap() {
        return mMap;
    }

}

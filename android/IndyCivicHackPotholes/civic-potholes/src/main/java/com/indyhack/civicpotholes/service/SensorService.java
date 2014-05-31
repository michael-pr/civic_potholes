package com.indyhack.civicpotholes.service;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

/**
 * Created by mike on 5/31/14.
 */
public class SensorService {

    private SensorManager sensorManager;
    private Sensor sensor;

    public SensorService(Context c) {
        sensorManager = (SensorManager) c.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    }

    public void getLinearAcceleration() {

    }


}

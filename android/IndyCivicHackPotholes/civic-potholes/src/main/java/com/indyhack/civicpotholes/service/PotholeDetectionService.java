package com.indyhack.civicpotholes.service;

import android.content.Context;
import android.hardware.Sensor;
import android.util.Log;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by mike on 5/31/14.
 */
public class PotholeDetectionService {

    public interface OnPotholeDetectedListener {
        public void onPotholeDetected();
    }


    private Context c;
    private OnPotholeDetectedListener listener;
    private List<Double> linearAccelerationValues;

    private double average;
    private int n = 0;

    boolean upperFound = false, lowerFound = false;


    public PotholeDetectionService(Context c, OnPotholeDetectedListener listener) {
        this.c = c;
        this.listener = listener;
        linearAccelerationValues = Collections.synchronizedList(new LinkedList<Double>());
    }

    public void addLinearAccelerationValue(double value) {
        // Add the value to the moving average
        if (linearAccelerationValues.size() > 10) {
            linearAccelerationValues.remove(0);
        }
        linearAccelerationValues.add(value);


        // Determine the average
        for (double d : linearAccelerationValues) {
            average += d;
        }
        average /= 10;

        if (average > 3) {
            upperFound = true;
        }
        if (average < -3) {
            if (upperFound) {
                lowerFound = true;
            }
        }
        if (average > -0.5 && average < 0.5) {
            upperFound = false;
            lowerFound = false;
        }
        if (upperFound && lowerFound) {
            listener.onPotholeDetected();
            fullPurge();
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
            }
            Log.d("asdf", "Woke up");
        }


    }
    
    public void start() {
        final SensorService service = new SensorService(c);
        new Thread(new Runnable() {
            public void run() {
                while (true) {

                    // Update the data with a new reading
                    double z = service.getLinearZAcceleration();
                    //Log.d("asdf", "" + z);
                    addLinearAccelerationValue(z);

                    // Wait a bit of time
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                    }

                }
            }
        }).start();
    }

    private boolean analyzeData() {
        // Look through the last twenty points of data
        // Look for a pattery where we had points lower than zero, zero, than higher than zero
        int i;
        boolean lowerBound = false, zero = false, upperBound = false;
        for (i = linearAccelerationValues.size()-1; i > 0; i--) {
            double currentValue = linearAccelerationValues.get(i);
            if (currentValue < -5) {
                lowerBound = true;
            }
            if (currentValue > -0.5 && currentValue < 0.5) {
                if (lowerBound && !upperBound) {
                    zero = true;
                } else {
                    lowerBound = false;
                    upperBound = false;
                }
            }
            if (currentValue > 2) {
                if (lowerBound && zero) {
                    upperBound = true;
                } else {
                    lowerBound = false;
                    zero = false;
                }
            }
            if (lowerBound && zero && upperBound) {
                return true;
            }
        }

        return false;
    }

    private void fullPurge() {
        linearAccelerationValues = Collections.synchronizedList(new LinkedList<Double>());
    }

    private void purge() {
        if (linearAccelerationValues.size() > 100) {
            for (int i = 0; i < 50; i++) {
                linearAccelerationValues.remove(0);
            }
        }
    }





}

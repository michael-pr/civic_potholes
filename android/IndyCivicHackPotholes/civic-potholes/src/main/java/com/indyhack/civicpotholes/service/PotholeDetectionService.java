package com.indyhack.civicpotholes.service;

import android.content.Context;
import android.hardware.Sensor;
import android.util.Log;

import java.util.ArrayList;
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

    private double prev = 0, current = 0;
    private boolean upPeakFound = false, downPeakFound = false;
    private int nIncreasing = 0, nDecreasing;

    public PotholeDetectionService(Context c, OnPotholeDetectedListener listener) {
        this.c = c;
        this.listener = listener;
        linearAccelerationValues = Collections.synchronizedList(new ArrayList<Double>());
    }

    public void addLinearAccelerationValue(double value) {
        // Add the value to the list of values
        if (linearAccelerationValues.size() > 1000) {
            linearAccelerationValues.remove(0);
        }
        linearAccelerationValues.add(value);

//
//        // Determine the average
//        for (double d : linearAccelerationValues) {
//            average += d;
//        }
//        average /= 5;
//
//        // Find the derivative between the first and the last ones we are watching (5 items, 50ms)
//        double d = linearAccelerationValues.get(linearAccelerationValues.size()) - linearAccelerationValues.get(0) / 5;
//        if (d > 2) {
//            // LA is significantly increasing
//
//        }
//
//        if (current > prev) {
//            nIncreasing++;
//        }
//
//        if (average > 1) {
//            upperFound = true;
//        }
//        if (average < -1) {
//            if (upperFound) {
//                lowerFound = true;
//            }
//        }
//        if (average > -0.5 && average < 0.5) {
//            upperFound = false;
//            lowerFound = false;
//        }
//        if (upperFound && lowerFound) {
//            listener.onPotholeDetected();
//            fullPurge();
//            upperFound = false;
//            lowerFound = false;
//            try {
//                Thread.sleep(10000);
//            } catch (InterruptedException e) {
//            }
//            Log.d("civic-pothole-detection", "Woke up");
//        }


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
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                    }

                    analyzeData();

                }
            }
        }).start();
    }

    private boolean analyzeData() {

        // We log 20 points of data at a time.
        // Through testing, we've determined that the upward tick of a pothole hit takes
        // roughly 30 ms (3 points of data). Then the downward tick takes ~50ms (5 points ot data).

        // Take a partial derivative of every piece of 3 points of data, looking for significant
        // increases in linear acceleration

        int N_INCREASE = 2;
        double D_INCREASE_THRESH = 2;
        int N_DECREASE = 2;
        double D_DECREASE_THRESH = -2;


        boolean sigIncreaseFound = false;
        int at = -1;
        for (int i = 0; i < linearAccelerationValues.size()-N_INCREASE; i+=N_INCREASE) {
            double start = linearAccelerationValues.get(i);
            double end = linearAccelerationValues.get(i+N_INCREASE);
            double d = (end - start) / (N_INCREASE+1);

            if (d > D_INCREASE_THRESH) {
                sigIncreaseFound = true;
                at = i;
                break;
            }
        }

        // If we found a significant increase, next look for a significant decrease
        // which takes place over around 5 points of data.

        boolean sigDecreaseFound = false;
        if (sigIncreaseFound) {
            for (int i = at; i < linearAccelerationValues.size() - N_DECREASE; i += N_DECREASE) {
                double start = linearAccelerationValues.get(i);
                double end = linearAccelerationValues.get(i + N_DECREASE);
                double d = (end - start) / (N_DECREASE+1);

                if (d < D_DECREASE_THRESH) {
                    sigDecreaseFound = true;
                    break;
                }
            }
        }

        // If we found both, register a pothole as hit
        if (sigIncreaseFound && sigDecreaseFound) {
            registerHit();
            return true;
        }

        return false;
    }

    private void fullPurge() {
        linearAccelerationValues = Collections.synchronizedList(new LinkedList<Double>());
    }

    private void registerHit() {
        listener.onPotholeDetected();
        fullPurge();
        try {Thread.sleep(10000); } catch (InterruptedException e) {}
        Log.d("civic-pothole-detection", "Continuing pothole detection");
    }





}

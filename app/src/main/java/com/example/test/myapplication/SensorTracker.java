package com.example.test.myapplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by clement.lafleur on 09/11/2015.
 */
public class SensorTracker extends Service implements SensorEventListener {
    private final Context mContext;
    private SensorManager mSensorManager;
    private final Sensor sensor;
    private float[] currentValue;

    public SensorTracker(Context context, Sensor sensor) {
        this.mContext = context;
        this.sensor = sensor;
    }

    public void start() {
        mSensorManager = (SensorManager) mContext.getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(this, getSensor(), 1000);
    }

    public void stop() {
        mSensorManager.unregisterListener(this);
    }

    private float getValue(int index) {
        if (currentValue == null || currentValue.length < index + 1)
            return 0;
        return currentValue[index];
    }

    public float getX() {
        return getValue(0);
    }

    public float getY() {
        return getValue(1);
    }

    public float getZ() {
        return getValue(2);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        currentValue = event.values;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public Sensor getSensor() {
        return sensor;
    }

}

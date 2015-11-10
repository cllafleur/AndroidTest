package com.example.test.myapplication;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Main2Activity extends AppCompatActivity {

    private GPSTracker gps;
    private TextView textView2;
    private Timer timer;
    private List<SensorTracker> sensors = new ArrayList<SensorTracker>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView2.setMovementMethod(new ScrollingMovementMethod());
        timer = new Timer();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gps != null) {
                    gps.stopUsingGPS();
                }
                if (sensors.size() > 0) {
                    stopSensors();
                }
                gps = new GPSTracker(Main2Activity.this);
                startSensors();
                if (gps.canGetLocation()) {
                    Snackbar.make(view, "GPS Started", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    showLocation();
                    timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                public void run() {
                                    showLocation();
                                    showSensors();
                                }
                            });
                        }
                    }, 10000, 2000);
                } else {
                    Snackbar.make(view, "Unable to get location.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    gps.showSettingsAlert();
                }

            }
        });
    }

    private void showLocation() {
        double latitude = gps.getLatitude();
        double longitude = gps.getLongitude();

        textView2.setText("GPS\nLat: " + latitude + "\nLong: " + longitude + "\n");
        Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
    }

    private void showSensors() {
        for (SensorTracker tracker : sensors) {
            Sensor sensor = tracker.getSensor();
            double gx = tracker.getX();
            double gy = tracker.getY();
            double gz = tracker.getZ();
            String str = sensor.getStringType().replace("android.sensor.", "");
            textView2.append(str + "\nX: " + gx + "\nY: " + gy + "\nZ: " + gz + "\n");
        }
    }

    private void startSensors() {
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

        for (Sensor sensor : sensorList) {
            Sensor current;
            if ((current = sensorManager.getDefaultSensor(sensor.getType())) != null) {
                SensorTracker tracker = new SensorTracker(Main2Activity.this, current);
                sensors.add(tracker);
                tracker.start();
            }
        }
    }

    private void stopSensors() {
        for (SensorTracker tracker : sensors) {
            tracker.stop();
        }
        sensors.clear();
    }
}

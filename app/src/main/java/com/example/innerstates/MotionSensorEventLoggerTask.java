package com.example.innerstates;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.AsyncTask;
import android.util.Log;

public class MotionSensorEventLoggerTask extends AsyncTask<SensorEvent, Void, Void> {

    private static final String DEBUG_TAG = "MotionLoggerService";


    @Override
    protected Void doInBackground(SensorEvent... sensorEvents) {
        SensorEvent event = sensorEvents[0];

        int sensorType = event.sensor.getType();
        Sensor sensor = event.sensor;

        // log the value
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                String valueAccel = event.values[0] + ", " + event.values[1] + ", " + event.values[2];
                Log.d(DEBUG_TAG, "ACCEL____ " + valueAccel);

                break;
            case Sensor.TYPE_GYROSCOPE:
                String valueGyro = event.values[0] + ", " + event.values[1] + ", " + event.values[2];
//                Log.d("Gryo.... ", valueGyro);
                break;

        }
        return null;
    }


}

package com.example.innerstates;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class MotionLoggerService extends Service implements SensorEventListener {

    private static final String DEBUG_TAG = "MotionLoggerService";

    private SensorManager mSensorManager = null;
    private Sensor sensor = null;

    private ArrayList<String> sensorList = new ArrayList<>();
    private HashMap<String, MySensor> mSensors = new HashMap<>();

    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = database.getReference();
    private SharedPreferences sharedPref;
    private String userInviteId;
    private String userUniqueId;

    @Override
    public void onCreate() {
        Log.d(DEBUG_TAG, "00000....---->> onCreate MotionLoggerService");


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedPref = getSharedPreferences(getString(R.string.preference_file_key),
                MODE_PRIVATE);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        Log.d(DEBUG_TAG, "---------START SERVICE");

        userInviteId = getInviteUserId();
        userUniqueId = getUserUniqueId();

        sensorList.add("ACCELEROMETER");
        sensorList.add("GYROSCOPE");
        sensorList.add("GRAVITY");
        sensorList.add("LINEAR_ACCEL");
        sensorList.add("LIGHT");
        sensorList.add("ROTATION_VECTOR");
        sensorList.add("MAGNETIC_FIELD");
        sensorList.add("PROXIMITY");
        sensorList.add("AMBIENT_TEMPERATURE");

        addSensor("ACCELEROMETER", Sensor.TYPE_ACCELEROMETER);
        addSensor("GYROSCOPE", Sensor.TYPE_GYROSCOPE);
        addSensor("GRAVITY", Sensor.TYPE_GRAVITY);
        addSensor("LINEAR_ACCEL", Sensor.TYPE_LINEAR_ACCELERATION);
        addSensor("LIGHT", Sensor.TYPE_LIGHT);
        addSensor("ROTATION_VECTOR", Sensor.TYPE_ROTATION_VECTOR);
        addSensor("MAGNETIC_FIELD", Sensor.TYPE_MAGNETIC_FIELD);
        addSensor("PROXIMITY", Sensor.TYPE_PROXIMITY);
        addSensor("AMBIENT_TEMPERATURE", Sensor.TYPE_AMBIENT_TEMPERATURE);


        for (MySensor sensor : mSensors.values()) {
            if (sensor.getSensor() != null) {
                mSensorManager.registerListener(this, sensor.getSensor(), SensorManager.SENSOR_DELAY_NORMAL);

//                mSensorManager.registerListener(this, sensor.getSensor(), 1000000, 1000000);
            }
        }

        writeSensorMetaDataFireBase();

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // grab the values and timestamp -- off the main thread
        new MotionSensorEventLoggerTask(userInviteId).execute(sensorEvent);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void writeSensorMetaDataFireBase() {

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (userUniqueId == null) {
                    userUniqueId = getUserUniqueId();
                }
                if (!snapshot.child("users").child(userUniqueId).hasChild("sensor_meta")) {

                    for (MySensor sensor : mSensors.values()) {
                        if (sensor.getSensor() != null) {
                            Map<String, Object> postValues = sensor.toMap();

                            mDatabase.child("users").child(userUniqueId).child("sensor_meta").push().setValue(postValues);
                        }
                    }



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public String getInviteUserId() {
        return sharedPref.getString(getString(R.string.invitation_user_id), "nodata");
    }

    public String getUserUniqueId() {
        return sharedPref.getString(getString(R.string.user_unique_id), "nodata");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    private void addSensor(String name, int type) {

        MySensor mySensor = new MySensor();
        mySensor.setSensor(mSensorManager.getDefaultSensor(type));
        mySensor.setName(name);
        mSensors.put(mySensor.getName(), mySensor);
    }

    @Override
    public void onDestroy() {
        Log.d(DEBUG_TAG, "----onDestroy");
        for (MySensor sensor : mSensors.values()) {
            if (sensor.getSensor() != null) {
                mSensorManager.unregisterListener(this, sensor.getSensor());
            }
        }
    }
}

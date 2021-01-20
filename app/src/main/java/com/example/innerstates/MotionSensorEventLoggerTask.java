package com.example.innerstates;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MotionSensorEventLoggerTask extends AsyncTask<SensorEvent, Void, Void> {

    private static final String DEBUG_TAG = "MotionLoggerService";
    private String userUniqueId;


    public MotionSensorEventLoggerTask() {

    }

    public MotionSensorEventLoggerTask(String userId) {
        userUniqueId = userId;
    }

    @Override
    protected Void doInBackground(SensorEvent... sensorEvents) {
        SensorEvent event = sensorEvents[0];

        int sensorType = event.sensor.getType();

        // log the value
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                String valueAccel = event.values[0] + ", " + event.values[1] + ", " + event.values[2];
                Log.d(DEBUG_TAG, "ACCEL____ " + valueAccel);
                new SensorXYZ(event, userUniqueId, "ACCELEROMETER").pushToServer();
                break;
            case Sensor.TYPE_GYROSCOPE:
                new SensorXYZ(event, userUniqueId, "GYROSCOPE").pushToServer();
                break;
            case Sensor.TYPE_GRAVITY:
                new SensorXYZ(event, userUniqueId, "GRAVITY").pushToServer();
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                new SensorXYZ(event, userUniqueId, "LINEAR_ACCEL").pushToServer();
                break;
            case Sensor.TYPE_LIGHT:
                new SensorOneValue(event, userUniqueId, "LIGHT").pushToServer();
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                new SensorXYZ(event, userUniqueId, "ROTATION_VECTOR").pushToServer();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                new SensorXYZ(event, userUniqueId, "MAGNETIC_FIELD").pushToServer();
                break;
            case Sensor.TYPE_PROXIMITY:
                new SensorOneValue(event, userUniqueId, "PROXIMITY").pushToServer();
                break;

        }
        return null;
    }


}

class SensorXYZ {
    private double x;
    private double y;
    private double z;
    private long timeStamp;
    private String userId;
    private static long lastTimeStamp = 0;
    private String sensorChild = "";

    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = database.getReference();


    public SensorXYZ(SensorEvent event, String userId, String sensorChild){
        if (event.values.length > 0) {
            this.timeStamp = MyUtil.getCurrentTime();
//            if (timeStamp > lastTimeStamp) {
                this.x = event.values[0];
                this.y = event.values[1];
                this.z = event.values[2];

                this.userId = userId;
//                lastTimeStamp = timeStamp;
                this.sensorChild = sensorChild;
//            }
        }

    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("x", x);
        result.put("y", y);
        result.put("z", z);
        result.put("time_stamp", timeStamp);
        result.put("user_id", userId);

        return result;
    }
    public void pushToServer() {
        if (!sensorChild.equals("")) {
            mDatabase.child("sensors").child(sensorChild).push().setValue(this.toMap());
        }
    }
}
class SensorOneValue {
    private double value;
    private long timeStamp;
    private String userId;
    private static long lastTimeStamp = 0;
    private String sensorChild = "";

    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = database.getReference();

    public SensorOneValue(SensorEvent event, String userId, String sensorChild){
        if (event.values.length > 0) {
            this.timeStamp = MyUtil.getCurrentTime();
//            if (timeStamp > lastTimeStamp) {
                this.value = event.values[0];
                this.timeStamp = MyUtil.getCurrentTime();
                this.userId = userId;
//                lastTimeStamp = timeStamp;
                this.sensorChild = sensorChild;
//            }
        }

    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("value", value);
        result.put("time_stamp", timeStamp);
        result.put("user_id", userId);

        return result;
    }
    public void pushToServer() {
        if (!sensorChild.equals("")) {
            mDatabase.child("sensors").child(sensorChild).push().setValue(this.toMap());
        }
    }
}

class SensorACCELEROMETER extends SensorXYZ {

    public SensorACCELEROMETER(SensorEvent event, String userId, String sensorChild) {
        super(event, userId, sensorChild);
    }
}

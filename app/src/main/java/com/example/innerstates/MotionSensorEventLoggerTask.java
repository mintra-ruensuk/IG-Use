package com.example.innerstates;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MotionSensorEventLoggerTask extends AsyncTask<SensorEvent, Void, Void> {

    private static final String DEBUG_TAG = "MotionLoggerService";
    private String userInviteId;


    public MotionSensorEventLoggerTask() {

    }

    public MotionSensorEventLoggerTask(String userId) {
        userInviteId = userId;
    }

    @Override
    protected Void doInBackground(SensorEvent... sensorEvents) {
        SensorEvent event = sensorEvents[0];

        int sensorType = event.sensor.getType();

        // log the value
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                new SensorACCELEROMETER(event, userInviteId, "ACCELEROMETER");

                break;
            case Sensor.TYPE_GYROSCOPE:
                new SensorGYROSCOPE(event, userInviteId, "GYROSCOPE");
                break;
            case Sensor.TYPE_GRAVITY:
                new SensorGRAVITY(event, userInviteId, "GRAVITY");
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                new SensorLINEAR_ACCELERATION(event, userInviteId, "LINEAR_ACCELERATION");
                break;
//            case Sensor.TYPE_LIGHT:
//                new SensorOneValue(event, userInviteId, "LIGHT").pushToServer();
//                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                new SensorROTATION_VECTOR(event, userInviteId, "ROTATION_VECTOR");
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                new SensorMAGNETIC_FIELD(event, userInviteId, "MAGNETIC_FIELD");
                break;
//            case Sensor.TYPE_PROXIMITY:
//                new SensorOneValue(event, userInviteId, "PROXIMITY").pushToServer();
//                break;

        }
        return null;
    }


}

class SensorXYZ {
    public double x;
    public double y;
    public double z;
    public long timeStamp;
    public String userId;
    public String childName;

    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = database.getReference();


    public SensorXYZ(SensorEvent event, String userId, String sensorChild){
        if (event.values.length > 0) {
            this.timeStamp = MyUtil.getCurrentTime();
                this.x = event.values[0];
                this.y = event.values[1];
                this.z = event.values[2];
                this.userId = userId;
                this.childName = sensorChild;
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
    public void pushToServer(ArrayList<SensorXYZ> arrayList) {
        Log.d("pushToServer -- ", childName);
        if (!childName.equals("")) {
            Map<String, Object> childUpdates = new HashMap<>();
            for( SensorXYZ obj : arrayList) {
//                mDatabase.child("sensors").child(childName).push().setValue(obj.toMap());
                String key = mDatabase.child("sensors").child(childName).push().getKey();
                childUpdates.put(key, obj.toMap());
            }
            mDatabase.child("sensors").child(childName).updateChildren(childUpdates);

        }
    }
    public void printLog() {
        Date myDate = new java.util.Date((long)timeStamp);
        String string = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(myDate);
        Log.d("SensorXYZ", "x = " + x + " time " + string);
    }
}

class SensorACCELEROMETER extends SensorXYZ {
    private static ArrayList<SensorXYZ> oneSecList = new ArrayList<>();
    private static long currentSec = 0;
    public SensorACCELEROMETER(SensorEvent event, String userId, String sensorChild) {
        super(event, userId, sensorChild);
        long dataSec = super.timeStamp / 1000L;
        if (dataSec == currentSec) {
            oneSecList.add(this);
        }else if (dataSec != currentSec) {
            if (oneSecList.size() > 60) {
                int removeItem = oneSecList.size() - 60;
                for (int i = 1 ; i <= removeItem ; i++) {
                    oneSecList.remove(new Random().nextInt(oneSecList.size()));
                }
            }
            Log.d("SensorAccel", " --------" + oneSecList.size());
            super.pushToServer(oneSecList);
            currentSec = dataSec;
            oneSecList.clear();
        }
    }

}

class SensorGYROSCOPE extends SensorXYZ {
    private static ArrayList<SensorXYZ> oneSecList = new ArrayList<>();
    private static long currentSec = 0;
    public SensorGYROSCOPE(SensorEvent event, String userId, String sensorChild) {
        super(event, userId, sensorChild);
        long dataSec = super.timeStamp / 1000L;
        if (dataSec == currentSec) {
            oneSecList.add(this);
        }else if (dataSec != currentSec) {
            if (oneSecList.size() > 60) {
                int removeItem = oneSecList.size() - 60;
                for (int i = 1 ; i <= removeItem ; i++) {
                    oneSecList.remove(new Random().nextInt(oneSecList.size()));
                }
            }
            Log.d("SensorGYROSCOPE", " --------" + oneSecList.size());
            super.pushToServer(oneSecList);
            currentSec = dataSec;
            oneSecList.clear();
        }
    }

}

class SensorGRAVITY extends SensorXYZ {
    private static ArrayList<SensorXYZ> oneSecList = new ArrayList<>();
    private static long currentSec = 0;
    public SensorGRAVITY(SensorEvent event, String userId, String sensorChild) {
        super(event, userId, sensorChild);
        long dataSec = super.timeStamp / 1000L;
        if (dataSec == currentSec) {
            oneSecList.add(this);
        }else if (dataSec != currentSec) {
            if (oneSecList.size() > 60) {
                int removeItem = oneSecList.size() - 60;
                for (int i = 1 ; i <= removeItem ; i++) {
                    oneSecList.remove(new Random().nextInt(oneSecList.size()));
                }
            }
            Log.d("GRAVITY", " --------" + oneSecList.size());
            super.pushToServer(oneSecList);
            currentSec = dataSec;
            oneSecList.clear();
        }
    }

}

class SensorLINEAR_ACCELERATION extends SensorXYZ {
    private static ArrayList<SensorXYZ> oneSecList = new ArrayList<>();
    private static long currentSec = 0;
    public SensorLINEAR_ACCELERATION(SensorEvent event, String userId, String sensorChild) {
        super(event, userId, sensorChild);
        long dataSec = super.timeStamp / 1000L;
        if (dataSec == currentSec) {
            oneSecList.add(this);
        }else if (dataSec != currentSec) {
            if (oneSecList.size() > 60) {
                int removeItem = oneSecList.size() - 60;
                for (int i = 1 ; i <= removeItem ; i++) {
                    oneSecList.remove(new Random().nextInt(oneSecList.size()));
                }
            }
            Log.d("SensorLINEAR_ACCEL", " --------" + oneSecList.size());
            super.pushToServer(oneSecList);
            currentSec = dataSec;
            oneSecList.clear();
        }
    }

}

class SensorROTATION_VECTOR extends SensorXYZ {
    private static ArrayList<SensorXYZ> oneSecList = new ArrayList<>();
    private static long currentSec = 0;
    public SensorROTATION_VECTOR(SensorEvent event, String userId, String sensorChild) {
        super(event, userId, sensorChild);
        long dataSec = super.timeStamp / 1000L;
        if (dataSec == currentSec) {
            oneSecList.add(this);
        }else if (dataSec != currentSec) {
            if (oneSecList.size() > 60) {
                int removeItem = oneSecList.size() - 60;
                for (int i = 1 ; i <= removeItem ; i++) {
                    oneSecList.remove(new Random().nextInt(oneSecList.size()));
                }
            }
            Log.d("SensorROTATION", " --------" + oneSecList.size());
            super.pushToServer(oneSecList);
            currentSec = dataSec;
            oneSecList.clear();
        }
    }

}

class SensorMAGNETIC_FIELD extends SensorXYZ {
    private static ArrayList<SensorXYZ> oneSecList = new ArrayList<>();
    private static long currentSec = 0;
    public SensorMAGNETIC_FIELD(SensorEvent event, String userId, String sensorChild) {
        super(event, userId, sensorChild);
        long dataSec = super.timeStamp / 1000L;
        if (dataSec == currentSec) {
            oneSecList.add(this);
        }else if (dataSec != currentSec) {
            if (oneSecList.size() > 60) {
                int removeItem = oneSecList.size() - 60;
                for (int i = 1 ; i <= removeItem ; i++) {
                    oneSecList.remove(new Random().nextInt(oneSecList.size()));
                }
            }
            Log.d("SensorMAGNETIC_FIELD", " --------" + oneSecList.size());
            super.pushToServer(oneSecList);
            currentSec = dataSec;
            oneSecList.clear();
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


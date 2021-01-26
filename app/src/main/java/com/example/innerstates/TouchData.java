package com.example.innerstates;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class TouchData {
    private String sizeMin;
    private String sizeMax;
    private String sizeMean;
    private String sizeMedian;
    private String sizeStd;
    private String sizeVar;
    private String pressureMin;
    private String pressureMax;
    private String pressureMean;
    private String pressureMedian;
    private String pressureStd;
    private String pressureVar;
    private double durationSinceLastPressed;
    private double holdTime;
    private double distance;
    private double speed;
    private String userId;
    private long timeStamp;

    public TouchData() {

    }


    public TouchData(double[] touchSize, double[] touchPressure, double durationSinceLastPressed, double holdTime, double distance, double speed, String userId) {
        this.sizeMin = String.format("%.13f", touchSize[0]);
        this.sizeMax = String.format("%.13f", touchSize[1]);
        this.sizeMean = String.format("%.13f", touchSize[2]);
        this.sizeMedian = String.format("%.13f", touchSize[3]);
        this.sizeStd = String.format("%.13f", touchSize[4]);
        this.sizeVar = String.format("%.13f", touchSize[5]);
        this.pressureMin = String.format("%.13f", touchPressure[0]);
        this.pressureMax = String.format("%.13f", touchPressure[1]);
        this.pressureMean = String.format("%.13f", touchPressure[2]);
        this.pressureMedian = String.format("%.13f", touchPressure[3]);
        this.pressureStd = String.format("%.13f", touchPressure[4]);
        this.pressureVar = String.format("%.13f", touchPressure[5]);
        this.durationSinceLastPressed = durationSinceLastPressed;
        this.holdTime = holdTime;
        this.distance = distance;
        this.speed = speed;
        this.userId = userId;
        this.timeStamp = MyUtil.getCurrentTime();
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("user_id", userId);
        result.put("time_stamp", timeStamp);
        result.put("size_min", sizeMin);
        result.put("size_max", sizeMax);
        result.put("size_mean", sizeMean);
        result.put("size_median", sizeMedian);
        result.put("size_std", sizeStd);
        result.put("size_var", sizeVar);
        result.put("pressure_min", pressureMin);
        result.put("pressure_max", pressureMax);
        result.put("pressure_mean", pressureMean);
        result.put("pressure_median", pressureMedian);
        result.put("pressure_std", pressureStd);
        result.put("pressure_var", pressureVar);
        result.put("duration", durationSinceLastPressed);
        result.put("hold_time", holdTime);
        result.put("distance", distance);
        result.put("speed", speed);

        return result;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}

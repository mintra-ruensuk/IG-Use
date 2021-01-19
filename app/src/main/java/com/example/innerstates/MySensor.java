package com.example.innerstates;

import android.hardware.Sensor;
import android.os.Build;
import android.widget.TextView;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.RequiresApi;

public class MySensor {
    private String name;
    private float resolution;
    private float maxRange;
    private int minDelay;
    private String typeString;
    private int type;
    private float power;
    private String vendor;
    private Sensor sensor;
    private TextView textView;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getResolution() {
        return resolution;
    }

    public void setResolution(float resolution) {
        this.resolution = resolution;
    }

    public float getMaxRange() {
        return maxRange;
    }

    public void setMaxRange(float maxRange) {
        this.maxRange = maxRange;
    }

    public int getMinDelay() {
        return minDelay;
    }

    public void setMinDelay(int minDelay) {
        this.minDelay = minDelay;
    }


    public float getPower() {
        return power;
    }

    public void setPower(float power) {
        this.power = power;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public Sensor getSensor() {
        return sensor;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
        if (sensor != null) {
            this.resolution = sensor.getResolution();
            this.maxRange = sensor.getMaximumRange();
            this.minDelay = sensor.getMinDelay();
            this.name = sensor.getName();
            this.power = sensor.getPower();
            this.type = sensor.getType();
            this.typeString = sensor.getStringType();
            this.vendor = sensor.getVendor();
        }

    }

    public TextView getTextView() {
        return textView;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    @Override
    public String toString() {
        return "MySensor{" +
                "name='" + name + '\'' +
                ", resolution=" + resolution +
                ", maxRange=" + maxRange +
                ", minDelay=" + minDelay +
                ", type='" + type + '\'' +
                ", power=" + power +
                ", vendor='" + vendor + '\'' +
                ", sensor=" + sensor +
                ", textView=" + textView +
                '}';
    }

    public String getTypeString() {
        return typeString;
    }

    public void setTypeString(String typeString) {
        this.typeString = typeString;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("resolution", resolution);
        result.put("maxRange", maxRange);
        result.put("minDelay", minDelay);
        result.put("type_string", typeString);
        result.put("type", type);
        result.put("power", power);
        result.put("vendor", vendor);

        return result;
    }
}

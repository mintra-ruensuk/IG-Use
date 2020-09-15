package com.example.innerstates;

import android.os.Build;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

@IgnoreExtraProperties
public class User {

    public String username;
    public String createdTime;
    public long createdUnixTime;
    public String osVersion;
    public int sdkInt;
    public String device;
    public String model;
    public String product;
    public String deviceName;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username) {
        this.username = username;
        this.createdTime = new Date().toString();
        this.osVersion = System.getProperty("os.version"); // OS version
        this.sdkInt = Build.VERSION.SDK_INT;
        this.device = android.os.Build.DEVICE;
        this.model = android.os.Build.MODEL;
        this.product = android.os.Build.PRODUCT;
        this.deviceName = getDeviceName();
        this.createdUnixTime = System.currentTimeMillis() / 1000L;

    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

}

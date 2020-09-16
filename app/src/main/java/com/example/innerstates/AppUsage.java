package com.example.innerstates;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class AppUsage {
    public String uid;
    public String appPackageName;
    public long actionTimestamp;
    public String status;
    public boolean isWiFiConnected;

    public AppUsage() {

    }
    public AppUsage(String uid, String appPackageName, String status, Context context) {
        this.uid = uid;
        this.appPackageName = appPackageName;
        this.actionTimestamp = System.currentTimeMillis() / 1000L;
        this.status = status;

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        this.isWiFiConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("package_name", appPackageName);
        result.put("time", actionTimestamp);
        result.put("status", status);
        result.put("is_wifi_connected", isWiFiConnected);

        return result;
    }


}

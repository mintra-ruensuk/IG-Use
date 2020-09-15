package com.example.innerstates;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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
        this.actionTimestamp = 0;
        this.actionTimestamp = System.currentTimeMillis() / 1000L;
        this.status = status;

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        this.isWiFiConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }


}

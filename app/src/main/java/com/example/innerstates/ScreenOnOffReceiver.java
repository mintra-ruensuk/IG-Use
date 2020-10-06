package com.example.innerstates;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;


public class ScreenOnOffReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Log.d("StackOverflow", "Screen Off");
            writeScreenOnOff(context, "off");

        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Log.d("StackOverflow", "Screen On");
            writeScreenOnOff(context, "on");
        }
    }
    private void writeScreenOnOff(Context context, String onOff) {
        SharedPreferences sharedPref = context.getSharedPreferences("com.example.innerstates.PREFERENCE_FILE_KEY",
                Context.MODE_PRIVATE);
        String userUniqueId = sharedPref.getString("user_unique_id", "nodata");
        String inviteUserId = sharedPref.getString("invitation_user_id", "nodata");


        String childName = "/users/" + userUniqueId + "/screen-onoff/";
        String key = mDatabase.child(childName).push().getKey();
        AppUsage appUsage = new AppUsage(userUniqueId, appPackageName, status, getInviteUserId(), mContext);
        Map<String, Object> postValues = appUsage.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(childName + key, postValues);
        childUpdates.put("/inner_usage/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }

}

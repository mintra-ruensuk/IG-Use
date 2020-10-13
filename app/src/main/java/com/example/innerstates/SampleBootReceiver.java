package com.example.innerstates;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SampleBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // Set the alarm here.
            Log.d("cccccc--->>", "BOOT COMPLETED");
            Intent pushIntent = new Intent(context, MainActivity.class);
            context.startService(pushIntent);
        }
    }
}

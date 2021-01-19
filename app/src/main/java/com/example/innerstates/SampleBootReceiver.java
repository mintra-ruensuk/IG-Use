package com.example.innerstates;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class SampleBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // Set the alarm here.
            Log.d("cccccc--->>", "BOOT COMPLETED");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, MainService.class));
//                context.startForegroundService(new Intent(context, MotionLoggerService.class));

            } else {
                context.startService(new Intent(context, MainService.class));
//                context.startService(new Intent(ontext, MotionLoggerService.class));

            }
        }else {
            Log.d("cccccc--->>", "BOOT COMPLETED BUTTTTTTTTTT");
        }


    }
}

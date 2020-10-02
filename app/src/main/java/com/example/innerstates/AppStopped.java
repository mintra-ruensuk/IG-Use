package com.example.innerstates;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class AppStopped extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Service.inner", "Service Started");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Service.inner", "Service Destroyed");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("Service.inner", "END");
        Toast toast = Toast.makeText(getApplicationContext(), "You have just closed XXXXXX app. Please open the app again.", Toast.LENGTH_LONG);
//        toast.getView().setBackgroundColor(Color.RED);
        toast.show();
        stopSelf();
    }
}

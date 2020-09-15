package com.example.innerstates;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rvalerio.fgchecker.AppChecker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    public final static int REQUEST_CODE = 5463;
    final static String CHANNEL_ID = "123456";
    private Context mContext;
    private String igPackageName = "com.instagram.android";
    final static int notificationId = 9876;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this.getBaseContext();


        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, SurveyActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);


        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.my_icon)
                .setContentTitle("How do you feel now?")
                .setContentText("Seem that you've just used Instagram. How do you feel?")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("You've just used Instagram. How do you feel now?"))
//                .setContentIntent(pendingIntent)
                .setFullScreenIntent(pendingIntent, true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);


        final AppChecker appChecker = new AppChecker();
        final Sample sample = new Sample();

        appChecker.other(new AppChecker.Listener() {

            @Override
            public void onForeground(String packageName) {
                String currentApp = appChecker.getForegroundApp(getApplicationContext());


//                Log.d("tagtagtag",packageName+"----=-=-==============");
                Log.d("tagtag", sample.getReadableStatus());
                if (packageName != null) {
                    if (sample.getStatus() == Sample.READY
                            && packageName.equals(igPackageName)) {
                        sample.setStatus(Sample.IG_OPENED);
                        Log.d("tagtag", "IG IS OPENing ===>>>>>>>>>>>>>>>>>>>>");
                    }
                    if (sample.getStatus() == Sample.IG_OPENED
                            && !packageName.equals(igPackageName)) {
                        Log.d("tagtag", "IG IS CLOSED ===>>>>>>>>>>>>>>>>>>>>");
                        sample.setStatus(Sample.POPUP);
//                        sleep(2000);

                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);

                        // notificationId is a unique int for each notification that you must define
                        notificationManager.notify(notificationId, builder.build());

                        sample.setStatus(Sample.WAIT_FOR_NEXT_POPUP);
                        sleep(2000);
//                        sample.setStatus(Sample.READY);
                    }


                }

            }
        }).timeout(500).start(this);

        createNotificationChannel();


    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshMessage();
    }

    public static void cancelNotification(Context ctx) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notificationId);
    }
    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void refreshMessage() {
        AppOpsManager appOps = (AppOpsManager) mContext.getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            mode = appOps.checkOpNoThrow("android:get_usage_stats",
                    android.os.Process.myUid(), mContext.getPackageName());
        }
        boolean granted = mode == AppOpsManager.MODE_ALLOWED;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkDrawOverlayPermission();
            requestUsageStatsPermission();
        }

//        TextView headMessage = (TextView) findViewById(R.id.textView4);
//        TextView subMessage = (TextView) findViewById(R.id.textView5);
        if (granted) {
//            headMessage.setText("You're in the study!");
//            subMessage.setText("This app will prompt you to record your emotions throughout the day. \n\nPlease keep this app running. If it is running, you will see a notice in your notification drawer.");

//            mDetectAppsService = new DetectAppsService();
//            mServiceIntent = new Intent(this, DetectAppsService.class);
//            if (!isMyServiceRunning(mDetectAppsService.getClass(), this)) {
//                startService(mServiceIntent);
//            }
        }
        else {
//            headMessage.setText("Your permission is required.");
//            subMessage.setText("To participate in this study, you must turn on usage data access in your settings. \n\nOn most devices, this is found under: Settings > Security > Usage data access ");
            showDialog(this);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkDrawOverlayPermission() {
        /** check if we already  have permission to draw over other apps */
        if (!Settings.canDrawOverlays(this)) {
            /** if not construct intent to request permission */
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            /** request permission via start activity for result */
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    private void showDialog(Context context) {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setPositiveButton("Turn On", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage("To get started in this study, you must turn on usage data access in your settings.")
                .setTitle("");

        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    void requestUsageStatsPermission() {
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && !hasUsageStatsPermission(this)) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), context.getPackageName());
        boolean granted = mode == AppOpsManager.MODE_ALLOWED;
        return granted;
    }

    private void sleep(int length) {
        try {
            Thread.sleep(length);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
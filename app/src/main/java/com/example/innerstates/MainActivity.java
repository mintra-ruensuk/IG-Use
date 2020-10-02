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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rvalerio.fgchecker.AppChecker;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
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

    private String userUniqueId;
    private String lastForegroundApp = "";
    public static Sample sample;
    private Sample igUsage;
    final AppChecker appChecker = new AppChecker();
    private long igOpenTime = 0;
    private long notifyTime = 0;
    private int notificationId;
    public static long startWaitNextNotificationTime = 0;

    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sample = new Sample();
        igUsage = new Sample();

        mContext = this.getBaseContext();

        startService(new Intent(getBaseContext(), AppStopped.class));


        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        // Set up user's unique ID (device id)
//        userUniqueId = Settings.Secure.getString(mContext.getContentResolver(),
//                Settings.Secure.ANDROID_ID);
        userUniqueId = MyUtil.getDeviceUniqueID(this);





        appChecker.other(new AppChecker.Listener() {
            @Override
            public void onForeground(String packageName) {
//                String currentApp = appChecker.getForegroundApp(getApplicationContext());


//                Log.d("tagtagtag",packageName+"----=-=-==============");

                if (packageName != null) {
                    Log.d("tagtag-------------->", sample.getReadableStatus());
                    recordIgUsage(packageName);

                    // IF 1.5 hours has passed... then do this:
                    if (sample.getStatus() == Sample.READY
                            && packageName.equals(igPackageName)) {
                        sample.setStatus(Sample.IG_OPENED);
                        igOpenTime = MyUtil.getCurrentTime();
                    }
                    if (sample.getStatus() == Sample.IG_OPENED
                            && !packageName.equals(igPackageName)) {

                        long test = MyUtil.getCurrentTime();
                        // Use IG at least 15 seconds
                        if(test >= (igOpenTime + 1)) {
                            sample.setStatus(Sample.POPUP);


                            notifyHowYouFeel();
                            notifyTime = MyUtil.getCurrentTime();
//                            sample.setStatus(Sample.WAIT_FOR_NEXT_POPUP);
//                            sleep(2000);

                        }


                    }
                    if (sample.getStatus() == Sample.POPUP) {
                        // 5 minutes = 300seconds
                        if(MyUtil.getCurrentTime() >= (notifyTime + 60)) {
                            cancelNotification(mContext, notificationId);
                            recordCancelNotification(notificationId);

                            sample.setStatus(Sample.READY);
                        }
                    }
                    if (sample.getStatus() == Sample.WAIT_FOR_NEXT_POPUP) {
                        //wait for 1.5 hours and then set ready state
                        if(MyUtil.getCurrentTime() >= (startWaitNextNotificationTime + 20)) {
                            sample.setStatus(Sample.READY);
                        }
                    }

                    lastForegroundApp = packageName;



                }

            }
        }).timeout(1000).start(this);

        createNotificationChannel();

        writeNewUser();


    }


    @Override
    protected void onResume() {
        super.onResume();
        refreshMessage();
    }

    private void recordIgUsage(String packageName) {
        if (igUsage.getStatus() == Sample.READY
                && packageName.equals(igPackageName)) {
            writeNewAppUsage(packageName, "IG_OPENED",mContext);
            igUsage.setStatus(Sample.IG_OPENED);
        }
        if (igUsage.getStatus() == Sample.IG_OPENED
                && !packageName.equals(igPackageName)) {
            writeNewAppUsage(packageName, "IG_CLOSED",mContext);
            igUsage.setStatus(Sample.READY);
        }
    }


    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);

        NotificationManagerCompat.from(ctx).cancelAll();
    }
    public static void cancelAllNotification(Context ctx) {

        NotificationManagerCompat.from(ctx).cancelAll();
    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
//            channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
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



    private void notifyHowYouFeel() {
        notificationId = MyUtil.generateFiveDigit();

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, SurveyActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("notificationId", notificationId);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.my_icon)
                .setContentTitle("How do you feel now?")
                .setContentText("Seem that you've just used Instagram. How do you feel?")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("You've just used Instagram. How do you feel now?"))
                .setFullScreenIntent(pendingIntent, true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());
        writeNewNotification(notificationId);

    }

    private void writeNewUser() {
        final User user = new User(userUniqueId, this);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.child("users").hasChild(userUniqueId)) {

                    mDatabase.child("users").child(userUniqueId).setValue(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void writeNewAppUsage(String appPackageName, String status, Context context) {

        String childName = "/users/" + userUniqueId + "/ig_usage/";
        String key = mDatabase.child(childName).push().getKey();
        AppUsage appUsage = new AppUsage(userUniqueId, appPackageName, status, mContext);
        Map<String, Object> postValues = appUsage.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(childName + key, postValues);
        childUpdates.put("/ig_usage/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }

    private void writeNewNotification(int notificationId) {

        String childName = "/users/" + userUniqueId + "/notification/";
        String key = mDatabase.child(childName).child(notificationId + "").getKey();
        Notification appUsage = new Notification(userUniqueId, notificationId);
        Map<String, Object> postValues = appUsage.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(childName + key, postValues);
        childUpdates.put("/notification/" + key, postValues);

        mDatabase.updateChildren(childUpdates);


    }

    public void recordCancelNotification(int notificationId) {

        String childName = "/users/" + userUniqueId + "/notification/";
        String childName2 = "/notification/";

        mDatabase.child(childName).child(notificationId+"").child("status").setValue(Notification.MISSED);

        mDatabase.child(childName2).child(notificationId+"").child("status").setValue(Notification.MISSED);

    }


}
package com.example.innerstates;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public final static int REQUEST_CODE = 5463;
//    final static String CHANNEL_ID = "123456";
    private Context mContext;
//    private String igPackageName = "com.instagram.android";
//    private String appPackageName = "com.example.innerstates";
//
//    private String userUniqueId;
//    private String lastForegroundApp = "";
//    public static Sample sample;
//    private Sample igUsage;
//    private Sample ourAppUsage;
//    final AppChecker appChecker = new AppChecker();
//    private static long igOpenTime = 0;
//    private static long notifyTime = 0;
//    public static int notificationId;
//    public static long startWaitNextNotificationTime = 0;
    private SharedPreferences sharedPref;
//    private String inviteUserId;
////    public static User user;
//
//    private AlarmManager alarmMgr;
//    private PendingIntent alarmIntent;

    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = database.getReference();

    Intent mServiceIntent;
    private MainService mMainService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_main2);
        getSupportActionBar().setTitle("Instagram Use");


//        sample = new Sample();
//        igUsage = new Sample();
//        ourAppUsage = new Sample();
//
        mContext = this.getBaseContext();
        sharedPref = mContext.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);


        checkInviteCode();


//        userUniqueId = MyUtil.getDeviceUniqueID(this);
//
//
//        alarmMgr = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(mContext, MainActivity.class);
//        alarmIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
//
//        // Set the alarm to start at 8:30 a.m.
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.set(Calendar.HOUR_OF_DAY, 14);
//        calendar.set(Calendar.MINUTE, 28);
//
//        // setRepeating() lets you specify a precise custom interval--in this case,
//        // 20 minutes.
//        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                1000 * 60 * 2, alarmIntent);
//
//        appChecker.other(new AppChecker.Listener() {
//            @Override
//            public void onForeground(String packageName) {
////                String currentApp = appChecker.getForegroundApp(getApplicationContext());
//
//
////                Log.d("tagtagtag",packageName+"----=-=-==============");
//
//                if (packageName != null) {
//                    Log.d("tagtag-------------->", sample.getReadableStatus());
//                    recordIgUsage(packageName);
//                    recordOurAppUsage(packageName);
//
//                    // IF 1.5 hours has passed... then do this:
//                    if (sample.getStatus() == Sample.READY
//                            && packageName.equals(igPackageName)) {
//                        sample.setStatus(Sample.IG_OPENED);
//                        igOpenTime = MyUtil.getCurrentTime();
//                    }
//                    if (sample.getStatus() == Sample.IG_OPENED
//                            && !packageName.equals(igPackageName)) {
//
//                        // Use IG at least 15 seconds
//                        if(MyUtil.getCurrentTime() >= (igOpenTime + 5)) {
//                            sample.setStatus(Sample.POPUP);
//
//
//                            notifyHowYouFeel();
//                            notifyTime = MyUtil.getCurrentTime();
//
//                        }else {
//                            sample.setStatus(Sample.READY);
//                        }
//
//
//                    }
//                    if (sample.getStatus() == Sample.POPUP) {
//                        // 5 minutes = 300seconds
//                        if(MyUtil.getCurrentTime() >= (notifyTime + 60)) {
//                            cancelNotification(mContext, notificationId);
//                            recordCancelNotification(notificationId);
//
//                            sample.setStatus(Sample.READY);
//                        }
//                    }
//                    if (sample.getStatus() == Sample.WAIT_FOR_NEXT_POPUP) {
//                        //wait for 1.5 hours and then set ready state
//                        if(MyUtil.getCurrentTime() >= (startWaitNextNotificationTime + 20)) {
//                            sample.setStatus(Sample.READY);
//                        }
//                    }
//
//                    lastForegroundApp = packageName;
//
//
//
//                }
//
//            }
//        }).timeout(2000).start(this);
//
//        createNotificationChannel();
//
//        writeNewUser();
//
//        Log.d("oncreate---------->", "onCreate");
    }


    @Override
    protected void onResume() {
        super.onResume();
        refreshMessage();
        Log.d("onResume---------->", "onResume");
    }

//    private void recordIgUsage(String packageName) {
//        if (igUsage.getStatus() == Sample.READY
//                && packageName.equals(igPackageName)) {
//            writeNewIgUsage(packageName, "IG_OPENED",mContext);
//            igUsage.setStatus(Sample.IG_OPENED);
//        }
//        if (igUsage.getStatus() == Sample.IG_OPENED
//                && !packageName.equals(igPackageName)) {
//            writeNewIgUsage(packageName, "IG_CLOSED",mContext);
//            igUsage.setStatus(Sample.READY);
//        }
//    }
//    private void recordOurAppUsage(String packageName) {
//        if (ourAppUsage.getStatus() == Sample.READY
//                && packageName.equals(appPackageName)) {
//            writeOurAppUsage(packageName, "INNER_OPENED",mContext);
//            ourAppUsage.setStatus(Sample.IG_OPENED);
//        }
//        if (ourAppUsage.getStatus() == Sample.IG_OPENED
//                && !packageName.equals(appPackageName)) {
//            writeOurAppUsage(packageName, "INNER_CLOSED",mContext);
//            ourAppUsage.setStatus(Sample.READY);
//        }
//    }
//
//
//    public static void cancelNotification(Context ctx, int notifyId) {
//        String ns = Context.NOTIFICATION_SERVICE;
//        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
//        nMgr.cancel(notifyId);
//
//        NotificationManagerCompat.from(ctx).cancelAll();
//    }
//    public static void cancelAllNotification(Context ctx) {
//
//
//        NotificationManagerCompat.from(ctx).cancelAll();
//    }
//
//
//    private void createNotificationChannel() {
//        // Create the NotificationChannel, but only on API 26+ because
//        // the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = getString(R.string.channel_name);
//            String description = getString(R.string.channel_description);
//            int importance = NotificationManager.IMPORTANCE_HIGH;
////            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
//            channel.setDescription(description);
////            channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
//            // Register the channel with the system; you can't change the importance
//            // or other notification behaviors after this
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//    }

    private void refreshMessage() {
        AppOpsManager appOps = (AppOpsManager) mContext.getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            mode = appOps.checkOpNoThrow("android:get_usage_stats",
                    android.os.Process.myUid(), mContext.getPackageName());
        }
        boolean granted = mode == AppOpsManager.MODE_ALLOWED;
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            //checkDrawOverlayPermission();
//            requestUsageStatsPermission();
//        }

        if (granted) {
//            headMessage.setText("You're in the study!");
//            subMessage.setText("This app will prompt you to record your emotions throughout the day. \n\nPlease keep this app running. If it is running, you will see a notice in your notification drawer.");

//            mDetectAppsService = new DetectAppsService();
//            mServiceIntent = new Intent(this, DetectAppsService.class);
            final EditText editText = findViewById(R.id.editTextInviteCode);
            final Button button = findViewById(R.id.submitInviteCode);
            editText.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);

            checkInviteCode();

            if (!isMyServiceRunning(AppStopped.class, this)) {
                Log.d("serviceeeeee------>", "AppStopped is starting...");
                startService(new Intent(getBaseContext(), AppStopped.class));
            }
            if (!isMyServiceRunning(ScreenOnOffService.class, this)) {
                Log.d("serviceeeeee------>", "ScreenOnOffService is starting...");
                startService(new Intent(getBaseContext(), ScreenOnOffService.class));
            }

//            mMainService = new MainService();
//            mServiceIntent = new Intent(this, MainService.class);
            if (!isMyServiceRunning(MainService.class, this)) {
                Log.d("serviceeeeee------>", "MainService is starting...");
                startService(new Intent(getBaseContext(), MainService.class));
            }else {
                Log.d("serviceeeeee------>", "MainService is running!");
            }

        }
        else {
//            headMessage.setText("Your permission is required.");
//            subMessage.setText("To participate in this study, you must turn on usage data access in your settings. \n\nOn most devices, this is found under: Settings > Security > Usage data access ");
            showDialog(this);
        }
    }
//    private boolean isMyServiceRunning(Class<?> serviceClass) {
//        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (serviceClass.getName().equals(service.service.getClassName())) {
//                return true;
//            }
//        }
//        return false;
//    }
    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
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



//    private void notifyHowYouFeel() {
//        notificationId = MyUtil.generateFiveDigit();
//
//        // Create an explicit intent for an Activity in your app
//        Intent intent = new Intent(this, SurveyActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        intent.putExtra("notificationId", notificationId);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        writeNewNotification(notificationId);
//
//        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setSmallIcon(R.drawable.my_icon)
//                .setContentTitle("How do you feel now?")
//                .setContentText("Seem that you've just used Instagram. How do you feel?")
//                .setStyle(new NotificationCompat.BigTextStyle()
//                        .bigText("You've just used Instagram. How do you feel now?"))
////                .setFullScreenIntent(pendingIntent, true)
//                .setContentIntent(pendingIntent)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
////                .setPriority(NotificationCompat.PRIORITY_HIGH);
//
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
//
//        // notificationId is a unique int for each notification that you must define
//        notificationManager.notify(notificationId, builder.build());
//
//
//    }
//
//    private void writeNewUser() {
//        final User user = new User(userUniqueId, inviteUserId, this);
//        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                if (!snapshot.child("users").hasChild(userUniqueId)) {
//
//                    mDatabase.child("users").child(userUniqueId).setValue(user);
//
//                    SharedPreferences.Editor editor = sharedPref.edit();
//                    editor.putString(getString(R.string.user_unique_id), userUniqueId);
//                    editor.apply();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }
//    private void writeNewIgUsage(String appPackageName, String status, Context context) {
//
//        String childName = "/users/" + userUniqueId + "/ig_usage/";
//        String key = mDatabase.child(childName).push().getKey();
//        AppUsage appUsage = new AppUsage(userUniqueId, appPackageName, status, getInviteUserId(), mContext);
//        Map<String, Object> postValues = appUsage.toMap();
//
//        Map<String, Object> childUpdates = new HashMap<>();
//        childUpdates.put(childName + key, postValues);
//        childUpdates.put("/ig_usage/" + key, postValues);
//
//        mDatabase.updateChildren(childUpdates);
//    }
//    private void writeOurAppUsage(String appPackageName, String status, Context context) {
//
//        String childName = "/users/" + userUniqueId + "/inner_usage/";
//        String key = mDatabase.child(childName).push().getKey();
//        AppUsage appUsage = new AppUsage(userUniqueId, appPackageName, status, getInviteUserId(), mContext);
//        Map<String, Object> postValues = appUsage.toMap();
//
//        Map<String, Object> childUpdates = new HashMap<>();
//        childUpdates.put(childName + key, postValues);
//        childUpdates.put("/inner_usage/" + key, postValues);
//
//        mDatabase.updateChildren(childUpdates);
//    }

//    private void writeNewNotification(int notificationId) {
//
//        String childName = "/users/" + userUniqueId + "/notification/";
//        String key = mDatabase.child(childName).child(notificationId + "").getKey();
//        Notification appUsage = new Notification(userUniqueId, notificationId, getInviteUserId());
//        Map<String, Object> postValues = appUsage.toMap();
//
//        Map<String, Object> childUpdates = new HashMap<>();
//        childUpdates.put(childName + key, postValues);
//        childUpdates.put("/notification/" + key, postValues);
//
//        mDatabase.updateChildren(childUpdates);
//
//
//    }
//
//    public void recordCancelNotification(int notificationId) {
//
//        String childName = "/users/" + userUniqueId + "/notification/";
//        String childName2 = "/notification/";
//
//        mDatabase.child(childName).child(notificationId+"").child("status").setValue(Notification.MISSED);
//
//        mDatabase.child(childName2).child(notificationId+"").child("status").setValue(Notification.MISSED);
//
//    }
    private void checkInviteCode() {
        final EditText editText = findViewById(R.id.editTextInviteCode);
        final Button button = findViewById(R.id.submitInviteCode);


        String inviteUserId = getInviteUserId();
        final String userUniqueId = getUserUniqueId();
//        Log.e("idididididi--->", inviteUserId);

        if (!inviteUserId.equals("nodata")) {
            editText.setVisibility(View.GONE);
            button.setVisibility(View.GONE);
        }

        if(button.getVisibility() == View.VISIBLE) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (editText.getText().length() == 6) {
                        String code = editText.getText().toString();
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(getString(R.string.invitation_user_id), code);
                        editor.apply();

                        editText.setVisibility(View.GONE);
                        button.setVisibility(View.GONE);



                        String childName = "/users/" + userUniqueId;
                        mDatabase.child(childName).child("inviteUserId").setValue(code);


                        Toast.makeText(getApplicationContext(),"Thank you for registration!",Toast.LENGTH_LONG).show();


                    }else {
                        editText.setError("Code must be 6 digits.");
                    }
                }
            });

        }
    }

    public String getInviteUserId() {
        return sharedPref.getString(getString(R.string.invitation_user_id), "nodata");
    }
    public String getUserUniqueId() {
        return sharedPref.getString(getString(R.string.user_unique_id), "nodata");
    }


}
package com.example.innerstates;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings.Secure;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rvalerio.fgchecker.AppChecker;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MainService extends Service {

    private static MainService instance;

    public final static int REQUEST_CODE = 5463;
    final static String CHANNEL_ID = "123456";
    private String igPackageName = "com.instagram.android";
    private String appPackageName = "com.example.innerstates";

    private String userUniqueId;
    private String lastForegroundApp = "";
    public static Sample sample;
    private Sample igUsage;
    private Sample ourAppUsage;
    final AppChecker appChecker = new AppChecker();
    private static long igOpenTime = 0;
    private static long notifyTime = 0;
    public static int notificationId;
    public static long startWaitNextNotificationTime = 0;
    private SharedPreferences sharedPref;
    private String inviteUserId ="default";
    private static int FOREGROUND_ID=16;
//    public static User user;

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = database.getReference();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        Log.d("Mainservice", "00000....---->> onCreate");
        instance = this;


        sample = new Sample();
        igUsage = new Sample();
        ourAppUsage = new Sample();


        sharedPref = getSharedPreferences(getString(R.string.preference_file_key),
                MODE_PRIVATE);



        userUniqueId = Secure.getString(this.getContentResolver(),
                Secure.ANDROID_ID);

        setAlarmManager();



        appChecker.other(new AppChecker.Listener() {
            @Override
            public void onForeground(String packageName) {

                if (packageName != null) {
                    Log.d("tagtag-------------->", sample.getReadableStatus());
                    recordIgUsage(packageName);
                    recordOurAppUsage(packageName);

                    // IF 1.5 hours has passed... then do this:
                    if (sample.getStatus() == Sample.READY
                            && packageName.equals(igPackageName)) {
                        sample.setStatus(Sample.IG_OPENED);
                        igOpenTime = MyUtil.getCurrentTime();
                    }
                    if (sample.getStatus() == Sample.IG_OPENED
                            && !packageName.equals(igPackageName)) {

                        // Use IG at least 15 seconds
                        if(MyUtil.getCurrentTime() >= (igOpenTime + 13)) {
                            sample.setStatus(Sample.POPUP);


                            notifyHowYouFeel();
                            notifyTime = MyUtil.getCurrentTime();

                        }else {
                            sample.setStatus(Sample.READY);
                        }


                    }
                    if (sample.getStatus() == Sample.POPUP) {
                        // 5 minutes = 300seconds
                        if(MyUtil.getCurrentTime() >= (notifyTime + 300)) {
                            cancelNotification(instance, notificationId);
                            recordCancelNotification(notificationId);

                            sample.setStatus(Sample.READY);
                        }
                    }
                    if (sample.getStatus() == Sample.WAIT_FOR_NEXT_POPUP) {
                        //wait for 1.5 hours and then set ready state
                        if(MyUtil.getCurrentTime() >= (startWaitNextNotificationTime + (90 * 60))) {
                            sample.setStatus(Sample.READY);
                        }
                    }

                    lastForegroundApp = packageName;



                }

            }
        }).timeout(2000).start(this);

        createNotificationChannel();

        writeNewUser();

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "MainService";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Keep MainService running, please!",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            android.app.Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("").setContentText("").build();

            startForeground(FOREGROUND_ID, notification);
        }

        Log.d("oncreate---------->", "onCreate");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("c", "service destroyed");
        instance = null;
        stopForeground(true);

        Intent broadcastIntent = new Intent("RestartSensor");
        sendBroadcast(broadcastIntent);
    }

    private void notifyHowYouFeel() {
        notificationId = MyUtil.generateFiveDigit();

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, SurveyActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("notificationId", notificationId);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        writeNewNotification(notificationId);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.my_icon)
                .setContentTitle("How do you feel now?")
                .setContentText("Seem that you've just used Instagram. How do you feel?")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("You've just used Instagram. How do you feel now?"))
//                .setFullScreenIntent(pendingIntent, true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());


    }

    private void writeNewUser() {
        final User user = new User(userUniqueId, getInviteUserId());
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.child("users").hasChild(userUniqueId)) {

                    mDatabase.child("users").child(userUniqueId).setValue(user);

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(getString(R.string.user_unique_id), userUniqueId);
                    editor.apply();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void writeNewIgUsage(String appPackageName, String status, Context context) {

        String childName = "/users/" + userUniqueId + "/ig_usage/";
        String key = mDatabase.child(childName).push().getKey();
        AppUsage appUsage = new AppUsage(userUniqueId, appPackageName, status, getInviteUserId(), context);
        Map<String, Object> postValues = appUsage.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(childName + key, postValues);
        childUpdates.put("/ig_usage/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }
    private void writeOurAppUsage(String appPackageName, String status, Context context) {

        String childName = "/users/" + userUniqueId + "/inner_usage/";
        String key = mDatabase.child(childName).push().getKey();
        AppUsage appUsage = new AppUsage(userUniqueId, appPackageName, status, getInviteUserId(), context);
        Map<String, Object> postValues = appUsage.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(childName + key, postValues);
        childUpdates.put("/inner_usage/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }

    private void writeNewNotification(int notificationId) {

        String childName = "/users/" + userUniqueId + "/notification/";
        String key = mDatabase.child(childName).child(notificationId + "").getKey();
        Notification appUsage = new Notification(userUniqueId, notificationId, getInviteUserId());
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

    private void recordIgUsage(String packageName) {
        if (igUsage.getStatus() == Sample.READY
                && packageName.equals(igPackageName)) {
            writeNewIgUsage(packageName, "IG_OPENED",this);
            igUsage.setStatus(Sample.IG_OPENED);
        }
        if (igUsage.getStatus() == Sample.IG_OPENED
                && !packageName.equals(igPackageName)) {
            writeNewIgUsage(packageName, "IG_CLOSED",this);
            igUsage.setStatus(Sample.READY);
        }
    }
    private void recordOurAppUsage(String packageName) {
        if (ourAppUsage.getStatus() == Sample.READY
                && packageName.equals(appPackageName)) {
            writeOurAppUsage(packageName, "INNER_OPENED",this);
            ourAppUsage.setStatus(Sample.IG_OPENED);
        }
        if (ourAppUsage.getStatus() == Sample.IG_OPENED
                && !packageName.equals(appPackageName)) {
            writeOurAppUsage(packageName, "INNER_CLOSED",this);
            ourAppUsage.setStatus(Sample.READY);
        }
    }

    public void setAlarmManager() {
        alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, SensorRestarterBroadcastReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        // Set the alarm to start at 8:30 a.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 15);

        // setRepeating() lets you specify a precise custom interval--in this case,
        // 2 hours
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000 * 60 * 120, alarmIntent);
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
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
//            channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }




    public String getInviteUserId() {
        return sharedPref.getString(getString(R.string.invitation_user_id), "nodata");
    }
    public String getUserUniqueId() {
        return sharedPref.getString(getString(R.string.user_unique_id), "nodata");
    }
}

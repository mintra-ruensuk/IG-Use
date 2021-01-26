package com.example.innerstates;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    public final static int REQUEST_CODE = 5463;
    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;
//    final static String CHANNEL_ID = "123456";
    private static final String DEBUG_TAG = "MainActivity";
    private Context mContext;
    private SharedPreferences sharedPref;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = database.getReference();
    private String userUniqueId;

    Intent mServiceIntent;
    private MainService mMainService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_main2);
        getSupportActionBar().setTitle("Instagram Use");

        mContext = this.getBaseContext();
        sharedPref = mContext.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        userUniqueId = Secure.getString(this.getContentResolver(),
                Secure.ANDROID_ID);

        checkInviteCode();


    }


    @Override
    protected void onResume() {
        super.onResume();
        refreshMessage();
        Log.d("onResume---------->", "onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMotionLoggerService();
    }

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

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {

        } else {
            requestCameraPermission();
        }
        if (granted && rc == PackageManager.PERMISSION_GRANTED) {
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
//            if (!isMyServiceRunning(ScreenOnOffService.class, this)) {
//                Log.d("serviceeeeee------>", "ScreenOnOffService is starting...");
//                startService(new Intent(getBaseContext(), ScreenOnOffService.class));
//            }

            if (!isMyServiceRunning(MainService.class, this)) {
                Log.d("serviceeeeee------>", "MainService is starting...");
                startService(new Intent(getBaseContext(), MainService.class));
            }else {
                Log.d("serviceeeeee------>", "MainService is running!");
            }

            Log.d(DEBUG_TAG, " ...... !!!!! ....");
            startMotionLoggerService();

            Intent intent = new Intent(this, WebViewIGActivity.class);
            startActivity(intent);

        }
        else {
//            headMessage.setText("Your permission is required.");
//            subMessage.setText("To participate in this study, you must turn on usage data access in your settings. \n\nOn most devices, this is found under: Settings > Security > Usage data access ");
            showDialog(this);
        }
    }
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
//                ASK CAMERA PERMISSION
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


    private void checkInviteCode() {
        final EditText editText = findViewById(R.id.editTextInviteCode);
        final Button button = findViewById(R.id.submitInviteCode);


        String inviteUserId = getInviteUserId();
        final String userUniqueId = Secure.getString(this.getContentResolver(),
                Secure.ANDROID_ID);
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

                        writeNewUser();

                        editText.setVisibility(View.GONE);
                        button.setVisibility(View.GONE);






                        Toast.makeText(getApplicationContext(),"Thank you for registration!",Toast.LENGTH_LONG).show();


                    }else {
                        editText.setError("Code must be 6 digits.");
                    }
                }
            });

        }
    }

    private void writeNewUser() {
        final User user = new User(userUniqueId, getInviteUserId());
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.child("users").hasChild(userUniqueId)) {

                    mDatabase.child("users").child(userUniqueId).setValue(user);
                    mDatabase.child("users").child(userUniqueId).child("inviteUserId").setValue(getInviteUserId());

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

    public String getInviteUserId() {
        return sharedPref.getString(getString(R.string.invitation_user_id), "nodata");
    }
    public String getUserUniqueId() {
        return sharedPref.getString(getString(R.string.user_unique_id), "nodata");
    }

    public void startMotionLoggerService() {
//        if (!MainActivity.isMyServiceRunning(MotionLoggerService.class, this)) {
//            Log.d("serviceeeeee------>", "MotionLoggerService is starting...");
//            startService(new Intent(getBaseContext(), MotionLoggerService.class));
//        }else {
//            Log.d("serviceeeeee------>", "MotionLoggerService is running!");
//        }
    }

    public void  stopMotionLoggerService() {
//        if (MainActivity.isMyServiceRunning(MotionLoggerService.class, this)) {
//            Log.d("serviceeeeee------>", "MotionLoggerService is stopping...");
//            stopService(new Intent(getBaseContext(), MotionLoggerService.class));
//        }
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(DEBUG_TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };
    }

}
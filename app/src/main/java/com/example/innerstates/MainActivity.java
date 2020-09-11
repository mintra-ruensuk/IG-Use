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
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rvalerio.fgchecker.AppChecker;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    public final static int REQUEST_CODE = 5463;
    Context mContext;
    String igPackageName = "com.instagram.android";

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


                        sample.setStatus(Sample.WAIT_FOR_NEXT_POPUP);
                        sleep(2000);
//                        sample.setStatus(Sample.READY);
                    }


                }

            }
        }).timeout(500).start(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshMessage();
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
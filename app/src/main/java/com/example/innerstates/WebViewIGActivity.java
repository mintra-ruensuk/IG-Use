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
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;

public class WebViewIGActivity extends AppCompatActivity {

    private WebView webView ;
    private SharedPreferences sharedPref;
    public final static int REQUEST_CODE = 5463;
    private Context mContext;
    private static final String DEBUG_TAG = "WebViewIGActivity";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = database.getReference();
    private float oldX = 0;
    private float oldY = 0;
    private long timerTime = 0;

    private VelocityTracker mVelocityTracker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        mDatabase.child("ig_usage").removeValue();
//        mDatabase.child("inner_usage").removeValue();
//        mDatabase.child("message").removeValue();
//        mDatabase.child("notification").removeValue();
//        mDatabase.child("survey_data").removeValue();
//        mDatabase.child("users").removeValue();
//        mDatabase.child("sensors").removeValue();

        mContext = this.getBaseContext();
        webView  = new WebView(this);

        webView.getSettings().setJavaScriptEnabled(true); // enable javascript

//        final Activity activity = this;
//
        webView.setWebViewClient(new WebViewClient() {

            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                // Redirect to deprecated method, so you can use it in all SDK versions
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url){
                // do your handling codes here, which url is the requested url
                // probably you need to open that url rather than redirect:
                view.loadUrl(url);
                return false; // then it is not handled by default action
            }
        });


        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int index = event.getActionIndex();
                int action = event.getAction();
                int pointerId = event.getPointerId(index);

                switch(action) {
                    case MotionEvent.ACTION_DOWN:
                        oldX = event.getX();
                        oldY = event.getY();
                        timerTime = MyUtil.getCurrentTime();
                        break;
                    case MotionEvent.ACTION_MOVE:

                        break;
                    case MotionEvent.ACTION_UP:
                        float newX = event.getX();
                        float newY = event.getY();
                        long timeDiff = (MyUtil.getCurrentTime() - timerTime);

                        double distance = Math.sqrt((newX-oldX) * (newX-oldX) + (newY-oldY) * (newY-oldY));
                        double speed = distance / timeDiff;
                        Log.d(DEBUG_TAG, "distance = " + distance + " .... speed = " + speed);

                    case MotionEvent.ACTION_CANCEL:

                        break;
                }
                //Log.d(DEBUG_TAG, "x = " + event.getX() + ", y = " +event.getY() + ", size = " + event.getSize());
                return false;
            }
        });
        // URL laden:
        webView.loadUrl("https://instagram.com");
        setContentView(webView);
    }


    @Override
    protected void onResume() {
        super.onResume();
        refreshMessage();
        Log.d("onResume---------->", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopMotionLoggerService();
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
        if (granted) {

            Log.d(DEBUG_TAG, " ...... !!!!! ....");
            startMotionLoggerService();

        }
        else {
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


}
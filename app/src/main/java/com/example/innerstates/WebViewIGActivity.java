package com.example.innerstates;

import android.Manifest;
import android.annotation.TargetApi;
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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
    private ArrayList<Double> touchSize = new ArrayList<>();
    private ArrayList<Double> touchPressure = new ArrayList<>();
    private double speed;
    private double distance;
    private double durationSinceLastPressed;
    private double holdTime;
    private long lastPressed = 0;
    private String userInviteId;

    CameraSource cameraSource;


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

        sharedPref = getSharedPreferences(getString(R.string.preference_file_key),
                MODE_PRIVATE);


        mContext = this.getBaseContext();
        webView  = new WebView(this);



        userInviteId = getInviteUserId();


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

                switch(action) {
                    case MotionEvent.ACTION_DOWN:
                        oldX = event.getX();
                        oldY = event.getY();
                        timerTime = MyUtil.getCurrentTime();
                        touchSize.add(new Double(event.getSize()));
                        touchPressure.add(new Double(event.getPressure()));
                        if(lastPressed > 0) {
                            durationSinceLastPressed = timerTime - lastPressed;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        touchSize.add(new Double(event.getSize()));
                        touchPressure.add(new Double(event.getPressure()));
                        break;
                    case MotionEvent.ACTION_UP:
                        float newX = event.getX();
                        float newY = event.getY();
                        long timeNow = MyUtil.getCurrentTime();
                        long timeDiff = (timeNow - timerTime);

                        holdTime = timeDiff;

                        distance = Math.sqrt((newX-oldX) * (newX-oldX) + (newY-oldY) * (newY-oldY));
                        speed = distance / timeDiff;
                        touchSize.add(new Double(event.getSize()));
                        touchPressure.add(new Double(event.getPressure()));

                        calStat();
                        lastPressed = timeNow;

                    case MotionEvent.ACTION_CANCEL:

                        break;
                }
//                Log.d(DEBUG_TAG, "x = " + event.getX() + ", y = " +event.getY() + ", size = " + event.getSize());
                return false;
            }
        });
        // URL laden:
        webView.loadUrl("https://instagram.com");
        setContentView(webView);

        createCameraSource();
    }

    public void calStat() {
        // min, max, mean, sd, median var
        DecimalFormat df = new DecimalFormat("#.#########");
        df.setRoundingMode(RoundingMode.CEILING);

        double[] touchSizeResult = getMinMaxETC(touchSize);
        double[] touchPressureResult = getMinMaxETC(touchPressure);


//        Log.d(DEBUG_TAG, "SIZE min=" + df.format(touchSizeResult[0]) + " max=" +df.format(touchSizeResult[1]) + " mean="+df.format(touchSizeResult[2]) + " median=" + df.format(touchSizeResult[3]) + " std="+ df.format(touchSizeResult[4]) + " var="+ df.format(touchSizeResult[5]));
//
//        Log.d(DEBUG_TAG, "PRES min=" + df.format(touchPressureResult[0]) + " max=" +df.format(touchPressureResult[1]) + " mean="+df.format(touchPressureResult[2]) + " median=" + df.format(touchPressureResult[3]) + " std="+ df.format(touchPressureResult[4]) + " var="+ df.format(touchPressureResult[5]));

        TouchData touchData = new TouchData(touchSizeResult, touchPressureResult, durationSinceLastPressed, holdTime, distance, speed, userInviteId);

        mDatabase.child("sensors_extra").child("TOUCH").push().setValue(touchData.toMap());

        touchSize.clear();
        touchPressure.clear();

    }

    public double[] getMinMaxETC(ArrayList<Double> list) {
        double data[] = new double[touchSize.size()];
        int i = 0;
        for (Double d: touchSize) data[i++] = d;


        double min = Collections.min(touchSize);
        double max = Collections.max(touchSize);
        double mean = MyUtil.calculateAverage(touchSize);
        double median = MyUtil.getMedianFromArray(data);
        double std = MyUtil.calculateStandardDeviation(data);
        double var = MyUtil.getVariance(data);

        double[] result = {min, max, mean, median, std, var};
        return result;

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

        if (cameraSource!=null) {
            cameraSource.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMotionLoggerService();
        if (cameraSource!=null) {
            cameraSource.release();
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
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if (granted && rc == AppOpsManager.MODE_ALLOWED) {

            Log.d(DEBUG_TAG, " ...... !!!!! ....");
            startMotionLoggerService();

        }
        else {
            showDialog(this);
        }

        if (cameraSource != null) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                cameraSource.start();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
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

    public void createCameraSource() {
        FaceDetector detector = new FaceDetector.Builder(this)
                .setTrackingEnabled(true)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setMode(FaceDetector.FAST_MODE)
                .build();
        detector.setProcessor(new MultiProcessor.Builder(new FaceTrackerFactory()).build());

        cameraSource = new CameraSource.Builder(this, detector)
                .setRequestedPreviewSize(1024, 768)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build();

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            cameraSource.start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }



    private class FaceTrackerFactory implements MultiProcessor.Factory<Face> {

        private FaceTrackerFactory() {

        }

        @Override
        public Tracker<Face> create(Face face) {
            return new EyesTracker();
        }
    }

    private class EyesTracker extends Tracker<Face> {

        private final float THRESHOLD = 0.75f;

        private EyesTracker() {
            Log.d("EyesTracker", "constructor");
        }

        @Override
        public void onUpdate(Detector.Detections<Face> detections, Face face) {
            float left = face.getIsLeftEyeOpenProbability();
            float right = face.getIsRightEyeOpenProbability();
            float smile = face.getIsSmilingProbability();
            EyeData eyeData = new EyeData(left, right, smile, userInviteId);


//            mDatabase.child("sensors_extra").child("EYE_TRACKING").push().setValue(eyeData.toMap());

            Log.d("EyesTracker", "onUpdate: Eyes Detected");
//
//            if (face.getIsLeftEyeOpenProbability() > THRESHOLD || face.getIsRightEyeOpenProbability() > THRESHOLD) {
//                Log.d("EyesTracker", "onUpdate: Eyes Detected");
//            }
//            else {
//
//                Log.d("EyesTracker", "Eyes Detected and closed");
//            }
        }

        @Override
        public void onMissing(Detector.Detections<Face> detections) {
            super.onMissing(detections);
//            Log.d("EyesTracker","Face Not Detected yet!");
        }

        @Override
        public void onDone() {
            super.onDone();
        }
    }


}






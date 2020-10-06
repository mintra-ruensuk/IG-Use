package com.example.innerstates;

import android.app.Activity;
import android.content.SharedPreferences;
import android.provider.Settings;

import java.util.Random;

public class MyUtil {

    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";

    public static long getCurrentTime() {
        return System.currentTimeMillis() / 1000L;
    }

    public static int generateFiveDigit() {
//        Random r = new Random( MyUtil.getCurrentTime() );
//        return ((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
        String ALLOWED_INT = "123456789";
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(8);
        for(int i=0;i<8;++i)
            sb.append(ALLOWED_INT.charAt(random.nextInt(ALLOWED_INT.length())));
        return Integer.parseInt(sb.toString());
    }

    public static String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    public static String getDeviceUniqueID(Activity activity){
        String device_unique_id = Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return device_unique_id;
    }


    /**
     * Returns the unique identifier for the device
     *
     * @return unique identifier for the device
     */
    public static String getDeviceIMEI(Activity activity) {
//        //FIXXXXXX ERRORRRR HEREEEEEE
//        String deviceUniqueIdentifier = null;
//        TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
//        if (null != tm) {
//            deviceUniqueIdentifier = tm.getDeviceId();
//        }
//        if (null == deviceUniqueIdentifier || 0 == deviceUniqueIdentifier.length()) {
//            deviceUniqueIdentifier = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
//        }
//        return deviceUniqueIdentifier;
        return "FIX ME";
    }


}

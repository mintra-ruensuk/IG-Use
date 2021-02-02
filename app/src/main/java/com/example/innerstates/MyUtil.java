package com.example.innerstates;

import android.app.Activity;
import android.provider.Settings;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class MyUtil {

    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";
    public static final String FIREBASE_URL = "https://interactions-6d8cf-default-rtdb.firebaseio.com/";

    public static long getCurrentTime() {
        return System.currentTimeMillis() / 1L;
//        return System.currentTimeMillis() / 1000L;

    }

    public static long getCurrentTime1000() {
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
    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    public static double calculateStandardDeviation(double[] sd) {

        if (sd.length == 0) return 0; // don't divide by zero!

        double sum = 0;
        double newSum = 0;

        for (int i = 0; i < sd.length; i++) {
            sum = sum + sd[i];
        }
        double mean = (sum) / (sd.length);

        for (int j = 0; j < sd.length; j++) {
            // put the calculation right in there
            newSum = newSum + ((sd[j] - mean) * (sd[j] - mean));
        }
        double squaredDiffMean = (newSum) / (sd.length);
        double standardDev = (Math.sqrt(squaredDiffMean));

        return standardDev;
    }

    public static double[] toPrimitive(Double[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            double[] doubles = new double[1];
            return doubles;
        }
        final double[] result = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].doubleValue();
        }
        return result;
    }

    public static double calculateAverage(List<Double> marks) {
        Double sum = 0.0;
        if(!marks.isEmpty()) {
            for (Double mark : marks) {
                sum += mark;
            }
            return sum.doubleValue() / marks.size();
        }
        return sum;
    }
    public static double getMedianFromArray(double[] numArray) {
        Arrays.sort(numArray);
        double median;
        if (numArray.length % 2 == 0)
            median = ((double)numArray[numArray.length/2] + (double)numArray[numArray.length/2 - 1])/2;
        else
            median = (double) numArray[numArray.length/2];
        return median;
    }

    public static double getVariance(double a[]){
        // Compute mean (average
        // of elements)
        double sum = 0;
        int n = a.length;
        if (n == 0) return 0; // don't divide by zero!

        for (int i = 0; i < n; i++)
            sum += a[i];
        double mean = (double)sum /
                (double)n;

        // Compute sum squared
        // differences with mean.
        double sqDiff = 0;
        for (int i = 0; i < n; i++)
            sqDiff += (a[i] - mean) *
                    (a[i] - mean);

        return (double)sqDiff / n;
    }


}

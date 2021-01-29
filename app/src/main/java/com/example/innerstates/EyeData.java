package com.example.innerstates;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class EyeData {
    private float left;
    private float right;
    private float smile;
    private String userId;
    public long timeStamp;

    public EyeData(float left, float right, float smile, String userId) {
        this.left = left;
        this.right = right;
        this.smile = smile;
        this.userId = userId;
        this.timeStamp = MyUtil.getCurrentTime();
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("left_eye", left);
        result.put("right_eye", right);
        result.put("smile", smile);
        result.put("user_id", userId);
        result.put("time_stamp", timeStamp);

        return result;
    }
}

package com.example.innerstates;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Notification {
    private static final String NOTIFY = "notified";
    private static final String MISSED = "missed";
    private static final String OPENED = "opened";

    private long timeStamp;
    private String status;
    private String userId;
    private int notificatioId;

    public Notification(String uid, int notificatioId) {
        this.timeStamp = System.currentTimeMillis() / 1000L;
        this.status = NOTIFY;
        this.userId = uid;
        this.notificatioId = notificatioId;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("user_id", userId);
        result.put("notification_id", notificatioId);
        result.put("time_stamp", timeStamp);
        result.put("status", status);
        return result;
    }
}

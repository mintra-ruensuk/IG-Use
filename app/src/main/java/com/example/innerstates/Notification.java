package com.example.innerstates;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Notification {
    private static final String NOTIFY = "notified";
    private static final String MISSED = "missed";
    private static final String OPENED = "opened";

    private long createdTimeStamp;
    private long openedTimeStamp;
    private String status;
    private String userId;
    private int notificationId;

    public Notification() {

    }
    public Notification(String uid, int notificationId) {
        this.createdTimeStamp = System.currentTimeMillis() / 1000L;
        this.status = NOTIFY;
        this.userId = uid;
        this.notificationId = notificationId;
        this.openedTimeStamp = 0;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("user_id", userId);
        result.put("notification_id", notificationId);
        result.put("create_time_stamp", createdTimeStamp);
        result.put("status", status);
        result.put("open_time_stamp", openedTimeStamp);
        return result;
    }
}

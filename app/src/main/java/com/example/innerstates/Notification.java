package com.example.innerstates;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Notification {
    public static final String NOTIFY = "notified";
    public static final String MISSED = "missed";
    public static final String OPENED = "opened";

    private long createdTimeStamp;
    private long openedTimeStamp;
    private String status;
    private String userId;
    private int notificationId;
    private String inviteUserId;

    public Notification() {

    }
    public Notification(String uid, int notificationId, String inviteUserId) {
        this.createdTimeStamp = MyUtil.getCurrentTime();
        this.status = NOTIFY;
        this.userId = uid;
        this.notificationId = notificationId;
        this.openedTimeStamp = 0;
        this.inviteUserId = inviteUserId;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("user_id", userId);
        result.put("notification_id", notificationId);
        result.put("create_time_stamp", createdTimeStamp);
        result.put("status", status);
        result.put("open_time_stamp", openedTimeStamp);
        result.put("invite_user_id", inviteUserId);
        return result;
    }
}

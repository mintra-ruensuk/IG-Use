package com.example.innerstates;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class SurveyData {
    private final String INCOMPLETE = "incomplete";
    private final String DONE = "done";
    private long timeStamp;
    private String uid;
    private String status;
    private HashMap<String, Object> answer ;

    public SurveyData(String uid) {
        this.timeStamp = System.currentTimeMillis() / 1000L;
        this.uid = uid;
        this.status = INCOMPLETE;
        answer = new HashMap<>();
        answer.put("type_of_communication", "");
        answer.put("s1", 0);
        answer.put("s2", 0);
        answer.put("e1", 0);
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("time_stamp", timeStamp);
        result.put("status", status);
        result.put("answer", answer);

        return result;
    }
}
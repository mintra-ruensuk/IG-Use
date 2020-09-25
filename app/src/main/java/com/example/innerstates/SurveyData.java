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
    private String pageFlow;
    private String surveyId;
    private HashMap<String, Object> answer ;

    public SurveyData() {

    }
    public SurveyData(String uid, String surveyId) {
        this.timeStamp = MyUtil.getCurrentTime();
        this.uid = uid;
        this.status = INCOMPLETE;
        this.pageFlow = "";
        this.surveyId = surveyId;
        answer = new HashMap<>();
        answer.put("t1", "0"); // type of communication
        answer.put("s1", "0"); // social comparison 1
        answer.put("s2", "0"); // social comparison 2
        answer.put("e1", "0"); // envy 1
        answer.put("e2", "0"); // envy 2
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("user_id", uid);
        result.put("time_stamp", timeStamp);
        result.put("status", status);
        result.put("page_flow", status);
        result.put("survey_id", surveyId);
        result.put("answer", answer);

        return result;
    }

    public HashMap<String, Object> getAnswer() {
        return answer;
    }
}

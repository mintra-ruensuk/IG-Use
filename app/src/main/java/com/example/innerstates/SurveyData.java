package com.example.innerstates;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class SurveyData {
    public static final String INCOMPLETE = "incomplete";
    public static final String DONE = "done";
    private long timeStamp;
    private long doneTimeStamp;
    private String uid;
    private String status;
    private String pageFlow;
    private String surveyId;
    private String inviteUserId;
    private HashMap<String, Object> answer ;

    public SurveyData() {

    }
    public SurveyData(String uid, String surveyId, String inviteUserId) {
        this.timeStamp = MyUtil.getCurrentTime();
        this.uid = uid;
        this.status = INCOMPLETE;
        this.pageFlow = "1";
        this.surveyId = surveyId;
        this.inviteUserId = inviteUserId;
        answer = new HashMap<>();
        answer.put("ty1", "0"); // type of communication
        answer.put("so1", "0"); // social comparison 1
        answer.put("so2", "0"); // social comparison 2
        answer.put("ev1", "0"); // envy 1
        answer.put("ev2", "0"); // envy 2
        answer.put("ev3", "0"); // envy 2
        answer.put("ev4", "0"); // envy 1
        answer.put("ev5", "0"); // envy 2
        answer.put("ev6", "0"); // envy 2
        answer.put("es1", "0"); // self-esteem 1
        answer.put("es2", "0"); // self-esteem 2
        answer.put("dp1", "0"); // depress 1
        answer.put("dp2", "0"); // depress 2
        answer.put("bd1", "0"); // body comparison 1
        answer.put("bd2", "0"); // body comparison 2
        answer.put("sa1", "0"); // valence
        answer.put("sa2", "0"); // arousal
//        answer.put("op1", "0"); // open-ended question 1
//        answer.put("op2", "0"); // open-ended question 2


    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("user_id", uid);
        result.put("start_time_stamp", timeStamp);
        result.put("done_time_stamp", timeStamp);
        result.put("status", status);
        result.put("page_flow", pageFlow);
        result.put("survey_id", surveyId);
        result.put("invite_user_id", inviteUserId);
        result.put("answer", answer);

        return result;
    }

    public HashMap<String, Object> getAnswer() {
        return answer;
    }

    public String getPageFlow() {
        return pageFlow;
    }

    public void setPageFlow(String pageFlow) {
        this.pageFlow = pageFlow;
    }

    @Override
    public String toString() {
        return "SurveyData{" +
                "timeStamp=" + timeStamp +
                ", uid='" + uid + '\'' +
                ", status='" + status + '\'' +
                ", pageFlow='" + pageFlow + '\'' +
                ", surveyId='" + surveyId + '\'' +
                ", inviteUserId='" + inviteUserId + '\'' +
                '}';
    }
}

package com.example.innerstates;

import android.app.NotificationManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.innerstates.lang.EnglishQuestion;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;

public class SurveyActivity extends AppCompatActivity {
    private HashMap<String, Question[]> surveyQuestion = new HashMap<String, Question[]>();
    private HashMap<String, Object> surveyAnswer = new HashMap<String, Object>();
    private int currentPage = 1;
    private LinearLayout questionLayOut;
    private int totalPage = 9;
    private Context mContext;
    private String userUniqueId;
    private String surveyKey;
    private int radioButtonId = 0;

    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = this.getBaseContext();
        questionLayOut = findViewById(R.id.questionLayout);

        // Set up user's unique ID (device id)
        userUniqueId = Settings.Secure.getString(mContext.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        Log.d("tagtag", "user unique id --> " + userUniqueId.toString());
        createSurvey();
        pushSurveyDataToDB();

        int notificationId = getIntent().getExtras().getInt("notificationId");
        if(notificationId > 0) {
            cancelNotification(mContext, notificationId);
            changeNotificationStatus(notificationId);
            Log.d("tagtag", "Yeah --> " + userUniqueId.toString());
        }

        displaySurvey();



    }

    private void displaySurvey() {


        populateSurveyAnswer();

        Log.d("tagtag----", "Displaying survey... " + currentPage);
        String page = "page" + currentPage;
        TextView pageTextView = findViewById(R.id.pageTextView);
        pageTextView.setText(currentPage + " / " + totalPage);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress((currentPage * 100) / totalPage);

        Question questions[] = surveyQuestion.get(page);
        for (Question question: questions) {
            TextView textView = new TextView(this);
            textView.setPadding(0,10,0,0);
            textView.setText(question.getQuestionTitle());
            textView.setTextColor(Color.BLACK);
            textView.setGravity(Gravity.CENTER);
            textView.setTypeface(null, Typeface.BOLD);

            questionLayOut.addView(textView);
            questionLayOut.addView(createRadioButton(question.getChoices(), question.getId()));

        }

        if (currentPage == 1) {
            Button nextButton = new Button(this);
            nextButton.setText("Next");
            nextButton.setId(currentPage);
            nextButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Log.d("tafftaff----", v.getId()+"");
                    nextSurvey();
                }
            });
            questionLayOut.addView(nextButton);
        }else {
            LinearLayout buttonLayout = new LinearLayout(this);
            buttonLayout.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            p.weight = 0.5f;

            Button backButton = new Button(this);
            backButton.setText("Back");
            backButton.setId(currentPage);
            backButton.setLayoutParams(p);
            backButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Log.d("tafftaff----", "Back button is pressed.");
                    backSurvey();
                }
            });

            Button nextButton = new Button(this);
            nextButton.setText("Next");
            nextButton.setId(currentPage);
            nextButton.setLayoutParams(p);
            nextButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Log.d("tafftaff----", v.getId()+"");
                    nextSurvey();
                }
            });
            buttonLayout.addView(backButton);
            buttonLayout.addView(nextButton);
            questionLayOut.addView(buttonLayout);
        }

    }

    private void nextSurvey() {
        questionLayOut.removeAllViews();
        currentPage += 1;
        displaySurvey();
    }

    private void backSurvey() {
        questionLayOut.removeAllViews();
        currentPage -= 1;
        displaySurvey();
    }
    private void pushSurveyDataToDB() {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String childName = "/users/" + userUniqueId + "/survey_data/";
        surveyKey = mDatabase.child(childName).push().getKey();
//        AppUsage appUsage = new AppUsage(userUniqueId, appPackageName, status, mContext);
        SurveyData surveyData = new SurveyData(userUniqueId);
        Map<String, Object> postValues = surveyData.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(childName + surveyKey, postValues);
        childUpdates.put("/survey_data/" + surveyKey, postValues);

        mDatabase.updateChildren(childUpdates);

        surveyAnswer = surveyData.getAnswer();
        mDatabase.child("/survey_data/" + surveyKey + "/answer/").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                surveyAnswer.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String key = ds.getKey();
                    String value = ds.getValue(String.class);
                    surveyAnswer.put(key, value);
//                    Log.d("tatat--->", surveyAnswer.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void createSurvey() {
        Question socialCompare1 = new Question("s1", EnglishQuestion.social1, disToAgree());
        Question socialCompare2 = new Question("s2", EnglishQuestion.social2, disToAgree());

        Question envy1 = new Question("e1", EnglishQuestion.envy1, disToAgree());
        Question envy2 = new Question("e2", EnglishQuestion.envy1, disToAgree());
        surveyQuestion.put("page1", new Question[] {socialCompare1, socialCompare2});
        surveyQuestion.put("page2", new Question[] {envy1, envy2});
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private RadioGroup createRadioButton(Choice[] choices, String questionId) {
        String selectedAnswer = (String) surveyAnswer.get(questionId);

        final RadioButton[] rb = new RadioButton[choices.length];
        RadioGroup rg = new RadioGroup(this); //create the RadioGroup
        rg.setOrientation(RadioGroup.HORIZONTAL);//or RadioGroup.VERTICAL
        rg.setPadding(0,0,0,50);
        for (int i = 0; i < choices.length; i++) {
            rb[i] = new RadioButton(this);
            rb[i].setText(choices[i].getChoiceTitle());
            rb[i].setId(++radioButtonId);
            rb[i].setTag(questionId + choices[i].getChoiceValue());
            rb[i].setButtonDrawable(null);
            rb[i].setWidth(200);



            TypedArray a = getTheme().obtainStyledAttributes(R.style.AppTheme, new int[] {android.R.attr.listChoiceIndicatorSingle});
            int attributeResourceId = a.getResourceId(0, 0);
//            Drawable drawable = getResources().getDrawable(attributeResourceId);
            Drawable drawable = mContext.getDrawable(attributeResourceId);
            DrawableCompat.setTint(drawable, Color.BLUE);

            rb[i].setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
            rb[i].setGravity(Gravity.CENTER | Gravity.BOTTOM);

            if( (i+1) == Integer.parseInt(selectedAnswer)) {
                Log.d("ttag--->>>", "id-> "+ questionId + " .... selectedAnswer-> " + selectedAnswer);
                rb[i].setChecked(true);
            }



            rg.addView(rb[i]);

        }
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = (RadioButton) findViewById(i);
                Log.d("Tatag", "-------->>> " + radioButton.getTag());
                saveChoiceToFirebase(radioButton.getTag().toString());
            }
        });
        return rg;

    }

    private void populateSurveyAnswer() {

    }

    private void saveChoiceToFirebase(String answerString) {
        String answerKey = answerString.substring(0,2);
        String answerValue = answerString.substring(2,3);

        String childName1 = "/users/" + userUniqueId + "/survey_data/" + surveyKey;
        String childName2 = "/survey_data/" + surveyKey;
        mDatabase.child(childName1).child("answer").child(answerKey).setValue(answerValue);
        mDatabase.child(childName2).child("answer").child(answerKey).setValue(answerValue);
    }

    private Choice[] disToAgree() {
        Choice[] choices = new Choice[5];
        choices[0] = new Choice("I strongly disagree", 1);
        choices[1] = new Choice("I disagree", 2);
        choices[2] = new Choice("I neither agree nor disagree", 3);
        choices[3] = new Choice("I agree", 4);
        choices[4] = new Choice("I strongly agree", 5);
        return choices;
    }

    public void cancelNotification(Context ctx, int notificationId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notificationId);
    }

    public void changeNotificationStatus(int notificationId) {

        long open_time_stamp = System.currentTimeMillis() / 1000L;

        String childName = "/users/" + userUniqueId + "/notification/";
        String childName2 = "/notification/";

        mDatabase.child(childName).child(notificationId+"").child("status").setValue(Notification.OPENED);
        mDatabase.child(childName).child(notificationId+"").child("open_time_stamp").setValue(open_time_stamp);

        mDatabase.child(childName2).child(notificationId+"").child("status").setValue(Notification.OPENED);
        mDatabase.child(childName2).child(notificationId+"").child("open_time_stamp").setValue(open_time_stamp);

    }
}
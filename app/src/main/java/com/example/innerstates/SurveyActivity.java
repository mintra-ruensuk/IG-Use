package com.example.innerstates;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.innerstates.lang.KoreanQuestion;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private int totalPage = 8;
    private Context mContext;
    private String userUniqueId;
    private String surveyKey;
    private int radioButtonId = 0;
    private SurveyData surveyData;
    private EditText[] openEndedText = new EditText[2];
    private String[] userOpenEndedText = new String[2];
    private List<RadioGroup> radioGroups = new ArrayList<>();

    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = database.getReference();
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = this.getBaseContext();
        questionLayOut = findViewById(R.id.questionLayout);


        sharedPref = mContext.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        userUniqueId = MyUtil.getDeviceUniqueID(this);

        Log.d("tagtag", "user unique id --> " + userUniqueId.toString());



        if(MainService.sample != null) {
            createSurvey();
            pushSurveyDataToDB();

            MainService.sample.setStatus(Sample.WAIT_FOR_NEXT_POPUP);
            changeNotiToOpenedStatus(MainService.notificationId);
            MainService.cancelAllNotification(mContext);
            displaySurvey();
        }




    }

    private void displaySurvey() {


//        populateSurveyAnswer();

        Log.d("tagtag----", "Displaying survey... " + currentPage);
        String page = "page" + currentPage;
        TextView pageTextView = findViewById(R.id.pageTextView);
        pageTextView.setText(currentPage + " / " + totalPage);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress((currentPage * 100) / totalPage);

        Question questions[] = surveyQuestion.get(page);
        if(page.equals("page8")) {
            TextView textView = new TextView(this);
            textView.setPadding(0,10,0,50);
            textView.setText(KoreanQuestion.depress1);
            textView.setTextColor(Color.BLACK);
            textView.setGravity(Gravity.CENTER);
            textView.setTypeface(null, Typeface.BOLD);

            questionLayOut.addView(textView);
        }else if(page.equals("page1")) {
            TextView textView = new TextView(this);
            textView.setPadding(0,10,0,50);
            textView.setText(KoreanQuestion.sameText);
            textView.setTextColor(Color.BLACK);
            textView.setGravity(Gravity.CENTER);
            textView.setTypeface(null, Typeface.BOLD);

            questionLayOut.addView(textView);
        }
        int index = 0;
        for (Question question: questions) {
            if(page.equals("page1")) {

                ImageView imageView = new ImageView(this);
                if(question.getId().equals("sa1")) {
                    imageView.setImageResource(R.drawable.valence);
                }else {
                    imageView.setImageResource(R.drawable.arousal);
                }
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                imageView.setAdjustViewBounds(true);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(p);

                questionLayOut.addView(imageView);
                questionLayOut.addView(createRadioButton(question.getChoices(), question.getId()));
            }
//            if(page.equals("page9")) {
//
//                TextView textView = new TextView(this);
//                textView.setPadding(0,10,0,50);
//                textView.setText(question.getQuestionTitle());
//                textView.setTextColor(Color.BLACK);
//                textView.setGravity(Gravity.CENTER);
//                textView.setTypeface(null, Typeface.BOLD);
//
//                questionLayOut.addView(textView);
//
//                openEndedText[index] = new EditText(this);
//                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
//                openEndedText[index].setLayoutParams(lp);
//                openEndedText[index].setWidth(500);
//                openEndedText[index].setSingleLine(false);
//                openEndedText[index].setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
//                openEndedText[index].setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
//                openEndedText[index].setText(userOpenEndedText[index]);
////                openEndedText[index].setHint("Write at least 20 characters");
//
//                questionLayOut.addView(openEndedText[index]);
//                index += 1;
//
//            }
            else {
                TextView textView = new TextView(this);
                textView.setPadding(0,10,0,0);
                textView.setText(question.getQuestionTitle());
                textView.setTextColor(Color.BLACK);
                textView.setGravity(Gravity.CENTER);
                textView.setTypeface(null, Typeface.BOLD);

                questionLayOut.addView(textView);
                questionLayOut.addView(createRadioButton(question.getChoices(), question.getId()));
            }



        }

        if (currentPage == 1) {
            Button nextButton = new Button(this);
            nextButton.setText("Next");
            nextButton.setId(currentPage);
            nextButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    nextSurvey();
                }
            });
            questionLayOut.addView(nextButton);
        }else {
            LinearLayout buttonLayout = new LinearLayout(this);
            buttonLayout.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            p.weight = 0.5f;
            p.setMargins(0,0,0,50);

            Button backButton = new Button(this);
            backButton.setText("Back");
            backButton.setId(currentPage);
            backButton.setLayoutParams(p);
            backButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    backSurvey();
                }
            });

            String nextText = "Next";
            if(currentPage == 8) {
                nextText = "Done";
            }
            Button nextButton = new Button(this);
            nextButton.setText(nextText);
            nextButton.setId(currentPage);
            nextButton.setLayoutParams(p);
            nextButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    nextSurvey();
                }
            });
            buttonLayout.addView(backButton);
            buttonLayout.addView(nextButton);
            questionLayOut.addView(buttonLayout);
        }

    }

    private void nextSurvey() {
        if(validateAnswer()) {
            currentPage += 1;
            pushFlowToDB();
            if(currentPage == 9) {
//                pushOpenEndedText();
                createThankYouPage();
            }else if(currentPage <= 8){
                questionLayOut.removeAllViews();
                displaySurvey();
            }
        }else if(currentPage < 8){
            // Display alert
            AlertDialog alertDialog = new AlertDialog.Builder(SurveyActivity.this).create();
            alertDialog.setTitle("Warning!");
            alertDialog.setMessage("You must select all answers.");
            alertDialog.setCancelable(false);
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }

    }

    private void backSurvey() {
        radioGroups.clear();
        questionLayOut.removeAllViews();
        currentPage -= 1;
        pushFlowToDB();
        displaySurvey();
    }

    private boolean validateAnswer() {
        if(currentPage == 9) {
            for (int i = 0 ; i < openEndedText.length ; i ++) {
                if( openEndedText[i].getText().toString().length() <= 20 ) {
                    openEndedText[i].setError("You must write at least 20 characters.");
                    return false;
                }
            }
        }else {
            for(RadioGroup rg: radioGroups) {
                if (rg.getCheckedRadioButtonId() == -1){
                    return false;
                }
            }
            radioGroups.clear();
        }
        return true;
    }

//    private void pushOpenEndedText() {
//        final String childName = "/users/" + userUniqueId + "/survey_data/" + surveyKey + "/answer/";
//        final String childName2 = "/survey_data/" + surveyKey;
//
//        userOpenEndedText = getUserOpenEndedText();
//
//
//        mDatabase.child(childName).child("answer").child("op1").setValue(userOpenEndedText[0]);
//        mDatabase.child(childName2).child("answer").child("op1").setValue(userOpenEndedText[0]);
//        mDatabase.child(childName).child("answer").child("op2").setValue(userOpenEndedText[1]);
//        mDatabase.child(childName2).child("answer").child("op2").setValue(userOpenEndedText[1]);
//
//        mDatabase.child(childName).child("status").setValue(SurveyData.DONE);
//        mDatabase.child(childName2).child("status").setValue(SurveyData.DONE);
//
//    }

    private void pushFlowToDB() {
        final String childName = "/users/" + userUniqueId + "/survey_data/" + surveyKey;
        final String childName2 = "/survey_data/" + surveyKey;
        final String page = currentPage + "";
        surveyData.setPageFlow(surveyData.getPageFlow()+page);

        mDatabase.child(childName).child("page_flow").setValue(surveyData.getPageFlow());
        mDatabase.child(childName2).child("page_flow").setValue(surveyData.getPageFlow());

    }
    private void pushSurveyDataToDB() {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String childName = "/users/" + userUniqueId + "/survey_data/";
        String surveyId = MyUtil.getRandomString(10);

//        surveyKey = mDatabase.child(childName).push().getKey();
        surveyKey = mDatabase.child(childName).child(surveyId).getKey();
//        AppUsage appUsage = new AppUsage(userUniqueId, appPackageName, status, mContext);
        surveyData = new SurveyData(userUniqueId, surveyId, getInviteUserId());
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
        Question typeCommmu = new Question("ty1", KoreanQuestion.type_of_communication, KoreanQuestion.choiceCommunication());

        Question socialCompare1 = new Question("so1", KoreanQuestion.social1, KoreanQuestion.disToAgree());
        Question socialCompare2 = new Question("so2", KoreanQuestion.social2, KoreanQuestion.disToAgree());

        Question body1 = new Question("bd1", KoreanQuestion.body1, KoreanQuestion.bodyScale());
        Question body2 = new Question("bd2", KoreanQuestion.body2, KoreanQuestion.bodyScale());

        Question envy1 = new Question("ev1", KoreanQuestion.envy1, KoreanQuestion.disToAgree());
        Question envy2 = new Question("ev2", KoreanQuestion.envy2, KoreanQuestion.disToAgree());
        Question envy3 = new Question("ev3", KoreanQuestion.envy3, KoreanQuestion.disToAgree());
        Question envy4 = new Question("ev4", KoreanQuestion.envy4, KoreanQuestion.disToAgree());
        Question envy5 = new Question("ev5", KoreanQuestion.envy5, KoreanQuestion.disToAgree());
        Question envy6 = new Question("ev6", KoreanQuestion.envy6, KoreanQuestion.disToAgree());

        Question esteem1 = new Question("es1", KoreanQuestion.esteem1, KoreanQuestion.esteemScale());
        Question esteem2 = new Question("es2", KoreanQuestion.esteem2, KoreanQuestion.esteemScale());
        Question esteem3 = new Question("es3", KoreanQuestion.esteem3, KoreanQuestion.esteemScale());



        Question depress1 = new Question("dp1", KoreanQuestion.depress2, KoreanQuestion.depressScale());
        Question depress2 = new Question("dp2", KoreanQuestion.depress3, KoreanQuestion.depressScale());



        Question valence = new Question("sa1", "", KoreanQuestion.samScale());
        Question arousal = new Question("sa2", "", KoreanQuestion.samScale());

        Question openQ1 = new Question("op1", KoreanQuestion.openQ1, KoreanQuestion.samScale());
        Question openQ2 = new Question("op2", KoreanQuestion.openQ2, KoreanQuestion.samScale());

        surveyQuestion.put("page1", new Question[] {valence, arousal});
        surveyQuestion.put("page2", new Question[] {typeCommmu});
        surveyQuestion.put("page3", new Question[] {socialCompare1, socialCompare2});
        surveyQuestion.put("page4", new Question[] {body1, body2});
        surveyQuestion.put("page5", new Question[] {envy1, envy2, envy3});
        surveyQuestion.put("page6", new Question[] {envy4, envy5, envy6});
        surveyQuestion.put("page7", new Question[] {esteem1, esteem2, esteem3});
        surveyQuestion.put("page8", new Question[] {depress1, depress2});


//        surveyQuestion.put("page9", new Question[] {openQ1, openQ2});

    }

    private void createThankYouPage(){
        changeSurveyStatusToDone();
        AlertDialog alertDialog = new AlertDialog.Builder(SurveyActivity.this).create();
        alertDialog.setTitle("Thank you!");
        alertDialog.setMessage("Thank you for your answer.");
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        MainService.sample.setStatus(Sample.WAIT_FOR_NEXT_POPUP);
                        finish();
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private RadioGroup createRadioButton(Choice[] choices, String questionId) {
        String selectedAnswer = (String) surveyAnswer.get(questionId);
//        int[] choiceWidth = {400,200,200,200,200,250,250,110,100};
        int[] choiceWidth = {110,400,200,200,200,200,250,250};

        final RadioButton[] rb = new RadioButton[choices.length];
        RadioGroup rg = new RadioGroup(this); //create the RadioGroup
        rg.setOrientation(RadioGroup.HORIZONTAL);//or RadioGroup.VERTICAL
        rg.setGravity(Gravity.CENTER_HORIZONTAL);
        rg.setPadding(0,10,0,60);
        for (int i = 0; i < choices.length; i++) {
            rb[i] = new RadioButton(this);
            rb[i].setText(choices[i].getChoiceTitle());
            rb[i].setId(++radioButtonId);
            rb[i].setTag(questionId + choices[i].getChoiceValue());
            rb[i].setButtonDrawable(null);
            rb[i].setWidth(choiceWidth[currentPage-1]);



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
        radioGroups.add(rg);
        return rg;

    }

    private void populateSurveyAnswer() {

    }

    private void saveChoiceToFirebase(String answerString) {
        String answerKey = answerString.substring(0,3);
        String answerValue = answerString.substring(3,4);

        String childName1 = "/users/" + userUniqueId + "/survey_data/" + surveyKey;
        String childName2 = "/survey_data/" + surveyKey;
        mDatabase.child(childName1).child("answer").child(answerKey).setValue(answerValue);
        mDatabase.child(childName2).child("answer").child(answerKey).setValue(answerValue);
    }



    public void cancelNotification(Context ctx, int notificationId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notificationId);
    }

    public void changeNotiToOpenedStatus(int notificationId) {

        long open_time_stamp = System.currentTimeMillis() / 1000L;

        String childName = "/users/" + userUniqueId + "/notification/";
        String childName2 = "/notification/";

        mDatabase.child(childName).child(notificationId+"").child("status").setValue(Notification.OPENED);
        mDatabase.child(childName).child(notificationId+"").child("open_time_stamp").setValue(open_time_stamp);

        mDatabase.child(childName2).child(notificationId+"").child("status").setValue(Notification.OPENED);
        mDatabase.child(childName2).child(notificationId+"").child("open_time_stamp").setValue(open_time_stamp);

    }

    public String[] getUserOpenEndedText() {
        for (int i = 0 ; i < openEndedText.length ; i++) {
            if(openEndedText[i] != null  && openEndedText[i].getText() != null) {
                userOpenEndedText[i] = openEndedText[i].getText().toString();
            }
        }

        return userOpenEndedText;
    }

    public String getInviteUserId() {
        return sharedPref.getString(getString(R.string.invitation_user_id), "nodata");
    }

    public void changeSurveyStatusToDone() {

        long doneTime = MyUtil.getCurrentTime();
        String childName1 = "/users/" + userUniqueId + "/survey_data/" + surveyKey;
        String childName2 = "/survey_data/" + surveyKey;
        mDatabase.child(childName1).child("status").setValue(SurveyData.DONE);
        mDatabase.child(childName2).child("status").setValue(SurveyData.DONE);

        mDatabase.child(childName1).child("done_time_stamp").setValue(doneTime);
        mDatabase.child(childName2).child("done_time_stamp").setValue(doneTime);
    }


}
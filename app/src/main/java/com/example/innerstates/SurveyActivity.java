package com.example.innerstates;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;

public class SurveyActivity extends AppCompatActivity {
    private HashMap<String, Question[]> surveyQuestion = new HashMap<String, Question[]>();
    private int currentPage = 1;
    private LinearLayout questionLayOut;
    private int totalPage = 9;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = this.getBaseContext();
        questionLayOut = findViewById(R.id.questionLayout);

        createSurvey();

        displaySurvey();



    }

    private void displaySurvey() {
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
            questionLayOut.addView(createRadioButton(question.getChoices()));

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

    private void createSurvey() {
        Question socialCompare1 = new Question("I was paying a lot of attention to how I do things compared to how people I follow on Instagram do things", disToAgree());
        Question socialCompare2 = new Question("I was wanting to find out how well I do things compared to people I follow on Instagram", disToAgree());

        Question envy1 = new Question("I was generally feel inferior to others", disToAgree());
        surveyQuestion.put("page1", new Question[] {socialCompare1, socialCompare2});
        surveyQuestion.put("page2", new Question[] {envy1, socialCompare2});
    }

    private RadioGroup createRadioButton(Choice[] choices) {

        final RadioButton[] rb = new RadioButton[choices.length];
        RadioGroup rg = new RadioGroup(this); //create the RadioGroup
        rg.setOrientation(RadioGroup.HORIZONTAL);//or RadioGroup.VERTICAL
        rg.setPadding(0,0,0,50);
        for (int i = 0; i < choices.length; i++) {
            rb[i] = new RadioButton(this);
            rb[i].setText(choices[i].getChoiceTitle());
            rb[i].setId(choices[i].getChoiceValue() + 100);
            rb[i].setButtonDrawable(null);
            rb[i].setWidth(200);

            TypedArray a = getTheme().obtainStyledAttributes(R.style.AppTheme, new int[] {android.R.attr.listChoiceIndicatorSingle});
            int attributeResourceId = a.getResourceId(0, 0);
            Drawable drawable = getResources().getDrawable(attributeResourceId);
            DrawableCompat.setTint(drawable, Color.BLUE);

            rb[i].setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
            rb[i].setGravity(Gravity.CENTER | Gravity.BOTTOM);
            rg.addView(rb[i]);

        }
        return rg;

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
}
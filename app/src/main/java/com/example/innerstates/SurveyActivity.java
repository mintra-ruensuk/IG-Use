package com.example.innerstates;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;

import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class SurveyActivity extends AppCompatActivity {
    private HashMap<String, Question[]> surveyQuestion = new HashMap<String, Question[]>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        createSurvey();

        LinearLayout questionLayOut = (LinearLayout) findViewById(R.id.questionLayout);

        Question questions[] = surveyQuestion.get("page1");
        for (Question question: questions) {
            TextView textView = new TextView(this);
            textView.setPadding(0,10,0,0);
            textView.setText(question.getQuestionTitle());
            textView.setTextColor(Color.BLACK);
            textView.setGravity(Gravity.CENTER);
            questionLayOut.addView(textView);
            questionLayOut.addView(createRadioButton(question.getChoices()));
        }



    }

    private void createSurvey() {
        Question socialCompare1 = new Question("I was paying a lot of attention to how I do things compared to how people I follow on Instagram do things", disToAgree());
        Question socialCompare2 = new Question("I was wanting to find out how well I do things compared to people I follow on Instagram", disToAgree());
        surveyQuestion.put("page1", new Question[] {socialCompare1, socialCompare2});
        surveyQuestion.put("page2", new Question[] {socialCompare1, socialCompare2});
    }

    private RadioGroup createRadioButton(Choice[] choices) {

        final RadioButton[] rb = new RadioButton[choices.length];
        RadioGroup rg = new RadioGroup(this); //create the RadioGroup
        rg.setOrientation(RadioGroup.HORIZONTAL);//or RadioGroup.VERTICAL
        for (int i = 0; i < choices.length; i++) {
            rb[i] = new RadioButton(this);
            rb[i].setText(choices[i].getChoiceTitle());
            rb[i].setId(choices[i].getChoiceValue() + 100);
            rb[i].setButtonDrawable(null);

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
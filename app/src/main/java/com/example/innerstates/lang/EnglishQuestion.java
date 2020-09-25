package com.example.innerstates.lang;

import com.example.innerstates.Choice;

public class EnglishQuestion {
    public static String type_of_communication = "What best describes how you used Instagram just now for communication?";
    public static String social1 = "I was paying a lot of attention to how I do things compared to how people I follow on Instagram do things";
    public static String social2 = "I was wanting to find out how well I do things compared to people I follow on Instagram";

    public static String envy1 = "I was generally feel inferior to others";
    public static String envy2 = "EV2 I was generally feel inferior to others";

    public static Choice[] disToAgree() {
        Choice[] choices = new Choice[5];
        choices[0] = new Choice("I strongly disagree", 1);
        choices[1] = new Choice("I disagree", 2);
        choices[2] = new Choice("I neither agree nor disagree", 3);
        choices[3] = new Choice("I agree", 4);
        choices[4] = new Choice("I strongly agree", 5);
        return choices;
    }
}

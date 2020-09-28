package com.example.innerstates.lang;

import com.example.innerstates.Choice;

public class EnglishQuestion {
    // ""
    public static String type_of_communication = "What best describes how you used Instagram just now for communication?";

    //
    public static String social1 = "I was paying a lot of attention to how I do things compared to how people I follow on Instagram do things.";

    //
    public static String social2 = "I was wanting to find out how well I do things compared to people I follow on Instagram";

    //
    public static String envy1 = "I generally feel inferior to others";

    //
    public static String envy2 = "It is so frustrating to see some people I follow on Instagram always having a good time";

    // Original:
    public static String envy3 = "It somehow doesn’t seem fair that some people I follow on Instagram seem to have all the fun";

    //
    public static String envy4 = "I wish I can travel as much as some of my friends on Instagram do";

    //
    public static String envy5 = "Many of my friends on Instagram have a better life than me";

    //
    public static String envy6 = "Many of my friends on Instagram are happier than me";


    //
    public static String esteem1 = "On the whole, I am satisfied with myself.";

    //
    public static String esteem2 = "I am able to do things as well as most other people I follow on Instagram.";

    //
    public static String esteem3 = "I feel I do not have much to be proud of";

    //
    public static String depress1 = "From the last time we ask, how often have you been bothered by the following problems?";

    //
    public static String depress2 = "Little interest or pleasure in doing things";

    //
    public static String depress3 = "Feeling down, depressed or hopeless";

    //
    public static String body1 = "When I was viewing posts on Instagram, I focus on their (body) appearance";

    //
    public static String body2 = "When I was viewing posts on Instagram, I compared their overall (body) appearance to my (body) appearance";

    //
    public static String sameText = "How do you feel now?";

    //
    public static String openQ1 = "Why do you think you were feeling this way?";

    //
    public static String openQ2 = "Describe a few examples of posts or actions (e.g., push like, Direct message, etc.,) you did that make you feel that way.";




    public static Choice[] choiceCommunication() {
        // For direct communication with others, For consuming information without direct communication with others
        Choice[] choices = new Choice[2];
        choices[0] = new Choice("Direct exchanges with others", 1);
        choices[1] = new Choice("Consuming information without direct exchanges ", 2);
        return choices;
    }

    public static Choice[] disToAgree() {
        // I strongly disagree ---> I strongly agree
        Choice[] choices = new Choice[5];
        choices[0] = new Choice("I strongly disagree", 1);
        choices[1] = new Choice("I disagree", 2);
        choices[2] = new Choice("I neither agree nor disagree", 3);
        choices[3] = new Choice("I agree", 4);
        choices[4] = new Choice("I strongly agree", 5);
        return choices;
    }

    public static Choice[] esteemScale() {
        // I strongly disagree ---> I strongly agree
        Choice[] choices = new Choice[4];
        choices[0] = new Choice("I strongly disagree", 1);
        choices[1] = new Choice("I disagree", 2);
        choices[2] = new Choice("I agree", 3);
        choices[3] = new Choice("I strongly agree", 4);
        return choices;
    }

    public static Choice[] depressScale() {

        Choice[] choices = new Choice[4];
        choices[0] = new Choice("Not at all", 1);
        choices[1] = new Choice("Several times", 2);
        choices[2] = new Choice("More than half of the time", 3);
        choices[3] = new Choice("Nearly all the time", 4);
        return choices;
    }

    public static Choice[] bodyScale() {

        Choice[] choices = new Choice[5];
        choices[0] = new Choice("Not at all", 1);
        choices[1] = new Choice("", 2);
        choices[2] = new Choice("", 3);
        choices[3] = new Choice("", 4);
        choices[4] = new Choice("Nearly all the time", 5);
        return choices;
    }

    public static Choice[] samScale() {

        Choice[] choices = new Choice[9];
        choices[0] = new Choice("", 1);
        choices[1] = new Choice("", 2);
        choices[2] = new Choice("", 3);
        choices[3] = new Choice("", 4);
        choices[4] = new Choice("", 5);
        choices[5] = new Choice("", 6);
        choices[6] = new Choice("", 7);
        choices[7] = new Choice("", 8);
        choices[8] = new Choice("", 9);
        return choices;
    }
}

package com.example.innerstates;
class Choice {
    private String choiceTitle;
    private int choiceValue;
    public Choice(String choiceTitle, int choiceValue) {
        this.choiceTitle = choiceTitle;
        this.choiceValue = choiceValue;
    }

    public String getChoiceTitle() {
        return choiceTitle;
    }

    public void setChoiceTitle(String choiceTitle) {
        this.choiceTitle = choiceTitle;
    }

    public int getChoiceValue() {
        return choiceValue;
    }

    public void setChoiceValue(int choiceValue) {
        this.choiceValue = choiceValue;
    }
}
public class Question {
    private String questionTitle;
    private Choice[] choices;
    public Question(String questionTitle, Choice[] choices) {
        this.questionTitle = questionTitle;
        this.choices = choices;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public Choice[] getChoices() {
        return choices;
    }

    public void setChoices(Choice[] choices) {
        this.choices = choices;
    }
}

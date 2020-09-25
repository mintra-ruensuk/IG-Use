package com.example.innerstates;

public class Choice {
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

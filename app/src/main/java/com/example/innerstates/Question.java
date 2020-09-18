package com.example.innerstates;

public class Question {
    private String questionTitle;
    private Choice[] choices;
    private String id;
    private String answer;
    public Question() {

    }
    public Question(String id, String questionTitle, Choice[] choices) {
        this.id = id;
        this.questionTitle = questionTitle;
        this.choices = choices;
    }
    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
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


    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}

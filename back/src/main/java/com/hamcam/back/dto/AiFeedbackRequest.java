package com.hamcam.back.dto;

public class AiFeedbackRequest {
    private String question;
    private String userAnswer;
    private String correctAnswer;
    private String explanation;

    public String getQuestion() { return question; }
    public String getUserAnswer() { return userAnswer; }
    public String getCorrectAnswer() { return correctAnswer; }
    public String getExplanation() { return explanation; }

    public void setQuestion(String question) { this.question = question; }
    public void setUserAnswer(String userAnswer) { this.userAnswer = userAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
} 
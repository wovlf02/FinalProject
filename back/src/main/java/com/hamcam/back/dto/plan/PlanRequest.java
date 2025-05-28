package com.hamcam.back.dto.plan;

public class PlanRequest {
    private String grade;   // 학년
    private String subject; // 과목
    private int weeks;      // 기간(주)
    private String range;   // 범위
    private String prompt;  // 프론트엔드에서 직접 생성한 프롬프트

    // Getter/Setter
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public int getWeeks() { return weeks; }
    public void setWeeks(int weeks) { this.weeks = weeks; }

    public String getRange() { return range; }
    public void setRange(String range) { this.range = range; }

    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
}

package org.example.ums.service.dto;

public class StudentScoreView {

    private final Integer studentId;
    private final String studentName;
    private final Double averageScore;
    private final Long attempts;

    public StudentScoreView(Integer studentId, String studentName, Double averageScore, Long attempts) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.averageScore = averageScore;
        this.attempts = attempts;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public Long getAttempts() {
        return attempts;
    }
}


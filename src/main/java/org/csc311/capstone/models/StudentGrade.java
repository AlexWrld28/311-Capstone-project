package org.csc311.capstone.models;

import java.math.BigDecimal;

public class StudentGrade {
    private int id;
    private String studentId;
    private String className;
    private String classCode;
    private BigDecimal grade;
    private int credits;
    private String term;

    public StudentGrade() {
    }

    public StudentGrade(int id, String studentId, String className, String classCode,
                        BigDecimal grade, int credits, String term) {
        this.id = id;
        this.studentId = studentId;
        this.className = className;
        this.classCode = classCode;
        this.grade = grade;
        this.credits = credits;
        this.term = term;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public BigDecimal getGrade() {
        return grade;
    }

    public void setGrade(BigDecimal grade) {
        this.grade = grade;
    }

    public String getGradeDisplay() {
        return grade == null ? "" : grade.stripTrailingZeros().toPlainString();
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}

package org.csc311.capstone.models;

public class Student {
    private String ID, firstName, lastName, department, major, gpa, imagePath;

    public Student() {
    }

    public Student(String ID, String firstName, String lastName, String department, String major, String gpa, String imagePath) {
        this.ID = ID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
        this.major = major;
        this.gpa = gpa;
        this.imagePath = imagePath;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getGpa() {
        return gpa;
    }

    public void setGpa(String gpa) {
        this.gpa = gpa;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}

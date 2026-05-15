package org.csc311.capstone.models;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

public class Student {

    @CsvBindByPosition(position = 0)
    @CsvBindByName(column = "ID")
    private String ID;

    @CsvBindByPosition(position = 1)
    @CsvBindByName(column = "First Name")
    private String firstName;

    @CsvBindByPosition(position = 3)
    @CsvBindByName(column = "Middle Name")
    private String middleName;

    @CsvBindByPosition(position = 3)
    @CsvBindByName(column = "Last Name")
    private String lastName;

    @CsvBindByPosition(position = 4)
    @CsvBindByName(column = "Department")
    private String department;

    @CsvBindByPosition(position = 5)
    @CsvBindByName(column = "Major")
    private String major;

    @CsvBindByPosition(position = 6)
    @CsvBindByName(column = "GPA")
    private String gpa;

    public Student(String id, String firstName, String middleName, String lastName, String department, String major, String gpa) {
        this.ID = id;
        this.firstName = firstName;
        this.middleName = lastName;
        this.lastName = lastName;
        this.department = department;
        this.major = major;
        this.gpa = gpa;
    }

    public Student() {
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

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
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
}
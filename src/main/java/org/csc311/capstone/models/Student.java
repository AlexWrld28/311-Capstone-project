package org.csc311.capstone.models;

import com.opencsv.bean.CsvBindAndJoinByPosition;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

import java.io.Serializable;

/**
 * @author Charles Gonzalez Jr
 */
public class Student{

    @CsvBindByPosition(position = 0)
    @CsvBindByName(column = "ID")
    String ID;

    @CsvBindByPosition(position = 1)
    @CsvBindByName(column = "First Name")
    String firstName;

    @CsvBindByPosition(position = 2)
    @CsvBindByName(column = "Last Name")
    String lastName;

    @CsvBindByPosition(position = 3)
    @CsvBindByName(column = "Department")
    String department;

    @CsvBindByPosition(position = 4)
    @CsvBindByName(column = "Major")
    String major;

    @CsvBindByPosition(position = 5)
    @CsvBindByName(column = "GPA")
    String gpa;

    public Student(String id, String firstName, String lastName, String department, String major, String gpa) {
        this.ID = id;
        this.firstName = firstName;
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

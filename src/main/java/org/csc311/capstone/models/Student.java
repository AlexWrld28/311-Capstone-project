package org.csc311.capstone.models;

/**
 * @author Charles Gonzalez Jr
 */
public class Student {
    String ID,firstName,lastName,department,major,gpa;

    public Student(String id, String firstName, String lastName, String department, String major, String gpa) {
        this.ID = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
        this.major = major;
        this.gpa = gpa;

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

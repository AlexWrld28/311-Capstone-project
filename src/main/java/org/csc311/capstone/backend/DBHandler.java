package org.csc311.capstone.backend;

import org.csc311.capstone.models.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBHandler {

    String CONNECTION_URL;
    String USER;
    String PASSWORD;

    public DBHandler(boolean testMode){
        if(testMode){ // for test classes only
           CONNECTION_URL = "jdbc:h2:mem:testdb;MODE=PostgreSQL;DATABASE_TO_UPPER=FALSE;INIT=RUNSCRIPT FROM 'classpath:data.sql'";
           USER = "sa";
           PASSWORD = "";
           try (Connection conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD)) {
               System.out.println("Connected to H2 in PostgreSQL mode!");
           }catch (SQLException e) {
               e.printStackTrace();
           }
        }else{
            // normal database setup
        }


    }

    /**
     * takes in a Result set and create student object using row data.
     * @param result
     * @return
     * @throws SQLException
     */
    private Student createStudent(ResultSet result) throws SQLException {
        String ID = result.getString("ID");
        String firstName =  result.getString("first_name");
        String lastName = result.getString("last_name");
        String department = result.getString("department");
        String major = result.getString("major");
        String gpa = result.getString("gpa");
        Student newStudent = new Student();
        newStudent.setID(ID);
        newStudent.setFirstName(firstName);
        newStudent.setLastName(lastName);
        newStudent.setDepartment(department);
        newStudent.setMajor(major);
        newStudent.setGpa(gpa);
        return newStudent;
    }

    /**
     * for obtain a list of all students in a database
     * @return list of all students
     */
    public List<Student> getAllStudents(){
        List<Student> returnList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD)) {
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery("SELECT * FROM student");
            while(result.next()) {
                returnList.add(createStudent(result));
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

        return returnList;
    }

    public List<Student> getStudentsByMajor(String major){
        List<Student> returnList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD)) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM student WHERE major = ?"); // do it this way to be safe from sql injection
            stmt.setString(1, major);
            ResultSet result = stmt.executeQuery();
            while(result.next()) {
                returnList.add(createStudent(result));
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

        return returnList;
    }




}

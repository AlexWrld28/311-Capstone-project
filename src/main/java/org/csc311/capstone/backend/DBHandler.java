package org.csc311.capstone.backend;
import org.csc311.capstone.models.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The DBHandler class provides functionality for database access and operations.
 * It supports interactions with a database to manage and retrieve student data.
 * @author Charles Gonzalez JR
 */
public class DBHandler {

    String CONNECTION_URL;
    String USER;
    String PASSWORD;

    public DBHandler(boolean testMode){
        if(testMode){ // for test classes only
           CONNECTION_URL = "jdbc:h2:mem:testdb;MODE=PostgreSQL;DATABASE_TO_UPPER=FALSE;INIT=RUNSCRIPT FROM 'classpath:data.sql'";
           USER = "sa";
           PASSWORD = "";
           try (var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD)) {
               System.out.println("Connected to H2 in PostgreSQL mode!");
           }catch (SQLException e) {
               e.printStackTrace();
           }
        }else{
            // normal database setup
        }


    }

    /**
     * Helper methods to create student from sql query result
     * @param result
     * @return
     * @throws SQLException
     */
    private Student createStudent(ResultSet result) throws SQLException {
        var ID = result.getString("ID");
        var firstName =  result.getString("first_name");
        var lastName = result.getString("last_name");
        var department = result.getString("department");
        var major = result.getString("major");
        var gpa = result.getString("gpa");
        return new Student(ID,firstName,lastName,department,major,gpa);

    }

    /**
     * for obtain a list of all students in a database
     * @return list of all students
     */
    public List<Student> getAllStudents(){
        var returnList = new ArrayList<Student>();
        try (var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD)) {
            var stmt = conn.createStatement();
            var result = stmt.executeQuery("SELECT * FROM student");
            while(result.next()) {
                returnList.add(createStudent(result));
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

        return returnList;
    }

    /**
     * Retrieves a list of students based on their major.
     *
     * @param major the major to filter students by
     * @return a list of students who are enrolled in the specified major
     */
    public List<Student> getStudentsByMajor(String major){
        var returnList = new ArrayList<Student>();
        try (var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD)) {
            var stmt = conn.prepareStatement("SELECT * FROM student WHERE major = ?"); // do it this way to be safe from sql injection
            stmt.setString(1, major);
            var result = stmt.executeQuery();
            while(result.next()) {
                returnList.add(createStudent(result));
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

        return returnList;
    }


    /**
     * Retrieves a list of students who belong to the specified department.
     *
     * @param department the department to filter students by
     * @return a list of students in the specified department
     */
    public List<Student> getStudentsByDepartment(String department) {
        var returnList = new ArrayList<Student>();
        try (var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD)) {
            var stmt = conn.prepareStatement("SELECT * FROM student WHERE department = ?"); // do it this way to be safe from sql injection
            stmt.setString(1, department);
            var result = stmt.executeQuery();
            while(result.next()) {
                returnList.add(createStudent(result));
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

        return returnList;
    }
}

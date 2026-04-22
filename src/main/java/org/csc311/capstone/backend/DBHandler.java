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
               // CORRECT
               Statement stmt = conn.createStatement();
               ResultSet result = stmt.executeQuery("SELECT * FROM student");
               if (result.next()) { // Move the cursor to the first row
                   String name = result.getString("first_name");
                   String lastName = result.getString("last_name");
                   String department = result.getString("department");
                   System.out.println("First student: " + name);
                   System.out.println("Last name: " + lastName);
                   System.out.println("department: " + department);
               } else {
                   System.out.println("No students found in the database.");
               }
           }catch (SQLException e) {
               e.printStackTrace();
           }
        }


    }

    public List<Student> getAllStudents(){
        return null;
    }




}

package org.csc311.capstone.backend;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * this class will test our SQL queries for students on a training database
 */
class DBHandlerTestStudent {

    static DBHandler dbHandler;

    @BeforeAll
    public static void testConnection(){
        dbHandler = new DBHandler(true);

    }

    @Test
    public void getAllStudents(){
        Assertions.assertEquals(50,dbHandler.getAllStudents().size());
    }

    @Test
    public void getStudentByMajor(){
        Assertions.assertEquals(3,dbHandler.getStudentsByMajor("Cognitive Science").size());
    }

    @Test
    public void getStudentByDepartment(){
        Assertions.assertEquals(11,dbHandler.getStudentsByDepartment("Psychology").size());
    }
  
}
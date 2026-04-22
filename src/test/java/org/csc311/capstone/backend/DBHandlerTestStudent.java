package org.csc311.capstone.backend;

import org.junit.jupiter.api.Test;

/**
 * this class will test our SQL queries for students on a training database
 */
class DBHandlerTestStudent {

    DBHandler dbHandler;

    @Test
    public void testConnection(){
        dbHandler = new DBHandler(true);

    }
  
}
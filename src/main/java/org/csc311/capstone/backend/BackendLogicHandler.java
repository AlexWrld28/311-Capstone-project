package org.csc311.capstone.backend;

import org.csc311.capstone.models.Staff;


/**
 * @author Charles Gonzalez Jr
 * This class implements the backend logic and application login state.
 */
public class BackendLogicHandler {

    Staff currentLoggedIn;
    DBHandler dbHandler;

    public BackendLogicHandler() {
        dbHandler = new DBHandler(false);
    }

    public void login(){}

    public void logout(){}

    public void getAllStudents(){}

    public void getStudentsWithMajor(String major){}






}

package org.csc311.capstone.backend;
import at.favre.lib.crypto.bcrypt.BCrypt;
import org.csc311.capstone.models.LoginDTO;
import org.csc311.capstone.models.RegisterDTO;
import org.csc311.capstone.models.Staff;
import org.csc311.capstone.models.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * The DBHandler class provides functionality for database access and operations.
 * It supports interactions with a database to manage and retrieve student data.
 * @author Charles Gonzalez JR
 */
public class DBHandler {

    String CONNECTION_URL;
    String USER;
    String PASSWORD;
    private Staff currentLoggedInUser;

    public DBHandler(boolean testMode){
        if(testMode){ // for test classes only
           CONNECTION_URL = "jdbc:h2:mem:testdb;MODE=PostgreSQL;DATABASE_TO_UPPER=FALSE;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'classpath:data.sql'";
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
     * Helper method to build a Staff object from a SQL result row.
     */
    private Staff createStaff(ResultSet result) throws SQLException {
        var staff = new Staff();
        staff.setID(result.getString("id"));
        staff.setFirstName(result.getString("first_name"));
        staff.setLastName(result.getString("last_name"));
        staff.setEmail(result.getString("email"));
        staff.setDepartment(result.getString("department"));
        staff.setJobType(result.getString("job_type"));
        staff.setImgURL(result.getString("img_url"));
        return staff;
    }

    /**
     * Authenticates a staff member using email and password.
     * On success, sets the current logged-in user.
     *
     * @param dto login credentials
     * @return Optional containing the Staff on success, empty on failure
     */
    public Optional<Staff> login(LoginDTO dto) {
        try (var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD)) {
            var stmt = conn.prepareStatement("SELECT * FROM staff WHERE email = ?");
            stmt.setString(1, dto.email());
            var result = stmt.executeQuery();
            if (result.next()) {
                var storedHash = result.getString("password_hash");
                var verified = BCrypt.verifyer().verify(dto.password().toCharArray(), storedHash.toCharArray()).verified;
                if (verified) {
                    currentLoggedInUser = createStaff(result);
                    return Optional.of(currentLoggedInUser);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Registers a new staff member with a BCrypt-hashed password.
     * Returns false if the email is already in use.
     *
     * @param dto registration details
     * @return true if registration succeeded, false otherwise
     */
    public boolean register(RegisterDTO dto) {
        try (var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD)) {
            var checkStmt = conn.prepareStatement("SELECT id FROM staff WHERE email = ?");
            checkStmt.setString(1, dto.email());
            if (checkStmt.executeQuery().next()) {
                return false;
            }
            var passwordHash = BCrypt.withDefaults().hashToString(12, dto.password().toCharArray());
            var id = "STF" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
            var stmt = conn.prepareStatement(
                "INSERT INTO staff (id, first_name, last_name, email, department, password_hash) VALUES (?, ?, ?, ?, ?, ?)"
            );
            stmt.setString(1, id);
            stmt.setString(2, dto.firstName());
            stmt.setString(3, dto.lastName());
            stmt.setString(4, dto.email());
            stmt.setString(5, dto.department());
            stmt.setString(6, passwordHash);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Returns the currently logged-in staff member, or null if no one is logged in.
     */
    public Staff getCurrentLoggedInUser() {
        return currentLoggedInUser;
    }

    /**
     * Logs out the current user by clearing the session.
     */
    public void logout() {
        currentLoggedInUser = null;
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
        var imagePath = result.getString("imagePath");
        return new Student(ID,firstName,lastName,department,major,gpa,imagePath);

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

package org.csc311.capstone.backend;

import org.csc311.capstone.models.LoginDTO;
import org.csc311.capstone.models.RegisterDTO;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests login, register, session tracking, and logout via DBHandler.
 * Tests are ordered because login depends on a prior register call.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DBHandlerTestStaff {

    static DBHandler dbHandler;
    static final String TEST_EMAIL = "jane.doe@school.edu";
    static final String TEST_PASSWORD = "securePass99";

    @BeforeAll
    static void setup() {
        dbHandler = new DBHandler(true);
    }

    @Test
    @Order(1)
    void testRegisterNewUser() {
        var dto = new RegisterDTO(TEST_EMAIL, TEST_PASSWORD, "Jane", "Doe", "Computer Science");
        assertTrue(dbHandler.register(dto));
    }

    @Test
    @Order(2)
    void testRegisterDuplicateEmailFails() {
        var dto = new RegisterDTO(TEST_EMAIL, "anotherPass", "Jane", "Doe", "Computer Science");
        assertFalse(dbHandler.register(dto));
    }

    @Test
    @Order(3)
    void testLoginSuccess() {
        var dto = new LoginDTO(TEST_EMAIL, TEST_PASSWORD);
        var result = dbHandler.login(dto);
        assertTrue(result.isPresent());
        assertEquals("Jane", result.get().getFirstName());
        assertEquals("Doe", result.get().getLastName());
        assertEquals(TEST_EMAIL, result.get().getEmail());
    }

    @Test
    @Order(4)
    void testGetCurrentLoggedInUserAfterLogin() {
        assertNotNull(dbHandler.getCurrentLoggedInUser());
        assertEquals(TEST_EMAIL, dbHandler.getCurrentLoggedInUser().getEmail());
    }

    @Test
    @Order(5)
    void testLoginWrongPasswordFails() {
        var dto = new LoginDTO(TEST_EMAIL, "wrongPassword!");
        var result = dbHandler.login(dto);
        assertFalse(result.isPresent());
    }

    @Test
    @Order(6)
    void testLoginNonexistentEmailFails() {
        var dto = new LoginDTO("nobody@school.edu", TEST_PASSWORD);
        var result = dbHandler.login(dto);
        assertFalse(result.isPresent());
    }

    @Test
    @Order(7)
    void testLogout() {
        dbHandler.login(new LoginDTO(TEST_EMAIL, TEST_PASSWORD));
        dbHandler.logout();
        assertNull(dbHandler.getCurrentLoggedInUser());
    }
}

package ch.epfl.qedit.model;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

import org.junit.Test;

public class UserTest {
    @Test
    public void dummyTestUser() {
        User user = new User("John", "Doe", User.Role.Administrator);
        assertEquals(user.getFirstName(), "John");
        assertEquals(user.getLastName(), "Doe");
        assertEquals(user.getFullName(), "John Doe");
        assertEquals(user.getRole(), User.Role.Administrator);

        assertFalse(user.equals("salkdjf"));
        assertTrue(user.equals(user));
        assertFalse(user.equals(new User("Bill", "Gates", User.Role.Participant)));
    }
}

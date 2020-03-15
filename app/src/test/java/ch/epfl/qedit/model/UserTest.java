package ch.epfl.qedit.model;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItems;

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

    @Test
    public void quizzesTest() {
        User user = new User("John", "Doe", User.Role.Administrator);
        user.addQuiz("q0", "First Quiz");
        user.addQuiz("q1", "Second Quiz");
        assertThat(user.getQuizzesKey(), hasItems("q0", "q1"));
        assertThat(user.getQuizzesTitle(), hasItems("First Quiz", "Second Quiz"));
    }

    @Test
    public void addQuizzesAddlTest() {
        final User user = new User("John", "Doe", User.Role.Administrator);
        user.addQuiz("q0", "First Quiz");
        assertTrue(user.addQuiz("q0", "First again Quiz"));
        assertFalse(user.addQuiz("q1", "Second Quiz"));
    }

    @Test
    public void removeQuizzesTest() {
        User user = new User("John", "Doe", User.Role.Administrator);
        user.addQuiz("q0", "First Quiz");
        user.addQuiz("q1", "Second Quiz");
        user.removeQuiz("q1");
        assertFalse(user.getQuizzesKey().contains("q1"));
    }
}

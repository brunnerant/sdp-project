package ch.epfl.qedit.model;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class UserTest {
    @Test
    public void dummyTestUser() {
        User user = new User("John", "Doe");
        assertEquals(user.getFirstName(), "John");
        assertEquals(user.getLastName(), "Doe");
        assertEquals(user.getFullName(), "John Doe");
        assertEquals(0, user.getScore());
        assertEquals(0, user.getSuccess());
        assertEquals(0, user.getAttempt());

        //noinspection SpellCheckingInspection
        assertNotEquals("salkdjf", user);
        assertEquals(user, user);
        assertNotEquals(user, new User("Bill", "Gates"));
    }

    @Test
    public void testConstructor() {
        User user = new User("John", "Doe", 456, 7, 1);
        assertEquals(456, user.getScore());
        assertEquals(7, user.getSuccess());
        assertEquals(1, user.getAttempt());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorFail1() {
        User user = new User("John", "Doe", -1, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorFail2() {
        User user = new User("John", "Doe", 0, -78, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorFail3() {
        User user = new User("John", "Doe", 0, 0, -895);
    }

    @Test
    public void testIncrement() {
        User user = new User("John", "Doe");
        user.incrementAttempt();
        user.incrementAttempt();
        user.incrementAttempt();
        assertEquals(3, user.getAttempt());

        user.incrementSuccess();
        user.incrementSuccess();
        assertEquals(2, user.getSuccess());
        assertEquals(5, user.getAttempt());

        user.incrementScore(45);
        assertEquals(45, user.getScore());

        user.decrementScore(5);
        assertEquals(40, user.getScore());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testScoreIncrementFail() {
        User user = new User("John", "Doe");
        user.incrementScore(-45);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testScoreDecrementFail1() {
        User user = new User("John", "Doe", 8, 0, 0);
        user.decrementScore(45);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testScoreDecrementFail2() {
        User user = new User("John", "Doe");
        user.decrementScore(-45);
    }

    @Test
    public void testEquals() {
        assertEquals(new User("D", "J", 0, 0, 0), new User("D", "J"));
        assertNotEquals(new User("D", "J", 0, 1, 0), new User("D", "J"));
        assertNotEquals(new User("D", "J", 0, 0, 1), new User("D", "J"));
    }

    @Test
    public void quizzesTest() {
        User user = new User("John", "Doe");
        user.addQuiz("q0", "First Quiz");
        user.addQuiz("q1", "Second Quiz");
        assertThat(user.getQuizzes().keySet(), hasItems("q0", "q1"));
        assertThat(user.getQuizzes().values(), hasItems("First Quiz", "Second Quiz"));
    }

    @Test
    public void addQuizzesTest() {
        final User user = new User("John", "Doe");
        user.addQuiz("q0", "First Quiz");
        assertTrue(user.addQuiz("q0", "First again Quiz"));
        assertFalse(user.addQuiz("q1", "Second Quiz"));
    }

    @Test
    public void removeQuizzesTest() {
        User user = new User("John", "Doe");
        user.addQuiz("q0", "First Quiz");
        user.addQuiz("q1", "Second Quiz");
        user.removeQuiz("q1");
        assertFalse(user.getQuizzes().containsKey("q1"));
    }
}

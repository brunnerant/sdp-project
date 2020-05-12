package ch.epfl.qedit.model;

import com.google.common.collect.ImmutableMap;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/** Represents a user of the QEDit app. */
public class User implements Serializable {

    /**
     * Table that contains quiz key of all the quizzes this user can attempt to we also store
     * directly the title of the quiz so that we don't have to query it from the database every time
     * we go back to the HomeActivity
     */
    private final HashMap<String, String> quizzes;

    private final String firstName;
    private final String lastName;

    private int score;
    private int success;
    private int attempt;

    public User(String firstName, String lastName) {
        this(firstName, lastName, 0, 0, 0);
    }

    public User(String firstName, String lastName, int score, int success, int attempt) {
        if (score < 0) throw new IllegalArgumentException("User score has to be positive");
        if (success < 0) throw new IllegalArgumentException("User success has to be positive");
        if (attempt < 0) throw new IllegalArgumentException("User attempt has to be positive");

        this.firstName = firstName;
        this.lastName = lastName;
        this.quizzes = new LinkedHashMap<>();
        this.score = score;
        this.success = success;
        this.attempt = attempt;
    }

    public boolean canAdd(String string) {
        return !quizzes.containsValue(string);
    }

    /**
     * add a quiz to the user's quizzes list and return true if this quiz is replacing an other,
     * i.e. if the key already is in the quizzes map
     */
    public boolean addQuiz(String key, String title) {
        return quizzes.put(key, title) != null;
    }

    public void removeQuizOnValue(String value) {
        // Using iterator to avoid ConcurrentModificationException
        Iterator<Map.Entry<String, String>> it = quizzes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            if (entry.getValue().equals(value)) {
                it.remove();
            }
        }
    }

    public void updateQuizOnValue(String oldValue, String newValue) {
        String value = newValue.trim();

        for (Map.Entry<String, String> entry : quizzes.entrySet()) {
            if (entry.getValue().equals(oldValue)) {
                quizzes.put(entry.getKey(), value);
            }
        }
    }

    public void removeQuiz(String key) {
        quizzes.remove(key);
    }

    public ImmutableMap<String, String> getQuizzes() {
        return ImmutableMap.copyOf(quizzes);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public int getScore() {
        return score;
    }

    public int getSuccess() {
        return success;
    }

    public int getAttempt() {
        return attempt;
    }

    public void incrementScore(int points) {
        if (points < 0)
            throw new IllegalArgumentException("Cannot increment score with negative points");
        score += points;
    }

    public void incrementSuccess() {
        ++success;
    }

    public void incrementAttempt() {
        ++attempt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        boolean equalStatistics =
                this.score == user.score
                        && this.success == user.success
                        && this.attempt == user.attempt;
        return firstName.equals(user.firstName)
                && lastName.equals(user.lastName)
                && equalStatistics;
    }
}

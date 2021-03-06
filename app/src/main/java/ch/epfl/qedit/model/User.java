package ch.epfl.qedit.model;

import com.google.common.collect.ImmutableMap;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/** Represents a user of the QEDit app. */
public class User implements Serializable {

    /**
     * Table that contains quiz key of all the quizzes this user can attempt to we also store
     * directly the title of the quiz so that we don't have to query it from the database every time
     * we go back to the HomeActivity
     */
    private Map<String, String> quizzes;

    private String firstName;
    private String lastName;

    private int score;
    private int successes;
    private int attempts;

    /** Useful constructor to get directly a user from firestore */
    public User() {}

    public User(String firstName, String lastName) {
        this(firstName, lastName, 0, 0, 0);
    }

    public User(String firstName, String lastName, int score, int successes, int attempts) {
        this(firstName, lastName, new LinkedHashMap<>(), score, successes, attempts);
    }

    public User(
            String firstName,
            String lastName,
            Map<String, String> quizzes,
            int score,
            int successes,
            int attempts) {
        if (score < 0) throw new IllegalArgumentException("User score has to be positive");
        if (successes < 0) throw new IllegalArgumentException("User success has to be positive");
        if (attempts < 0) throw new IllegalArgumentException("User attempt has to be positive");

        this.firstName = firstName;
        this.lastName = lastName;
        this.quizzes = Objects.requireNonNull(quizzes);
        this.score = score;
        this.successes = successes;
        this.attempts = attempts;
    }

    /**
     * Adds a quiz to the user's quizzes list and return true if this quiz is replacing an other,
     * i.e. if the key already is in the quizzes map
     */
    public boolean addQuiz(String key, String title) {
        return quizzes.put(key, title) != null;
    }

    /** Updates the title of the quiz with the given ID. */
    public void updateQuizTitle(String id, String newTitle) {
        quizzes.put(id, newTitle);
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

    public int getSuccesses() {
        return successes;
    }

    public int getAttempts() {
        return attempts;
    }

    public void incrementScore(int points) {
        if (points < 0)
            throw new IllegalArgumentException(
                    "Cannot increment score with negative points: use decrementScore instead");
        score += points;
    }

    public void decrementScore(int points) {
        if (points < 0)
            throw new IllegalArgumentException(
                    "Cannot decrement score with negative points: use incrementScore instead");
        if (score - points < 0)
            throw new IllegalArgumentException("User cannot have a negative score");
        score -= points;
    }

    public void incrementSuccess() {
        ++successes;
        ++attempts;
    }

    public void incrementAttempt() {
        ++attempts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        boolean equalStatistics =
                this.score == user.score
                        && this.successes == user.successes
                        && this.attempts == user.attempts;
        return firstName.equals(user.firstName)
                && lastName.equals(user.lastName)
                && equalStatistics;
    }
}

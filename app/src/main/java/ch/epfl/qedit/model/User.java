package ch.epfl.qedit.model;

import com.google.common.collect.ImmutableMap;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/** Represents a user of the QEDit app. */
public class User implements Serializable {
    public enum Role {
        Participant,
        Editor,
        Administrator
    }

    /**
     * Table that contains quiz key of all the quizzes this user can attempt to we also store
     * directly the title of the quiz so that we don't have to query it from the database every time
     * we go back to the HomeActivity
     */
    private final HashMap<String, String> quizzes;

    private final String firstName;
    private final String lastName;
    private final Role role;

    public User(String firstName, String lastName, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.quizzes = new LinkedHashMap<>();
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
        System.err.println("===========================================================");
        for (String name : quizzes.keySet()) {
            String key2 = name.toString();
            String value = quizzes.get(name).toString();
            System.out.println(key2 + " " + value);
        }

        quizzes.remove(key);
        for (String name : quizzes.keySet()) {
            String key2 = name.toString();
            String value = quizzes.get(name).toString();
            System.out.println(key2 + " " + value);
        }
        System.err.println("===========================================================");
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

    public Role getRole() {
        return role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return firstName.equals(user.firstName)
                && lastName.equals(user.lastName)
                && role == user.role;
    }
}

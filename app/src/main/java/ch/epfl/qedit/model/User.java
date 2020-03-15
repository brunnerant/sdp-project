package ch.epfl.qedit.model;

import java.io.Serializable;
import java.util.ArrayList;

/** Represents a user of the QEDit app. */
public class User implements Serializable {
    public enum Role {
        Participant,
        Editor,
        Administrator
    }

    public ArrayList<String> quizzes;

    private final String firstName;
    private final String lastName;
    private final Role role;

    public User(String firstName, String lastName, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.quizzes = new ArrayList<String>();
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

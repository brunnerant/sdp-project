package ch.epfl.qedit.model.users;

import ch.epfl.qedit.model.Quiz;

public class Corrector extends User {
    public Corrector(String firstName, String lastName, int userId, String language) {
        super(firstName, lastName, userId, language);
    }
    // returns the score of the quiz
    public int correctQuiz(Quiz quiz) {
        return 0;
    }
}

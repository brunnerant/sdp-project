package ch.epfl.qedit.model.users;

import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.QuizCategorie;

public class Editor extends User {

    public Editor(String firstName, String lastName, int userId, String language) {
        super(firstName, lastName, userId, language);
    }

    //should return a new empty quiz
    public Quiz createQuiz(int nbOfQuestions, QuizCategorie categorie){
        return new Quiz(nbOfQuestions,categorie);
    }
    //allows the editor to edit a quiz
    public Quiz editQuiz(Quiz quiz){
        quiz.edit();
        return quiz;
    }
}

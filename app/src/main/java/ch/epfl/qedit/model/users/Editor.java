package ch.epfl.qedit.model.users;

import java.util.List;

import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;

public class Editor extends User {

    public Editor(String firstName, String lastName, int userId, String language) {
        super(firstName, lastName, userId, language);
    }

    //should return a new empty quiz
    public Quiz createQuiz(List<Question> questions){
        return new Quiz(questions);
    }
    //allows the editor to edit a quiz
    public Quiz editQuiz(Quiz quiz){
        quiz.edit();
        return quiz;
    }
}

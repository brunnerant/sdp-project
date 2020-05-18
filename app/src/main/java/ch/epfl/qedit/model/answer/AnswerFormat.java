package ch.epfl.qedit.model.answer;

import ch.epfl.qedit.model.MultiLanguage;
import ch.epfl.qedit.util.Mappable;
import ch.epfl.qedit.view.answer.AnswerFragment;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * This class represents all the answer formats that are available in the app. The purpose of this
 * class is merely to represents how the answer fields look like, and not to store what the user
 * entered. To store what the user entered, see AnswerModel.
 */
public abstract class AnswerFormat implements MultiLanguage<AnswerFormat>, Serializable, Mappable {

    public static final String TO_MAP_TYPE = "type";
    /**
     * This is the title of the answer, to help the user know what he needs to fill. It can be null
     * if it is not needed.
     */
    private String text;

    /** The correct answer */
    private AnswerModel solution;

    public void setCorrectAnswer(AnswerModel correctAnswer) {
        this.solution = correctAnswer;
    }

    public boolean correct(AnswerModel participantAnswer) {
        if (participantAnswer == null || solution == null) return false;
        return solution.equals(participantAnswer);
    }
    // Package-private constructor, to be used by subclasses
    AnswerFormat(String text) {
        this.text = text;
    }

    /** Returns the text of the answer */
    public String getText() {
        return text;
    }

    /**
     * This function should return an empty answer model corresponding to its type, so that answers
     * can be stored for this answer format.
     *
     * @return the corresponding answer model, empty
     */
    public abstract AnswerModel getEmptyAnswerModel();

    /**
     * This function should return the android fragment corresponding to its type, so that the quiz
     * activity can dynamically instantiate the corresponding user interface.
     *
     * @return the corresponding answer fragment
     */
    public abstract AnswerFragment getAnswerFragment();

    @Override
    public boolean equals(Object o) {
        if (o instanceof AnswerFormat) {
            AnswerFormat other = (AnswerFormat) o;
            return Objects.equals(this.text, other.text);
        }
        return false;
    }

    @Override
    public Map<String, Object> toMap() {
        return solution.toMap();
    }
}

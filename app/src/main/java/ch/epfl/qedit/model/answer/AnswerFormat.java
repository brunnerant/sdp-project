package ch.epfl.qedit.model.answer;

import ch.epfl.qedit.model.MultiLanguage;
import ch.epfl.qedit.view.answer.AnswerFragment;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/**
 * This class represents all the answer formats that are available in the app. The purpose of this
 * class is merely to represents how the answer fields look like, and not to store what the user
 * entered. To store what the user entered, see AnswerModel.
 */
public abstract class AnswerFormat implements MultiLanguage<AnswerFormat>, Serializable {

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

    /** Parses a simple answer format, that is not recursive */
    private static AnswerFormat parseSimpleFormat(String field) {

        // Split <format>:<text> into two string
        String[] formatAndText = field.split(":", 2);
        String format = formatAndText[0];
        String text = (formatAndText.length == 2) ? formatAndText[1].trim() : null;

        return MatrixFormat.parse(format, text);
    }

    /**
     * Parse AnswerFormat from a string answerFormat, call the override parse method in child
     * classes
     *
     * @param answerFormat string to parse into a AnswerFormat
     * @return AnswerFormat corresponding to the parsed format string, or null if the string is not
     *     a correct format
     */
    public static AnswerFormat parse(String answerFormat) {
        if (answerFormat == null) {
            return null;
        }
        // Parse answer format:  <format>:<text> ; <format> ; ... ; <format>:<text>
        ArrayList<AnswerFormat> fields = new ArrayList<>();
        for (String field : answerFormat.split(";")) {
            // <format>:<text> | <format>
            AnswerFormat result = parseSimpleFormat(field);
            if (result == null) {
                return null;
            }
            fields.add(result);
        }

        return (fields.size() == 1) ? fields.get(0) : new MultiFieldFormat(fields);
    }
}

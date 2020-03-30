package ch.epfl.qedit.model.answer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/** This class represents all the answer formats that are available in the app. */
public abstract class AnswerFormat implements Serializable {

    // Can be null
    private String text;

    // Package Private
    AnswerFormat(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AnswerFormat) {
            AnswerFormat other = (AnswerFormat) o;
            return Objects.equals(this.text, other.text);
        }
        return false;
    }

    /** This method is used to implement the visitor pattern */
    public abstract void accept(Visitor visitor);

    public interface Visitor {

        void visitMatrixAnswerFormat(MatrixFormat matrixFormat);

        void visitMutliFieldFormat(MultiFieldFormat multiFieldFormat);
    }

    private static AnswerFormat parseNotCompoundFormat(String format, String text) {
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
        ArrayList<AnswerFormat> formatsList = new ArrayList<>();
        for (String format : answerFormat.split(";")) {
            // <format>:<text> | <format>
            String[] formatText = format.split(":", 2);
            String text = (formatText.length == 2) ? formatText[1].trim() : null;
            AnswerFormat result = parseNotCompoundFormat(formatText[0], text);
            if (result == null) {
                return null;
            }
            formatsList.add(result);
        }

        return (formatsList.size() == 1) ? formatsList.get(0) : new MultiFieldFormat(formatsList);
    }
}

package ch.epfl.qedit.model;

import java.io.Serializable;

/** Represents the question of a quiz. For now, it is simply represented as a string. */
public class Question implements Serializable {
    /** For now, a question consists of a number, a title, and a text */
    private final int index;
    private final int numberOfAnswers;

    private final String title;
    private final String text;

    /** The answer format for this question */
    private final AnswerFormat format;

    public Question(int index, String title, String text, AnswerFormat format, int numberOfAnswers) {
        if (index < 0 || title == null || text == null || format == null)
            throw new IllegalArgumentException();

        this.index = index;
        this.title = title;
        this.text = text;
        this.format = format;
        this.numberOfAnswers = numberOfAnswers;
    }

    public int getIndex() {
        return index;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public AnswerFormat getFormat() {
        return format;
    }
}

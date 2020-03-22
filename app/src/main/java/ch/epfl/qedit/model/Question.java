package ch.epfl.qedit.model;

import ch.epfl.qedit.util.Bundleable;
import ch.epfl.qedit.util.BundledData;
import java.util.Objects;

/** Represents the question of a quiz. For now, it is simply represented as a string. */
public class Question implements Bundleable {
    /** For now, a question consists of a number, a title, and a text */
    private final String title;

    private final String text;

    /** The answer format for this question */
    private final AnswerFormat format;

    public Question(String title, String text, AnswerFormat format) {
        this.title = Objects.requireNonNull(title);
        this.text = Objects.requireNonNull(text);
        this.format = Objects.requireNonNull(format);
    }

    /** Construct a question with a string to parse for the answer_format */
    public Question(String title, String text, String answer_format) {
        this.title = Objects.requireNonNull(title);
        this.text = Objects.requireNonNull(text);
        this.format = Objects.requireNonNull(AnswerFormat.parse(answer_format));
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

    @Override
    public BundledData toBundle() {
        return new BundledData().update("title", title).update("text", text);
    }

    public static Question fromBundle(BundledData bundle) throws IllegalArgumentException {
        String title = (String) bundle.get("title");
        String text = (String) bundle.get("text");
        return new Question(title, text, new MatrixFormat(1, 1));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Question) {
            Question other = (Question) o;
            return this.title.equals(other.title)
                    && this.text.equals(other.text)
                    && this.format.equals(other.format);
        }
        return false;
    }
}

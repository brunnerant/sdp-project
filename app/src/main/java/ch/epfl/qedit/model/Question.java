package ch.epfl.qedit.model;

import ch.epfl.qedit.util.Bundlable;
import ch.epfl.qedit.util.Bundle;
import java.io.Serializable;
import java.util.Objects;

/** Represents the question of a quiz. For now, it is simply represented as a string. */
public class Question implements Bundlable, Serializable {
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
    public Bundle toBundle() {
        return new Bundle().update("title", title).update("text", text);
    }

    public static Question fromBundle(Bundle bundle) throws IllegalArgumentException {
        String title = (String) bundle.get("title");
        String text = (String) bundle.get("text");
        return new Question(title, text, new AnswerFormat.NumberField(0, 100, 0));
    }
}

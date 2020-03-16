package ch.epfl.qedit.model;

import java.io.Serializable;
import java.util.Objects;

import ch.epfl.qedit.util.Bundlable;
import ch.epfl.qedit.util.Bundle;

/** Represents the question of a quiz. For now, it is simply represented as a string. */
public class Question implements Bundlable, Serializable {
    /** For now, a question consists of a number, a title, and a text */
    private String title;
    private String text;

    private int index;
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
        return new Bundle()
            .update("title", title)
            .update("text", text);
    }

    @Override
    public void fromBundle(Bundle bundle) throws IllegalArgumentException {
        this.title = (String) bundle.get("title");
        this.text = (String) bundle.get("text");
    }

}

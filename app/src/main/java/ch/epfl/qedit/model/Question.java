package ch.epfl.qedit.model;

import ch.epfl.qedit.util.Bundlable;
import ch.epfl.qedit.util.Bundle;

/** Represents the question of a quiz. For now, it is simply represented as a string. */
public class Question implements Bundlable {
    /** For now, a question consists of a number, a title, and a text */
    private String title;
    private String text;

    /** The answer format for this question */
    private final AnswerFormat format;

    public Question(int index, String title, String text, AnswerFormat format) {
        if (index < 0 || title == null || text == null || format == null)
            throw new IllegalArgumentException();

        this.title = title;
        this.text = text;
        this.format = format;
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
        String title = (String) bundle.get("title");
        String text = (String) bundle.get("text");

        if (title == null || text == null) {
            throw new IllegalArgumentException();
        }

        this.title = title;
        this.text = text;
    }
}

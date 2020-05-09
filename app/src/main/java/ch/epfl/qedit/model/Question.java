package ch.epfl.qedit.model;

import android.location.Location;

import java.io.Serializable;
import java.util.Objects;

import ch.epfl.qedit.model.answer.AnswerFormat;

/** Represents the question of a quiz. For now, it is simply represented as a string. */
public final class Question implements MultiLanguage<Question>, Serializable {
    /** A question consists of a title, and a text */
    private final String title;

    private final String text;

    /**
     * Treasure hunt questions also have a location and a radius within which they can be answered.
     */
    private final Location location;

    private final int radius;

    /** The answer format for this question */
    private final AnswerFormat format;

    // This constructor is for treasure hunt questions
    public Question(String title, String text, AnswerFormat format, Location location, int radius) {
        this.title = Objects.requireNonNull(title);
        this.text = Objects.requireNonNull(text);
        this.format = Objects.requireNonNull(format);
        this.location = Objects.requireNonNull(location);
        this.radius = radius;

        if (radius <= 0)
            throw new IllegalArgumentException("Treasure hunt radius has to be positive");
    }

    // This constructor is for normal questions
    public Question(String title, String text, AnswerFormat format) {
        this.title = Objects.requireNonNull(title);
        this.text = Objects.requireNonNull(text);
        this.format = Objects.requireNonNull(format);
        this.location = null;
        this.radius = -1;
    }

    /** Construct a question with a string to parse for the answer_format */
    public Question(String title, String text, String answerFormat) {
        this(title, text, AnswerFormat.parse(answerFormat));
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

    public Location getLocation() {
        return location;
    }

    public int getRadius() {
        return radius;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Question) {
            Question other = (Question) o;
            return this.title.equals(other.title)
                    && this.text.equals(other.text)
                    && this.format.equals(other.format)
                    && Objects.equals(this.location, other.location)
                    && this.radius == other.radius;
        }
        return false;
    }

    @Override
    public Question instantiateLanguage(StringPool pool) {
        String newTitle = pool.get(title);
        String newText = pool.get(text);
        AnswerFormat newFormat = format.instantiateLanguage(pool);

        return new Question(newTitle, newText, newFormat);
    }
}

package ch.epfl.qedit.model;

import ch.epfl.qedit.model.answer.AnswerFormat;
import java.io.Serializable;
import java.util.Objects;

/** Represents the question of a quiz. For now, it is simply represented as a string. */
public final class Question implements MultiLanguage<Question>, Serializable {
    /** A question consists of a title, and a text */
    private final String title;

    private final String text;

    /**
     * Treasure hunt questions also have a location and a radius within which they can be answered.
     * android.Location is not serializable, so we store the longitude and latitude instead.
     */
    private final double longitude;

    private final double latitude;
    private final double radius;

    /** The answer format for this question */
    private final AnswerFormat format;

    // This constructor is for treasure hunt questions
    public Question(
            String title,
            String text,
            AnswerFormat format,
            double longitude,
            double latitude,
            double radius) {
        this.title = Objects.requireNonNull(title);
        this.text = Objects.requireNonNull(text);
        this.format = Objects.requireNonNull(format);

        this.longitude = longitude;
        this.latitude = latitude;
        this.radius = radius;

        if (radius <= 0)
            throw new IllegalArgumentException("Treasure hunt radius has to be positive");
    }

    // This constructor is for normal questions
    public Question(String title, String text, AnswerFormat format) {
        this.title = Objects.requireNonNull(title);
        this.text = Objects.requireNonNull(text);
        this.format = Objects.requireNonNull(format);

        // We store some dummy values, since nobody should try to access them anyway
        this.longitude = 0;
        this.latitude = 0;
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

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Question) {
            Question other = (Question) o;
            return this.title.equals(other.title)
                    && this.text.equals(other.text)
                    && this.format.equals(other.format)
                    && this.longitude == other.longitude
                    && this.latitude == other.latitude
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

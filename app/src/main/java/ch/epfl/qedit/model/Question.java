package ch.epfl.qedit.model;

import android.location.Location;
import ch.epfl.qedit.model.answer.AnswerFormat;
import ch.epfl.qedit.util.Mappable;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/** Represents the question of a quiz. For now, it is simply represented as a string. */
public final class Question implements MultiLanguage<Question>, Serializable, Mappable {

    public static final String TO_MAP_TITLE = "title";
    public static final String TO_MAP_TEXT = "text";
    public static final String TO_MAP_RADIUS = "radius";
    public static final String TO_MAP_LOCATION = "location";
    public static final String TO_MAP_ANSWERS = "answers";

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

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(TO_MAP_TITLE, title);
        map.put(TO_MAP_TEXT, text);
        if (radius != -1 && location != null) {
            map.put(TO_MAP_RADIUS, radius);
            map.put(TO_MAP_LOCATION, location);
        }
        map.put(TO_MAP_ANSWERS, Arrays.asList(format.toMap()));
        return map;
    }
}

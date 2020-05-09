package ch.epfl.qedit.model;

import static ch.epfl.qedit.model.StringPool.NO_QUESTION_TEXT_ID;
import static ch.epfl.qedit.model.StringPool.NO_QUESTION_TITLE_ID;

import ch.epfl.qedit.search.Searchable;
import ch.epfl.qedit.model.answer.AnswerFormat;
import ch.epfl.qedit.model.answer.EmptyAnswerFormat;
import java.io.Serializable;
import java.util.Objects;

/** Represents the question of a quiz. For now, it is simply represented as a string. */
public class Question implements MultiLanguage<Question>, Searchable<Question>, Serializable {
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
    public boolean equals(Object o) {
        if (o instanceof Question) {
            Question other = (Question) o;
            return this.title.equals(other.title)
                    && this.text.equals(other.text)
                    && this.format.equals(other.format);
        }
        return false;
    }

    @Override
    public Question search(String string, int position) {
        return null;
    }

    public Question instantiateLanguage(StringPool pool) {
        String newTitle = pool.get(title);
        String newText = pool.get(text);
        AnswerFormat newFormat = format.instantiateLanguage(pool);

        return new Question(newTitle, newText, newFormat);
    }

    public boolean isEmpty() {
        return false;
    }

    public static class Empty extends Question {
        public Empty() {
            super(NO_QUESTION_TITLE_ID, NO_QUESTION_TEXT_ID, new EmptyAnswerFormat(null));
        }

        @Override
        public boolean isEmpty() {
            return true;
        }
    }
}

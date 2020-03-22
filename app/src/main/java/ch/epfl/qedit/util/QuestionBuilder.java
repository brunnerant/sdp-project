package ch.epfl.qedit.util;

import ch.epfl.qedit.model.AnswerFormat;
import ch.epfl.qedit.model.Question;

public class QuestionBuilder {

    private String title;
    private String text;
    private AnswerFormat format;

    public QuestionBuilder() {
        this.title = "Untitle Quiz";
        this.text = null;
        this.format = null;
    }

    public QuestionBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public QuestionBuilder setText(String text) {
        this.text = text;
        return this;
    }

    public QuestionBuilder setFormat(String title) {
        this.format = format.clone();
        return this;
    }

    public Question build() {
        if (title == null || text == null || format == null) throw new IllegalStateException();
        Question q = new Question(title, text, format);
        title = null;
        text = null;
        format = null;
        return q;
    }
}

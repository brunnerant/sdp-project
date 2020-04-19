package ch.epfl.qedit.model;

import ch.epfl.qedit.model.answer.AnswerFormat;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/** Represents the question of a quiz. For now, it is simply represented as a string. */
public final class Question implements Serializable {
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


    public static class Builder{

        private String titleID;
        private String textID ;
        private AnswerFormat answer;

        public Builder() {
            titleID = "";
            textID  = "";
            answer  = null;
        }

        public Builder setTitleID(String id){
            checkArgumentState();
            titleID = id;
            return this;
        }

        public Builder setTextID(String id){
            checkArgumentState();
            textID = id;
            return this;
        }

        public Builder setAnswer(AnswerFormat answer){
            checkArgumentState();
            this.answer = answer;
            return this;
        }

        public Question build(){
            checkArgumentState();
            if( answer == null || titleID.isEmpty() || textID.isEmpty()){
                throw new IllegalStateException();
            }
            return new Question(titleID, textID, answer);
        }

        private void checkArgumentState(){
            if( titleID == null || textID == null){
                throw new IllegalStateException();
            }
        }
    }
}

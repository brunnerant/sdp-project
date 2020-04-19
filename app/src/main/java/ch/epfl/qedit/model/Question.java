package ch.epfl.qedit.model;

import ch.epfl.qedit.model.answer.AnswerFormat;
import java.io.Serializable;
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
        private AnswerFormat format;

        public Builder() {
            titleID = "";
            textID  = "";
            format = null;
        }

        public Builder(Question question){
            titleID = question.title;
            textID  = question.text;
            format = question.format;
        }

        public Builder setTitleID(String id){
            checkState();
            titleID = id;
            return this;
        }

        public Builder setTextID(String id){
            checkState();
            textID = id;
            return this;
        }

        public Builder setFormat(AnswerFormat format){
            checkState();
            this.format = format;
            return this;
        }

        public Question build(){
            checkState();
            checkAttributesValidity();

            String resultTitleID = titleID;
            titleID = null;
            return new Question(resultTitleID, textID, format);
        }

        private void checkAttributesValidity(){
            String strError = "";
            if(format == null){
                strError += "AnswerFormat not specified i.e. answer format is null.";
            }
            if(titleID == null || titleID.isEmpty()){
                strError += " ID of title not specified.";
            }
            if(textID == null || textID.isEmpty()){
                strError += " ID of text not specified.";
            }
            if(!strError.isEmpty()){
                throw new IllegalStateException(strError);
            }

        }

        private void checkState(){
            if(titleID == null){
                throw new IllegalStateException("Builder already build once.");
            }
        }
    }
}

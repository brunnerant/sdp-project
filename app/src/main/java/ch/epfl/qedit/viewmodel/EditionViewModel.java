package ch.epfl.qedit.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.StringPool;

public class EditionViewModel extends ViewModel {

    private final MutableLiveData<Integer> focusedQuestion = new MutableLiveData<>(null);
    private StringPool stringPool;
    private Quiz.Builder quizBuilder;

    public Quiz.Builder getQuizBuilder() {
        return quizBuilder;
    }

    public void setQuizBuilder(Quiz.Builder quizBuilder) {
        this.quizBuilder = quizBuilder;
    }

    public StringPool getStringPool() {
        return stringPool;
    }

    public void setStringPool(StringPool stringPool) {
        this.stringPool = stringPool;
    }

    public MutableLiveData<Integer> getFocusedQuestion() {
        return focusedQuestion;
    }
}

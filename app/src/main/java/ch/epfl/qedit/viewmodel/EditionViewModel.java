package ch.epfl.qedit.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.StringPool;

/**
 * This ViewModel will be created by the EditQuizActivity and will be used to share state between
 * the two fragments EditOverviewFragment and EditPreviewFragment which are shown simultaneously to
 * the user
 */
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

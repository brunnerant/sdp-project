package ch.epfl.qedit.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import ch.epfl.qedit.model.Quiz;

public class QuizViewModel extends ViewModel {

    private Quiz quiz = null;

    public void setQuiz(Quiz quiz) {
        if (this.quiz == null) {
            this.quiz = quiz;
        }
    }

    public Quiz getQuiz() {
        return quiz;
    }

    private final MutableLiveData<Integer> focusedQuestion = new MutableLiveData<>(null);

    public MutableLiveData<Integer> getFocusedQuestion() {
        return focusedQuestion;
    }
}

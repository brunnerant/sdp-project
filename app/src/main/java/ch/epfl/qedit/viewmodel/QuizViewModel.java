package ch.epfl.qedit.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.answer.AnswerModel;
import java.util.HashMap;

public class QuizViewModel extends ViewModel {
    private final MutableLiveData<Integer> focusedQuestion = new MutableLiveData<>(null);
    private final MutableLiveData<HashMap<Integer, AnswerModel>> answers =
            new MutableLiveData<>(new HashMap<>());
    private Quiz quiz = null;

    public void setQuiz(Quiz quiz) {
        if (this.quiz == null) {
            this.quiz = quiz;
        }
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public MutableLiveData<Integer> getFocusedQuestion() {
        return focusedQuestion;
    }

    public MutableLiveData<HashMap<Integer, AnswerModel>> getAnswers() {
        return answers;
    }
}

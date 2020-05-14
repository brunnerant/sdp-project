package ch.epfl.qedit.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.model.answer.AnswerModel;
import java.util.HashMap;

public class QuizViewModel extends ViewModel {
    private final MutableLiveData<Integer> focusedQuestion = new MutableLiveData<>(null);
    private final MutableLiveData<HashMap<Integer, AnswerModel>> answers =
            new MutableLiveData<>(new HashMap<>());

    private Quiz quiz = null;
    private StringPool stringPool = null;

    public void initialize(Quiz quiz, StringPool stringPool) {
        if (this.quiz == null && this.stringPool == null) {
            this.quiz = quiz;
            this.stringPool = stringPool;
        }
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public StringPool getStringPool() {
        return stringPool;
    }

    public MutableLiveData<Integer> getFocusedQuestion() {
        return focusedQuestion;
    }

    public MutableLiveData<HashMap<Integer, AnswerModel>> getAnswers() {
        return answers;
    }
}

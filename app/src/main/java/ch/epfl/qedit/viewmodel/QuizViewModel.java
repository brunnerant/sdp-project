package ch.epfl.qedit.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import ch.epfl.qedit.model.AnswerModel;
import ch.epfl.qedit.model.Quiz;
import java.util.HashMap;

public class QuizViewModel extends ViewModel {
    private final MutableLiveData<Integer> focusedQuestion = new MutableLiveData<>(null);
    private final MutableLiveData<HashMap<Integer, AnswerModel>> Answers =
            new MutableLiveData<>(new HashMap<Integer, AnswerModel>());
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
        return Answers;
    }

    /*private final MutableLiveData<HashMap<Integer, HashMap<Integer, Float>>> Answers =
            new MutableLiveData<>(new HashMap<Integer, HashMap<Integer, Float>>()); //TODO

    public void initializeAnswersMap() {
        for (int i = 0; i < quiz.getValue().getQuestions().size(); i++) {
            Answers.getValue().put(i, new HashMap<Integer, Float>());
        }
    }

    public MutableLiveData<HashMap<Integer, HashMap<Integer, Float>>> getAnswers() {
        return Answers;
    }*/
}

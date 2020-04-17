package ch.epfl.qedit.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;

import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;

public class EditionViewModel extends ViewModel {

    private final MutableLiveData<Integer> focusedQuestion = new MutableLiveData<>(null);
    private Quiz.Builder quizBuilder = new Quiz.Builder();
    private Question.Builder questionBuilder = null;
    private ImmutableMap.Builder<String, String> stringPoolBuilder = new ImmutableMap.Builder<>();

    public Quiz.Builder getQuizBuilder() {
        return quizBuilder;
    }

    public Question.Builder getQuestionBuilder() {
        return questionBuilder;
    }

    public void initQuestionBuilder(){
        questionBuilder = new Question.Builder();
    }

    public MutableLiveData<Integer> getFocusedQuestion() {
        return focusedQuestion;
    }
}

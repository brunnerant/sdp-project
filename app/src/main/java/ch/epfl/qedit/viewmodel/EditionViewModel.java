package ch.epfl.qedit.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.UUID;

import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.StringPool;

public class EditionViewModel extends ViewModel {

    private final MutableLiveData<Integer> focusedQuestion = new MutableLiveData<>(null);
    private Quiz.Builder quizBuilder = new Quiz.Builder();
    private Question.Builder questionBuilder = null;
    private StringPool strPool = new StringPool();


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

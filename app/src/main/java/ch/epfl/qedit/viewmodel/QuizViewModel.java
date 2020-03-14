package ch.epfl.qedit.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import ch.epfl.qedit.backend.database.DatabaseFactory;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.util.Bundle;
import ch.epfl.qedit.util.Callback;
import ch.epfl.qedit.util.Response;

public class QuizViewModel extends ViewModel {
    public enum Status {
        NotLoaded,
        Loading,
        Loaded,
        CouldntLoad
    }

    private final MutableLiveData<Status> status = new MutableLiveData<>(Status.NotLoaded);
    private final MutableLiveData<Quiz> quiz = new MutableLiveData<>(null);
    private final MutableLiveData<Integer> focusedQuestion = new MutableLiveData<>(null);

    public void loadQuiz() {
        if (status.getValue() == Status.NotLoaded) {
            status.setValue(Status.Loading);
            DatabaseFactory.getInstance()
                    .getBundle(
                            "quizzes",
                            "quiz1",
                            new Callback<Response<Bundle>>() {
                                @Override
                                public void onReceive(Response<Bundle> response) {
                                    if (response.successful()) {
                                        quiz.postValue(Quiz.fromBundle(response.getData()));
                                        status.postValue(Status.Loaded);
                                    } else {
                                        status.postValue(Status.CouldntLoad);
                                    }
                                }
                            });
        }
    }

    public LiveData<Status> getStatus() {
        return status;
    }

    public LiveData<Quiz> getQuiz() {
        return quiz;
    }

    public MutableLiveData<Integer> getFocusedQuestion() {
        return focusedQuestion;
    }
}

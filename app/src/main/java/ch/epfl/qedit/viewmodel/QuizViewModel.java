package ch.epfl.qedit.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import ch.epfl.qedit.backend.database.DatabaseFactory;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.util.BundledData;
import ch.epfl.qedit.util.Callback;
import ch.epfl.qedit.util.Response;

public class QuizViewModel extends ViewModel {
    public enum Status {
        NotLoaded,
        Loading,
        Loaded,
        CouldNotLoad
    }

    private final MutableLiveData<Status> status = new MutableLiveData<>(Status.NotLoaded);
    private final MutableLiveData<Quiz> quiz = new MutableLiveData<>(null);
    private final MutableLiveData<Integer> focusedQuestion = new MutableLiveData<>(null);

    public void loadQuiz(String quizID) {
        if (status.getValue() == Status.NotLoaded) {
            status.postValue(Status.Loading);
            DatabaseFactory.getInstance()
                    .getBundle(
                            "quizzes",
                            quizID,
                            new Callback<Response<BundledData>>() {
                                @Override
                                public void onReceive(Response<BundledData> response) {
                                    if (response.successful()) {
                                        quiz.postValue(Quiz.fromBundle(response.getData()));
                                        status.postValue(Status.Loaded);
                                    } else {
                                        status.postValue(Status.CouldNotLoad);
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

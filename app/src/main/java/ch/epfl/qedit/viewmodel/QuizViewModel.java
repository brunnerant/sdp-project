package ch.epfl.qedit.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import ch.epfl.qedit.backend.database.DatabaseFactory;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.util.BundledData;
import ch.epfl.qedit.util.Callback;
import ch.epfl.qedit.util.Response;
import java.util.HashMap;

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
    private HashMap<Integer, HashMap<Integer, Float>> questionsAnswers = new HashMap<>();
    private final MutableLiveData<HashMap<Integer, HashMap<Integer, Float>>> Answers =
            new MutableLiveData<>(null);

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

                                        // when Quiz is loaded, create space in Hashmap to store
                                        // answers
                                        for (int i = 0;
                                                i < quiz.getValue().getQuestions().size();
                                                i++) {
                                            questionsAnswers.put(i, new HashMap<Integer, Float>());
                                        }
                                        Answers.postValue(questionsAnswers);
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

    public MutableLiveData<HashMap<Integer, HashMap<Integer, Float>>> getAnswers() {
        return Answers;
    }
}

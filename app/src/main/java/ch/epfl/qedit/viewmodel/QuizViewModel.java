package ch.epfl.qedit.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class QuizViewModel extends ViewModel { //TODO put quiz here
    private final MutableLiveData<Integer> focusedQuestion = new MutableLiveData<>(null);

    public MutableLiveData<Integer> getFocusedQuestion() {
        return focusedQuestion;
    }
}

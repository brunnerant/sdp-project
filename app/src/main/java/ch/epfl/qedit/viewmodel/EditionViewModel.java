package ch.epfl.qedit.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EditionViewModel extends ViewModel {

    private final MutableLiveData<Integer> focusedQuestion = new MutableLiveData<>(null);
    public MutableLiveData<Integer> getFocusedQuestion() {
        return focusedQuestion;
    }
}
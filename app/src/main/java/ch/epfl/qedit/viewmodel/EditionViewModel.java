package ch.epfl.qedit.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import ch.epfl.qedit.model.Question;
import com.google.common.collect.ImmutableList;
import java.util.List;

public class EditionViewModel extends ViewModel {

    private final MutableLiveData<Integer> focusedQuestion = new MutableLiveData<>(null);
    private List<Question> overviewList;

    public void initializeOverviewList(ImmutableList<Question> questions) {
        overviewList = questions.asList();
    }

    public void addEmptyQuestion() {
        overviewList.add(null);
    }

    public void addFilledOutQuestion(int position, Question question) {
        if (position >= 0 && position < overviewList.size() && overviewList.get(position) == null) {
            overviewList.add(position, question);
        }
    }

    public void removeQuestion(int position) {
        if (position >= 0 && position < overviewList.size()) {
            overviewList.remove(position);
        }
    }

    public MutableLiveData<Integer> getFocusedQuestion() {
        return focusedQuestion;
    }
}

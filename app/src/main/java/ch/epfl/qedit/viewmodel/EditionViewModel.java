package ch.epfl.qedit.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import ch.epfl.qedit.model.Question;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;

public class EditionViewModel extends ViewModel {

    private final MutableLiveData<Integer> focusedQuestion = new MutableLiveData<>(null);
    private List<Question> overviewList;

    public void initializeOverviewList(ImmutableList<Question> questions) {
        overviewList = new ArrayList<>(questions);
    }

    public List<Question> getOverviewList() { // TODO ugly
        return overviewList;
    }

    public void addFilledOutQuestion(int position, Question question) {
        if (position >= 0 && position < overviewList.size() && overviewList.get(position) == null) {
            overviewList.add(position, question);
        }
    }

    public MutableLiveData<Integer> getFocusedQuestion() {
        return focusedQuestion;
    }
}

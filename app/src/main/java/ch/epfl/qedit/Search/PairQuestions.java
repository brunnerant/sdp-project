package ch.epfl.qedit.Search;

import ch.epfl.qedit.model.Question;

public class PairQuestions extends SearchablePair<Question> {

    @Override
    public Question search(String string, int position) {
        if(position >= e.size()) {
            throw new IllegalArgumentException();
        }

        return e.get(position).search(string, position);
    }
}

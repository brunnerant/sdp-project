package ch.epfl.qedit.model.answer;

import androidx.annotation.Nullable;

/** This model contains nothing */
public class EmptyAnswerModel extends AnswerModel {
    @Override
    public boolean equals(@Nullable Object obj) {
        return false;
    }
}

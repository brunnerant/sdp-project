package ch.epfl.qedit.model.answer;

import androidx.annotation.Nullable;
import java.io.Serializable;

/**
 * This class represents the answer that a user entered into a question. Its subclasses are in a
 * one-to-one correspondence with the types of answers represented by the AnswerFormat class
 * hierarchy.
 */
public abstract class AnswerModel implements Serializable {
    @Override
    public abstract boolean equals(@Nullable Object obj);
}

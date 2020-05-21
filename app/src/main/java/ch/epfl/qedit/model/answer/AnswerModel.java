package ch.epfl.qedit.model.answer;

import androidx.annotation.Nullable;
import ch.epfl.qedit.util.Mappable;
import java.io.Serializable;

/**
 * This class represents the answer that a user entered into a question. Its subclasses are in a
 * one-to-one correspondence with the types of answers represented by the AnswerFormat class
 * hierarchy.
 */
public abstract class AnswerModel implements Serializable, Mappable {
    @Override
    public abstract boolean equals(@Nullable Object obj);
}

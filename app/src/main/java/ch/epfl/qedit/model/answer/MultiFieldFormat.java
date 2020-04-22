package ch.epfl.qedit.model.answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.view.answer.AnswerFragment;

public class MultiFieldFormat extends AnswerFormat {

    private List<AnswerFormat> fields;

    public MultiFieldFormat(List<AnswerFormat> formats) {
        // For now, MultiFieldFormat does not support a helper text
        // However, each field can have a helper text
        super(null);
        fields = new ArrayList<>();
        fields.addAll(formats);
    }

    public MultiFieldFormat(AnswerFormat... formats) {
        super(null);
        fields = Arrays.asList(formats);
    }

    @Override
    public AnswerModel getEmptyAnswerModel() {
        return null;
    }

    @Override
    public AnswerFragment getAnswerFragment() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (super.equals(o) && o instanceof MultiFieldFormat) {
            MultiFieldFormat other = (MultiFieldFormat) o;
            return fields.equals(other.fields);
        }
        return false;
    }

    @Override
    public AnswerFormat instantiateLanguage(StringPool pool) {
        List<AnswerFormat> newFields = new ArrayList<>(fields.size());

        for (AnswerFormat f : fields)
            newFields.add(f.instantiateLanguage(pool));

        return new MultiFieldFormat(newFields);
    }
}

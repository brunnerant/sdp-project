package ch.epfl.qedit.model.answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiFieldFormat extends AnswerFormat {

    private List<AnswerFormat> fields;

    public MultiFieldFormat(List<AnswerFormat> formats) {
        // For now, MultiFieldFormat don't support a helper text
        // each field can however have a helper text
        super(null);
        fields = new ArrayList<>();
        fields.addAll(formats);
    }

    public MultiFieldFormat(AnswerFormat... formats) {
        super(null);
        fields = Arrays.asList(formats);
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
    public void accept(Visitor visitor) {
        visitor.visitMutliFieldFormat(this);
    }
}

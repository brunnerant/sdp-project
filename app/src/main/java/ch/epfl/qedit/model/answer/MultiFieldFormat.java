package ch.epfl.qedit.model.answer;

import java.util.ArrayList;
import java.util.List;

public class MultiFieldFormat extends AnswerFormat {

    ArrayList<AnswerFormat> fields;

    public MultiFieldFormat(List<AnswerFormat> formats) {
        // For now, MultiFieldFormat don't support a helper text
        // each field can however have a helper text
        super(null);
        fields = new ArrayList<>();
        fields.addAll(formats);
    }

    @Override
    public boolean equals(Object o) {
        if (super.equals(o) && o instanceof MultiFieldFormat) {
            MultiFieldFormat other = (MultiFieldFormat) o;
            if (this.fields.size() != other.fields.size()) {
                return false;
            }
            for (int i = 0; i < this.fields.size(); ++i) {
                if (!this.fields.get(i).equals(other.fields.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitMutliFieldFormat(this);
    }
}

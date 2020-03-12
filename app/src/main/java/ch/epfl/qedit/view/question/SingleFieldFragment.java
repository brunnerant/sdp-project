package ch.epfl.qedit.view.question;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.AnswerFormat;

public class SingleFieldFragment extends Fragment {
    private AnswerFragment answerFragment;
    private AnswerFormat answerFormat;
    private AnswerFormat.NumberField numberField;
    private EditText[] editTexts;

    public void setAnswerFormat(AnswerFormat answerFormat) {
        this.answerFormat = answerFormat;

        editTexts = new EditText[3];
    }

    public void accept(AnswerFormat.Visitor visitor) {
        answerFormat.accept(visitor);
    }

    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        ViewGroup groupsOfText = null;
        View view = groupsOfText;

        switch (numberField.getType()) {
            case SIMPLE:
                view = inflater.inflate(R.layout.answer_fragment, container, false);
                break;
            case MORE_ONE_QUESTION:
                view = inflater.inflate(R.layout.single_field_fragment, container, false);
                editTexts[0] = createEditText(view, R.id.answer_zone2);
                editTexts[1] = createEditText(view, R.id.answer_zone3);
                editTexts[2] = createEditText(view, R.id.answer_zone4);
                break;
            case OTHER:
                throw new IllegalArgumentException();
        }

        return view;
    }

    private EditText createEditText(View view, int id) {
        EditText editText = (EditText) view.findViewById(id);
        editText.setRawInputType(
                InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL
                        | InputType.TYPE_NUMBER_FLAG_SIGNED);

        return editText;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}

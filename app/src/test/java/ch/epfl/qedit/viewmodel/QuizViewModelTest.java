package ch.epfl.qedit.viewmodel;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import org.junit.Rule;
import org.junit.Test;

public class QuizViewModelTest {
    @Rule public InstantTaskExecutorRule testRule = new InstantTaskExecutorRule();

    @Test
    public void testThatQuestionCanBeSelected() {
        Observer questionObserver = mock(Observer.class);
        QuizViewModel model = new QuizViewModel();
        model.getFocusedQuestion().observeForever(questionObserver);
        model.getFocusedQuestion().setValue(3);
        verify(questionObserver).onChanged(3);
    }
}
